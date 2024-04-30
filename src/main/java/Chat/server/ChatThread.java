package Chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;

public class ChatThread extends Thread {
    // 생성자를 통해 클라이언트 소켓을 얻어옴,
    private Socket socket;
    private String id;
    private Map<String, PrintWriter> chatClients;

    BufferedReader in = null;

    public ChatThread(Socket socket, Map<String, PrintWriter> chatClients) {
        this.socket = socket;
        this.chatClients = chatClients;

        // 클라이언트가 생성될 때 클라이언트로 부터 아이디를 얻어오게 하고 싶어요.
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            id = in.readLine();
            broadcast(id + "님이 입장하셨습니다.");
            System.out.println("새로운 사용자의 아이디는 " + id + "입니다.");

            // 동시에 일어날 수도
            synchronized (chatClients) {
                chatClients.put(this.id, out);
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();

        }
    }
    // run
    @Override
    public void run() {
        // 연결된 클라이언트가 메시지를 전송하면, 그 메시지를 받아서 다른 사용자들에게 보내줌.
        String msg = null;
        try {
            while ((msg = in.readLine()) != null) {
                if ("/quit".equalsIgnoreCase(msg)) {
                    break;
                }
                if (msg.indexOf("/to") == 0) {
                    sendMsg(msg);
                } else {
                    broadcast(id + " : " + msg);
                }
            }

        } catch (IOException e) {
            System.out.println(e);
        }finally {
            synchronized (chatClients) {
                chatClients.remove(id);
            }
            broadcast(id + " 님이 채팅에서 나갔습니다.");

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }


        }
    }

    //메시지를 특정 사용자에게만 보내는 메서드
    public void sendMsg(String msg) {
        int firstSpaceIndex = msg.indexOf(" ");
        if (firstSpaceIndex == -1) {
            return;  // 공백이 없다면... 실행 종료
        }
        int secontSpaceIndex = msg.indexOf(" ", firstSpaceIndex + 1);
        if (secontSpaceIndex == -1) {
            return;
        }
        String to = msg.substring(firstSpaceIndex + 1, secontSpaceIndex);
        String message = msg.substring(secontSpaceIndex + 1);

        //to(수신자)에게 메시지 전송.
        PrintWriter pw = chatClients.get(to);
        if (pw != null) {
            pw.println(id + "님으로 부터 온 비밀 메시지 " + message);
        } else {
            System.out.println("오류 ;: 수신자 " + to + " 님을 찾을 수 없습니다.");
        }

    }


    // 전체 사용자에게 알려주는 메소드
    public void broadcast(String msg) {
//        for (PrintWriter out : chatClients.values()) {
//            out.println(msg);
//        }
//    }
        synchronized (chatClients) {
            Iterator<PrintWriter> it = chatClients.values().iterator();
            while (it.hasNext()) {
                PrintWriter out = it.next();
                try {
                    out.println(msg);
                } catch (Exception e) {
                    it.remove();
                    e.printStackTrace();
                }
            }
        }
    }
}

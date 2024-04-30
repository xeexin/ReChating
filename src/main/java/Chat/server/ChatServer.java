package Chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class ChatServer {
    public static void main(String[] args){
        //객체가 1개만 생성되어야 함. (싱글턴 패턴)
        ClientManager clientManager = new ClientManager();
        ChatRoomManager chatRoomManager = new ChatRoomManager();

        // 1. 서버소켓 생성
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("서버가 준비 되었습니다.");
            while (true) {
                // 2. accept() // 3. clinet 접속 -> socket 생성
                Socket socket = serverSocket.accept();
                System.out.println("[CLIENT 접속] : " + socket.getInetAddress());

                // 4. 소켓으로 부터 in, out 생성하고, 처음 읽어들인 한 줄은 닉네임이다.
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String nickname = in.readLine(); // 처음 읽어들인 한 줄은 닉네임이다.
                System.out.println("[닉네임] : " + nickname + " 연결");

                //5. socket, in, out, nickname을 가지고 [client 객체 생성]
                Client client = new Client(socket, in, out, nickname);

                // 5-1 ClientManager에 클라이언트가 추가 되어야 함
                clientManager.addClient(client);

                //클라이언트 접속 시 : 사용법 [INFO]  .. 어떤 객체가 해야 할까?
                out.println();
                out.println("[ 접속을 환영합니다 : " + nickname + "님]");
                out.println("[INFO]");
                out.println("/create : 새로운 채팅방을 생성합니다.");
                out.println("/list : 채팅방 목록을 보여줍니다.");
                out.println("/join [방번호] : 해당 번호의 채팅방으로 이동합니다.");
                out.println("/exit : 채팅방을 나갑니다.");
                out.println("/bye : 접속을 종료합니다.");


                // 6. ChatHanler를 생성하여 클라이언트와 계속 통신 하도록
                new ChatHandler(clientManager, chatRoomManager, client).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

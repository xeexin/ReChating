package Chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

//스레드로 구현되어야 함.
public class ChatHandler extends Thread {
    private ClientManager clientManager;
    private ChatRoomManager chatRoomManager;
    private Client client;

    private ChatRoom chatRoom; //null인 경우 : 채팅방에 참여하지 않은 상태 // 대기실에 있는 경우.

    public ChatHandler(ClientManager clientManager,ChatRoomManager chatRoomManager, Client client) {
        this.clientManager = clientManager;
        this.chatRoomManager = chatRoomManager;
        this.client = client;
    }

    @Override
    public void run() {
        while (true) {
            //클라이언트로부터 읽어 옴
            try {
                String message = client.getIn().readLine();

                if (message == null) {
                    System.out.println(client.getNickname() + "님의 사용자가 연결을 끊었습니다.");
                    client.close();
                    return;
                }
                if (message.indexOf("/") == 0) { //명령어라는 뜻

                    if ("/list".equalsIgnoreCase(message)) {
                        //채팅룸 리스트 보여주기.
                        List<ChatRoom> chatRooms = chatRoomManager.getChatRooms();
                        for (ChatRoom room : chatRooms) {
                            client.getOut().println("방 : " + room.getId());
                        }
                    } else if ("/create".equalsIgnoreCase(message)) {
                        //방을 생성하고, 생성된 방에 client 입장.
                        chatRoom = chatRoomManager.createChatRoom();
                        //채팅룸에 정보 넣어주기.
                        chatRoom.addClient(client);

                        System.out.println("채팅방 [ " + chatRoom.getId() + " ]가 생성되었습니다.");
                        client.getOut().println("[채팅방 생성] 방번호 : " + chatRoom.getId());
                        chatRoom.broadcast(client.getNickname() + "님이 입장하였습니다.");

                    } else if (message.indexOf("/join") == 0) {
                        String[] tokens = message.split(" ");
                        int chatRoomId = -1;
                        try {
                            //[방번호] 받아옴
                            chatRoomId = Integer.parseInt(tokens[1]);
                            chatRoom = chatRoomManager.getChatRoom(chatRoomId);
                            chatRoom.addClient(client);
                            chatRoom.broadcast(client.getNickname() + "님이 입장하였습니다.");
                        } catch (Exception e) {
                            // 방번호를 입력하지 않았을 때 예외처리
                        client.getOut().println("방 번호가 정확하지 않습니다.");
                        }
                    } else if ("/exit".equalsIgnoreCase(message)) {
                        //채팅방에서만 빠져나간다.
                        if (chatRoom != null) {
                            chatRoom.broadcast(client.getNickname() + "님이 퇴장하였습니다.");
                            chatRoom.removeClient(client);
                            chatRoom = null;
                        } else {
                            client.getOut().println("현재 채팅방이 아닙니다.");
                        }
                    } else if ("/bye".equalsIgnoreCase(message)) {
                        //채팅방 안이라면, 채팅방에서 빠져나가고, 접속도 종료
                        if (chatRoom != null) {
                            chatRoom.broadcast(client.getNickname() + "님이 퇴장하였습니다.");
                            chatRoom.removeClient(client);
                        }
                        System.out.println(client.getNickname() + "님의 접속을 종료합니다.");
                        clientManager.removeClient(client);
                        client.close();
                        break;
                    }
                } else {
                    //대화 입력이 들어옴
                    //지정된 채팅룸이 없을 때 대화 할 수 없음.
                    if (chatRoom != null) {
                        // 대화방에 들어옴
                        // 정해진 대화방에 들어간 모든 사용자에게 메시지 전달.
                        chatRoom.broadcast(client.getNickname() + " : " + message);

                    } else {
                        System.out.println("현재 대기실에 위치해 있습니다.");
                        client.getOut().println("채팅방 안에 없으므로 대화 할 수 없습니다!");
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
                client.close();
            }
        }
    }
}

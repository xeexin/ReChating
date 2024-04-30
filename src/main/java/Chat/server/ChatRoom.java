package Chat.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatRoom {
    //방번호
    private int id;

    //어떤 클라이언트들이 방에 속해 있는지
    private List<Client> clients = Collections.synchronizedList(new ArrayList<>()); //동시성 해결 리스트 지속적 동기화

    public ChatRoom(int id) { //룸매니저가 생성할 것(아이디를 가지고)
        this.id = id;
    }

    //METHOD
    //방에 있는 모든 사용자한테 broadcast하면 됨

    //방번호 get
    public int getId() {
        return id;
    }

    // 유저 추가 (클라이언트가 들어오면 클라이언트 추가)
    public void addClient(Client client) {
        clients.add(client);
    }

    //유저 삭제
    public void removeClient(Client client) {
        clients.remove(client);
    }

    //룸에 있는 사용자에게 채팅 메시지를 보내주는 메소드
    public void broadcast(String msg) {
        for (Client client : clients) {
            client.getOut().println(msg);
        }
    }

}

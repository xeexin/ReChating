package Chat.server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientManager {

    // 클라이언트들을 관리하는 객체

    // 클라이언트를 알고 있어야 한다.

    // 어떤 자료 구조가 적합할까? 여러개의 Client를 담아야 한다.
    // List , Set , Map..

    // 1. 배열 -> 고정 길이. 클라이언트가 몇명 접속할지 고려해야 함으로 부적합
    // 2. List<Client> (중복을 허용) : 검색 시간이 오래 걸림,.
    // 3. Set<Client> (중복 미허용) :
    // 4. MAP<key, value> Client -- key 값을 이용해서 단번에 값을 찾아낼 수 있다는 장점 MAP<String, Client> (닉네임, 클라이언트)

    Map<String, Client> map = new ConcurrentHashMap<>(); //동시성 고려

    public void addClient(Client client) {
        map.put(client.getNickname(), client);
    }

    public void removeClient(Client client) {
        map.remove(client);
    }

}

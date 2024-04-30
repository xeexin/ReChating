package Chat.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatRoomManager {
    private static int ROOMID = 1;

    //채팅룸 리스트를 저장
    private Map<Integer, ChatRoom> roomList = new ConcurrentHashMap<>();

    //기능
    //ChatRoom 생성 -- 방번호 자동 생성
    public ChatRoom createChatRoom() {
        ChatRoom chatRoom = new ChatRoom(ROOMID++);
        roomList.put(chatRoom.getId(), chatRoom);

        return chatRoom;
    }

    //ChatRoom 삭제 -- 채팅룸 카운트가 없을 경우 삭제 됨
    public void removeChatRoom(int id) {
        roomList.remove(id);
    }


    //방번호에 해당하는 채팅룸을 get하는 메서드
    public ChatRoom getChatRoom(int id) {
        return roomList.get(id);
    }


    //채팅룸 리스트 보여주는 메서드
    public List<ChatRoom> getChatRooms() {
        return new ArrayList<>(roomList.values());
    }

}

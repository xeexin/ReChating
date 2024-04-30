package Chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String nickname;

    public Client(Socket socket, BufferedReader in, PrintWriter out, String nickname) {
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.nickname = nickname;
    }

    public Socket getSocket() {
        return socket;
    }

    public BufferedReader getIn() {
        return in;
    }

    public PrintWriter getOut() {
        return out;
    }

    public String getNickname() {
        return nickname;
    }

    public void close() {
        try {
            in.close();
            out.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

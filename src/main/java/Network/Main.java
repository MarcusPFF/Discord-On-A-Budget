package Network;

public class Main {
    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.startServer(6969);
    }
}

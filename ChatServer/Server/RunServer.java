package ChatServer.Server;

public class RunServer {
    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer("Local server",8080);
        chatServer.acceptClients();
    }
}

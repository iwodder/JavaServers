package ChatServer.Server;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

public class UserThread implements Runnable, Observer {
    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    String input;
    String name;
    ChatServer chatServer;

    public UserThread(Socket socket, String name, ChatServer chatServer) throws IOException {
        this.socket = socket;
        this.name = name;
        this.chatServer = chatServer;
        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        welcomeMessages();
            try {
                while(true) {
                    input = dataInputStream.readUTF();
                    if (input.equals("exit")) {
                        chatServer.removeObserver(this);
                        socket.close();
                        Thread.interrupted();
                        break;
                    }
                    update(chatServer, "[" + name + "]: " + input);
                }
            } catch (IOException e) {

            }
        }


    public DataOutputStream getOutput() {
        return this.dataOutputStream;
    }

    public void notification(String userName, int numOfUsers) throws IOException {
        dataOutputStream.writeUTF("New user joined the chat [" + userName + "]");
        dataOutputStream.writeUTF("There are now" + numOfUsers + " in the group");
    }

    @Override
    public void update(Observable o, Object arg) {
        chatServer.sendMessage(arg, this);
    }
    public void welcomeMessages() {
        try {
            dataOutputStream.writeUTF("Welcome to the group " + name);
            dataOutputStream.writeUTF("There are " + chatServer.numberOfConnectedUsers() + " in the group");
            if(chatServer.numberOfConnectedUsers() > 1) {
                String connectedUsers = buildUserList();
                dataOutputStream.writeUTF(connectedUsers);
            }
        } catch (IOException e) {

        }
    }

    public String buildUserList() {
        StringBuilder sb = new StringBuilder();
        Set<String> users = chatServer.usersInChat();
        for(String name : users) {
            sb.append(name);
            sb.append(", ");
        }
        return sb.toString();
    }

    public String getName() {
        return this.name;
    }
}


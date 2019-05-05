package ChatServer.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

public class ChatServer extends Observable {
    private ServerSocket serverSocket;
    private String serverName;
    private int portNumber;
    private Set<String> userNames;
    private Set<UserThread> connectedUsers;


    public ChatServer(String serverName, int portNumber) {
        try {
            userNames = new HashSet<>();
            connectedUsers = new HashSet<>();
            this.portNumber = portNumber;
            this.serverName = serverName;
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void acceptClients() {
        System.out.println(serverName + " is listening on " + portNumber);
        while(true) {
            try {
                Socket newClient = serverSocket.accept();
                DataOutputStream dataOutputStream = new DataOutputStream(newClient.getOutputStream());
                dataOutputStream.writeUTF("Enter your username: ");
                DataInputStream dataInputStream = new DataInputStream(newClient.getInputStream());
                String userName = dataInputStream.readUTF();
                UserThread newUser = new UserThread(newClient, userName, this);
                Thread thread = new Thread(newUser);
                registerObserver(newUser);
                notifyObservers(userName, newUser);
                thread.start();
            } catch (Exception e) {

            }
        }
    }

    public void registerObserver(UserThread userThread) {
        connectedUsers.add(userThread);
        userNames.add(userThread.getName());
    }

    public void removeObserver(UserThread userThread) {
        sendMessage(userThread.getName() + " has left", userThread);
        connectedUsers.remove(userThread);
        userNames.remove(userThread.getName());
    }

    public void sendMessage(Object message, UserThread excludedUser) {
        printSendingMessage();
        System.out.println(message);
        DataOutputStream dataOutputStream;
        for(UserThread userThread : connectedUsers) {
            if(!userThread.equals(excludedUser)) {
                dataOutputStream = userThread.getOutput();
                try {
                    dataOutputStream.writeUTF((String) message);
                } catch (IOException e) {
                    System.out.println("@Server Logs: error sendMessage");
                    System.out.println(e);
                    if(e.getMessage().contains("Connection reset by peer:")) {
                        removeObserver(userThread);
                    }

                }
            }
        }
    }

    public void notifyObservers(String userName, UserThread excludedUser) {
        for (UserThread userThread : connectedUsers) {
            if(!userThread.equals(excludedUser)) {
                try {
                    userThread.notification(userName, numberOfConnectedUsers());
                } catch (IOException e) {
                    System.out.println("@Server Logs: error notifyObservers");
                    System.out.println(e);
                }
            }
        }
    }
    public Set<String> usersInChat() {
        return this.userNames;
    }
    public int numberOfConnectedUsers() {
        return this.connectedUsers.size();
    }

    private void printSendingMessage() {
        System.out.println("===================== Sending Message ===========================");
    }
}

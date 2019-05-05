package ChatServer.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientOutput implements Runnable {
    Socket socket;
    DataInputStream dataInputStream;
    Client client;

    public ClientOutput(Socket socket, Client client) throws IOException {
        this.socket = socket;
        this.client = client;
        dataInputStream = new DataInputStream(client.getInput());
    }
    @Override
    public void run() {
        while(true) {
            try {
                System.out.println(dataInputStream.readUTF());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

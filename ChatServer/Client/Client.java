package ChatServer.Client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    private String clientName;
    private int portNumber;
    private static String localHost = "";
    Socket socket;
    Thread clientOutput;

    public Client(String clientName) {
        this.clientName = clientName;
    }

    public String getClientName() {
        return this.clientName;
    }

    public int getPort() {
        return this.portNumber;
    }
    public boolean createSocket(int portNumber) {
        try {
            if(portNumber < 0 || localHost.isEmpty()) {
                return false;
            } else {
                this.portNumber = portNumber;
            }
            socket = new Socket(localHost, portNumber);
            clientOutput = new Thread(new ClientOutput(socket, this));
            clientOutput.start();
            return true;
        } catch (IOException e) {
            this.portNumber = 0;
            return false;
        }
    }

    public boolean setLocalHost() {
        boolean result = false;
        int failedAttemps = 0;
        while(!result && failedAttemps < 3) {
            try {
                result = getLocalHost();
            } catch (UnknownHostException e) {
                failedAttemps++;
                System.out.println("Error setting local host, retrying in 5s...");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    System.out.println("Failed to set local host");
                    return false;
                }
            }
        }
        return true;
    }

    public OutputStream getOutput() throws IOException {
        return socket.getOutputStream();
    }

    public InputStream getInput() throws IOException {
        return socket.getInputStream();
    }

    public void shutdown() throws Exception {
        socket.close();
        clientName = null;
        portNumber = 0;
    }

    private static boolean getLocalHost() throws UnknownHostException {
        localHost = InetAddress.getLocalHost().getHostAddress();
        return true;
    }
}

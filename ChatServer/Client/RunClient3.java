package ChatServer.Client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class RunClient3 {
    public static void main(String[] args) {
        DataOutputStream dataOutputStream = null;

        Scanner in = new Scanner(System.in);
        System.out.print("What is the name of the client? -> ");
        String clientName = in.nextLine();
        System.out.print("What is the port number? -> ");
        int port = in.nextInt();
        in.nextLine();

        Client c = new Client(clientName);
        c.setLocalHost();
        c.createSocket(port);
        System.out.println("Connected...");

        try {
            dataOutputStream = new DataOutputStream(c.getOutput());
        } catch (IOException e) {
            System.exit(1);
        }

        String output;
        while(true) {
            try {
                output = in.nextLine();

                System.out.println("[" + clientName + "]: " + output);
                dataOutputStream.writeUTF(output);
                if(output.equals("exit")) {
                    c.shutdown();
                    System.exit(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

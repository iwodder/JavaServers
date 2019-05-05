package ChatServer.Client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {
    Client client;
    String name;
    int portNumber;

    @BeforeEach
    public void setup() {
        portNumber = 8000;
        name = "Ian's client";
        client = new Client(name);
    }

    @Test
    @DisplayName("Client should be successfully created")
    public void createClient() {
        assertEquals(name, client.getClientName());
        assertEquals(portNumber, client.getPort());
    }

    @Test
    public void setLocalHost() {
        assertTrue(client.setLocalHost());
    }

    @Test
    public void testCreateSocket() {
        assertFalse(client.createSocket(portNumber));

        client.setLocalHost();
        assertTrue(client.createSocket(portNumber));
    }

}
package server;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServerTest {

    @Mock
    private Socket mockSocket;
    public ServerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddUser() {
        Server server = new Server();
        server.addUser("user1", new Socket());
        server.addUser("user2", new Socket());
        String[] users = server.getUsers();
        List<String> userList = Arrays.asList(users);
        assertEquals(2, userList.size());
        assertTrue(userList.contains("user1"));
        assertTrue(userList.contains("user2"));
    }

    @Test
    void testHandler_ValidCommand() throws IOException {
        Server server = new Server();
        Socket socket = new Socket();
        server.addUser("user1", new Socket());
        server.addUser("user2", new Socket());
        server.handler("addUser user3", socket);
        String[] users = server.getUsers();
        List<String> userList = Arrays.asList(users);
        assertEquals(3, userList.size());
        assertTrue(userList.contains("user1"));
        assertTrue(userList.contains("user2"));
        assertTrue(userList.contains("user3"));
    }

    @Test
    void testHandler_InvalidCommand() throws IOException {
        Server server = new Server();
        Socket socket = new Socket();
        server.addUser("user1", new Socket());
        server.addUser("user2", new Socket());
        server.handler("invalidCommand",socket);
        String response = server.handler("invalidCommand",socket);
        assertEquals("invalidCommand", response);
    }

    @Test
    void testGetUsers() throws IOException {
        Server server = new Server();
        server.addUser("user1", mockSocket);
        server.addUser("user2", mockSocket);
        server.addUser("user3", mockSocket);
        when(mockSocket.isClosed()).thenReturn(false);
        when(mockSocket.isInputShutdown()).thenReturn(false);
        when(mockSocket.isOutputShutdown()).thenReturn(false);
        String[] expectedUsers = {"user1", "user2", "user3"};
        String[] actualUsers = server.getUsers();
        assertArrayEquals(expectedUsers, actualUsers, "Users retrieved do not match expected users");
    }

    @Test
    void testAddMessage_NotExistingUsers() throws IOException {
        Server server = new Server();
        assertThrows(NullPointerException.class, () -> {
            server.addMessage("sender", "recipient", "Hello!");
        });
    }
}
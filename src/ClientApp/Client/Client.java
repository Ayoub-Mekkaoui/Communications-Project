package ClientApp.Client;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.Queue;
import java.net.InetAddress;
import java.net.Socket;
import Common.MessageInterface;
import Common.MessageType;
import ServerApp.User.User;

public class Client {
private InetAddress serverAddress;
    private int port;
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Queue inboundRequestQueue;
    private Queue outboundResponseQueue;
    private User userData;
    private Map ChatBoxList;

    public Client(InetAddress serverAddress, int port) {
        this.serverAddress = serverAddress;
        this.port = port;
    }

    // Connect to the server
    public void start() {
        try {
        	InetAddress host = InetAddress.getLocalHost(); // Using Inet to resolve address
            socket = new Socket(host, port);
            System.out.println("Connected to server at " + host+ ":" + port);

            // Initialize ObjectOutputStream before ObjectInputStream
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush(); // Ensure stream header is written correctly
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        }
    }

    // Send a message to the server
    public void sendMessage(MessageInterface message) {
        try {
            outputStream.writeObject(message); // Send the message as a serialized object
            outputStream.flush();
            System.out.println("Message sent: " + message.getType());
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    // Receive a response from the server
    public MessageInterface receiveResponse() {
        try {
            Object response = inputStream.readObject(); // Read the response as a serialized object
            if (response instanceof MessageInterface) {
                return (MessageInterface) response;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error receiving response: " + e.getMessage());
        }
        return null;
    }

    // Close the connection
    public void closeConnection() {
        try {
            if (socket != null) socket.close();
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            System.out.println("Connection closed.");
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    // Incomplete methods filled based on required logic

    public void login(String username, String password) {
        // Create a LOGIN message and send it to the server
//        MessageInterface loginMessage = new Message(MessageType.LOGIN, username + ":" + password);
//        sendMessage(loginMessage);
    }

    public void logout() {
        // Create a LOGOUT message and send it to the server
//        MessageInterface logoutMessage = new Message(MessageType.LOGOUT, null);
//        sendMessage(logoutMessage);
    }

    public void sendChatMessage(String chatBoxID, String message) {
        // Create a SEND_MESSAGE message and send it to the server
//        MessageInterface chatMessage = new Message(MessageType.SEND_MESSAGE, chatBoxID + ":" + message);
//        sendMessage(chatMessage);
    }

    public void requestUserList() {
        // Create a REQUEST_USER_LIST message and send it to the server
//        MessageInterface userListRequest = new Message(MessageType.REQUEST_USER_LIST, null);
//        sendMessage(userListRequest);
    }

    public static void main(String[] args) {
        try {
            //String serverAddress = "192.168.1.1";
        	InetAddress host = InetAddress.getLocalHost();
            int port = 3000;

            Client client = new Client(host, port);
            client.start();


//            client.login("testUser", "password123");
//            client.requestUserList();
//
//            MessageInterface response = client.receiveResponse();
//            if (response != null) {
//                System.out.println("Response from server: " + response.getType());
//            }
//
//            client.logout();
            client.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
   

    public DataInputStream ClientMessageThread() {
    	return null;
    }

    public void sendMessageRequest() {
    	
    }

    public void sendChatBoxRequest() {
    	
    }

    public void sendUnbanUserRequest() {
    	
    }

    public void sendResetUserPassword() {
    	
    }

    public void sendCreateUserRequest() {
    	
    }

    public void receiveChatBoxStatus() {
    	
    }

    public void sendViewChatBoxLogRequest() {
    	
    }

    public void receiveChatBoxLog() {
    	
    }
    
}

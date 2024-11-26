package ClientApp.Client2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

// Import existing classes from your Common package
import ClientApp.Gui.Gui;
import Common.ChatBox.ChatBox;
import Common.MessageInterface;
import Common.MessageType;
import Common.Messages.Login;
import Common.Messages.LoginResponse;
import Common.User.User;

import javax.swing.*;

public class Client2 {
    private boolean loggedIn = false;
    private InetAddress serverAddress;
    private int port;
    private BlockingQueue<MessageInterface> inboundRequestQueue;
    private BlockingQueue<MessageInterface> outboundResponseQueue;
    private User userData;
    private TreeSet<ChatBox> chatBoxList;
    private Gui gui;
    private Scanner scanner = new Scanner(System.in);
    private ObjectOutputStream outObj = null;
    private ObjectInputStream inObj = null;
    private Socket socket = null;
    private User user = null;

    public Client2() {
        chatBoxList = new TreeSet<>(Comparator.comparingInt(ChatBox::getChatBoxID));
        Client2 client = this;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                gui = new Gui(client);
            }
        });
        inboundRequestQueue = new LinkedBlockingQueue<>();
        outboundResponseQueue = new LinkedBlockingQueue<>();
    }


    private void handleServerResponses() {
        while (true) {
//            try {
            MessageInterface response = inboundRequestQueue.poll();
            if (response != null) {
                switch (response.getType()) {
                    case MessageType.LOGIN_RESPONSE:
                        receiveLoginResponse((LoginResponse) response);
                        break;
                    case NOTIFICATION:
                        break;
                    case RETURN_CHATBOX:
                        break;
                    case RETURN_USER_LIST:
                        break;
                    case SEND_MESSAGE:
                        break;
                    case RETURN_CHATBOX_LOG:
                        break;
                    default:
                        break;
                }
            }


//            } catch (ClassNotFoundException e) {
//                System.err.println("Class not found: " + e.getMessage());
//            } catch (IOException e) {
//                System.err.println("I/O error: " + e.getMessage());
//                break;
//            }
        }
    }

    public TreeSet<ChatBox> getChatBoxList() {
        return chatBoxList;
    }

    private void receiveLoginResponse(LoginResponse loginResponse) {
        userData = loginResponse.user();
        chatBoxList.addAll(loginResponse.chatBoxList());
    }


    public void addMessage(MessageInterface message) {
        outboundResponseQueue.add(message);

    }


    public void messageSender() {
        while (true) {
            MessageInterface message;
            try {
                message = outboundResponseQueue.take();
                outObj.writeObject(message);
//                outObj.flush();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void messageReceiver() {
        while (true) {
            try {
                inboundRequestQueue.add((MessageInterface) inObj.readObject());
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public static void main(String[] args) {
        Client2 client = new Client2();
        Thread senderThread = null;
        Thread receiverThread = null;

        try {

            // Get server IP and port from the user
            System.out.print("Enter server IP address: ");
            String serverIP = client.scanner.nextLine();

            System.out.print("Enter server port number: ");
            int port = Integer.parseInt(client.scanner.nextLine());

            // Connect to the server
            client.socket = new Socket(serverIP, port);
            System.out.println("Connected to the server.");

            // Set up object streams
            client.outObj = new ObjectOutputStream(client.socket.getOutputStream());
            client.inObj = new ObjectInputStream(client.socket.getInputStream());

            senderThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    client.messageSender();
                }
            });
            receiverThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    client.messageReceiver();
                }
            });
            senderThread.start();
            receiverThread.start();
//            // Get username and password from the user
//            System.out.print("Enter username: ");
//            String username = client.scanner.nextLine();
//
//            System.out.print("Enter password: ");
//            String password = client.scanner.nextLine();

            // Create and send the login message
//            final MessageInterface[] message = new MessageInterface[1];


            while (!client.loggedIn) {
                client.addMessage(client.gui.login());
                MessageInterface response = client.inboundRequestQueue.take();
                if (response.getType() == MessageType.LOGIN_RESPONSE) {
                    client.receiveLoginResponse((LoginResponse) response);
                    if (client.user != null) {
                        client.loggedIn = true;
                        System.out.println("Logged in.");
                    } else {
                        new JOptionPane().createDialog("Invalid username or password");
                    }
                }
            }

            client.gui.showMain();
            // Handle server responses
            client.handleServerResponses();

        } catch (IOException | InterruptedException e) {
            System.err.println("I/O error: " + e.getMessage());
//        } catch (InterruptedException | InvocationTargetException e) {
//            throw new RuntimeException(e);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
        } finally {
            // Close resources
            try {
                if (senderThread != null) {
                    senderThread.interrupt();
                }
                if (receiverThread != null) {
                    receiverThread.interrupt();
                }
                if (client.outObj != null) {
                    client.outObj.close();
                }
                if (client.inObj != null) {
                    client.inObj.close();
                }
                if (client.socket != null) {
                    client.socket.close();
                }
                client.scanner.close();
            } catch (IOException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}

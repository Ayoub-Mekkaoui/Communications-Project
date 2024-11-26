package ClientApp.Gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CountDownLatch;

import ClientApp.Client2.Client2;
import Common.Messages.Login;
import Common.Messages.SendMessage;
import Common.ChatBox.ChatBox;
import Common.Message.Message;
import Common.User.User;

public class Gui {
    volatile boolean loggedIn = false;
    private final User user = null;
    private final Client2 client;
    private final JFrame frame;
    private final LoginWindow loginWindow;
    private final MainWindow mainWindow;

    public Gui(Client2 client) {
        this.client = client;
        frame = new JFrame("Chat Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);// Center the window
        loginWindow = new LoginWindow();
        mainWindow = new MainWindow();
    }


    public void showMain() {
        frame.getContentPane().add(mainWindow.getPanel());
        mainWindow.chatBox = client.getChatBoxList().first();
        addAllMessages(mainWindow.chatBox);
        show();
    }

    private void sendMessage() {
        mainWindow.sendMessage();
    }

    public Login login() {
        int stuff;

        frame.setVisible(false);
        return loginWindow.getLogin();
    }

    public void addMessage(Message message) {
        User user = idToUser(message.getSenderID(), mainWindow.chatBox);
        String username;
        if (user == null) {
            username = String.valueOf(message.getSenderID());
        }else{
            username = user.getUsername();
        }
        mainWindow.chatModel.addElement("<html><b>"+username+": </b>" +
                         message.toString().replace("\n", "<br><plaintext>     </plaintext>") +
                         "<br></html>");
    }



    public User idToUser(int id, ChatBox chatBox) {
        for(var i:chatBox.getParticipantsList()){
            if(i.getUserID() == id){
                return i;
            }
        }
        return null;
    }
    public void show() {
        frame.setVisible(true);
    }

    private void addAllMessages(ChatBox chatBox) {
       for(var i:chatBox.getMessages()){
           addMessage(i);
       }
    }
//    public static void main(String[] args) {
//        // Run the GUI on the Event Dispatch Thread for thread-safety
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                Gui gui = null;
//                gui = new Gui(new Client2());
//
//                gui.display();
//            }
//        });
//    }


    private static class LoginWindow {
        private final JFrame frame;
        private final JTextField userNameField;
        private final JPasswordField passwordField;
        private CountDownLatch latch;
        private final Login[] login;

        public LoginWindow() {
            frame = new JFrame("Login");
            login = new Login[1];
            JPanel usernamePanel = new JPanel();
            usernamePanel.setLayout(new FlowLayout());
            usernamePanel.add(new JLabel("Username:"));
            userNameField = new JTextField();
            userNameField.setPreferredSize(new Dimension(150, userNameField.getPreferredSize().height));
            usernamePanel.add(userNameField);
            JPanel passwordPanel = new JPanel();
            passwordPanel.setLayout(new FlowLayout());
            passwordPanel.add(new JLabel("Password:"));
            passwordField = new JPasswordField();
            passwordField.setPreferredSize(new Dimension(150, passwordField.getPreferredSize().height));
            passwordPanel.add(passwordField);
            JButton loginButton = getLoginButton();

            JPanel loginPanel = new JPanel();
            loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
            loginPanel.add(usernamePanel);
            loginPanel.add(passwordPanel);
            loginPanel.add(loginButton);

            frame.getContentPane().add(loginPanel);
            frame.setAlwaysOnTop(true);
            frame.setLocationRelativeTo(null);
            frame.pack();
            frame.setResizable(false);
        }

        private JButton getLoginButton() {
            JButton loginButton = new JButton("Login");
            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    login[0] = new Login(userNameField.getText(), String.valueOf(passwordField.getPassword()));
                    userNameField.setText("");
                    passwordField.setText("");
                    latch.countDown();
                }
            });
            return loginButton;
        }

        public Login getLogin() {
            latch = new CountDownLatch(1);

            frame.setVisible(true);
            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            frame.setVisible(false);
            return login[0];
        }
    }

    private class MainWindow {
        private final JList<String> chatList;
        private JList<ChatBox> chatBoxList;
        private final JTextArea messageField;
        private final JButton sendButton;
        private final JPanel messagePane;
        private final JScrollPane scrollPane;
        private final JScrollPane messageScrollPane;
        private final DefaultListModel<String> chatModel;
        private final DefaultListModel<ChatBox> chatBoxModel;
        private ChatBox chatBox = new ChatBox();
        private final JPanel panel;

        public MainWindow() {
            frame.setSize(600, 500);
            // Create the chat area (used to display messages)
            chatModel = new DefaultListModel<>();
            chatList = new JList<>(chatModel);
            chatBoxModel = new DefaultListModel<>();
            ChatBox chatBox = new ChatBox();

            chatList.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
//        chatArea.setEditable(false);  // Messages should not be editable

            // Create the scroll pane for the chat area
            scrollPane = new JScrollPane(chatList);
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

            // Create the message input field

            messageField = new JTextArea();
            messageField.setLineWrap(true);
            messageField.setWrapStyleWord(true);
            messageScrollPane = new JScrollPane(messageField);
            messageScrollPane.setPreferredSize(new Dimension(500, 60));
            messagePane = new JPanel();
            messagePane.add(messageScrollPane);
            // Create the send button
            sendButton = new JButton("Send");
            messagePane.add(sendButton);
            frame.setResizable(false);

            // Action listener for the send button
            sendButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sendMessage();
                }
            });

            // Create the layout and add components
            panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.add(scrollPane, BorderLayout.CENTER);
            panel.add(new JPanel(), BorderLayout.SOUTH);
            panel.add(messagePane, BorderLayout.SOUTH);

        }

        public void selectChatBox(ChatBox chatBox) {
            this.chatBox = chatBox;
            addAllMessages(this.chatBox);
        }
        public void sendMessage() {
            String message = messageField.getText();
            if (!message.isEmpty() && user != null) {
                client.addMessage(new SendMessage(new Message(user.getUserID(), message), chatBox.getChatBoxID()));
                messageField.setText("");// Clear the input field
            }
        }

        public JPanel getPanel() {
            return panel;
        }


    }
}

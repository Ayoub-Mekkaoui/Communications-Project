package ServerApp.MessageHandler;

import ServerApp.StorageManager.StorageManager;
import ServerApp.ChatBox.ChatBox;
import ServerApp.Message.Message;
import ServerApp.Server.Server;
import ServerApp.ClientHandler.ClientHandler;
import ServerApp.User.User;

import java.util.HashMap;
import java.util.List;

public class MessageHandler {

    // Attributes
    private StorageManager storageManager; // Manages storage operations for chatboxes
    private HashMap<Integer, ChatBox> chatBoxes; // Shared instance of chatBoxes from Server

    // Constructor
    // INPUT: storageManager (StorageManager), chatBoxes (HashMap<Integer, ChatBox>)
    public MessageHandler(StorageManager storageManager, HashMap<Integer, ChatBox> chatBoxes) {
        this.storageManager = storageManager;
        this.chatBoxes = chatBoxes;
    }

    // Sends a message to a specific chatbox
    // INPUT: chatBoxID (int), message (Message)
    // OUTPUT: true if successful, false otherwise
    public boolean sendMessage(int chatBoxID, Message message) {
        ChatBox chatBox = chatBoxes.get(chatBoxID);
        if (chatBox != null) {
            chatBox.addMessage(message);
            storeChatBox(chatBox); // Store updated chatbox in persistent storage
            updateParticipants(chatBoxID); // Update all participants with the new chatbox
            return true;
        }
        return false;
    }

    // Sends a message to all chatboxes
    // INPUT: message (Message)
    // OUTPUT: none
    public void sendMessageToAllChatBoxes(Message message) {
        for (ChatBox chatBox : chatBoxes.values()) {
            chatBox.addMessage(message);
            storeChatBox(chatBox); // Store updated chatbox in persistent storage
            updateParticipants(chatBox.getChatBoxID()); // Update all participants with the new chatbox
        }
    }

    // Sends a message to a specific user
    // INPUT: userID (int), content (String)
    // OUTPUT: true if message sent successfully, false otherwise
    public boolean sendMessageToUser(int userID, String content) {
        Message message = new Message(userID, content);
        sendMessageToAllChatBoxes(message);
        return true;
    }

    // Adds a user to a specific chatbox
    // INPUT: chatBoxID (int), clientHandler (ClientHandler)
    // OUTPUT: true if user added successfully, false otherwise
    public boolean addParticipantToChatBox(int chatBoxID, ClientHandler clientHandler) {
        ChatBox chatBox = chatBoxes.get(chatBoxID);
        if (chatBox != null) {
            boolean added = chatBox.addParticipant(clientHandler.getUser());
            if (added) {
                storeChatBox(chatBox); // Store updated chatbox to reflect the new participant
                return true;
            }
        }
        return false;
    }

    // Removes a user from a specific chatbox
    // INPUT: chatBoxID (int), userID (int)
    // OUTPUT: true if user removed successfully, false otherwise
    public boolean removeParticipantFromChatBox(int chatBoxID, int userID) {
        ChatBox chatBox = chatBoxes.get(chatBoxID);
        if (chatBox != null) {
            User userToRemove = chatBox.getParticipants().stream().filter(user -> user.getUserID() == userID).findFirst().orElse(null);
            boolean removed = chatBox.removeParticipant(userToRemove);
            if (removed) {
                storeChatBox(chatBox); // Store updated chatbox to reflect the removed participant
                return true;
            }
        }
        return false;
    }

    // Retrieves all messages for a specific chatbox
    // INPUT: chatBoxID (int)
    // OUTPUT: List of messages or null if chatbox not found
    public List<Message> getMessages(int chatBoxID) {
        ChatBox chatBox = chatBoxes.get(chatBoxID);
        if (chatBox != null) {
            return chatBox.getMessagesList();
        }
        return null;
    }

    // Updates all participants in the chatbox with the latest chatbox state
    // INPUT: chatBoxID (int)
    // OUTPUT: none
    private void updateParticipants(int chatBoxID) {
        ChatBox chatBox = chatBoxes.get(chatBoxID);
        if (chatBox != null) {
            for (User participant : chatBox.getParticipants()) {
                ClientHandler clientHandler = findClientHandler(participant.getUserID());
                if (clientHandler != null) {
                    clientHandler.sendChatBoxUpdate(chatBox); // Send the updated chatbox to the client via the client handler
                }
            }
        }
    }

    // Finds the ClientHandler for a given user ID
    // INPUT: userID (int)
    // OUTPUT: ClientHandler or null if not found
    private ClientHandler findClientHandler(int userID) {
        // Iterate through client handlers to find the matching userID
        for (ClientHandler handler : Server.getClientHandlers()) {
            if (handler.getUser().getUserID() == userID) {
                return handler; 
            }
        }
        return null;
    }

    // Loads a chatbox into memory from storage
    // INPUT: chatBoxID (int)
    // OUTPUT: Loaded ChatBox or null if not found
    public ChatBox loadChatBox(int chatBoxID) {
        ChatBox chatBox = storageManager.retrieveChatBox(chatBoxID);
        if (chatBox != null) {
            chatBoxes.put(chatBoxID, chatBox); // Add the chatbox to the in-memory collection
        }
        return chatBox;
    }

    // Stores a chatbox in persistent storage
    // INPUT: chatBox (ChatBox)
    // OUTPUT: true if stored successfully, false otherwise
    public boolean storeChatBox(ChatBox chatBox) {
        if (chatBox != null) {
            chatBoxes.put(chatBox.getChatBoxID(), chatBox); // Update the in-memory collection
            storageManager.storeChatBox(chatBox); // Store the chatbox in persistent storage
            return true;
        }
        return false;
    }

    // Hides a specific message in a chatbox
    // INPUT: chatBoxID (int), messageID (int)
    // OUTPUT: true if message hidden successfully, false otherwise
    public boolean hideMessage(int chatBoxID, int messageID) {
        ChatBox chatBox = chatBoxes.get(chatBoxID);
        if (chatBox != null) {
            Message messageToHide = chatBox.getMessages().stream()
                .filter(message -> message.getMessageID() == messageID) 
                .findFirst().orElse(null);
            if (messageToHide != null) {
                messageToHide.setHidden(true);
                storeChatBox(chatBox); // Store updated chatbox to reflect hidden message
                updateParticipants(chatBoxID); // Update all participants with the latest chatbox state
                return true;
            }
        }
        return false;
    }

    // Hides an entire chatbox
    // INPUT: chatBoxID (int)
    // OUTPUT: true if chatbox hidden successfully, false otherwise
    public boolean hideChatBox(int chatBoxID) {
        ChatBox chatBox = chatBoxes.get(chatBoxID);
        if (chatBox != null) {
            chatBox.hideChatBox(); // Set chatbox to hidden
            storeChatBox(chatBox); // Store updated chatbox to reflect hidden status
            updateParticipants(chatBoxID); // Update all participants with the latest chatbox state
            return true;
        }
        return false;
    }
}

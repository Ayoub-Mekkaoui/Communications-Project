package ClientApp.Client;

import java.io.DataInputStream;
import java.net.Socket;
import java.util.Map;
import java.util.Queue;

import ServerApp.User.User;

public class Client {
	private String serverAddress;
	private DataInputStream inputStream;
	private Socket socket;
	private int port;
	private Queue inboundRequestQueue;
	private Queue outboundResponseQueue;
	private User userData;
	private Map ChatBoxList;
	
    public static void main(String[] args) {

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

public String receiveResponse() {
	return null;
}

public void receiveChatBoxStatus() {
	
}

public void sendViewChatBoxLogRequest() {
	
}

public void receiveChatBoxLog() {
	
}

}
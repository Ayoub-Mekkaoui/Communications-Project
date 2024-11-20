package ServerApp.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ServerApp.ClientHandler.ClientHandler;
import ServerApp.MessageHandler.MessageHandler;
import ServerApp.User.User;


public class Server {
	
	private String ipAddress;
	private static List<ClientHandler> clientHandler;
	private MessageHandler messageHandler;
	private HashMap<String, User> userDB;
	private static ServerSocket serverSocket=null;
	private int port;
	
	public Server(int port, String ip) {
		this.port=port;
		this.ipAddress=ip;
		this.clientHandler=new ArrayList<>();
	}
	

	public static void main(String[] args) {
		int default_port=3000;
		String ip="127.0.0.1";		//Will change when working in different computers
		Server server = new Server(default_port, ip);
		try{
			server.startServer(default_port);
		 
			while(true) {
				try {
					server.acceptClient();
					}
				catch (IOException e) {
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }	
			 }
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			server.shutDownServer();
			}
		}
	
	public String getIpAddress() {
		return this.ipAddress;
	}
	public void setPort(int port) {
		this.port=port;
	}
	public int getPort() {
		return this.port;
	}
	public static List<ClientHandler> getClientHandlers(){
		return clientHandler;
	}
	private void startServer(int port) throws IOException{
		serverSocket = new ServerSocket(port);		//creates a new ServerSocket object that listens for incoming connections
		System.out.println("The server is running in port: "+ port);
		serverSocket.setReuseAddress(true);
		
	}
	private void acceptClient() throws IOException{
		try {
			Socket client=serverSocket.accept();
			 
			// Displaying that new client is connected to server
			System.out.println("New client connected " + client);
			
			// create a new thread object
			ClientHandler clientSock = new ClientHandler(client);
			
			//Add new Client to the list
			this.clientHandler.add(clientSock);
			
			clientSock.createThread();
			
			}
		catch (IOException e) {
            System.err.println("Error accepting client connection: " + e.getMessage());
        }	
	}
	private void shutDownServer() {
		if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                System.out.println("Server shut down successfully.");
            } catch (IOException e) {
                System.err.println("Error shutting down server: " + e.getMessage());
                e.printStackTrace();
            }
        }
	}
  
	}

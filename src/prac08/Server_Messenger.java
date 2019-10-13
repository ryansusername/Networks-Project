package prac08;

//Imports (Sampled from prac07 solution)
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;


import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server_Messenger extends JFrame {
//Initial Variables (Skeleton from P7_Server)
	final int NO_OF_members = 4;

	ArrayList<ServerThread> members = new ArrayList<ServerThread>();
	ArrayList<String> memberNames = new ArrayList<String>();
	
	int nextMember = -1;
	
	JTextArea outputArea;
	private ServerSocket serverSocket;
	
//Constructor for server class (Skeleton from P7_Server)	
	public Server_Messenger()
	{	super("Server_Messenger");
		addWindowListener
		(	
			new WindowAdapter()
			{	
				public void windowClosing(WindowEvent e)
				{	
					System.exit(0);
				}
			}
		);
		
// get a server socket & bind to port 6000			
		try
		{	
			serverSocket = new ServerSocket(6000);
		}
		catch(IOException e) // thrown by ServerSocket
		{	System.out.println(e);
			System.exit(1);
		}
		
// create and add GUI components
		Container c = getContentPane();
		c.setLayout(new FlowLayout());
		
// add text output area
		outputArea = new JTextArea(18,30);
		outputArea.setEditable(false);
		outputArea.setLineWrap(true);
		outputArea.setWrapStyleWord(true);
		outputArea.setFont(new Font("Verdana", Font.BOLD, 11));
		c.add(outputArea);
		c.add(new JScrollPane(outputArea));
		
		setSize(400,320);
		setResizable(false);
		setVisible(true);
	}

	
	void getMembers()
	{	// output message
		addOutput("Server is up and waiting for a connection...");
		
// arrays containing valid user names, nick names and passwords
		String[] usernames = {"ryan", "allen", "rick","marty"};
		int[] memberNumbers = {1,2,3,4,5};
		String[] nickNames={"BRy","Al","Richard III", "Mort"};
		String[] passwords = {"password", "password", "password","password"};
		
		boolean[] memberLoggedOn = new boolean[NO_OF_members];
		
					
		 	
		int memberCount = 0;
		while(memberCount < NO_OF_members)
		{
// client has attempted to get a connection to server, now 
// create a socket to communicate with this client 
			try
			{	
				Socket client = serverSocket.accept(); 
				
// get input & output streams
				ObjectInputStream input = new ObjectInputStream(client.getInputStream());
				ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
				
// read username from input stream
				//String username = (String) input.readObject();
			
// read password from input stream
				//String password = (String) input.readObject();
				
				
				
				// read encrypted username and password
				EncryptedMessage username = (EncryptedMessage)input.readObject();
				EncryptedMessage password = (EncryptedMessage)input.readObject();

				// output encrypted username and password
				addOutput("\nLogin Details Received\n----------------------------------------");
				addOutput("encrypted username : " + username.getMessage());
				addOutput("encrypted password : " + password.getMessage());

				// decrypt username and password
				username.decrypt();
				password.decrypt();

				// output decrypted username and password
				addOutput("decrypted username : " + username.getMessage());
				addOutput("decrypted password : " + password.getMessage());
				
				int pos = -1;
				boolean found = false;
		
// check to ensure that user is registered to message app
				for(int l = 0; l < usernames.length; l++)
				{	
					if(username.getMessage().equals(usernames[l]) && password.getMessage().equals(passwords[l]) && !memberLoggedOn[l])
					{	
						memberLoggedOn[l] = true;
						found = true;
						pos = l;
						
						if(username.getMessage().equals(usernames[0]))
						{
							output.writeObject(memberNumbers[0]);
						}
						else if(username.getMessage().equals(usernames[1]))
						{
							output.writeObject(memberNumbers[1]);
						}
						else if(username.getMessage().equals(usernames[2]))
						{
							output.writeObject(memberNumbers[2]);
						}
						else if(username.getMessage().equals(usernames[3]))
						{
							output.writeObject(memberNumbers[3]);
						}
						else
						{
							System.out.println("Member credentials doesn't match");
						}
						
					}

				}
//Player registered, start new thread for player
				if(found)
				{	
					output.writeObject(new Boolean(true));
					
					
/* spawn a new thread, i.e. an instance of class ServerThread
   to run parallel with Server and handle all communications
   with this client */
					ServerThread player = new ServerThread(input, output, usernames[pos]);
					
// add this thread to the array list
					members.add(player);
					
// start thread - execution of the thread will begin at method run
					player.start();
					String name = usernames[pos];
					memberNames.add(name);
					
				}
//Player not registered
				else
				{
					output.writeObject(6);
					output.writeObject(new Boolean(false));
				}
					
					
			}
			catch(IOException e) // thrown by Socket
			{	System.out.println(e);
				System.exit(1);
			}
			catch(ClassNotFoundException e) // thrown by method readObject
			{	System.out.println(e);
				System.exit(1);
			}
		}
	
// add message to text output area
		addOutput("Welcome to messenger");
	}
	
	
	
// add message to text output area	
	void addOutput(String s)
	{	
		outputArea.append(s + "\n");
		outputArea.setCaretPosition(outputArea.getText().length());
	}
	
	
	synchronized String getMessage()
	{	
		String str = "Get Message Method";
		return str;
	} 
	
// main method of class P7_Server
		public static void main(String args[])
		{	
			Server_Messenger messServer = new Server_Messenger();
			messServer.getMembers();
		}
	
	
	
	
	
// beginning of class ServerThread		
	private class ServerThread extends Thread 
	{	
		ObjectInputStream threadInputStream;
		ObjectOutputStream threadOutputStream;
		String memberName;
		
		int memberNumber; 

	
// initialise input stream, output stream & player name		
		public ServerThread(ObjectInputStream in, ObjectOutputStream out, String name)
		{	
			threadInputStream = in;
			threadOutputStream = out;
			memberName = name;
  		}
 
  		public void run()
  		{	try
  			{	
  			
 //Initial message when joining chat
  			/* when method start() is called thread execution will begin at  
  		    	   the following statement */
  		    	String broadcastMessage = memberName + " has got a connection.";  	 
  				// add message to server text output area 
  				addOutput(broadcastMessage);
  				// send message to client - for broadcast text output area
  				broadcast(broadcastMessage);
  			
  				//threadOutputStream.writeObject(memberName + " Has joined the chat!");
  				//System.out.println(memberName + " Has joined the chat!");
  				
  				
  				
				String clientMessage;
			
				boolean isConnected = true;
				
//Allow server to receive messages from client 				
				while(isConnected)
				{
					
//Boolean to check if private (OMIT IF NOT WORKING)
					
					boolean isPrivate = false;
					int memberToSend = -1;
					int memberSent = -1;
					
					isPrivate = threadInputStream.readBoolean();
						if(isPrivate)
						{
							memberToSend = (int)threadInputStream.readObject();
							memberSent = (int)threadInputStream.readObject();
						}
						
					
					
					
//Decompression code
					String newMessage;
					String messageDecompressed;
					newMessage = (String)threadInputStream.readObject();
					
					int compressedSize = newMessage.length();
					addOutput("***MESSAGE INFORMATION***");
					addOutput("Message compresed Size: " + Integer.toString(compressedSize));
					
					int decompressedSize;
					
					
					CompressedMessage cm = new CompressedMessage(newMessage);
					cm.decompress();
					decompressedSize = cm.getMessage().length();
					
					addOutput("Message decompresed Size: " + Integer.toString(decompressedSize));
					addOutput("__________________________");
					addOutput("");
					
					messageDecompressed = cm.getMessage();
				
					System.out.println(messageDecompressed);
					
//Add to output area
					addOutput(messageDecompressed);
					
					
					
//Broadcast to all members
					
						if(!isPrivate)
						{
							broadcast(messageDecompressed);	
						}
						
//Send to one person only
						else
						{
							privateMessage(messageDecompressed,memberToSend,(memberSent-1));
						}
					
					
				
				}
				
  			}
// thrown by method readObject, writeObject, close
			catch(IOException e) 
			{	
				System.out.println(e);
				System.exit(1);
			}
	
  			catch (ClassNotFoundException e) 
  			{
				e.printStackTrace();
			}
   		}
   	
  		protected void privateMessage(String str,int memberToSend,int memberSent)
  		{
  			synchronized(members)
  			{
  				System.out.println("Sending a PRIVATE message, standby");
  				addOutput("Will be sent as PRIVATE message");
  				
  				ServerThread singleThread = members.get(memberToSend);
  				ServerThread senderThread = members.get(memberSent);
  				
				try
//Send broadcast message to one member 				
				{
					String messageCompressed;
					CompressedMessage cmOut = new CompressedMessage(str);
					cmOut.compress();
					messageCompressed = cmOut.getMessage();
				
					singleThread.threadOutputStream.writeObject( "\n***NEW PRIVATE MESSAGE***\nDirect message to you from " + messageCompressed);
					senderThread.threadOutputStream.writeObject(messageCompressed);

				}
				
				catch(IOException e) // thrown by method writeObject
				{	System.out.println(e);
					System.exit(1);
				}
  			}
  		}
  		
  		
   		protected void broadcast(String str) 
		{	
   			synchronized(members)
			{	  		
   				System.out.println("Sending a GROUP message, standby");
   				addOutput("Will be sent as GROUP MESSAGE");
   				
				for (ServerThread sThread : members)
				{	
					try
//Send broadcast message to all members 				
					{	
						
//Compression code
						String messageCompressed;
						CompressedMessage cmOut = new CompressedMessage(str);
						cmOut.compress();
						messageCompressed = cmOut.getMessage();
						
//Write to clients						
						sThread.threadOutputStream.writeObject(messageCompressed);
					}
					
					
					
					
					
					
					
					catch(IOException e) // thrown by method writeObject
					{	System.out.println(e);
						System.exit(1);
					}
				}
			}
		}
	}
}

//Student name: Ryan McCloskey
//
//Student number: 40128312
//
//Module code: CSC2008
//
//Practical day: Monday

//NOTE UPDATED PRIVATE MESSAGING WORKING 07_12_15 9:50am



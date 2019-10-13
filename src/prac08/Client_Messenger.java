package prac08;

//Imports (Sampled from prac07 solution)

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;



import java.net.*;
import java.io.*;

public class Client_Messenger extends JFrame{

//Components for client panel
	Container container;
	BorderLayout bLayout;
	JPanel mainPanel,logonPanel,logonPanelButtons,outputPanel,inputPanel,membersPanel,failedLogonPanel,emojPanel;
	JLabel lblLogonTitle,lblUserName,lblPassword,lblmainTitle,lblLogonFailed;
	JLabel imgMessenger;
	ImageIcon icnMessenger;
	JTextArea outputArea,inputArea,failedLogonArea;
	JTextField userName,message;
	JPasswordField password;
	
	boolean isPrivate;
	int sendingNumber;
	int yourNumber;
	
	JButton btnLogon,btnCancel,btnLogout,btnSendMessage,btnMember1,btnMember2,btnMember3,btnMember4,btnAllMembers;
	JButton emoj1,emoj2,emoj3,emoj4;
	ButtonHandler buttonHandler;
	
	String logonName;
	String whosChatting;
	
	

//Socket used for communication
	Socket socketClient;
// output stream - data sent to the server will be written to this stream
	ObjectOutputStream outputStreamClient;
// input stream - data sent by the server will be read from this stream
	ObjectInputStream inputStreamClient;
	
	
	public Client_Messenger()
	{
		super("Messenger Application");
		addWindowListener(new WindowAdapter(){
				
				public void windowClosing(WindowEvent e)
				{	
					System.exit(0);
				}
			}
		);
		
//Container Construct
		container = getContentPane();
		container.setLayout(new BorderLayout());
		
//Login panel 
	//Text Area
		logonPanel = new JPanel();
		logonPanel.setLayout(new GridLayout(2,2,5,5));
		
		lblUserName = new JLabel("User Name:");
		lblPassword = new JLabel("Password");
		
		userName = new JTextField(10);
		password = new JPasswordField(10);
		
		logonPanel.add(lblUserName);
		logonPanel.add(userName);
		logonPanel.add(lblPassword);
		logonPanel.add(password);
	
	//Button Area
		logonPanelButtons = new JPanel();
		logonPanelButtons.setLayout(new GridLayout(2,1,5,5));
		
		btnLogon = new JButton("Log in");
		btnCancel = new JButton("Close");
		buttonHandler = new ButtonHandler();
		
		btnLogon.addActionListener(buttonHandler);
		btnCancel.addActionListener(buttonHandler);
		
		logonPanelButtons.add(btnLogon);
		logonPanelButtons.add(btnCancel);
		
//Add panels to container		
		container.add(logonPanel,BorderLayout.CENTER);
		container.add(logonPanelButtons,BorderLayout.SOUTH);
		
		setSize(300,125);
		setResizable(false);
		setVisible(true);
	}	
	
	
	
	void setupApplication(boolean loggedOn,String message)
	{
//Remove Login Panels from container
		container.remove(logonPanel);
		container.remove(logonPanelButtons);
		
		

//Set panel if login failed (Sampled from P7_Client)
		if(!loggedOn)
		{
			failedLogonPanel = new JPanel();
			
			failedLogonArea = new JTextArea(12,30);
			failedLogonArea.setEditable(false);
			failedLogonArea.setLineWrap(true);
			failedLogonArea.setWrapStyleWord(true);
			failedLogonArea.setFont(new Font("Verdana", Font.BOLD, 11));

// add message to text area
			failedLogonArea.setText(message);
			
			outputArea = new JTextArea(5,30);
			outputArea.setForeground(Color.DARK_GRAY);
			outputArea.setFont(new Font("Verdana",Font.PLAIN,11));
			outputArea.setEditable(false);
			outputArea.setText("Logonon Unsuccessful");
			
			failedLogonPanel.add(outputArea);
			failedLogonPanel.add(new JScrollPane(outputArea));	
			container.add(failedLogonPanel, BorderLayout.CENTER);
			setSize(375,300);
		}
		else
		{			
//Output messages panel		
			outputPanel = new JPanel();
			JLabel outputTitle = new JLabel("Chat Messages");
			outputPanel.add(outputTitle,BorderLayout.NORTH);
			outputArea = new JTextArea(15,30);
			outputArea.setForeground(Color.DARK_GRAY);
			outputArea.setFont(new Font("Verdana",Font.PLAIN,11));
			outputArea.setEditable(false);
			outputArea.setLineWrap(true);
			outputArea.setWrapStyleWord(true);
			
			
//Emoticon buttons
			emojPanel = new JPanel();
			emojPanel.setLayout(new GridLayout(1,4,5,5));
			emoj1 = new JButton(":)");
			emoj2 = new JButton(":(");
			emoj3 = new JButton(";)");
			emoj4 = new JButton("<3");
			
			emoj1.addActionListener(buttonHandler);
			emoj2.addActionListener(buttonHandler);
			emoj3.addActionListener(buttonHandler);
			emoj4.addActionListener(buttonHandler);
			
			emojPanel.add(emoj1);
			emojPanel.add(emoj2);
			emojPanel.add(emoj3);
			emojPanel.add(emoj4);

			
			
			outputPanel.add(outputArea,BorderLayout.CENTER);
			outputPanel.add(new JScrollPane(outputArea),BorderLayout.CENTER);
			outputPanel.add(emojPanel,BorderLayout.SOUTH);
//Buttons for main panel 
			icnMessenger = new ImageIcon("images/Messaging_256x256-32.png");
			imgMessenger = new JLabel(icnMessenger);
			
			
			
			ButtonHandler bHandler = new ButtonHandler();
			btnSendMessage = new JButton("Send");
			btnSendMessage.setEnabled(false);
			btnSendMessage.addActionListener(bHandler);
			
			btnLogout = new JButton("Log Out");
			btnLogout.addActionListener(bHandler);
			
			btnAllMembers = new JButton("Group");
			btnAllMembers.addActionListener(bHandler);
			
			btnMember1 = new JButton("Ryan");
			btnMember1.addActionListener(bHandler);
			//btnMember1.setEnabled(false);
			
			btnMember2 = new JButton("Allen");
			btnMember2.addActionListener(bHandler);
			//btnMember2.setEnabled(false);

			
			btnMember3 = new JButton("Rick");
			btnMember3.addActionListener(bHandler);
			//btnMember3.setEnabled(false);

			btnMember4 = new JButton("Mort");
			btnMember4.addActionListener(bHandler);
			//btnMember4.setEnabled(false);

			JPanel buttonPanel1 = new JPanel(new GridLayout(2,1,10,10));
			
			buttonPanel1.add(btnSendMessage);
			buttonPanel1.add(btnLogout);
			
			
//Input messages panel
			inputPanel = new JPanel();
			JLabel inputTitle = new JLabel("Send Message");
			
			
			inputArea = new JTextArea(5,20);
			inputArea.setEditable(true);
			inputArea.setLineWrap(true);
			inputArea.setWrapStyleWord(true);
			inputArea.setForeground(Color.BLACK);
			inputArea.setFont(new Font("Verdana", Font.BOLD, 11));
			
			
			inputPanel.add(inputTitle,BorderLayout.NORTH);
			inputPanel.add(inputArea,BorderLayout.CENTER);
			inputPanel.add(buttonPanel1, BorderLayout.SOUTH);
			
//Members buttons Panel
			membersPanel = new JPanel(new GridLayout(7,1,5,5));
			JLabel membersTitle = new JLabel("Members");
			
			membersPanel.add(imgMessenger);
			membersPanel.add(membersTitle);
			membersPanel.add(btnMember1);
			membersPanel.add(btnMember2);
			membersPanel.add(btnMember3);
			membersPanel.add(btnMember4);
			membersPanel.add(btnAllMembers);	
			
//Add panels to main messaging panel			
			
			container.add(outputPanel,BorderLayout.CENTER);
			container.add(inputPanel,BorderLayout.SOUTH);
			container.add(membersPanel,BorderLayout.WEST);
			//container.add(mainPanel);
			
			try 
			{
				String initialConnectMessage;
				initialConnectMessage = (String)inputStreamClient.readObject();
				outputArea.setText(outputArea.getText() + "\n"+ initialConnectMessage);
				
			} 
			catch (ClassNotFoundException | IOException e) 
			{
				e.printStackTrace();
			}
			
			setSize(500,500);
			setResizable(false);
			setVisible(true);
		}
	}
	
	void getConnections()
	{	
		try
		{
//Initialise a socket and get a connection to server
			socketClient = new Socket(InetAddress.getLocalHost(), 6000);
			
//Get input & output object streams
			outputStreamClient = new ObjectOutputStream(socketClient.getOutputStream());
			inputStreamClient = new ObjectInputStream(socketClient.getInputStream());
			
//Create a new thread of ClientThread, sending input  
//stream variable as a parameter 
// start thread - execution will begin at method run	
			
			ClientThread t = new ClientThread(inputStreamClient);
			t.start();
		}
		catch(UnknownHostException e) 
		{	
			System.out.println(e);
			System.exit(1); 
		}
		catch(IOException e) 
		{	
			System.out.println(e);
			System.exit(1); 
		} 
	}
	

//Close input stream, output stream, socket
	void closeStreams()
	{	try
      	{	

      		outputStreamClient.close();
			inputStreamClient.close();
			socketClient.close();
		}
		catch(IOException e) 
		{	
			System.out.println(e);
			System.exit(1); 
		}
	}
	

	
//get text from input (will be added to addOutput)	
	String getInput()
	{
		String message = inputArea.getText();
		inputArea.setText("");
		return message;
	}
	
	void setMessage(String message)
	{
		
		message = logonName + ": " + message; 
//Compress message for sending
		String compressedMessageOut;
		CompressedMessage cOut = new CompressedMessage(message);
		cOut.compress();
		compressedMessageOut = cOut.getMessage();
		
// send a message to the server		
		try
		{	
			outputStreamClient.writeBoolean(isPrivate);
			
			if(isPrivate)
			{
				outputStreamClient.writeObject(sendingNumber);
				outputStreamClient.writeObject(yourNumber);
			}
			

			outputStreamClient.writeObject(compressedMessageOut);
			
			
		}
		catch(IOException e) // thrown by method writeObject
		{	System.out.println(e);
			System.exit(1);
			}
	
	}
	
	void sendAMessage()
	{
//Boolean check if private 
		
		
		
		String messageToSend;
		messageToSend = getInput();
		
		setMessage(messageToSend);
		inputArea.append("");
	}
	
	
	void getMessage()
	{
		String recievedMessage;
		String compressedMessageIn;
		try 
		{
			recievedMessage = (String)inputStreamClient.readObject();
			CompressedMessage cmIn = new CompressedMessage(recievedMessage);
			cmIn.decompress();
			compressedMessageIn = cmIn.getMessage();
			
			
//For group chat
			System.out.println("TEST: This should be a gorup chat message");
			System.out.println(compressedMessageIn);
			outputArea.setText(outputArea.getText() + "\n" + compressedMessageIn);
		
		} 
		catch (ClassNotFoundException | IOException e)
		{
			e.printStackTrace();
		}	
	}
	
	
//Send Log in details to server (sampled from P7_Client	)
	void sendLoginDetails()
		{	try
			{	
				EncryptedMessage uname = new EncryptedMessage(userName.getText());
				uname.encrypt();

				// get password from password field and encrypt
				EncryptedMessage pword = new EncryptedMessage(new String (password.getPassword()));
				pword.encrypt();
				
				outputStreamClient.writeObject(uname);
				// send password to server
				outputStreamClient.writeObject(pword);
				
				logonName = userName.getText();
			}
			catch(IOException e) 
			{	System.out.println(e);
				System.exit(1);
			}
		}
		
	
	
//ButtonHandler class for handling action events (sampled skeleton from P7_Client)	
	private class ButtonHandler implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			
			if(e.getSource() == btnLogon)
			{
				//Run method to send login details to server
				sendLoginDetails();
			}
			else if(e.getSource() == btnCancel)
			{
				System.exit(0);
			}
			else if(e.getSource() == btnLogout)
			{
				
				System.exit(0);
			}
				
			
			
			
//Sends message to output area	
			else if(e.getSource() == btnSendMessage)
			{
				sendAMessage();
			}
			else if(e.getSource() == emoj1)
			{
				inputArea.setText(inputArea.getText() + ":)");
			}
			else if(e.getSource() == emoj2)
			{
				inputArea.setText(inputArea.getText() + ":(");
			}
			else if(e.getSource() == emoj3)
			{
				inputArea.setText(inputArea.getText() + ";)");
			}
			else if(e.getSource() == emoj4)
			{
				inputArea.setText(inputArea.getText() + "<3");
			}
//For messaging buttons
			else
			{
				boolean btnEnabled = true;
				btnSendMessage.setEnabled(true);
			
//Sets what happens when buttons are pressed
				if(e.getSource() == btnMember1)
				{
					
					
					btnMember1.setEnabled(false);
					btnMember2.setEnabled(true);
					btnMember3.setEnabled(true);
					btnMember4.setEnabled(true);
					btnAllMembers.setEnabled(true);
					
					//privateChat(btnMember1.getText(),1);
					isPrivate = true;
					sendingNumber = 0;
					
				

				}
				else if(e.getSource() == btnMember2)
				{
					btnMember1.setEnabled(true);
					btnMember2.setEnabled(false);
					btnMember3.setEnabled(true);
					btnMember4.setEnabled(true);
					btnAllMembers.setEnabled(true);
					
					//privateChat(btnMember2.getText(),2);
					isPrivate = true;
					sendingNumber = 1;

				}
				else if(e.getSource() == btnMember3)
				{
					btnMember1.setEnabled(true);
					btnMember2.setEnabled(true);
					btnMember3.setEnabled(false);
					btnMember4.setEnabled(true);
					btnAllMembers.setEnabled(true);
					
					//privateChat(btnMember3.getText(),3);
					isPrivate = true;
					sendingNumber = 2;

				}
				else if(e.getSource() == btnMember4)
				{
					btnMember1.setEnabled(true);
					btnMember2.setEnabled(true);
					btnMember3.setEnabled(true);
					btnMember4.setEnabled(false);
					btnAllMembers.setEnabled(true);
					
					//privateChat(btnMember4.getText(),4);
					isPrivate = true;
					sendingNumber = 3;

				}
				
				else if(e.getSource() == btnAllMembers)
				{
					btnMember1.setEnabled(true);
					btnMember2.setEnabled(true);
					btnMember3.setEnabled(true);
					btnMember4.setEnabled(true);
					btnAllMembers.setEnabled(false);
					
					
					isPrivate = false;
				}
				
			}
			
		}	
		
	}
	
	
//Method for private chats
	public void privateChat(String reciever,int recieverNo)
	{
	}
// main method of class Client
		public static void main(String args[])
	   	{	
			Client_Messenger client = new Client_Messenger();
			client.getConnections();
		}
	
		
		
//Client Thread Class
	private class ClientThread extends Thread
	{
		ObjectInputStream threadInputStream;
		
// initialise input stream		
		public ClientThread(ObjectInputStream in)
		{
			threadInputStream = in;
		}
		
		public void run()
		{	
// when method start is called thread execution will begin in this method
//Read Boolean value sent by server - it is converted to
//a primitive boolean value  
  		    
			try
			{	
//Identify who's connected with an int (For private messaging)
				
				int incomingNo;
				
				
				incomingNo = (int) inputStreamClient.readObject();
				yourNumber = incomingNo;
			
				System.out.println(yourNumber);
				
				boolean loggedOn = (Boolean)threadInputStream.readObject(); 

				if(!loggedOn)
				{ 	


// call method to display message
					setupApplication(loggedOn, "Logon unsuccessful");

// call method to close input & output streams & socket
					closeStreams();	
				}
				else
				{
					setupApplication(loggedOn, "Logged in");
					boolean connected = true;
					
					while(connected)
					{
						getMessage();
					}
				
				}
				
			}
			catch(IOException e) // thrown by method readObject
			{	System.out.println(e);
				System.exit(1);
			}	
			catch(ClassNotFoundException e) // thrown by method readObject
			{	System.out.println(e);
				System.exit(1);
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









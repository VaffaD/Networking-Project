package server;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;

public class UDPServer { 
  
	private DatagramSocket datagramSocket;
	private byte[] recieveBufferDP = new byte[1024];
	private byte[] sendBufferDP;
	public static ArrayList<ClientLog> clientList;
	private static File logFile;
	
	public UDPServer (DatagramSocket datagramSocket) {
		this.datagramSocket = datagramSocket;
	}
	
	public static void main(String args[]) throws SocketException {	
		DatagramSocket datagramSocket = new DatagramSocket(1234);
		UDPServer server = new UDPServer(datagramSocket);
		
		// Create a new ServerLog file
		try {
		      logFile = new File("ServerLog.txt");
		      if (logFile.createNewFile()) {
		    	  System.out.println("Log File created: " + logFile.getName());
		      } 
		      else {
		    	  System.out.println("ServerLog.txt File already exists...");
		    	  System.out.println("Clearing Log File contents...");
		    	  logFile.delete();
		    	  logFile.createNewFile();
		      }
	    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
	    }
		
		// Start Server UDP MEssage Processing
		server.recieveThenSend();
		
		// Terminate Program
		String timeStamp = currentTimeStamp();
		logMessage(timeStamp + " - All Connections have been closed, Terminating Server Instance..");
		datagramSocket.close();
	}
	
	public void recieveThenSend() {
		System.out.println("Server is Online");
		clientList = new ArrayList<ClientLog>();
		
		// Server will not terminate
		do {
			try{
				// Receive the Message from a Client
				DatagramPacket recievedClientDP = new DatagramPacket(recieveBufferDP, recieveBufferDP.length);
				datagramSocket.receive(recievedClientDP);
				LocalDateTime currentTime = LocalDateTime.now();
				String timeStamp = currentTimeStamp();
				String messageFromClient = new String(recievedClientDP.getData(), 0, recievedClientDP.getLength());
				int portNum = recievedClientDP.getPort();
				InetAddress inetAddress = recievedClientDP.getAddress();

				String clientName = new String();
				
				// Preserves the Client name during an Exit request
				if(messageFromClient.toLowerCase().matches("^exit.*")) {
					clientName = "\n" + timeStamp + " - Client Name: " + findClientLog(portNum).getClientName();
				}
				
				// Generate Response message
				String messageToClient = processMessage(messageFromClient, portNum, timeStamp, currentTime);
				
				// Create Received Message Information
				if(!messageFromClient.toLowerCase().matches("^exit.*")) {
					clientName = "\n" + timeStamp + " - Client Name: " + findClientLog(portNum).getClientName();
				}
				String clientInfo = timeStamp + " - Client Port#: " + portNum;
				String clientMessage = timeStamp + " - Message From Client: " + messageFromClient;
				
				// Send Response to Client
				sendBufferDP = messageToClient.getBytes();
				DatagramPacket sendingClientDP = new DatagramPacket(sendBufferDP, sendBufferDP.length, inetAddress, portNum);
				datagramSocket.send(sendingClientDP);
				
				// Log the Received and Sending Message info to the Console and Log File
				logMessage(clientName);
				logMessage(clientInfo);
				logMessage(clientMessage);
				logMessage(timeStamp + " - Message to Client: " + messageToClient);
			} 
			catch(IOException e) {
				e.printStackTrace();
				break;
			}
		}while(!clientList.isEmpty());
	}
	  
	private static void logMessage(String logMessage) {
		System.out.println(logMessage);
	    try {
	    	PrintWriter printWriter = new PrintWriter(new FileWriter("ServerLog.txt", true));
	    	printWriter.write("\n" + logMessage);
	    	printWriter.close();
		} 
	    catch (IOException e) {
		    System.out.println("Log File Error Occurred.");
		    e.printStackTrace();
		}
	}

	public static String processMessage(String messageFromClient, int portNum, String timeStamp, LocalDateTime currentTime) {
		
		String messageToSend;
		
		// First Request: Store Client Info
		if(messageFromClient.toLowerCase().matches("^name.*")) {
			String clientName = messageFromClient.substring(7);
			processClientHeader(portNum, timeStamp , currentTime, clientName);
			messageToSend = "Connection Established, Hello " + clientName;
		}
		// Math Request: Process Math String
		else if(messageFromClient.matches("(\\d{1,4} [\\+\\-\\*\\/] ){1,3}\\d{1,4}")){
			messageToSend = parseMathString(messageFromClient);
		}
		// Exit Request: Remove Client Log and Acknowledge
		else if (messageFromClient.toLowerCase().matches("^exit.*")) {
			
			String connectionTimePeriod = findClientLog(portNum).calcTimeConnected(currentTime);
			clientList.remove(findClientLog(portNum));
			messageToSend = "Close Connection Request Received by Server; Time Connected: " + connectionTimePeriod;
		}
		// Invalid Request
		else {
			messageToSend = "REQUEST ERROR - Unknown Request Received from the Client";
		}
		
		return messageToSend;
	}
	
	private static ClientLog findClientLog(int portNum) {
		ClientLog foundClient = new ClientLog();
		
		for(ClientLog client: clientList) {
			if(portNum == client.getPortNum()) {
				foundClient = client;
				break;
			}
		}
		
		return foundClient;
	}

	public static void processClientHeader(int portNum, String startTime, LocalDateTime timeStamp, String clientName) {
		
		boolean newClient = true;
		
		for(ClientLog client: clientList) {
			if(portNum == client.getPortNum()) {
				newClient = false;
				break;
			}
		}
		
		if(newClient) {
			clientList.add(new ClientLog(portNum, startTime, timeStamp, clientName));
		}
	}
  
	// requires input to have whitespace between operators and operands
	// ignores precedence
	public static String parseMathString(String eq) {
		  
		  String[] EQ = eq.split("\s");
		  Integer eqOutput;
		  String results;
	
		  if(EQ[0].matches("[\\+\\-\\*\\/]") ||  EQ[EQ.length - 1].matches("[\\+\\-\\*\\/]")) {
			   results = "FORMAT ERROR: Input Math Equation is Invalid";
		  }
		  else {
			  eqOutput = Integer.parseInt(EQ[0]);
			  
			  for(int i = 0; i < EQ.length; i++) {
				  switch (EQ[i]) {
				  
					  case "+":	eqOutput = eqOutput + Integer.parseInt(EQ[i+1]);
						  		break;
					  case "-":	eqOutput = eqOutput - Integer.parseInt(EQ[i+1]);
					  			break;
					  case "*":	eqOutput = eqOutput * Integer.parseInt(EQ[i+1]);
					  			break;
					  case "/":	eqOutput = eqOutput / Integer.parseInt(EQ[i+1]);
					  			break;
				  }
			  }
			  
			  results = "Answer: " + Integer.toString(eqOutput);
		  }
		  
		  return results;
	}
	
    private static String currentTimeStamp() {
    	LocalDateTime timeSent = LocalDateTime.now();
		DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss.n");
		String timeStamp = timeSent.format(timeFormat).substring(0, 12);
		
		return timeStamp;
    }
}  


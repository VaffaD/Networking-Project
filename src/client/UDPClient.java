package client;
import java.io.*; 
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit; 
 
class UDPClient {
	
	private DatagramSocket datagramSocket;
	private InetAddress inetAddress;
	private byte[] recieveBufferDP = new byte[1024];
	private byte[] sendBufferDP;
	
	public UDPClient(DatagramSocket datagramSocket, InetAddress inetAddress) {
		this.datagramSocket = datagramSocket;
		this.inetAddress = inetAddress;
	}
	
    public static void main(String args[]) throws Exception {
    	
    	DatagramSocket datagramSocket = new DatagramSocket();
    	InetAddress inetAddress = InetAddress.getByName("localhost");
    	UDPClient client = new UDPClient(datagramSocket, inetAddress);
      
    	if(args[0].toLowerCase().matches("-m")) {
    		
    		// Request the Client's Name
        	System.out.println("Please enter your name:");
        	Scanner scanner = new Scanner(System.in);
        	client.sendThenReceive("Name - " + scanner.nextLine());
        	
    		boolean terminate = false;
    	
    		// Run until the user requests to exit
	    	while(!terminate) {
		    	
	    		//display commands available to the user
	    		printInputCommands();
	    		
	    		//retrieve users input
	    		String messageFromClient = new String(scanner.nextLine());
		    	
	    		// Math equeation request
		    	if(messageFromClient.toLowerCase().matches("(\\d{1,4} [\\+\\-\\*\\/] ){1,3}\\d{1,4}")) {
		    		client.sendThenReceive(messageFromClient);
		    	}
		    	// Display Menu request
		    	else if(messageFromClient.toLowerCase().matches("menu")) {
		    		printInputCommands();
		    	}
		    	// Exit program instance request
		    	else if(messageFromClient.toLowerCase().matches("exit")) {
		        	client.sendThenReceive(messageFromClient);
		    		terminate = true;
		    		String timeStamp = currentTimeStamp();
		    		System.out.println(timeStamp + " - Client: Terminating Client Program");
		    	}
		    	// Invalid user input
		    	else {
		    		printMathInputOptions();
		    	}
	    	}
	    	scanner.close();
    	}
    	// Program auto generates math equations and send intervals
    	else if(args[0].toLowerCase().matches("-a") && args.length == 2) {
    		
    		// Retrieve the username from the command line interface
    		client.sendThenReceive("Name - " + args[1]);
    		
    		// Generate 3 Math Equations requests and an Exit request
	    	for (int i = 0; i < 4; i++) {
		    	String messageFromClient = randomMathString();
	    	
		    	TimeUnit.SECONDS.sleep(randomSleepTimer());
	    		
		    	if(i == 3) {
		    		client.sendThenReceive("exit");
		    		String timeStamp = currentTimeStamp();
		    		System.out.println(timeStamp + " - Client: Terminating Client Program");
		    	}
		    	else {
		    		client.sendThenReceive(messageFromClient);
		    	}
	    	}
    	}
    	// CLI Arguments provided were invalid
    	else {
    		System.out.println(args.length);
    		System.out.println("Incorrect CLI arguements, correct CLI Arguments are as follows:");
    		System.out.println("\tManual Mode : UDPClient.exe -m");
    		System.out.println("\tAuto Mode : UDPClient.exe -a \"user-name\"");
    	}
    	
        datagramSocket.close();
    }
    
    public static void printMathInputOptions() {
    	System.out.println("\nMath Equation should adhere to the following rules:\n\n"
    			+ "\t1. Operands must be between 1-4 digits long\n"
    			+ "\t2. There must be between 2-4 operands total\n"
    			+ "\t3. Acceptable operators are: / * - +\n"
    			+ "\t4. Leave a space between operators and operands\n\n"
    			+ "\tEXAMPLE INPUT: 1 + 12 - 123 * 1234\n\n");
    }
    
    public static void printInputCommands() {
    	System.out.println("User Input Commands:\n\n"
    			+ "\t\"Exit\" - Terminates the connection and the program\n"
    			+ "\t\"Menu\" - Prints this menu to the console\n"
    			+ "\tMath Equation - Follow the Rules Listed Below");
    	
    	printMathInputOptions();
    }
    
    public void sendThenReceive(String messageToSend) {
	
		try {
			// Send a Message to the Server
			sendBufferDP = messageToSend.getBytes();
			DatagramPacket datagramPacket = new DatagramPacket(sendBufferDP, sendBufferDP.length, inetAddress, 1234);
			datagramSocket.send(datagramPacket);
			String timeStamp = currentTimeStamp();
			System.out.println(timeStamp + " - Client Message Sent: " + messageToSend);
	    	
			// Receive a Message from the Server
			DatagramPacket recievedClientDP = new DatagramPacket(recieveBufferDP, recieveBufferDP.length);
			datagramSocket.receive(recievedClientDP);
			String messageFromServer = new String(recievedClientDP.getData(), 0, recievedClientDP.getLength());
			
			// Create Time Stamp for received message
			timeStamp = currentTimeStamp();

			// Print the Received Message
			System.out.println(timeStamp + " - Server Message Recieved: " + messageFromServer + "\n");
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
    }
    
    private static String currentTimeStamp() {
    	LocalDateTime currentTime = LocalDateTime.now();
		DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss.n");
		String timeStamp = currentTime.format(timeFormat).substring(0, 12);
		
		return timeStamp;
    }
    
    private static int randomSleepTimer() {
    	Random rd = new Random();
    	return rd.nextInt(5);
    }
    
    private static String randomMathString() {
    	Random rd = new Random();
    	
    	int numOperand;
    	
    	do {
    		numOperand = rd.nextInt(4);
    	} while(numOperand < 2);
    	
    	ArrayList<String> mathString = new ArrayList<String>();
    	
    	for(int i = 0; i < numOperand; i++) {
    		mathString.add(Integer.toString(rd.nextInt(99)));
    		int tempOperator = rd.nextInt(3);
    		
    		if (tempOperator == 0) {
    			mathString.add("+");
    		}
    		else if (tempOperator == 1) {
    			mathString.add("-");
    		}
    		else if (tempOperator == 2) {
    			mathString.add("*");
    		}
    		else if (tempOperator == 3) {
    			mathString.add("/");
    		}
    		else {
    			mathString.add(" MATH STRING AUTO FORMATTING ERROR ");
        	}
    	}
    	
    	mathString.add(Integer.toString(rd.nextInt(99)));
    	
    	return mathString.toString().replaceAll("[\\[\\],]", "");
    }
}

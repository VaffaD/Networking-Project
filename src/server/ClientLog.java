package server;

import java.time.*;

public class ClientLog {

	private int portNum; 
	private String clientName;
	private String startTime;
	private LocalDateTime startConnectionTime;
	
	public ClientLog() {
	}
	
	public ClientLog(int portNum, String startTime, LocalDateTime startConnectionTime, String clientName) {
		this.startTime = startTime;
		this.portNum = portNum;
		this.startConnectionTime = startConnectionTime;
		this.clientName = clientName;
	}
	
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	
	public int getPortNum() {
		return portNum;
	}
	
	public String getClientName() {
		return clientName;
	}
	
	public String getStartTime() {
		return startTime;
	}
	
	public String calcTimeConnected(LocalDateTime endConnectionTime) {
		
		Duration durationTime = Duration.between(startConnectionTime, endConnectionTime);
		String seconds = Long.toString(durationTime.getSeconds());
		String nano = Long.toString(durationTime.getNano());
		String diffTime = seconds + "." + nano.substring(0, 3) + " seconds";
		
		return diffTime;
	}
}

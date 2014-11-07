package commands;

public class CmdObject implements java.io.Serializable {
	//Serializable allows an object to be represented as a sequence
	//of bytes. Serializable is used so that this cmdObject can be 
	//sent via TCP to the simulator. The simulator can convert 
	//this sequence of bytes back to the object if it has this class.

	/*
		cmd_type inlcudes:
		1) "initialize" > Purpose: to request for vehicleID and pos from Simulator
		2) "create_EMW" > Purpose: to request for unique eventID from Simulator
		3) "carinfoReq" > Purpose: to request for car info
	*/
	private String cmd_type;
	private int vehicleID;
	private int eventID;
	private float[] pos;

	public CmdObject(String cmd_type, int vehicleID, int eventID, float[] pos){
		this.cmd_type = cmd_type;
		this.vehicleID = vehicleID;
		this.eventID = eventID;
		this.pos = pos;
	}
	// These method may be handy when debugging or modifying cmdObject

	// cmd_type
	public String getCmd_Type(){
		return cmd_type;
	}
	public void setCmd_Type(String newcmd){
		this.cmd_type = newcmd;
	}
	// vehicleID
	public int getVehicleID(){
		return vehicleID;
	}
	public void setVehicleID(int newid){
		this.vehicleID = newid;
	}
	// eventID
	public int getEventID(){
		return eventID;
	}
	public void setEventID(int newid){
		this.vehicleID = newid;
	}
	// pos
	public float[] getPos(){
		return pos;
	}
	public void setPos(float[] newpos){
		this.pos = newpos;
	}

}

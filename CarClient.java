import commands.CmdObject;
import java.io.*;
import java.net.*;


public class CarClient{

	private Socket socket = null;
	private ObjectInputStream inputStream = null;
	private ObjectOutputStream outputStream = null;
	private boolean isConnected = false;

	public void requestSim(){
		while (!isConnected){
			try{
				// Test on my own. In reality, will need simulator's IP and port.
				socket = new Socket("localhost",5000);
				isConnected = true;
				outputStream = new ObjectOutputStream(socket.getOutputStream());
				// Test by sending a request to the simulator

				CmdObject command = new CmdObject("initial",null,null,null);
				outputStream.writeObject(command);
			}
			catch(SocketException sockerr){
				sockerr.printStackTrace();
			}
			catch(IOException ioerr){
				ioerr.printStackTrace();
			}

		}
	}
	public static void main(String[] args){
		CarClient client = new CarClient();
		client.requestSim();
	}
}

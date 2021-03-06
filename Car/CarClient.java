import java.io.*;
import java.net.*;
import java.util.*;
import static java.lang.System.out;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class CarClient{
	
	Semaphore nodeSemaphore = new Semaphore(1,true);
	String registeredX;
	String registeredY;
	String registeredDir;
	
	private HashMap<String, eventCreated> eventIDList = new HashMap<String, eventCreated>(); //store all events created by a car
	private HashMap<String, event>eventsHeard = new HashMap<String, event>(); 	//store all events heard
	
	private Socket socket = null;
	private ObjectInputStream inputStream = null;
	private ObjectOutputStream outputStream = null;
	private boolean isConnected = false;
	messageListener listener;
	DatagramSocket sock = new DatagramSocket();
	private int port;
	private String strIP = "192.168.173.220"; //<--192.168.173.220";
	
	static int regCount = 0;

	public void registerSim(){
		try{
			try{
				nodeSemaphore.acquire();
				}catch(InterruptedException ie){
					System.out.println ("semaphore failed");
				}
			
			regCount++;
			System.out.println("SEMAPHORE regCount = "+regCount);
			nodeSemaphore.release();
			
			DatagramSocket carSocket = new DatagramSocket();
			InetAddress simaddr = InetAddress.getByName("192.168.173.1"); //<--192.168.173.220
			int simport = 50000;


		
		byte[] sendData;
		/*
		StringBuilder request = new StringBuilder();
		request.append(ipaddr);
		request.append(":");
		request.append(port);
		*/

		//String strIP = caraddr.getHostAddress();
		String strPORT = Integer.toString(port);

		//Simulator Addr: 192.168.173.1 Port: 50000
		String reqMSG = "192.168.173.1:50000,"+ strIP + ":" + strPORT + ",register" + ",REGmsg"; //<-- 192/168.173.1:50000
		//String reqMSG = "000";
		sendData = reqMSG.getBytes();

		System.out.println("REGISTERSIM: Sending to Simulator");
		DatagramPacket sendRequest = new DatagramPacket(sendData, sendData.length, simaddr, simport);
		System.out.println("Registration request MSG = "+new String(sendData));
		carSocket.send(sendRequest);

		carSocket.close();
		/*
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		carSocket.receive(receivePacket);
		String fromSim = new String(receivePacket.getData());
		System.out.println("FROM SIMULATOR:" + fromSim);
		System.out.println("---------------------------------------------");

		carSocket.close();
		*/
		
		}catch(IOException e1){
			System.out.println("UnknownHostError?");
			e1.printStackTrace();
		}
	}
	
	
	public void requestPOSDIR(){
		try{
			DatagramSocket carSocket = new DatagramSocket();
			InetAddress simaddr = InetAddress.getByName("192.168.173.1");//<--192.168.173.1
			int simport = 50000;
			byte[] receiveData = new byte[1024];
			byte[] sendData = new byte[1024];
			String strPORT = Integer.toString(port);
			
			//Create message addressed to simulator
			String reqMSG = "192.168.173.1:50000,"+ strIP + ":" + strPORT + ",1" + ",randomMessage";
			sendData = reqMSG.getBytes();

			System.out.println(port+" "+"SENDING pos/dir REQUEST: Sending to Simulator");
			DatagramPacket sendRequest = new DatagramPacket(sendData, sendData.length, simaddr, simport);
			carSocket.send(sendRequest);

			
			//should'nt need cause car is already listening
			/*
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			carSocket.receive(receivePacket);
			String fromSim = new String(receivePacket.getData());
			System.out.println("requestPOSDIR gets CONTAINS:" + fromSim);
			System.out.println("---------------------------------------------");
			 */
			carSocket.close();
			
			
		}catch(IOException e1){
			System.out.println("UnknownHostError?");
			e1.printStackTrace();
		}
	}
	
	
	public void sendMSG(String msgType,String message){
		try{
			DatagramSocket carSocket = new DatagramSocket();
			InetAddress simaddr = InetAddress.getByName("192.168.173.1"); //<-- 192.168.173.1
			int simport = 50000;

			byte[] receiveData = new byte[1024];
			byte[] sendData = new byte[1024];
			String strPORT = Integer.toString(port);

			//Simulator Addr: 192.168.173.1 Port: 50000
			//message should be of form (type,paramaters)
			String reqMSG = "255.255.255.0:7000,"+ strIP + ":" + strPORT + "," + msgType + "," + message;
			sendData = reqMSG.getBytes();

			System.out.println("Sending to Simulator from sendMSG func");
			DatagramPacket sendRequest = new DatagramPacket(sendData, sendData.length, simaddr, simport);
			carSocket.send(sendRequest);
			carSocket.close();

		}catch(IOException e1){
			System.out.println("UnknownHostError?");
			e1.printStackTrace();
		}
	}
	
	private class messageListener extends Thread{
		DatagramSocket sock;
		String sentEWM[] = {"","","","","","",""};
		String currentEWM[]={"","","","","","",""}; //type,xpos,ypos,dir,ack_car_id,event_id,event_ttl
		String ewmPosDir[]={"","","",""};
		boolean EWMComplete = false;
		boolean isProcessingEWM = false;
		
		public messageListener() throws IOException
		{
			super("MESSAGELISTENER: STARTING THE THREAD");
			sock = new DatagramSocket(port);
		}
		public void run()
		{
			System.out.println("INSIDE THREAD.RUN: Start listening");
			
			while(true)
			{
				
				DatagramPacket pack;
				byte[] buf = new byte[256];
				pack = new DatagramPacket(buf, buf.length);
				try {
					sock.receive(pack);
				} catch (IOException e1) {
					System.out.println("Error.....");
					e1.printStackTrace();
				}
				String msg = (new String(pack.getData())).trim();
				System.out.println(port+"MESSAGELISTENER:Got message:" + msg);
				String arrayMSG[] = msg.split(",");

				String msgType = arrayMSG[2];
				
				//if it's an EWM message
				//Extract EWM msg info to propogate and request pos/dir from simulator
				if (msgType.equals("1")){
		
					//check if it's a new EWM ID
					if(!eventsHeard.containsKey(arrayMSG[8])){
						System.out.println(port+" SAW A NEW EWM!");
						//Capture relevant parts of the message
						sentEWM[0]=arrayMSG[2];//type
						sentEWM[1]=arrayMSG[3];//xpos
						sentEWM[2]=arrayMSG[4];//ypos
						sentEWM[3]=arrayMSG[5];//direction
						sentEWM[4]=arrayMSG[6];//ack_id
						sentEWM[5]=arrayMSG[7];//event id 
						sentEWM[6]=arrayMSG[8];//event ttl
						
					
						//request xpos,ypos,dir
						System.out.println(port+"NOW REQUESTING POSITION INFO FOR COMPARISON");
						isProcessingEWM = true;
						requestPOSDIR();
						
						
					}//end of if its an EWM message
						
						
				}
					
				
				//if it's a response to requestPOSDIR (or a registration message response)
				//insert xpos,ypos etc values into the EWM message being built
				if(msgType.equals("info")){
					
					registeredX=arrayMSG[3];
					registeredY=arrayMSG[4];
					registeredDir=arrayMSG[5];
					
					if(!isProcessingEWM){
					currentEWM[1] = arrayMSG[3]; //x-pos
					currentEWM[2] = arrayMSG[4]; //y-pos
					currentEWM[3] = arrayMSG[5]; //currently direction (should just make this direction and modify Adrian func?)
					currentEWM[4] = arrayMSG[6]; //currently diff in y coordinate, not used
					}
					
					if(isProcessingEWM){
						//store info for this car 
						currentEWM[1] = arrayMSG[3]; //x-pos
						currentEWM[2] = arrayMSG[4]; //y-pos
						currentEWM[3] = arrayMSG[5]; //currently direction (should just make this direction and modify Adrian func?)
						//currentEWM[4] = arrayMSG[6]; //currently diff in y coordinate, not used
						
						//PRINT DIAGNOSTICS
						System.out.println(port+" RD="+currentEWM[3]+", SD="+sentEWM[3]+", RX="+currentEWM[1]+", SX="+sentEWM[1]);
						System.out.println(port +"movingSameDirection returns/thinks should rebroadcast"+movingSameDirection_andIsBehind(currentEWM[3],sentEWM[3],currentEWM[1],sentEWM[1]));	
						System.out.println(port+" ReceiverX: "+currentEWM[1]+", SenderX: "+sentEWM[1]);
						System.out.println(port+" movingSameDirection = "+movingSameDirection(currentEWM[3],sentEWM[3]));
						System.out.println(port+" ReceiverDir: "+currentEWM[3]+", SenderDir: "+sentEWM[3]);
						//System.out.println(port+" thinks it should rebroadcast?: "+(isBehind(currentEWM[1],currentEWM[2],sentEWM[1],sentEWM[4]) && movingSameDirection(currentEWM[3],sentEWM[3])));
						
						//check if car is behind and moving in the same direction
						//if (isBehind(currentEWM[1],currentEWM[2],sentEWM[1],sentEWM[4]) && movingSameDirection(currentEWM[3],sentEWM[3])){
						if(movingSameDirection_andIsBehind(currentEWM[3],sentEWM[3],currentEWM[1],sentEWM[1])) {
							System.out.println(port+"TRYING TO REBROADCAST PACKET");
						
						
							//add to events heard
							eventsHeard.put(sentEWM[5], new event(sentEWM[5], sentEWM[6], true));
							
							//finish building packet for transmission
							currentEWM[0]=sentEWM[0];
							currentEWM[4]=sentEWM[4];
							currentEWM[5]=sentEWM[5];
							currentEWM[6]=sentEWM[6];
							
							
							//Check if EWM is finished being built
							for (int i=0; i<currentEWM.length; i++){
								if (currentEWM[i]==""){
									EWMComplete=false;
									break;
								}else
									EWMComplete=true;
							}
							
							
							if (EWMComplete){
								String EWMString = currentEWM[1];
								for(int i=2; i<currentEWM.length; i++)
									EWMString = EWMString + "," + currentEWM[i];
							
								//broadcast packet
								sendMSG(currentEWM[0],EWMString);
								
							//	System.out.println(port+"Ready to broadcast: "+currentEWM[0]+", "+currentEWM[1]+", "
								//		+currentEWM[2]+", "+currentEWM[3]+", "+currentEWM[4]+", "+currentEWM[5]+", "
									//	+currentEWM[6]+", "+currentEWM[7]+", ");
								
								EWMComplete=false;
								isProcessingEWM=false;
							}
						
						
						
						}
						
					}//end is processing EWM
				
				}//end message type = info
				
				
				
				//.info denotes the message originated at the simulator
				//if (!(msgType.equals("info"))){
				//requestPOSDIR();
				//}
				
				/*
				//if message was message was a response to a request for information..
				if ((msgType.equals("info"))){
					String sender_pos_x = arrayMSG[3];
					String sender_pos_y = arrayMSG[4];
				  //String sender_dir_x = arrayMSG[];
					String sender_dir = arrayMSG[5];
					String send_id = strIP+":"+port;
					String ack_car_id;
					String event_id;
					String event_ttl;
					
					}
				}*/
				
				/*
				String posdir[] = requestPORDIR();
				String sender_pos_x = posdir[0];
				String sender_pos_y = posdir[1];
				String sender_dir_x = posdir[2];
				String sender_dir_y = posdir[3];
				String send_id = strIP;
				String ack_car_id;
				String event_id;
				String event_ttl;
				*/
				
				/* Type 1 = EWM
				   Type 2 = ACK*/
				
				//if (msgType.equal("1")){
				//	reactEWM(arrayMSG[2],arrayMSG[3])	
				}
			}
		
	}
	
	public CarClient(int portNo) throws IOException {
		this.port = portNo;
		this.registerSim();
		System.out.println("Creating a listener");
		listener = new messageListener();
		listener.start();

	}
	
	static void displayInterfaceInformation(NetworkInterface netint) throws SocketException {
        out.printf("Display name: %s\n", netint.getDisplayName());
        out.printf("Name: %s\n", netint.getName());
        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
            out.printf("InetAddress: %s\n", inetAddress);
        }
        out.printf("\n");
     }

	
	public boolean isBehind(String receivingCar_x,String receivingCar_y,String sender_x,String sender_y) {
		if(Double.parseDouble(receivingCar_x) < Double.parseDouble(sender_x)) {
			return true;
		}
		else return false;
	}	
	
	
	public boolean movingSameDirection(String recievingCar_direction,String sender_direction) {
		if(Double.parseDouble(recievingCar_direction) > 0 && Double.parseDouble(sender_direction) > 0) {
			return true;
		}
		else if (Double.parseDouble(recievingCar_direction) < 0 && Double.parseDouble(sender_direction) < 0){
			return true;
		}
		else return false;
	}
	
	public boolean movingSameDirection_andIsBehind(String recievingCar_direction,String sender_direction, String recieving_x, String sending_x) {
		if(Double.parseDouble(recievingCar_direction) > 0 && Double.parseDouble(sender_direction) > 0) {
			// Cars moving to the right so if receiving cars x is smaller, then it is behind the EWM
			if(Double.parseDouble(recieving_x) < Double.parseDouble(sending_x)) {
				return true;
			}
			else return false;
		}
		else if (Double.parseDouble(recievingCar_direction) < 0 && Double.parseDouble(sender_direction) < 0){
			//Cars moving to the left, so if receiving car's x is larger, then it is behind the EWM
			if(Double.parseDouble(recieving_x) > Double.parseDouble(sending_x)) {
				return true;
			}
			else return false;
		}
		else return false;
	}
	
	
	
	
	public static void main(String[] args) throws SocketException,IOException{
		
		CarClient client0 = new CarClient(9000);
		
		try{
			Thread.sleep(500);
			}catch(InterruptedException ie){
				System.out.println("problem sleeping it");
			}

		
		
		CarClient client1 = new CarClient(9001);	
		
		try{
			Thread.sleep(500);
			}catch(InterruptedException ie){
				System.out.println("problem sleeping it");
			}

		CarClient client2 = new CarClient(9002);
		
		
		
		try{
			Thread.sleep(500);
			}catch(InterruptedException ie){
				System.out.println("problem sleeping it");
			}

		CarClient client3 = new CarClient(9003);
		
		
		
		try{
			Thread.sleep(500);
			}catch(InterruptedException ie){
				System.out.println("problem sleeping it");
			}

		CarClient client4 = new CarClient(9004);
		
		
		try{
			Thread.sleep(500);
			}catch(InterruptedException ie){
				System.out.println("problem sleeping it");
			}
		
		CarClient client5 = new CarClient(9005);
		
		
		try{
			Thread.sleep(500);
			}catch(InterruptedException ie){
				System.out.println("problem sleeping it");
			}
		
		CarClient client6 = new CarClient(9006);
		
		
		try{
			Thread.sleep(500);
			}catch(InterruptedException ie){
				System.out.println("problem sleeping it");
			}
	
		CarClient client7 = new CarClient(9007);
		
		
		try{
			Thread.sleep(500);
			}catch(InterruptedException ie){
				System.out.println("problem sleeping it");
			}
		
		CarClient client8 = new CarClient(9008);
		
		
		try{
			Thread.sleep(500);
			}catch(InterruptedException ie){
				System.out.println("problem sleeping it");
			}
		
CarClient client9 = new CarClient(9009);
		
		
		try{
			Thread.sleep(500);
			}catch(InterruptedException ie){
				System.out.println("problem sleeping it");
			}
		
CarClient client10 = new CarClient(9010);
		
		
		try{
			Thread.sleep(500);
			}catch(InterruptedException ie){
				System.out.println("problem sleeping it");
			}
		
CarClient client11 = new CarClient(9011);
		
		
		try{
			Thread.sleep(500);
			}catch(InterruptedException ie){
				System.out.println("problem sleeping it");
			}
		
CarClient client12 = new CarClient(9012);
		
		
		try{
			Thread.sleep(500);
			}catch(InterruptedException ie){
				System.out.println("problem sleeping it");
			}
		
CarClient client13 = new CarClient(9013);
		
		
		try{
			Thread.sleep(500);
			}catch(InterruptedException ie){
				System.out.println("problem sleeping it");
			}
		
CarClient client14 = new CarClient(9014);
		
		
		try{
			Thread.sleep(500);
			}catch(InterruptedException ie){
				System.out.println("problem sleeping it");
			}
		
CarClient client15 = new CarClient(9015);
		
		
		try{
			Thread.sleep(500);
			}catch(InterruptedException ie){
				System.out.println("problem sleeping it");
			}
		//type is not included and will have to be inserted during send
		//EMW = sender_x_pos, sender_y_pos, direction, ack_car_id, event_id,event_ttl 
		String EWM_msg = client0.registeredX+","+client0.registeredY+","+
							client0.registeredDir+","+",ack_car_id,1,20";
		client0.sendMSG("1", EWM_msg);
	}
}



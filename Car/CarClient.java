import java.io.*;
import java.net.*;
import java.util.*;
import static java.lang.System.out;

public class CarClient{

	private Socket socket = null;
	private ObjectInputStream inputStream = null;
	private ObjectOutputStream outputStream = null;
	private boolean isConnected = false;
	messageListener listener;
	DatagramSocket sock = new DatagramSocket();
	private int port;
	private String strIP = "192.168.173.220"; //<--192.168.173.220";

	public void registerSim(){
		try{
			
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

			System.out.println("SENDING pos/dir REQUEST: Sending to Simulator");
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
		String currentEWM[]={"","","","","","","",""}; //type,xpos,ypos,xdir,ydir,ack_car_id,event_id,event_ttl
		boolean EWMComplete = false;
		
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
					//Capture relevant parts of the message
					currentEWM[0] = arrayMSG[2];//message type
					currentEWM[5] =	arrayMSG[7];//ack_car_id
					currentEWM[6] =	arrayMSG[8];//event_id
					currentEWM[7] =	arrayMSG[9];//event_ttl
					
					//request xpos,ypos,xdir,ydir
					System.out.println("NOW REQUESTING POSITION INFO");
					requestPOSDIR();
					
				}
				
				//if it's a response to requestPOSDIR (or a registration message response)
				//insert xpos,ypos etc values into the EWM message being built
				if(msgType.equals("info")){
					currentEWM[1] = arrayMSG[3]; //x-pos
					currentEWM[2] = arrayMSG[4]; //y-pos
					currentEWM[3] = arrayMSG[5]; //currently direction (should just make this direction and modify Adrian func?)
					currentEWM[4] = arrayMSG[6]; //currently diff in y coordinate
				}
				
				//Check if EWM is finished being built
				for (int i=0; i<currentEWM.length; i++){
					if (currentEWM[i]==""){
						EWMComplete=false;
						break;
					}else
						EWMComplete=true;
				}
				
				//If EWM is built, send to Adrian code? Broadcast?
				if(EWMComplete){
					System.out.println(port+"Ready to broadcast: "+currentEWM[0]+", "+currentEWM[1]+", "
							+currentEWM[2]+", "+currentEWM[3]+", "+currentEWM[4]+", "+currentEWM[5]+", "
							+currentEWM[6]+", "+currentEWM[7]+", ");
					
					EWMComplete=false;
					//clear EWM here?
				}
				
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

	public static void main(String[] args) throws SocketException,IOException{
		CarClient client1 = new CarClient(9000);
		
		CarClient client2 = new CarClient(9001);	
		CarClient client3 = new CarClient(9002);
		CarClient client4 = new CarClient(9003);
		CarClient client5 = new CarClient(9004);
		CarClient client6 = new CarClient(9005);
		CarClient client7 = new CarClient(9006);	
		CarClient client8 = new CarClient(9007);
		CarClient client9 = new CarClient(9008);
		CarClient client10 = new CarClient(9009);

		//type is not included and will have to be inserted during send
		//EMW = sender_x_pos, sender_y_pos, sender_dir_x, sender_dir_y, ack_car_id, event_id,event_ttl 
		String EWM_msg = "500.12,300.41,700.0,900.0,ack_car_id,1,20";
		client1.sendMSG("1", EWM_msg);
	}
}



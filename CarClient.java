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
	private String strIP;

	public void registerSim(int port){
		try{
			DatagramSocket carSocket = new DatagramSocket(port);
			InetAddress simaddr = InetAddress.getByName("192.168.173.1");
			int simport = 50000;

			// This finds the ip address of interface wlan0.
			Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        	for (NetworkInterface netint : Collections.list(nets))
            	if (new String (netint.getDisplayName()).equals("wlan0")){
            		Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
            		/*Set counter, usually display MAC addr first then IP addr.
            	  	We want the IP address (i = 1)*/
            		int i = 0;  
        			for (InetAddress inetAddress : Collections.list(inetAddresses)) {	
            			//out.printf("InetAddress: %s\n", inetAddress);
            			if (i == 1){ 
            				//This is the IP adress
            				InetAddress caraddr = inetAddress;
            				this.strIP = (caraddr.toString()).substring(1);
            				//System.out.println("wlan0 ip is or strIP is " + strIP);
            		}
            		i++;
        		}
            }


		//InetAddress caraddr = InetAddress.getLocalHost();


		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];
		/*
		StringBuilder request = new StringBuilder();
		request.append(ipaddr);
		request.append(":");
		request.append(port);
		*/

		//String strIP = caraddr.getHostAddress();
		String strPORT = Integer.toString(port);

		//Simulator Addr: 192.168.173.1 Port: 50000
		String reqMSG = "192.168.173.1:50000,"+ strIP + ":" + strPORT;
		//String reqMSG = "000";
		sendData = reqMSG.getBytes();

		System.out.println("REGISTERSIM: Sending to Simulator");
		DatagramPacket sendRequest = new DatagramPacket(sendData, sendData.length, simaddr, simport);
		carSocket.send(sendRequest);

		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		carSocket.receive(receivePacket);
		String fromSim = new String(receivePacket.getData());
		System.out.println("FROM SIMULATOR:" + fromSim);
		System.out.println("---------------------------------------------");

		carSocket.close();
		}catch(IOException e1){
			System.out.println("UnknownHostError?");
			e1.printStackTrace();
		}
	}
	public void sendMSG(String msgType,String message){
		try{
			DatagramSocket carSocket = new DatagramSocket();
			InetAddress simaddr = InetAddress.getByName("192.168.173.1");
			int simport = 50000;

			byte[] receiveData = new byte[1024];
			byte[] sendData = new byte[1024];
			String strPORT = Integer.toString(port);

			//Simulator Addr: 192.168.173.1 Port: 50000
			//message should be of form (type,paramaters)
			String reqMSG = "255.255.255.0:7000,"+ strIP + ":" + strPORT + "," + msgType + "," + message;
			sendData = reqMSG.getBytes();

			System.out.println("Sending to Simulator");
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
		DatagramPacket pack;
		byte[] buf = new byte[256];
		public messageListener() throws IOException
		{
			super("MESSAGELISTENER: STARTING THE THREAD");
			sock = new DatagramSocket(port);
		}
		public void run()
		{
			//car.hello();
			System.out.println("INSIDE THREAD.RUN: Start listening");
			while(true)
			{
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
				String msgData = arrayMSG[3];
				/* Type 1 = EWM
				   Type 2 = ACK*/
				
				//if (msgType.equal("1")){
				//	reactEWM(arrayMSG[2],arrayMSG[3])	
				}
			}
		
	}
	
	public CarClient(int portNo) throws IOException{
		this.port = portNo;
		this.registerSim(this.port);
		
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

		client1.sendMSG("1","hi");
	}
}

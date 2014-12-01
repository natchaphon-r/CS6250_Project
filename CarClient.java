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

	public void registerSim(int port){
		try{
			//set up listening stuff
			listener = new messageListener();
			listener.start();
			
			this.port = port;
			DatagramSocket carSocket = new DatagramSocket(port);
			InetAddress simaddr = InetAddress.getByName("192.168.173.1");
			int simport = 50000;
			String strIP = "";

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
            				strIP = (caraddr.toString()).substring(1);
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

		System.out.println("Sending to Simulator");
		DatagramPacket sendRequest = new DatagramPacket(sendData, sendData.length, simaddr, simport);
		carSocket.send(sendRequest);

		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		carSocket.receive(receivePacket);
		String fromSim = new String(receivePacket.getData());
		System.out.println("FROM SIMULATOR:" + fromSim);

		carSocket.close();
		}catch(IOException e1){
			System.out.println("UnknownHostError?");
			e1.printStackTrace();
		}

	}
	
	public void sendMSG(String message){
		try{
			DatagramSocket carSocket = new DatagramSocket();
			InetAddress simaddr = InetAddress.getByName("192.168.173.1");
			int simport = 50000;
			String strIP = "192.168.173.220";

		

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
		//message should be of form (type,paramaters)
		String reqMSG = "255.255.255.0:7000,"+ strIP + ":" + strPORT + "," + message;
		//String reqMSG = "000";
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
			super("message listener");
			sock = new DatagramSocket(port);
		}

		public void run()
		{
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
				System.out.println("Got message:" + msg) ;
			}

		}
	}
	public CarClient(int portNo) throws IOException{
		this.port = portNo;
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
		
		//send a message from each car to sim
		client1.sendMSG("1,register1");
		client2.sendMSG("1,register2");
		client3.sendMSG("1,register3");
		client4.sendMSG("1,register4");
		client5.sendMSG("1,register5");
		client6.sendMSG("1,register6");
		
		try{
		Thread.sleep(20);
		}catch(InterruptedException ie){
			System.out.println("sleep messed up in main");
		}
		
		//this should broadcast collision ahead to any node within range of client 3
		client3.sendMSG("1,collisionahead");
		try{
			Thread.sleep(20);
			}catch(InterruptedException ie){
				System.out.println("sleep messed up in main");
			}
		
		//brodcast an ACK to any node in range of client 2
		client2.sendMSG("2,this is an ACK");
		
		/*
		Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets))
            displayInterfaceInformation(netint);*/
	}
}

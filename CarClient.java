import java.io.*;
import java.net.*;
import java.util.*;
import static java.lang.System.out;


public class CarClient{

	private Socket socket = null;
	private ObjectInputStream inputStream = null;
	private ObjectOutputStream outputStream = null;
	private boolean isConnected = false;

	public void registerSim(int port){
		try{
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

	static void displayInterfaceInformation(NetworkInterface netint) throws SocketException {
        out.printf("Display name: %s\n", netint.getDisplayName());
        out.printf("Name: %s\n", netint.getName());
        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
            out.printf("InetAddress: %s\n", inetAddress);
        }
        out.printf("\n");
     }

	public static void main(String[] args) throws SocketException{
		CarClient client = new CarClient();
		client.registerSim(9000);
		/*
		Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets))
            displayInterfaceInformation(netint);*/
	}
}

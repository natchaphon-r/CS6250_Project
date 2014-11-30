import java.io.*;
import java.net.*;


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

		NetworkInterface ni = NetworkInterface.getByName("wlan0");
		Enumeration<InetAddress> inetAddresses =  ni.getInetAddresses();

		while(inetAddresses.hasMoreElements()) {
            InetAddress ia = inetAddresses.nextElement();
            if(!ia.isLinkLocalAddress()) {
                System.out.println("IP: " + ia.getHostAddress());
            }
        }

		InetAddress caraddr = InetAddress.getLocalHost();


		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];
		/*
		StringBuilder request = new StringBuilder();
		request.append(ipaddr);
		request.append(":");
		request.append(port);
		*/

		String strIP = caraddr.getHostAddress();
		String strPORT = Integer.toString(port);

		//Simulator Addr: 192.168.173.1 Port: 50000
		String reqMSG = "192.168.173.1:50000,"+ strIP + ":" + strPORT;
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
	public static void main(String[] args){
		CarClient client = new CarClient();
		client.registerSim(9000);
	}
}

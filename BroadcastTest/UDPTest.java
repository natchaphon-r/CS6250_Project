import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;


public class UDPTest {

	private class messageListener extends Thread{
		
		DatagramSocket sock;
		DatagramPacket pack;
		byte[] buf = new byte[256];
		
		public messageListener(String nic) throws IOException
		{
			super("multicast listener");
			sock = new DatagramSocket(50000);
//			if(!nic.equals(""))
//			{
//				sock.setInterface(InetAddress.getByName(nic));
//			}
//			InetAddress group = InetAddress.getByName("239.14.14.1");
//			sock.joinGroup(group);
		}
		
		public void run()
		{
			while(true)
			{
				pack = new DatagramPacket(buf, buf.length);
				try {
					sock.receive(pack);
				} catch (IOException e1) {
					System.out.println("what the hell just happened..");
					e1.printStackTrace();
				}
				String msg = new String(pack.getData());
				System.out.println("got spam from " + pack.getAddress());
			}
			
		}
	}
	
	private class messageSpammer extends Thread{
		
		DatagramSocket sock;
		DatagramPacket pack;
		byte[] buf = new byte[256];
		String destination;
		
		
		public messageSpammer(String d) throws SocketException, UnknownHostException
		{
			super("multicast spammer");
			sock = new DatagramSocket();	
			destination = d;
		}
		
		public void run()
		{
			while(true)
			{
				try {
				buf = "lawl".getBytes();
				InetAddress dest = InetAddress.getByName(destination);
				pack = new DatagramPacket(buf, buf.length, dest, 50000);
				sock.send(pack);
				System.out.println("spam sent forth...");
				Thread.sleep(500);
				} catch (Exception e) {
					System.out.println("what the hell just happened..");
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		
		UDPTest test = new UDPTest();
		messageListener ml;
		messageSpammer ms;

		//ml = test.new messageListener(args[0]);
		ms = test.new messageSpammer(args[0]);

		//ml.start();
		ms.start();

	}

}

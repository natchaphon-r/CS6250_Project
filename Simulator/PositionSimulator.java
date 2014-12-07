import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class PositionSimulator {
	
	
//		public void run()
//		{
//			while(true)
//			{
//				try {
//				buf = "lawl".getBytes();
//				
//				pack = new DatagramPacket(buf, buf.length, group, 50000);
//				sock.send(pack);
//				System.out.println("spam sent forth...");
//				Thread.sleep(500);
//				} catch (Exception e) {
//					System.out.println("what the hell just happened..");
//					e.printStackTrace();
//				}
//			}
//		}
	
	
	public static void main(String[] args) throws IOException, InterruptedException {

		World world = new World();
		Display display = new Display(world);
		
//		DatagramSocket sock = new DatagramSocket(50000);
//		InetAddress group = InetAddress.getByName("239.0.0.1");
//		DatagramPacket pack;
//		byte[] buf = new byte[256];
		
		while(true)
		{
			world.nodeSemaphore.acquire();
			world.update(20.0/1000);
			display.repaint();
			world.nodeSemaphore.release();
			Thread.sleep(20);
		}
		
	}

}

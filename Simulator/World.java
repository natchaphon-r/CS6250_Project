import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Semaphore;


public class World {

	messageListener listener; 
	Random rand = new Random();
	
	HashMap<String,Node> nodes = new HashMap<String,Node>();
	Semaphore nodeSemaphore = new Semaphore(1, true);
	boolean constrainToHighway = false;
	double broadcastRange = 300;
	double speed = 100;
	double boundx = 800;
	double boundy = 800;
	int msgDuration = 1000;
	String simulatorAddress = "192.168.173.1:50000";
	DatagramSocket sock = new DatagramSocket();
	
	private class messageListener extends Thread{

		DatagramSocket sock;
		
		public messageListener() throws IOException
		{
			super("message listener");
			sock = new DatagramSocket(50000);
//			InetAddress group = InetAddress.getByName("239.14.14.1");
//			sock.joinGroup(group);
		}

		public void run()
		{
			while(true)
			{
				byte[] buf = new byte[256];
				DatagramPacket pack;
				
				pack = new DatagramPacket(buf, buf.length);
				
				try {
					sock.receive(pack);
				} catch (IOException e1) {
					System.out.println("what the hell just happened..");
					e1.printStackTrace();
				}
				
				String msg = (new String(pack.getData())).trim();
				String[] tokens = msg.split(",");
				System.out.println("Got " + msg + " from " + tokens[1]);
				
				try {
					nodeSemaphore.acquire();
				} catch (InterruptedException e) {
					System.out.println("semaphore bullshit in message listener thread");
					e.printStackTrace();
				}
				
				if(!nodes.containsKey(tokens[1]))
				{
					double x;
					double y;
					double dx;
					double dy;
					
					if(constrainToHighway)
	        		{
	        			x = rand.nextDouble()*boundx;
	        			dx = rand.nextDouble()*boundx;
	    				y = boundy/2;
	    				dy = boundy/2;
	        		}
	        		else
	        		{
	        			x = rand.nextDouble()*boundx;
	        			dx = rand.nextDouble()*boundx;
	            		y = rand.nextDouble()*boundy;
	            		dy = rand.nextDouble()*boundy;
	        		} 
					nodes.put(tokens[1], new Node(tokens[1], x, y, dx, dy));
				}	
				processMsg(msg);
				nodeSemaphore.release();
			}

		}
	}
	
	public World() throws IOException
	{
		listener = new messageListener();
		listener.start();
	}
	
	public void processMsg(String msg)
	{
		String[] tokens = msg.split(",");
		Node source = nodes.get(tokens[1]);
		
		if(!tokens[0].equals(simulatorAddress))
		{	
			for(Node n : nodes.values())
			{
				if(n != source && (n.x-source.x)*(n.x-source.x) + (n.y-source.y)*(n.y-source.y) <= broadcastRange*broadcastRange)
				{
					source.addMsg(n);
					sendMsg(msg, n.name);
				}
			}
		}
		else
		{
			String posInfo = tokens[1] + "," + simulatorAddress + "," + "info" + 
		"," + source.x + "," + source.y + 
		"," + (source.destx - source.x) + "," + (source.desty - source.y) + 
		"," + speed + "," + broadcastRange;
			sendMsg(posInfo, tokens[1]);
		}
	}
	
	public void sendMsg(String msg, String destination)
	{
		try {
			byte[] buf = msg.getBytes();
			String[] addr = destination.split(":");
			InetAddress dest = InetAddress.getByName(addr[0]);
			int port = Integer.parseInt(addr[1]);
			DatagramPacket pack = new DatagramPacket(buf, buf.length, dest, port);
			sock.send(pack);
			System.out.println("Sent " + msg + " to " + destination);
		} 
		catch (Exception e) {
			System.out.println("Sending msg failed.");
			e.printStackTrace();
		}
		
	}
	
	public void update(double dt)
	{
		for(Node node : nodes.values())
        {
			double dist = Math.sqrt((node.x-node.destx)*(node.x-node.destx) + (node.y-node.desty)*(node.y-node.desty));
        	if(speed * dt > dist)
        	{
        		if(constrainToHighway)
        		{
        			node.destx = rand.nextDouble()*boundx;
    				node.desty = boundy/2;
        		}
        		else
        		{
        			node.destx = rand.nextDouble()*boundx;
            		node.desty = rand.nextDouble()*boundy;
        		} 		
        	}
        	else
        	{
        		node.x = node.x + speed * dt * (node.destx-node.x)/dist;
        		node.y = node.y + speed * dt * (node.desty-node.y)/dist;
        	}
        	
        	node.updateMsg(msgDuration);
        			
        }
	}
	
	public void constrainToHighway(boolean b)
	{
		constrainToHighway = b;
		
		if(constrainToHighway)
		{
			for(Node node : nodes.values())
			{
				node.destx = rand.nextDouble()*boundx;
				node.desty = boundy/2;
			}
		}
	}

}

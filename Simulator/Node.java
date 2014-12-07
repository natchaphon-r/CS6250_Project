import java.util.ArrayList;


public class Node {

	String name;
	double x;
	double y;
	double destx;
	double desty;
	
	ArrayList<Node> receivers = new ArrayList<Node>();
	ArrayList<Long> times = new ArrayList<Long>();
	
	public Node(String n, double ix, double iy, double idx, double idy)
	{
		name = n;
		x = ix;
		y = iy;
		destx = idx;
		desty = idy;
	}
	
	public void updateMsg(int duration)
	{
		if(!times.isEmpty() && System.currentTimeMillis() - times.get(0) > duration)
		{
			times.remove(0);
			receivers.remove(0);
		}
	}
	
	public void addMsg(Node receiver)
	{
		receivers.add(receiver);
		times.add(System.currentTimeMillis());
	}
}

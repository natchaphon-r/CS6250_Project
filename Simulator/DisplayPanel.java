import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;


public class DisplayPanel extends JPanel{
	
	private World world;
	
	public DisplayPanel(World w)
	{
		world = w;
		this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                w.boundx = ((JPanel)e.getSource()).getWidth();
                w.boundy = ((JPanel)e.getSource()).getHeight();
            }
        });
	}
	
	public void paintComponent(Graphics gr) 
	{
		super.paintComponent(gr); 
		try {
			world.nodeSemaphore.acquire();
		} catch (InterruptedException e) {
			System.out.println("semaphore bullshit in repaint");
			e.printStackTrace();
		}
		
		Graphics2D g = (Graphics2D) gr;
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 7 * 0.1f));

		g.setColor(new Color(0,40,100));
		for(Node node : world.nodes.values())
		{
			g.fillOval((int)(node.x-world.broadcastRange), 
					(int)(node.y-world.broadcastRange), 
					(int)(world.broadcastRange*2), 
					(int)(world.broadcastRange*2));
		}
		
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 10 * 0.1f));


		g.setColor(new Color(100,100,40));
		for(Node node : world.nodes.values())
		{
			g.fillOval((int)(node.x-10), (int)(node.y-10), 20, 20);
			g.drawString(node.name, (int)(node.x)+15, (int)(node.y));
		}

		g.setColor(new Color(40,240,40));
		for(Node node : world.nodes.values())
		{
			for(Node n : node.receivers)
			{
				drawArrow(g, (int)node.x, (int)node.y, (int)n.x, (int)n.y);
			}
		}
        
		world.nodeSemaphore.release();
        //drawArrow(g, 400, 200, 100, 700);
        //drawArrow(g, 200, 100, 400, 300);
        
    }
	
	private void drawArrow(Graphics g, int x1, int y1, int x2, int y2)
	{
		g.drawLine(x1, y1, x2, y2);
		double d = Math.sqrt(1.0*(x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
		double x3 = x2 + 10 * (x1-x2)*(Math.cos(15*Math.PI/180)/d);
		double y3 = y2 + 10 * (y1-y2)*(Math.cos(15*Math.PI/180)/d);
		double x4 = x3 + 10 * Math.sin(15*Math.PI/180)*(y1-y2)/d;
		double y4 = y3 - 10 * Math.sin(15*Math.PI/180)*(x1-x2)/d;
		double x5 = x3 - 10 * Math.sin(15*Math.PI/180)*(y1-y2)/d;
		double y5 = y3 + 10 * Math.sin(15*Math.PI/180)*(x1-x2)/d;
		g.drawLine(x2, y2, (int)x4, (int)y4);
		g.drawLine(x2, y2, (int)x5, (int)y5);
	}
}

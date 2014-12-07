import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class Display extends JFrame{
	
	private DisplayPanel dispPanel;
	private JPanel controlPanel;
	private World world;
	
	public Display(World w)
	{
		super("Position Simulator Display");
		world = w;
		
		controlPanel = new JPanel();
		controlPanel.setPreferredSize(new Dimension(200,800));
		controlPanel.setBackground(new Color(120,120,120));
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		
		JSlider rangeSlider = new JSlider(JSlider.HORIZONTAL, 0, 800, 300);
		rangeSlider.setBackground(new Color(120,120,120));
		rangeSlider.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent arg0) {
				JSlider source = (JSlider)arg0.getSource();
				world.broadcastRange = source.getValue();
			}});
		
		JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, 0, 800, 100);
		speedSlider.setBackground(new Color(120,120,120));
		speedSlider.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent arg0) {
				JSlider source = (JSlider)arg0.getSource();
				world.speed = source.getValue();
			}});
		
		JSlider durationSlider = new JSlider(JSlider.HORIZONTAL, 0, 30000, 1000);
		durationSlider.setBackground(new Color(120,120,120));
		durationSlider.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent arg0) {
				JSlider source = (JSlider)arg0.getSource();
				world.msgDuration = source.getValue();
			}});
		
		JCheckBox constrainButton = new JCheckBox("Constrain to highway");
		constrainButton.setSelected(false);
		constrainButton.setBackground(controlPanel.getBackground());
		constrainButton.addItemListener(new ItemListener(){

			public void itemStateChanged(ItemEvent arg0) {
				JCheckBox source = (JCheckBox)arg0.getSource();
				world.constrainToHighway(source.isSelected());
			}
			
		});
		
		controlPanel.add(new JLabel("Broadcast Range"));
		controlPanel.add(rangeSlider);
		controlPanel.add(new JLabel("Speed"));
		controlPanel.add(speedSlider);
		controlPanel.add(new JLabel("Message Arrow Duration"));
		controlPanel.add(durationSlider);
		controlPanel.add(constrainButton);
			
		dispPanel = new DisplayPanel(world);
		dispPanel.setPreferredSize(new Dimension(800,800));
		dispPanel.setMinimumSize(new Dimension(800,800));
		dispPanel.setBackground(new Color(0,20,50));
		
		this.add(controlPanel, BorderLayout.WEST);
		this.add(dispPanel, BorderLayout.CENTER);
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.pack();
		this.setMinimumSize(getSize());
		this.setVisible(true);
		
	}
}

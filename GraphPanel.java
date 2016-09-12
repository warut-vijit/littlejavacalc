package main;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

public class GraphPanel extends JPanel {
	double graph_width,graph_height;
	ArrayList<Double[]> graph_points;
	public GraphPanel(){
		super();
		graph_width = 10.0;
		graph_height = 10.0;
		graph_points = new ArrayList<Double[]>();
	}
	public void paint(Graphics g){
		int height = this.getHeight();
		int width = this.getWidth();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.BLACK);
		g.setFont(g.getFont().deriveFont(10.0f));
		g.drawLine(0, height/2, width, height/2);
		for(int i=0;i<22;i++){
			g.drawLine(width*i/21+width/42, height/2-2, width*i/21+width/42, height/2+2);
		}
		g.drawString(-1*graph_width/2+"", 0, height/2-2);
		g.drawString(1*graph_width/2+"", width*20/21, height/2-2);
		g.drawLine(width/2, 0, width/2, height);
		for(int i=0;i<22;i++){
			g.drawLine(width/2-2,height*i/21+height/42, width/2+2, height*i/21+height/42);
		}
		g.setColor(Color.BLUE);
		for(int i=0;i<graph_points.size()-1;i++){
			int x1 = (int)(graph_points.get(i)[0]/graph_width*width+width/2);
			int y1 = (int)(-1*graph_points.get(i)[1]/graph_height*height+height/2);
			int x2 = (int)(graph_points.get(i+1)[0]/graph_width*width+width/2);
			int y2 = (int)(-1*graph_points.get(i+1)[1]/graph_height*height+height/2);
			g.drawLine(x1,y1,x2,y2);
		}
	}
}

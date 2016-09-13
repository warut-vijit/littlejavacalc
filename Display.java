package main;

import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Display extends JFrame {
	TreeMap<Character,Double> variables = new TreeMap<Character,Double>();
	Commands commands;
	GraphPanel graph;
	JDialog graphcontainer;
	public static void main(String[] args) {
		Display s= new Display();
	}
	public Display(){
		graphcontainer = new JDialog();
		graph = new GraphPanel();
		graph.setSize(400,400);
		graphcontainer.add(graph);
		graphcontainer.setSize(400,400);
		graphcontainer.setVisible(true);
		graphcontainer.setLocation(600,100);
		commands = new Commands(this);
		for(char c='a';c<='z';c++) variables.put(c, 0.0);
		for(char c='A';c<='Z';c++) variables.put(c, 0.0);
		JPanel jp = new JPanel();
		jp.setLayout(new BoxLayout(jp,BoxLayout.Y_AXIS));
			JTextArea jta = new JTextArea(30,30);
			jta.setFocusable(false);
			jp.add(jta);
			JTextField jtf = new JTextField();
			jtf.setFocusable(true);
			jtf.requestFocus();
			jtf.addKeyListener(new KeyListener(){
				public void keyPressed(KeyEvent arg0) {
					if(arg0.getKeyCode()==10){//enter
						jta.setText(jta.getText()+"\n>>"+jtf.getText());
						jta.setText(jta.getText()+"\n            "+processInput(jtf.getText()));
						jtf.setText("");
					}
				}
				public void keyReleased(KeyEvent arg0) {}
				public void keyTyped(KeyEvent arg0) {}
			});
			jp.add(jtf);
		add(jp);
		setVisible(true);
		setSize(500,500);
		setLocation(100,100);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(new WindowListener(){
			public void windowActivated(WindowEvent arg0) {}
			public void windowClosed(WindowEvent arg0) {}
			public void windowClosing(WindowEvent arg0) {System.out.println("Window is closing");}
			public void windowDeactivated(WindowEvent arg0) {}
			public void windowDeiconified(WindowEvent arg0) {}
			public void windowIconified(WindowEvent arg0) {}
			public void windowOpened(WindowEvent arg0) {}
		});
	}
	private String processInput(String s){
		char[] chars = s.toCharArray();
		String commandoutput = commands.check(s);
		if(commandoutput!=null) return commandoutput; //A command was detected and executed
		ArrayList<Object> elements = new ArrayList<Object>();
		for(int i=0;i<chars.length;i++){elements.add(chars[i]);}
		ArrayList<Object> parsed = parse(elements);
		double result = operate(parsed);
		return result+"";
	}
	public ArrayList<Object> parse(ArrayList<Object> elements){
		ArrayList<Object> temp = new ArrayList<Object>();
		int i=0;
		while(i<elements.size()){
			if(Character.isDigit((char) elements.get(i))){
				if(i>0 && temp.get(temp.size()-1) instanceof Double){//previous is already a number
					temp.set(temp.size()-1, (double)temp.get(temp.size()-1)*10+ Character.getNumericValue((char)elements.get(i)));
				}
				else{//Previous was a symbol
					temp.add((double)Character.getNumericValue((char)elements.get(i)));
				}
			}//Digits are added to the value
			else if((char) elements.get(i) == 'x'){temp.add('x');}
			else if((char) elements.get(i) == '('){//Dealing with parentheses
				i++;//Skip the original parentheses
				ArrayList<Object> temp2 = new ArrayList<Object>();
				int nested_value = 0;
				while((char) elements.get(i) != ')' || nested_value!=0){//while parentheses are still open
					if((char) elements.get(i)=='(') nested_value++;
					if((char) elements.get(i)==')') nested_value--;
					temp2.add((char) elements.get(i));
					i++;
				}
				temp.add(operate(parse(temp2)));
			}
			else {//operators
				temp.add(elements.get(i));
			}
			i++;
		}
		for(int j=0;j<temp.size()-1;j++){
			if(temp.get(j) instanceof Character && (char)temp.get(j)=='-'){
				if(j==0 || !(temp.get(j-1) instanceof Double)){
					temp.set(j+1, (double)temp.get(j+1) * -1);
					temp.remove(j);
				}
			}
			if(temp.get(j) instanceof Double && temp.get(j+1) instanceof Double){
				temp.add(j+1, '*');
			}
		}
			//Parentheses are eliminated. Proceed with order of operations
		return temp;
	}
	public double operate(ArrayList<Object> elements){
		ArrayList<Object> temp = new ArrayList<Object>();
		if(elements.size()==1){
			try{return (double) elements.get(0);}catch(ClassCastException cce){return variables.get(elements.get(0));}
		}//Only one number left (result)
		char highest_op = elements.contains('^') ? '^' : ((elements.contains('*')||elements.contains('/')||elements.contains('%')) ? '*' : '+');
		int i=0;
		while(i<elements.size()){
			if(elements.get(i) instanceof Double || elements.get(i) instanceof Integer){
				temp.add(elements.get(i));
			}
			else if((char)elements.get(i)==highest_op || ( ((char)elements.get(i)=='/'||(char)elements.get(i)=='%') && highest_op=='*') || ((char)elements.get(i)=='-' && highest_op=='+')){
				if((char)elements.get(i)=='^'){
					double a;
					double b;
					try{a=(double) temp.get(temp.size()-1);}catch(ClassCastException cce){a=variables.get(temp.get(temp.size()-1));}
					try{b=(double) elements.get(i+1);}catch(ClassCastException cce){b=variables.get(elements.get(i+1));}
					temp.remove(temp.size()-1);//Bumps the last object off
					temp.add(Math.pow(a, b));
				}
				else if((char)elements.get(i)=='*'){
					double a;
					double b;
					try{a=(double) temp.get(temp.size()-1);}catch(ClassCastException cce){a=variables.get(temp.get(temp.size()-1));}
					try{b=(double) elements.get(i+1);}catch(ClassCastException cce){b=variables.get(elements.get(i+1));}
					temp.remove(temp.size()-1);//Bumps the last object off
					temp.add(a*b);
				}
				else if((char)elements.get(i)=='/'){
					double a;
					double b;
					try{a=(double) temp.get(temp.size()-1);}catch(ClassCastException cce){a=variables.get(temp.get(temp.size()-1));}
					try{b=(double) elements.get(i+1);}catch(ClassCastException cce){b=variables.get(elements.get(i+1));}
					temp.remove(temp.size()-1);//Bumps the last object off
					if(b==0){System.out.println("Error: Division by 0");return 0;}
					else{temp.add(a/b);}
				}
				else if((char)elements.get(i)=='%'){
					double a;
					double b;
					try{a=(double) temp.get(temp.size()-1);}catch(ClassCastException cce){a=variables.get(temp.get(temp.size()-1));}
					try{b=(double) elements.get(i+1);}catch(ClassCastException cce){b=variables.get(elements.get(i+1));}
					temp.remove(temp.size()-1);//Bumps the last object off
					temp.add(a%b);
				}
				else if((char)elements.get(i)=='+'){
					double a;
					double b;
					try{a=(double) temp.get(temp.size()-1);}catch(ClassCastException cce){a=variables.get(temp.get(temp.size()-1));}
					try{b=(double) elements.get(i+1);}catch(ClassCastException cce){b=variables.get(elements.get(i+1));}
					temp.remove(temp.size()-1);//Bumps the last object off
					temp.add(a+b);
				}
				else if((char)elements.get(i)=='-'){
					double a;
					double b;
					try{a=(double) temp.get(temp.size()-1);}catch(ClassCastException cce){a=variables.get(temp.get(temp.size()-1));}
					try{b=(double) elements.get(i+1);}catch(ClassCastException cce){b=variables.get(elements.get(i+1));}
					temp.remove(temp.size()-1);//Bumps the last object off
					temp.add(a-b);
				}
				i++;
			}else{
				temp.add(elements.get(i));
			}
			i++;
		}
		//Check if syntax error
		boolean dupe = true;
		for(int j=0;j<temp.size();j++){
			if(temp.get(j)!=elements.get(j)){dupe = false;}
		}
		if(dupe){System.out.println("Unknown Syntax Error.");return 0;}//If no operations occurred, syntax error has
		return operate(temp);
	}
}

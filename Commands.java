package main;

import java.util.ArrayList;
import java.util.Map;

public class Commands {
	Display module;
	public Commands(Display d){
		module = d;
	}
	public String check(String s){
		String command = "";
		String[] argslist;
		s = s.replaceAll("\\s", "");
		if(s.indexOf(':')==-1){command = s;argslist = new String[0];}
		else{
			int acc = 0;
			while(acc<s.length() && s.charAt(acc)!=':'){
				command = command+s.charAt(acc);
				acc++;
			}
			String args = s.substring(acc+1);
			argslist = args.split(",");
		}
		switch( command ) {
			case "reset":
				for(Map.Entry<Character, Double> entry : module.variables.entrySet()) entry.setValue(0.0);
				return "Done";
			case "setWidth":
				if(argslist.length!=1){return "Syntax Error: One argument expected";}
				try{
					double i = Double.parseDouble(argslist[0]);
					module.graph.graph_width = i;
					module.graph.repaint();
					return "Done";
				}catch(NumberFormatException nfe){return "Syntax Error: Number expected";}
			case "setHeight":
				if(argslist.length!=1){return "Syntax Error: One argument expected";}
				try{
					double i = Double.parseDouble(argslist[0]);
					module.graph.graph_height = i;
					module.graph.repaint();
					return "Done";
				}catch(NumberFormatException nfe){return "Syntax Error: Number expected";}
			case "showGraph":
				if(argslist.length>0 && !argslist[0].equals("")){return "Syntax Error: No arguments expected";}
				module.graphcontainer.setVisible(true);
				module.graph.repaint();
				return "Done";
			case "hideGraph":
				if(argslist.length>0 && !argslist[0].equals("")){return "Syntax Error: No arguments expected";}
				module.graphcontainer.setVisible(false);
				return "Done";
			case "set":
				if(argslist.length!=2){return "Syntax Error: Two arguments expected";}
				char variable = argslist[0].charAt(0);
				if(!Character.isAlphabetic(variable)){return "Syntax Error: Variable undefined";}
				try{
					char[] chars = argslist[1].toCharArray();
					ArrayList<Object> elements = new ArrayList<Object>();
					for(int i=0;i<chars.length;i++){elements.add(chars[i]);}
					module.variables.put(variable,module.operate(module.parse(elements)));
				}catch(NumberFormatException nfe){return "Syntax Error: Invalid value";}
				return "Done";
			case "graph":
				if(argslist.length!=1){return "Syntax Error: One argument expected";}
				double temp = module.variables.get('x');
				double iterator = -1*module.graph.graph_width;
				char[] chars = argslist[0].toCharArray();
				ArrayList<Object> elements = new ArrayList<Object>();
				for(int i=0;i<chars.length;i++){elements.add(chars[i]);}
				module.graph.graph_points.clear();
				while(iterator<module.graph.graph_width){
					module.variables.put('x', iterator);
					ArrayList<Object> parsed = module.parse(elements);
					module.graph.graph_points.add(new Double[]{iterator,module.operate(parsed)});
					iterator+=0.01;
				}
				module.variables.put('x',temp);
				module.graph.repaint();
				return "Done";
			default:
				return null;
		}
	}
}

package graphgenerator;

import java.io.IOException;
import graphViz.HandleVizStype;
import graphViz.HandleVizStype.stylesheet;

import org.graphstream.graph.Graph;

import graphTool.ReadDGS;

public class TreeGraph {

	String id ;
	Graph input ;
	Graph g  ;

	public TreeGraph ( String id , Graph input ) {
		this.id = id ;
		this.input = input;
	}
	
	public void compute () {
		g = GeneratorTools.GetMSTGraph( id , input ); 	
	}
	
	public Graph getGraph ( ) { return g ; }
	
	public void storeDGS ( String path ) throws IOException {
		g.write(path  + g.getId()+".dgs");
	}
	
	public void storeImage (String path ) {
		g.display(false);
		graphViz.HandleVizStype netViz = new HandleVizStype(g, stylesheet.manual, "seed", 1);
		netViz.setupIdViz(false, g, 20, "black");
		netViz.setupDefaultParam(g, "black", "black", 3, 0.5);
		netViz.setupVizBooleanAtr(false, g, "black", "red", false, false);// netViz.setupFixScaleManual( true ,  net, sizeGridX , 0);
//		netViz.createSquare(true, g, sizeGrid[0], 0);
		g.addAttribute("ui.screenshot", path + "/" + g.getId() + ".png");
	}
	
	public static void main(String[] args) throws IOException {
		String path = "/home/mtirico/results/generators/dgs/tree.dgs";
		Graph input = new ReadDGS(path).getGraph();
		TreeGraph tg = new TreeGraph("tree" , input) ;
		tg.compute();
				
		Graph tree = tg.getGraph();

		tree.display(true);
	}

}

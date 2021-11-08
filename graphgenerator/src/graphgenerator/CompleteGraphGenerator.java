package graphgenerator;

import java.io.IOException;
import java.util.ArrayList;

import org.graphstream.algorithm.Prim;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.graph.implementations.SingleGraph;

import generic_tools.Tools;
import graphViz.HandleVizStype;
import statistical_and_math_tools.BinominalProbGenerator;

public class CompleteGraphGenerator {

	private int numNodes ; 
	private int[] sizeGrid ;
	private String id ;
	private Graph g ;
	private int idNodeInt = 0 , idEdgeInt = 0 ;
	private ArrayList<Node> listNodes = new ArrayList<Node>  ( );
	private BinominalProbGenerator bpg;

	public CompleteGraphGenerator ( String id , int seed , int numNodes , int[] sizeGrid) {
		this.id = id ;
		this.numNodes = numNodes ;
		this.sizeGrid = sizeGrid ;	
		g = new SingleGraph(id);
		bpg = new BinominalProbGenerator(seed);
		for ( double[] coords :  Tools.getListRandomCoords( seed, numNodes, sizeGrid, 0 ) )  {
			Node n = g.addNode(Integer.toString(idNodeInt++)) ;
			n.addAttribute("xy", coords[0], coords[1]);
			listNodes.add(n);
		}
	}
	
	public CompleteGraphGenerator ( String id , int seed , int numNodes , int[] sizeGrid,boolean run) {
		this.id = id ;
		this.numNodes = numNodes ;
		this.sizeGrid = sizeGrid ;	
		g = new SingleGraph(id);
		bpg = new BinominalProbGenerator(seed);
		for ( double[] coords :  Tools.getListRandomCoords( seed, numNodes, sizeGrid, 0 ) )  {
			Node n = g.addNode(Integer.toString(idNodeInt++)) ;
			n.addAttribute("xy", coords[0], coords[1]);
			listNodes.add(n);
		}
		if (run)
			compute();
	}
	
	public void compute () {
		for (Node n0 : g.getEachNode())
			for (Node n1 : g.getEachNode())
				if (!n0.equals(n1))
					try { 
						Edge ed = g.addEdge(Integer.toString(idEdgeInt++), n0, n1);
//						ed.addAttribute("weight", GraphTool.getDistGeom(n0, n1));
					}
					catch (Exception e) { 	}
	}

	public Graph getGraph ( ) { return g ; }
	
	public void storeDGS ( String path ) throws IOException {
		g.write(path  + g.getId()+".dgs");
	}
	
	public void storeImage (String path ) {
		g.display(false);
		HandleVizStype netViz = new HandleVizStype(g,  HandleVizStype.stylesheet.manual, "seed", 1);
		netViz.setupIdViz(false, g, 20, "black");
		netViz.setupDefaultParam(g, "black", "black", 3, 0.5);
		netViz.setupVizBooleanAtr(false, g, "black", "red", false, false);// netViz.setupFixScaleManual( true ,  net, sizeGridX , 0);
		netViz.createSquare(true, g, sizeGrid[0], 0);
		g.addAttribute("ui.screenshot", path + "/" + g.getId() + ".png");
	}
	
	
	public static void main(String[] args) throws IOException {
		CompleteGraphGenerator cgg = new CompleteGraphGenerator("random", 0, 10, new int [] {10,10});
		cgg.compute();
		Graph graph = cgg.getGraph();
//		graph.display(false);
//		for (Node n : gr
//		aph.getEachNode())
			
//		TreeGraph tg = new TreeGraph("t", aa );
//		tg.compute();
//		tg.getGraph().display(true);
		//		cgg.storeDGS(path);
//		cgg.storeImage(path);
		
		
	 
		String css = "edge .notintree {size:0px;fill-color:gray;} " +
				 "edge .intree {size:3px;fill-color:black;}";


		graph.addAttribute("ui.stylesheet", css);
		graph.display(false);

		
		Prim prim = new Prim("ui.class", "intree", "notintree");
//		prim.setFlagAttribute("weight");
		prim.getFlagOn();
		prim.init(graph);
		prim.compute();

		int a =0 ;
		for (Edge e :prim.getTreeEdges() ) 
			a++;
		System.out.println(a);
			
		
 
	}

}

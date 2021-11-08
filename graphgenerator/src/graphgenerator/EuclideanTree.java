package graphgenerator;

import java.io.IOException;
import java.util.ArrayList;

import org.graphstream.algorithm.Prim;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import generic_tools.Tools;
import graphTool.GraphTool;
import graphViz.HandleVizStype;
import statistical_and_math_tools.BinominalProbGenerator;

public class EuclideanTree {

	

	private int seed, numNodes ; 
	private int[] sizeGrid ;
	private String id ;
	private Graph g ;
	private int idNodeInt = 0 , idEdgeInt = 0 ;
	private ArrayList<Node> listNodes = new ArrayList<Node>  ( );
	private BinominalProbGenerator bpg;

	public EuclideanTree ( String id , int seed , int numNodes , int[] sizeGrid) {
		this.id = id ;
		this.numNodes = numNodes ;
		this.seed = seed ;
		this.sizeGrid = sizeGrid ;	
		g = new SingleGraph(id);
		bpg = new BinominalProbGenerator(seed);
		
	}
	
	public void compute () {
		Delaunay_generator cgg = new Delaunay_generator ("del", seed, numNodes, new int [] {10,10});
		cgg.compute();
		Graph complete = cgg.getGraph();
		
		for (Edge e : complete.getEachEdge()) 
			e.addAttribute("weight", GraphTool.getDistGeom(e.getNode0(), e.getNode1()));
		
		Prim prim = new Prim("ui.class", "intree", "notintree");
		prim.init(complete);
		prim.compute();

		for (Node nc : complete.getEachNode()) {
			Node nt = g.addNode(nc.getId());
			nt.addAttribute("xy", nc.getAttribute("xy"));
		}
		
 
		for (Edge ec :prim.getTreeEdges() ) {
			Node nc0 = ec.getNode0(), nc1 = ec.getOpposite(nc0);
			String id0 = nc0.getId(), id1 = nc1.getId() , idec = ec.getId();
			Node nt0 = g.getNode(id0), nt1 = g.getNode(id1) ;
			Edge et = g.addEdge(idec, nt0 ,nt1);
			et.addAttribute("dist", graphTool.GraphTool.getDistGeom(nt0, nt1));	
		}
		
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
		// TODO Auto-generated method stub
		EuclideanTree tg = new EuclideanTree("eclideanTree", 0, 10, new int[] {10,10}) ;
		tg.compute();
		tg.getGraph().display(false);
		
		String path = "/home/mtirico/results/v10_";
		
//		tg.storeDGS(path);
		tg.storeImage(path);
		
		

	}

}

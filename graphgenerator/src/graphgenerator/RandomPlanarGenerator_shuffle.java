package graphgenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

//import org.apache.commons.math3.util.Pair;
import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;

import generic_tools.Tools;
import graphTool.GraphTool;
import graphViz.HandleVizStype;
import graphViz.HandleVizStype.stylesheet;
import statistical_and_math_tools.BinominalProbGenerator;

public class RandomPlanarGenerator_shuffle {

	private int numNodes ; 
	private int[] sizeGrid ;
	private String id ;
	private Graph g ;
	private int idNodeInt = 0 , idEdgeInt = 0 ;
	private ArrayList<Node> listNodes = new ArrayList<Node>  ( );
	private BinominalProbGenerator bpg;

	public RandomPlanarGenerator_shuffle ( String id , int seed , int numNodes , int[] sizeGrid) {
		this.id = id ;
		this.numNodes = numNodes ;
		this.sizeGrid = sizeGrid ;	
		g = new SingleGraph(id);
		bpg = new BinominalProbGenerator(seed);
		for ( double[] coords : Tools.getListRandomCoords( seed, numNodes, sizeGrid, 0 ) )  {
			Node n = g.addNode(Integer.toString(idNodeInt++)) ;
			n.addAttribute("xy", coords[0], coords[1]);
			listNodes.add(n);
		}
	}
		
	public void compute (double prob) {
		for (Node[] pair : getPairs()) {
			Node n0 = pair[0], n1 = pair[1];
			double [] coords0 = GraphPosLengthUtils.nodePosition(n0) ,
					  coords1 = GraphPosLengthUtils.nodePosition(n1)  ;
			Edge ex = GraphTool.getEgeIntersecInEdgeSet(coords0, coords1, g.getEdgeSet());
			boolean xEdge = (ex==null) ? false : true , 					
					addProb = bpg.getNextBoolean(prob) ;
			if ( xEdge ==false && addProb == true ) 
				g.addEdge(Integer.toString(idEdgeInt++), n0, n1);
		};
	}
	
	public void computeMaxDist (double prob) {
		for (Node[] pair : getPairs()) {
			Node n0 = pair[0], n1 = pair[1];
			double [] coords0 = GraphPosLengthUtils.nodePosition(n0) ,
					  coords1 = GraphPosLengthUtils.nodePosition(n1)  ;
			Edge ex = GraphTool.getEgeIntersecInEdgeSet(coords0, coords1, g.getEdgeSet());
			double ratio = 10/Math.pow(2, .5) ;
			double test = GraphTool.getDistGeom(n0,n1 ) / ratio;
			boolean xEdge = (ex==null) ? false : true , 					
					addProb = bpg.getNextBoolean(prob*test) ;
			if ( xEdge ==false && addProb == true ) 
				g.addEdge(Integer.toString(idEdgeInt++), n0, n1);
		};
	}

	
	private ArrayList<Node[]> getPairs () {
		ArrayList<Node[]> pairs = new ArrayList<Node[]> ();
//		double dist = 0 ;
		for ( int x = 0 ; x < g.getNodeCount() ; x++) 
			for ( int y = 0 ; y < g.getNodeCount() ; y++) 
				if (x > y) {
					pairs.add(new Node[] {listNodes.get(x), listNodes.get(y) } );		
					double test = GraphTool.getDistGeom(listNodes.get(x), listNodes.get(y) );
//					if ( test > dist ) dist = test ; 
					
				}
//		System.out.print(dist);
//		Collection test = pairs; 
		Collections.shuffle(pairs);		
		return pairs;
	}

	public Graph getGraph ( ) { return g ; }
	
	public void storeDGS ( String path ) throws IOException {
		g.write(path  + g.getId()+".dgs");
	}
	
	public void storeImage (String path ) {
		g.display(false);
		HandleVizStype netViz = new HandleVizStype(g, stylesheet.manual, "seed", 1);
		netViz.setupIdViz(false, g, 20, "black");
		netViz.setupDefaultParam(g, "black", "black", 3, 0.5);
		netViz.setupVizBooleanAtr(false, g, "black", "red", false, false);// netViz.setupFixScaleManual( true ,  net, sizeGridX , 0);
		netViz.createSquare(true, g, sizeGrid[0], 0);
		g.addAttribute("ui.screenshot", path + "/" + g.getId() + ".png");
	}
	
//-------------------------------------------------------------------------------------------------------------------------------	
	public static void main(String[] args) throws IOException {
		
		RandomPlanarGenerator_shuffle rpg = new RandomPlanarGenerator_shuffle("random_02", 0, 1000, new int [] {10,10});
		rpg.compute(0.5);
		rpg.getGraph().display(false);
		
		String path = "/home/mtirico/results/";
		
		rpg.storeDGS(path);
		rpg.storeImage(path);
////		 
	}
}
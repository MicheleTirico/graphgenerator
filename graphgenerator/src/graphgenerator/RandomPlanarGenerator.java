package graphgenerator;

import java.io.IOException;
import java.util.ArrayList;

import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;
import graphTool.GraphTool;

public class RandomPlanarGenerator {
	private int numNodes ; 
	private int[] sizeGrid ;
	private String id ;
	private Graph g ;
	private int idNodeInt = 0 , idEdgeInt = 0 ;
	private ArrayList<Node> listNodes = new ArrayList<Node>  ( );
	private statistical_and_math_tools.BinominalProbGenerator bpg;

	public RandomPlanarGenerator ( String id , int seed , int numNodes , int[] sizeGrid) {
		this.id = id ;
		this.numNodes = numNodes ;
		this.sizeGrid = sizeGrid ;	
		g = new SingleGraph(id);
		bpg = new statistical_and_math_tools.BinominalProbGenerator(seed);
//		for ( double[] coords : getListRandomCoords( 0, numNodes, sizeGrid, 0 ) )  {
//			Node n = g.addNode(Integer.toString(idNodeInt++)) ;
//			n.addAttribute("xy", coords[0], coords[1]);
//			listNodes.add(n);
//		}
	}
	
	public void compute (double prob) {
		for ( Node n0 : g.getEachNode()) {
			for ( Node n1 : g.getEachNode()) {
				if (!n0.equals(n1)) {	
					double [] coords0 = GraphPosLengthUtils.nodePosition(n0) ,
							  coords1 = GraphPosLengthUtils.nodePosition(n1)  ;
					Edge ex = GraphTool.getEgeIntersecInEdgeSet(coords0, coords1, g.getEdgeSet());
					boolean xEdge = (ex==null) ? false : true , 					
							addProb = bpg.getNextBoolean(prob) ;
					if ( xEdge ==false && addProb == true ) {
						try {
							g.addEdge(Integer.toString(idEdgeInt++), n0, n1);
						} catch (EdgeRejectedException e) {  }
					}
				}
			}
		}
	}
	public Graph getGraph ( ) { return g ; }
	
//-------------------------------------------------------------------------------------------------------------------------------	
	public static void main(String[] args) throws IOException {
		
		RandomPlanarGenerator rpg = new RandomPlanarGenerator("rd", 10, 1000, new int [] {10,10});
		rpg.compute(0.5);
		rpg.getGraph().display(false);
		
	}
}

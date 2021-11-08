package graphgenerator;

import java.util.ArrayList;

import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;

import generic_tools.Tools;
import graphTool.GraphTool;
import statistical_and_math_tools.BinominalProbGenerator;

public class GeometricalRandomGenerator {
	private int numNodes ; 
	private int[] sizeGrid ;
	private String id ;
	private Graph g ;
	private int idNodeInt = 0 , idEdgeInt = 0 ;
	private ArrayList<Node> listNodes = new ArrayList<Node>  ( );
	private BinominalProbGenerator bpg;

	public GeometricalRandomGenerator ( String id , int seed , int numNodes , int[] sizeGrid) {
		this.id = id ;
		this.numNodes = numNodes ;
		this.sizeGrid = sizeGrid ;	
		g = new SingleGraph(id);
		bpg = new BinominalProbGenerator(seed);
		for ( double[] coords : Tools.getListRandomCoords( 0, numNodes, sizeGrid, 0 ) )  {
			Node n = g.addNode(Integer.toString(idNodeInt++)) ;
			n.addAttribute("xy", coords[0], coords[1]);
			listNodes.add(n);
		}
	}
	public void compute (double dist , boolean isPlanar) {
		for ( Node n0 : g.getEachNode()) {
			for ( Node n1 : g.getEachNode()) {
				if ( ! n0.equals(n1) ) {
					double testDist = GraphTool.getDistGeom(n0, n1);
					if ( testDist < dist ) {
						boolean problems = false ;
						if ( isPlanar )  
							problems = GraphTool.isSegmentIntersecInEdgeSet(GraphPosLengthUtils.nodePosition(n0), 
									GraphPosLengthUtils.nodePosition(n1), g.getEdgeSet());
						if ( ! problems )
							try {
								g.addEdge("e"+Integer.toString(idEdgeInt++), n0, n1) ;
							} catch (EdgeRejectedException e) {  }
					}
				}
			}
		}
	}
	public Graph getGraph ( ) { return g ; }
	
//-------------------------------------------------------------------------------------------------------------------------------		
	public static void main(String[] args) {
		GeometricalRandomGenerator grg = new GeometricalRandomGenerator("grg", 10, 1000, new int[] { 10 , 10 }) ;
		grg.compute(.5, true);
		grg.getGraph().display(false);

	}

}

package graphgenerator;

import java.util.ArrayList;

import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
 import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;

import generic_tools.Tools;
import graphTool.GraphTool;
import graphTool.GraphTool;
 
public class GabrielGenerator {
	private int numNodes ; 
	private int[] sizeGrid ;
	private String id ;
	private Graph g ;
	private int idNodeInt = 0 , idEdgeInt = 0 ;
	private ArrayList<Node> listNodes = new ArrayList<Node>  ( );
	public GabrielGenerator ( String id , int seed , int numNodes , int[] sizeGrid) {
		this.id = id ;
		this.numNodes = numNodes ;
		this.sizeGrid = sizeGrid ;	
		g = new SingleGraph(id);
		for ( double[] coords : Tools.getListRandomCoords( 0, numNodes, sizeGrid, 0 ) )  {
			Node n = g.addNode(Integer.toString(idNodeInt++)) ;
			n.addAttribute("xy", coords[0], coords[1]);
			listNodes.add(n);
		}
	}
	
	public void compute ( ) {
		for ( Node n0 : g.getEachNode())  
			for ( Node n1 : g.getEachNode() )  
				if ( ! n0.equals(n1)) {
					double[] coords0 = GraphPosLengthUtils.nodePosition(n0),
							 coords1 = GraphPosLengthUtils.nodePosition(n1) ,
							 middle = new double[] {( coords0[0] + coords1[0])/2 , ( coords0[1] + coords1[1])/2 } ;
					double   r = GraphTool.getDistGeom(coords0, coords1)/2 ;
					int i = 0 ;
					boolean test = true ;
					while ( test == true && i < g.getNodeCount() ) {
						Node t = listNodes.get(i++);
						if ( !t.equals(n0) && !t.equals(n1)) {
							double[] coordsT = GraphPosLengthUtils.nodePosition(t);
							if ( GraphTool.getDistGeom(middle, coordsT) > r ) test = true ;
 							else 			test = false ;
						}
					}  
					if ( test == true )  
						try {
							g.addEdge(Integer.toString(idEdgeInt++), n0, n1);	 
						} catch (EdgeRejectedException e) { 	} 
				}
	}
	
	public Graph getGraph ( ) { return g ; }
	
	

}

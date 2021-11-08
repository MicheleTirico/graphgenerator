package graphgenerator;

import java.io.IOException;

import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;

import graphTool.GraphTool;

public class GeometricalGenerator extends GraphTool {

	private Graph source , output ;
	private double dist ;
	private boolean isPlanar ;
	private int idEdgeInt = 0 ;
	
	public GeometricalGenerator ( Graph source , double dist , boolean isPlanar ) {
		this.source = source;
		this.dist = dist ;
		this.isPlanar = isPlanar ;
	}
	
	public void compute ( ) {
		for ( Node n0 : source.getEachNode () ) {
			for ( Node n1 : source.getEachNode () ) {
				if ( ! n0.equals(n1) ) {
					double testDist = getDistGeom(n0, n1);
					if ( testDist < dist ) {
						boolean problems = false ;
						if ( isPlanar )  	problems = isSegmentIntersecInEdgeSet(GraphPosLengthUtils.nodePosition(n0), GraphPosLengthUtils.nodePosition(n1), source.getEdgeSet());
						if ( ! problems )
							try {
								source.addEdge("e"+Integer.toString(idEdgeInt++), n0, n1) ;
							} catch (EdgeRejectedException e) {  }
					}
				}
			}
		}
	}
	
//-------------------------------------------------------------------------------------------------------------------------------	
	//public static void main(String[] args) throws IOException {
//		GeometricalGenerator gg = new GeometricalGenerator(source, dist, isPlanar)
	//}
			
}

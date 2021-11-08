package graphgenerator;

import java.util.Random;

import org.graphstream.algorithm.ConnectedComponents;
import org.graphstream.algorithm.Kruskal;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.GridGenerator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;

public class GeneratorTools {

	public static Graph getGraphWithRandomNodes ( String idGr, int numNodes , int seedRandom , double[] limX, double[] limY ) {
		int idNodeInt = 0 ;
		Graph g = new SingleGraph(idGr);
		Random rd = new Random(seedRandom) ;
		while ( g.getNodeCount() < numNodes ) { 
			double 	coordX  = limX[0]+ rd.nextDouble() * ( limX[1] - limX[0]) ,
					coordY = limY[0]+ rd.nextDouble() * ( limY[1]- limY[0]) ;
			g.addNode("nInit"+Integer.toString(idNodeInt++)).setAttribute("xyz", coordX,coordY,0);
		}
		return g;
	}
	
	public static Graph getGrid ( String id , int size , boolean cross, boolean  tore,boolean  generateXY ) {
		Graph graph = new SingleGraph(id);
		Generator gen = new GridGenerator(cross, tore, generateXY);
		gen.addSink(graph);
		gen.begin();

		for(int i=1; i<size; i++) {
			gen.nextEvents();
		}

		gen.end();
		return graph;
	}
	
	public static void addNodeToGraph(Graph g, int numNodes, int seedRandom, double[] limX, double[] limY) {
		int idNodeInt = 0 ;
		Random rd = new Random(seedRandom) ;
		while ( g.getNodeCount() < numNodes ) { 
			double 	coordX  = limX[0]+ rd.nextDouble() * ( limX[1] - limX[0]) ,
					coordY = limY[0]+ rd.nextDouble() * ( limY[1]- limY[0]) ;
			g.addNode(Integer.toString(idNodeInt++)).setAttribute("xyz", coordX,coordY,0);
		}
	}
	public static Graph getGiantGraph ( Graph source , boolean setCoords ) {
		ConnectedComponents cc = new ConnectedComponents();
		cc.init(source);
		Graph gr = new MultiGraph("gr2");
		for (Node n : cc.getGiantComponent() ) {
			Node n2 = gr.addNode(n.getId()) ;
			if ( setCoords ) {				
				double[] coords = GraphPosLengthUtils.nodePosition(n);
				n2.setAttribute("xyz",coords[0], coords[1] , 0);			
			}
		}
		
		for ( Node n : cc.getGiantComponent()) 
			for ( Edge e : n.getEdgeSet() ) {
				String id = e.getId() ;
				Node n0 = e.getNode0(), n1 = e.getNode1() ; 
				try {
					gr.addEdge(id , n0.getId() , n1.getId());
				} catch (Exception ex) { 	} 
		}
		
		return gr ;
	}
	
	public static Graph GetMSTGraph ( String id , Graph g ) {
		Graph gt = new MultiGraph(id) ;
		Kruskal kruskal = new Kruskal( "tree" , true , false ) ;
		kruskal.init(g) ;
		kruskal.compute();	
		for ( Edge e : g.getEachEdge()) {
			boolean tree = e.getAttribute("tree");	
			if ( tree )  {
				Node n0 = e.getNode0(), n1 = e.getOpposite(n0), nt0 , nt1 ;
				double[] coords0 = GraphPosLengthUtils.nodePosition(n0) ,
						 coords1 = GraphPosLengthUtils.nodePosition(n1) ;
				try {
					nt0 = gt.addNode(n0.getId());
					nt0.setAttribute("xy", coords0[0] , coords0[1]);	
				} catch (Exception ex) {
					nt0 = gt.getNode(n0.getId());
				}
				try {
					nt1 = gt.addNode(n1.getId());
					nt1.setAttribute("xy", coords1[0] , coords1[1]) ;
				} catch (Exception ex) {
					nt1 = gt.getNode(n1.getId()); 
				}		
				gt.addEdge(e.getId(), nt0, nt1);
			}
		}
		return gt;
	}
}

package graphgenerator;

import java.io.IOException;

import org.graphstream.algorithm.generator.BarabasiAlbertGenerator;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import graphTool.GraphTool;
import graphTool.WriteCsvAttributesNodes;

public class Preferential_attachement_generator {
	
	private int nodes, maxEdges;
	private Graph graph ; 
	
	public Preferential_attachement_generator (int nodes, int maxEdges ) {
		this.nodes= nodes;
		this.maxEdges = maxEdges; 
		graph = new SingleGraph("Barabàsi-Albert");
		Generator gen = new BarabasiAlbertGenerator(maxEdges);
		gen.addSink(graph); 
		gen.begin();

		for(int i=0; i<nodes; i++) gen.nextEvents();
		gen.end();
	}
	
	public Graph getGraph ( ) {return graph; } 
	
	public static void main(String[] args) throws IOException {
		Graph g = new Preferential_attachement_generator(100000,1).getGraph();
//		g.display(true);
		for ( Node n : g.getEachNode()) {
			double d = n.getDegree();
			n.addAttribute("degree", d);
		}
		String pathExport = "data/test/ba_deg.csv" ;
		new WriteCsvAttributesNodes( g , new String[] {"degree"} , pathExport ).compute();;
		
		System.out.print("finish, go to -> "+ pathExport);

		
	}
}
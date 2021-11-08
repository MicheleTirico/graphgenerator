package graphgenerator;

import java.util.ArrayList;
import java.util.Arrays;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

 
public class PathStar {

	private String id ;
	private int numBranch , idNodeInt , idEdgeInt ;
	private double len ;
	private Graph g;
	public PathStar ( String id , int numBranch ,double len, int[]sizeGrid ) {
		this.id = id ;
		this.numBranch= numBranch ;
		this.len = len ;	
		g = new SingleGraph(id);
	}
	
	public static void main(String[] args) {
		System.out.print(new Object(){}.getClass().getName());

	}

}

package graphgenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.linearref.LinearIterator;
import org.locationtech.jts.triangulate.DelaunayTriangulationBuilder;

import generic_tools.Tools;
import graphViz.HandleVizStype;
 
public class Delaunay_generator {
	private int numNodes ; 
	private int[] sizeGrid ;
	private String id ;
	private Graph g ;
	private int idNodeInt = 0 , idEdgeInt = 0 ;
	private ArrayList<Node> listNodes = new ArrayList<Node>  ( );
	
	public Delaunay_generator ( String id , int seed , int numNodes , int[] sizeGrid) {
		this.id = id ;
		this.numNodes = numNodes ;
		this.sizeGrid = sizeGrid ;	
		g = new SingleGraph(id);
		for ( double[] c : Tools.getListRandomCoords( seed, numNodes, sizeGrid, 0 ) )  {
			Node n = g.addNode(Integer.toString(idNodeInt++)) ;
			n.addAttribute("xy", c[0], c[1]);
			listNodes.add(n);
		}
	}
	
	public Delaunay_generator ( String id , int seed , int numNodes , int[] sizeGrid , boolean run) {
		this.id = id ;
		this.numNodes = numNodes ;
		this.sizeGrid = sizeGrid ;	
		g = new SingleGraph(id);
		for ( double[] c : Tools.getListRandomCoords( seed, numNodes, sizeGrid, 0 ) )  {
			Node n = g.addNode(Integer.toString(idNodeInt++)) ;
			n.addAttribute("xy", c[0], c[1]);
			listNodes.add(n);
		}
		if (run) 
			compute();
		
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
	
	public void compute ( ) {	
		DelaunayTriangulationBuilder dtb= new DelaunayTriangulationBuilder();
		GeometryFactory gf = new GeometryFactory();
		Collection<Coordinate> col = new ArrayList<Coordinate>();
		Map<Coordinate,Node> map = new HashMap<Coordinate,Node>();
		for (Node n : listNodes) {
			double[] coord = GraphPosLengthUtils.nodePosition(n);
			Coordinate c = new Coordinate( coord[0],coord[1]); 
			col.add(c);
			map.put(c, n);
		}
		
		dtb.setSites(col);
		Geometry ml = dtb.getEdges(gf);
		LinearIterator li = new LinearIterator(ml);
		while ( li.hasNext()) {
			LineString ls = li.getLine();
			li.next();			
			Coordinate[] c = ls.getCoordinates();
			try { 
				g.addEdge(Integer.toString(idEdgeInt++), map.get(c[0]),map.get(c[1]));	 
			} catch (Exception e) { 	}
		}
	}
		
	public static void main(String[] args) throws IOException {
		int numNodes = 1000, seed = 1;
		int[] sizeGrid = new int[] {10,10};
		String id = "delaunay" ;
		
		Delaunay_generator gg = new Delaunay_generator(id, seed, numNodes, sizeGrid ) ;
		gg.compute();
		gg.getGraph().display(false);
		String path = "/home/mtirico/test/";
		
		gg.storeDGS(path);
//		gg.storeImage(path);
	}
}

 

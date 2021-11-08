package BuildGraphFromShp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;

public class CreateGraphFromAdjList {

	private Graph g ;
	private int idNodeInt = 0 , idEdgeint = 0;
	private String pathAdjList  ;
	private BufferedReader br ;
	private File csv ;
	private  String line ;
	private double incremSize ;
	private double[] xExt  , yExt  ;
	private Collection<Double>  coordX = new HashSet<Double> () , coordY = new HashSet<Double> () ;
		
	public CreateGraphFromAdjList ( String pathAdjList , double incremSize ) {
		this.pathAdjList = pathAdjList ;
		this.incremSize = incremSize * 100 ;
	}
	
	public void init ( int startAtLine  ) throws IOException {
		g = new MultiGraph("g");
		csv = new File (pathAdjList);
		br = new BufferedReader(new FileReader(csv));
		int i = 0 ;
		while ( i < startAtLine ) {	line = br.readLine();	i++;	}
	}
	
	public void alignGraph ( double[] minLignXY) {
		if ( xExt == null  || yExt ==null) { System.out.print("ERROR: graph not still computed");return ; }
		
		double[] deltaXY = new double[] { xExt[0] - minLignXY[0]   , yExt[0] - minLignXY[1]  } ;
		for ( Node n : g ) {
			double[] coords = GraphPosLengthUtils.nodePosition(n);
			n.addAttribute("xyz", coords[0] - deltaXY[0] , coords[1] - deltaXY[1] , 0 );
		}		
	}
	
	public void incremCoords ( ) {
		for ( Node n : g ) {
			double[] coords = GraphPosLengthUtils.nodePosition(n);
			n.addAttribute("xyz", coords[0] * incremSize , coords[1] * incremSize , 0 );
		}	
	}
	
// COMPUTE	
// ------------------------------------------------------------------------------------------------	
	public void compute (int stopAtLine ) throws IOException { 
		ArrayList<String[]> listEdgerefref = new ArrayList<String[]>() ;
		Map<String , Node> map = new HashMap<String , Node>() ;
		int posLine = 0 ;	
		while ( line  != null  && posLine < stopAtLine  )  {
			line = br.readLine();
	        if ( line != null ) {
	        	ArrayList<String> lineList =  getListNodes(line) ;
	        	double[] coord = getCoordsFromString(lineList.get(0) );     
	        	coordX.add(coord[0]);coordY.add(coord[1]);	        	
	        	String ref = getRef(coord) ;
	        	Node n0 = g.addNode(Integer.toString(idNodeInt++));
          		n0.addAttribute("xyz", (double) coord[0] ,(double)  coord[1] , 0); 		
          		map.put(ref, n0);
          		int i = 1 ;
          		while ( i < lineList.size() ) {
          			double[] coord1 = getCoordsFromString(lineList.get(i) );
          			String ref1 = getRef(coord1);
      				listEdgerefref.add(new String[] { ref , ref1} );
      				i++;
          		}
          		posLine++;
	        }
		}
		for ( String[] refref : listEdgerefref ) {
			Node n0 = map.get(refref[0]) , n1 = map.get(refref[1]) ;
			g.addEdge(Integer.toString(idEdgeint++), n0, n1) ;			
		}		
		xExt = new double[] { Collections.min(coordX) , Collections.max(coordX) };
		yExt = new double[] { Collections.min(coordY) , Collections.max(coordY) };		
	}

// PRIVATE
// ------------------------------------------------------------------------------------------------	
	private String getRef ( double[] coord ) {
		return String.format("%.10f" ,  coord[0]) + "_" +String.format("%.10f" , coord[1] ) ;	 
	}

	private ArrayList<String> getListNodes( String line  ) {
		ArrayList<String> nodes = new ArrayList<String>  () ;
		String[] lineArr = line.split("/" ) ;
		nodes.addAll(Arrays.asList(lineArr)) ;
		return nodes;
	}

	private double[] getCoordsFromString ( String coords ) {
//		System.out.println(coords);
		int min0 = 1  , pos = min0 ;
		String sep0 = ","  ;
//		String sep0 = "/"  ;
		char[] chars  = coords.toCharArray();
		String t = new String(new char[]{chars[pos]});
		while ( ! "," .equals( t )  )  	t = new String(new char[]{chars[pos++]});			   
//		while ( ! "/" .equals( t )  )  	t = new String(new char[]{chars[pos++]});			   
		int max0 = pos - 1 ;	 			 
		int min1 = max0 + 2  ;
		while ( ! sep0.equals( t )  ) 	t = new String(new char[]{chars[pos++]});		
		
		return new double[] {Double.parseDouble( coords.substring(min0, max0) )
				,Double.parseDouble(coords.substring(min1, coords.length() -1 ) ) }  ;
	}

// GET SET 
// ------------------------------------------------------------------------------------------------	
    public Graph getGraph ( ) {	return g ; }
    public void storeDGS ( String path ) throws IOException {
		g.write(path);
	}
	
// RUN
// ------------------------------------------------------------------------------------------------	
    public static void main(String[] args) throws IOException {
        String pathAdjList = args[0], pathDgs = args[1];
        
        System.out.println(pathAdjList + " " +pathDgs);
        CreateGraphFromAdjList c = new CreateGraphFromAdjList(pathAdjList, 1);
        c.init(3);
        c.compute(100000000);
//        Graph g = c.getGraph() ;
//        g.display(false);
        c.storeDGS("/home/mtirico/test/test.dgs");
        c.storeDGS(pathDgs);

    }

}

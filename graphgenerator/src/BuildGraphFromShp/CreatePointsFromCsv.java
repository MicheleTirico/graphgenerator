package BuildGraphFromShp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import graphTool.StoreNetwork;

public class CreatePointsFromCsv {

	private Graph g ;
	private int idNodeInt = 0  ;
	private String pathPoints  ;
	private BufferedReader br ;
	private File csv ;
	private  String line ;
	private double incremSize ;
	
	private double[] xExt  , yExt  ; 
	
	private Collection<Double>  coordX = new HashSet<Double> () ,
								coordY = new HashSet<Double> () ;
		
	public CreatePointsFromCsv ( String id , String pathPoints , double incremSize ) throws IOException {
		this.pathPoints = pathPoints ;
		this.incremSize = incremSize * 100 ;
		g = new SingleGraph(id); 
		createPoints();
	}
	
	public CreatePointsFromCsv ( String id , String pathPoints , double incremSize ,int[] posXY , int[] posAttr , String[] nameAttr) throws IOException {
		this.pathPoints = pathPoints ;
		this.incremSize = incremSize ;
		g = new SingleGraph(id); 
		createPoints(posXY , posAttr , nameAttr);
	}
	
	private void createPoints ( ) throws IOException {
		File file = new File ( pathPoints ) ;
	    BufferedReader input = new BufferedReader(new FileReader(file));
	    while ((line = input.readLine()) != null) {
	    	if ( ! line.startsWith("os")) {
	    		try { //    	
			    	String[] lineArr = line.split(",");
//			    	System.out.println(Arrays.asList(lineArr) + lineArr[0] + " // " + lineArr[1]) ;
			    	double x = Double.parseDouble(lineArr[0].substring(1));
			    	double y = Double.parseDouble(lineArr[1].substring(0, lineArr[1].length()-1 )) ;
//			    	
//			    	double x = Double.parseDouble(lineArr[3]), y = Double.parseDouble(lineArr[4]) ;
//			    	System.out.println(line + " " + x + " " + y ) ;
			    	g.addNode(Integer.toString(idNodeInt++ )).addAttribute("xyz",incremSize *  x , incremSize *  y , 0.0 ); 
	    		} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {System.out.println(e);}
	    	}
	    }
		System.out.println(g.getNodeCount()) ;
		
	    input.close();
	}
	private void createPoints ( int[] posXY , int[] posAttr ,String[]  nameAttr) throws IOException {
		File file = new File ( pathPoints ) ;
	    BufferedReader input = new BufferedReader(new FileReader(file));
	    while ((line = input.readLine()) != null) {
	    	if ( ! line.startsWith("os")) {
	    		try { //    	System.out.println(line) ;
			    	String[] lineArr = line.split(";");
			    	double x = Double.parseDouble(lineArr[posXY[0]]), y = Double.parseDouble(lineArr[posXY[1]]) ;
			    	Node n = g.addNode(Integer.toString(idNodeInt++ ));
			    	n.addAttribute("xyz",incremSize *  x , incremSize *  y ); 
			    	int pos = 0 ; 
			    	while ( pos < nameAttr.length ) {
			    		n.addAttribute(nameAttr[pos], Double.parseDouble(lineArr[posAttr[pos]]));
			    		pos++ ;
			    	}
	    		} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {	}
	    	}
	    }
	    input.close();
	}
// GET SET 
// ------------------------------------------------------------------------------------------------			
	public Graph getGraph ( ) {	return g ; }
		
// RUN
// ------------------------------------------------------------------------------------------------	
	public static void main(String[] args) throws IOException {
		String 
				path 		= "data/" ,
				city 		= "Fecamp" ,
				pathData 	= path + "Fecamp_addList.csv", 
				pathStore 	= path + city +"/" +city+".dgs";
				
		System.out.println(city);			
		CreateGraphFromAdjList cg = new CreateGraphFromAdjList(pathData, 10 );
		cg.init(3);
		cg.compute(150000000);
		Graph g = cg.getGraph() ; //			g.display(false);
		StoreNetwork.storeGraph(pathStore , city, g ) ;  
	 
		
		System.out.println("finish ! " + g.getNodeCount());
		g.display(false);
		
	}
	
}

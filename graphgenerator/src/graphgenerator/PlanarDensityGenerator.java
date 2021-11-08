package graphgenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import org.apache.commons.math3.stat.inference.AlternativeHypothesis;
import org.apache.commons.math3.stat.inference.BinomialTest;
import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;

import graphTool.GraphTool;

public class PlanarDensityGenerator extends GraphTool {

	private Graph graph  ;
	private double distMax , distMin ;
	int numNodes; double[] extX, extY; 
	private int idEdgeInt = 0 , idNodeInt = 1 ;
	
	public PlanarDensityGenerator ( Graph graph , double distMin,double distMax , int numNodes,  double[] extX , double[] extY ) {
		this.graph = graph;
		this.distMax = distMax ;
		this.distMin = distMin ;
		this.numNodes = numNodes;
		this.extX = extX ;
		this.extY = extY ;
		graph = new SingleGraph("test");
	}
	
	public void compute (  ) {
		graph.addNode("0").setAttribute("xyz", (extX[1] - extX[0])/2 , (extY[1] - extY[0])/2 , 0 );
		int t  = 0 ; 
		graph.display(false);
		while ( graph.getNodeCount() < numNodes ) {
	//		System.out.println(t++ + " "+ graph.getNodeCount());
			double  x = extX[0] + new Random ().nextDouble() *(extX[1] - extX[0]) ,
					y = extY[0] + new Random ().nextDouble() *(extY[1] - extY[0]) ;
			
			boolean distMinTest = false ;
			Iterator<Node> itNode = graph.getNodeIterator();
			while( distMinTest == false && itNode.hasNext()) {
				Node testNode = itNode.next();
				double a = getDistGeom(new double[] {x, y} ,GraphPosLengthUtils.nodePosition(testNode) );
				if ( a <  distMin )  
					distMinTest = true ;
				
				
			}
			if ( distMinTest == true ) {
				Node nodeSource = graph.addNode(Integer.toString(idNodeInt++));
				nodeSource.setAttribute("xyz", x , y , 0 );
				double[] coordsNodeSource = GraphPosLengthUtils.nodePosition(nodeSource);
				ArrayList<Node> listNodeNear = getListNodesNear(graph, nodeSource, distMax);
				Edge e = null ;
				int p = 0 ; 
				listNodeNear.remove(nodeSource);
				ArrayList<Node> listNodeToVisit = listNodeNear ;
				ArrayList<Edge> listEdgeNear = new ArrayList<Edge> () ;
				for ( Node n : listNodeNear) 
					listEdgeNear.addAll(n.getEdgeSet());
				Node nodeTest  = null ;
				Collection<Edge> edgeSet = graph.getEdgeSet();
				double numEdge = listEdgeNear.size() ;
				while ( /* e == null &&*/  p < listNodeNear.size() ) {
					
				//	System.out.print(listNodeToVisit.size() +"-" + listNodeNear.size() + " / ");
					Random rd = new Random (10);
					nodeTest = listNodeToVisit.get((int) (rd.nextDouble() * listNodeToVisit.size()));
					double prob = nodeTest.getDegree() / ( 2 * numEdge );
					BinomialTest bt = new BinomialTest();
					double test = bt.binomialTest(10, 4, prob, AlternativeHypothesis.GREATER_THAN);
		//			 System.out.println(nodeTest.getDegree() + " " + numEdge +" " + test + " " + prob);
					if (  test < .4 || Double.isNaN(test) == true ) {
				//		System.out.print("ciao" );
						double[] coordsNodeTest = GraphPosLengthUtils.nodePosition(nodeTest);
						if ( isSegmentIntersecInEdgeSet(coordsNodeTest, coordsNodeSource , edgeSet) == false  ) {
							try {
								e  = graph.addEdge(Integer.toString(idEdgeInt++), nodeTest, nodeSource);
	//									edgeSet.add(e);
								listNodeToVisit.remove(nodeTest) ;
							} catch (EdgeRejectedException ex ) { 	}
						} else {
							listNodeToVisit.remove(nodeTest);
							}
					} else {
						listNodeToVisit.remove(nodeTest);
					}
					p++;
				}
				if ( e == null ) {
					graph.removeNode(nodeSource);
				}
					
			} 
			
		}
		
	}
	
	public void computeConnectAll () {
		graph.addNode("0").setAttribute("xyz", (extX[1] - extX[0])/2 , (extY[1] - extY[0])/2 , 0 );
		int t  = 0 ; 
		graph.display(false);
	 
		while ( graph.getNodeCount() < numNodes ) {
			double  x = extX[0] + new Random ().nextDouble() *(extX[1] - extX[0]) ,
					y =  extY[0] + new Random ().nextDouble() *(extY[1] - extY[0]) ;
			
			boolean distMinTest = false ;
			Iterator<Node> itNode = graph.getNodeIterator();
			while( distMinTest == false && itNode.hasNext()) {
				Node testNode = itNode.next();
				double a = getDistGeom(new double[] {x, y} ,GraphPosLengthUtils.nodePosition(testNode) );
				if ( a <  distMin ) {
					distMinTest = true ;
				}
				
			}
			if ( distMinTest == true ) {
				System.out.println(t++ + " "+ graph.getNodeCount());
				
				Node nodeSource = graph.addNode(Integer.toString(idNodeInt++));
				nodeSource.setAttribute("xyz", x , y , 0 );
				double[] coordsNodeSource = GraphPosLengthUtils.nodePosition(nodeSource);
				ArrayList<Node> listNodeNear = getListNodesNear(graph, nodeSource, distMax);
				boolean almostOneEdge = false;
				for ( Node nodeTest : listNodeNear ) {
					Collection<Edge> edgeSet = graph.getEdgeSet();
					double[] coordsNodeTest = GraphPosLengthUtils.nodePosition(nodeTest);
					if ( isSegmentIntersecInEdgeSet(coordsNodeTest, coordsNodeSource , edgeSet) == false  ) {
						 graph.addEdge(Integer.toString(idEdgeInt++), nodeTest, nodeSource);
						 almostOneEdge = true ;
					}
				}
				if ( almostOneEdge == false)
					graph.removeNode(nodeSource);
			
			}
		}
	}
	
	private Node getRandomNodeNear (Graph graph , Node source , double distMax ) {
		Node near = null ;
		ArrayList<Node> listNodes = getListNodes(graph);
		Random rd = new Random ();
		while ( near == null ) { //distTest < distMax) {
			Node n = listNodes.get((int) (rd.nextDouble() * listNodes.size()));
			if ( getDistGeom( n , source) < distMax ) 
				near = n ; 
		}
		return near ; 
	}
	
	private ArrayList<Node> getListNodesNear (Graph graph ,  Node source , double distMax) {
		ArrayList<Node> list = new ArrayList<Node> () ;
		for ( Node n : getListNodes(graph)) {
	//		System.out.println(getDistGeom(n, source) + " / " + distMax);
			if ( getDistGeom( n , source) < distMax && ! list.contains(n)) {
				list.add(n);
			}
		}
		
//		System.out.println(getListNodes(graph));
		return list; 
	}
	
//	public ArrayList<Node> getListNodes (Graph graph ) {
//		ArrayList<Node> listNodes = new ArrayList<Node> () ;
//		listNodes.addAll( graph.getNodeSet()) ;		
//		return listNodes ;
//	}
}
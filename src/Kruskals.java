import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

//Kruskals class
//******************PUBLIC OPERATIONS*********************
//void kruskal             --> Kruskals alg to make MST
//void insert              --> Create list of edges and a map
//******************ERRORS********************************
//Throws IOException and FileNotFoundException as appropriate

/**
 *  Work with graphs and Kruskals algorithm for minimum spanning trees for a set of cities in Texas.
 */
public class Kruskals 
{	
	private static HashMap<String, Integer> cityNums = new HashMap<>();//city names mapped to integers																	
	private static List<List<Edge>> edges = new LinkedList<List<Edge>>();//adjacency edgeList edges
	private static List<Edge> MSTedges = new ArrayList<>();//MST edges
	private static int vertnum; //total vertices/cities
	
	/**
     * Graph edge with city endpoints and dist
     */
	private static class Edge 
	{
		String begCity;
		String endCity;
		int dist;
		Edge(String begCity, String endCity, int dist) 
		{
			this.begCity = begCity;
			this.endCity = endCity;
			this.dist = dist;
		}
	}	
	
	/**
	 * Using Priority Queue and Disjoint Set Kruskal alg to build MST
	 */	
	public void kruskal() 
	{				
		DisjSets DisjSet = new DisjSets(vertnum);
		
		//priority queue sorted by dist
		PriorityQueue<Edge> edgeQ = new PriorityQueue<>(new Comparator<Edge>() 
		{
			@Override
			public int compare(Edge edge1, Edge edge2) 
			{
				return edge1.dist - edge2.dist;
			}
		});
		PQedger(edgeQ);//add to pq
		
		int treeEdges = 0;
		Edge edgy;		
		while(treeEdges < vertnum - 1) 
		{
			edgy = edgeQ.poll(); //min edge
			int begPar = DisjSet.find(cityNums.get(edgy.begCity));
			int endPar = DisjSet.find(cityNums.get(edgy.endCity));
			if(begPar != endPar) 
			{
				treeEdges++;//add new MST edge
				DisjSet.union(begPar, endPar);
				MSTedges.add(edgy); //add edge to MST
			}
		}
	}	
	
	/**
     * Parses data to create edge edgeList and map.
     */
	public void insert(BufferedReader buffread) throws IOException 
	{		
		String line;
		int cnt = 0;
		while ((line = buffread.readLine()) != null) 
		{
			String[] nowLine = line.split(",");
			int edgeNum = nowLine.length;
			if(edgeNum <= 0) 
			{
				break;//no edge
			}
			List<Edge> edgeList = new LinkedList<>();
			String begCity = nowLine[0];
			cityNums.put(begCity, cnt);
			for(int i = 1; i < edgeNum; i += 2) 
			{
				//new edge
				Edge edge = new Edge(begCity, nowLine[i], Integer.parseInt(nowLine[i + 1]));
				edgeList.add(edge);
			}
			edges.add(edgeList);
			cnt++;
        }
		vertnum = cityNums.size();
	}
	
	/**
	 * Adds unique edges to priority queue 
	 */
	private void PQedger(PriorityQueue<Edge> edgeQ) 
	{						
		//row x col
		for(int i = 0; i < edges.size(); i++) 
		{			
			for(int j = 0; j < edges.get(i).size(); j++) 
			{
				Edge edge = edges.get(i).get(j);
				Edge revEdge = new Edge(edge.endCity, edge.begCity, edge.dist);
				if(!edgeQ.contains(revEdge)) 
				{
					edgeQ.offer(edge);
				}
			}
		}
	}	

	public static void main(String[] args) 
	{
		Kruskals krusky = new Kruskals();
		
		//make edges and graph
		try 
		{
			//read file
			String file = "assn9_data.csv";
			InputStream instr = new FileInputStream(file);
	        InputStreamReader inred = new InputStreamReader(instr, Charset.forName("UTF-8"));
			BufferedReader buffread = new BufferedReader(inred);
			krusky.insert(buffread);  
	        buffread.close();
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} catch (IOException e)	
		{
			e.printStackTrace();
		}
		
		//create MST
		krusky.kruskal();
		
		//print MST
		int distSum = 0;
		for(int i = 0; i < MSTedges.size(); i++) 
		{
			distSum += MSTedges.get(i).dist;
			System.out.println(MSTedges.get(i).begCity + " to " + MSTedges.get(i).endCity + " is " + MSTedges.get(i).dist + " miles.");
		}
		System.out.println("\nSum of distances: " + distSum + " miles.");
	}
}
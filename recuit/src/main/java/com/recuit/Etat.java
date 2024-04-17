package com.recuit;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.recuit.interfaces.PointInterface;

public class Etat <T extends PointInterface>{
    
    public List<Point2D> nodes;
    public int Nmin = 10; // Minimum number of points
	public int Nmax = 30; // Maximum number of points
	public int N;		  // Actual number of points

    public double objective; // Value of the cost function with that state

    @SuppressWarnings("rawtypes")
    public Graph myGraph;

    private static Random generateur=new Random(126);

    
    // Constructor
    public Etat() {
		// Set a random number of points
		this.N = (int) ((Math.random()*(Nmax-Nmin) + Nmin));
        this.nodes = new ArrayList<Point2D>();
				
		Point2D randomPoint;
		int count = 0;
		while (count<this.N){
			randomPoint = Manager.grid.randomFreePoint();
            this.nodes.add(randomPoint);
        
			count++;
		}
		
		System.out.println(Recuit.YELLOW+"New random state contains nodes: "+count+Recuit.RESET);
    }

    public void initAleatEtat(){

		// Set a random number of points
		this.N = (int) ((Math.random()*(Nmax-Nmin) + Nmin));;
        this.nodes = new ArrayList<Point2D>();
		
		Point2D randomPoint;
		int count = 0;
		while (count<this.N){
			randomPoint = Manager.grid.randomFreePoint();
            this.nodes.add(randomPoint);

			count++;
		}

        this.myGraph = PRM.createGraph(this.nodes, Manager.grid, 1.1);
        
    }

    public void initAleatEtatDefiningN(int N){

		// Set a random number of points
		this.N = N;
        this.nodes = new ArrayList<Point2D>();
		
		Point2D randomPoint;
		int count = 0;
		while (count<this.N){
			randomPoint = Manager.grid.randomFreePoint();
            this.nodes.add(randomPoint);

			count++;
		}

        this.myGraph = PRM.createGraph(this.nodes, Manager.grid, 1.1);
        
    }



    /**
     * Copy the objective and the vecteur from 'in' to 'out'.
     */
    @SuppressWarnings("unchecked")
    public static void copy(Etat in, Etat out){
        
		// Set a random number of points
		out.N = in.N;
        out.nodes = new ArrayList<Point2D>();
        
        int count = 0;
		while (count<out.N){

            Point2D copiedPoint = new Point2D(((Point2D) in.nodes.get(count)).getX(), ((Point2D) in.nodes.get(count)).getY());
            out.nodes.add(copiedPoint);
			
			count++;
		}

        
		out.objective=in.objective;

        // COPY THE GRAPH PROPERLY
        out.myGraph = new Graph(out.nodes, in.myGraph.edges);

    }


    /**
     * Generate voisin (Generate neighbor)
     */
    public void genererVoisin(){

		int newN = (int) ((Math.random()*(this.Nmax-this.Nmin) + this.Nmin));
		
        List<Point2D> newNodes = new ArrayList<Point2D>();
		
		int count = 0;
		
		if (newN >= this.N){
			
			for (count = 0; count<N; count++) {
                newNodes.add(this.nodes.get(count));
				count++;
			}
			while (count<=newN-1) {
				
				Point2D newRandom = Manager.grid.randomFreePoint();
                newNodes.add(newRandom);
                count ++;
			
            }
			
		}
		else {
			while (count<=newN-1) {
                newNodes.add(this.nodes.get(count));
				count++;
				
			}
			
		}
		
        this.nodes = newNodes;
        this.N = newN;

        myGraph = PRM.createGraph(this.nodes, Manager.grid, 1.1);

    }


    /**
     * Generate voisin (Generate neighbor) N not changed. Random changed by its neighbor
     */
    public void genererVoisinOpt3(){
		
	    int position = generateur.nextInt(N);
        
	    Point2D changed = Manager.grid.getPointFromID(this.nodes.get(position).getId());

        String voisin = changed.getRandomNeighbor();

        // CHECK IF THERE IS ALREADY THIS NODE:
        boolean keepLooking = true;
        while (keepLooking){
            if(itIsAlreadyNode(voisin)){
                voisin = changed.getRandomNeighbor();
            }
            else{
                keepLooking = false;
            }
        }

        Graph bufferGraph = myGraph.copy();
        myGraph = PRM.createGraphFromGraph(bufferGraph, Manager.grid.getPointFromID(voisin), changed, Manager.grid, position);
        this.nodes.set(position, Manager.grid.getPointFromID(voisin));
    }


    private boolean itIsAlreadyNode(String voisin) {
        int i = 0;
        while(i<N){
            if (nodes.get(i).getId().equals(voisin)){
                return true;
            }
            i++;
        }
        return false;
    }

    
    public void genererVoisinOpt4(){

        int position = generateur.nextInt(N);

        Point2D newRandom = Manager.grid.randomPoint();
        while (!Manager.grid.isAvailable(newRandom)){
            newRandom = Manager.grid.randomPoint();
        }

        Graph bufferGraph = myGraph.copy();
        myGraph = PRM.createGraphFromGraph(bufferGraph, newRandom, nodes.get(position), Manager.grid, position);
        this.nodes.set(position, newRandom);
    }



    public String printEtat()
    {
        String buffer = "";
        buffer=buffer+"DimEtat= "+ N;
        buffer=buffer + " Objective= " + objective;
        buffer=buffer + " Graph= " + myGraph.printGraph();

        buffer = buffer + " Nodes=";
        for (Point2D p : nodes){
            buffer = buffer+ " "+p.getId()+",";
        }
        return buffer;
    }

    public void saveEtat(String nomGen){
    // Save the state in the RESULTS/xxx.txt file
    // The file structure is:
    // The objective
    // The edges
    // The nodes
    
		String nomFichierOut = "src/RESULTS/"+nomGen+".txt";
        
        try {
            FileWriter file = new FileWriter(nomFichierOut);
            PrintWriter fileOutput = new PrintWriter(file);
            fileOutput.print(" "+ objective +" = objective"+"\n");

            List<Edge> edges = myGraph.getEdges();
            for (int i=0;i<edges.size();i++)
            {
                fileOutput.print(edges.get(i).printEdge() + "\n");
            }
            
            List<Point2D> nodes = myGraph.nodes;
            for (int i=0;i<nodes.size();i++)
            {
                fileOutput.print(nodes.get(i).getId() + "\n");
            }

            fileOutput.close();
        }
        catch(IOException ioe) {
            System.err.println("Erreur fichier\n"+ioe);
        }
    }


    public double calculCritere(double ratioTemperature) {
        /*
         * The point is to have a value of the cost function for this given state
         * However, a state does not have enough info
         * From the given state a Graph must be made
         * Then, it comes the evaluation of that graph
         * Then the objective is computed from there
         * So:
         * 1. compute the graph for the given etat NO THE ETAT IS DONE IN THE GENERATION
         * 2. compute cost function from that graph
         */

        objective = myGraph.evaluateMe();

        return objective;
    }

}

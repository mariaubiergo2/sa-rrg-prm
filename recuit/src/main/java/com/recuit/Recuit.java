package com.recuit;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.WindowConstants;

import com.recuit.edgesInformation.MongoDBConnection;
import com.recuit.interfaces.PointInterface;


public class Recuit <T extends PointInterface>{
    
    // Random value used for acceptance probability
    private static Random generateur = new Random(123);

    // Parameters to recuit
    private static final int nbTransitions = 2000;
    private static final double alpha = 0.999;
    private static final boolean minimisation = true;
    private static String nomGen = "matrixBigObstacles"; // name of the DATA file CHANGED
											// Is reading the matrix as an input

    // Dimension of the problem
    private static int DIMENSION = 	10; // Number of nodes!!!!! NOW
    private Etat xi, xj;
    private static double temperature = 0.01;
	public static int PENALTY = 9000;
    
	// Matrix stuff
	private static int neighborRadius = 2;

	public List<Point2D> basesList;

	// Plotting stuff
	public static String RESET = "\u001B[0m";
	public static String RED = "\u001B[31m";
	public static String GREEN = "\u001B[32m";
	public static String YELLOW = "\u001B[33m";
	public static String PURPLE = "\u001B[35m";

	public PlotGraph example;


	// Constructor
	public Recuit() {
		xi = new Etat();
		xj = new Etat();

		basesList = new ArrayList<>();
		Point2D point1 = new Point2D(5, 5);
		Point2D point2 = new Point2D(95, 95);
		Point2D point3 = new Point2D(5, 95);
		Point2D point4 = new Point2D(95, 5);
		basesList.add(point1);
		basesList.add(point2);
		basesList.add(point3);
		basesList.add(point4);

		Manager.setBases(basesList);
	}


	public static void preProcessing(String nomGen){
	// This function aim is to start the simulation
				
		Manager.initializeGrid(nomGen);
		Manager.setNeighborsGrid(neighborRadius);
		Manager.setConstants();

		MongoDBConnection.generateMongoDBConnection("mongodb://localhost:27017", "con"); 

	}

	
    public void postProcessing(String nomGen){
		xi.saveEtat(nomGen);

    }


    // Acceptance principle in maximization
	// This method implements the acceptance criterion for simulated annealing.
	// It determines whether a new state should be accepted based on the change in the objective function value and the current temperature.
    private boolean accept(double objectivei, double objectivej, double temp, boolean minimiser, double ratioTemperature){
		boolean res=false;
		double proba;

		//minimisation
		if (minimiser){
			if (objectivej<objectivei){
				res = true;
			}
			else {
				// The probability to accept it either way
				proba = Math.exp((objectivei-objectivej)/temp);
				if (generateur.nextDouble()<proba) {
					res = true;
				} 
				
			}
		}

		//maximisation
		else {
			if (objectivej>objectivei){
				res = true;
				// System.out.println(GREEN+"V Accepted because is better."+RESET);
			}
			else {
				// The probability to accept it either way
				proba = Math.exp((objectivei-objectivej)/temp);
				if (generateur.nextDouble()<proba) {
					// System.out.println(GREEN+"V Accepted though it is worse."+RESET);
					res = true;
				} 
				// else {
				// 	System.out.println(RED+"X Not accepted."+RESET);
				// }
			}
		}
		return res;
    }



    // Heat Up
    public double heatUpLoop() {
		//HeatUp heat = new HeatUp();

		long startTimeHeating=System.currentTimeMillis();

		int acceptCount = 0;
		double yi = 0, yj;
		double T=0.0;
		double tauxAccept=0.0;

		xi.initAleatEtat();
		yi=xi.calculCritere(0.5);
		T=0.1*yi;
		do {
			acceptCount=0;
			for (int i = 0; i < nbTransitions; i++) {
				//generation d'un point de l'espace d'etat
				xi.initAleatEtat();
				yi = xi.calculCritere(0.5);
		
				//generation d'un voisin
				Etat.copy(xi,xj);
				xj.genererVoisin();
				yj = xj.calculCritere(0.5);
				
				if (accept(yi,yj,T,minimisation, 0.0)) {
					acceptCount++;
				}
			}
			// Percentage of accepted
			tauxAccept = (double) acceptCount / (double) nbTransitions;
			T = T * 1.1;
			System.out.println(PURPLE+"T= " + T + " tauxAccept= " + tauxAccept);
			System.out.println("-------------------------------------------------------"+RESET);
		} while(tauxAccept < 0.8);
		
		System.out.println(PURPLE+"Temperature after heatUp = " + T + " tauxAccept= " + tauxAccept+ "\n"+RESET);

		long endTimeHeating=System.currentTimeMillis();
		
		// Print th information
		System.out.println("Simulation time : "+(endTimeHeating-startTimeHeating)/1000+" s ");

		return T;
    }

    // Cooling
	public void coolingLoop(double Tinit) {
		double yi=0.0,yj=0.0;
		
		double T=Tinit;

		Etat bestEtat = new Etat<>();
		
		// Future work: use the ratio temperature to make the algorithm more accurate
		double ratioTemperature = 0.5;
	    
		// Generate a first random estate
	    // xi.initAleatEtatDefiningNandBases(DIMENSION, basesList);
		xi.initAleatEtatDefiningN(DIMENSION);
		yi = xi.calculCritere(ratioTemperature);

		Etat.copy(xi, bestEtat);

	    do {
			ratioTemperature=T/Tinit;
		
			for (int i=0; i < nbTransitions; i++){
				// long startTime=System.nanoTime();

				Etat.copy(xi,xj);

				// long endTime=System.nanoTime();
		
				// System.out.println(RED+"COPY time : "+(endTime-startTime)+" s "+RESET);

				// long startTime=System.nanoTime();
				// xj.genererVoisinOpt3WithBases();
				xj.genererVoisinOpt3();

				// long endTime=System.nanoTime();
		
				// System.out.println(RED+"Voisin time : "+(endTime-startTime)+" s "+RESET);

				// startTime=System.nanoTime();
				yj=xj.calculCritere(ratioTemperature);
				// endTime=System.nanoTime();
				// System.out.println(RED+"Calcule teria time : "+(endTime-startTime)+" s "+RESET);

				

				// Call the function you want to measure
				// long startTime4=System.nanoTime();
				if (accept(yi,yj,T, minimisation, ratioTemperature)){
					
					yi=yj;

					Etat.copy(xj,xi);

				}

				// long endTime4=System.nanoTime();
		
				// System.out.println(RED+"Acceptance time : "+(endTime4-startTime4)+" s "+RESET);

				
			}
			T=T*alpha;

			if (bestEtat.objective > xi.objective){
				Etat.copy(xi, bestEtat);
				System.out.println("**********BEST ETAT***********");
				System.out.println("T= " + T + " Tinitial= " + Tinit + " valeur critere " + xi.objective);
				System.out.println(xi.printEtat());
				
				this.postProcessing(nomGen.concat("BEST"));
			}

	    } while(T>0.0001*Tinit);

		xi.calculCritere(ratioTemperature);

		System.out.println(PURPLE+"**************************************************");
		System.out.println("T= " + T + " Tinitial= " + Tinit + " valeur critere " + yi+RESET);
		System.out.println(YELLOW+"Final Etat: "+RESET);
		System.out.println(xi.printEtat());

    }


	
	public void plotResults(String title){
		this.example = new PlotGraph(title);
        this.example.setSize(800, 600);
        this.example.setLocationRelativeTo(null);
        this.example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.example.setVisible(true);
	}


	public void plotResultsTimer(String title, int miliSeconds){
		
		plotResults(title);
        
        TimerTask task = new TimerTask() {
			public void run() {
				example.setVisible(false);
				// example.dispose();
			}
        };
        Timer timer = new Timer("Timer");
        
        long delay = miliSeconds; //set 3000
        timer.schedule(task, delay);
	}




    //*******************************************
    //           MAIN
    //*******************************************
    public static void main( String args[] ){

		// Reading the samples

		System.out.println(RED+"PREPROCESSING START ..."+RESET);
		
		Recuit.preProcessing(nomGen);

		System.out.println(RED+"... PREPROCESSING END");
		
		// Just for accounting how much time it takes to simulate
		long startTime=System.currentTimeMillis();

		// Definition of the starting temperature
		System.out.println("HEATUP LOOP START ..."+RESET);

		Recuit monRecuit = new Recuit();

		temperature=monRecuit.heatUpLoop();
		
		System.out.println(RED+"... HEATUP LOOP END");
		
		// Main loop to find the solution
		System.out.println("COOLING LOOP START ... "+RESET);
		// System.console().profile();

		monRecuit.coolingLoop(temperature);
		
		System.out.println(RED+"COOLING LOOP END ...");
		
		// Just for accounting how much time it takes to simulate
		long endTime=System.currentTimeMillis();
		
		// Print th information
		System.out.println(RED+"Simulation time : "+(endTime-startTime)/1000+" s "+RESET);

		// Close files
		monRecuit.postProcessing(nomGen);
    } //end main

}

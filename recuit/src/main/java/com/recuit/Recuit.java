package com.recuit;

import java.util.Random;
import javax.swing.WindowConstants;


public class Recuit {
    
    // Random value used for acceptance probability
    private static Random generateur = new Random(123);

    // Parameters to recuit
    private static final int nbTransitions = 2000;
    private static final double alpha = 0.999;
    private static final boolean minimisation = true;
    private static String nomGen = "matrixBigObstacles"; // name of the DATA file CHANGED
											 // Is reading the matrix as an input

    // Dimension of the problem
    private static int DIMENSION = 	50; // Number of nodes!!!!! NOW
    private Etat xi, xj;
    private static double temperature = 0.01;
    
	// Matrix stuff
	private static int neighborRadius = 2;

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
	}


	public static void preProcessing(String nomGen){
	// This function aim is to start the simulation
				
		Manager.initializeGrid(nomGen);
		Manager.setNeighborsGrid(neighborRadius);
		
	}

    private static void closeFiles(){
		// Close files

		// pwRes.close();
    }

	
    public void postProcessing(String nomGen){
		// Etat.postProcessing(nomGen);

		xi.saveEtat(nomGen);

		closeFiles();
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
    @SuppressWarnings("unchecked")
	public void coolingLoop(double Tinit) {
		//HeatUp heat = new HeatUp();		
		double yi=0.0,yj=0.0;
		// double proba;
		double T=Tinit;
		// Etat buffer;

		Etat bestEtat = new Etat<>();
		
		// Future work: use the ratio temperature to make the algorithm more accurate
		double ratioTemperature = 0.5;
	    
		// Generate a first random estate
	    xi.initAleatEtatDefiningN(DIMENSION);
		yi = xi.calculCritere(ratioTemperature);

		Etat.copy(xi, bestEtat);


	    do {
			ratioTemperature=T/Tinit;
		
			for (int i=0; i < nbTransitions; i++){
				
				Etat.copy(xi,xj);

				xj.genererVoisinOpt3();

				yj=xj.calculCritere(ratioTemperature);

				// Call the function you want to measure
				if (accept(yi,yj,T, minimisation, ratioTemperature)){
					
					yi=yj;

					Etat.copy(xj,xi);

				}
				
			}
			T=T*alpha;

			if (bestEtat.objective > xi.objective){
				Etat.copy(xi, bestEtat);
				System.out.println("**********BEST ETAT***********");
				System.out.println("T= " + T + " Tinitial= " + Tinit + " valeur critere " + xi.objective);
				System.out.println(xi.printEtat());
			}

			//pwRes.println(" " + yi + " "+ xi.objS1+ " " + xi.objS2);
	    } while(T>0.0001*Tinit);

		xi.calculCritere(ratioTemperature);

		System.out.println(PURPLE+"**************************************************");
		System.out.println("T= " + T + " Tinitial= " + Tinit + " valeur critere " + yi+RESET);
		System.out.println(xi.printEtat());
		System.out.println(YELLOW+"Final Etat: "+RESET);
		System.out.println(xi.printEtat());

    }


	
	public void plotResults(String title){
		
		this.example = new PlotGraph(title);
        this.example.setSize(800, 600);
        this.example.setLocationRelativeTo(null);
        this.example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.example.setVisible(true);
        
        // TimerTask task = new TimerTask() {
        // public void run() {
        //     example.setVisible(false);
		// 	// example.dispose();
        // }
        // };
        // Timer timer = new Timer("Timer");
        
        // long delay = 3000;
        // timer.schedule(task, delay);

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

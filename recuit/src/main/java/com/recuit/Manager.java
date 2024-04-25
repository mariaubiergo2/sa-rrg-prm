package com.recuit;

import java.util.Random;

public class Manager {

    // Static grid
    public static Grid grid;

    // For all the Etats
    public static Random generateur;

    // For getting a random neighbor
    public static Random random;

    

    /**
     * Pre-processing method.
	 * The aim of this function is to read the data in the .txt stored in the DATA folder
     */
    public static void initializeGrid(String nomGen) {
        
        String nomFicSamples = "src/DATA/" + nomGen + ".txt";

        grid = new Grid(nomFicSamples);

        generateur = new Random(126);

        random = new Random();

    }

	public static void setNeighborsGrid(int radius){
		grid.setPointsNeighbors(radius);
	}
}

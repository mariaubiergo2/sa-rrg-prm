package com.recuit;

public class Manager {

    // Static grid
    public static Grid grid;

    /**
     * Pre-processing method.
	 * The aim of this function is to read the data in the .txt stored in the DATA folder
     */
    public static void initializeGrid(String nomGen) {
        
        String nomFicSamples = "src/DATA/" + nomGen + ".txt";

        grid = new Grid(nomFicSamples);

    }

	public static void setNeighborsGrid(int radius){
		grid.setPointsNeighbors(radius);
	}
}

package com.recuit;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

import com.recuit.interfaces.PointInterface;

public class Manager <T extends PointInterface>{

    // Static grid
    public static Grid grid;

    // For all the Etats
    public static Random generateur;

    // For getting a random neighbor
    public static Random random;

    public static HashSet<String> basesDictionary;

    public static int nBases;


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

    public static void setConstants(){
        generateur = new Random(126);

        random = new Random();
    }

    public static void setBases(List<Point2D> basesList){
        nBases = basesList.size();

        basesDictionary = new HashSet<String>();

        for (Point2D base : basesList){
            basesDictionary.add(base.getId());
        }
    }

}

package com.recuit.spaceRecognition;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import com.recuit.Edge;
import com.recuit.Grid;
import com.recuit.Point2D;

public class SpaceRecognition {
    
    public static String filePathToMatrix;

    public static String fileEdgesInformation;



    public static void readSpace(){
        // Nous deja avons une function que analyse l'space
        Grid myGrid = new Grid(filePathToMatrix);
    
		String nomFichierOut = "src/DATA/"+fileEdgesInformation+".txt";
        
        try {
            FileWriter file = new FileWriter(nomFichierOut);
            PrintWriter fileOutput = new PrintWriter(file);
            
            String buffer;
            Edge edge;
            Point2D p1, p2;
        
            for (int i = 0; i<myGrid.getRows(); i++){
                for (int j=0; j<myGrid.getColumns(); j++){

                    p1 = new Point2D(i, j);
                
                    for (int p = 0; p<myGrid.getRows(); p++){
                        for (int c=0; c<myGrid.getColumns(); c++){

                            p2 = new Point2D(p, c);

                            edge = new Edge(p1, p2);

                            buffer = p1.getId()+" "+p2.getId();

                            boolean avail = myGrid.areConnectable(p1, p2);

                            if (avail){
                                buffer = buffer+" 1.0";
                            }else{
                                buffer = buffer+" 0.0";
                            }

                            buffer = buffer+" "+edge.distance()+" \n";

                            fileOutput.print(buffer);
                        }
                    }
                    
                }
            }
            fileOutput.close();
        }
        catch(IOException ioe) {
            System.err.println("Erreur fichier\n"+ioe);
        }
    }


    public static void main( String args[] ){
        // Generate the file or the db connection
        
        SpaceRecognition.fileEdgesInformation = "infoEdges";
        SpaceRecognition.filePathToMatrix = "src/DATA/matrixBigObstacles.txt";

        SpaceRecognition.readSpace();

    }

}

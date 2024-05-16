package com.recuit;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import org.bson.Document;

import com.mongodb.client.model.Filters;
import com.recuit.edgesInformation.MongoDBConnection;
import com.recuit.interfaces.SpaceInterface;

public class Grid implements SpaceInterface<Point2D> {

    private static final Random random = new Random(123);
    private int rows;
    private int columns;
    private double sizeX;
    private double sizeY;
    private HashMap<String, Point2D> freePoints;
	private HashMap<String, Point2D> obstaclePoints;

    public Grid(){

    }


    public Grid(int rows,int columns,double sizeX,double sizeY){
        this.rows = rows;
        this.columns = columns;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.freePoints = new HashMap<String, Point2D>();
		this.obstaclePoints = new HashMap<String, Point2D>();
    }


    // Constructor
	public Grid (String filePath) {
        
		try{
			File myFile = new File(filePath);
			Scanner reader = new Scanner(myFile);
			String[] s = reader.nextLine().split(" ");
			
			// File first row indicates the size
			this.rows = Integer.parseInt(s[0]);
			this.columns = Integer.parseInt(s[1]);
			this.sizeX = Double.parseDouble(s[2]);
			this.sizeY = Double.parseDouble(s[3]);

			// Dictionaries with points classificated
			this.freePoints = new HashMap<String, Point2D>();
			this.obstaclePoints = new HashMap<String, Point2D>();
			
			int line = 0;
			int value;
			while(reader.hasNextLine()){
				String[] data = reader.nextLine().split(" ");
				int col = 0;
				for (String d : data) {
						
					// Value is 1 (obstacle) or 0 (free space)
					value = Integer.parseInt(d);
					Point2D point = new Point2D(line, col);
										
					if (value == 1) {
						this.obstaclePoints.put(point.getId(), point);
					} else {
						this.freePoints.put(point.getId(), point);
					}
							
					// this.matrix[line][col] = value;
					col++;
				}
				line++;
			}
			reader.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("-- THERE HAS BEEN AN ERROR --");
			e.printStackTrace();
		}
	}


	public int getRows(){
		return this.rows;
	}

	
	public int getColumns(){
		return this.columns;
	}

    // Getter
	public HashMap<String, Point2D> getFreePoints() {
		return this.freePoints;
	}

	// Getter
	public HashMap<String, Point2D> getObstaclePoints() {
		return this.obstaclePoints;
	}


    @Override
    public double lebesgueMeasure() {
        return rows*sizeY*columns*sizeX;
    }

    @Override
    public double unitBall() {
        return Math.PI;
    }


	
	@Override
    public boolean areConnectableMongoDB(Point2D p1, Point2D p2){

		Edge edge = new Edge(p1, p2);

        Document doc = MongoDBConnection.collection.find(Filters.eq("identification", edge.getStart().getId()+" "+edge.getEnd().getId())).first();
        if (doc != null) {
            return doc.getBoolean("available");
            // System.out.println("_id: " + doc.getObjectId("_id")
            //     + ", name: " + doc.getString("name")
            //     + ", dateOfDeath: " + doc.getDate("dateOfDeath"));
            }
        else{
            Document docReversed = MongoDBConnection.collection.find(Filters.eq("identification", edge.getEnd().getId()+" "+edge.getStart().getId())).first();
            if (docReversed != null) {
                return docReversed.getBoolean("available");
                
            } else {
                boolean state = areConnectable((Point2D) edge.getEnd(), (Point2D) edge.getStart());
                Document newDoc = MongoDBConnection.generateDocument(edge, state);
				MongoDBConnection.uploadDocument(newDoc);
                return state;
            }
                
        }
    }


    @Override
    public boolean areConnectable(Point2D p1, Point2D p2) {

		// return true;

        List<Point2D> listCoordinate = new ArrayList<Point2D>();
		int x, y;
        int dx, dy;
        int incx, incy;
        int balance;

        int x1 = p1.getX();
        int x2 = p2.getX();
        int y1 = p1.getY();
        int y2 = p2.getY();

        if (x2 >= x1) {
            dx = x2 - x1;
            incx = 1;
        } else {
            dx = x1 - x2;
            incx = -1;
        }

        if (y2 >= y1) {
            dy = y2 - y1;
            incy = 1;
        } else {
            dy = y1 - y2;
            incy = -1;
        }

        x = x1;
        y = y1;

        if (dx >= dy) {
            dy <<= 1;
            balance = dy - dx;
            dx <<= 1;

            while (x != x2) {
                listCoordinate.add(new Point2D(y, x));
                if (balance >= 0) {
                    y += incy;
                    balance -= dx;
                }
                balance += dy;
                x += incx;
            }
            listCoordinate.add(new Point2D(y, x));
        } else {
            dx <<= 1;
            balance = dx - dy;
            dy <<= 1;

            while (y != y2) {
            	listCoordinate.add(new Point2D(y, x));
                if (balance >= 0) {
                    x += incx;
                    balance -= dy;
                }
                balance += dx;
                y += incy;
            }
            listCoordinate.add(new Point2D(y, x));
        }
        for(Point2D coordinate : listCoordinate) {
        	if(!isAvailableInteger(coordinate)) { //CHANGED WITH A !
        		return false; 
        	}
        }
		return true;
    }


    @Override
    public Point2D randomPoint() {
        return new Point2D(random.nextInt(columns), random.nextInt(rows));
    }


    public Point2D randomFreePointMAUVAIS() {

        int randomIndex = random.nextInt(freePoints.size());
        
        Point2D res = new Point2D();
        int i = 0;
		for (Map.Entry<String, Point2D> point : freePoints.entrySet()){
			
			if (i==randomIndex) {
				res = point.getValue();
			}
			i++;
		}
		return res;
    }


	@Override
    public Point2D randomFreePoint() {

        List<Point2D> pointsList = new ArrayList<>(freePoints.values());
    
		if (pointsList.isEmpty()) {
			return null;
		}
		
		// Generate a random index within the size of the list
		int randomIndex = random.nextInt(pointsList.size());
		
		// Return the Point2D at the random index
		return pointsList.get(randomIndex);
    }


    @Override
    public boolean isAvailable(Point2D p) {
        
        return this.freePoints.containsKey(p.getId());
        
    }

	public boolean isAvailableInteger(Point2D p) {
        
        return this.freePoints.containsKey((Math.round(p.getX()))+" "+Math.round(p.getY()));
        
    }


    @Override
    public boolean existsPoint(Point2D p) {

        if (!this.freePoints.containsKey(p.getId())){
			return this.obstaclePoints.containsKey(p.getId());
		} else {
			return true;
		}
    }


    
	@Override
	public void setPointsNeighbors(int radius){
		
		// String idPoint;
		Point2D point;
		int xPoint, yPoint;
		int initialX, finalX, initialY, finalY;
		Point2D neighbor;
		boolean isFreeNeighbor;
		
		for (HashMap.Entry<String, Point2D> entry : this.freePoints.entrySet()) {
			point = entry.getValue();
			// idPoint = entry.getKey();
			xPoint = point.getX();
			yPoint = point.getY();
			
						
			initialX = xPoint - radius;
			finalX = xPoint + radius;
			initialY = yPoint - radius;
			finalY = yPoint + radius;
						
			
			if (initialX < 0){
				initialX = 0;
			}
			if (finalX >= rows) {
				finalX = columns;
			}
			if (initialY < 0){
				initialY = 0;
			}
			if (finalY >= columns) {
				finalY = rows;
			}
					
			
			for (int x = initialX; x <= finalX; x++){
				for (int y = initialY; y <= finalY; y++){
					
					if (x == xPoint && y == yPoint){
						continue;
					}
				
					neighbor = new Point2D(x, y);
		
					isFreeNeighbor = this.freePoints.containsKey(neighbor.getId());
				
					if (isFreeNeighbor){
						// If you are my neighbor => i am your neighbor
						
						this.freePoints.get(point.getId()).setNeighbor(neighbor);
						this.freePoints.get(neighbor.getId()).setNeighbor(point);
					}
							
				}
			}
		}
	}
	

    public Point2D getPoint (int x, int y){
	
		try {
			String key = String.valueOf(x)+' '+String.valueOf(y);
			if (!this.freePoints.containsKey(key)){
				return this.obstaclePoints.get(key);
			} else {
				return this.freePoints.get(key);
			}
		} catch (NullPointerException e) {
			System.out.println("This point does not exist");
			return null;
		}
	}

    public Point2D getPointFromID (String id){
	
		try {
			if (!this.freePoints.containsKey(id)){
				return this.obstaclePoints.get(id);
			} else {
				return this.freePoints.get(id);
			}
		} catch (NullPointerException e) {
			System.out.println("This point does not exist");
			return null;
		}
	}
	
    
}

package com.recuit.edgesInformation;

import com.mongodb.*;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;

import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import com.recuit.Edge;
import com.recuit.Grid;
import com.recuit.Point2D;
import com.recuit.interfaces.PointInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;



public class MongoClientConnectionExample {

    public MongoCollection<Document> collection;


    public MongoClientConnectionExample(String uriString, String collectionString) {
        // Generate the connection with the MongoDB
        // There is any input bc by now I'm the one that has the db
        // String uri = "mongodb://localhost:27017";
        String uri = uriString;

        // Construct a ServerApi instance using the ServerApi.builder() method
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .serverApi(serverApi)
                .build();

        // Create a new client and connect to the server
        try (MongoClient mongoClient = MongoClients.create(settings)) {
            MongoDatabase database = mongoClient.getDatabase("tfg");
            try {
                // Send a ping to confirm a successful connection
                Bson command = new BsonDocument("ping", new BsonInt64(1));
                Document commandResult = database.runCommand(command);
                
                System.out.println("Pinged your deployment. You successfully connected to MongoDB!");

                // collection = database.getCollection("possible connections");
                collection = database.getCollection(collectionString);

                String nomFicSamples = "src/DATA/" + "matrixBigObstacles" + ".txt";

                // generateDB(nomFicSamples);

                Grid myGrid = new Grid(nomFicSamples);
        
        for (int i = 0; i<myGrid.getRows(); i++){
            for (int j=0; j<myGrid.getColumns(); j++){

                Point2D p1 = new Point2D(i, j);

                int bufi, bufj;

                bufi = i;
                bufj = j;
            
                for (int p = bufi; p<myGrid.getRows(); p++){
                    for (int c=bufj; c<myGrid.getColumns(); c++){

                        Point2D p2 = new Point2D(p, c);

                        Edge edge = new Edge(p1, p2);

                        boolean avail = myGrid.areConnectable(p1, p2);

                        Document doc = generateDocument(edge, avail);

                        // uploadDocument(doc);
                        InsertOneResult result = collection.insertOne(doc);
                    }
                }
                System.out.println("new i and j"+i+j);

                
            }
        }
                

            } catch (MongoException me) {
                System.err.println(me);
            }
        }
    }

    public void generateDB(String filePath){
        // Nous deja avons une function que analyse l'space
        Grid myGrid = new Grid(filePath);
        
        for (int i = 0; i<myGrid.getRows(); i++){
            for (int j=0; j<myGrid.getColumns(); j++){

                Point2D p1 = new Point2D(i, j);
            
                for (int p = 0; p<myGrid.getRows(); p++){
                    for (int c=0; c<myGrid.getColumns(); c++){

                        Point2D p2 = new Point2D(p, c);

                        Edge edge = new Edge(p1, p2);

                        boolean avail = myGrid.areConnectable(p1, p2);

                        Document doc = generateDocument(edge, avail);

                        // uploadDocument(doc);
                        InsertOneResult result = collection.insertOne(doc);
                    }
                }
                System.out.println("One done");

                
            }
        }
        
        


    }

    private Document generateDocument(Edge edge, boolean available){

        Document author = new Document("_id", new ObjectId())
                .append("identification", edge.getStart().getId()+" "+edge.getEnd().getId())
                .append("startEdge", edge.getStart().getId())
                .append("endEdge", edge.getEnd().getId())
                .append("distance", edge.distance())
                .append("available", available);

        return author;
    }


    private void uploadDocument(Document doc){

        InsertOneResult result = collection.insertOne(doc);

    }

    private void printDocument(){
        try{
        Document doc = collection.find(Filters.eq("name", "Gabriel García Márquez")).first();
        if (doc != null) {
            System.out.println("_id: " + doc.getObjectId("_id")
                + ", name: " + doc.getString("name")
                + ", dateOfDeath: " + doc.getDate("dateOfDeath"));
        }

        } catch (MongoException me) {
            System.err.println(me);
        }
    }


    public static void main(String[] args) {
        // First u generate the connection
        MongoClientConnectionExample myMongoDBConnection = new MongoClientConnectionExample("mongodb://localhost:27017", "con"); 
        // 2nd u generate the inputs
        // String nomFicSamples = "src/DATA/" + "matrixBigObstacles" + ".txt";

        // myMongoDBConnection.generateDB(nomFicSamples);

        // MongoDBConnection.generateMongoDBConnection("mongodb://localhost:27017", "possible connections"); 

        // Document doc = MongoDBConnection.generateDocument(new Edge(new Point2D(0, 0), new Point2D(2,2)), false);

        // MongoDBConnection.uploadDocument(doc);

        // Edge edge = new Edge(new Point2D(0,0), new Point2D(2, 2));

        // Document doc = MongoDBConnection.collection.find(Filters.eq("identification",edge.getStart().getId()+" "+edge.getEnd().getId())).first();
        // if (doc != null) {
        //     System.out.println(doc.getBoolean("available"));
        //     // System.out.println("_id: " + doc.getObjectId("_id")
        //     //     + ", name: " + doc.getString("name")
        //     //     + ", dateOfDeath: " + doc.getDate("dateOfDeath"));
        //     }
        // else{
        //     Document docReversed = MongoDBConnection.collection.find(Filters.eq("identification", edge.getEnd().getId()+" "+edge.getStart().getId())).first();
        //     if (docReversed != null) {
        //         System.out.println(docReversed.getBoolean("available"));
                
        //     } else {
        //         // boolean state = areConnectable((Point2D) edge.getEnd(), (Point2D) edge.getStart());
        //         // Document newDoc = MongoDBConnection.generateDocument(edge, state);
		// 		// MongoDBConnection.uploadDocument(newDoc);
        //         System.out.println("END");
        //     }
                
        // }


    }
    
}

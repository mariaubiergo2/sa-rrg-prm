package com.recuit.edgesInformation;

import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import com.recuit.Edge;
import com.recuit.Grid;
import com.recuit.Point2D;

public class MongoDBConnection {

    public static MongoClient mongoClient;
    public static MongoDatabase database;
    public static MongoCollection<Document> collection;


    public static void generateMongoDBConnection(String uriString, String collectionString) {
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
        try {
            mongoClient = MongoClients.create(settings);
            database = mongoClient.getDatabase("tfg");
            
                // Send a ping to confirm a successful connection
                // Bson command = new BsonDocument("ping", new BsonInt64(1));
                // Document commandResult = database.runCommand(command);
                // System.out.println("Pinged your deployment. You successfully connected to MongoDB!");

                // collection = database.getCollection("possible connections");
            collection = database.getCollection(collectionString);

        } catch (MongoException me) {
            System.err.println(me);
        }
     
    }

    public static void uploadDocument(Document doc){
        InsertOneResult result = collection.insertOne(doc);
    }


    public static Document generateDocument(Edge edge, boolean available){

        Document author = new Document("_id", new ObjectId())
                .append("identification", edge.getStart().getId()+" "+edge.getEnd().getId())
                .append("startEdge", edge.getStart().getId())
                .append("endEdge", edge.getEnd().getId())
                .append("distance", edge.distance())
                .append("available", available);

        return author;
    }



    


    public static void main( String args[] ){
        // Generate the file or the db connection
        
    }
}

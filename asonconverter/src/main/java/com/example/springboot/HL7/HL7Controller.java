package com.example.springboot.HL7;

import com.mongodb.MongoClient;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import gov.cdc.ncezid.eip.services.transform.helper.HL7Helper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("hl7")
public class HL7Controller {
    HL7Helper hl7Helper = HL7Helper.getInstance();

    private static MongoClient mongoClient = new MongoClient("localhost", 27017);

    @RequestMapping(value = "/add", method = RequestMethod.PUT)
    public ResponseEntity addHL7(@RequestBody String hl7String) {
        try {
            hl7Helper.parseSingleMessageToJSON(hl7String);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getLocalizedMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body("Create user successfully");
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public String getAllHL7() {
        MongoDatabase database = mongoClient.getDatabase("HL7");
        MongoCollection<Document> collection = database.getCollection("myHL7Collection");
        MongoCursor<Document> cursor = collection.find().iterator();
        String builder = "";
        Random rand = new Random();
        int i = 0;
        try {
            while (cursor.hasNext() && i<1) {
            	if (rand.nextInt(100) < 5) {
            		builder = cursor.next().toJson();
                    i++;
            	} else {
            		cursor.next();
            	}
            }

        } finally {
            cursor.close();
        }
        return builder;
    }
    
    @RequestMapping(value = "/get1", method = RequestMethod.GET)
    public String getOneHL7() {
        MongoDatabase database = mongoClient.getDatabase("HL7");
        MongoCollection<Document> collection = database.getCollection("myHL7Collection");
        MongoCursor<Document> cursor = collection.find().iterator();
        String builder = "";
        Random rand = new Random();
        int i = 0;
        try {
            while (cursor.hasNext() && i<1) {
            	if (rand.nextInt(100) < 5) {
            		builder = cursor.next().toJson();
                    i++;
            	} else {
            		builder = cursor.next().toJson();
            	}
                
            }

        } finally {
            cursor.close();
        }
        return builder;
    }
    
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public long queryHL7(@RequestBody KeyValue params) {
        MongoDatabase database = mongoClient.getDatabase("HL7");
        MongoCollection<Document> collection = database.getCollection("myHL7Collection");
        Pattern regex = Pattern.compile(params.value, Pattern.CASE_INSENSITIVE);
        Bson filter = Filters.eq(params.key, regex);
        long cursor = collection.count(filter);
        return cursor;
    }
    
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public int setSearch(@RequestBody String searchString) {
    	MongoDatabase database = mongoClient.getDatabase("HL7");
        MongoCollection<Document> collection = database.getCollection("myHL7Collection");
        int count = 0;
        MongoCursor<Document> cursor = null;
        try {
            cursor = collection.find(new Document("$text", new Document("$search", "\"" + searchString + "\"").append("$caseSensitive", new Boolean(false)).append("$diacriticSensitive", new Boolean(false)))).iterator();
 
            while (cursor.hasNext()) {
                cursor.next();
                count++;
            }
 
            cursor.close();
 
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
        	cursor.close();
        }
        return count;
    }
}

class KeyValue {
	@NotNull
	public String key;
	@NotNull
    public String value;
	
	public KeyValue() {}
	
    public KeyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }
}

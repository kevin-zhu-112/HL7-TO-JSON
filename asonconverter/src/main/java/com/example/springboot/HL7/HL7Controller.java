package com.example.springboot.HL7;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import gov.cdc.ncezid.eip.services.transform.helper.HL7Helper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

@RestController
@RequestMapping("hl7")
public class HL7Controller {
    int a = 0;
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
    public JsonArray getAllHL7() {
        MongoDatabase database = mongoClient.getDatabase("HL7");
        MongoCollection<Document> collection = database.getCollection("myHL7Collection");
        MongoCursor<Document> cursor = collection.find().iterator();
        JsonArrayBuilder builder = Json.createArrayBuilder();
        try {
            while (cursor.hasNext()) {
                builder.add(cursor.next().toJson());
            }

        } finally {
            cursor.close();
        }
        return builder.build();
    }



}

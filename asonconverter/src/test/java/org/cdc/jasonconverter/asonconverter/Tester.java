package org.cdc.jasonconverter.asonconverter;

import java.io.BufferedReader;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.json.JSONObject;

import gov.cdc.ncezid.eip.services.transform.helper.HL7Helper;

public class Tester {

	public static void main(String args[]) {
		try {
			
			String validMessage = "SFT|Mirth Corp.|2.0|Mirth Connect|789654||20110101" + 
					"";
			/*
			HL7Helper helper = HL7Helper.getInstance();
			helper.parseToJSON(validMessage);
			*/
			StringTokenizer token = new StringTokenizer("");
			String content = new Scanner(new File("./Project Test Files/2.5.1_GPS_Maxiumum Data Complex Microbiology.txt-revHEAD.svn000.tmp.txt")).useDelimiter("\\Z").next();
			//String content = validMessage;
			 System.out.println(content);
			HL7Helper helper = HL7Helper.getInstance();
			JSONObject object=helper.parseSingleMessageToJSON(content);
			MongoClient client = new MongoClient( "localhost" , 27017 );
			MongoCollection<Document> collection = client.getDatabase("mydb").getCollection("test");
			Document doc = Document.parse(object.toString());
			collection.insertOne(doc);
			System.out.println(object);
		} catch (IOException e) {
			System.out.println("error thrown:" + e);
		} catch (Exception e) {
			System.out.println("error2 thrown:" + e);
		}
	}
	
	
	 
}

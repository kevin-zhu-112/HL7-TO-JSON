package org.cdc.jasonconverter.asonconverter;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import gov.cdc.ncezid.eip.services.transform.helper.HL7Helper;
import org.bson.Document;
import org.json.JSONObject;

public class Tester {

	public static void main(String args[]) {
		try {
			
			File folder = new File("./Project Test Files/Test Files");
			HL7Helper helper = HL7Helper.getInstance();
			File[] files = folder.listFiles();
			
			/*
			 * Local MongoDB instance for testing
			 */
			MongoClient client = new MongoClient( "localhost" , 27017 );
			
			// Change to file name in folder
			int i = 0;
			MongoCollection<Document> collection = client.getDatabase("mydb").getCollection("test");
			for (File file : files) {
				String content = new Scanner(file).useDelimiter("\\Z").next();
				JSONObject object=helper.parseSingleMessageToJSON(content);
				
				
				// Test to see if helper methods worked properly
				if (object != null) {
					// Insert to local db instance
					Document doc = Document.parse(object.toString());
					collection.insertOne(doc);
					System.out.printf("Transferred file %d%n", i++);
				} else {
					System.out.printf("File %d failed%n", i++);
					break;
				}
				
			}
			
			
			
			
			System.out.println("Files transferred successfully");
		} catch (IOException e) {
			System.out.println("error thrown:" + e);
		} catch (Exception e) {
			System.out.println("error2 thrown:" + e);
			e.printStackTrace();
		}
	}
	
	
	 
}

package gov.cdc.ncezid.eip.services.transform.helper;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.camel.dataformat.xmljson.XmlJsonDataFormat;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import com.jayway.jsonpath.JsonPath;

import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.DefaultXMLParser;
import ca.uhn.hl7v2.parser.GenericParser;
import ca.uhn.hl7v2.parser.XMLParser;
import ca.uhn.hl7v2.util.Hl7InputStreamMessageIterator;
/*import gov.cdc.ncezid.eip.services.transform.exceptions.ServiceException;
import gov.cdc.ncezid.eip.services.transform.model.TransformModel;
*/

//@PropertySource("classpath:application.yml")
//@ConfigurationProperties
@Component
public class HL7Helper {
                
                private static final Logger logger = Logger.getLogger(HL7Helper.class);
                
                private static HL7Helper instance;

                private static MongoClient mongoClient = new MongoClient("localhost", 27017);
                
                @Value("${version}")
                private static String appVersion;
                
                
                   

                

                private XMLParser xmlParser = new DefaultXMLParser();
                XmlJsonDataFormat xmlJsonDataFormat = new XmlJsonDataFormat();
                
                private HL7Helper(@Value("${version}") String version) {
                                this.appVersion = version;
                
                                instance = this;
                }
                
                
                public static HL7Helper getInstance() {
                                if( instance == null) {
                                                return new HL7Helper(appVersion);
                                }
                                return instance;
                }
                
                public JSONObject parseSingleMessageToJSON(String message) throws Exception {
                   // ByteArrayInputStream is = new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8));
                    GenericParser parser = new GenericParser();
                    Message msg =parser.parse(message);
                    JSONObject json = HL7Helper.getInstance().parseToJSON(msg);
                    saveToDatabase(json);
                    return json;
                }
                public JSONObject parseToJSON(String message) throws Exception {

                	ByteArrayInputStream is = new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8));
                                Hl7InputStreamMessageIterator iter = new Hl7InputStreamMessageIterator(is);

                                if (iter.hasNext()) {
                                                Message msg = iter.next();
                                                JSONObject json = HL7Helper.getInstance().parseToJSON(msg);
                                                return json;
                                } else
                                                return null;
                }

                public JSONObject parseToJSON(Message msg) throws Exception {
                                try {
                                                // instantiate an XML parser
                                                xmlParser = new DefaultXMLParser();// new parser is assigned to clean out the previous message as this is a static class
                                                String xml = xmlParser.encode(msg);
                                                System.out.println("XML is " + xml);
                                                xmlJsonDataFormat = new XmlJsonDataFormat();
                                                xmlJsonDataFormat.setEncoding("UTF-8");
                                                xmlJsonDataFormat.setForceTopLevelObject(true);
                                                xmlJsonDataFormat.setTrimSpaces(false);
                                                xmlJsonDataFormat.setSkipNamespaces(true);
                                                xmlJsonDataFormat.setRemoveNamespacePrefixes(true);

                                                xmlJsonDataFormat.start();
                                                InputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
                                                String json = xmlJsonDataFormat.getSerializer().readFromStream(stream).toString();

                                                JSONObject obj = new JSONObject();
                                                JSONObject message = new JSONObject();
                                                JSONObject hl7 = new JSONObject();
                                                obj.put("message", message);
                                                message.put("HL7", hl7);

                                                // Get json message
                                                JSONObject jsonObject = new JSONObject(json);
                                                cleanJSON(jsonObject);
                                                Object messageVersion = parseJsonPath(jsonObject, "$.ORU_R01.MSH.MSH-12");
                                                if (messageVersion == null)
                                                                messageVersion = "unknown";
                                                hl7.put("version", messageVersion);
                                                hl7.put("source", jsonObject);
                                                // Add extractor information
                                                JSONObject extractor = new JSONObject();
                                                extractor.put("version", "1.0");
                                                extractor.put("hash", getMD5Hash(msg.toString()));
                                                extractor.put("timestamp", Instant.now().toString());
                                                obj.put("extractor", extractor);
                                                return obj;
                                } catch (Exception e) {
                                                throw new Exception(e);
                                }
                }
                
                
                private Object parseJsonPath(JSONObject jsonObject, String jsonPath) {
                                Object obj = null;
                                try {
                                                obj = JsonPath.parse(jsonObject.toString()).read(jsonPath);
                                } catch (Exception e) {
                                                logger.error(e);
                                }
                                return obj;
                }

                // Method used to remove "." from keys
                private void cleanJSON(Object object) throws Exception, JSONException {
                                if (object instanceof JSONObject)
                                                cleanJSON((JSONObject) object);
                }

                // Method used to remove "." from keys
                private void cleanJSON(JSONObject jsonObject) throws Exception, JSONException {

                                Set<String> keysToProcess = new HashSet();

                                // Check if keys contains a dot
                                Iterator<?> keys = jsonObject.keys();
                                while (keys.hasNext()) {
                                                String key = (String) keys.next();
                                                // If it's a child is a JSON object
                                                if (jsonObject.get(key) instanceof JSONObject)
                                                                cleanJSON((JSONObject) jsonObject.get(key));

                                                // If it's a child is a JSON array
                                                if (jsonObject.get(key) instanceof JSONArray) {
                                                                JSONArray array = (JSONArray) jsonObject.get(key);
                                                                for (int i = 0; i < array.length(); i++)
                                                                                cleanJSON(array.get(i));
                                                }

                                                if (key.contains("."))
                                                                keysToProcess.add(key);
                                }

                                for (String key : keysToProcess) {
                                                String newKey = key.replaceAll("\\.", "-");
                                                jsonObject.put(newKey, jsonObject.get(key));
                                                jsonObject.remove((String) key);
                                }
                }

                

                public String getMD5Hash(String msg) throws Exception {
                                MessageDigest md;
                                try {
                                                md = MessageDigest.getInstance("MD5");
                                } catch (NoSuchAlgorithmException e) {
                                                throw new Exception(e);
                                }
                                md.update(msg.getBytes());

                                byte[] byteData = md.digest();

                                // convert the byte to hex format method 1
                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < byteData.length; i++) {
                                                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
                                }

                                return sb.toString();

                }

                public String transform(Element element) throws Exception {
                                try {
                                                return transform(new DOMSource(element));
                                } catch (Exception e) {
                                                throw new Exception(e);
                                }
                }

                /*
                public String transform(Document xml) throws Exception {
                                try {
                                                return transform(new DOMSource(xml));
                                } catch (Exception e) {
                                                throw new Exception(e);
                                }
                }
                */

                private String transform(DOMSource source) throws TransformerFactoryConfigurationError, TransformerException {
                                TransformerFactory tf = TransformerFactory.newInstance();
                                Transformer transformer = tf.newTransformer();
                                StringWriter writer = new StringWriter();
                                transformer.transform(source, new StreamResult(writer));
                                return writer.getBuffer().toString();
                }

                private void saveToDatabase(JSONObject jsonObject) {
                    MongoDatabase database = mongoClient.getDatabase("HL7");
                    MongoCollection<Document> collection = database.getCollection("myHL7Collection");
                    Document doc = Document.parse(jsonObject.toString());
                    collection.insertOne(doc);
                }
                
                
}



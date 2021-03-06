# Release Notes HL7-to-JSON v1.0

# NEW FEATURES
HL7 to JSON Converter:<br />
Now compatible with HL7 v2.3.1 and v2.3<br />
Converter works on an entire folder of HL7 files instead of on a single file<br />
Queries can be implemented on the database<br />

UI: <br />
Settings page implemented in the UI<br />
Print from the UI<br />
Export files from the UI<br />

BUG FIXES<br />
Reading data from MongoDB no longer causes the records table to crash the UI<br />
Fixed HL7 version dependency issues in the converter<br />

KNOWN BUGS<br />
UNKNOWN keys appear in JSON files for HL7 2.3.1 files<br />
Exported/printed files from the UI not formatted properly<br />

# Install Guide
PREREQUISITES/DEPENDENCIES<br />
There are no hardware prerequisites to begin the installation process. The only thing that you need is a working computer. There are, however, a number of third party software that you must download for our software to function. <br />
The first is npm, or Node.js. This can be downloaded here: https://nodejs.org/en/download/. <br />
The second is Angular CLI which is installed by running npm install -g @angular/cli from the command line. <br />
A JDK is necessary to run the converter. This can be downloaded here https://www.oracle.com/technetwork/java/javase/downloads/index.html<br />
A MongoDB instance must also be present. MongoDB can be downloaded from https://www.mongodb.com/download-center<br />

DOWNLOAD<br />
Converter: https://github.com/kevin-zhu-112/HL7-TO-JSON/archive/master.zip<br />
UI: https://github.com/Bathenape/Convert-It-UI/archive/master.zip<br />

BUILD<br />
The converter can be built by opening the asonconverter folder in any Java IDE<br />
The UI does not need build instructions. The Convert-It-UI folder can be run directly from the command line<br />

INSTALLATION<br />
The converter must be run through an IDE and does not need installation<br />
Edit you environment variables to add the Convert-It-UI folder to your CLASSPATH<br />

RUNNING APPLICATION<br />
Run the UI by running npm run electron from the command line<br />
Run the converter by running Tester.java in the IDE. <br />

# Troubleshooting
For converting other HL7 versions besides v2.3.1 and 2.5 go to https://mvnrepository.com/artifact/ca.uhn.hapi and copy/paste the maven dependency for that specific version to the maven dependencies file in eclipse.<br />
The Current converter connects to a MongoDB instance at localhost:27027 to a database called HL7 and a collection called myHL7Repository. Modify the databaseConnect method in  HL7Helper.java to point to the appropriate MongoDB instance<br />
If the UI fails to run, run npm install from the command line before trying to run the UI.<br />

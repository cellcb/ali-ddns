"# ali-ddns" 
# ali-ddns

##package   
mvn clean  assembly:assembly  

##ddns.properties example  
accessKeyId=youkey  
accessKeySecret=youkeysecret  
domainName=demo.com

##run   
java -jar ali-ddns-1.0-SNAPSHOT-jar-with-dependencies.jar ddns.properties


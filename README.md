"# ali-ddns" 
# ali-ddns

##package 
mvn clean  assembly:assembly

##run 
java -jar ali-ddns-1.0-SNAPSHOT-jar-with-dependencies.jar ddns.properties

##ddns.properties example
accessKeyId=youkey
accessKeySecret=youkeysecret
domainName=demo.com

cd .. && mvn clean package
#cd deploy && docker-compose build && docker-compose up --force-recreate 
sudo -u postgres psql -d sec -a -f secure-server/src/main/java/META-INF/drop-tables.sql

java -jar secure-server/target/secure-server-1.0.jar --server.port=8080 &

# Server startup epoch count as an initial delay of 30 seconds while clients have an initial
# delay of 10 seconds. We can wait up to 20 seconds for the server to be up and running
# and then allow the client to start.
sleep 15
sudo -u postgres psql -d sec -a -f secure-server/src/main/java/META-INF/load-script.sql

java -jar secure-client/target/secure-client-1.0.jar --server.port=9001 1 &
java -jar secure-client/target/secure-client-1.0.jar --server.port=9002 2 &
java -jar secure-client/target/secure-client-1.0.jar --server.port=9003 3 &
java -jar secure-client/target/secure-client-1.0.jar --server.port=9004 4 &

read CONT

ps axf | grep secure-client | grep -v grep | awk '{print "kill -9 " $1}' | sh
ps axf | grep secure-server | grep -v grep | awk '{print "kill -9 " $1}' | sh

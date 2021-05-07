cd .. && mvn clean package

sudo -u postgres psql -d sec -a -f secure-server/src/main/java/META-INF/drop-tables.sql

java -jar secure-server/target/secure-server-1.0.jar --server.port=9201 1 | tee deploy/logs/server_log.txt &

# Wait for the server to initiate the database before inserting the data
sleep 15
sudo -u postgres psql -d sec -a -f secure-server/src/main/java/META-INF/load-script.sql
echo "SERVER DEPLOY DONE, START CLIENTS NOW"
read CONT

ps axf | grep secure-server | grep -v grep | awk '{print "kill -9 " $1}' | sh

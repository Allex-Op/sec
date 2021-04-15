cd ..

java -jar secure-client/target/secure-client-1.0.jar --server.port=9001 1 | tee deploy/logs/client1_log.txt &
java -jar secure-client/target/secure-client-1.0.jar --server.port=9002 2 | tee deploy/logs/client2_log.txt &
java -jar secure-client/target/secure-client-1.0.jar --server.port=9003 3 | tee deploy/logs/client3_log.txt &
java -jar secure-client/target/secure-client-1.0.jar --server.port=9004 4 | tee deploy/logs/client4_log.txt &

read CONT

ps axf | grep secure-client | grep -v grep | awk '{print "kill -9 " $1}' | sh


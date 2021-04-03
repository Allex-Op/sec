cd .. && mvn clean package
cd Deploy && docker-compose build && docker-compose up --force-recreate 

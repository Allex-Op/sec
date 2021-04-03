cd .. && mvn clean package
cd deploy && docker-compose build && docker-compose up --force-recreate

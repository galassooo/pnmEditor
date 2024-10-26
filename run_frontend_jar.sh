cd backend
mvn clean
cd ../frontend
mvn clean

cd ../backend
mvn install -DskipTests
cd ../frontend
mvn package -DskipTests

java -jar target/frontend-1.0-SNAPSHOT-jar-with-dependencies.jar


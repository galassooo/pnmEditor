cd backend
mvn clean
cd ../frontend
mvn clean

cd ../backend
mvn install -DskipTests
cd ../frontend
mvn package -DskipTests

java -jar target/frontend-1.0-SNAPSHOT-jar-with-dependencies.jar

#jar xf frontend/target/frontend-1.0-SNAPSHOT-jar-with-dependencies.jar META-INF/MANIFEST.MF
#cat META-INF/MANIFEST.MF



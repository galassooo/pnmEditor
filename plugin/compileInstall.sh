mkdir -p target/classes
javac -d target/classes src/main/java/module-info.java src/main/java/ch/supsi/SamplePlugin.java

# copia servizi
mkdir -p target/classes/META-INF/services
cp src/main/resources/META-INF/services/com.sun.source.util.Plugin target/classes/META-INF/services/
#crea il jar
jar cf target/plugin-1.0-SNAPSHOT.jar -C target/classes .

#installa jar
mvn install:install-file -Dfile=target/plugin-1.0-SNAPSHOT.jar -DgroupId=ch.supsi -DartifactId=plugin -Dversion=1.0-SNAPSHOT -Dpackaging=jar
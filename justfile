build:
    mvn compile -Dskiptests

package:
    mvn package -Dskiptests

restore:
    mvn install -DskipTests

test:
    mvn test -q
    mvn -p1 shared test

run-client:
    mvn -pl client exec:java -DskipTests

run-server:
    mvn -pl server exec:java -DskipTests

run-client-compiled: package
    java -jar client/target/client-jar-with-dependencies.jar
    
run-packaged-server:
    java -jar server/target/server.jar
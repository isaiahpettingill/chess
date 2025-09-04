build:
    mvn compile -Dskiptests

package:
    mvn package -Dskiptests

restore:
    mvn install -DskipTests

test:
    mvn test
    mvn -p1 shared test

run-client:
    mvn -pl client exec:java -DskipTests

run-server:
    mvn -pl server exec:java -DskipTests


build:
    mvn compile

package:
    mvn package -Dskiptests

restore:
    mvn install

test:
    mvn test
    mvn -p1 shared test

run-client:
    mvn -pl client exec:java

run-server:
    mvn -pl server exec:java


#!/bin/bash

#export JP_INPUT_PATH=/home/victor/aspirantura/sample_projects/Mines/
#export JP_INPUT_PATH=/home/victor/aspirantura/seams/src/main/java
mkdir detailedreport
mkdir detailedreport/ssa
mkdir detailedreport/deps
export JP_INPUT_PATH=./src/main/java
find $JP_INPUT_PATH -type f -name "*.deps" -delete
find $JP_INPUT_PATH -type f -name "*.jpg" -delete
java -classpath ~/.m2/repository/com/google/code/javaparser/javaparser/1.0.8/javaparser-1.0.8.jar:~/.m2/repository/net/sf/jung/jung-io/2.0.1/jung-io-2.0.1.jar:~/.m2/repository/net/sf/jung/jung-graph-impl/2.0.1/jung-graph-impl-2.0.1.jar:~/.m2/repository/net/sf/jung/jung-visualization/2.0.1/jung-visualization-2.0.1.jar:~/.m2/repository/net/sf/jung/jung-algorithms/2.0.1/jung-algorithms-2.0.1.jar:~/.m2/repository/net/sf/jung/jung-api/2.0.1/jung-api-2.0.1.jar:~/.m2/repository/commons-cli/commons-cli/1.2/commons-cli-1.2.jar:target/classes:target/resources org.creativelabs.MainApp $JP_INPUT_PATH
#java -cp `find /home/victor/aspirantura/sample_projects/jetty -name *.jar|sed -e :a -e "N;s/\n/:/; ta"`:target/seams-0.1.jar org.creativelabs.MainApp -d -f $JP_INPUT_PATH

TOMCAT_CLASSPATH=`find /home/victor/aspirantura/sample_projects/tomcat/output/build -name *.jar|sed -e :a -e "N;s/\n/:/; ta"`

#java -cp /home/victor/aspirantura/sample_projects/Mines:target/seams-0.1.jar org.creativelabs.MainApp -d -f $JP_INPUT_PATH
#java -cp target/seams-0.1.jar org.creativelabs.MainApp -d -f $JP_INPUT_PATH
java -cp $TOMCAT_CLASSPATH:target/seams-0.1.jar org.creativelabs.MainApp -d -f $JP_INPUT_PATH
#scala -cp lib/javaparser-1.0.8.jar:target/classes org.creativelabs.seams.ScalaApp

#!/bin/bash

export JP_INPUT_PATH=Sample.java
find $JP_INPUT_PATH -type f -name "*.deps" -delete
#java -classpath ~/.m2/repository/com/google/code/javaparser/javaparser/1.0.8/javaparser-1.0.8.jar:~/.m2/repository/net/sf/jung/jung-io/2.0.1/jung-io-2.0.1.jar:~/.m2/repository/net/sf/jung/jung-graph-impl/2.0.1/jung-graph-impl-2.0.1.jar:~/.m2/repository/net/sf/jung/jung-visualization/2.0.1/jung-visualization-2.0.1.jar:~/.m2/repository/net/sf/jung/jung-algorithms/2.0.1/jung-algorithms-2.0.1.jar:~/.m2/repository/net/sf/jung/jung-api/2.0.1/jung-api-2.0.1.jar:target/classes:target/resources org.creativelabs.MainApp $JP_INPUT_PATH
java -jar target/seams-0.1.jar $JP_INPUT_PATH

#scala -cp lib/javaparser-1.0.8.jar:target/classes org.creativelabs.seams.ScalaApp

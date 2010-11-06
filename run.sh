#!/bin/bash

java -classpath ~/.m2/repository/log4j/log4j/1.2.14/log4j-1.2.14.jar:lib/javaparser-1.0.8.jar:target/classes:target/resources org.creativelabs.MainApp Sample.java
#scala -cp lib/javaparser-1.0.8.jar:target/classes org.creativelabs.seams.ScalaApp

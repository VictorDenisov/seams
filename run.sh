#!/bin/bash

export JP_INPUT_PATH=Sample.java
find $JP_INPUT_PATH -type f -name "*.deps" -delete
java -classpath ~/.m2/repository/net/sourceforge/collections/collections-generic/4.01/collections-generic-4.01.jar:~/.m2/repository/concurrent/concurrent/1.3.4/concurrent-1.3.4.jar:~/.m2/repository/colt/colt/1.2.0/colt-1.2.0.jar:lib/'*':target/classes:target/resources org.creativelabs.MainApp $JP_INPUT_PATH

#scala -cp lib/javaparser-1.0.8.jar:target/classes org.creativelabs.seams.ScalaApp

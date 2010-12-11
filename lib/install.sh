#!/bin/bash

mvn install:install-file -DgroupId=jung -DartifactId=jung-visualization -Dversion=2.0.1 -Dpackaging=jar -Dfile=jung-visualization-2.0.1.jar
mvn install:install-file -DgroupId=jung -DartifactId=jung-algorithms -Dversion=2.0.1 -Dpackaging=jar -Dfile=jung-algorithms-2.0.1.jar
mvn install:install-file -DgroupId=jung -DartifactId=jung-api -Dversion=2.0.1 -Dpackaging=jar -Dfile=jung-api-2.0.1.jar
mvn install:install-file -DgroupId=jung -DartifactId=jung-graph-impl -Dversion=2.0.1 -Dpackaging=jar -Dfile=jung-graph-impl-2.0.1.jar
mvn install:install-file -DgroupId=jung -DartifactId=jung-io -Dversion=2.0.1 -Dpackaging=jar -Dfile=jung-io-2.0.1.jar


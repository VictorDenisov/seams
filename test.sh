#!/bin/bash

./run.sh > log.txt
diff -U3 log.txt sample.txt


#!/bin/bash

if [ $# -ne 0 ]; then
  echo $0: "usage: ./run_spark.sh input" 
  exit 1
fi

echo $SPARK_HOME

input1=src/main/resources/Posts.xml
output=output

echo Reading input from $input1
echo Writing output to $output

APP="
    target/scala-2.10/core-assembly-0.1.0.jar \
    $input1 \
    $output
    "


flag=0
if [ ${flag} == 0 ]; then
  # Run application locally
  $SPARK_HOME/bin/spark-submit \
    --class edu.vanderbilt.accre.stackex.StackExApp \
    --master "local[*]" \
    $APP
elif [ ${flag} == 1 ]; then
  # Run on a Spark standalone cluster in client deploy mode
  $SPARK_HOME/bin/spark-submit \
    --class WordCountApp \
    --master spark://vmp741.vampire:7077 \
    $APP
elif [ ${flag} == 2 ]; then
  # Run on a YARN cluster
  $SPARK_HOME/bin/spark-submit \
    --class WordCountApp \
    --master yarn \
    --deploy-mode cluster \
    --num-executors 25 \
    --executor-cores 3 \
    $APP
fi

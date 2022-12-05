#!/bin/bash

set -e

TAG=3.3.1-hadoop3

build() {
    NAME=$1
    IMAGE=bde2020/spark-$NAME:$TAG
    cd $([ -z "$2" ] && echo "./$NAME" || echo "$2")
    echo '--------------------------' building $IMAGE in $(pwd)
    docker build -t $IMAGE .
    cd -
}

if [ $# -eq 0 ]
  then
    echo "----------------->Build base:"
    build base
    echo "----------------->Build master:"
    build master
    echo "----------------->Build worker:"
    build worker
    echo "----------------->Build history server:"
    build history-server
    echo "----------------->Build history submit:"
    build submit
    echo "----------------->Build maven-template:"
    build maven-template template/maven
    echo "----------------->Build sbt-template:"
    build sbt-template template/sbt
    echo "----------------->Build python-template:"
    build python-template template/python
    echo "----------------->Build examples/python:"
    build python-example examples/python
  else
    build $1 $2
fi

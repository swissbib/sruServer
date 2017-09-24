#!/bin/bash

cd ..

mvn clean package
cp target/sru.war /usr/local/vufind/tomcat.sru/webapps

rm -r /usr/local/vufind/tomcat.sru/webapps/sru


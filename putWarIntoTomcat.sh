#!/bin/bash

apache="/home/aqali/Документы/apache-tomcat-8.5.32/"

sh ${apache}/bin/shutdown.sh

rm -rf ${apache}/webapps/*

cp sandbox.server/war/build/libs/sandbox-0.0.1.war ${apache}/webapps/
mv ${apache}/webapps/sandbox-0.0.1.war ${apache}/webapps/ROOT.war

sh ${apache}/bin/startup.sh

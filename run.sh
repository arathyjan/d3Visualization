#!/bin/sh
gradle build && java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar build/libs/ivr_domain.jar

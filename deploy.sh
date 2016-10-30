#!/bin/bash

./gradlew server:stage
echo 'Stop service'
ssh root@95.213.200.68 'service squatality stop'
echo 'Copy new version'
scp app.jar root@95.213.200.68:/opt/squatality/app.jar
echo 'Start service'
ssh root@95.213.200.68 'service squatality start'

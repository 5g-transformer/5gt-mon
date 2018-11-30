#!/bin/bash
path='../../../target/configmanager-1.0.0-SNAPSHOT-fat.jar'

java -jar $path --test &

pid="$!"

sleep 1

printf "\n\n==================\nSent Request\n==================\n\n"

printf "POST http://localhost:8989/prom-manager/dashboard\n"

curl -X POST http://localhost:8989/prom-manager/dashboard -H "Accept: application/json" -d @dashboard-request.json | jq .

printf "\n\n==================\n==================\n"

echo

printf "\n\n==================\nSent Request\n==================\n\n"

curl -X GET http://localhost:8989/prom-manager/dashboard -H "Accept: application/json"  | jq .

printf "\n\n==================\n==================\n"

echo

kill "$pid"

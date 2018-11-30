#!/bin/bash
path='../../../target/configmanager-1.0.0-SNAPSHOT-fat.jar'

java -jar $path --test &

pid="$!"

sleep 1

printf "\n\n==================\nSent Request\n==================\n\n"

printf "POST http://localhost:8989/prom-manager/exporter\n"

curl -X POST http://localhost:8989/prom-manager/exporter -H "Accept: application/json" -d '
{
  "name": "pippo",
  "endpoint": [
    {
      "address": "10.10.10.10",
      "port": 7623
    }
  ],
  "collectionPeriod": 15
}' | jq .

printf "\n\n==================\n==================\n"

echo

printf "\n\n==================\nSent Request\n==================\n\n"

curl -X GET http://localhost:8989/prom-manager/exporter -H "Accept: application/json"  | jq .

printf "\n\n==================\n==================\n"

echo

kill "$pid"

#!/bin/bash
path='../../../target/configmanager-1.0.0-SNAPSHOT-fat.jar'

java -jar $path --test &

pid="$!"

sleep 2

printf "\n\n==================\nSent Request\n==================\n\n"

printf "POST http://localhost:8989/prom-manager/alert\n"

curl -X POST http://localhost:8989/prom-manager/alert -H "Accept: application/json" -H "Content-Type: application/json" -d '
{
  "alertName": "name",
  "labels": [{
    "key": "custom_label",
    "value": "custom-value"
  },{
        "key": "custom_numbers_2",
        "value": "custom-value123"
  }],
  "query": "http_requests_total",
  "value": 120,
  "kind": "GEQ",
  "for": "130s",
  "severity": "error",
  "target": "http://localhost:8899/notf"
}' | jq .

printf "\n\n==================\n==================\n"

echo

printf "\n\n==================\nSent Request\n==================\n\n"

curl -X GET http://localhost:8989/prom-manager/alert -H "Accept: application/json"  | jq .

printf "\n\n==================\n==================\n"

echo

kill "$pid"
#!/bin/bash
for (( c=0; c<100; c++ ))
do
    curl -X POST http://localhost:8080/trimou-ping/rest/ping
done

# Template executions
# REST
for (( c=0; c<100; c++ ))
do
    curl http://localhost:8080/trimou-ping/rest/ping
done
# Servlet
for (( c=0; c<100; c++ ))
do
    curl http://localhost:8080/trimou-ping/ping
done

#!/bin/bash
set -e
echo "Running ONDC Integration Service container..."
docker run -d \
  --name ondc-integration-service \
  --network food-delivery-network \
  -p 8095:8095 \
  ondc-integration-service:latest
echo "Container started: ondc-integration-service"

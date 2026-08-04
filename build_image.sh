#!/bin/bash
set -e
echo "Building ONDC Integration Service Docker image..."
docker build -t ondc-integration-service:latest .
echo "Image built successfully: ondc-integration-service:latest"

#!/bin/bash
echo "Starting services..."

nohup java -jar url-service/target/url-service-1.0.0.jar > url-service.log 2>&1 &
echo "Started URL Service (PID: $!)"

nohup java -jar redirect-service/target/redirect-service-1.0.0.jar > redirect-service.log 2>&1 &
echo "Started Redirect Service (PID: $!)"

nohup java -jar analytics-service/target/analytics-service-1.0.0.jar > analytics-service.log 2>&1 &
echo "Started Analytics Service (PID: $!)"

sleep 10 # Wait for services to initialize before starting gateway
nohup java -jar api-gateway/target/api-gateway-1.0.0.jar > api-gateway.log 2>&1 &
echo "Started API Gateway (PID: $!)"

echo "All services started in background. Check logs (*.log) for details."

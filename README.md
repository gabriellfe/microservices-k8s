# Spring boot and Microservice with Kubernates 

## Running kubernates dashboard
- kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.5.0/aio/deploy/recommended.yaml

- kubectl proxy

- Get token : kubectl -n kubernetes-dashboard create token admin-user

## Understanding building individual Microservice

To run only build : mvn clean install

## Applying kubernates configuration to make pod running.

Go to k8s directory which contains 
- Apply de k8s
kubectl apply -f .\mysql-deployment.yaml -n my-namespace
kubectl apply -f .\config-maps.yaml -n my-namespace
kubectl apply -f .\service-registry-statefulset.yaml -n my-namespace
kubectl apply -f .\config-server-deployment.yaml -n my-namespace
kubectl apply -f .\cloud-gateway-deployment.yaml -n my-namespace
kubectl apply -f .\order-service-deployment.yaml -n my-namespace
kubectl apply -f .\payment-service-deployment.yaml -n my-namespace
kubectl apply -f .\product-service-deployment.yaml -n my-namespace

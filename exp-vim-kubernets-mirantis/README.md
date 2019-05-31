# Kubernetes exporter for Prometheus

For base was used 
https://github.com/coreos/prometheus-operator/tree/master/contrib/kube-prometheus/manifests

Nginx was used as an entry point and to change URL

# Installation

- 1)Change external IP in file "configuration/nginx-service.yaml" section "externalIPs:" to master node ip address 
- 2)Execute command to install
kubectl create -f ./configuration || true 
- 3)To check execute command
curl http://x.x.x.x:9090/metrics
- 4)Execute command to uninstall
kubectl delete -f ./configuration || true 
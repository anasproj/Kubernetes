# Commandes pour lancer le TP

1. Construire les images Docker :
docker build -t app-a:1.0 ./a
docker build -t app-b:1.0 ./b
docker build -t app-c:1.0 ./c

2. Importer les images dans K3s :
docker save app-a:1.0 | sudo k3s ctr images import -
docker save app-b:1.0 | sudo k3s ctr images import -
docker save app-c:1.0 | sudo k3s ctr images import -

3. Lancer Kubernetes :
kubectl apply -f k8s/
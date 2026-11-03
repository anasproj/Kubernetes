# Compte Rendu de TP : Micro-services et Kubernetes
SABER Anas

## Introduction
L'objectif de ce TP était de créer trois petites applications (A, B et C) dans des langages différents, de les lier entre elles, puis de les conteneuriser avec Docker avant de les déployer sur un cluster Kubernetes. 

## Exercice 1 : Installation du cluster
J'ai choisi d'utiliser **K3s** car c'est une version allégée de Kubernetes, parfaite pour tourner en local sur ma machine sans consommer trop de ressources. 
J'ai utilisé le script d'installation officiel (`curl -sfL https://get.k3s.io | sh -`).
En tapant la commande `kubectl get nodes`, mon nœud est bien apparu avec le statut `Ready`, ce qui m'a permis de passer à la suite.

## Exercice 2 et 3 : Les 3 applications et Docker Compose
J'ai développé trois petits services très simples :
* **App A (Python / Flask) :** C'est le point d'entrée. Elle reçoit la requête de l'utilisateur et appelle l'application B.
* **App B (Java / Spring Boot) :** C'est l'intermédiaire. Pour faire simple, j'ai tout mis dans un seul fichier. Elle appelle l'application C.
* **App C (Node.js / Express) :** C'est le back-end. J'ai utilisé le module `fs` de Node.js pour lire et écrire un nombre dans un fichier texte (`counter.txt`) pour faire office de compteur de visites.

**Variables d'environnement :** 
Pour respecter la consigne, les URL des services ne sont pas écrites en dur dans le code. J'ai utilisé `os.environ` en Python et `process.env` en Node.js pour récupérer l'adresse du service suivant.

**Docker Compose :**
J'ai fait un `Dockerfile` simple par application. Ensuite, j'ai tout relié dans un fichier `compose.yaml`. J'ai déclaré un volume Docker (`counter-data`) monté sur l'application C. 
*Vérification :* En éteignant tout avec `docker compose down` puis en rallumant avec `docker compose up`, mon compteur de visites n'est pas retombé à zéro. Le volume fonctionne bien.

## Exercice 4 : Déploiement sur Kubernetes
J'ai créé un dossier `k8s/` avec tous mes fichiers YAML pour déployer mon projet sur mon cluster K3s. J'ai tout mis dans un namespace dédié appelé `tp-k8s`.

Voici les éléments mis en place et pourquoi je les ai utilisés :

1. **ConfigMap (`01-namespace-config.yaml`) :** Je l'ai utilisé pour stocker les URL des services (SERVICE_B_URL, etc.). Ça permet de changer les adresses sans avoir à refaire les images Docker.
2. **PersistentVolumeClaim (`02-pvc.yaml`) :** J'ai demandé un volume de 1Go (ReadWriteOnce) pour l'application C. C'est l'équivalent du volume de Docker Compose, mais pour Kubernetes. C'est indispensable pour que le fichier du compteur survive si le pod redémarre.
3. **Services (`03-services.yaml`) :** J'ai créé un Service (ClusterIP) pour chaque application pour qu'elles puissent se trouver sur le réseau interne du cluster grâce à leur nom (DNS).
4. **Deployments (`04-deployments.yaml`) :** J'ai fait un déploiement pour chaque app. J'y ai ajouté :
   * Des limites de ressources (CPU/RAM) pour éviter qu'une app fasse planter ma machine.
   * Des `readinessProbe` (sondes) sur les ports web pour que Kubernetes sache quand l'application est vraiment prête à recevoir du trafic.
5. **Ingress (`05-ingress.yaml`) :** J'ai configuré un Ingress pour que l'application A soit accessible depuis l'extérieur de mon cluster (depuis mon navigateur web).
6. **HPA (`06-hpa.yaml`) :** J'ai mis un autoscaler sur l'application B. Si elle dépasse 50% d'utilisation CPU, Kubernetes créera automatiquement jusqu'à 3 répliques.

**Tests de validation :**
* J'ai supprimé volontairement le pod de l'application C avec `kubectl delete pod...`. Kubernetes en a recréé un nouveau instantanément, et en refaisant une requête, j'ai pu voir que mon compteur n'avait pas été réinitialisé grâce au PVC.
* La chaîne complète (A appelle B qui appelle C) fonctionne parfaitement depuis le navigateur.

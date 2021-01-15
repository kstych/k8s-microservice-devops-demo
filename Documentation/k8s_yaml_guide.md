# K8s Yaml Guide

### Creating Namespace
- Syntax for creating namespace using yaml

    ```
    apiVersion: v1
    kind: Namespace
    metadata:
    name: NamespaceName   #name of namespace

    ```

---
### Creating Secrets in k8s

- **Using Yaml**

    ```
    kind: Secret
    apiVersion: v1
    metadata:
    name: orp-registry-secret
    namespace: orp
    data:
    .dockerconfigjson: >-
        eyJhdXRocyI6eyJyZWdpc3RyeS5rOHMua3N0eWNoLmNvbSI6eyJ1c2VybmFtZSI6ImNpY2QiLCJwYXNzd29yZCI6InliOTczOHoiLCJhdXRoIjoiWTJsalpEcDVZamszTXpoNiJ9fX0=
    type: kubernetes.io/dockerconfigjson

    ```
- **Using kubectl**

    ```
    kubectl create secret docker-registry secret-name  --docker-server=serverAddr --docker-username=username --docker-password=password -n namespace

    eg:- 
    kubectl create secret docker-registry orp-registry-secret --docker-server=registry.k8s.kstych.com --docker-username=cicd --docker-password=***** -n cicd
    ```
---
### Creating External Storage in k8s

- **Create a Storage Class**

    ```
    apiVersion: storage.k8s.io/v1
    kind: StorageClass
    metadata:
    name: cicdstorage  # storage class name
    provisioner: nfspod  # Pod name 
    parameters:
    archiveOnDelete: “false”

    ```

- **Creating Persistent Volume for each service**

    ```
    apiVersion: v1
    kind: PersistentVolume
    metadata:
    name: cicdvolume  # name of volume of your choice
    labels:
        name: cicdvolume # name can be anything
    spec:
    storageClassName: cicdstorage # storage class name
    capacity:
        storage: 40Gi  # Storage Space
    accessModes:
        - ReadWriteOnce
    nfs:
        server: 192.168.1.2 # ip addres of nfs server 
        path: "/mnt/k8s/nfsroot"  # path to directory, make sure directory is available

    ```

- **Claiming Volume**

    ```
    apiVersion: v1
    kind: PersistentVolumeClaim
    metadata:
    name: gitea-data   # name of claim 
    namespace: cicd    # namespace 
    spec:
    storageClassName: cicdstorage   # storage class name
    accessModes:
    - ReadWriteOnce
    resources:
        requests:
        storage: 2Gi   # Claiming space
        
    ```
- Now we have to mount the claim in deployment where we need storage.
---
### Generating Ssl certificates 

- Run Below Command to add **cert-manager** in k8s.
    ```
    kubectl apply -f https://github.com/jetstack/cert-manager/releases/download/v1.1.0/cert-manager.yaml
    ```

- Create Cluster Issuer

    ```
    apiVersion: cert-manager.io/v1
    kind: ClusterIssuer
    metadata:
    name: cicd-letsencrypt           # Name of your choice
    namespace: cert-manager #retuired cert-manager (same as cert-manager)
    spec:
    acme:
        email: k8s@kstych.com         # provide email
        server: https://acme-v02.api.letsencrypt.org/directory
        privateKeySecretRef:
        name: cicd-letsencrypt-key   # key name of your choice
        solvers:
        - http01:
            ingress:
                class:  istio

    ```

- Generate certificatesfor all Domains

    ```
    apiVersion: cert-manager.io/v1
    kind: Certificate
    metadata:
    name: cicd-certs   # Name of your choice
    namespace: istio-system #retuired istio-system (same as ingress-gateway service)
    spec:
    dnsNames:
    - git.k8s.kstych.com         # First domain
    - registry.k8s.kstych.com    # Second domain etc
    secretName: cicd-certs-tls  
    issuerRef:
        kind: ClusterIssuer
        name: cicd-letsencrypt

    ```
---
### Istio-Ingress in k8s

- Creating Gateway

    ```
    apiVersion: networking.istio.io/v1alpha3
    kind: Gateway
    metadata:
    name: cicd-gateway        # Gateway name of your choice
    namespace: cicd
    spec:
    selector:
        istio: ingressgateway   # use Istio default gateway implementation
    servers:
    - port:
        number: 80
        name: http
        protocol: HTTP
        hosts:                 # Add All hosts of which you want ingress
        - git.k8s.kstych.com
        - registry.k8s.kstych.com
        - jenkins.k8s.kstych.com
        tls:
        httpsRedirect: true # sends 301 redirect for http requests
    - port:
        number: 443
        name: https-443
        protocol: HTTPS
        hosts:
        - git.k8s.kstych.com
        - registry.k8s.kstych.com
        - jenkins.k8s.kstych.com
        tls:
        mode: SIMPLE
        credentialName: cicd-certs-tls # This should match the Certifcate secretName
    ```

- Creating Virtual Service
   
   Now To we need to create a virtual service for each domains.

    ```
    apiVersion: networking.istio.io/v1alpha3
    kind: VirtualService
    metadata:
    name: gitea           # Name of your choice
    namespace: cicd 
    spec:
    hosts:
    - git.k8s.kstych.com   # Host name
    gateways:
    - cicd-gateway         # Gateway name should same as Gateway name
    http:
    - match:
        - uri:
            prefix: /       
        route:
        - destination:
            port:
            number: 3000         # Port of service
            host: gitea-service    # service name to host
    ```
---

### Creating Service

```
apiVersion: v1
kind: Service
metadata:
name: gitea-service  # service name of your choice
namespace: cicd      # namespace
spec:
selector:
    app: gitea         # name of deployment for which you want to create service
ports:
- name: http-gitea   # Port name
    port: 3000         # k8s cluster
    targetPort: 3000   # deployment exposed port

```
---

### Creating Deployment

```
apiVersion: apps/v1
kind: Deployment
metadata:
name: gitea-deployment    # Deployment Name of your choice
namespace: cicd          
labels:
# Label should be same as in selector section of service yaml of this deployment  
    app: gitea   
spec:
replicas: 1           # No. of replicas you want 
selector:
    matchLabels:
    app: gitea        # Same Label 
template:
    metadata:
    labels:
        app: gitea      # Same Label
    spec:
    containers:
    - name: gitea                 # Container name of your choice
        image: gitea/gitea:latest   # Image Location
        ports:
        - containerPort: 3000       # Ports to target
        name: gitea             
        volumeMounts:
        - mountPath: /data          # Mounting new /data folder
        name: gitea-data-vol      # Mount name
        subPath: gitea            # Creating subPath to separated from other StorageClass
    volumes:
    - name: gitea-data-vol        # Volume name = Mount Name
        persistentVolumeClaim:     
        claimName: gitea-data     # Volume claim name 

```

- If Image Registry is **private** then add below line in **spec section** to add secrets.

    ```
    imagePullSecrets:
        - name: orp-registry-secret # Name of Secret for registry
    ```
- Passinng Env variable in Deployment

    To pass Env variable to Deployment use below code at same level of image.

    ```
    env:
    - name: KEYCLOAK_USER        # Variable name
    value: "admin"             # Variable Value
    - name: KEYCLOAK_PASSWORD
    value: "*****"
            
    ```

# Container Orchestration Guide

This guide provides instructions on how to run the EatEasy application using **Kubernetes** or **Docker Swarm**.

## Prerequisites

- **Docker** installed and running.
- **Kubernetes CLI (kubectl)** applied (if using Kubernetes).
- A Kubernetes cluster (e.g., Docker Desktop k8s, Minikube, or Kind) OR Docker Swarm initialized.

---

## Option 1: Kubernetes

We have provided Kubernetes manifests in the `k8s/` directory.

### 1. Build Docker Images
First, you must build the images locally so the cluster can find them.

```bash
# Build Backend
cd backend
docker build -t eat-easy-backend:latest .
cd ..

# Build Frontend
cd Frontend
docker build -t eat-easy-frontend:latest .
cd ..
```

*Note: The Kubernetes configurations use `imagePullPolicy: Never`, which means it expects the images to be present locally. If using Minikube or Kind, you may need to load the images into the cluster node(s).*

### 2. Apply Configurations

Run the following commands to deploy the application:

```bash
# Apply Database Configuration
kubectl apply -f k8s/mysql.yaml

# Apply Backend Configuration
kubectl apply -f k8s/backend.yaml

# Apply Frontend Configuration
kubectl apply -f k8s/frontend.yaml
```

### 3. Access the Application

- **Frontend**: The frontend service is exposed as a `NodePort` on port `30080`.
  - Access via: `http://localhost:30080`
- **Backend**: The backend is running internally but can be port-forwarded if needed for debugging.

To check status:
```bash
kubectl get pods
kubectl get services
```

---

## Option 2: Docker Swarm

If you prefer to use Docker Swarm (which uses the `docker-compose.yml`), follow these steps.

### 1. Initialize Swarm
If you haven't already:
```bash
docker swarm init
```

### 2. Build Images
```bash
docker-compose build
```

### 3. Deploy Stack
```bash
docker stack deploy -c docker-compose.yml eateasy
```

### 4. Verify Deployment
```bash
docker service ls
```

To remove the stack:
```bash
docker stack rm eateasy
```

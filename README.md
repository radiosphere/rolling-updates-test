# Rolling Updates Test

This test shows a miniture version of Infinispan operating under rolling updates in kubernetes. It observes that once a rolling update has been performed there's a performance degradation that lasts for about 4 minutes before the cluster is up to normal performance again. This degradation is shown through performing the `count` command on all caches in this case, and fetching some of the keys. 

This test has been adjusted to work with `minikube`.

## Prerequisites

* GraalVM with java version 11.
* Docker
* minikube
* cli tool `jq`

## Setup

0. Install minikube, follow instructions [here](https://k8s-docs.netlify.app/en/docs/tasks/prepareSubscriptionstools/install-minikube/).
1. Start minikube: `minikube start`.
1. Install java using `SDKMAN`: `sdk install java 21.3.0.r11-grl`
2. Build the project: `./gradlew build`
3. Build a docker image: `docker build . -t local/rolling-updates-test:latest`
4. Load your image into minikube: `minikube image load local/rolling-updates-test:latest`
6. Apply the manifests in the `k8s` folder. `cd k8s && kubectl apply -f .`

## Run the Example

1. Wait until the service has started up on your machine 
2. Create a portforward to access your service: `kubectl port-forward services/rolling-updates-test 8080:80`
3. Create 100 sessions: `curl -XPOST localhost:8080/session-bulk/create/100`
4. Verify you have 100 sessions: `curl localhost:8080/sessions | jq '.total'`
5. Do a deployment rollout: `k rollout restart deployment rolling-updates-test`
5. If you execute the `curl localhost:8080/sessions | jq '.total'` command now it will take about 2-3 seconds, not sub 0.5 seconds as it did before.
6. Wait 3 minutes. Try the command again. Same result, 2-3 seconds.
7. After 4 minutes of the last started pod has started it's cache (about 4 min 30s) the time is back to be below 0.5 seconds.



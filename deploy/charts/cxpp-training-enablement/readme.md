## Pre-Requisites for Helm Deployments

```
* Docker image is built and published into docker repo. In our case it is containers.cisco.com
* Make sure you give public access to your repo so that SDP clusters can pull that artifact
* Make sure you have helm installed on your build machine eg: brew install kubernetes-helm
* Make sure you have the certs to connect to the Kubernetes cluster.
  
```

## Install

```
 * Copy the helm charts folder into your project root.
 * Run the following commands
 	* export KUBECONFIG= <cluster certs location>
 	* helm install --name <appname> ./helm-chart/
 	* kubectl get pods | grep <appname>
 	* kubectl describe pod <pod name> (The pod name would come from above step)
 	* kubectl logs -f <pod name>
```


## DELETE

```
* helm del --purge <pod name>


```

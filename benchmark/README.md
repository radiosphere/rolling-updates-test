Here are some tools that will help perform benchmarks on this poc. The idea is that metrics are reported using prometheus. If one wants to test the scaling up and down, one has to use kubectl. This tool will help to create a specific amount of sessions (either by decreasing or increasing the amount of sessions). This way we can see the cluster performance characteristics as the amount of sessions go up and down.

## Prerequisites

You need to have node and npm. Make sure to run `npm i` in this directory.

## Usage

Run the tool by doing:
```
node index.js <port> <num>
```

This will try to access a service on localhost at the given port (use port forwarding from Kubernetes to expose the service to you local machine.). The num is the number of sessions that should be live.

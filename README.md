# List of components

## Table of Contents

1. [Monitoring Core](#monitoring-core)
2. [Config Manager](#config-manager)
3. [Exporters](#exporters)
    1. [ODL exporter](#odl-ex)
    2. [ONOS exporter](#onos-ex)
    3. [OpenStack exporter](#os-ex)
    4. [Kubernetes exporter](#kube-ex)
    5. [Basic VM exporter](#basic-vm-ex)
4. [App instrumentation example](#app-ex)


## 1. Monitoring Core <a name="monitoring-core"></a>

This contains scripts and configuration file usable to deploy the basic infrastructure
needed for monitoring.

Folder: `mon-core`


## 2. Config Manager <a name="config-manager"></a>

The Prometheus Config Manager acts as a relay point enabling the configuration of a
Prometheus + Alertmanager + Grafana deployment from a single RESTful HTTP endpoint.

Folder: `config-manager-nxw`


## 3. Exporters <a name="exporters"></a>


### 3.1. ODL exporter <a name="odl-ex"></a>

ATOS TBC

Folder: `exp-odl-sonata-atos`


### 3.2. ONOS exporter <a name="onos-ex"></a>

SSSA TBC

Folder: `exp-onos-sssa`


### 3.3. OpenStack exporter <a name="os-ex"></a>

ATOS TBC

Folder: `exp-os-sonata-atos`


### 3.4. Kubernetes exporter <a name="kube-ex"></a>

MIRANTIS TBC

Folder: `exp-vim-kubernetes-mirantis`


### 3.5. Basic VM data exporter <a name="basic-vm-ex"></a>

This contains a compilation of general-purpose VM data exporters, appropriate for
general use or as a fallback.

Folder: `exp-vm-nxw`


## 4. App instrumentation example <a name="app-ex"></a>

This contains an example app instrumented to directly export data to prometheus.

Folder: `example-instrumented-app-nxw`

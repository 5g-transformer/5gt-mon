# 5G-Transformer monitoring core

## 1. Prometheus

### 1.1. Software download

The 5G-Transformer monitoring platform uses a standard installation of Prometheus as the underlying engine.
Prometheus can be installed either by using the precompiled binary from the [website](https://prometheus.io/download/)
or from source (`git clone https://github.com/prometheus/prometheus`).

Prometheus will then be available at the URL `<host IP>:9090`.

### 1.2. Configuration

There are two configuration files in the `prometheus` subfolder, which are examples meanungful for the 5G-Transformer use case.

The `prometheus.yml` file is the main prometheus configuration file. 
It contains the URL for the alertmanager instance to connect to, and the URLs and details 
of the scraping jobs to be performed by prometheus.

The `alert.rules` file defines the thresholds to be checked by prometheus for notification,
in particular it defines queries and identifiers for such thresholds. Whenever one of these
threshold is passed, the Alertmanager is notified for notification dispatching.

Both those files should be placed in the root folder of the Prometheus installation (i.e. side by side with
the `prometheus` executable file), overwriting the ones pre-provided if necessary.

## 2. Alertmanager

### 2.1. Installation

In a similar manner to Prometheus, a standard installation of the Alertmanager is enough for 5G-Transformer purposes.
Also as Prometheus, it can be installed as a [precompiled binary](https://prometheus.io/download/#alertmanager) or
from source (`git clone https://github.com/prometheus/alertmanager`).

The Alertmanager GUI is then available at `<host IP>:9093`.

### 2.2. Configuration

Also in this case, the `alertmanager` folder contains the `alertmanager.yml` file, which is a configuration file
for the Alertmanager showcasing the main features needed by the 5G-Transformer project.

It contains the definition of notification routes and receivers.
Whenever a notification is passed to the alertmanager for forwarding, it will check
its details and match it to one notification route (or the default one if none match).
It will then forward the notification to all receivers registered on that route.

In the file, we can see the definition of a webhook receiver registered to the default route,
hence Alertmanager forwards all notification in the form of HTTP POST requests to `localhost:8080`.

## 3. Grafana

### 3.1. Installation

The last component needed is a standard installation of Grafana, and it too can be obtained
in [pre-compiled form](https://grafana.com/grafana/download) or from source (`git clone https://github.com/grafana/grafana`).

The Grafana dashboards and web UI are available at `<host IP>:3000`.

### 3.2. Configuration

The only required Grafana configuration is the definition of the desired dashboards. 
This can be done via file-based configuration or via the configuration REST API, but the easiest way would be to
create the dashboards interactively from the web UI.

To do so, select an already existing dashboard to use as a basis or create a new one, 
add the required panels, if necessary, then resize them by dragging with the mouse.
Each panel can be customized by clicking on its title bar and selecting "Edit". 
This will open up a view which will allow the user to, for example, change the query graphed into the panel,
on top of changing the panel title and other details.
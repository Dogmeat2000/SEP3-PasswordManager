package dk.sep3.passwordmanager.loadBalancer.application;

import dk.sep3.passwordmanager.loadBalancer.factory.LoadBalancerFactory;
import dk.sep3.passwordmanager.loadBalancer.service.LoadBalancerMonitor;
import dk.sep3.passwordmanager.loadBalancer.service.LoadBalancerService;

public class LoadBalancerApplication {
    private LoadBalancerMonitor monitor;
    private LoadBalancerFactory factory;

    public LoadBalancerApplication(LoadBalancerFactory factory) {
        this.factory = factory;
    }

    public void start() {
        LoadBalancerService initialLoadBalancer = factory.createLoadBalancer();

        monitor = new LoadBalancerMonitor(factory);
        monitor.addLoadBalancer(initialLoadBalancer);

        monitor.startMonitoring();
    }
}

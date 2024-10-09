package dk.sep3.passwordmanager.loadBalancer.service;

import dk.sep3.passwordmanager.loadBalancer.factory.LoadBalancerFactory;

import java.util.ArrayList;

public class LoadBalancerMonitor {
    private ArrayList<LoadBalancerService> loadBalancers = new ArrayList<>();
    private LoadBalancerFactory factory;
    private final int MAX_LOAD = 100; // Replace with actual max number

    public LoadBalancerMonitor(LoadBalancerFactory factory) {
        this.factory = factory;
    }

    public void startMonitoring() {
        // TODO implement monitoring, and create new loadbalancer if overloaded
    }

    // Adds loadbalancer to list of loadbalancers
    public void addLoadBalancer(LoadBalancerService loadBalancer) {
        loadBalancers.add(loadBalancer);
    }

    // Checks if loadbalancer is overloaded
    private boolean isOverloaded(LoadBalancerService loadBalancer) {
        return loadBalancer.getCurrentLoad() > MAX_LOAD;
    }

    // Creates a new loadbalancer
    private void createNewLoadBalancer() {
        LoadBalancerService newLoadBalancer = factory.createLoadBalancer();
        loadBalancers.add(newLoadBalancer);
    }

}

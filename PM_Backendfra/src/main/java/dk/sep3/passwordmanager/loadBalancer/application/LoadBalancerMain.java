package dk.sep3.passwordmanager.loadBalancer.application;
import dk.sep3.passwordmanager.loadBalancer.factory.LoadBalancerFactory;

public class LoadBalancerMain {
    public static void main(String[] args) {
        LoadBalancerFactory factory = new LoadBalancerFactory();
        LoadBalancerApplication app = new LoadBalancerApplication(factory);
        app.start();
    }
}

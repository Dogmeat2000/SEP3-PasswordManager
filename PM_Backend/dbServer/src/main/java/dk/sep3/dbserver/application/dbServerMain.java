package dk.sep3.dbserver.application;

public class dbServerMain
{
  public static void main(String[] args) {
    // TODO: This class is currently unused. So long as we only need 1 database server this
    //  class will remain unused, as the main Spring Boot Application simply automatically
    //  instantiates the @GrpcService marked classes and controls them.
  }
}

/* Note: When establishing connection with the database server,
* please connect via the 'grpc.service' implementations to allow for Spring Boot
* GRPC to handle the low-level stuff between the clients (loadbalancers) and server (database server).
* Add additional methods as required to the .proto file, since this acts similar to an interface contract (protocol contract).*/
package dk.sep3.dbserver.grpc.service;

import io.grpc.*;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

// Read more here: https://grpc.io/docs/guides/interceptors/
// Metrics can be accessed from here: http://localhost:8080/actuator/metrics/grpc.server.active.connections (replace port number)
@Component
public class ConnectionTracker implements ServerInterceptor
{
  private static final Logger logger = LoggerFactory.getLogger(ConnectionTracker.class);
  private final AtomicInteger activeConnections = new AtomicInteger(0);

  @Autowired
  public ConnectionTracker(MeterRegistry meterRegistry){
    // Register the active connections gauge to be monitored
    Gauge.builder("grpc.server.active.connections", activeConnections::get)
        .description("Number of active gRPC connections")
        .register(meterRegistry);
  }


  @Override public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call,
      Metadata headers,
      ServerCallHandler<ReqT, RespT> next) {

    //When a new connection is registered, increase number of active connections by 1:
    activeConnections.incrementAndGet();

    ServerCall.Listener<ReqT> listener = next.startCall(call, headers);

    return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(listener) {
      @Override
      public void onComplete() {
        super.onComplete();
        // Decrease number of active connections ny 1, when the connection is closed
        activeConnections.decrementAndGet();
      }

      @Override
      public void onCancel() {
        super.onCancel();
        // Decrease number of active connections ny 1, when the connection is closed
        activeConnections.decrementAndGet();
      }
    };
  }
}

package dk.sep3.dbserver.grpc;

import dk.sep3.dbserver.grpc.service.ConnectionTracker;
import io.micrometer.core.instrument.MeterRegistry;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfig {

  @Bean
  @GrpcGlobalServerInterceptor
  public ConnectionTracker connectionTrackingInterceptor(MeterRegistry meterRegistry) {
    return new ConnectionTracker(meterRegistry);
  }
}

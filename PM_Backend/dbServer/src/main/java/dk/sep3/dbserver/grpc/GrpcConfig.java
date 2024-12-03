package dk.sep3.dbserver.grpc;

import dk.sep3.dbserver.grpc.service.ConnectionTracker;
import io.micrometer.core.instrument.MeterRegistry;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**<p>Responsible for defining @Configuration related information for the Grpc Connection Metrics tracker.</p>*/
@Configuration
public class GrpcConfig {

  /**<p>Responsible for defining @Configuration related information for the Grpc Connection Metrics tracker.</p>
   * @param meterRegistry The MeterRegistry implementation responsible for providing low-level gRPC connection metrics.
   * @return A reference to a ConnectionTracker, which provides high-level access to gRPC connection metrics and health diagnostics. */
  @Bean
  @GrpcGlobalServerInterceptor
  public ConnectionTracker connectionTrackingInterceptor(MeterRegistry meterRegistry) {
    return new ConnectionTracker(meterRegistry);
  }
}

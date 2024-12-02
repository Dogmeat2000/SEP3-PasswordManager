package dk.sep3.webapi.network;

import grpc.PasswordManagerServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configuration class implemented for added flexibility, better scalability and easier testability of the gRPC connection. */
@Configuration
public class GrpcConfig
{
  private ManagedChannel channel;

  /**
   * Creates and configures a gRPC ManagedChannel to connect to the Password Manager server.
   *
   * @return The ManagedChannel configured to connect to localhost at port 8090.
   */
  @Bean
  public ManagedChannel grpcChannel() {
    channel = ManagedChannelBuilder.forAddress("localhost", 8090)
        .usePlaintext() // Use plaintext for testing purposes
        .build();
    return channel;
  }

  /**
   * Creates a gRPC blocking stub to communicate with the Password Manager service.
   *
   * @param channel The ManagedChannel used to connect to the gRPC server.
   * @return The blocking stub for making synchronous calls to the Password Manager service.
   */
  @Bean
  public PasswordManagerServiceGrpc.PasswordManagerServiceBlockingStub passwordManagerServiceBlockingStub(ManagedChannel channel) {
    return PasswordManagerServiceGrpc.newBlockingStub(channel);
  }

  /**
   * Shuts down the gRPC channel if it is open.
   */
  public void shutdownChannel() {
    if (channel != null && !channel.isShutdown()) {
      channel.shutdown();
    }
  }
}

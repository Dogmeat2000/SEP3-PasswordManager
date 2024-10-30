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

  @Bean
  public ManagedChannel grpcChannel() {
    channel = ManagedChannelBuilder.forAddress("localhost", 8090)
        .usePlaintext() // Use plaintext for testing purposes
        .build();
    return channel;
  }

  @Bean
  public PasswordManagerServiceGrpc.PasswordManagerServiceBlockingStub passwordManagerServiceBlockingStub(ManagedChannel channel) {
    return PasswordManagerServiceGrpc.newBlockingStub(channel);
  }

  public void shutdownChannel() {
    if (channel != null && !channel.isShutdown()) {
      channel.shutdown();
    }
  }
}

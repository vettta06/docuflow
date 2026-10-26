package com.accounting.platform.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {

  @Bean(destroyMethod = "shutdown")
  public ManagedChannel pythonGrpcChannel() {
    String host = System.getenv().getOrDefault("PYTHON_SERVICE_HOST", "localhost");
    int port = Integer.parseInt(System.getenv().getOrDefault("PYTHON_SERVICE_PORT", "50051"));

    return ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
  }
}

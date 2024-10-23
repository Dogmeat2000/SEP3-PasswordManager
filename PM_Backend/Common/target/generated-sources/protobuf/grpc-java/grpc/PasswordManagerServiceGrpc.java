package grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.63.0)",
    comments = "Source: PasswordManagerService.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class PasswordManagerServiceGrpc {

  private PasswordManagerServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "grpc.PasswordManagerService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<grpc.GenericRequest,
      grpc.GenericResponse> getHandleRequestMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "HandleRequest",
      requestType = grpc.GenericRequest.class,
      responseType = grpc.GenericResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<grpc.GenericRequest,
      grpc.GenericResponse> getHandleRequestMethod() {
    io.grpc.MethodDescriptor<grpc.GenericRequest, grpc.GenericResponse> getHandleRequestMethod;
    if ((getHandleRequestMethod = PasswordManagerServiceGrpc.getHandleRequestMethod) == null) {
      synchronized (PasswordManagerServiceGrpc.class) {
        if ((getHandleRequestMethod = PasswordManagerServiceGrpc.getHandleRequestMethod) == null) {
          PasswordManagerServiceGrpc.getHandleRequestMethod = getHandleRequestMethod =
              io.grpc.MethodDescriptor.<grpc.GenericRequest, grpc.GenericResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "HandleRequest"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GenericRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.GenericResponse.getDefaultInstance()))
              .setSchemaDescriptor(new PasswordManagerServiceMethodDescriptorSupplier("HandleRequest"))
              .build();
        }
      }
    }
    return getHandleRequestMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static PasswordManagerServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PasswordManagerServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PasswordManagerServiceStub>() {
        @java.lang.Override
        public PasswordManagerServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PasswordManagerServiceStub(channel, callOptions);
        }
      };
    return PasswordManagerServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static PasswordManagerServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PasswordManagerServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PasswordManagerServiceBlockingStub>() {
        @java.lang.Override
        public PasswordManagerServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PasswordManagerServiceBlockingStub(channel, callOptions);
        }
      };
    return PasswordManagerServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static PasswordManagerServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PasswordManagerServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PasswordManagerServiceFutureStub>() {
        @java.lang.Override
        public PasswordManagerServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PasswordManagerServiceFutureStub(channel, callOptions);
        }
      };
    return PasswordManagerServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void handleRequest(grpc.GenericRequest request,
        io.grpc.stub.StreamObserver<grpc.GenericResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getHandleRequestMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service PasswordManagerService.
   */
  public static abstract class PasswordManagerServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return PasswordManagerServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service PasswordManagerService.
   */
  public static final class PasswordManagerServiceStub
      extends io.grpc.stub.AbstractAsyncStub<PasswordManagerServiceStub> {
    private PasswordManagerServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PasswordManagerServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PasswordManagerServiceStub(channel, callOptions);
    }

    /**
     */
    public void handleRequest(grpc.GenericRequest request,
        io.grpc.stub.StreamObserver<grpc.GenericResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getHandleRequestMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service PasswordManagerService.
   */
  public static final class PasswordManagerServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<PasswordManagerServiceBlockingStub> {
    private PasswordManagerServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PasswordManagerServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PasswordManagerServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public grpc.GenericResponse handleRequest(grpc.GenericRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getHandleRequestMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service PasswordManagerService.
   */
  public static final class PasswordManagerServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<PasswordManagerServiceFutureStub> {
    private PasswordManagerServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PasswordManagerServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PasswordManagerServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<grpc.GenericResponse> handleRequest(
        grpc.GenericRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getHandleRequestMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_HANDLE_REQUEST = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_HANDLE_REQUEST:
          serviceImpl.handleRequest((grpc.GenericRequest) request,
              (io.grpc.stub.StreamObserver<grpc.GenericResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getHandleRequestMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              grpc.GenericRequest,
              grpc.GenericResponse>(
                service, METHODID_HANDLE_REQUEST)))
        .build();
  }

  private static abstract class PasswordManagerServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    PasswordManagerServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return grpc.PasswordManagerServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("PasswordManagerService");
    }
  }

  private static final class PasswordManagerServiceFileDescriptorSupplier
      extends PasswordManagerServiceBaseDescriptorSupplier {
    PasswordManagerServiceFileDescriptorSupplier() {}
  }

  private static final class PasswordManagerServiceMethodDescriptorSupplier
      extends PasswordManagerServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    PasswordManagerServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (PasswordManagerServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new PasswordManagerServiceFileDescriptorSupplier())
              .addMethod(getHandleRequestMethod())
              .build();
        }
      }
    }
    return result;
  }
}

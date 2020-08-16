package com.somatt.client;

import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import somatt.hello.GreeterGrpc.GreeterBlockingStub;
import somatt.hello.GreeterGrpc;
import somatt.hello.HelloReply;
import somatt.hello.HelloRequest;

public class HelloClient {

    private final ManagedChannel managedChannel;
    private final GreeterBlockingStub greeterBlockingStub;

    public HelloClient(String host, int port){
        managedChannel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        greeterBlockingStub = GreeterGrpc.newBlockingStub(managedChannel);
    }

    public void shutdown() throws InterruptedException {
        managedChannel.awaitTermination(5, TimeUnit.SECONDS);
    }

    public void createHello(){
        HelloRequest request = HelloRequest.newBuilder().setName("11111").build();
        HelloReply response = HelloReply.getDefaultInstance();

        response = greeterBlockingStub.sayHello(request);
        System.out.println(response.getMessage());
    }

    public static void main(String[] args) throws InterruptedException {
        HelloClient client = new HelloClient("0.0.0.0", 8080);

        try {
            client.createHello();
        } finally {
            client.shutdown();
        }

    }
}

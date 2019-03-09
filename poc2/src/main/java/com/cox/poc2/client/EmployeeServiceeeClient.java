package com.cox.poc2.client;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import com.cox.grpc.messages.EmployeeServiceGrpc;
import com.cox.grpc.messages.Messages;
import com.cox.grpc.messages.Messages.AddPhotoReesponse;
import com.cox.grpc.messages.Messages.AddPhotoRequest;
import com.cox.grpc.messages.Messages.EmployeeResponse;
import com.google.protobuf.ByteString;

import io.grpc.Channel;
import io.grpc.ClientInterceptors;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.MetadataUtils;
import io.grpc.stub.StreamObserver;

public class EmployeeServiceeeClient {

	public static void main(String[] args) throws Exception {
		File cert = new File("certificates/certificate.pem");
		//In case of insecure connection use MannagedChannelBuilder
		ManagedChannel channel = NettyChannelBuilder.
				forAddress("localhost", 9000)
				.sslContext(GrpcSslContexts.forClient().trustManager(cert).build())
				.build();
		
		//Blocking client
		EmployeeServiceGrpc.EmployeeServiceBlockingStub blockingClient =  
				EmployeeServiceGrpc.newBlockingStub(channel);
		
		//Non-Blocking client
				EmployeeServiceGrpc.EmployeeServiceStub nonBlockingClient =  
						EmployeeServiceGrpc.newStub(channel);
		
				
		sendMetadata(blockingClient);
		System.out.println("______________");
		getByBadgeNumber(blockingClient);
		System.out.println("______________");
		getAll(blockingClient);
		System.out.println("______________");
		addPhoto(nonBlockingClient);
				
		Thread.sleep(500);
		channel.shutdown();
		channel.awaitTermination(1, TimeUnit.SECONDS);

	}
	
	
	//Use the channel only if you are going to send data
	private static void sendMetadata(EmployeeServiceGrpc.EmployeeServiceBlockingStub blocingClient) {
		Metadata md = new Metadata();
		md.put(Metadata.Key.of("username", Metadata.ASCII_STRING_MARSHALLER), "Orlas");
		md.put(Metadata.Key.of("durec", Metadata.ASCII_STRING_MARSHALLER), "Dir");
		//Important add metadata
		Channel ch = ClientInterceptors.intercept(blocingClient.getChannel(), 
				MetadataUtils.newAttachHeadersInterceptor(md));
		
		EmployeeResponse value = blocingClient.withChannel(ch).getByBadgeNumber(Messages.GetByBadgeNumberRequest.newBuilder().build());
		System.out.println(value.getEmployee().toString());
	}
	
	
	private static void getByBadgeNumber(EmployeeServiceGrpc.EmployeeServiceBlockingStub blocingClient) {
		EmployeeResponse value = blocingClient.getByBadgeNumber(Messages.GetByBadgeNumberRequest.newBuilder().setBadgeNumber(2000).build());
		System.out.println(value.getEmployee().toString());
	}
	
	private static void getAll(EmployeeServiceGrpc.EmployeeServiceBlockingStub blocingClient) {
		 Iterator<EmployeeResponse> iterator = blocingClient.getAll(Messages.GetAllRequest.newBuilder().build());
		while(iterator.hasNext()) {
			System.out.println(iterator.next().getEmployee());
		}
	}
	
	private static void addPhoto(EmployeeServiceGrpc.EmployeeServiceStub nonBlockingClient) throws Exception {
		StreamObserver<AddPhotoRequest> stream = 
		nonBlockingClient.addPhoto(new StreamObserver<Messages.AddPhotoReesponse>() {

			@Override
			public void onNext(AddPhotoReesponse response) {
				System.out.println(response.getIsOk());
				
			}

			@Override
			public void onError(Throwable t) {
				System.out.println(t);
				
			}

			@Override
			public void onCompleted() {
				// TODO Auto-generated method stub
				
			}
		});
		
		FileInputStream fs = new FileInputStream("img/download.png");
		while(true) {
			byte[] data = new byte[64*1024];
			int bytesRead = fs.read(data);
			if(bytesRead == -1) {
				break;
			}
			if(bytesRead < data.length) {
				
				byte[] newData = new byte[bytesRead];
				System.arraycopy(data, 0, newData, 0, bytesRead);
				System.out.println(newData);
				data = newData;
			}
			
			AddPhotoRequest request = AddPhotoRequest.newBuilder().setData(ByteString.copyFrom(data)).build();
			stream.onNext(request);
		}
		fs.close();
		
		stream.onCompleted();
		
		
	}
	
	
	

}

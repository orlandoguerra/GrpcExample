package com.cox.poc2;

import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

import java.util.ArrayList;
import java.util.List;

import com.cox.grpc.messages.EmployeeServiceGrpc.EmployeeServiceImplBase;
import com.cox.grpc.messages.Messages;
import com.cox.grpc.messages.Messages.AddPhotoReesponse;
import com.cox.grpc.messages.Messages.AddPhotoRequest;
import com.cox.grpc.messages.Messages.EmployeeResponse;
import com.cox.grpc.messages.Messages.GetAllRequest;
import com.cox.grpc.messages.Messages.GetByBadgeNumberRequest;
import com.google.common.io.ByteStreams;
import com.google.protobuf.ByteString;

import io.grpc.stub.StreamObserver;

public class EmployeService extends EmployeeServiceImplBase{
	
	@Override
	public void getByBadgeNumber(GetByBadgeNumberRequest request, StreamObserver<EmployeeResponse> responseObserver) {
		Messages.Employee respo1 = Messages.Employee.newBuilder().setId(1).build();
		
		System.out.println(request.toString());
 
		EmployeeResponse response = EmployeeResponse.newBuilder().setEmployee(respo1).build();
		responseObserver.onNext(response);
		
		System.out.println("::::getByBadgeNumber");
		
		
		responseObserver.onCompleted();
		
		//responseObserver.onError(new  Exception("Errroooooooooor"));
	 }
	
	@Override
	public void getAll(GetAllRequest request, StreamObserver<EmployeeResponse> responseObserver) {
		List<Messages.Employee> list = returnlist();
		for (Messages.Employee employee : list) {
			EmployeeResponse response = EmployeeResponse.newBuilder().setEmployee(employee).build();
			responseObserver.onNext(response);
		}
		responseObserver.onCompleted();
	}
	
	//This is when you consume streams
	public StreamObserver<AddPhotoRequest> addPhoto(final StreamObserver<AddPhotoReesponse> responseObserver) {
		
		 return new StreamObserver<AddPhotoRequest>() {
			 
			 private ByteString result;

			@Override
			public void onNext(AddPhotoRequest value) {
				if(result == null) {
					result = value.getData();
				}else {
					result = result.concat(value.getData());
				}
				System.out.println("received"+value.getData().size());
				
			}

			@Override
			public void onError(Throwable t) {
				System.out.println(t);
				
			}

			@Override
			public void onCompleted() {
				System.out.println(result.size());
				responseObserver.onNext(AddPhotoReesponse.newBuilder().setIsOk(true).build());
				responseObserver.onCompleted();
			}

			
			
		};


	}
	
	
	private List<Messages.Employee> returnlist(){
		List<Messages.Employee> list = new ArrayList<Messages.Employee>();
		list.add(Messages.Employee.newBuilder().setId(1).build());
		list.add(Messages.Employee.newBuilder().setId(2).build());
		list.add(Messages.Employee.newBuilder().setId(3).build());
		return list;
	}

}

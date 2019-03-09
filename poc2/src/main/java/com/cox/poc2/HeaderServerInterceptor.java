package com.cox.poc2;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

public class HeaderServerInterceptor implements ServerInterceptor{

	@Override
	public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata md,
			ServerCallHandler<ReqT, RespT> next) {
		
		if(call.getMethodDescriptor().getFullMethodName().equalsIgnoreCase("EmployeeService/GetByBadgeNumber")) {
			for (String key : md.keys()) {
				System.out.println(key);
			}
		}
		return next.startCall(call, md);
	}

}

package com.cox.poc2;

import java.io.File;
import java.io.IOException;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;
import io.grpc.ServerServiceDefinition;

public class EmployeeServiceServer {

	private Server server;
	public static void main(String[] args) throws IOException, InterruptedException {
		
		final EmployeeServiceServer attrib = new EmployeeServiceServer();
		attrib.start();
		System.out.println("listening on 90001");
	}
	
	private void start() throws IOException, InterruptedException{
		
		EmployeService employe = new EmployeService();
		ServerServiceDefinition ssd = ServerInterceptors.interceptForward(employe, new HeaderServerInterceptor());
		System.out.println("starting");
		
		File cert = new File("certificates/certificate.pem");
		File key = new File("certificates/key.pem");
		
		server  = ServerBuilder.forPort(9000)
				.useTransportSecurity(cert, key)
				.addService(ssd)
				.build().start();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				EmployeeServiceServer.this.stop();
			}
		});
		server.awaitTermination();
		
	}
	
	private void stop() {
		if(server!=null) {
			server.shutdown();
		}
			
	}

}


package com.hms.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
@Controller
public class ConsumerControllerClient {
	
	@Autowired
	private DiscoveryClient discoveryClient;
	
	@Autowired
	private LoadBalancerClient loadBalancerClient;
	
	public void getShipment() throws RestClientException, IOException {
		
	
		String baseUrl = getShipmentDataProducer(); // Change it to # getShipmentDataViaLoadBalancer(); So it uses Load Balancer url.
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response=null;
		
		try{
		response=restTemplate.exchange(baseUrl,
				HttpMethod.GET, getHeaders(),String.class);
		
		}catch (Exception ex)
		{
			System.out.println(ex);
		}
		System.out.println(response.getBody());
	}
	
	/**
	 * Autowire Discovery Client
	 * using that Discovery Client call - service registered # Shipment producer.
	 * get the service instance via Eureka Server.
	 * Base url should be appended with # producer URI
	 * 
	 * Rest is simple communication between Micro Services using REST Templates.
	 * 
	 * Look at the caller method.
	 * 
	 */
	private String getShipmentDataProducer() {
		
		String baseUrl = getServiceInstaceBaseUrl("shipment-producer");
		
		baseUrl=baseUrl+"/shipment";
		return baseUrl;
	}
	
	/**
	 * Autowire Discovery Client
	 * using that Discovery Client call - service registered # Shipment producer.
	 * get the service instance via Eureka Server.
	 * Base url should be appended with # producer URI
	 * 
	 * Rest is simple communication between Micro Services using REST Templates.
	 * 
	 * Look at the caller method.
	 * 
	 */
	private String getShipmentDataViaLoadBalancer() {
		
		String baseUrl = getLoadBalanceBaseUrl("shipment-producer");
		
		baseUrl=baseUrl+"/shipment";
		return baseUrl;
	}
	/**
	 * 
	 * @param pServiceInstanceName
	 * @return
	 */
	private String getServiceInstaceBaseUrl(String pServiceInstanceName) {
		
		List<ServiceInstance> instances=discoveryClient.getInstances(pServiceInstanceName);
		ServiceInstance serviceInstance=instances.get(0);
		String baseUrl=serviceInstance.getUri().toString();
		return baseUrl;
	}
	
	/**
	 * 
	 * @param pServiceInstanceName
	 * @return Load Balanced Ribbon Instance.
	 */
	private String getLoadBalanceBaseUrl(String pServiceInstanceName) {
		

		ServiceInstance serviceInstance=loadBalancerClient.choose(pServiceInstanceName);
		String baseUrl=serviceInstance.getUri().toString();
		return baseUrl;
	}

	private static HttpEntity<?> getHeaders() throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		return new HttpEntity<>(headers);
	}
}
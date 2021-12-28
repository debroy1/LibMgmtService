package com.roy.estore.productsservice.rest;

import java.util.UUID;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.roy.estore.productsservice.command.CreateProductCommand;

@RestController
@RequestMapping("/products") // http://localhost:8080/products
public class ProductsController {
	private final Environment env;
	private final CommandGateway commandGateway;
	
	@Autowired
	public ProductsController(Environment env, CommandGateway commandGateway) {
		this.env = env;
		this.commandGateway = commandGateway;
	}
	
	@PostMapping
	public String createProduct(@RequestBody CreateProductRestModel model) {
		
		CreateProductCommand createProductCommand = CreateProductCommand.builder()
			.price(model.getPrice())
			.quantity(model.getQuantity())
			.title(model.getTitle())
			.productId(UUID.randomUUID().toString())
			.build();
		
		String returnValue;
		try {
			returnValue = commandGateway.sendAndWait(createProductCommand);
		} catch (Exception e) {
			returnValue = e.getLocalizedMessage();
		}

		return "Post: Product " + model.getTitle() + " is created by port: " + env.getProperty("local.server.port") + " with returnValue: " + returnValue;
	}

	@GetMapping
	public String getProduct() {
		return "Get: Product is found by port: " + env.getProperty("local.server.port");
	}

	@PutMapping
	public String updateProduct() {
		return "Put: Product is updated by port: " + env.getProperty("local.server.port");
	}

	@DeleteMapping
	public String deleteProduct() {
		return "Delete: Product is deleted by port: " + env.getProperty("local.server.port");
	}

}

package com.roy.estore.productsservice.command;

import java.math.BigDecimal;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import com.roy.estore.productsservice.core.events.ProductCreatedEvent;

@Aggregate
public class ProductAggregate {
	@AggregateIdentifier
	private String productId;
	private String title;
	private BigDecimal price;
	private Integer quantity;

	public ProductAggregate() {
		// kept empty for Axon instantiation
	}
	
	@CommandHandler
	public ProductAggregate(CreateProductCommand command) {
		// validate the command
		if(command.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Product price cannot be less than or equal to zero");
		}
		if(command.getTitle() == null || command.getTitle().isBlank()) {
			throw new IllegalArgumentException("Product title cannot be empty");
		}
		
		ProductCreatedEvent event = new ProductCreatedEvent();
		BeanUtils.copyProperties(command, event);
		
		AggregateLifecycle.apply(event);
	}
	
	@EventSourcingHandler
	public void on(ProductCreatedEvent event) {
		this.productId = event.getProductId();
		this.title = event.getTitle();
		this.price = event.getPrice();
		this.quantity = event.getQuantity();
	}

}

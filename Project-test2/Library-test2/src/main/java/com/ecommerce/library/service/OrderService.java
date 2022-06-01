package com.ecommerce.library.service;

import java.util.Date;
import java.util.List;

import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.Order;
import com.ecommerce.library.model.Product;
import com.ecommerce.library.model.ShoppingCart;

public interface OrderService {
	Order checkout(ShoppingCart cart);

	List<Order> updateStatus(Customer customer, Date now);

	List<Order> findByCustomer(Customer customer);

	Order findById(Long id);

	List<Order> findAll();

	Order confirm(Order order);

	Order done(Order order, Date date);
}

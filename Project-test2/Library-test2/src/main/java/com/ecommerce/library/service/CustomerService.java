package com.ecommerce.library.service;

import com.ecommerce.library.dto.CustomerDto;
import com.ecommerce.library.model.Customer;

public interface CustomerService {
	Customer findByUsername(String username);

	Customer save(CustomerDto customerDto);

	Customer save(Customer customer);
}

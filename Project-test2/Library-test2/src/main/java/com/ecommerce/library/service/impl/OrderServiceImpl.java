package com.ecommerce.library.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.library.model.CartItem;
import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.Order;
import com.ecommerce.library.model.OrderDetail;
import com.ecommerce.library.model.Product;
import com.ecommerce.library.model.ShoppingCart;
import com.ecommerce.library.repository.CartItemRepository;
import com.ecommerce.library.repository.OrderDetailRepository;
import com.ecommerce.library.repository.OrderRepository;
import com.ecommerce.library.repository.ProductRepository;
import com.ecommerce.library.repository.ShoppingCartRepository;
import com.ecommerce.library.service.OrderService;
import com.ecommerce.library.service.ProductService;

@Service
public class OrderServiceImpl implements OrderService {
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private OrderDetailRepository detailRepository;
	@Autowired
	private ShoppingCartRepository cartRepository;
	@Autowired
	private CartItemRepository cartItemRepository;
	@Autowired
	private ProductService productService;

	@Override
	public Order checkout(ShoppingCart cart) {
		Order order = new Order();
		order.setCustomer(cart.getCustomer());
		order.setTotalPrice(cart.getTotalPrices());
		Date orderDate = new Date();
		order.setOrderDate(orderDate);
		order.setOrderStatus("Đơn hàng của bạn đang được chuẩn bị.");
		Calendar c = Calendar.getInstance();
		c.setTime(orderDate);
		c.add(Calendar.DATE, 1);
		Date deliveryDate = c.getTime();
		order.setDeliveryDate(deliveryDate);
		Set<CartItem> cartItems = cart.getCartItem();
		List<OrderDetail> details = new ArrayList<OrderDetail>();
		for (CartItem cartItem : cartItems) {
			OrderDetail detail = new OrderDetail();
			detail.setProduct(cartItem.getProduct());
			detail.setQuantity(cartItem.getQuantity());
			detail.setTotalPrice(cartItem.getTotalPrice());
			detail.setOrder(order);
			detailRepository.save(detail);
			details.add(detail);
		}
		order.setOrderDetailList(details);
		cartItemRepository.deleteAll();
		cartRepository.deleteAll();
		return orderRepository.save(order);
	}

	@Override
	public List<Order> findByCustomer(Customer customer) {
		return orderRepository.findByCustomer(customer);
	}

	@Override
	public List<Order> updateStatus(Customer customer, Date now) {
		List<Order> order = orderRepository.findByCustomer(customer);
		Calendar c = Calendar.getInstance();
		for (Order order2 : order) {
			Date orderDate = order2.getOrderDate();
			Date deliveryDate = order2.getDeliveryDate();

			if (now.after(deliveryDate)) {
				order2.setOrderStatus("Đơn hàng đã hoàn thành.");
				orderRepository.save(order2);
			} else {
				c.setTime(orderDate);
				c.add(Calendar.HOUR, 12);
				now = c.getTime();
				if (now.before(deliveryDate))
					order2.setOrderStatus("Đơn hàng đang được vận chuyển.");
				orderRepository.save(order2);
			}
		}
		return orderRepository.saveAll(order);
	}

	@Override
	public Order findById(Long id) {
		return orderRepository.findById(id).get();
	}

	@Override
	public List<Order> findAll() {
		List<Order> list = orderRepository.findAll();
		return list;
	}

	@Override
	public Order confirm(Order order) {
		order.setOrderStatus("Đơn hàng đang được vận chuyển.");
		List<OrderDetail> details = order.getOrderDetailList();
		for (OrderDetail detail : details) {
			Product product = detail.getProduct();
			int sub = product.getCurrentQuantity() - detail.getQuantity();
			if(sub == 0) {
				product.set_activated(false);
			}
			product.setCurrentQuantity(sub);
			productService.save(product);
		}
		return orderRepository.save(order);
	}
}

package com.ecommerce.admin.controller;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.Order;
import com.ecommerce.library.model.OrderDetail;
import com.ecommerce.library.model.ShoppingCart;
import com.ecommerce.library.repository.OrderRepository;
import com.ecommerce.library.service.OrderService;

@Controller
public class OrderController {
	@Autowired
	private OrderService orderService;
	@Autowired
	private OrderRepository orderRepository;

	@GetMapping("/orders")
	public String order(Model model) {
		List<Order> list = orderService.findAll();
		if (list == null) {
			model.addAttribute("check", "Chưa có sản phẩm nào trong giỏ hàng.");
		}
		model.addAttribute("orders", list);
		model.addAttribute("title", "Quản lý đơn hàng");
		return "order";
	}

	@GetMapping("/shipping/{id}")
	public String ship(Model model, @PathVariable Long id, HttpServletRequest request) {
		Order order = orderService.findById(id);
		orderService.confirm(order);
		return "redirect:" + request.getHeader("Referer");
	}

	@GetMapping("/done/{id}")
	public String done(Model model, @PathVariable Long id, HttpServletRequest request) {
		Order order = orderService.findById(id);
		order.setOrderStatus("Đơn hàng đã hoàn thành.");
		orderRepository.save(order);
		return "redirect:" + request.getHeader("Referer");
	}

	@GetMapping("/view/{id}")
	public String checkDetail(Model model, @PathVariable("id") Long id) {
		Order order = orderService.findById(id);
		List<OrderDetail> details = order.getOrderDetailList();
		model.addAttribute("title", "Chi tiết đơn hàng.");
		model.addAttribute("details", details);
		return "order-detail";

	}
	@GetMapping("/customer/{id}")
	public String checkCustomer(Model model, @PathVariable("id") Long id) {
		Order order = orderService.findById(id);
		Customer customer = order.getCustomer();
		model.addAttribute("title", "Chi tiết đơn hàng.");
		model.addAttribute("details", customer);
		return "customer-detail";

	}

}

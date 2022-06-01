package com.ecommerce.customer.controller;

import java.security.Principal;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecommerce.library.model.CartItem;
import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.Order;
import com.ecommerce.library.model.OrderDetail;
import com.ecommerce.library.model.ShoppingCart;
import com.ecommerce.library.service.CustomerService;
import com.ecommerce.library.service.OrderService;
import com.ecommerce.library.service.ShoppingCartService;

@Controller
public class OrderController {
	@Autowired
	private CustomerService customerService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private ShoppingCartService cartService;

	@GetMapping("/checkout")
	public String checkout(Model model, Principal principal) {
		if (principal == null) {
			ShoppingCart cart = new ShoppingCart();

			model.addAttribute("shoppingCart", cart);
		} else {
			String username = principal.getName();
			Customer customer = customerService.findByUsername(username);
			ShoppingCart cart = customer.getShoppingCart();
			if (cart == null) {
				cart = new ShoppingCart();
				model.addAttribute("check", "Chưa có sản phẩm nào trong giỏ hàng.");
			}
			model.addAttribute("shoppingCart", cart);
			Set<CartItem> cartItems = cart.getCartItem();
			int discount = 0;
			if (cartItems != null) {
				for (CartItem item : cartItems) {
					discount += (item.getProduct().getCostPrice() - item.getProduct().getSalePrice())
							* item.getQuantity();
				}
				int subTotal = cart.getTotalPrices() + discount;
				model.addAttribute("subTotal", subTotal);
				model.addAttribute("discount", discount);
			}
			model.addAttribute("firstName", customer.getFirstName());
			model.addAttribute("customer", customer);
		}
		model.addAttribute("title", "Thanh toán");
		return "checkout";
	}

	@GetMapping("/placeOrder")
	public String placeOrder(Model model, Principal principal, HttpServletRequest request,
			RedirectAttributes attributes) {
		String username = principal.getName();
		Customer customer = customerService.findByUsername(username);
		ShoppingCart cart = customer.getShoppingCart();
		orderService.checkout(cart);

		attributes.addFlashAttribute("success", "Đặt hàng thành công!");
		return "redirect:" + request.getHeader("Referer");

	}

	@GetMapping("/track-orders")
	public String trackOrders(Model model, Principal principal) {
		if (principal == null) {
			ShoppingCart cart = new ShoppingCart();

			model.addAttribute("shoppingCart", cart);
		} else {
			String username = principal.getName();
			Customer customer = customerService.findByUsername(username);
			ShoppingCart cart = customer.getShoppingCart();
			if (cart == null) {
				cart = new ShoppingCart();

			}
			model.addAttribute("shoppingCart", cart);
		}
		String username = principal.getName();
		Customer customer = customerService.findByUsername(username);
		List<Order> list = orderService.findByCustomer(customer);
		if (list == null) {
			model.addAttribute("check", "Chưa có sản phẩm nào trong giỏ hàng.");
		}
		Collections.reverse(list);
		model.addAttribute("orders", list);
		model.addAttribute("title", "Theo dõi đơn hàng");
		return "tracking";
	}

//	@GetMapping("updateStatus")
//	public String updateStatus(Model model, HttpServletRequest request, Principal principal) {
//		String username = principal.getName();
//		Customer customer = customerService.findByUsername(username);
//		Date now = new Date();
//		orderService.updateStatus(customer,now);
//		return "redirect:" + request.getHeader("Referer");
//
//	}

	@GetMapping("/orderDetail/{id}")
	public String checkDetail(Principal principal, Model model, @PathVariable("id") Long id) {
		if (principal == null) {
			ShoppingCart cart = new ShoppingCart();

			model.addAttribute("shoppingCart", cart);
		} else {
			String username = principal.getName();
			Customer customer = customerService.findByUsername(username);
			ShoppingCart cart = customer.getShoppingCart();
			if (cart == null) {
				cart = new ShoppingCart();

			}
			model.addAttribute("shoppingCart", cart);
		}
		Order order = orderService.findById(id);
		List<OrderDetail> details = order.getOrderDetailList();
		model.addAttribute("title", "Chi tiết đơn hàng.");
		model.addAttribute("details", details);
		return "order-detail";

	}
}

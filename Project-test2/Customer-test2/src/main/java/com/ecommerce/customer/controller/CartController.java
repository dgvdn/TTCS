package com.ecommerce.customer.controller;

import java.security.Principal;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import com.ecommerce.library.dto.ProductDto;
import com.ecommerce.library.model.CartItem;
import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.Product;
import com.ecommerce.library.model.ShoppingCart;
import com.ecommerce.library.service.CustomerService;
import com.ecommerce.library.service.ProductService;
import com.ecommerce.library.service.ShoppingCartService;

@Controller
public class CartController {
	@Autowired
	private CustomerService customerService;
	@Autowired
	private ProductService productService;
	@Autowired
	private ShoppingCartService cartService;

	@GetMapping("/cart")
	public String cart(Model model, Principal principal) {
		if (principal == null) {
			return "redirect:/login";
		}
		String username = principal.getName();
		Customer customer = customerService.findByUsername(username);
		ShoppingCart cart = customer.getShoppingCart();
		if (cart == null) {
			cart = new ShoppingCart();
			model.addAttribute("check", "Chưa có sản phẩm nào trong giỏ hàng.");
		} else {
			Set<CartItem> cartItems = cart.getCartItem();
			int discount = 0;
			for (CartItem item : cartItems) {
				discount += (item.getProduct().getCostPrice() - item.getProduct().getSalePrice()) * item.getQuantity();
			}
			int subTotal = cart.getTotalPrices() + discount;
			model.addAttribute("subTotal", subTotal);
			model.addAttribute("discount", discount);
		}
		model.addAttribute("shoppingCart", cart);
		model.addAttribute("title", "Giỏ hàng");
		return "cart";
	}

	@PostMapping("/addToCart")
	public String addToCart(HttpServletRequest request, @RequestParam("id") Long productId,
			@RequestParam(value = "quantity", required = false, defaultValue = "1") int quantity, Principal principal,
			Model model) {
		if (principal == null) {
			return "redirect:/login";
		}
		Product product = productService.getProductById(productId);
		String username = principal.getName();
		Customer customer = customerService.findByUsername(username);
		ShoppingCart cart = cartService.addItemToCart(product, quantity, customer);
		return "redirect:" + request.getHeader("Referer");

	}

	@GetMapping("/removeCartItem/{id}")
	public String removeItem(@PathVariable("id") Long id, HttpServletRequest request, Principal principal) {
		String username = principal.getName();
		Customer customer = customerService.findByUsername(username);
		ShoppingCart cart = cartService.removeCartItem(id, customer);
		return "redirect:" + request.getHeader("Referer");
	}

	@PostMapping("/updateShoppingCart")
	public String updateCartItem(@RequestParam("item_id") Long id, @RequestParam("quantity") int quantity,
			Principal principal, HttpServletRequest request) {
		String username = principal.getName();
		Customer customer = customerService.findByUsername(username);
		cartService.updateShoppingCartItem(id, quantity, customer);
		return "redirect:" + request.getHeader("Referer");
	}
}

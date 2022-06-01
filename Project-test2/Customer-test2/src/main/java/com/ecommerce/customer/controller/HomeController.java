package com.ecommerce.customer.controller;

import com.ecommerce.library.model.Category;
import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.Product;
import com.ecommerce.library.model.ShoppingCart;
import com.ecommerce.library.service.CategoryService;
import com.ecommerce.library.service.CustomerService;
import com.ecommerce.library.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
	@Autowired
	private ProductService productService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private CustomerService customerService;

	@GetMapping("/home")
	public String index(Model model, Principal principal) {
		List<Category> categories = categoryService.findAll();
		List<Product> homeList = new ArrayList<Product>();
		List<Product> list = new ArrayList<Product>();
		for (Category category : categories) {
			list = productService.getHomeList(category.getId());
			homeList.addAll(list);
		}
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
		}
		model.addAttribute("categories", categories);
		model.addAttribute("products", homeList);
		model.addAttribute("title", "Trang chủ");
		return "index";
	}
}

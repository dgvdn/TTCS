package com.ecommerce.customer.controller;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ecommerce.library.dto.AdminDto;
import com.ecommerce.library.dto.CustomerDto;
import com.ecommerce.library.model.Admin;
import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.ShoppingCart;
import com.ecommerce.library.service.CustomerService;
import com.ecommerce.library.service.impl.CustomerServiceImpl;

@Controller
public class AuthController {
	@Autowired
	private CustomerServiceImpl customerServiceImpl;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private CustomerService customerService;

	@GetMapping("/login")
	public String loginForm(Model model) {
		model.addAttribute("title", "Đăng nhập");
		return "login";
	}

	@RequestMapping("/index")
	public String home(Model model) {
		model.addAttribute("title", "Home Page");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "redirect:/login";
		}
		return "index";
	}

	@GetMapping("/register")
	public String register(Model model) {
		model.addAttribute("title", "Đăng ký");
		model.addAttribute("customerDto", new CustomerDto());
		return "register";
	}

	@PostMapping("/register-new")
	public String addNewCustomer(@Valid @ModelAttribute("customerDto") CustomerDto customerDto, BindingResult result,
			Model model) {

		try {

			if (result.hasErrors()) {
				model.addAttribute("customerDto", customerDto);
				result.toString();
				return "register";
			}
			String username = customerDto.getUsername();
			Customer customer = customerServiceImpl.findByUsername(username);
			if (customer != null) {
				model.addAttribute("customerDto", customerDto);
				model.addAttribute("emailError", "Email của bạn đã được đăng ký!");
				return "register";
			}
			if (customerDto.getPassword().equals(customerDto.getRepeatPassword())) {
				customerDto.setPassword(passwordEncoder.encode(customerDto.getPassword()));
				customerServiceImpl.save(customerDto);
				System.out.println("success");
				model.addAttribute("success", "Đăng ký thành công!");
				model.addAttribute("customerDto", customerDto);
			} else {
				model.addAttribute("customerDto", customerDto);
				model.addAttribute("passwordError", "Mật khẩu sai! Vui lòng kiểm tra lại!");
				return "register";
			}
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errors", "Lỗi kết nối server!");
		}
		return "register";

	}

	@GetMapping("/account")
	public String account(Model model, Principal principal) {
		if (principal == null) {
			ShoppingCart cart = new ShoppingCart();
			model.addAttribute("shoppingCart", cart);
			return "redirect:/login";
		} else {
			String username = principal.getName();
			Customer customer = customerService.findByUsername(username);
			ShoppingCart cart = customer.getShoppingCart();
			if (cart == null) {
				cart = new ShoppingCart();
				model.addAttribute("check", "Chưa có sản phẩm nào trong giỏ hàng.");
			}
			model.addAttribute("shoppingCart", cart);
			model.addAttribute("customer", customer);
		}
		model.addAttribute("title", "Tài khoản");
		return "my-account";
	}

	@PostMapping("/addInfo")
	public String addInfo(Model model, Principal principal, @RequestParam("lastName") String lastName,
			@RequestParam("firstName") String firstName, @RequestParam("phoneNumber") String phoneNumber,
			@RequestParam("address") String address) {
		String username = principal.getName();
		Customer customer = customerService.findByUsername(username);
		customer.setFirstName(firstName);
		customer.setLastName(lastName);
		customer.setAddress(address);
		customer.setPhoneNumber(phoneNumber);
		customerService.save(customer);
		return "redirect:/checkout";

	}
}

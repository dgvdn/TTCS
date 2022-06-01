package com.ecommerce.customer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {
	@RequestMapping("/error")
	public String handleError(Model model) {
		model.addAttribute("title", "404 - Page not found");
		return "404";
	}
}

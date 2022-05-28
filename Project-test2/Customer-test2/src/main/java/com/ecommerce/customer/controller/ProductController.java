package com.ecommerce.customer.controller;

import com.ecommerce.library.dto.CategoryDto;
import com.ecommerce.library.dto.ProductDto;
import com.ecommerce.library.model.Category;
import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.Product;
import com.ecommerce.library.model.ShoppingCart;
import com.ecommerce.library.service.CategoryService;
import com.ecommerce.library.service.CustomerService;
import com.ecommerce.library.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;

@Controller
public class ProductController {

	@Autowired
	private ProductService productService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private CustomerService customerService;

	@GetMapping("/products")
	public String products(Model model, Principal principal) {
		List<CategoryDto> categoryDtoList = categoryService.getCategoryAndProduct();
		List<Product> products = productService.getAllProducts();
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
		model.addAttribute("categories", categoryDtoList);
		model.addAttribute("viewProducts", products);
		model.addAttribute("products", products);
		model.addAttribute("title", "Danh mục sản phẩm");
//		model.addAttribute("byPrice", Comparator.comparing(Product::getSalePrice));
		model.addAttribute("sort", "Mặc định");
		return "shop";
	}

	@GetMapping("/find-product/{id}")
	public String findProductById(@PathVariable("id") Long id, Model model, Principal principal) {
		Product product = productService.getProductById(id);
		Long categoryId = product.getCategory().getId();
		List<Product> products = productService.getRelatedProducts(categoryId);
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
		model.addAttribute("product", product);
		model.addAttribute("products", products);
		model.addAttribute("title", product.getName());
		return "product-detail";
	}

	@GetMapping("/products-in-category/{id}")
	public String getProductsInCategory(@PathVariable("id") Long categoryId, Model model, Principal principal) {
		Category category = categoryService.findById(categoryId);
		List<CategoryDto> categories = categoryService.getCategoryAndProduct();
		List<Product> products = productService.getProductsInCategory(categoryId);
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
		model.addAttribute("category", category);
		model.addAttribute("categories", categories);
		model.addAttribute("products", products);
		model.addAttribute("title", category.getName());
		return "products-in-category";
	}

	@GetMapping("/search")
	public String searchProducts(Model model, String keyword, Principal principal) {
		if (keyword != null) {
			List<Product> products = productService.findByKeyWord(keyword);
			model.addAttribute("products", products);
			model.addAttribute("title", "Tìm kiếm " + keyword);
		} else {
			List<Product> products = productService.getAllProducts();
			model.addAttribute("products", products);
			model.addAttribute("title", "Tìm kiếm " + keyword);
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
		return "search";
	}

	@GetMapping("/sortByPriceAsc")
	public String sortProductAsc(Model model, Principal principal) {
		List<Product> products = productService.findByOrderBySalePriceAsc();
		model.addAttribute("products", products);
		List<CategoryDto> categoryDtoList = categoryService.getCategoryAndProduct();
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
		model.addAttribute("categories", categoryDtoList);
		model.addAttribute("title", "Danh mục sản phẩm");
		model.addAttribute("sort", "Giá thấp đến cao");
		model.addAttribute("viewProducts", products);
		return "shop";
	}

	@GetMapping("/sortByPriceDesc")
	public String sortProductDesc(Model model, Principal principal) {
		List<Product> products = productService.findByOrderBySalePriceDesc();
		model.addAttribute("products", products);
		List<CategoryDto> categoryDtoList = categoryService.getCategoryAndProduct();
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
		model.addAttribute("categories", categoryDtoList);
		model.addAttribute("title", "Danh mục sản phẩm");
		model.addAttribute("sort", "Giá cao đến thấp");
		model.addAttribute("viewProducts", products);
		return "shop";
	}
}

package com.ecommerce.library.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.library.model.CartItem;
import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.Product;
import com.ecommerce.library.model.ShoppingCart;
import com.ecommerce.library.repository.CartItemRepository;
import com.ecommerce.library.repository.ShoppingCartRepository;
import com.ecommerce.library.service.ShoppingCartService;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
	@Autowired
	private CartItemRepository cartItemRepository;
	@Autowired
	private ShoppingCartRepository cartRepository;

	@Override
	public ShoppingCart addItemToCart(Product product, int quantity, Customer customer) {
		ShoppingCart cart = customer.getShoppingCart();
		// customer don't have ShoppingCart
		if (cart == null) {
			cart = new ShoppingCart();
		}
		Set<CartItem> cartItems = cart.getCartItem();
		CartItem cartItem = findCartItem(cartItems, product.getId());
		// ShoppingCart empty
		if (cartItems == null) {
			cartItems = new HashSet<>();
			if (cartItem == null) {
				cartItem = new CartItem();
				cartItem.setProduct(product);
				cartItem.setTotalPrice(quantity * product.getCostPrice());
				cartItem.setQuantity(quantity);
				cartItem.setCart(cart);
				cartItems.add(cartItem);
				cartItemRepository.save(cartItem);
			}
		} else {
			if (cartItem == null) {
				cartItem = new CartItem();
				cartItem.setProduct(product);
				cartItem.setTotalPrice(quantity * product.getCostPrice());
				cartItem.setQuantity(quantity);
				cartItem.setCart(cart);
				cartItems.add(cartItem);
				cartItemRepository.save(cartItem);
			} else {
				cartItem.setQuantity(cartItem.getQuantity() + quantity);
				cartItem.setTotalPrice(cartItem.getTotalPrice() + (quantity * product.getCostPrice()));
				cartItemRepository.save(cartItem);
			}

		}
		cart.setCartItem(cartItems);
		int totalItems = totalItems(cart.getCartItem());
		int totalPrice = totalPrice(cart.getCartItem());
		cart.setTotalItems(totalItems);
		cart.setTotalPrices(totalPrice);
		cart.setCustomer(customer);

		return cartRepository.save(cart);

	}

	private CartItem findCartItem(Set<CartItem> cartItems, Long productId) {
		if (cartItems == null) {
			return null;
		}
		CartItem cartItem = null;
		for (CartItem iterator : cartItems) {
			if (iterator.getProduct().getId() == productId) {
				cartItem = iterator;
			}

		}
		return cartItem;
	}

	private int totalItems(Set<CartItem> cartItems) {
		int totalItems = 0;
		for (CartItem cartItem : cartItems) {
			totalItems += cartItem.getQuantity();
		}
		return totalItems;
	}

	private int totalPrice(Set<CartItem> cartItems) {
		int totalPrice = 0;
		for (CartItem cartItem : cartItems) {
			totalPrice += cartItem.getProduct().getSalePrice() * cartItem.getQuantity();
		}
		return totalPrice;

	}

	@Override
	public ShoppingCart removeCartItem(Long id, Customer customer) {
		ShoppingCart cart = customer.getShoppingCart();
		CartItem cartItem = cartItemRepository.getById(id);
		Set<CartItem> cartItems = cart.getCartItem();
		cartItems.remove(cartItem);
		cartItemRepository.delete(cartItem);
		cart.setCartItem(cartItems);
		int totalItems = totalItems(cart.getCartItem());
		int totalPrice = totalPrice(cart.getCartItem());
		cart.setTotalItems(totalItems);
		cart.setTotalPrices(totalPrice);
		return cartRepository.save(cart);
	}

	@Override
	public ShoppingCart updateShoppingCartItem(Long id, int quantity, Customer customer) {
		ShoppingCart cart = customer.getShoppingCart();
		Set<CartItem> cartItems = cart.getCartItem();
		CartItem cartItem = cartItemRepository.findById(id).get();
		if (quantity == 0) {
			cartItems.remove(cartItem);
			cartItemRepository.delete(cartItem);
		} else {
			cartItem.setQuantity(quantity);
			cartItem.setTotalPrice(quantity * cartItem.getProduct().getCostPrice());
			cartItemRepository.save(cartItem);
		}
		cart.setCartItem(cartItems);
		int totalItems = totalItems(cart.getCartItem());
		int totalPrice = totalPrice(cart.getCartItem());
		cart.setTotalItems(totalItems);
		cart.setTotalPrices(totalPrice);
		return cartRepository.save(cart);
	}

}

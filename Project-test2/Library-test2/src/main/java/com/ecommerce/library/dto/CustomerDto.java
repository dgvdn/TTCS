package com.ecommerce.library.dto;

import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class CustomerDto {
	private String username;
	@Size(min = 5, max = 15, message = "Mật khẩu không khả dụng !(5-15 kí tự)")
	private String password;

	private String repeatPassword;
}

package com.ecommerce.admin.controller;

import com.ecommerce.library.dto.AdminDto;
import com.ecommerce.library.model.Admin;
import com.ecommerce.library.service.impl.AdminServiceImpl;
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

import javax.validation.Valid;

@Controller
public class LoginController {
    @Autowired
    private AdminServiceImpl adminService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping(value = {"/login","/",""})
    public String loginForm(Model model){
        model.addAttribute("title", "Đăng nhập");
        return "login";
    }

    @RequestMapping("/index")
    public String home(Model model){
        model.addAttribute("title", "Trang chủ");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || authentication instanceof  AnonymousAuthenticationToken){
            return "redirect:/login";
        }
        return "index";
    }

   @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("title", "Đăng ký");
        model.addAttribute("adminDto", new AdminDto());
        return "register";
   }

   @GetMapping("/forgot-password")
    public String forgotPassword(Model model){
        model.addAttribute("title", "Quên mật khẩu");
        return "forgot-password";
   }

   @PostMapping("/register-new")
   public String addNewAdmin(@Valid @ModelAttribute("adminDto")AdminDto adminDto,
                             BindingResult result,
                             Model model){

        try {

            if(result.hasErrors()){
                model.addAttribute("adminDto", adminDto);
                result.toString();
                return "register";
            }
            String username = adminDto.getUsername();
            Admin admin = adminService.findByUsername(username);
            if(admin != null){
                model.addAttribute("adminDto", adminDto);
                System.out.println("admin not null");
               model.addAttribute("emailError", "Email đăng ký đã tồn tại!");
                return "register";
            }
            if(adminDto.getPassword().equals(adminDto.getRepeatPassword())){
                adminDto.setPassword(passwordEncoder.encode(adminDto.getPassword()));
                adminService.save(adminDto);
                System.out.println("success");
               model.addAttribute("success", "Đăng ký thành công!");
                model.addAttribute("adminDto", adminDto);
            }else{
                model.addAttribute("adminDto", adminDto);
                model.addAttribute("passwordError", "Vui lòng kiểm tra lại mật khẩu!");
                System.out.println("password not same");
                return "register";
            }
        }catch (Exception e){
            e.printStackTrace();
            model.addAttribute("errors", "Lỗi server!");
        }
        return "register";

   }






}

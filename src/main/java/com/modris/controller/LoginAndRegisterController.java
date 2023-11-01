package com.modris.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.modris.model.Users;
import com.modris.services.RegistrationService;

@Controller
public class LoginAndRegisterController {

	
	private final RegistrationService registrationService;
	
	public LoginAndRegisterController(RegistrationService registrationService) {
		this.registrationService = registrationService;
	}
	
	@GetMapping("/login")
	public String loginpage() {
		return "login.html";
	}
	@PostMapping("/logout")
	public String logout() {
		return "redirect:/login?logout";
	}
	
	@GetMapping("/register")
	public String register(@RequestParam(value = "usernameTaken",required=false) String taken, Model model) {
		model.addAttribute("usernameTaken",taken);
		return "register.html";
	}
	
	@PostMapping("/registerSave")
	public String register(Users user,
							@RequestParam("password") String password,
							Model model,
							RedirectAttributes redirectAttributes) {
						
		
		user.setPassword(password);
		
		if(registrationService.taken(user.getUsername())) {
			redirectAttributes.addAttribute("usernameTaken","Registration failed. Username is taken.");
			return "redirect:/register";
		} else {
			registrationService.register(user);
			
			return "redirect:/login";
		}
		
	}
}

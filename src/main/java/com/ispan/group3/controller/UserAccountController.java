package com.ispan.group3.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.http.HttpClient.Redirect;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

//import com.ispan.group3.context.UserContext;
import com.ispan.group3.repository.UserData;
import com.ispan.group3.repository.UserDetailsData;
import com.ispan.group3.repository.UserRepository;
import com.ispan.group3.service.UserService;
import com.ispan.group3.service.impl.UserDetailsServiceImpl;
import com.ispan.group3.util.FileUploadUtil;

import net.bytebuddy.utility.RandomString;




@Controller
public class UserAccountController {

//	@Autowired
//	private UserContext userContext;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserDetailsServiceImpl userdetailsServletImpl;
	
//	@Autowired
//	private UserAccountService userAccountService;
	
	@RequestMapping("/frontend/index")
	public String index() {
		return "frontend/index";
	}
	
	@RequestMapping("/fail")
	public String loginFailed() {
		return "frontend/login";
	}
	
	@GetMapping({"/admin"})
	public String admin() { 
		return "backend/index";
	}
	
	@RequestMapping("/logout")
	public String logout() {
		return "frontend/index";
	}
	
	@GetMapping({"/login"})
	public String login(){
		
		return "frontend/login";
	}
	
	@GetMapping({"/default"})
	public String rederect() {
		return "frontend/index";
	}
	
//	@PostMapping({"/register"})
//	public String doRegister(UserData user){
//		
//		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//		
//		user.setUsername(user.getUsername());
//		user.setPassword(passwordEncoder.encode(user.getPassword()));
//		user.setAuthority("ROLE_USER");  //註冊預設是顧客
//
//		userRepository.save(user); //新增使用者到資料庫
//		
////		userContext.setCurrentUser(user); //設定使用者為已登入
//		return "redirect:/login";
//	}
	
	@PostMapping("/register")
    public String processRegister(UserData user, HttpServletRequest request) 
    		throws UnsupportedEncodingException, MessagingException {
        userService.register(user, getSiteURL(request));       
        return "frontend/register_success";
    }
	
	
//	@PostMapping("/register")
//	public String processRegister2(UserData user) 
//			throws UnsupportedEncodingException, MessagingException {
//		userService.register(user);       
//		return "frontend/register_success";
//	}
     
    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

	@GetMapping({"/userdetails"})
	public String UserDetail(@AuthenticationPrincipal UserDetailsData loggedUser, Model model) {
		String username = loggedUser.getUsername();
		UserData user = userRepository.findByUsername(username);
		model.addAttribute("user", user);
		return "frontend/user-detail";
	}
	
	@PostMapping({"/userdetals/update"})
	public String saveDetails (UserData user, RedirectAttributes redirectAttributes,
			@AuthenticationPrincipal UserDetailsData loggedUser,
			@RequestParam(value = "imagefile", required = false) MultipartFile file) {
//		try {
//			String savePath = FileUploadUtil.saveFile("user", file);
//			user.setImage(savePath);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		userRepository.save(user);
		
		loggedUser.setCh_name(user.getCh_name());
		loggedUser.setEn_name(user.getEn_name());
		loggedUser.setBirthday(user.getBirthday());
		loggedUser.setGender(user.getGender());
		loggedUser.setPhone(user.getPhone());
		loggedUser.setCity(user.getCity());
		loggedUser.setLocation(user.getLocation());
		loggedUser.setAddress(user.getAddress());
		
		redirectAttributes.addFlashAttribute("message", "儲存成功！");
		
		return "redirect:/userdetails";
	}
	
	@GetMapping("/verify")
	public String verifyUser(@Param("code") String code) {
	    if (userService.verify(code)) {
	        return "frontend/verify_success";
	    } else {
	        return "frontend/verify_fail";
	    }
	}
	
	//檢查Email是否已存在
		@ResponseBody
		@PostMapping(value = "/CheckEmail", produces = "application/json; charset = UTF-8")
		public boolean checkUser(@RequestParam String username) {
			UserData user = userRepository.findByUsername(username);
			if (user == null) {
				return true;
			}
			return false;
		}

}

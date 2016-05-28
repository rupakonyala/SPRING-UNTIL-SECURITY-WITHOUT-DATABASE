package com.ecommerce.Controller;


import org.springframework.validation.BindingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;

import com.ecommerce.model.UserDetails;
import com.ecommerce.model.ProductModel;
import com.ecommerce.DAOController.ProductDAO;
import com.ecommerce.service.PdtService;
import com.ecommerce.service.PdtServiceImpl;
import com.ecommerce.User.*;


@Controller
public class ControllerFile 
{
	@Autowired
	PdtService pdtServiceImpl;
	
//	@Autowired
//	UserServiceInterface usrServiceImpl;
	
	
	String message = "Welcome ! ";
	String setName = ""; 
	

	@RequestMapping("/loginPage")
	public ModelAndView showLoginPage()
	{
		ModelAndView mv = new ModelAndView("Login");
		return mv;
	}
	
	/*
	@RequestMapping(value = { "/", "/welcome**" })
	public ModelAndView welcomePage() {

		ModelAndView model = new ModelAndView();
		model.addObject("title", "Spring Security Custom Login Form");
		model.addObject("message", "This is welcome page!");
		model.setViewName("Home");
		return model;
	}
	*/
	
	@RequestMapping("/register")
	public ModelAndView showRegister()
	{
		ModelAndView mv = new ModelAndView("Register");
		return mv;
	}
	
//	@RequestMapping("/registerUser")
//	public String registerUserDetails(UserDetails user)
//	{
//		usrServiceImpl.addUser(user);
//		return "success";		
//	}
		
	@RequestMapping("/productsInfo")
	public ModelAndView showProducts(
			@RequestParam(value = "name", required = false, defaultValue = "img") String name)
	{
		ModelAndView mv = null;
		setName = name;
		
		mv = new ModelAndView("GsonConvert");			  
		
		return mv;
	}


	List<ProductModel> list;
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/GsonCon")
	public @ResponseBody String getValues()throws Exception
	{
		String result = "";
		
		if(setName.equals("all")){			
			list = pdtServiceImpl.getProducts();
			Gson gson = new Gson();			  
			result = gson.toJson(list);			
		}
		else{
			System.out.println("Controller : "+setName);
			ProductModel pm = pdtServiceImpl.getProduct(Integer.parseInt(setName));
			List<ProductModel> list = new ArrayList<ProductModel>();
			list.add(pm);
			Gson gson = new Gson();
			result = gson.toJson(list);
		}
		return result;
	}
	
	@RequestMapping("/insert")
	public ModelAndView showInsertPage(ProductModel pm)		
	{	
		return new ModelAndView("InsertProduct");
	}

	@RequestMapping("/insertProductValues")
	public String insertProduct(ProductModel pm)		
	{	
		pdtServiceImpl.addProduct(pm);
		return "success";
	}
	
	int pid = 0;
	
	@RequestMapping("/edit")
	public ModelAndView editProductPage(
			@RequestParam(value = "ProductId", required = false, defaultValue = "pid") int ProductId,@ModelAttribute("pm") ProductModel pm,BindingResult result)
	{	
		System.out.println(ProductId);
		pid = ProductId;
		ProductModel pmObject = pdtServiceImpl.getRowById(ProductId);
		return new ModelAndView("EditProduct","PIDObj",pmObject);	  
	}
	
	@RequestMapping("/updateProduct")
	public ModelAndView updateProduct(@ModelAttribute("pm") ProductModel pm) {
		pm.setPid(pid);
		
		pdtServiceImpl.updateProduct(pm);
		
	 	ModelAndView modelAndView = new ModelAndView("success");

		String message = "Product was successfully edited.";

		modelAndView.addObject("message", message);

		return modelAndView;
	}

	@RequestMapping("/delete")
	public ModelAndView deleteProduct(
			@RequestParam(value = "ProductId", required = false, defaultValue = "pid") int PId,@ModelAttribute("pm") ProductModel pm,BindingResult result){
		System.out.println(PId);
		
		pdtServiceImpl.deleteProduct(PId);
		
		ModelAndView modelAndView = new ModelAndView("success");
		
		String message = "Product was successfully deleted.";
		
		modelAndView.addObject("message", message);
		
		return modelAndView;
	}

	
	@RequestMapping(value = { "/", "/welcome**" }, method = RequestMethod.GET)
	public ModelAndView defaultPage() {

		ModelAndView model = new ModelAndView();
		model.addObject("title", "Spring Security Login Form - Database Authentication");
		model.addObject("message", "This is default page!");
		model.setViewName("hello");
		return model;

	}

	@RequestMapping(value = "/admin**", method = RequestMethod.GET)
	public ModelAndView adminPage() {

		ModelAndView model = new ModelAndView();
		model.addObject("title", "Spring Security Login Form - Database Authentication");
		model.addObject("message", "This page is for ROLE_ADMIN only!");
		model.setViewName("Admin");

		return model;

	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView login(@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout) {

		ModelAndView model = new ModelAndView();
		if (error != null) {
			model.addObject("error", "Invalid username and password!");
		}

		if (logout != null) {
			model.addObject("msg", "You've been logged out successfully.");
		}
		model.setViewName("Login");

		return model;

	}
	
	//for 403 access denied page
	@RequestMapping(value = "/403", method = RequestMethod.GET)
	public ModelAndView accesssDenied() {

		ModelAndView model = new ModelAndView();
		
		//check if user is login
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			UserDetails userDetail = (UserDetails) auth.getPrincipal();
			System.out.println(userDetail);
		
			model.addObject("username", userDetail.getUsername());
			
		}
		
		model.setViewName("403");
		return model;

	}

}
package com.carRepair.carRepair.Web.AdminControllers;

import com.carRepair.carRepair.Converters.UserConverter;
import com.carRepair.carRepair.Domain.Member;
import com.carRepair.carRepair.Domain.User;
import com.carRepair.carRepair.Forms.UserForm;
import com.carRepair.carRepair.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller

public class UserController {

    private static final String USER_FORM = "userForm";
    private static final String BASE_URL = "/admin";

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/admin/create-user", method = RequestMethod.GET)
    String getCreateUserView(Model model, RedirectAttributes redirectAttributes){

        if(model.containsAttribute("userId")){
            String message = model.asMap().get("message").toString();
            String userId = model.asMap().get("userId").toString();
            model.addAttribute("message", message);
            model.addAttribute("userId", userId);
        }
        if(!model.containsAttribute(USER_FORM)){
            model.addAttribute(USER_FORM, new UserForm());
        }

        return "/admin/user/create-user";
    }

    @RequestMapping(value = "/admin/create-user", method = RequestMethod.POST)
    public String createUser(Model model, @Valid @ModelAttribute(name = USER_FORM) UserForm userForm,
                             BindingResult bindingResult, RedirectAttributes redirectAttributes){

        if(bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userForm", bindingResult);
            redirectAttributes.addFlashAttribute(USER_FORM, userForm);
            redirectAttributes.addFlashAttribute("message", "Please fill the fields again");
            redirectAttributes.addFlashAttribute("errorMessage", "Create user failed!");
            return "redirect:/admin/create-user";
        }

        User user = UserConverter.buildUserObjecr(userForm);
        Member member;
        try {
            user = userService.insertUser(user);
            member = (Member) user;  // Downcasting

            // If user is admin (true), redirect a message and user id to create vehicle page
            if(!user.getUserType()){
                String message = "Add vehicle for user: " + member.getFirstname() + " " + member.getLastname() + " - " + member.getVat();
                redirectAttributes.addFlashAttribute("message", message);
                redirectAttributes.addFlashAttribute("userId", user.getUserId());
                return "redirect:/admin/create-vehicle";
            }

            String message = "New user is created: " + member.getFirstname() + " " + member.getLastname() + " - " + member.getVat();
            redirectAttributes.addFlashAttribute("message", message);
            redirectAttributes.addFlashAttribute("userId", member.getUserId());

        }catch (Exception e){
            redirectAttributes.addFlashAttribute("message", "There is already an account with same VAT or email.");
            redirectAttributes.addFlashAttribute(USER_FORM, userForm);
        }

        return "redirect:/admin/create-user";
    }

    @RequestMapping(value = "/admin/edit-user", method = RequestMethod.GET)
    public String getEditUserView(Model model, UserForm userForm){
        userForm.setFirstname("Akis");
        userForm.setLastname("Dimopoulos");
        model.addAttribute("userForm", userForm);
        return "/admin/user/edit-user";
    }
}

package com.carRepair.carRepair.web.admin_controllers.user;

import com.carRepair.carRepair.domain.Member;
import com.carRepair.carRepair.exceptions.UserNotFoundException;
import com.carRepair.carRepair.services.member.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserDeleteController {

    @Autowired
    private MemberService memberService;

    @RequestMapping(value = "/admin/delete-user", method = RequestMethod.POST)
    String deleteUser(Model model , @RequestParam("hidden_email" )String hidden_email ){
        try {
           Member member = memberService.getMemberByEmail(hidden_email);
           memberService.deleteMember(member.getUserId());

        } catch (UserNotFoundException userNotFound) {
            model.addAttribute("errorMessage", "Can t find user!" );

        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", "Something went wrong");
        }

        return "redirect:/admin/search-user";
    }
}
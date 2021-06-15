package ru.geekbrains.erpsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.geekbrains.erpsystem.services.UserService;

@Controller
@RequestMapping("/")
public class LoginController {
    private UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("login")
    public String login(){
        return "login_my_form";
    }
    @GetMapping("/logout")
    public String logaut(){
        return "redirect:/login";
    }
}

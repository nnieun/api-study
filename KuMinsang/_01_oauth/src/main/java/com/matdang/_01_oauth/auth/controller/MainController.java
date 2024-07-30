package com.matdang._01_oauth.auth.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class MainController {
    @GetMapping("/welcome")
    public void welcome(Model model, Authentication authentication){
        if(authentication != null){
            log.debug(authentication.getName());
            model.addAttribute("username", authentication.getName());
            log.debug("authentication = {}", authentication.toString());
        }
    }
}

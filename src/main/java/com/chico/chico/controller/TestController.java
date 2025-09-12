package com.chico.chico.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/not-the-landing-page")
    public String testPage() {
        return "Hi! this isn't the landing page, u must login to access it";
    }

    @GetMapping("/landing-page/test")
    public String sayHello() {
        return "Hello World! (landing page test)";
    }

}

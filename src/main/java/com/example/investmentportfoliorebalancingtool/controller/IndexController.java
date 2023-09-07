package com.example.investmentportfoliorebalancingtool.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    @RequestMapping({"", "/", "index", "index.html"})
    public String index() {
        return "index";
    }

    @RequestMapping({ "/about"})
    public String about() {
        return "about";
    }
}

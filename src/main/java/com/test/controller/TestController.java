package com.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("test")
public class TestController {
    @GetMapping(value = "/isOk", produces = "application/json;UTF-8")
    @ResponseBody
    public String isOk () {
        return "ok";
    }
}

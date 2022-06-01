package com.example.esjd.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Description:TODO
 * @Author: TGP
 * @Date: 2022/5/11 16:20
 **/
@Controller
public class IndexController {
    @GetMapping({"/","/index"})
    public String index(){
        return "index";
    }
}

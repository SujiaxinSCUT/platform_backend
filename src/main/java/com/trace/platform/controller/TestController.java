package com.trace.platform.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

    @RequestMapping("/trace/test")
    @ResponseBody
    public String test() {
        return "测试成功！";
    }

    @RequestMapping("/trace/admin/fund/test")
    @ResponseBody
    public String testFund() { return "测试成功!"; }

    @RequestMapping("/trace/admin/quality/test")
    @ResponseBody
    public String testQuality() { return "测试成功!"; }


    public static void main(String arg[]) {
        System.out.println(new BCryptPasswordEncoder().encode("123"));
    }
}

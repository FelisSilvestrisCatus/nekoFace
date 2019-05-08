package com.example.demo;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class api {


    @RequestMapping(value = "/facedetection")
    public String index(String temppath, String uid, String type) {
        System.out.println("temppath = " + temppath);
        System.out.println("uid = " + uid);
        System.out.println("type = " + type);
        Face.facedetection(temppath, uid, type);
        return "over ....\n";
    }


}

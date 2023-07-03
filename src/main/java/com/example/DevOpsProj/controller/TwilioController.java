//package com.example.DevOpsProj.controller;
//
//import com.example.DevOpsProj.service.TrilioService.PhoneVerificationService;
//import com.example.DevOpsProj.service.TrilioService.VerificationResult;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//@Controller
//@RequestMapping("/api/otp")
//public class TwilioController {
//
//    @Autowired
//    private PhoneVerificationService phoneVerificationService;
//
//    @PostMapping("/sendOTP")
//    public ResponseEntity<String> sendOTP(@RequestParam("phone") String phone){
//        VerificationResult result = phoneVerificationService.startVerification(phone);
//        System.out.println(result);
//        if(result.isValid()){
//            return new ResponseEntity<>("OTP Sent", HttpStatus.OK);
//        }
//        return new ResponseEntity<>("Failed to send OTP", HttpStatus.BAD_REQUEST);
//    }
//}

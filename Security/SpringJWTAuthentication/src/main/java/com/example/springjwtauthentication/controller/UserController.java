package com.example.springjwtauthentication.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.springjwtauthentication.model.persistence.dto.UserDto;
import com.example.springjwtauthentication.model.persistence.entity.User;
import com.example.springjwtauthentication.service.UserService;

/**
 * for testing purpose
 * The idea is to register a user in DDBB with no restriction,
 * then retrieve a token with its credentials,
 * and with that token call another method that is protected
 * */

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user")
    public List listUser(){
        return userService.findAll();
    }

    @GetMapping("/user/{id}")
    public User getOne(@PathVariable(value = "id") Long id){
        return userService.findById(id);
    }

    @PostMapping("/signup")
    public User saveUser(@RequestBody UserDto user) throws Exception {
    	return userService.save(user);
    }
    
}

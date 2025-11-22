package com.example.demoSpring17.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestApiController {

  @GetMapping("/hello")
  public String sayHello() {
    return "Hello from Spring Boot!";
  }

  @PostMapping("/hello")
  public String createHello(@RequestBody String name) {
    return "Hello, " + name + "!";
  }
}

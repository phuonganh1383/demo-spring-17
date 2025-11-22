package com.example.demoSpring17.controllers;

import com.example.demoSpring17.dto.requestDto.AuthenticationRequest;
import com.example.demoSpring17.dto.requestDto.IntrospectRequest;
import com.example.demoSpring17.dto.responseDto.IntrospectResponse;
import com.example.demoSpring17.models.User;
import com.example.demoSpring17.services.AuthenticationService;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

  AuthenticationService authenticationService;

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest) {
    var res = authenticationService.authenticate(
        authenticationRequest.getUsername(),
        authenticationRequest.getPassword()
    );
    return ResponseEntity.ok(res);
  }

  @PostMapping("/introspect")
  public ResponseEntity<IntrospectResponse> login(@RequestBody IntrospectRequest request)
          throws ParseException, JOSEException {
    var res = authenticationService.introspectToken(request);
    return ResponseEntity.ok(res);
  }
}

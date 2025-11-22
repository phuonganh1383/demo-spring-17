package com.example.demoSpring17.services;

import com.example.demoSpring17.dto.requestDto.IntrospectRequest;
import com.example.demoSpring17.dto.responseDto.IntrospectResponse;
import com.example.demoSpring17.repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
  UserRepository userRepository;
  PasswordEncoder passwordEncoder;

  @NonFinal

  @Value("${jwt.signerKey}")
  protected String SECRET_KEY;
//  protected  static final String SECRET_KEY = "619671ce0f18e191065f7a0376441c6c9bd3e64b15b263b19ad26586499ba867";

  public IntrospectResponse introspectToken(IntrospectRequest request)
          throws JOSEException, ParseException {
    // Logic to introspect token
    var token = request.getToken();
    JWSVerifier verifier = new MACVerifier(SECRET_KEY.getBytes());
    SignedJWT signedJWT = SignedJWT.parse(token);
    Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
    var verified = signedJWT.verify(verifier) && expirationTime.after(new Date());
    return IntrospectResponse.builder()
            .valid(verified)
        .build();
  }


  public String authenticate(String username, String password) {
    var user =  userRepository.findByUsername(username).orElseThrow(() ->
        new RuntimeException("User not found with username: " + username));
//    PasswordEncoder encoder = new BCryptPasswordEncoder(10);
    boolean matches = passwordEncoder.matches(password, user.getPassword());
    if (!matches) {
      throw new RuntimeException("Authentication failed");
    }
    return generateToken(username);
  }

  private String generateToken(String username) {
    // Logic to generate JWT or any other token
    JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

    JWTClaimsSet claims = new JWTClaimsSet.Builder()
        .subject(username)
        .issuer("demoSpring17")
            .issueTime(new Date())
            .expirationTime(new Date(Instant.now()
                    .plus(1, ChronoUnit.HOURS).toEpochMilli()))
            .claim("customClaim", "custom")
        .build();

    Payload payload = new Payload(claims.toJSONObject());

    try {
      JWSObject jwsObject = new JWSObject(jwsHeader, payload);
      jwsObject.sign(new MACSigner(SECRET_KEY.getBytes()));
      return jwsObject.serialize();
    } catch (JOSEException e) {
        throw new RuntimeException("Error generating token", e);
    }

  }
}

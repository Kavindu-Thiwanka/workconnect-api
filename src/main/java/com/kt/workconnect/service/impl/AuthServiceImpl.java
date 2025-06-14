package com.kt.workconnect.service.impl;

import com.kt.workconnect.dto.RegisterRequest;
import com.kt.workconnect.entity.User;
import com.kt.workconnect.repository.UserRepository;
import com.kt.workconnect.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CognitoIdentityProviderClient cognitoClient;

    @Value("${aws.cognito.userPoolId}")
    private String userPoolId;

    @Value("${aws.cognito.appClientId}")
    private String appClientId;

    @Value("${aws.cognito.appClientSecret}")
    private String appClientSecret;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> registerUser(RegisterRequest registerRequest) {
        try {
            List<AttributeType> userAttrs = new ArrayList<>();
            userAttrs.add(AttributeType.builder().name("email").value(registerRequest.getEmail()).build());
            userAttrs.add(AttributeType.builder().name("given_name").value(registerRequest.getFirstName()).build());
            userAttrs.add(AttributeType.builder().name("family_name").value(registerRequest.getLastName()).build());
            userAttrs.add(AttributeType.builder().name("custom:user_role").value(registerRequest.getUserRole().name()).build());

            String secretHash = calculateSecretHash(registerRequest.getEmail(), appClientId, appClientSecret);

            SignUpRequest signUpRequest = SignUpRequest.builder()
                    .clientId(appClientId)
                    .username(registerRequest.getEmail())
                    .password(registerRequest.getPassword())
                    .userAttributes(userAttrs)
                    .secretHash(secretHash)
                    .build();

            SignUpResponse signUpResponse = cognitoClient.signUp(signUpRequest);
            String cognitoSub = signUpResponse.userSub();

            User newUser = new User();
            newUser.setCognitoSub(cognitoSub);
            newUser.setEmail(registerRequest.getEmail());
            newUser.setUserRole(registerRequest.getUserRole());
            userRepository.save(newUser);

            return ResponseEntity.ok(newUser);

        } catch (UsernameExistsException e) {
            throw new IllegalStateException("User with this email already exists.");
        } catch (CognitoIdentityProviderException e) {
            throw new RuntimeException("Error during Cognito sign-up: " + e.getMessage(), e);
        }
    }

    private String calculateSecretHash(String username, String clientId, String clientSecret) {
        final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
        SecretKeySpec signingKey = new SecretKeySpec(
                clientSecret.getBytes(StandardCharsets.UTF_8),
                HMAC_SHA256_ALGORITHM);
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(signingKey);
            mac.update(username.getBytes(StandardCharsets.UTF_8));
            byte[] rawHmac = mac.doFinal(clientId.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Error while calculating SecretHash", e);
        }
    }
}

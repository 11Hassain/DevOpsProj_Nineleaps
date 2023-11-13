package com.example.devopsproj.controller;

import com.example.devopsproj.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * The AuthController class is responsible for handling RESTful API endpoints related to user authentication and authorization.
 * It provides an endpoint for retrieving an email from a token for login verification purposes.
 * .
 * This controller integrates with the userService to perform user authentication and verification.
 *
 * @version 2.0
 */

@RestController
@RequestMapping("/auth")
@Validated
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    /**
     * Get the email associated with a verification token.
     *
     * @param emailToVerify The email verification token to retrieve the associated email.
     * @return ResponseEntity with the retrieved email or a not found status if not found.
     */
    @GetMapping("/api/v1/get-email")
    @Operation(
            description = "Get Email From Token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Email retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Email not found")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getEmailFromToken(@RequestHeader("emailToVerify") String emailToVerify) {
        logger.info("Received a request to get email from verification token: {}", emailToVerify);

        Object object = userService.loginVerification(emailToVerify);

        if (object == null) {
            logger.info("Email not found for verification token: {}", emailToVerify);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            logger.info("Email retrieved successfully for verification token: {}", emailToVerify);
            return ResponseEntity.ok(userService.loginVerification(emailToVerify));
        }
    }

}


package com.example.devopsproj.model;

import com.example.devopsproj.commons.enumerations.TokenType;
import jakarta.persistence.*;
import lombok.*;

/**
 * The Token class represents an authentication token used in a DevOps project. It stores information such as the token's
 * identifier, type, revocation status, expiration status, and the user it is associated with.
 * .
 * This class is mapped to a database table and is part of the authentication and authorization system for the project.
 *
 * @version 2.0
 */

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(unique = true)
    private String tokenId;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType = TokenType.BEARER;

    @Getter
    @Setter
    private boolean revoked;

    @Getter
    @Setter
    private boolean expired;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
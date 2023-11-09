package com.example.devopsproj.model;

import com.example.devopsproj.commons.enumerations.TokenType;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a user token in the database.
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
    public Integer id;

    @Column(unique = true)
    public String tokens;

    @Enumerated(EnumType.STRING)
    public TokenType tokenType = TokenType.BEARER;

    private boolean revoked;

    private boolean expired;

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User user;

    public Token(String tokens) {
        this.tokens = tokens;
        // Initialize other properties as needed
    }
}

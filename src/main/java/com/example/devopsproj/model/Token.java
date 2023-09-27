package com.example.devopsproj.model;

import com.example.devopsproj.commons.enumerations.TokenType;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token {

    @Id
    @GeneratedValue
    public Integer id;

    @Column(unique = true)
    public String tokenId;

    @Enumerated(EnumType.STRING)
    public TokenType tokenType = TokenType.BEARER;

    @Getter
    @Setter
    private boolean revoked;

    @Getter
    @Setter
    private boolean expired;

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User user;
}
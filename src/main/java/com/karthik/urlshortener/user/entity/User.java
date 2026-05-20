package com.karthik.urlshortener.user.entity;

import com.karthik.urlshortener.url.entity.Url;
import com.karthik.urlshortener.user.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    private Instant createdAt;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Url> urls = new ArrayList<>();

    @PrePersist
    public void prePersist(){
        this.createdAt = Instant.now();
    }
}

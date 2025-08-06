package com.fcmh.femcodersmentorhub.auth;

import com.fcmh.femcodersmentorhub.mentees.MenteeProfile;
import com.fcmh.femcodersmentorhub.mentors.MentorProfile;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "password", nullable = false, length = 150)
    private String password;

    @Column(name = "role", length = 20)
    @Enumerated(EnumType.STRING)
    private Role role;

    //mappedBy = "user"
    @OneToOne
    private MentorProfile mentorProfiles;

    //mappedBy = "user"
    @OneToOne
    private MenteeProfile menteeProfiles;

    public User(String username, String email, String password, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}

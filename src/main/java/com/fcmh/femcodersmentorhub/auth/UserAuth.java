package com.fcmh.femcodersmentorhub.auth;

import com.fcmh.femcodersmentorhub.mentees.MenteeProfile;
import com.fcmh.femcodersmentorhub.mentors.MentorProfile;
import com.fcmh.femcodersmentorhub.security.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAuth {
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
}

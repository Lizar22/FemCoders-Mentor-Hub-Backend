package com.fcmh.femcodersmentorhub.mentors;

import com.fcmh.femcodersmentorhub.auth.UserAuth;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "mentor_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @ElementCollection
    @CollectionTable(name = "mentor_technologies", joinColumns = @JoinColumn(name = "mentor_id"))
    @Column(name = "technologies", nullable = false)
    private List<String> technologies;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Level level;

    @Column(length = 1000)
    private String bio;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserAuth user;
}

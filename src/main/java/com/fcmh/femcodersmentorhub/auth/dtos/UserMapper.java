package com.fcmh.femcodersmentorhub.auth.dtos;

import com.fcmh.femcodersmentorhub.auth.User;
import org.hibernate.mapping.List;

public class UserMapper {
    public static User dtoToEntity(UserRequest dto) {
        return new User(
                dto.username(),
                dto.email(),
                dto.password(),
                dto.role()
        );
    }

    public static UserResponse entityToDto(User user) {
        return new UserResponse(
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }
}

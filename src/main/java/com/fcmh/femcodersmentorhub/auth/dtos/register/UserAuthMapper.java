package com.fcmh.femcodersmentorhub.auth.dtos.register;

import com.fcmh.femcodersmentorhub.auth.UserAuth;

public class UserAuthMapper {
    public static UserAuth dtoToEntity(UserAuthRequest dto) {
        return UserAuth.builder()
                .username(dto.username().trim())
                .email(dto.email().trim())
                .password(dto.password().trim())
                .role(dto.role()).build();
    }

    public static UserAuthResponse entityToDto(UserAuth userAuth) {
        return new UserAuthResponse(
                userAuth.getUsername(),
                userAuth.getEmail(),
                userAuth.getRole()
        );
    }
}
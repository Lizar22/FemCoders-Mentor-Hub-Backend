package com.fcmh.femcodersmentorhub.auth.dtos;

import com.fcmh.femcodersmentorhub.auth.UserAuth;

public class UserAuthMapper {
    public static UserAuth dtoToEntity(UserAuthRequest dto) {
        return new UserAuth(
                dto.username(),
                dto.email(),
                dto.password(),
                dto.role()
        );
    }

    public static UserAuthResponse entityToDto(UserAuth userAuth) {
        return new UserAuthResponse(
                userAuth.getUsername(),
                userAuth.getEmail(),
                userAuth.getRole()
        );
    }
}

package com.ms.security;

import com.ms.hogwartsuser.HogwartsUser;
import com.ms.hogwartsuser.MyUserPrincipal;
import com.ms.hogwartsuser.converter.UserToUserDtoConverter;
import com.ms.hogwartsuser.dto.UserDto;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService
{
    private final JwtProvider jwtProvider;
    private final UserToUserDtoConverter userToUserDtoConverter;

    public AuthService(JwtProvider jwtProvider, UserToUserDtoConverter userToUserDtoConverter) {
        this.jwtProvider = jwtProvider;
        this.userToUserDtoConverter = userToUserDtoConverter;
    }

    public Map<String,Object> createLoginInfo(Authentication authentication) {
        //create user info
        MyUserPrincipal principal = (MyUserPrincipal) authentication.getPrincipal();

        HogwartsUser hogwartsUser = principal.getHogwartsUser();
        UserDto userDto = userToUserDtoConverter.convert(hogwartsUser);

        // create a JWT
        String token = jwtProvider.createToken(authentication);

        Map<String,Object> loginResultMap = new HashMap<>();

        loginResultMap.put("userInfo",userDto);
        loginResultMap.put("token",token);


        return loginResultMap;
    }
}

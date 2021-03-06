package co.edu.javeriana.eas.patterns.users.mappers;

import co.edu.javeriana.eas.patterns.persistence.entities.UserEntity;
import co.edu.javeriana.eas.patterns.users.dtos.AuthenticationInfoDto;

import java.util.Objects;

public class LoginMapper {

    private LoginMapper() {

    }

    public static AuthenticationInfoDto userEntityMapperInAuthenticationInfo(UserEntity userEntity) {
        AuthenticationInfoDto authenticationInfoDto = new AuthenticationInfoDto();
        authenticationInfoDto.setUserId(userEntity.getId());
        if (Objects.nonNull(userEntity.getPerson().getProvider())) {
            authenticationInfoDto.setProviderId(userEntity.getPerson().getProvider().getId());
        }
        authenticationInfoDto.setUserCode(userEntity.getUserCode());
        authenticationInfoDto.setFirstName(userEntity.getPerson().getFirstName());
        authenticationInfoDto.setLastName(userEntity.getPerson().getLastName());
        authenticationInfoDto.setProfile(userEntity.getProfile().getProfileDescription());
        return authenticationInfoDto;
    }

}
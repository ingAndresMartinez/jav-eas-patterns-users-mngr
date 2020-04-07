package co.edu.javeriana.eas.patterns.users.services.abstracts;

import co.edu.javeriana.eas.patterns.persistence.entities.PersonEntity;
import co.edu.javeriana.eas.patterns.persistence.entities.ProfileEntity;
import co.edu.javeriana.eas.patterns.persistence.entities.UserEntity;
import co.edu.javeriana.eas.patterns.persistence.entities.UserStatusEntity;
import co.edu.javeriana.eas.patterns.persistence.repositories.IPersonRepository;
import co.edu.javeriana.eas.patterns.persistence.repositories.IUserRepository;
import co.edu.javeriana.eas.patterns.persistence.repositories.IUserStatusRepository;
import co.edu.javeriana.eas.patterns.users.dtos.UserCreateDto;
import co.edu.javeriana.eas.patterns.users.enums.EUserStatus;
import co.edu.javeriana.eas.patterns.users.exceptions.CreateUserException;
import co.edu.javeriana.eas.patterns.users.mappers.UserMapper;
import co.edu.javeriana.eas.patterns.users.services.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class UserServiceAbs implements IUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceAbs.class);

    private UserStatusEntity userActive;
    private UserStatusEntity userInactive;

    protected String milestone;
    protected ProfileEntity profileEntity;

    private IPersonRepository personRepository;
    private IUserRepository userRepository;
    private IUserStatusRepository userStatusRepository;

    @Override
    public void createUser(UserCreateDto userCreateDto) throws CreateUserException {
        LOGGER.info("INICIA CREACIÓN DE NUEVO USUARIO [{}] CON PERFIL [{}]", userCreateDto.getUserCode(), milestone);
        PersonEntity personEntity = createEntityPerson(userCreateDto);
        personRepository.save(personEntity);
        UserEntity userEntity = createUserEntity(userCreateDto, personEntity);
        userRepository.save(userEntity);
        LOGGER.info("FINALIZA CREACIÓN DE NUEVO USUARIO [{}] CON PERFIL [{}]", userCreateDto.getUserCode(), milestone);
    }

    @Override
    public void updateUser() {

    }

    protected abstract PersonEntity createEntityPerson(UserCreateDto userCreateDto) throws CreateUserException;

    protected PersonEntity personEntityBase(UserCreateDto userCreateDto) {
        return UserMapper.userCreateMapperInPersonEntity(userCreateDto);
    }

    private UserEntity createUserEntity(UserCreateDto userCreateDto, PersonEntity personEntity) {
        UserEntity userEntity = UserMapper.userCreateMapperInUserEntity(userCreateDto);
        userEntity.setProfile(profileEntity);
        userEntity.setPerson(personEntity);
        userEntity.setStatus(userActive);
        return userEntity;
    }


    @Autowired
    public void setPersonRepository(IPersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Autowired
    public void setUserRepository(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setUserStatusRepository(IUserStatusRepository userStatusRepository) {
        this.userStatusRepository = userStatusRepository;
    }

    @Autowired
    public void setUserActive() {
        this.userActive = userStatusRepository.findById(EUserStatus.ACTIVE.getStatus()).orElse(new UserStatusEntity());
    }

    @Autowired
    public void setUserInactive() {
        this.userInactive = userStatusRepository.findById(EUserStatus.INACTIVE.getStatus()).orElse(new UserStatusEntity());
    }
}
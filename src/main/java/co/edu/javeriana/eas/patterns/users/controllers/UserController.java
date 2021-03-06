package co.edu.javeriana.eas.patterns.users.controllers;

import co.edu.javeriana.eas.patterns.common.enums.EExceptionCode;
import co.edu.javeriana.eas.patterns.users.dtos.*;
import co.edu.javeriana.eas.patterns.users.exceptions.AuthenticationException;
import co.edu.javeriana.eas.patterns.users.exceptions.CreateUserException;
import co.edu.javeriana.eas.patterns.users.exceptions.UpdateUserException;
import co.edu.javeriana.eas.patterns.users.services.IAuthenticationService;
import co.edu.javeriana.eas.patterns.users.services.IHandlerUserManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private IAuthenticationService authenticatorService;
    private IHandlerUserManagementService handlerUserCreateService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserInfoDto> getUserInformation(@PathVariable int userId) {
        LOGGER.info("INICIA PROCESO DE CONSULTA DE USUARIO [{}]", userId);
        try {
            LOGGER.info("FINALIZA PROCESO DE CONSULTA DE USUARIO [{}]", userId);
            return new ResponseEntity<>(handlerUserCreateService.getInfoUser(userId), HttpStatus.OK);
        } catch (AuthenticationException e) {
            LOGGER.error("ERROR EN CONSULTA DE USUARIO", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationInfoDto> loginUser(@Valid @RequestBody LoginParamDto loginParamDto) {
        LOGGER.info("INICIA PROCESO DE LOGIN PARA EL USUARIO [{}]", loginParamDto.getUserCode());
        AuthenticationInfoDto response;
        try {
            response = authenticatorService.login(loginParamDto);
        } catch (AuthenticationException e) {
            LOGGER.error("ERROR EN AUTENTICACIÓN", e);
            return handleAuthenticationException(e);
        }
        LOGGER.info("FINALIZA PROCESO DE LOGIN PARA EL USUARIO [{}] CON RESULTADO [{}]", loginParamDto.getUserCode(), response);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @PostMapping
    public ResponseEntity<Void> createUser(@Valid @RequestBody UserCreateDto userCreateDto) {
        LOGGER.info("INICIA PROCESO DE CREACIÓN DE USUARIO [{}]", userCreateDto.getUserCode());
        try {
            handlerUserCreateService.defineAndCreateUserFromInputProfile(userCreateDto);
        } catch (CreateUserException e) {
            LOGGER.error("ERROR EN CREACION DE USUARIO", e);
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        LOGGER.info("FINALIZA PROCESO DE CREACIÓN DE USUARIO [{}]", userCreateDto.getUserCode());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Void> updateUser(@PathVariable int userId, @RequestBody UserUpdateDto userUpdateDto) {
        try {
            handlerUserCreateService.updateUser(userId, userUpdateDto);
        } catch (UpdateUserException e) {
            LOGGER.error("ERROR EN MODIFICACIÓN DE USUARIO", e);
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PutMapping("/status/{userId}")
    public ResponseEntity<Void> updateStatusUser(@PathVariable int userId) {
        try {
            handlerUserCreateService.updateStatusUser(userId);
        } catch (UpdateUserException e) {
            LOGGER.error("ERROR EN MODIFICACIÓN DE USUARIO", e);
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    private ResponseEntity<AuthenticationInfoDto> handleAuthenticationException(AuthenticationException e) {
        ResponseEntity<AuthenticationInfoDto> errorResponse = null;
        if (e.getExceptionCode() == EExceptionCode.USER_OR_PASSWORD_INVALID) {
            errorResponse = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (e.getExceptionCode() == EExceptionCode.USER_BLOCKED) {
            errorResponse = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return errorResponse;
    }

    @Autowired
    public void setAuthenticatorService(IAuthenticationService authenticatorService) {
        this.authenticatorService = authenticatorService;
    }

    @Autowired
    public void setHandlerUserCreateService(IHandlerUserManagementService handlerUserCreateService) {
        this.handlerUserCreateService = handlerUserCreateService;
    }

}
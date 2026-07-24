package arda.xmlproject.demo.controller;

import arda.xmlproject.demo.dto.UserDto;
import arda.xmlproject.demo.entities.UserEntity;
import arda.xmlproject.demo.entities.UserRoles;
import arda.xmlproject.demo.mappers.Mapper;
import arda.xmlproject.demo.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserController {

    public final UserService userService;
    private final Mapper<UserEntity, UserDto> userMapper;

    public UserController(UserService userService, Mapper<UserEntity, UserDto> userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping(path = "/users")
    public UserDto createUser(@RequestBody UserDto user) {
        UserEntity userEntity = userMapper.mapFrom(user);

        userEntity.setRole(UserRoles.USER);
        userEntity.setPermissions(null);

        UserEntity savedUserEntity = userService.save(userEntity);
        return userMapper.mapTo(savedUserEntity);

    }

    @GetMapping(path = "/users")
    public List<UserDto> listUsers() {
        List<UserEntity> users = userService.findAll();
        return users.stream()
                .map(userMapper::mapTo)
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/users/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable("id") Long id) {
        Optional<UserEntity> foundUser = userService.findOne(id);

        return foundUser.map(userEntity -> {
            UserDto userDto = userMapper.mapTo(userEntity);
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping(path = "/users/{id}")
    public ResponseEntity<UserDto> fullUpdateUser(@PathVariable("id") Long id, @RequestBody UserDto userDto) {
        if (!userService.isExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        userDto.setId(id);
        UserEntity userEntity = userMapper.mapFrom(userDto);

        userEntity.setRole(null);
        userEntity.setPermissions(null);


        UserEntity savedUserEntity = userService.save(userEntity);

        return new ResponseEntity<>(userMapper.mapTo(savedUserEntity), HttpStatus.OK);
    }


    @PatchMapping(path = "/users/{id}")
    public ResponseEntity<UserDto> partialUpdate(@PathVariable("id") Long id, @RequestBody UserDto userDto) {
        if (!userService.isExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        userDto.setRole(null);
        userDto.setPermissions(null);


        UserEntity userEntity = userMapper.mapFrom(userDto);
        UserEntity updatedUser = userService.partialUpdate(id, userEntity);
        return new ResponseEntity<>(
                userMapper.mapTo(updatedUser),
                HttpStatus.OK);
    }

    @DeleteMapping(path = "/users/{id}")
    public ResponseEntity deleteUser(@PathVariable("id") Long id) {
        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

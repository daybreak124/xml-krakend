package arda.xmlproject.demo.services;

import arda.xmlproject.demo.entities.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserService {

    UserEntity save(UserEntity userEntity);
    List<UserEntity> findAll();
    Optional<UserEntity> findOne(Long id);
    Optional<UserEntity> findByUsername(String username);
    boolean isExists(Long id);


    UserEntity partialUpdate(Long id, UserEntity userEntity);
    void delete(Long id);
}

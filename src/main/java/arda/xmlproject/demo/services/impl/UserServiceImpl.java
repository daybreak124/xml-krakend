package arda.xmlproject.demo.services.impl;

import arda.xmlproject.demo.entities.UserEntity;
import arda.xmlproject.demo.repositories.UserRepository;
import arda.xmlproject.demo.services.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserEntity save(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }

    @Override
    public List<UserEntity> findAll() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    @Override
    public Optional<UserEntity> findOne(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public boolean isExists(Long id) {
        return userRepository.existsById(id);
    }

    @Override
    public UserEntity partialUpdate(Long id, UserEntity userEntity) {
        return userRepository.findById(id).map(existingUser -> {
            Optional.ofNullable(userEntity.getPermissions()).ifPresent(existingUser::setPermissions);
            Optional.ofNullable(userEntity.getName()).ifPresent(existingUser::setName);
            Optional.ofNullable(userEntity.getRole()).ifPresent(existingUser::setRole);
            Optional.ofNullable(userEntity.getLastName()).ifPresent(existingUser::setLastName);
            Optional.ofNullable(userEntity.getAccountCreatedAt()).ifPresent(existingUser::setAccountCreatedAt);
            Optional.ofNullable(userEntity.getLastAccessAt()).ifPresent(existingUser::setLastAccessAt);
            Optional.ofNullable(userEntity.getUsername()).ifPresent(existingUser::setUsername);
            Optional.ofNullable(userEntity.getPassword()).ifPresent(existingUser::setPassword);

            return userRepository.save(existingUser);

        }).orElseThrow(() -> new RuntimeException("User does not exist"));
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}

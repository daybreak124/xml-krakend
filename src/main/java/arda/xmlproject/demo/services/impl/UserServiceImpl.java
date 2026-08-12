package arda.xmlproject.demo.services.impl;

import arda.xmlproject.demo.controller.AuthController;
import arda.xmlproject.demo.entities.UserEntity;
import arda.xmlproject.demo.repositories.RefreshTokenRepository;
import arda.xmlproject.demo.repositories.UserRepository;
import arda.xmlproject.demo.services.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static arda.xmlproject.demo.controller.AuthController.hashPassword;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public UserServiceImpl(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
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
    @Transactional
    public UserEntity partialUpdate(Long id, UserEntity userEntity) {
        return userRepository.findById(id).map(existingUser -> {

            boolean securityChanged =
                    userEntity.getPermissions() != null ||
                            userEntity.getRole() != null ||
                            userEntity.getPassword() != null ||
                            userEntity.getUsername() != null;

            Optional.ofNullable(userEntity.getPermissions()).ifPresent(existingUser::setPermissions);
            Optional.ofNullable(userEntity.getId()).ifPresent(existingUser::setId);
            Optional.ofNullable(userEntity.getSsn()).ifPresent(existingUser::setSsn);
            Optional.ofNullable(userEntity.getName()).ifPresent(existingUser::setName);
            Optional.ofNullable(userEntity.getRole()).ifPresent(existingUser::setRole);
            Optional.ofNullable(userEntity.getLastName()).ifPresent(existingUser::setLastName);
            Optional.ofNullable(userEntity.getAccountCreatedAt()).ifPresent(existingUser::setAccountCreatedAt);
            Optional.ofNullable(userEntity.getLastAccessAt()).ifPresent(existingUser::setLastAccessAt);
            Optional.ofNullable(userEntity.getUsername()).ifPresent(existingUser::setUsername);
            Optional.ofNullable(userEntity.getPassword()).map(AuthController::hashPassword).ifPresent(existingUser::setPassword);
            UserEntity updatedUser = userRepository.save(existingUser);


            if (securityChanged) refreshTokenRepository.deleteByUser(updatedUser);


            return updatedUser;

        }).orElseThrow(() -> new RuntimeException("User does not exist"));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}

package org.example.userservice.service;

import org.example.userservice.dao.UserDao;
import org.example.userservice.dao.UserDaoImpl;
import org.example.userservice.entity.User;

import java.util.List;
import java.util.Optional;

public class UserService {

    private final UserDao userDao;

    public UserService() {
        this.userDao = new UserDaoImpl();
    }

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    // CREATE
    public User createUser(String name, String email, int age) {

        validateUserData(name, email, age);

        User user = new User(
                name,
                email,
                age
        );

        return userDao.save(user);
    }

    // READ BY ID
    public Optional<User> getUserById(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "ID должен быть положительным числом"
            );
        }

        return userDao.findById(id);
    }

    // READ ALL
    public List<User> getAllUsers() {

        return userDao.findAll();
    }

    // UPDATE
    public User updateUser(
            Long id,
            String name,
            String email,
            int age
    ) {

        validateUserData(name, email, age);

        Optional<User> existingUser =
                userDao.findById(id);

        if (existingUser.isEmpty()) {
            throw new IllegalArgumentException(
                    "Пользователь с ID " + id + " не найден"
            );
        }

        User user = existingUser.get();

        user.setName(name);
        user.setEmail(email);
        user.setAge(age);

        return userDao.update(user);
    }

    // DELETE
    public void deleteUser(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "ID должен быть положительным числом"
            );
        }

        Optional<User> existingUser =
                userDao.findById(id);

        if (existingUser.isEmpty()) {
            throw new IllegalArgumentException(
                    "Пользователь с ID " + id + " не найден"
            );
        }

        userDao.delete(id);
    }

    // Валидация
    private void validateUserData(
            String name,
            String email,
            int age
    ) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Имя не может быть пустым"
            );
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email не может быть пустым"
            );
        }

        if (!email.contains("@")) {
            throw new IllegalArgumentException(
                    "Некорректный email"
            );
        }

        if (age <= 0 || age > 150) {
            throw new IllegalArgumentException(
                    "Возраст должен быть от 1 до 150"
            );
        }
    }
}
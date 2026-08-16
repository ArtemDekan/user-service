package org.example.userservice.console;

import org.example.userservice.config.HibernateUtil;
import org.example.userservice.entity.User;
import org.example.userservice.service.UserService;

import java.util.List;
import java.util.Optional;

public class ServiceTest {

    public static void main(String[] args) {

        UserService userService = new UserService();

        try {

            // =========================
            // CREATE
            // =========================

            System.out.println("=== CREATE ===");

            User createdUser = userService.createUser(
                    "Alex",
                    "alex" + System.currentTimeMillis() + "@gmail.com",
                    28
            );

            System.out.println("Создан:");
            System.out.println(createdUser);


            // =========================
            // READ BY ID
            // =========================

            System.out.println("\n=== READ BY ID ===");

            Optional<User> foundUser =
                    userService.getUserById(createdUser.getId());

            foundUser.ifPresentOrElse(
                    user -> System.out.println(
                            "Найден: " + user
                    ),
                    () -> System.out.println(
                            "Пользователь не найден"
                    )
            );


            // =========================
            // READ ALL
            // =========================

            System.out.println("\n=== READ ALL ===");

            List<User> users =
                    userService.getAllUsers();

            users.forEach(System.out::println);


            // =========================
            // UPDATE
            // =========================

            System.out.println("\n=== UPDATE ===");

            User updatedUser =
                    userService.updateUser(
                            createdUser.getId(),
                            "Alex Updated",
                            createdUser.getEmail(),
                            35
                    );

            System.out.println("Обновлён:");
            System.out.println(updatedUser);


            // =========================
            // DELETE
            // =========================

            System.out.println("\n=== DELETE ===");

            userService.deleteUser(
                    createdUser.getId()
            );

            System.out.println(
                    "Пользователь удалён"
            );


        } catch (Exception e) {

            System.err.println(
                    "Произошла ошибка:"
            );

            e.printStackTrace();

        } finally {

            HibernateUtil.shutdown();
        }
    }
}
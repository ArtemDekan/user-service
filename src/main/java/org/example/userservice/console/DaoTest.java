package org.example.userservice.console;

import org.example.userservice.config.HibernateUtil;
import org.example.userservice.dao.UserDao;
import org.example.userservice.dao.UserDaoImpl;
import org.example.userservice.entity.User;

import java.util.List;
import java.util.Optional;

public class DaoTest {

    public static void main(String[] args) {

        UserDao userDao = new UserDaoImpl();

        try {

            // =========================
            // CREATE
            // =========================

            User user = new User(
                    "Alex",
                    "alex@gmail.com",
                    30
            );

            User savedUser = userDao.save(user);

            System.out.println("=== CREATE ===");
            System.out.println("Создан пользователь:");
            System.out.println(savedUser);


            // =========================
            // READ BY ID
            // =========================

            System.out.println("\n=== READ BY ID ===");

            Optional<User> foundUser =
                    userDao.findById(savedUser.getId());

            if (foundUser.isPresent()) {
                System.out.println("Пользователь найден:");
                System.out.println(foundUser.get());
            } else {
                System.out.println("Пользователь не найден");
            }


            // =========================
            // READ ALL
            // =========================

            System.out.println("\n=== READ ALL ===");

            List<User> users = userDao.findAll();

            for (User currentUser : users) {
                System.out.println(currentUser);
            }

            // =========================
            // UPDATE
            // =========================

            System.out.println("\n=== UPDATE ===");

            savedUser.setName("Petr Updated");
            savedUser.setAge(35);

            User updatedUser = userDao.update(savedUser);

            System.out.println("Пользователь обновлён:");
            System.out.println(updatedUser);

            // =========================
            // DELETE
            // =========================

            System.out.println("\n=== DELETE ===");

            userDao.delete(savedUser.getId());

            System.out.println(
                    "Пользователь с ID " +
                            savedUser.getId() +
                            " удалён"
            );


            // =========================
            // CHECK DELETE
            // =========================

            Optional<User> deletedUser =
                    userDao.findById(savedUser.getId());

            if (deletedUser.isEmpty()) {
                System.out.println(
                        "Проверка: пользователь действительно удалён"
                );
            } else {
                System.out.println(
                        "Ошибка: пользователь всё ещё существует"
                );
            }


        } catch (Exception e) {

            System.err.println("Произошла ошибка:");
            e.printStackTrace();

        } finally {

            HibernateUtil.shutdown();
        }
    }
}
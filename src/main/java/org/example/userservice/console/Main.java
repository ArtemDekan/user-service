package org.example.userservice.console;

import org.example.userservice.config.HibernateUtil;
import org.example.userservice.entity.User;
import org.example.userservice.exception.UserDaoException;
import org.example.userservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final UserService userService = new UserService();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        log.info("Приложение user-service запущено");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> createUser();
                    case "2" -> getUserById();
                    case "3" -> getAllUsers();
                    case "4" -> updateUser();
                    case "5" -> deleteUser();
                    case "0" -> running = false;
                    default -> System.out.println("Неизвестная команда, попробуйте снова.");
                }
            } catch (IllegalArgumentException e) {
                // ошибки валидации — это ожидаемо, пользователю просто говорим "что не так"
                System.out.println("Ошибка ввода: " + e.getMessage());
            } catch (UserDaoException e) {
                // ошибки Hibernate/БД — логируем полностью, пользователю — коротко
                log.error("Ошибка при работе с БД", e);
                System.out.println("Ошибка при работе с базой данных: " + e.getMessage());
            } catch (Exception e) {
                log.error("Непредвиденная ошибка", e);
                System.out.println("Непредвиденная ошибка: " + e.getMessage());
            }
        }

        HibernateUtil.shutdown();
        log.info("Приложение user-service остановлено");
        System.out.println("До свидания!");
    }

    private static void printMenu() {
        System.out.println("""
                
                === USER SERVICE ===
                1. Создать пользователя
                2. Найти пользователя по ID
                3. Показать всех пользователей
                4. Обновить пользователя
                5. Удалить пользователя
                0. Выход
                Выберите действие:""");
    }

    private static void createUser() {
        System.out.print("Имя: ");
        String name = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Возраст: ");
        int age = readInt();

        User user = userService.createUser(name, email, age);
        System.out.println("Создан: " + user);
    }

    private static void getUserById() {
        System.out.print("ID: ");
        Long id = readLong();

        Optional<User> user = userService.getUserById(id);
        System.out.println(user.map(User::toString).orElse("Пользователь не найден"));
    }

    private static void getAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("Пользователей нет");
        } else {
            users.forEach(System.out::println);
        }
    }

    private static void updateUser() {
        System.out.print("ID пользователя для обновления: ");
        Long id = readLong();

        System.out.print("Новое имя: ");
        String name = scanner.nextLine();

        System.out.print("Новый email: ");
        String email = scanner.nextLine();

        System.out.print("Новый возраст: ");
        int age = readInt();

        User updated = userService.updateUser(id, name, email, age);
        System.out.println("Обновлён: " + updated);
    }

    private static void deleteUser() {
        System.out.print("ID пользователя для удаления: ");
        Long id = readLong();

        userService.deleteUser(id);
        System.out.println("Пользователь удалён");
    }

    private static int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Введите корректное число");
        }
    }

    private static Long readLong() {
        try {
            return Long.parseLong(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Введите корректный числовой ID");
        }
    }
}
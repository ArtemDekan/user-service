package org.example.userservice.dao;

import org.example.userservice.entity.User;
import org.example.userservice.exception.UserDaoException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.junit.jupiter.api.*;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UserDaoImplIT {

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName("user_service_test")
            .withUsername("test")
            .withPassword("test");

    static SessionFactory sessionFactory;

    private UserDaoImpl userDao;

    @BeforeAll
    static void setUpSessionFactory() {
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        configuration.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        configuration.setProperty("hibernate.connection.username", postgres.getUsername());
        configuration.setProperty("hibernate.connection.password", postgres.getPassword());
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        configuration.setProperty("hibernate.hbm2ddl.auto", "update");
        configuration.addAnnotatedClass(User.class);

        ServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();

        sessionFactory = configuration.buildSessionFactory(registry);
    }

    @AfterAll
    static void tearDownSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @BeforeEach
    void setUp() {
        userDao = new UserDaoImpl(sessionFactory);

        // очищаем таблицу перед каждым тестом — изоляция между тестами
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    @DisplayName("save сохраняет пользователя и присваивает ID")
    void save_persistsUser() {
        User user = new User("Alex", "alex@mail.com", 30);

        User saved = userDao.save(user);

        assertNotNull(saved.getId());
        assertEquals("Alex", saved.getName());
    }

    @Test
    @DisplayName("save выбрасывает исключение при дублирующемся email")
    void save_throwsOnDuplicateEmail() {
        userDao.save(new User("Alex", "dup@mail.com", 30));

        User duplicate = new User("Petr", "dup@mail.com", 25);

        assertThrows(UserDaoException.class, () -> userDao.save(duplicate));
    }

    @Test
    @DisplayName("findById возвращает пользователя, если он существует")
    void findById_returnsUser_whenExists() {
        User saved = userDao.save(new User("Alex", "alex2@mail.com", 30));

        Optional<User> found = userDao.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    @DisplayName("findById возвращает пустой Optional, если пользователь не существует")
    void findById_returnsEmpty_whenNotExists() {
        Optional<User> found = userDao.findById(999L);

        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("findAll возвращает всех сохранённых пользователей")
    void findAll_returnsAllUsers() {
        userDao.save(new User("Alex", "alex3@mail.com", 30));
        userDao.save(new User("Petr", "petr3@mail.com", 25));

        List<User> users = userDao.findAll();

        assertEquals(2, users.size());
    }

    @Test
    @DisplayName("update изменяет данные пользователя")
    void update_changesUserData() {
        User saved = userDao.save(new User("Alex", "alex4@mail.com", 30));

        saved.setName("Alex Updated");
        saved.setAge(35);
        User updated = userDao.update(saved);

        assertEquals("Alex Updated", updated.getName());
        assertEquals(35, updated.getAge());
    }

    @Test
    @DisplayName("delete удаляет пользователя из базы")
    void delete_removesUser() {
        User saved = userDao.save(new User("Alex", "alex5@mail.com", 30));

        userDao.delete(saved.getId());

        assertTrue(userDao.findById(saved.getId()).isEmpty());
    }
}
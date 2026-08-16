package org.example.userservice.dao;

import org.example.userservice.config.HibernateUtil;
import org.example.userservice.entity.User;
import org.example.userservice.exception.UserDaoException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.JDBCConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

    private static final Logger log = LoggerFactory.getLogger(UserDaoImpl.class);

    @Override
    public User save(User user) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.persist(user);

            transaction.commit();

            log.info("Пользователь создан: id={}, email={}", user.getId(), user.getEmail());
            return user;

        } catch (ConstraintViolationException e) {
            rollback(transaction);
            log.warn("Нарушение уникальности при создании пользователя с email={}", user.getEmail());
            throw new UserDaoException(
                    "Пользователь с email '" + user.getEmail() + "' уже существует",
                    e
            );

        } catch (JDBCConnectionException e) {
            rollback(transaction);
            log.error("Не удалось подключиться к базе данных при сохранении пользователя", e);
            throw new UserDaoException("Не удалось подключиться к базе данных", e);

        } catch (HibernateException e) {
            rollback(transaction);
            log.error("Ошибка Hibernate при сохранении пользователя с email={}", user.getEmail(), e);
            throw new UserDaoException("Ошибка при сохранении пользователя", e);

        } catch (Exception e) {
            rollback(transaction);
            log.error("Непредвиденная ошибка при сохранении пользователя с email={}", user.getEmail(), e);
            throw new UserDaoException("Непредвиденная ошибка при сохранении пользователя", e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            User user = session.get(User.class, id);

            if (user != null) {
                log.debug("Пользователь найден: id={}", id);
            } else {
                log.debug("Пользователь с id={} не найден", id);
            }

            return Optional.ofNullable(user);

        } catch (JDBCConnectionException e) {
            log.error("Не удалось подключиться к базе данных при поиске пользователя id={}", id, e);
            throw new UserDaoException("Не удалось подключиться к базе данных", e);

        } catch (HibernateException e) {
            log.error("Ошибка Hibernate при поиске пользователя id={}", id, e);
            throw new UserDaoException("Ошибка при поиске пользователя с ID: " + id, e);

        } catch (Exception e) {
            log.error("Непредвиденная ошибка при поиске пользователя id={}", id, e);
            throw new UserDaoException("Непредвиденная ошибка при поиске пользователя с ID: " + id, e);
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            List<User> users = session
                    .createQuery("FROM User", User.class)
                    .getResultList();

            log.debug("Получено пользователей: {}", users.size());
            return users;

        } catch (JDBCConnectionException e) {
            log.error("Не удалось подключиться к базе данных при получении списка пользователей", e);
            throw new UserDaoException("Не удалось подключиться к базе данных", e);

        } catch (HibernateException e) {
            log.error("Ошибка Hibernate при получении списка пользователей", e);
            throw new UserDaoException("Ошибка при получении списка пользователей", e);

        } catch (Exception e) {
            log.error("Непредвиденная ошибка при получении списка пользователей", e);
            throw new UserDaoException("Непредвиденная ошибка при получении списка пользователей", e);
        }
    }

    @Override
    public User update(User user) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            User updatedUser = session.merge(user);

            transaction.commit();

            log.info("Пользователь обновлён: id={}", user.getId());
            return updatedUser;

        } catch (ConstraintViolationException e) {
            rollback(transaction);
            log.warn("Нарушение уникальности при обновлении пользователя id={}, email={}",
                    user.getId(), user.getEmail());
            throw new UserDaoException(
                    "Пользователь с email '" + user.getEmail() + "' уже существует",
                    e
            );

        } catch (JDBCConnectionException e) {
            rollback(transaction);
            log.error("Не удалось подключиться к базе данных при обновлении пользователя id={}", user.getId(), e);
            throw new UserDaoException("Не удалось подключиться к базе данных", e);

        } catch (HibernateException e) {
            rollback(transaction);
            log.error("Ошибка Hibernate при обновлении пользователя id={}", user.getId(), e);
            throw new UserDaoException("Ошибка при обновлении пользователя", e);

        } catch (Exception e) {
            rollback(transaction);
            log.error("Непредвиденная ошибка при обновлении пользователя id={}", user.getId(), e);
            throw new UserDaoException("Непредвиденная ошибка при обновлении пользователя", e);
        }
    }

    @Override
    public void delete(Long id) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            User user = session.get(User.class, id);

            if (user != null) {
                session.remove(user);
                log.info("Пользователь удалён: id={}", id);
            } else {
                log.warn("Попытка удалить несуществующего пользователя id={}", id);
            }

            transaction.commit();

        } catch (JDBCConnectionException e) {
            rollback(transaction);
            log.error("Не удалось подключиться к базе данных при удалении пользователя id={}", id, e);
            throw new UserDaoException("Не удалось подключиться к базе данных", e);

        } catch (HibernateException e) {
            rollback(transaction);
            log.error("Ошибка Hibernate при удалении пользователя id={}", id, e);
            throw new UserDaoException("Ошибка при удалении пользователя с ID: " + id, e);

        } catch (Exception e) {
            rollback(transaction);
            log.error("Непредвиденная ошибка при удалении пользователя id={}", id, e);
            throw new UserDaoException("Непредвиденная ошибка при удалении пользователя с ID: " + id, e);
        }
    }

    private void rollback(Transaction transaction) {
        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
            log.debug("Транзакция откачена");
        }
    }
}
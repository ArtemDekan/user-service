package org.example.userservice.dao;

import org.example.userservice.config.HibernateUtil;
import org.example.userservice.entity.User;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

    @Override
    public User save(User user) {

        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            transaction = session.beginTransaction();

            session.persist(user);

            transaction.commit();

            return user;

        } catch (Exception e) {

            if (transaction != null) {
                transaction.rollback();
            }

            throw new RuntimeException("Ошибка при сохранении пользователя", e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            User user = session.get(User.class, id);

            return Optional.ofNullable(user);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Ошибка при поиске пользователя с ID: " + id,
                    e
            );
        }
    }

    @Override
    public List<User> findAll() {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            return session
                    .createQuery("FROM User", User.class)
                    .getResultList();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Ошибка при получении списка пользователей",
                    e
            );
        }
    }

    @Override
    public User update(User user) {

        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            transaction = session.beginTransaction();

            User updatedUser = session.merge(user);

            transaction.commit();

            return updatedUser;

        } catch (Exception e) {

            if (transaction != null) {
                transaction.rollback();
            }

            throw new RuntimeException(
                    "Ошибка при обновлении пользователя",
                    e
            );
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
            }

            transaction.commit();

        } catch (Exception e) {

            if (transaction != null) {
                transaction.rollback();
            }

            throw new RuntimeException(
                    "Ошибка при удалении пользователя с ID: " + id,
                    e
            );
        }
    }
}
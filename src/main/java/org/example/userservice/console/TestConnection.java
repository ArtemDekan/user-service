package org.example.userservice.console;

import org.example.userservice.config.HibernateUtil;

public class TestConnection {

    public static void main(String[] args) {

        try {
            HibernateUtil.getSessionFactory();

            System.out.println("Hibernate успешно подключился к PostgreSQL!");

        } catch (Exception e) {
            System.err.println("Ошибка подключения:");
            e.printStackTrace();

        } finally {
            HibernateUtil.shutdown();
        }
    }
}
package org.example.userservice.exception;

public class UserDaoException extends RuntimeException {
    public UserDaoException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserDaoException(String message) {
        super(message);
    }
}
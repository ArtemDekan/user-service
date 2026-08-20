package org.example.userservice.service;

import org.example.userservice.dao.UserDao;
import org.example.userservice.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;

    private UserService userService;

    @BeforeEach
    void setUp() {
        // новый UserService на каждый тест — изоляция от состояния предыдущих тестов
        userService = new UserService(userDao);
    }

    @Test
    @DisplayName("createUser сохраняет корректного пользователя")
    void createUser_savesValidUser() {
        User expected = new User("Alex", "alex@mail.com", 30);
        when(userDao.save(any(User.class))).thenReturn(expected);

        User result = userService.createUser("Alex", "alex@mail.com", 30);

        assertEquals("Alex", result.getName());
        verify(userDao, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("createUser выбрасывает исключение при пустом имени")
    void createUser_throwsOnBlankName() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.createUser("", "alex@mail.com", 30));

        verifyNoInteractions(userDao);
    }

    @Test
    @DisplayName("createUser выбрасывает исключение при некорректном email")
    void createUser_throwsOnInvalidEmail() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.createUser("Alex", "not-an-email", 30));

        verifyNoInteractions(userDao);
    }

    @Test
    @DisplayName("createUser выбрасывает исключение при некорректном возрасте")
    void createUser_throwsOnInvalidAge() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.createUser("Alex", "alex@mail.com", 0));

        verifyNoInteractions(userDao);
    }

    @Test
    @DisplayName("getUserById возвращает пользователя, если он найден")
    void getUserById_returnsUser_whenFound() {
        User user = new User("Alex", "alex@mail.com", 30);
        when(userDao.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals("Alex", result.get().getName());
    }

    @Test
    @DisplayName("getUserById выбрасывает исключение при некорректном ID")
    void getUserById_throwsOnInvalidId() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.getUserById(-1L));

        verifyNoInteractions(userDao);
    }

    @Test
    @DisplayName("getAllUsers возвращает список из DAO")
    void getAllUsers_returnsListFromDao() {
        List<User> users = List.of(
                new User("Alex", "alex@mail.com", 30),
                new User("Petr", "petr@mail.com", 25)
        );
        when(userDao.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userDao).findAll();
    }

    @Test
    @DisplayName("updateUser обновляет существующего пользователя")
    void updateUser_updatesExistingUser() {
        User existing = new User("Alex", "alex@mail.com", 30);
        when(userDao.findById(1L)).thenReturn(Optional.of(existing));
        when(userDao.update(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUser(1L, "Alex Updated", "alex@mail.com", 35);

        assertEquals("Alex Updated", result.getName());
        assertEquals(35, result.getAge());
        verify(userDao).update(existing);
    }

    @Test
    @DisplayName("updateUser выбрасывает исключение, если пользователь не найден")
    void updateUser_throwsWhenUserNotFound() {
        when(userDao.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(1L, "Alex", "alex@mail.com", 30));

        verify(userDao, never()).update(any());
    }

    @Test
    @DisplayName("deleteUser удаляет существующего пользователя")
    void deleteUser_deletesExistingUser() {
        User existing = new User("Alex", "alex@mail.com", 30);
        when(userDao.findById(1L)).thenReturn(Optional.of(existing));

        userService.deleteUser(1L);

        verify(userDao).delete(1L);
    }

    @Test
    @DisplayName("deleteUser выбрасывает исключение, если пользователь не найден")
    void deleteUser_throwsWhenUserNotFound() {
        when(userDao.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> userService.deleteUser(1L));

        verify(userDao, never()).delete(anyLong());
    }
}
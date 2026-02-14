package com.openclassrooms.etudiant.service;

import com.openclassrooms.etudiant.configuration.security.JwtUtils;
import com.openclassrooms.etudiant.entities.User;
import com.openclassrooms.etudiant.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String LOGIN = "LOGIN";
    private static final String PASSWORD = "PASSWORD";
    private static final String JWT = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJUZXN0VXNlciIsImlhdCI6MTc3MDkxMjQ0OCwiZXhwIjoxNzcwOTE2MDQ4fQ.y9y3IlLH5exRutwafO1tL33mEyFYcFdx0NQotj06y1I";

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService service;

    @Nested
    @Tag("RegisterTests")
    @DisplayName("Créer un utilisateur")
    class RegisterTests {
        @Test
        @DisplayName("Sans paramètres renvoie une erreur")
        public void test_create_null_user_throws_IllegalArgumentException() {
            // GIVEN

            // THEN
            Assertions.assertThrows(IllegalArgumentException.class,
                    () -> service.register(null));
        }

        @Test
        @DisplayName("Avec un login existant renvoie une erreur")
        public void test_create_already_exist_user_throws_IllegalArgumentException() {
            // GIVEN
            User user = new User();
            user.setFirstName(FIRST_NAME);
            user.setLastName(LAST_NAME);
            user.setLogin(LOGIN);
            user.setPassword(PASSWORD);
            when(passwordEncoder.encode(PASSWORD)).thenReturn(PASSWORD);
            when(userRepository.findByLogin(any())).thenReturn(Optional.of(user));

            // THEN
            Assertions.assertThrows(IllegalArgumentException.class,
                    () -> service.register(user));
        }

        @Test
        @DisplayName("Avec un nouveau login fonctionne")
        public void test_create_user() {
            // GIVEN
            User user = new User();
            user.setFirstName(FIRST_NAME);
            user.setLastName(LAST_NAME);
            user.setLogin(LOGIN);
            user.setPassword(PASSWORD);
            when(passwordEncoder.encode(PASSWORD)).thenReturn(PASSWORD);
            when(userRepository.findByLogin(any())).thenReturn(Optional.empty());

            // WHEN
            service.register(user);

            // THEN
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            assertThat(userCaptor.getValue()).isEqualTo(user);
        }
    }

    @Nested
    @Tag("LoginTests")
    @DisplayName("Se connecter")
    class LoginTests {

        @Test
        @DisplayName("Avec un login qui n'existe pas renvoie une erreur")
        public void test_connect_user_with_unknow_login_throws_IllegalArgumentException(){
            // GIVEN
            when(userRepository.findByLogin(any())).thenReturn(Optional.empty());

            //THEN
            verify(passwordEncoder, times(0)).matches(any(String.class), any(String.class));
            Assertions.assertThrows(IllegalArgumentException.class,
                    () -> service.login(LOGIN, PASSWORD));
        }

        @Test
        @DisplayName("Avec un mot de passe incorrect renvoie une erreur")
        public void test_connect_user_with_bad_password_throws_IllegalArgumentException(){
            // GIVEN
            User user = new User();
            user.setFirstName(FIRST_NAME);
            user.setLastName(LAST_NAME);
            user.setLogin(LOGIN);
            user.setPassword(PASSWORD);
            when(userRepository.findByLogin(any())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(false);

            //WHEN
            Assertions.assertThrows(IllegalArgumentException.class, () -> service.login(LOGIN, PASSWORD));

            //THEN
            verify(passwordEncoder, times(1)).matches(any(String.class), any(String.class));

        }

        @Test
        @DisplayName("Avec un login et un mot de passe existant fonctionne")
        public void test_connect_user(){
            // GIVEN
            User user = new User();
            user.setFirstName(FIRST_NAME);
            user.setLastName(LAST_NAME);
            user.setLogin(LOGIN);
            user.setPassword(PASSWORD);
            when(userRepository.findByLogin(any())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(true);
            when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(null);
            when(jwtUtils.generateToken(any(String.class))).thenReturn(JWT);

            //WHEN
            final String result = service.login(LOGIN, PASSWORD);

            //THEN
            verify(authenticationManager, times(1)).authenticate(any(Authentication.class));
            verify(jwtUtils, times(1)).generateToken(any(String.class));
            assertThat(result).isEqualTo(JWT);
        }
    }
}

package com.openclassrooms.etudiant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.etudiant.dto.LoginRequestDTO;
import com.openclassrooms.etudiant.dto.RegisterDTO;
import com.openclassrooms.etudiant.entities.User;
import com.openclassrooms.etudiant.repository.UserRepository;
import com.openclassrooms.etudiant.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class UserControllerTest {

    @Container
    static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.0.44");

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;


    private static final String URL_REGISTER = "/api/register";
    private static final String URL_LOGIN = "/api/login";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";

    @DynamicPropertySource
    static void configureTestProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> mySQLContainer.getJdbcUrl());
        registry.add("spring.datasource.username", () -> mySQLContainer.getUsername());
        registry.add("spring.datasource.password", () -> mySQLContainer.getPassword());
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("Créer un utilisateur")
    class RegisterTest {
        @DisplayName("Avec des données manquantes renvoie une erreur")
        @Test
        public void registerUserWithoutRequiredData() throws Exception {
            // GIVEN
            RegisterDTO registerDTO = new RegisterDTO();

            // WHEN
            mockMvc.perform(MockMvcRequestBuilders.post(URL_REGISTER)
                            .content(objectMapper.writeValueAsString(registerDTO))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        @DisplayName("Existant déjà renvoie une erreur")
        @Test
        public void registerAlreadyExistUser() throws Exception {
            // GIVEN
            User user = new User();
            user.setFirstName(FIRST_NAME);
            user.setLastName(LAST_NAME);
            user.setLogin(LOGIN);
            user.setPassword(PASSWORD);
            userService.register(user);

            RegisterDTO registerDTO = new RegisterDTO();
            registerDTO.setFirstName(FIRST_NAME);
            registerDTO.setLastName(LAST_NAME);
            registerDTO.setLogin(LOGIN);
            registerDTO.setPassword(PASSWORD);

            // WHEN
            mockMvc.perform(MockMvcRequestBuilders.post(URL_REGISTER)
                            .content(objectMapper.writeValueAsString(registerDTO))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        @DisplayName("Avec des données correctes fonctionne")
        @Test
        public void registerUserSuccessful() throws Exception {
            // GIVEN
            RegisterDTO registerDTO = new RegisterDTO();
            registerDTO.setFirstName(FIRST_NAME);
            registerDTO.setLastName(LAST_NAME);
            registerDTO.setLogin(LOGIN);
            registerDTO.setPassword(PASSWORD);

            // WHEN
            mockMvc.perform(MockMvcRequestBuilders.post(URL_REGISTER)
                            .content(objectMapper.writeValueAsString(registerDTO))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.status().isCreated());
        }
    }

    @Nested
    @DisplayName("Se connecter")
    class LoginTest{
        @DisplayName("Sans mettre de login renvoie une erreur")
        @Test
        public void loginUserNoLogin() throws Exception {
            // GIVEN
            LoginRequestDTO loginDto = new LoginRequestDTO();
            loginDto.setPassword(PASSWORD);

            // WHEN
            mockMvc.perform(MockMvcRequestBuilders.post(URL_LOGIN)
                            .param("password", loginDto.getPassword())
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        @DisplayName("Sans mettre de mot de passe renvoie une erreur")
        @Test
        public void loginUserNoPassword() throws Exception {
            // GIVEN
            LoginRequestDTO loginDto = new LoginRequestDTO();
            loginDto.setLogin(LOGIN);

            // WHEN
            mockMvc.perform(MockMvcRequestBuilders.post(URL_LOGIN)
                            .param("login", loginDto.getLogin())
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        @DisplayName("À un utilisateur qui n'existe pas renvoie une erreur")
        @Test
        public void loginUserUnknownUser() throws Exception {
            // GIVEN
            LoginRequestDTO loginDto = new LoginRequestDTO();
            loginDto.setLogin(LOGIN);
            loginDto.setPassword(PASSWORD);

            // WHEN
            mockMvc.perform(MockMvcRequestBuilders.post(URL_LOGIN)
                            .param("login", loginDto.getLogin())
                            .param("password", loginDto.getPassword())
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        @DisplayName("Avec un mauvais mot de passe renvoie une erreur")
        @Test
        public void loginUserBadPassword() throws Exception {
            // GIVEN
            final String BAD_PASSWORD = "Badpassword";
            RegisterDTO registerDTO = new RegisterDTO();
            registerDTO.setFirstName(FIRST_NAME);
            registerDTO.setLastName(LAST_NAME);
            registerDTO.setLogin(LOGIN);
            registerDTO.setPassword(PASSWORD);
            mockMvc.perform(MockMvcRequestBuilders.post(URL_REGISTER)
                    .content(objectMapper.writeValueAsString(registerDTO))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));
            LoginRequestDTO loginDto = new LoginRequestDTO();
            loginDto.setLogin(LOGIN);
            loginDto.setPassword(BAD_PASSWORD);

            // WHEN
            mockMvc.perform(MockMvcRequestBuilders.post(URL_LOGIN)
                            .param("login", loginDto.getLogin())
                            .param("password", loginDto.getPassword())
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        @DisplayName("Avec des informations correctes fonctionne")
        @Test
        public void loginUserSuccessful() throws Exception {
            // GIVEN
            RegisterDTO registerDTO = new RegisterDTO();
            registerDTO.setFirstName(FIRST_NAME);
            registerDTO.setLastName(LAST_NAME);
            registerDTO.setLogin(LOGIN);
            registerDTO.setPassword(PASSWORD);
            mockMvc.perform(MockMvcRequestBuilders.post(URL_REGISTER)
                    .content(objectMapper.writeValueAsString(registerDTO))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));
            LoginRequestDTO loginDto = new LoginRequestDTO();
            loginDto.setLogin(LOGIN);
            loginDto.setPassword(PASSWORD);

            // WHEN
            mockMvc.perform(MockMvcRequestBuilders.post(URL_LOGIN)
                            .param("login", loginDto.getLogin())
                            .param("password", loginDto.getPassword())
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
        }

    }


}

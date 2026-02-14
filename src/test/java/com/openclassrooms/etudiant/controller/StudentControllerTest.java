package com.openclassrooms.etudiant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.etudiant.dto.StudentDTO;
import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.entities.User;
import com.openclassrooms.etudiant.repository.StudentRepository;
import com.openclassrooms.etudiant.repository.UserRepository;
import com.openclassrooms.etudiant.service.StudentService;
import com.openclassrooms.etudiant.service.UserService;
import org.junit.jupiter.api.*;
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

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class StudentControllerTest {

    @Container
    static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.0.44");

    @Autowired
    private StudentService service;
    @Autowired
    private StudentRepository repository;
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
    private static final String URL_STUDENTS = "/api/student";
    private static final String URL_STUDENT = "/api/student/";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";

    private static final String STUDENT_NUM = "000001";
    private static final String STUDENT_FIRST_NAME = "Jean";
    private static final String STUDENT_LAST_NAME = "Smith";
    private static final LocalDate BIRTHDATE = LocalDate.of(2000, 2, 1);
    private static final String BIRTHDATE_STR = "01/02/2000";
    private static final String PHONE_NUMBER = "0606060606";
    private static final String EMAIL = "testemail@hotmail.com";
    private static final LocalDate SUBSCRIBE_START = LocalDate.of(2026, 1, 1);
    private static final String SUBSCRIBE_START_STR = "01/01/2026";
    private static final LocalDate SUBSCRIBE_END = LocalDate.of(2030, 1, 1);
    private static final String SUBSCRIBE_END_STR = "01/01/2030";

    private String token;
    @Autowired
    private StudentService studentService;

    @DynamicPropertySource
    static void configureTestProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> mySQLContainer.getJdbcUrl());
        registry.add("spring.datasource.username", () -> mySQLContainer.getUsername());
        registry.add("spring.datasource.password", () -> mySQLContainer.getPassword());
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
    }

    @BeforeEach
    public void beforeEach() throws Exception{
        User user = new User();
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setLogin(LOGIN);
        user.setPassword(PASSWORD);
        userService.register(user);
        this.token = userService.login(LOGIN, PASSWORD);
    }

    @AfterEach
    public void afterEach() {
        repository.deleteAll();
        userRepository.deleteAll();
    }

    @DisplayName("Récupérer tous les étudiants")
    @Test
    public void getAllStudents() throws Exception{
        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.get(URL_STUDENTS)
                .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Nested
    @DisplayName("Récupérer un étudiant")
    class GetStudentTest {
        @DisplayName("À partir d'un numéro étudiant inexistant renvoie une erreur")
        @Test
        public void getStudentWithUnknownStudentNumber() throws Exception{
            // WHEN
            mockMvc.perform(MockMvcRequestBuilders.get(URL_STUDENT+STUDENT_NUM)
                            .header("Authorization", "Bearer " + token)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        @DisplayName("À partir d'un numéro étudiant existant fonctionne")
        @Test
        public void getStudent() throws Exception{
            //GIVEN
            Student student = new Student();
            student.setStudentNumber(STUDENT_NUM);
            student.setFirstName(STUDENT_FIRST_NAME);
            student.setLastName(STUDENT_LAST_NAME);
            student.setBirthDate(BIRTHDATE);
            studentService.createStudent(student);

            // WHEN
            mockMvc.perform(MockMvcRequestBuilders.get(URL_STUDENT+STUDENT_NUM)
                            .header("Authorization", "Bearer " + token)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
        }
    }

    @Nested
    @DisplayName("Créer un étudiant")
    class PostStudentTest {
        @DisplayName("Sans donner de valeur renvoie une erreur")
        @Test
        public void postStudentWithNullParams() throws Exception {
            // WHEN
            mockMvc.perform(MockMvcRequestBuilders.post(URL_STUDENTS)
                            .header("Authorization", "Bearer " + token)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        @DisplayName("Avec un numéro étudiant existant renvoie une erreur")
        @Test
        public void postStudentWithKnownStudentNum() throws Exception {
            // GIVEN
            Student student = new Student();
            student.setStudentNumber(STUDENT_NUM);
            student.setFirstName(STUDENT_FIRST_NAME);
            student.setLastName(STUDENT_LAST_NAME);
            student.setBirthDate(BIRTHDATE);
            service.createStudent(student);
            StudentDTO studentDTO = new StudentDTO();
            studentDTO.setStudentNumber(STUDENT_NUM);
            studentDTO.setFirstName(STUDENT_FIRST_NAME);
            studentDTO.setLastName(STUDENT_LAST_NAME);
            studentDTO.setBirthDate(BIRTHDATE);

            // WHEN
            mockMvc.perform(MockMvcRequestBuilders.post(URL_STUDENTS)
                            .content(objectMapper.writeValueAsString(studentDTO))
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        @DisplayName("Avec un numéro étudiant inexistant fonctionne")
        @Test
        public void postStudent() throws Exception {
            // GIVEN
            StudentDTO studentDTO = new StudentDTO();
            studentDTO.setStudentNumber(STUDENT_NUM);
            studentDTO.setFirstName(STUDENT_FIRST_NAME);
            studentDTO.setLastName(STUDENT_LAST_NAME);
            studentDTO.setBirthDate(BIRTHDATE);

            // WHEN
            mockMvc.perform(MockMvcRequestBuilders.post(URL_STUDENTS)
                            .content(objectMapper.writeValueAsString(studentDTO))
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isCreated());
        }
    }

    @Nested
    @DisplayName("Mettre à jour un étudiant")
    class PutStudentTest {
        @DisplayName("Qui n'existe pas le crée")
        @Test
        public void updateNewStudent() throws Exception {
            // GIVEN
            StudentDTO studentDTO = new StudentDTO();
            studentDTO.setStudentNumber(STUDENT_NUM);
            studentDTO.setFirstName(STUDENT_FIRST_NAME);
            studentDTO.setLastName(STUDENT_LAST_NAME);
            studentDTO.setBirthDate(BIRTHDATE);

            // WHEN
            mockMvc.perform(MockMvcRequestBuilders.put(URL_STUDENT+STUDENT_NUM)
                            .content(objectMapper.writeValueAsString(studentDTO))
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isCreated());
        }

        @DisplayName("Avec des information vides fonctionne")
        @Test
        public void updateStudentWithoutNewValues() throws Exception {
            // GIVEN
            Student student = new Student();
            student.setStudentNumber(STUDENT_NUM);
            student.setFirstName(STUDENT_FIRST_NAME);
            student.setLastName(STUDENT_LAST_NAME);
            student.setBirthDate(BIRTHDATE);
            service.createStudent(student);
            StudentDTO studentDTO = new StudentDTO();

            // WHEN
            mockMvc.perform(MockMvcRequestBuilders.put(URL_STUDENT+STUDENT_NUM)
                            .content(objectMapper.writeValueAsString(studentDTO))
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
        }

        @DisplayName("Avec des information corrects fonctionne")
        @Test
        public void updateStudent() throws Exception {
            // GIVEN
            Student student = new Student();
            student.setStudentNumber(STUDENT_NUM);
            student.setFirstName(STUDENT_FIRST_NAME);
            student.setLastName(STUDENT_LAST_NAME);
            student.setBirthDate(BIRTHDATE);
            service.createStudent(student);

            StudentDTO studentDTO = new StudentDTO();
            studentDTO.setEmail(EMAIL);
            studentDTO.setPhoneNumber(PHONE_NUMBER);
            studentDTO.setSubscribeStart(SUBSCRIBE_START);
            studentDTO.setSubscribeEnd(SUBSCRIBE_END);

            // WHEN
            mockMvc.perform(MockMvcRequestBuilders.put(URL_STUDENT+STUDENT_NUM)
                            .content(objectMapper.writeValueAsString(studentDTO))
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
        }
    }

    @Nested
    @DisplayName("Supprimer un étudiant")
    class DeleteStudentTest {
        @DisplayName("Dont le numéro étudiant n'existe pas renvoie une erreur")
        @Test
        public void deleteStudentWithUnknownStudentNum() throws Exception {
            // WHEN
            mockMvc.perform(MockMvcRequestBuilders.delete(URL_STUDENT+STUDENT_NUM)
                            .header("Authorization", "Bearer " + token)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        @DisplayName("Dont le numéro étudiant existe fonctionne")
        @Test
        public void deleteStudent() throws Exception {
            // GIVEN
            Student student = new Student();
            student.setStudentNumber(STUDENT_NUM);
            student.setFirstName(STUDENT_FIRST_NAME);
            student.setLastName(STUDENT_LAST_NAME);
            student.setBirthDate(BIRTHDATE);
            service.createStudent(student);

            // WHEN
            mockMvc.perform(MockMvcRequestBuilders.delete(URL_STUDENT+STUDENT_NUM)
                            .header("Authorization", "Bearer " + token)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        }
    }


}

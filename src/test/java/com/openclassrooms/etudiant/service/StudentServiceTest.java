package com.openclassrooms.etudiant.service;

import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.repository.StudentRepository;
import com.openclassrooms.etudiant.validator.StudentValidator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class StudentServiceTest {
    final String STUDENT_NUM = "000001";
    final String FIRST_NAME = "Jean";
    final String LAST_NAME = "Smith";
    final LocalDate BIRTH_DATE = LocalDate.of(2000, 2, 1);
    final String PHONE_NUMBER = "0606060606";
    final String EMAIL = "testemail@hotmail.com";
    final LocalDate SUBSCRIBE_START = LocalDate.of(2026, 1, 1);
    final LocalDate SUBSCRIBE_END = LocalDate.of(2030, 1, 1);

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private StudentValidator studentValidator;
    @InjectMocks
    private StudentService service;

    @DisplayName("Récupérer tous les étudiants")
    @Test
    public void test_get_all_students() {
        //WHEN
        service.getStudentsList();

        //THEN
        verify(studentRepository, times(1)).findAll();
    }

    @Nested
    @DisplayName("Récupérer un étudiant")
    class GetStudent {
        @DisplayName("À partir d'un numéro étudiant inexistant renvoie une erreur")
        @Test
        public void test_get_student_with_invalid_studentnum_throws_IllegalArgumentException() {
            //GIVEN
            when(studentRepository.findByStudentNumber(any(String.class))).thenReturn(Optional.empty());

            Assertions.assertThrows(IllegalArgumentException.class, () -> service.getStudentByStudentNum(any(String.class)));
        }

        @DisplayName("À partir d'un numéro étudiant existant fonctionne")
        @Test
        public void test_get_student() {
            //GIVEN
            Student student = new Student();
            student.setStudentNumber(STUDENT_NUM);
            when(studentRepository.findByStudentNumber(any(String.class))).thenReturn(Optional.of(student));

            //WHEN
            Student result = service.getStudentByStudentNum(STUDENT_NUM);

            //THEN
            assertThat(result.getStudentNumber()).isEqualTo(student.getStudentNumber());
        }
    }

    @Nested
    @DisplayName("Créer un étudiant")
    class CreateStudent {
        @DisplayName("Sans donner de valeur renvoie une erreur")
        @Test
        public void test_create_student_with_null_param_throws_IllegalArgumentException() {
            Assertions.assertThrows(IllegalArgumentException.class, () -> service.createStudent(null));
        }

        @DisplayName("Avec un numéro étudiant existant renvoie une erreur")
        @Test
        public void test_create_student_with_existing_studentnum_throws_IllegalArgumentException() {
            //GIVEN
            Student student = new Student();
            student.setStudentNumber(STUDENT_NUM);
            when(studentRepository.findByStudentNumber(any(String.class))).thenReturn(Optional.of(student));

            //WHEN
            Assertions.assertThrows(IllegalArgumentException.class, () -> service.createStudent(student));

            //THEN
            verify(studentRepository, times(1)).findByStudentNumber(any(String.class));
        }

        @DisplayName("Avec un numéro étudiant inexistant fonctionne")
        @Test
        public void test_create_student() {
            //GIVEN
            Student student = new Student();
            student.setStudentNumber(STUDENT_NUM);
            when(studentRepository.findByStudentNumber(any(String.class))).thenReturn(Optional.empty());
            doNothing().when(studentValidator).validate(any(Student.class));

            //WHEN
            service.createStudent(student);

            //THEN
            verify(studentRepository, times(1)).save(any(Student.class));
        }
    }

    @Nested
    @DisplayName("Mettre à jour un étudiant")
    class UpdateStudent {
        @DisplayName("Qui n'existe pas renvoie une erreur")
        @Test
        public void test_update_student_with_null_studentdb_throws_IllegalArgumentException() {
            //GIVEN
            Student student = new Student();

            //THEN
            Assertions.assertThrows(IllegalArgumentException.class, () -> service.updateStudent(null, student));
        }

        @DisplayName("Avec des informations incorrects renvoie une erreur")
        @Test
        public void test_update_student_with_null_student_throws_IllegalArgumentException() {
            //GIVEN
            Student studentDb = new Student();
            studentDb.setStudentNumber(STUDENT_NUM);

            //THEN
            Assertions.assertThrows(IllegalArgumentException.class, () -> service.updateStudent(studentDb, null));
        }

        @DisplayName("Avec des information vides fonctionne")
        @Test
        public void test_update_student_without_new_values() {
            //GIVEN
            Student studentDb = new Student();
            studentDb.setStudentNumber(STUDENT_NUM);
            Student student = new Student();
            doNothing().when(studentValidator).validate(any(Student.class));

            //WHEN
            service.updateStudent(studentDb, student);

            //THEN
            verify(studentRepository, times(1)).save(any(Student.class));
        }

        @DisplayName("Avec des information corrects fonctionne")
        @Test
        public void test_update_student() {
            //GIVEN
            Student studentDb = new Student();
            studentDb.setStudentNumber(STUDENT_NUM);
            Student student = new Student();
            student.setFirstName(FIRST_NAME);
            student.setLastName(LAST_NAME);
            student.setBirthDate(BIRTH_DATE);
            student.setEmail(EMAIL);
            student.setPhoneNumber(PHONE_NUMBER);
            student.setSubscribeStart(SUBSCRIBE_START);
            student.setSubscribeEnd(SUBSCRIBE_END);
            doNothing().when(studentValidator).validate(any(Student.class));

            //WHEN
            service.updateStudent(studentDb, student);

            //THEN
            verify(studentRepository, times(1)).save(any(Student.class));
        }
    }

    @Nested
    @DisplayName("Supprimer un étudiant")
    class DeleteStudent {
        @DisplayName("Dont le numéro étudiant n'existe pas renvoie une erreur")
        @Test
        public void test_delete_student_with_invalid_studentnum_throws_IllegalArgumentException() {
            //GIVEN
            when(studentRepository.findByStudentNumber(any(String.class))).thenReturn(Optional.empty());

            //THEN
            Assertions.assertThrows(IllegalArgumentException.class, () -> service.deleteStudent(any(String.class)));
        }

        @DisplayName("Dont le numéro étudiant existe fonctionne")
        @Test
        public void test_delete_student() {
            //GIVEN
            Student student = new Student();
            student.setStudentNumber(STUDENT_NUM);
            when(studentRepository.findByStudentNumber(any(String.class))).thenReturn(Optional.of(student));
            doNothing().when(studentValidator).validate(any(Student.class));

            //WHEN
            service.deleteStudent(STUDENT_NUM);

            //THEN
            verify(studentRepository, times(1)).delete(any(Student.class));
        }
    }

    @DisplayName("Sauvegarder un étudiant")
    @Test
    public void test_save_student() {
        //GIVEN
        Student student = new Student();
        student.setStudentNumber(STUDENT_NUM);
        doNothing().when(studentValidator).validate(any(Student.class));

        //WHEN
        service.saveStudent(student);

        //THEN
        verify(studentRepository, times(1)).save(any(Student.class));
    }
}

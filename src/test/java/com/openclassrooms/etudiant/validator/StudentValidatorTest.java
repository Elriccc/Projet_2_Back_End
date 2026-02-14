package com.openclassrooms.etudiant.validator;

import com.openclassrooms.etudiant.entities.Student;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.Errors;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class StudentValidatorTest {
    final String STUDENT_NUM = "000001";
    final String FIRST_NAME = "Testname";
    final String LAST_NAME = "Testlastname";
    final LocalDate BIRTH_DATE = LocalDate.of(2000, 1, 1);
    final String PHONE_NUMBER = "0606060606";
    final String EMAIL = "testemail@hotmail.com";
    final LocalDate SUBSCRIBE_START = LocalDate.of(2026, 1, 1);
    final LocalDate SUBSCRIBE_END = LocalDate.of(2030, 1, 1);
    private Student student;

    @InjectMocks
    private StudentValidator validator;

    @BeforeEach
    public void init(){
        student = new Student();
        student.setStudentNumber(STUDENT_NUM);
        student.setFirstName(FIRST_NAME);
        student.setLastName(LAST_NAME);
        student.setBirthDate(BIRTH_DATE);
        student.setEmail(EMAIL);
        student.setPhoneNumber(PHONE_NUMBER);
        student.setSubscribeStart(SUBSCRIBE_START);
        student.setSubscribeEnd(SUBSCRIBE_END);
    }

    @DisplayName("Un étudiant avec un prénom trop long renvoie une erreur")
    @Test
    public void test_student_validator_with_first_name_too_long_throws_IllegalArgumentException(){
        //GIVEN
        Errors errors = Mockito.mock(Errors.class, Answers.CALLS_REAL_METHODS);
        doThrow(new IllegalArgumentException()).when(errors).rejectValue(any(String.class), any(String.class), any(String.class));
        student.setFirstName("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

        //THEN
        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validate(student, errors));
        verify(errors, times(1)).rejectValue(any(String.class), any(String.class), any(String.class));
    }

    @DisplayName("Un étudiant avec un nom de famille trop long renvoie une erreur")
    @Test
    public void test_student_validator_with_last_name_too_long_throws_IllegalArgumentException(){
        //GIVEN
        Errors errors = Mockito.mock(Errors.class, Answers.CALLS_REAL_METHODS);
        doThrow(new IllegalArgumentException()).when(errors).rejectValue(any(String.class), any(String.class), any(String.class));
        student.setLastName("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

        //THEN
        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validate(student, errors));
        verify(errors, times(1)).rejectValue(any(String.class), any(String.class), any(String.class));
    }

    @DisplayName("Un étudiant avec une date de naissance supérieur à aujourd'hui renvoie une erreur")
    @Test
    public void test_student_validator_with_birth_date_too_recentthrows_IllegalArgumentException(){
        //GIVEN
        Errors errors = Mockito.mock(Errors.class, Answers.CALLS_REAL_METHODS);
        doThrow(new IllegalArgumentException()).when(errors).rejectValue(any(String.class), any(String.class), any(String.class));
        student.setBirthDate(LocalDate.of(4000, 1, 1));

        //THEN
        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validate(student, errors));
        verify(errors, times(1)).rejectValue(any(String.class), any(String.class), any(String.class));
    }

    @DisplayName("Un étudiant avec un numéro de téléphone incorrect renvoie une erreur")
    @Test
    public void test_student_validator_with_invalid_phone_number_throws_IllegalArgumentException(){
        //GIVEN
        Errors errors = Mockito.mock(Errors.class, Answers.CALLS_REAL_METHODS);
        doThrow(new IllegalArgumentException()).when(errors).rejectValue(any(String.class), any(String.class), any(String.class));
        student.setPhoneNumber("abcd");

        //THEN
        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validate(student, errors));
        verify(errors, times(1)).rejectValue(any(String.class), any(String.class), any(String.class));
    }

    @DisplayName("Un étudiant avec un email incorrect renvoie une erreur")
    @Test
    public void test_student_validator_with_invalid_email_throws_IllegalArgumentException(){
        //GIVEN
        Errors errors = Mockito.mock(Errors.class, Answers.CALLS_REAL_METHODS);
        doThrow(new IllegalArgumentException()).when(errors).rejectValue(any(String.class), any(String.class), any(String.class));
        student.setEmail("not.an.email");

        //THEN
        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validate(student, errors));
        verify(errors, times(1)).rejectValue(any(String.class), any(String.class), any(String.class));
    }

    @DisplayName("Un étudiant avec une date de fin d'abonnement inférieur à une date de début d'abonnement renvoie une erreur")
    @Test
    public void test_student_validator_with_invalid_subscribing_throws_IllegalArgumentException(){
        //GIVEN
        Errors errors = Mockito.mock(Errors.class, Answers.CALLS_REAL_METHODS);
        doThrow(new IllegalArgumentException()).when(errors).rejectValue(any(String.class), any(String.class), any(String.class));
        student.setSubscribeStart(LocalDate.of(4000, 1, 1));

        //THEN
        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validate(student, errors));
        verify(errors, times(1)).rejectValue(any(String.class), any(String.class), any(String.class));
    }

    @DisplayName("Un étudiant avec des informations correctes passe la validation")
    @Test
    public void test_student_validator(){
        //GIVEN
        Errors errors = Mockito.mock(Errors.class, Answers.CALLS_REAL_METHODS);
        doThrow(new IllegalArgumentException()).when(errors).rejectValue(any(String.class), any(String.class), any(String.class));

        //WHEN
        validator.validate(student, errors);

        //THEN
        verify(errors, times(0)).rejectValue(any(String.class), any(String.class), any(String.class));
    }
}

package com.openclassrooms.etudiant.validator;

import com.openclassrooms.etudiant.entities.Student;
import org.apache.logging.log4j.util.Strings;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

public class StudentValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Student.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Student student = (Student) target;
        LocalDate today = LocalDate.now();
        if(student.getFirstName().length() > 120){
            errors.rejectValue("firstName", "firstName.toolong", "First name is too long");
        }
        if(student.getLastName().length() > 120){
            errors.rejectValue("lastName", "lastName.toolong", "Last name is too long");
        }
        if(student.getBirthDate().isAfter(today)){
            errors.rejectValue("birthDate", "birthDate.impossible", "Student isn't born yet");
        }
        if(Strings.isNotBlank(student.getPhoneNumber()) && !student.getPhoneNumber().matches("^[+]?[0-9\\s]+$")){
            errors.rejectValue("phoneNumber", "phone.incorrect", "Phone number is incorrect");
        }
        if(!Strings.isEmpty(student.getEmail()) && !student.getEmail().matches("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")){
            errors.rejectValue("email", "email.incorrect", "Email is incorrect");
        }
        if(student.getSubscribeStart() != null && student.getSubscribeEnd() != null && student.getSubscribeStart().isAfter(student.getSubscribeEnd())){
            errors.rejectValue("subscribeEnd", "subscribeEnd.impossible", "Subscribe can't end before it starts");
        }
    }
}

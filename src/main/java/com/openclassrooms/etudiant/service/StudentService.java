package com.openclassrooms.etudiant.service;

import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.repository.StudentRepository;
import com.openclassrooms.etudiant.validator.StudentValidator;
import io.jsonwebtoken.lang.Assert;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StudentService {
    private final StudentValidator studentValidator;
    private final StudentRepository studentRepository;


    public List<Student> getStudentsList(){
        return studentRepository.findAll();
    }

    public Student getStudentByStudentNum(String studentNumber){
        return studentRepository.findByStudentNumber(studentNumber).orElseThrow(() -> new IllegalArgumentException("Student does not exist"));
    }

    public void createStudent(Student student){
        Assert.notNull(student, "Student must not be null");
        log.info("Creating new student");

        Optional<Student> optionalStudent = studentRepository.findByStudentNumber(student.getStudentNumber());
        if(optionalStudent.isPresent()){
            throw new IllegalArgumentException("Student with student number " + student.getStudentNumber() + " already exists");
        }
        this.saveStudent(student);
    }

    public void updateStudent(Student studentDb, Student student){
        Assert.notNull(student, "Student must not be null");
        log.info("Updating student {}", studentDb.getStudentNumber());

        if(student.getFirstName() != null) studentDb.setFirstName(student.getFirstName());
        if(student.getLastName() != null) studentDb.setLastName(student.getLastName());
        if(student.getBirthDate() != null) studentDb.setBirthDate(student.getBirthDate());
        if(student.getPhoneNumber() != null) studentDb.setPhoneNumber(student.getPhoneNumber());
        if(student.getEmail() != null) studentDb.setEmail(student.getEmail());
        if(student.getSubscribeStart() != null) studentDb.setSubscribeStart(student.getSubscribeStart());
        if(student.getSubscribeEnd() != null) studentDb.setSubscribeEnd(student.getSubscribeEnd());
        this.saveStudent(studentDb);
    }

    public void deleteStudent(String studentNum){
        Student student = this.getStudentByStudentNum(studentNum);
        Assert.notNull(student, "Student must not be null");
        log.info("Deleting student {}", student.getStudentNumber());

        studentRepository.delete(student);
    }

    public void saveStudent(Student student){
        Errors errors = new BeanPropertyBindingResult(student, "user");
        studentValidator.validate(student, errors);
        if (errors.hasErrors()) {
            throw new IllegalArgumentException(errors.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(". ")));
        }
        studentRepository.save(student);
    }
}

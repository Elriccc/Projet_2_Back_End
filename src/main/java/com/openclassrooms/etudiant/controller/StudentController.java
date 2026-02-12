package com.openclassrooms.etudiant.controller;

import com.openclassrooms.etudiant.dto.StudentDTO;
import com.openclassrooms.etudiant.dto.StudentUpdateDTO;
import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.mapper.StudentDtoMapper;
import com.openclassrooms.etudiant.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final StudentDtoMapper studentDtoMapper;

    @GetMapping("/api/student")
    public ResponseEntity<?> getAllStudents(){
        List<Student> students = studentService.getStudentsList();
        return ResponseEntity.ok(students.stream()
                .map(studentDtoMapper::toDto)
                .toList());
    }

    @GetMapping("/api/student/{studentNum}")
    public ResponseEntity<?> getStudent(@PathVariable String studentNum){
        Student student = studentService.getStudentByStudentNum(studentNum);
        return ResponseEntity.ok(studentDtoMapper.toDto(student));
    }

    @PostMapping("/api/student")
    public ResponseEntity<?> createStudent(@RequestBody StudentDTO studentDTO){
        studentService.createStudent(studentDtoMapper.toEntity(studentDTO));
        return ResponseEntity.created(
                UriComponentsBuilder.fromPath("/api/student/{studentNum}")
                        .encode()
                        .buildAndExpand(studentDTO.getStudentNumber())
                        .toUri())
                .build();
    }

    @PutMapping("/api/student/{studentNum}")
    public ResponseEntity<?> updateStudent(@PathVariable String studentNum, @RequestBody StudentUpdateDTO studentUpdateDTO){
        Student student = studentService.getStudentByStudentNum(studentNum);
        if(student != null){
            studentService.updateStudent(student, studentDtoMapper.toEntity(studentUpdateDTO));
            return ResponseEntity.ok(studentDtoMapper.toDto(studentService.getStudentByStudentNum(studentNum)));
        } else {
            studentService.createStudent(studentDtoMapper.toEntity(studentUpdateDTO));
            return ResponseEntity.created(
                    UriComponentsBuilder.fromPath("/api/student/{studentNum}")
                            .encode()
                            .buildAndExpand(studentNum)
                            .toUri())
                    .build();
        }
    }

    @DeleteMapping("/api/student/{studentNum}")
    public ResponseEntity<?> deleteStudent(@PathVariable String studentNum){
        studentService.deleteStudent(studentNum);
        return ResponseEntity.ok(studentNum);
    }

}

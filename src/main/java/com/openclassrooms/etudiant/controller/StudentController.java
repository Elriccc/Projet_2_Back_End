package com.openclassrooms.etudiant.controller;

import com.openclassrooms.etudiant.dto.StudentDTO;
import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.mapper.StudentDtoMapper;
import com.openclassrooms.etudiant.repository.StudentRepository;
import com.openclassrooms.etudiant.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final StudentDtoMapper studentDtoMapper;
    private final StudentRepository studentRepository;

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
    public ResponseEntity<?> updateStudent(@PathVariable String studentNum, @RequestBody StudentDTO studentDTO){
        Optional<Student> student = studentRepository.findByStudentNumber(studentNum);
        if(student.isPresent()){
            studentService.updateStudent(student.get(), studentDtoMapper.toEntity(studentDTO));
            return ResponseEntity.ok(studentDtoMapper.toDto(studentService.getStudentByStudentNum(studentNum)));
        } else {
            studentService.createStudent(studentDtoMapper.toEntity(studentDTO));
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

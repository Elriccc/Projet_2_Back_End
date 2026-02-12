package com.openclassrooms.etudiant.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentUpdateDTO {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String email;
    private String phoneNumber;
    private LocalDate subscribeStart;
    private LocalDate subscribeEnd;
}

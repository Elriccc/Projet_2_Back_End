package com.openclassrooms.etudiant.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "student", uniqueConstraints =
        @UniqueConstraint(columnNames = "studentNum"))
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Column(name = "studentNum", nullable = false)
    private String studentNumber;

    @NotBlank
    @Column(name = "firstName", nullable = false)
    private String firstName;

    @NotBlank
    @Column(name = "lastName", nullable = false)
    private String lastName;

    @NotNull
    @Column(name = "birthDate", nullable = false)
    private LocalDate birthDate;

    @Column(name = "email")
    private String email;

    @Column(name = "phoneNum")
    private String phoneNumber;

    @Column(name = "subStart")
    private LocalDate subscribeStart;

    @Column(name = "subEnd")
    private LocalDate subscribeEnd;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime created_at;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updated_at;


}

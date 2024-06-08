package com.example.springbatch.rest;

import com.example.springbatch.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/student")
@AllArgsConstructor
public class StudentResource {
    private final StudentService studentService;
    @PostMapping("/import-data")
    public ResponseEntity<Void> importStudentRecord(){
        studentService.importStudentRecord();
        return ResponseEntity.ok().build();
    }

}

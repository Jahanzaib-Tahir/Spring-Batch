package com.example.springbatch.service;

import com.example.springbatch.domain.Student;

public interface StudentService {
    Student save(Student student);
    void importStudentRecord();
}

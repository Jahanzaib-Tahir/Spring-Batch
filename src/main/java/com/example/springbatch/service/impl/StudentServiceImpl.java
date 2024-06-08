package com.example.springbatch.service.impl;

import com.example.springbatch.domain.Student;
import com.example.springbatch.repo.StudentRepository;
import com.example.springbatch.service.StudentService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final Logger LOGGER = LoggerFactory.getLogger(StudentServiceImpl.class);

    private final StudentRepository studentRepository;
    private final JobLauncher jobLauncher;
    private final Job job;

    @Override
    public Student save(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public void importStudentRecord() {
        JobParameters jobParameters = new JobParametersBuilder()
            .addLong("import student record job start at : ", System.currentTimeMillis()).toJobParameters();
        try {
            LOGGER.info("Started import student record job");
            jobLauncher.run(job, jobParameters);
            LOGGER.info("Finished import student record job");
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            LOGGER.error("failed to import student record", e);
        }
    }
}

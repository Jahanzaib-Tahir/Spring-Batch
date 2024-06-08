package com.example.springbatch.config;

import com.example.springbatch.domain.Student;
import com.example.springbatch.repo.StudentRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@AllArgsConstructor
public class SpringBatchConfig {
    private final StudentRepository studentRepository;
    private final static String FILE_ITEM_READER_NAME = "STUDENT_CSV_READER";
    private final static int FILE_ITEM_READER_LINE_TO_SKIP = 1;
    @Bean
    public FlatFileItemReader<Student> fileItemReader(){
        FlatFileItemReader<Student> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new FileSystemResource("src/main/resources/data/student_record.csv"));
        flatFileItemReader.setLinesToSkip(FILE_ITEM_READER_LINE_TO_SKIP);
        flatFileItemReader.setName(FILE_ITEM_READER_NAME);
        flatFileItemReader.setLineMapper(csvToStudentPojoMapper());
        return flatFileItemReader;
    }
    @Bean
    public LineMapper<Student> csvToStudentPojoMapper(){
        DefaultLineMapper<Student> studentDefaultLineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "cgpa", "dateOfBirth");

        BeanWrapperFieldSetMapper<Student> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Student.class);

        studentDefaultLineMapper.setLineTokenizer(lineTokenizer);
        studentDefaultLineMapper.setFieldSetMapper(fieldSetMapper);
        return studentDefaultLineMapper;
    }
    @Bean
    public RepositoryItemWriter<Student> writer() {
        RepositoryItemWriter<Student> writer = new RepositoryItemWriter<>();
        writer.setRepository(studentRepository);
        writer.setMethodName("save");
        return writer;
    }
    @Bean
    public Step readDataFromCsvAndPersistInDb(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("csv-step", jobRepository).
            <Student, Student>chunk(10, transactionManager)
            .reader(fileItemReader())
            .writer(writer())
            .build();
    }
    @Bean
    public Job transferCsvToDatabase(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new JobBuilder("transferCsvToDatabase", jobRepository)
            .flow(readDataFromCsvAndPersistInDb(jobRepository, platformTransactionManager))
            .end()
            .build();
    }
}

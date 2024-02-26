package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.model.Student;
import com.ufpa.lafocabackend.repository.StudentRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {


    private final StudentRepository studentRepository;
    private final UserPhotoService userPhotoService;

    public StudentService(StudentRepository studentRepository, UserPhotoService userPhotoService) {
        this.studentRepository = studentRepository;
        this.userPhotoService = userPhotoService;
    }

    public Student save (Student student) {

        return studentRepository.save(student);
    }

    public List<Student> list (){

        return studentRepository.findAll();
    }

    public Student read (Long studentId) {
        return getOrFail(studentId);
    }

    public Student update (Student student) {

        return save(student);
    }

    public void delete (Long studentId) {

        try {
            studentRepository.deleteById(studentId);
        } catch (DataIntegrityViolationException e) {
            throw new EntityInUseException(getClass().getSimpleName(), studentId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(getClass().getSimpleName(), studentId);
        }

    }

    private Student getOrFail(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow( () -> new EntityNotFoundException(getClass().getSimpleName(), studentId));
    }

    public void deletePhoto(Long studentId) {

        userPhotoService.

    }
}

package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.model.FunctionStudent;
import com.ufpa.lafocabackend.domain.model.Student;
import com.ufpa.lafocabackend.infrastructure.service.PhotoStorageService;
import com.ufpa.lafocabackend.infrastructure.service.StorageUtils;
import com.ufpa.lafocabackend.repository.StudentRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentService {


    private final StudentRepository studentRepository;
    private final PhotoStorageService photoStorageService;
    private final FunctionStudentService functionStudentService;

    public StudentService(StudentRepository studentRepository, PhotoStorageService photoStorageService, FunctionStudentService functionStudentService) {
        this.studentRepository = studentRepository;
        this.photoStorageService = photoStorageService;
        this.functionStudentService = functionStudentService;
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

        final String photoId = studentRepository.getUserPhotoIdByStudentId(studentId);
        studentRepository.removePhotoReference(studentId);
        final String photoFileName = studentRepository.findFileNameByUserPhotoId(photoId);
        studentRepository.deletePhotoByUserId(photoId);

        StorageUtils storageUtils = StorageUtils.builder().fileName(photoFileName).build();

        photoStorageService.deletar(storageUtils);
    }
    @Transactional
    public void associateFunction(Long functionStudentId, Long studentId) {

        final FunctionStudent functionStudent = functionStudentService.read(functionStudentId);
        final Student student = read(studentId);

        student.setFunctionStudent(functionStudent);
        save(student);
    }
}

package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.model.FunctionStudent;
import com.ufpa.lafocabackend.domain.model.Skill;
import com.ufpa.lafocabackend.domain.model.Student;
import com.ufpa.lafocabackend.domain.model.Tcc;
import com.ufpa.lafocabackend.domain.model.dto.input.StudentInputDto;
import com.ufpa.lafocabackend.domain.model.dto.input.TccDto;
import com.ufpa.lafocabackend.infrastructure.service.PhotoStorageService;
import com.ufpa.lafocabackend.infrastructure.service.StorageUtils;
import com.ufpa.lafocabackend.repository.StudentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentService {


    private final StudentRepository studentRepository;
    private final PhotoStorageService photoStorageService;
    private final FunctionStudentService functionStudentService;
    private final SkillService skillService;
    private final ModelMapper modelMapper;
    private final TccService tccService;

    public StudentService(StudentRepository studentRepository, PhotoStorageService photoStorageService, FunctionStudentService functionStudentService, SkillService skillService, ModelMapper modelMapper, TccService tccService) {
        this.studentRepository = studentRepository;
        this.photoStorageService = photoStorageService;
        this.functionStudentService = functionStudentService;
        this.skillService = skillService;
        this.modelMapper = modelMapper;
        this.tccService = tccService;
    }

    @Transactional
    public Student save (StudentInputDto studentInputDto) {
        Student student = modelMapper.map(studentInputDto, Student.class);

        //is an entity update
        if(studentInputDto.getId() != null){
            student = read(studentInputDto.getId());
            modelMapper.map(studentInputDto, student);
        }

        final FunctionStudent functionStudent = functionStudentService.read(studentInputDto.getFunctionStudentId());
        List<Skill> skills = new ArrayList<>();
        for(Long skillId: studentInputDto.getSkillsId()){
            final Skill skill = skillService.read(skillId);
            skills.add(skill);
        }

        student.setSkills(skills);
        student.setFunctionStudent(functionStudent);

        final Student studentSaved = studentRepository.save(student);

        if(studentInputDto.getTcc() != null) {
            final TccDto tccDto = studentInputDto.getTcc();
            final Tcc tcc = modelMapper.map(tccDto, Tcc.class);
            tcc.setStudent(studentSaved);
            final Tcc tccSaved = tccService.save(tcc);
            studentSaved.setTcc(tccSaved);
        }

        return studentSaved;
    }

    @Transactional
    public Student update (Long studentId, StudentInputDto studentInputDto) {
        final Student student = read(studentId);

        final FunctionStudent functionStudent = functionStudentService.read(studentInputDto.getFunctionStudentId());

        List<Skill> skills = new ArrayList<>();
        for(Long skillId: studentInputDto.getSkillsId()){
            final Skill skill = skillService.read(skillId);
            skills.add(skill);
        }

        modelMapper.map(studentInputDto, student);
        student.setStudentId(studentId);
        student.setFunctionStudent(functionStudent);
        student.setSkills(skills);

        return studentRepository.save(student);
    }

    public List<Student> list (){

        return studentRepository.findAll();
    }

    public Student read (Long studentId) {
        return getOrFail(studentId);
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
        studentRepository.save(student);
    }
    @Transactional
    public void associateSkill (Long studentId, Long skillId) {
        final Student student = read(studentId);
        final Skill skill = skillService.read(skillId);
        student.addSkill(skill);
    }

    @Transactional
    public void disassociateSkill(Long studentId, Long skillId) {
        final Student student = read(studentId);
        final Skill skill = skillService.read(skillId);
        student.removeSkill(skill);
    }

    public Student save(Student student) {
        return studentRepository.save(student);
    }
}

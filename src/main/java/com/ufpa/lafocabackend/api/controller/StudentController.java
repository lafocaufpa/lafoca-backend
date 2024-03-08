package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.security.CheckSecurityPermissionMethods;
import com.ufpa.lafocabackend.domain.model.FunctionStudent;
import com.ufpa.lafocabackend.domain.model.Skills;
import com.ufpa.lafocabackend.domain.model.Student;
import com.ufpa.lafocabackend.domain.model.UserPhoto;
import com.ufpa.lafocabackend.domain.model.dto.PhotoDto;
import com.ufpa.lafocabackend.domain.model.dto.StudentDto;
import com.ufpa.lafocabackend.domain.model.dto.input.StudentInputDto;
import com.ufpa.lafocabackend.domain.service.FunctionStudentService;
import com.ufpa.lafocabackend.domain.service.SkillsService;
import com.ufpa.lafocabackend.domain.service.StudentService;
import com.ufpa.lafocabackend.domain.service.UserPhotoService;
import com.ufpa.lafocabackend.infrastructure.service.PhotoStorageService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/students")
public class StudentController {


    private final StudentService studentService;
    private final ModelMapper modelMapper;
    private final UserPhotoService userPhotoService;
    private final FunctionStudentService functionStudentService;
    private final SkillsService skillsService;

    public StudentController(StudentService studentService, ModelMapper modelMapper, UserPhotoService userPhotoService, FunctionStudentService functionStudentService, SkillsService skillsService) {
        this.studentService = studentService;
        this.modelMapper = modelMapper;
        this.userPhotoService = userPhotoService;
        this.functionStudentService = functionStudentService;
        this.skillsService = skillsService;
    }

    @CheckSecurityPermissionMethods.L1
    @PostMapping
    public ResponseEntity<StudentDto> add (@RequestBody StudentInputDto studentInputDto) {

        final FunctionStudent functionStudent = functionStudentService.read(studentInputDto.getFunctionStudentId());
        final Skills skill = skillsService.read(studentInputDto.getSkillId());

        final Student student = modelMapper.map(studentInputDto, Student.class);

        student.addSkill(skill);
        student.setFunctionStudent(functionStudent);

        final StudentDto studentSaved = modelMapper.map(studentService.save(student), StudentDto.class);

        return ResponseEntity.ok(studentSaved);
    }

    @CheckSecurityPermissionMethods.L1
    @GetMapping("/{studentId}")
    public ResponseEntity<StudentDto> read (@PathVariable Long studentId){

        final Student student = studentService.read(studentId);
        final StudentDto studentDto = modelMapper.map(student, StudentDto.class);
        return ResponseEntity.ok(studentDto);
    }

    @CheckSecurityPermissionMethods.L1
    @GetMapping
    public ResponseEntity<Collection<StudentDto>> list (){

        final List<Student> list = studentService.list();

        Type listType = new TypeToken<List<StudentDto>>() {

        }.getType();

        final List<StudentDto> map = modelMapper.map(list, listType);

        return ResponseEntity.ok(map);
    }

    @CheckSecurityPermissionMethods.L1
    @PutMapping("/{studentId}")
    public ResponseEntity<StudentDto> update (@PathVariable Long studentId, @RequestBody StudentInputDto studentInputDto){

        final Student student = studentService.read(studentId);
        final FunctionStudent functionStudent = functionStudentService.read(studentInputDto.getFunctionStudentId());

        modelMapper.map(studentInputDto, student);
        student.setFunctionStudent(functionStudent);
        final Student studentUpdated = studentService.update(student);
        final StudentDto studentDtoUpdated = modelMapper.map(studentUpdated, StudentDto.class);
        return ResponseEntity.ok(studentDtoUpdated);
    }

    @CheckSecurityPermissionMethods.L1
    @DeleteMapping("/{studentId}")
    public ResponseEntity<Void> delete (@PathVariable Long studentId){

        studentService.delete(studentId);

        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.L1
    @PostMapping(value = "/{studentId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhotoDto> addPhoto(MultipartFile photo, @PathVariable Long studentId) throws IOException {

        final Student student = studentService.read(studentId);

        String originalFilename = student.getStudentId()
                + "_"
                + student.getName()
                + Objects.requireNonNull
                        (photo.getOriginalFilename())
                .substring(photo.getOriginalFilename().lastIndexOf("."));

        final UserPhoto userPhoto = new UserPhoto();
        userPhoto.setUserPhotoId(String.valueOf(student.getStudentId()));
        userPhoto.setFileName(originalFilename);
        userPhoto.setSize(photo.getSize());
        userPhoto.setContentType(photo.getContentType());

        final UserPhoto photoSaved = userPhotoService.save(userPhoto, photo.getInputStream());

        student.setPhoto(photoSaved);
        studentService.save(student);

        final PhotoDto photoDto = modelMapper.map(photoSaved, PhotoDto.class);

        return ResponseEntity.ok(photoDto);
    }

    @CheckSecurityPermissionMethods.L1
    @GetMapping(value = "/{studentId}/photo")
    public ResponseEntity<?> getPhoto(@PathVariable Long studentId) {

        final Student student = studentService.read(studentId);
        final UserPhoto photo = student.getPhoto();

        if (photo == null) {
            return ResponseEntity.notFound().build();
        }

        if (photo.getUrl() != null) {
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, photo.getUrl()).build();
        } else {

            final PhotoStorageService.RecoveredPhoto recoveredPhoto = userPhotoService.get(photo.getFileName());

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, photo.getContentType());

            return ResponseEntity.ok().headers(headers).body(new InputStreamResource(recoveredPhoto.getInputStream()));
        }

    }

    @CheckSecurityPermissionMethods.L1
    @DeleteMapping(value = "/{studentId}/photo")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long studentId) {

        studentService.read(studentId);
        userPhotoService.delete(String.valueOf(studentId));
        studentService.deletePhoto(studentId);

        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.L1
    @PutMapping("/{studentId}/functions-student/{functionStudentId}")
    public ResponseEntity<Void> asociateFunction (@PathVariable Long studentId, @PathVariable Long functionStudentId) {

        studentService.associateFunction(functionStudentId, studentId);

        return ResponseEntity.noContent().build();
    }
}

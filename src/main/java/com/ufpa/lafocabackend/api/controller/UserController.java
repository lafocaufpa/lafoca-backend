package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.domain.model.Photo;
import com.ufpa.lafocabackend.domain.model.User;
import com.ufpa.lafocabackend.domain.model.dto.PhotoDto;
import com.ufpa.lafocabackend.domain.model.dto.UserDto;
import com.ufpa.lafocabackend.domain.model.dto.input.PhotoInputDto;
import com.ufpa.lafocabackend.domain.model.dto.input.UserDtoInput;
import com.ufpa.lafocabackend.domain.service.PhotoService;
import com.ufpa.lafocabackend.domain.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/users")
public class UserController {



    private final UserService userService;
    private final ModelMapper modelMapper;
    private final PhotoService photoService;

    public UserController(UserService userService, ModelMapper modelMapper, PhotoService photoService) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.photoService = photoService;
    }

    @PostMapping
    public ResponseEntity<User> add(@RequestBody UserDtoInput userDtoInput) {

        final User user = modelMapper.map(userDtoInput, User.class);
        final User savedUser = userService.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> list() {

        final List<User> users = userService.list();
        final List<UserDto> dtos = users.stream().map(u -> modelMapper.map(u, UserDto.class)).toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> read(@PathVariable Long userId) {

        final User user = userService.read(userId);



        final UserDto userDto = modelMapper.map(user, UserDto.class);

        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> update (@RequestBody UserDtoInput userDtoInput, @PathVariable Long userId) {

        final User existingUser = userService.read(userId);
        modelMapper.map(userDtoInput, existingUser);
        final UserDto userDto = modelMapper.map(existingUser, UserDto.class);
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "{userId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhotoDto> addPhoto(PhotoInputDto photo, @PathVariable Long userId) throws IOException {

        final User user = userService.read(userId);
        Photo photoUser = new Photo();
        MultipartFile photoFile = photo.getPhoto();

        String originalFilename = user.getUserId()
                + "_"
                + user.getName()
                + Objects.requireNonNull
                    (photo.getPhoto().getOriginalFilename())
                .substring(photo.getPhoto().getOriginalFilename().lastIndexOf("."));

        photoUser.setPhotoId(user.getUserId());
        photoUser.setFileName(originalFilename);
        photoUser.setSize(photoFile.getSize());
        photoUser.setContentType(photoFile.getContentType());

//        final Path path = Path.of("src/main/resources/photos/", String.valueOf(userId));
//        final Path resolve = path.resolve(originalFilename);

//        if(!path.toFile().exists()){
//            path.toFile().mkdirs();
//        }

        final Photo save = photoService.save(photoUser, photoFile.getInputStream());
        final PhotoDto photoDto = modelMapper.map(save, PhotoDto.class);
//        photo.getPhoto().transferTo(resolve);

        return ResponseEntity.ok(photoDto);

    }

}

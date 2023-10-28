package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.domain.model.User;
import com.ufpa.lafocabackend.domain.model.dto.UserDto;
import com.ufpa.lafocabackend.domain.model.dto.input.PhotoInputDto;
import com.ufpa.lafocabackend.domain.model.dto.input.UserDtoInput;
import com.ufpa.lafocabackend.domain.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
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
    public ResponseEntity<UserDto> atualizar(@RequestBody UserDtoInput userDtoInput, @PathVariable Long userId) {

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
    public ResponseEntity<Void> addPhoto(PhotoInputDto photo, @PathVariable Long userId) throws IOException {

        final User user = userService.read(userId);

        String originalFilename = user.getUserId() + "_" + user.getName() + Objects.requireNonNull(photo.getPhoto().getOriginalFilename()).substring(photo.getPhoto().getOriginalFilename().lastIndexOf("."));

        final Path path = Path.of("src/resources/photos/", String.valueOf(userId));
        final Path resolve = path.resolve(originalFilename);

        if(!path.toFile().exists()){
            path.toFile().mkdirs();
        }

        photo.setFileName(originalFilename);
        photo.getPhoto().transferTo(resolve);
        return ResponseEntity.noContent().build();

    }

}

package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.security.CheckSecurityPermissionMethods;
import com.ufpa.lafocabackend.domain.model.Group;
import com.ufpa.lafocabackend.domain.model.User;
import com.ufpa.lafocabackend.domain.model.dto.GroupDto;
import com.ufpa.lafocabackend.domain.model.dto.UserDto;
import com.ufpa.lafocabackend.domain.model.dto.input.UserDtoInput;
import com.ufpa.lafocabackend.domain.model.dto.input.userInputPasswordDTO;
import com.ufpa.lafocabackend.domain.service.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @CheckSecurityPermissionMethods.Level1
    @PostMapping
    public ResponseEntity<UserDto> add(@RequestBody @Valid UserDtoInput userDtoInput) {

        final User user = modelMapper.map(userDtoInput, User.class);

        final UserDto userDto = modelMapper.map(userService.save(user), UserDto.class);

        return ResponseEntity.ok(userDto);
    }

    @CheckSecurityPermissionMethods.Level1
    @GetMapping
    public ResponseEntity<List<UserDto>> list() {

        final List<User> users = userService.list();
        final List<UserDto> dtos = users.stream().map(u -> modelMapper.map(u, UserDto.class)).toList();

        return ResponseEntity.ok(dtos);
    }

    @CheckSecurityPermissionMethods.User.UserHimselfOrLevel1
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> read(@PathVariable String userId) {

        final User user = userService.read(userId);

        final UserDto userDto = modelMapper.map(user, UserDto.class);

        return ResponseEntity.ok(userDto);
    }

    @CheckSecurityPermissionMethods.User.UserHimselfOrLevel1
    @GetMapping("/search/{slug}")
    public ResponseEntity<UserDto> readBySlug(@PathVariable String slug) {

        final User user = userService.readBySlug(slug);
        final UserDto userDto = modelMapper.map(user, UserDto.class);

        return ResponseEntity.ok(userDto);
    }

    @CheckSecurityPermissionMethods.User.UserHimselfOrLevel1OrLevel2
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> update(@RequestBody UserDtoInput userDtoInput, @PathVariable String userId) {

        final User existingUser = userService.read(userId);

        modelMapper.map(userDtoInput, existingUser);
        final UserDto userDto = modelMapper.map(existingUser, UserDto.class);
        return ResponseEntity.ok(userDto);
    }

    @CheckSecurityPermissionMethods.User.UserHimselfOrLevel1
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable String userId) {

        userService.removePhoto(userService.read(userId));
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/password")
    @CheckSecurityPermissionMethods.User.UserHimselfOrLevel1
    public ResponseEntity<Void> updatePassword(@RequestBody @Valid userInputPasswordDTO passwordDTO, @PathVariable String userId) {

        userService.changePassword(passwordDTO, userId);
        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.User.UserHimselfOrLevel1
    @PostMapping(value = "/{userId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addPhoto(MultipartFile photo, @PathVariable String userId) throws IOException {

        User user = userService.read(userId);
        String url = userService.addPhoto(new MultipartFileWrapper(photo), user);

        return ResponseEntity.ok(url);
    }

    @CheckSecurityPermissionMethods.User.UserHimselfOrLevel1
    @PostMapping(value = "/search/{userSlug}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addPhotoBySlug(MultipartFile photo, @PathVariable String userSlug) throws IOException {

        User user = userService.readBySlug(userSlug);
        String url = userService.addPhoto(new MultipartFileWrapper(photo), user);

        return ResponseEntity.ok(url);
    }

    @CheckSecurityPermissionMethods.User.UserHimselfOrLevel1
    @DeleteMapping(value = "/{userId}/photo")
    public ResponseEntity<Void> deletePhoto(@PathVariable String userId) {

        User user = userService.read(userId);
        userService.removePhoto(user);

        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.User.UserHimselfOrLevel1
    @DeleteMapping(value = "/search/{userSlug}/photo")
    public ResponseEntity<Void> deletePhotoBySlug(@PathVariable String userSlug) {

        User user = userService.readBySlug(userSlug);
        userService.removePhoto(user);
        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.User.UserHimselfOrLevel1
    @GetMapping("/{userId}/groups")
    public ResponseEntity<Collection<GroupDto>> listGroups(@PathVariable String userId){

        final User user = userService.read(userId);

        final Set<Group> groups = user.getGroups();

        final Type type = new TypeToken<Set<GroupDto>>() {
        }.getType();

        final Set<GroupDto> groupsDto = modelMapper.map(groups, type);

        return ResponseEntity.ok(groupsDto);
    }

    @CheckSecurityPermissionMethods.Level1
    @PutMapping("/{userId}/groups/{groupId}")
    public ResponseEntity<Void> associateGroup(@PathVariable String userId, @PathVariable Long groupId) {

        userService.addGroup(userId, groupId);

        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.Level1
    @DeleteMapping("/{userId}/groups/{groupId}")
    public ResponseEntity<Void> DisassociateGroup(@PathVariable String userId, @PathVariable Long groupId) {

        userService.removeGroup(userId, groupId);

        return ResponseEntity.noContent().build();
    }
}

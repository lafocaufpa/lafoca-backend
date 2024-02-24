package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.security.CheckSecurityPermissionMethods;
import com.ufpa.lafocabackend.domain.model.Group;
import com.ufpa.lafocabackend.domain.model.User;
import com.ufpa.lafocabackend.domain.model.UserPhoto;
import com.ufpa.lafocabackend.domain.model.dto.GroupDto;
import com.ufpa.lafocabackend.domain.model.dto.PhotoDto;
import com.ufpa.lafocabackend.domain.model.dto.UserDto;
import com.ufpa.lafocabackend.domain.model.dto.input.UserDtoInput;
import com.ufpa.lafocabackend.domain.model.dto.input.userInputPasswordDTO;
import com.ufpa.lafocabackend.infrastructure.service.PhotoStorageService.RecoveredPhoto;
import com.ufpa.lafocabackend.domain.service.UserPhotoService;
import com.ufpa.lafocabackend.domain.service.UserService;
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
import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;
    private final UserPhotoService userPhotoService;

    public UserController(UserService userService, ModelMapper modelMapper, UserPhotoService userPhotoService) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.userPhotoService = userPhotoService;
    }

    @CheckSecurityPermissionMethods.User.L1L2
    @PostMapping
    public ResponseEntity<UserDto> add(@RequestBody UserDtoInput userDtoInput) {

        final User user = modelMapper.map(userDtoInput, User.class);

        final UserDto userDto = modelMapper.map(userService.save(user), UserDto.class);

        return ResponseEntity.ok(userDto);
    }

    @CheckSecurityPermissionMethods.User.L1L2
    @GetMapping
    public ResponseEntity<List<UserDto>> list() {

        final List<User> users = userService.list();
        final List<UserDto> dtos = users.stream().map(u -> modelMapper.map(u, UserDto.class)).toList();

        return ResponseEntity.ok(dtos);
    }

    @CheckSecurityPermissionMethods.User.L1L2OrUserHimself
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> read(@PathVariable String userId) {

        final User user = userService.read(userId);

        final UserDto userDto = modelMapper.map(user, UserDto.class);

        return ResponseEntity.ok(userDto);
    }

    @CheckSecurityPermissionMethods.User.L1L2OrUserHimself
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> update(@RequestBody UserDtoInput userDtoInput, @PathVariable String userId) {

        final User existingUser = userService.read(userId);

        modelMapper.map(userDtoInput, existingUser);
        final UserDto userDto = modelMapper.map(existingUser, UserDto.class);
        return ResponseEntity.ok(userDto);
    }

    @CheckSecurityPermissionMethods.User.L1OrUserHimself
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable String userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/password")
    @CheckSecurityPermissionMethods.User.L1L2OrUserHimself
    public ResponseEntity<Void> updatePassword(@RequestBody userInputPasswordDTO passwordDTO, @PathVariable String userId) {

        userService.changePassword(passwordDTO, userId);
        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.User.L1L2OrUserHimself
    @PostMapping(value = "{userId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhotoDto> addPhoto(MultipartFile photo, @PathVariable String userId) throws IOException {

        final User user = userService.read(userId);

        String originalFilename = user.getUserId()
                + "_"
                + user.getName()
                + Objects.requireNonNull
                        (photo.getOriginalFilename())
                .substring(photo.getOriginalFilename().lastIndexOf("."));

        final UserPhoto userPhoto = new UserPhoto();
        userPhoto.setUserPhotoId(user.getUserId());
        userPhoto.setFileName(originalFilename);
        userPhoto.setSize(photo.getSize());
        userPhoto.setContentType(photo.getContentType());

        final UserPhoto photoSaved = userPhotoService.save(userPhoto, photo.getInputStream());

        user.setUserPhoto(photoSaved);
        userService.save(user);

        final PhotoDto photoDto = modelMapper.map(photoSaved, PhotoDto.class);

        return ResponseEntity.ok(photoDto);
    }

    @CheckSecurityPermissionMethods.User.L1L2OrUserHimself
    @GetMapping(value = "{userId}/photo")
    public ResponseEntity<?> getPhoto(@PathVariable String userId) {

        final User user = userService.read(userId);
        final UserPhoto photo = user.getUserPhoto();

        if (photo == null) {
            return ResponseEntity.notFound().build();
        }

        if (photo.getUrl() != null) {
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, photo.getUrl()).build();
        } else {

            final RecoveredPhoto recoveredPhoto = userPhotoService.get(photo.getFileName());

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, photo.getContentType());

            return ResponseEntity.ok().headers(headers).body(new InputStreamResource(recoveredPhoto.getInputStream()));
        }

    }

    @CheckSecurityPermissionMethods.User.L1L2OrUserHimself
    @DeleteMapping(value = "{userId}/photo")
    public ResponseEntity<Void> deletePhoto(@PathVariable String userId) {

        userService.userExists(userId);
        userPhotoService.delete(userId);

        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.L1
    @GetMapping("/{userId}/groups")
    public ResponseEntity<Collection<GroupDto>> listGroups(@PathVariable String userId){

        final User user = userService.read(userId);

        final Set<Group> groups = user.getGroups();

        final Type type = new TypeToken<Set<GroupDto>>() {
        }.getType();

        final Set<GroupDto> groupsDto = modelMapper.map(groups, type);

        return ResponseEntity.ok(groupsDto);
    }

    @PutMapping("/{userId}/groups/{groupId}")
    public ResponseEntity<Void> associateGroup(@PathVariable String userId, @PathVariable Long groupId) {

        userService.addGroup(userId, groupId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/groups/{groupId}")
    public ResponseEntity<Void> DisassociateGroup(@PathVariable String userId, @PathVariable Long groupId) {

        userService.removeGroup(userId, groupId);

        return ResponseEntity.noContent().build();
    }
}

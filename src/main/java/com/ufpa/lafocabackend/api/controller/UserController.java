package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.security.CheckSecurityPermissionMethods;
import com.ufpa.lafocabackend.core.file.StandardCustomMultipartFile;
import com.ufpa.lafocabackend.core.security.dto.ResetPassword;
import com.ufpa.lafocabackend.core.utils.LafocaCacheUtil;
import com.ufpa.lafocabackend.domain.enums.ErrorMessage;
import com.ufpa.lafocabackend.domain.exception.CannotDeleteOnlyAdministratorException;
import com.ufpa.lafocabackend.domain.model.Group;
import com.ufpa.lafocabackend.domain.model.User;
import com.ufpa.lafocabackend.domain.model.dto.input.UserPersonalInputDto;
import com.ufpa.lafocabackend.domain.model.dto.output.GroupDto;
import com.ufpa.lafocabackend.domain.model.dto.output.UserDto;
import com.ufpa.lafocabackend.domain.model.dto.input.UserInputDto;
import com.ufpa.lafocabackend.domain.model.dto.input.UserInputPasswordDTO;
import com.ufpa.lafocabackend.domain.service.UserService;
import jakarta.servlet.http.Part;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @CheckSecurityPermissionMethods.AdminOrManagerUsersGroups
    @PostMapping
    public ResponseEntity<UserDto> add(@RequestBody @Valid UserInputDto userInputDto) {

        final UserDto userDto = modelMapper.map(userService.save(userInputDto), UserDto.class);

        return ResponseEntity.ok(userDto);
    }

    @CheckSecurityPermissionMethods.AdminOrManagerUsersGroups
    @GetMapping
    public ResponseEntity<Page<UserDto>> list(Pageable pageable) throws InterruptedException {

        final Page<User> users = userService.list(pageable);

        Type type = new TypeToken<List<UserDto>>() {

        }.getType();

        List<UserDto> map = modelMapper.map(users.getContent(), type);

        PageImpl<UserDto> userDtos = new PageImpl<>(map, pageable, users.getTotalElements());

        return LafocaCacheUtil.createCachedResponseUser(userDtos);
    }

    @CheckSecurityPermissionMethods.UserHimselfOrManagerUsersGroupsOrAdmin
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> read(@PathVariable String userId) {

        final User user = userService.read(userId);

        final UserDto userDto = modelMapper.map(user, UserDto.class);

        return LafocaCacheUtil.createCachedResponseUser(userDto);
    }

    @CheckSecurityPermissionMethods.UserHimselfOrManagerUsersGroupsOrAdmin
    @GetMapping("/read/{slug}")
    public ResponseEntity<UserDto> readBySlug(@PathVariable String slug) {

        final User user = userService.readBySlug(slug);
        final UserDto userDto = modelMapper.map(user, UserDto.class);

        return LafocaCacheUtil.createCachedResponseUser(userDto);
    }

    @CheckSecurityPermissionMethods.UserHimselfOrManagerUsersGroupsOrAdmin
    @GetMapping("/read-by-email/{userEmail}")
    public ResponseEntity<UserDto> readByEmail (@PathVariable String userEmail) {

        final User user = userService.readByEmail(userEmail);
        final UserDto userDto = modelMapper.map(user, UserDto.class);

        return LafocaCacheUtil.createCachedResponseUser(userDto);
    }

    @CheckSecurityPermissionMethods.UserHimselfOrManagerUsersGroupsOrAdmin
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> update(@RequestBody @Valid UserPersonalInputDto userInputDto, @PathVariable String userId) {

        final UserDto userDto = modelMapper.map(userService.update(userInputDto, userId), UserDto.class);
        return ResponseEntity.ok(userDto);
    }

    @CheckSecurityPermissionMethods.UserHimselfOrManagerUsersGroupsOrAdmin
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable String userId) {


        User user = userService.read(userId);

        boolean thereAreTwoOrMoreAdministrators = userService.existsMoreThanOneAdministrator();
        if (!thereAreTwoOrMoreAdministrators && userId.equals(userService.getAuthentication())) {
            throw new CannotDeleteOnlyAdministratorException(ErrorMessage.UNICO_ADM.get());
        }

        userService.removePhoto(user);
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/password")
    @CheckSecurityPermissionMethods.UserHimselfOrManagerUsersGroupsOrAdmin
    public ResponseEntity<Void> updatePassword(@RequestBody @Valid UserInputPasswordDTO passwordDTO, @PathVariable String userId) {

        userService.changePassword(passwordDTO, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPassword email) {

        userService.resetPassword(email.email());
        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.UserHimselfOrManagerUsersGroupsOrAdmin
    @PostMapping(value = "/{userId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addPhoto(Part file, @PathVariable String userId) throws IOException {

        var customFile = new StandardCustomMultipartFile(file);
        User user = userService.read(userId);
        if(user.getUrlPhoto() != null){
            userService.removePhoto(user);
        }

        String url = userService.addPhoto(customFile, user);

        return ResponseEntity.ok(url);
    }

    @CheckSecurityPermissionMethods.UserHimselfOrManagerUsersGroupsOrAdmin
    @PostMapping(value = "/read/{userSlug}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addPhotoBySlug(Part file, @PathVariable String userSlug) throws IOException {

        var customFile = new StandardCustomMultipartFile(file);
        User user = userService.readBySlug(userSlug);
        String url = userService.addPhoto(customFile, user);

        return ResponseEntity.ok(url);
    }

    @CheckSecurityPermissionMethods.UserHimselfOrManagerUsersGroupsOrAdmin
    @DeleteMapping(value = "/{userId}/photo")
    public ResponseEntity<Void> deletePhoto(@PathVariable String userId) {

        User user = userService.read(userId);
        userService.removePhoto(user);

        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.UserHimselfOrManagerUsersGroupsOrAdmin
    @DeleteMapping(value = "/read/{userSlug}/photo")
    public ResponseEntity<Void> deletePhotoBySlug(@PathVariable String userSlug) {

        User user = userService.readBySlug(userSlug);
        userService.removePhoto(user);
        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.AdminOrManagerUsersGroups
    @GetMapping("/{userId}/groups")
    public ResponseEntity<Collection<GroupDto>> listGroups(@PathVariable String userId) {

        final User user = userService.read(userId);

        final Set<Group> groups = user.getGroups();

        final Type type = new TypeToken<Set<GroupDto>>() {
        }.getType();

        final Set<GroupDto> groupsDto = modelMapper.map(groups, type);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(groupsDto);
    }

    @CheckSecurityPermissionMethods.AdminOrManagerUsersGroups
    @PutMapping("/{userId}/groups/{groupId}")
    public ResponseEntity<Void> associateGroup(@PathVariable String userId, @PathVariable Long groupId) {

        userService.addGroup(userId, groupId);

        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.AdminOrManagerUsersGroups
    @DeleteMapping("/{userId}/groups/{groupId}")
    public ResponseEntity<Void> DisassociateGroup(@PathVariable String userId, @PathVariable Long groupId) {

        userService.removeGroup(userId, groupId);

        return ResponseEntity.noContent().build();
    }
}

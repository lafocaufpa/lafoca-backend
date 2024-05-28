package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.core.file.CustomMultipartFile;
import com.ufpa.lafocabackend.core.utils.LafocaUtils;
import com.ufpa.lafocabackend.core.utils.StoragePhotoUtils;
import com.ufpa.lafocabackend.core.utils.TypeEntityPhoto;
import com.ufpa.lafocabackend.domain.exception.*;
import com.ufpa.lafocabackend.domain.model.Group;
import com.ufpa.lafocabackend.domain.model.User;
import com.ufpa.lafocabackend.domain.model.dto.input.UserInputDto;
import com.ufpa.lafocabackend.domain.model.dto.input.UserInputPasswordDTO;
import com.ufpa.lafocabackend.infrastructure.SmtpEmailService;
import com.ufpa.lafocabackend.infrastructure.service.PhotoStorageService;
import com.ufpa.lafocabackend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.ufpa.lafocabackend.core.utils.LafocaUtils.createPhotoFilename;

@Service
@EnableAsync
public class UserService {

    @Value("${group.admin.id}")
    private Long adminGroupId;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GroupService groupService;
    private final PhotoStorageService photoStorageService;
    private final SmtpEmailService smtpSendEmailService;
    private final ModelMapper modelMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, GroupService groupService, PhotoStorageService photoStorageService, SmtpEmailService smtpSendEmailService, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.groupService = groupService;
        this.photoStorageService = photoStorageService;
        this.smtpSendEmailService = smtpSendEmailService;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public User save(User user) {

        /*Se um user vindo do banco com o mesmo email for diferente do user vindo da requisição, cai no if */
        if (userRepository.existsUserByEmail(user.getEmail())) {
            throw new EntityAlreadyRegisteredException(User.class.getSimpleName(), user.getEmail());

        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public List<User> list() {
        return userRepository.findAll();
    }

    @Transactional
    public User update(UserInputDto userInputDto, String userId) {

        User user = read(userId);

        modelMapper.map(userInputDto, user);

        Set<Group> groups = new HashSet<>();

        for (Long groupId : userInputDto.getGroups()) {
            Group group = groupService.read(groupId);
            groups.add(group);
        }

        user.setGroups(groups);
        return userRepository.save(user);
    }

    @Transactional
    public void delete(String userId) {

        try {
            removePhoto(read(userId));
            userRepository.deleteById(userId);

        } catch (DataIntegrityViolationException e) {
            throw new EntityInUseException(User.class.getSimpleName(), userId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(User.class.getSimpleName(), userId);
        }
    }

    private User getOrFail(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(getClass().getSimpleName(), userId));
    }

    public User read(String userId) {
        return getOrFail(userId);
    }

    public User readBySlug(String slug) {
        return userRepository.findBySlug(slug).
                orElseThrow(() -> new EntityNotFoundException(User.class.getSimpleName(), slug));
    }

    public User readByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(User.class.getSimpleName(), email));
    }

    @Transactional
    public void changePassword(UserInputPasswordDTO passwordDTO, String userId) {
        final User user = getOrFail(userId);

        if (!passwordEncoder.matches(passwordDTO.getCurrentPassword(), user.getPassword()))
            throw new PasswordDoesNotMachException();

        user.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
    }

    @Transactional
    public void addGroup(String userId, Long groupId) {
        final User user = read(userId);
        final Group group = groupService.read(groupId);
        user.addGroup(group);
    }

    @Transactional
    public void removeGroup(String userId, Long groupId) {
        final User user = read(userId);
        final Group group = groupService.read(groupId);

        user.removeGroup(group);
    }

    @Transactional
    public String addPhoto(CustomMultipartFile photo, User user) throws IOException {

        String originalFilename = createPhotoFilename(user.getSlug(), photo.getOriginalFilename());

        StoragePhotoUtils photoUtils = StoragePhotoUtils.builder()
                .fileName(originalFilename)
                .contentType(photo.getContentType())
                .contentLength(photo.getSize())
                .type(TypeEntityPhoto.User)
                .inputStream(photo.getInputStream())
                .build();

        String url = photoStorageService.armazenar(photoUtils);
        user.setUrlPhoto(url);

        return url;
    }

    @Transactional
    public void removePhoto(User user) {

        if (user.getUrlPhoto() != null) {

            photoStorageService
                    .deletar(StoragePhotoUtils.builder()
                            .fileName(createPhotoFilename(user.getSlug(), user.getUrlPhoto()))
                            .type(TypeEntityPhoto.User)
                            .build());
            user.removePhoto();
        }
    }

    public boolean existsAdminUser() {
        return userRepository.countUsersInGroup(adminGroupId) >= 1;
    }

    public boolean existsMoreThanOneAdministrator() {
        return userRepository.countUsersInGroup(adminGroupId) > 1;
    }

    @Transactional
    @Async
    public void resetPassword(String email) {

        User user = null;
        try {
            user = readByEmail(email);
        } catch (EntityNotFoundException e) {
            return;
        }

        String password = LafocaUtils.generateRandomPassword();

        sendResetPasswordEmailAsync(email, user, password);

        user.setPassword(passwordEncoder.encode(password));

    }

    public void sendResetPasswordEmailAsync(String email, User user, String password) {
        EmailService.Message message = EmailService.Message.builder()
                .body("reset-password.html")
                .variable("password", password)
                .variable("user", user)
                .recipient(email)
                .subject("Redefinição de Senha").build();
        smtpSendEmailService.send(message);
    }
}

package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.core.file.CustomMultipartFile;
import com.ufpa.lafocabackend.core.utils.StoragePhotoUtils;
import com.ufpa.lafocabackend.core.utils.TypeEntityPhoto;
import com.ufpa.lafocabackend.domain.exception.EntityAlreadyRegisteredException;
import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.exception.PasswordDoesNotMachException;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.ufpa.lafocabackend.core.utils.LafocaUtils.createPhotoFilename;

@Service
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
        if(userRepository.existsUserByEmail(user.getEmail())){
            throw  new EntityAlreadyRegisteredException(User.class.getSimpleName(), user.getEmail());

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

        for (Long groupId: userInputDto.getGroups()) {
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

    public void resetPassword(String email) {

        String body = "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <title>Password Reset</title>" +
                "    <style>" +
                "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                "        .container { width: 100%; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); }" +
                "        .header { text-align: center; padding-bottom: 20px; }" +
                "        .header h1 { margin: 0; color: #007BFF; }" +
                "        .content { margin: 20px 0; }" +
                "        .content p { margin: 10px 0; }" +
                "        .footer { margin-top: 20px; text-align: center; font-size: 0.9em; color: #666; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class=\"container\">" +
                "        <div class=\"header\">" +
                "            <h1>Password Reset</h1>" +
                "        </div>" +
                "        <div class=\"content\">" +
                "            <p>Hi <strong>Name</strong>,</p>" +
                "            <p>Someone - presumably you - has requested a password reset on Lafoca.com.br</p>" +
                "            <p>Your new login information is as follows:</p>" +
                "            <p>Username: <strong>username</strong></p>" +
                "            <p>Password: <strong>12345678</strong></p>" +
                "            <p>To login, please click the link below or paste it into your browser's address bar.</p>" +
                "            <p><a href=\"https://lafoca.com.br/login\" target=\"_blank\">https://lafoca.com.br/login</a></p>" +
                "        </div>" +
                "        <div class=\"footer\">" +
                "            <p>PLEASE DO NOT REPLY TO THIS MESSAGE!</p>" +
                "            <p>Regards,</p>" +
                "            <p>Lafoca.com.br Administrator</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";

        userRepository.findByEmail(email).isEmpty();

            EmailService.Message message = EmailService.Message.builder()
                    .body(body)
                    .recipient(email)
                    .subject("Password Reset").build();
            smtpSendEmailService.send(message);

    }
}

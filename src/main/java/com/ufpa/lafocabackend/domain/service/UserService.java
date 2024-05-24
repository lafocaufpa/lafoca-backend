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
import com.ufpa.lafocabackend.domain.model.dto.input.UserInputPasswordDTO;
import com.ufpa.lafocabackend.infrastructure.service.PhotoStorageService;
import com.ufpa.lafocabackend.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

import static com.ufpa.lafocabackend.core.utils.LafocaUtils.createPhotoFilename;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GroupService groupService;
    private final PhotoStorageService photoStorageService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, GroupService groupService, PhotoStorageService photoStorageService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.groupService = groupService;
        this.photoStorageService = photoStorageService;
    }

    @Transactional
    public User save(User user) {
        /*userRepository.detach(user); Antes de chamar o findbyEmail o SDJPA faz o commit dos objetos gerenciados
        por isso é necessário desanexar do contexto de persistencia, pois vai adicionar no bd um user com o mesmo
        email*/

        /*Se um user vindo do banco com o mesmo email for diferente do user vindo da requisição, cai no if */
        if(userRepository.existsByEmail(user.getEmail())){
            throw new EntityAlreadyRegisteredException(User.class.getSimpleName(), user.getEmail());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public List<User> list(){
        return userRepository.findAll();
    }

    @Transactional
    public User update(User user){
        return save(user);
    }

    @Transactional
    public void delete(String userId){

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
                .orElseThrow( () -> new EntityNotFoundException(getClass().getSimpleName(), userId));
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

        if(!passwordEncoder.matches(passwordDTO.getCurrentPassword(), user.getPassword()))
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
    public void removeGroup (String userId, Long groupId) {
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

        if(user.getUrlPhoto() != null){

            photoStorageService
                    .deletar(StoragePhotoUtils.builder()
                            .fileName(createPhotoFilename(user.getSlug(), user.getUrlPhoto()))
                            .type(TypeEntityPhoto.User)
                            .build());
            user.removePhoto();
        }
    }

    public boolean existsMoreThanOneAdministrator() {
        return userRepository.existsMoreThanOneAdministrator(1L);
    }
}

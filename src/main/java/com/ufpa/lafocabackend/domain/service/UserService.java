package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.exception.EntityAlreadyRegisteredException;
import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.exception.PasswordDoesNotMachException;
import com.ufpa.lafocabackend.domain.model.Group;
import com.ufpa.lafocabackend.domain.model.User;
import com.ufpa.lafocabackend.domain.model.dto.input.userInputPasswordDTO;
import com.ufpa.lafocabackend.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GroupService groupService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, GroupService groupService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.groupService = groupService;
    }

    @Transactional
    public User save(User user) {
        /*userRepository.detach(user); Antes de chamar o findbyEmail o SDJPA faz o commit dos objetos gerenciados
        por isso é necessário desanexar do contexto de persistencia, pois vai adicionar no bd um user com o mesmo
        email*/

        final Optional<User> existingUser = userRepository.findByEmail(user.getEmail());

        /*Se um user vindo do banco com o mesmo email for diferente do user vindo da requisição, cai no if */
        if(existingUser.isPresent() && !existingUser.get().equals(user)){
            throw new EntityAlreadyRegisteredException(getClass().getSimpleName(), user.getEmail());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        final Group group = groupService.standardMemberLafoca();
        user.addGroup(group);

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
            userRepository.deleteById(userId);
            userRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new EntityInUseException(getClass().getSimpleName(), userId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(getClass().getSimpleName(), userId);
        }
    }

    public void userExists(String userId){

        if(!userRepository.existsByUserId(userId)){
            throw new EntityNotFoundException(getClass().getSimpleName(), userId);
        }
    }

    private User getOrFail(String userId) {
        return userRepository.findById(userId)
                .orElseThrow( () -> new EntityNotFoundException(getClass().getSimpleName(), userId));
    }

    public User read(String userId) {
        return getOrFail(userId);
    }

    @Transactional
    public void changePassword(userInputPasswordDTO passwordDTO, String userId) {
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
}

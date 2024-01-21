package com.ufpa.lafocabackend.domain.service;

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

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User save(User user) {
        /*userRepository.detach(user); Antes de chamar o findbyEmail o SDJPA faz o commit dos objetos gerenciados
        por isso é necessário desanexar do contexto de persistencia, pois vai adicionar no bd um user com o mesmo
        email*/

        final Optional<User> existingUser = userRepository.findByEmail(user.getEmail());

        /*Se um user vindo do banco com o mesmo email for diferente do user vindo da requisição, cai no if */
        if(existingUser.isPresent() && !existingUser.get().equals(user)){
            throw new RuntimeException("User already registered");
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
            userRepository.deleteById(userId);
            userRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Entity in use: id " + userId);
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("User not found: id " + userId);
        }

    }

    public void userExists(String userId){

        if(!userRepository.existsByUserId(userId)){
            throw new RuntimeException("User not found: id " + userId);
        }
    }

    private User getOrFail(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: id " + userId));
    }

    public User read(String userId) {
        return getOrFail(userId);
    }

    @Transactional
    public void changePassword(userInputPasswordDTO passwordDTO, String userId) {
        final User user = getOrFail(userId);

        if(!passwordEncoder.matches(passwordDTO.getCurrentPassword(), user.getPassword()))
            throw new RuntimeException("Password does not match");

        user.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
    }
}

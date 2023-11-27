package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.model.User;
import com.ufpa.lafocabackend.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PhotoStorageService photoStorageService;

    public UserService(UserRepository userRepository, PhotoStorageService photoStorageService) {
        this.userRepository = userRepository;
        this.photoStorageService = photoStorageService;
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

        photoStorageService.deletar(userId);
    }

    private User getOrFail(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: id " + userId));
    }

    public User read(String userId) {
        return getOrFail(userId);
    }
}

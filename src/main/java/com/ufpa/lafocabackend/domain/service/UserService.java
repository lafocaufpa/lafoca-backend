package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.model.User;
import com.ufpa.lafocabackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
//
//    @Transactional
//    public User save(User user) {
//        userRepository.detach(user); /*Antes de chamar o findbyEmail o SDJPA faz o commit dos objetos gerenciados
//        por isso é necessário desanexar do contexto de persistencia, pois vai adicionar no bd um user com o mesmo
//        email*/
//
//        final Optional<User> usuarioExistente = userRepository.findByEmail(user.getEmail());
//
//        /*Se um user vindo do banco com o mesmo email for diferente do user vindo da requisição, cai no if */
//        if(usuarioExistente.isPresent() && !usuarioExistente.get().equals(user)){
//            throw new NegocioException(String.format(ErrorMessage.EMAIL_JA_CADASTRADO.get(), user.getEmail()));
//        }
//        user.setSenha(passwordEncoder.encode(user.getSenha()));
//        return userRepository.save(user);
//    }
}

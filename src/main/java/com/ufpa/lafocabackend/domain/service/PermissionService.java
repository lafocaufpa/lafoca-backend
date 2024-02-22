package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.model.Permission;
import com.ufpa.lafocabackend.domain.model.PermissionRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService {


    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public Permission save (Permission permission) {

        return permissionRepository.save(permission);
    }

    public List<Permission> list (){

        return permissionRepository.findAll();
    }

    public Permission read (Long permissionId) {
        exist(permissionId);

        return permissionRepository.findById(permissionId).get();
    }

    public Permission update (Permission permission) {

        return save(permission);
    }

    public void delete (Long permissionId) {

        try {
            permissionRepository.deleteById(permissionId);
        } catch (DataIntegrityViolationException e) {
            throw new EntityInUseException(Permission.class.getSimpleName(), permissionId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(Permission.class.getSimpleName(), permissionId);
        }

    }

    private void exist(Long permissionId){

        if(!permissionRepository.existsById(permissionId)){
//            throw new RuntimeException("User not found: id " + userId);
            throw new EntityNotFoundException(Permission.class.getSimpleName(), permissionId);

        }
    }
}

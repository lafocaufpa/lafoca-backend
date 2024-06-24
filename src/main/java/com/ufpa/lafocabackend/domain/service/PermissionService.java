package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.model.Permission;
import com.ufpa.lafocabackend.repository.PermissionRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PermissionService {


    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public Permission save (Permission permission) {

        return permissionRepository.save(permission);
    }

    public Page<Permission> list (Pageable pageable){

        return permissionRepository.findAll(pageable);
    }

    public Page<Permission> searchByName(String name, Pageable pageable) {
        return permissionRepository.findByNameContaining(name, pageable);
    }


    public Permission read (Long permissionId) {

        return getOrFail(permissionId);
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

    private Permission getOrFail(Long permissionId) {
        return permissionRepository.findById(permissionId)
                .orElseThrow( () -> new EntityNotFoundException(Permission.class.getSimpleName(), permissionId));
    }

    public Optional<Permission> orElseGet(Long id) {
        return permissionRepository.findById(id);
    }
}

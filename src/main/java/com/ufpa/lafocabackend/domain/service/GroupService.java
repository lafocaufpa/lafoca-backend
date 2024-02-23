package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.model.Group;
import com.ufpa.lafocabackend.domain.model.Permission;
import com.ufpa.lafocabackend.repository.GroupRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GroupService {
    
    private final GroupRepository groupRepository;
    private final PermissionService permissionService;

    public GroupService(GroupRepository groupRepository, PermissionService permissionService) {
        this.groupRepository = groupRepository;
        this.permissionService = permissionService;
    }

    public Group save (Group group) {

        return groupRepository.save(group);
    }

    public List<Group> list (){

        return groupRepository.findAll();
    }

    public Group read (Long groupId) {
        exist(groupId);

        return groupRepository.findById(groupId).get();
    }

    public Group update (Group group) {

        return save(group);
    }

    public void delete (Long groupId) {

        try {
            groupRepository.deleteById(groupId);
        } catch (DataIntegrityViolationException e) {
            throw new EntityInUseException(Group.class.getSimpleName(), groupId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(Group.class.getSimpleName(), groupId);
        }

    }

    private void exist(Long groupId){

        if(!groupRepository.existsById(groupId)){
            throw new EntityNotFoundException(Group.class.getSimpleName(), groupId);

        }
    }

    @Transactional
    public void addPermission(Long groupId, Long permissionId) {
        final Group group = read(groupId);
        final Permission permission = permissionService.read(permissionId);

        group.associatePermission(permission);
        groupRepository.save(group);
    }

    @Transactional
    public void removePermission(Long groupId, Long permissionId) {
        final Group group = read(groupId);
        final Permission permission = permissionService.read(permissionId);

        group.disassociatePermission(permission);
        groupRepository.save(group);
    }
}

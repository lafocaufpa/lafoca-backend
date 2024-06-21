package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.model.Group;
import com.ufpa.lafocabackend.domain.model.Permission;
import com.ufpa.lafocabackend.repository.GroupRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GroupService {
    
    private final GroupRepository groupRepository;
    private final PermissionService permissionService;

    public GroupService(GroupRepository groupRepository,
                        PermissionService permissionService) {
        this.groupRepository = groupRepository;
        this.permissionService = permissionService;
    }

    public Group save (Group group) {

        return groupRepository.save(group);
    }

    public Page<Group> list (Pageable pageable){

        return groupRepository.findAll(pageable);
    }

    public Page<Group> searchByName(String name, Pageable pageable) {
        return groupRepository.findByNameContaining(name, pageable);
    }

    public Group read (Long groupId) {
        return getOrFail(groupId);
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

    private Group getOrFail(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow( () -> new EntityNotFoundException(Group.class.getSimpleName(), groupId));
    }

    public boolean existsById(Long groupId) {
        return groupRepository.existsById(groupId);
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

    public List<Group> listWithoutPagination() {
        return groupRepository.findAll();
    }
}

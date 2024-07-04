package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.model.Group;
import com.ufpa.lafocabackend.domain.model.Permission;
import com.ufpa.lafocabackend.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GroupService {

    @Value("${group.admin.name}")
    private String adminGroupName;

    @Value("${permission.admin.name}")
    private String permissionFullAccessName;

    @Value("${permission.admin.description}")
    private String permissionFullAccessDescription;
    
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
        ensureAdminHasFullAccess(group);
        return save(group);
    }

    private void ensureAdminHasFullAccess(Group group) {
        if (group.getName().equals(adminGroupName)) {
            boolean hasFullAccess = group.getPermissions().stream()
                    .anyMatch(permission -> permission.getName().equals(permissionFullAccessName));

            if (!hasFullAccess) {
                Optional<Permission> permission = permissionService.readByName(permissionFullAccessName);

                group.getPermissions().add(permission.get());
            }
        }
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

    public Optional<Group> existsByName(String name) {
        return groupRepository.findByName(name);
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

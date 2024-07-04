package com.ufpa.lafocabackend.core.database;

import com.ufpa.lafocabackend.domain.model.Group;
import com.ufpa.lafocabackend.domain.model.Permission;
import com.ufpa.lafocabackend.domain.model.User;
import com.ufpa.lafocabackend.domain.service.GroupService;
import com.ufpa.lafocabackend.domain.service.PermissionService;
import com.ufpa.lafocabackend.domain.service.UserService;
import com.ufpa.lafocabackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    private final GroupService groupService;
    private final PermissionService permissionService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;


    @Value("${group.admin.name}")
    private String adminGroupName;

    @Value("${permission.admin.name}")
    private String permissionFullAccessName;

    @Value("${permission.admin.description}")
    private String permissionFullAccessDescription;

    @Value("${default.admin.user.email}")
    private String defaultAdminEmail;

    @Value("${default.admin.user.name}")
    private String defaultAdminName;

    @Value("${default.admin.user.password}")
    private String defaultAdminPassword;

    public DataLoader(GroupService groupService, PermissionService permissionService, UserService userService, UserRepository userRepository, PasswordEncoder encoder) {
        this.groupService = groupService;
        this.permissionService = permissionService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) throws Exception {
        Permission permissionFullAccess = getOrCreatePermission(permissionFullAccessName, permissionFullAccessDescription);

        Optional<Group> groupAdmin = groupService.existsByName(adminGroupName);

        if (groupAdmin.isEmpty()) {
            Set<Permission> permissions = new HashSet<>();
            permissions.add(permissionFullAccess);

            Group admin = new Group();
            admin.setName(adminGroupName);
            admin.setPermissions(permissions);

            groupService.save(admin);
        }

        if (!userService.existsAdminUser()) {
            User defaultAdmin = new User();
            defaultAdmin.setEmail(defaultAdminEmail);
            defaultAdmin.setName(defaultAdminName);
            defaultAdmin.setPassword(encoder.encode(defaultAdminPassword));
            defaultAdmin.getGroups().add(groupService.existsByName(adminGroupName).get());
            userRepository.save(defaultAdmin);
        }
    }

    private Permission getOrCreatePermission(String name, String description) {
        return permissionService.readByName(name).orElseGet(() -> {
            Permission permission = new Permission();
            permission.setName(name);
            permission.setDescription(description);
            return permissionService.save(permission);
        });
    }
}
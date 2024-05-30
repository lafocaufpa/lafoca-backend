package com.ufpa.lafocabackend.infrastructure.config;

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
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    private final GroupService groupService;
    private final PermissionService permissionService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Value("${group.admin.id}")
    private Long adminGroupId;

    @Value("${group.admin.name}")
    private String adminGroupName;

    @Value("${permission.admin.level1.id}")
    private Long adminLevel1Id;

    @Value("${permission.admin.level1.name}")
    private String adminLevel1Name;

    @Value("${permission.admin.level1.description}")
    private String adminLevel1Description;

    @Value("${permission.admin.level2.id}")
    private Long adminLevel2Id;

    @Value("${permission.admin.level2.name}")
    private String adminLevel2Name;

    @Value("${permission.admin.level2.description}")
    private String adminLevel2Description;

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
        Permission adminLevel1 = getOrCreatePermission(adminLevel1Id, adminLevel1Name, adminLevel1Description);
        Permission adminLevel2 = getOrCreatePermission(adminLevel2Id, adminLevel2Name, adminLevel2Description);

        if (!groupService.existsById(adminGroupId)) {
            Set<Permission> permissions = new HashSet<>();
            permissions.add(adminLevel1);
            permissions.add(adminLevel2);

            Group admin = new Group();
            admin.setGroupId(adminGroupId);
            admin.setName(adminGroupName);
            admin.setPermissions(permissions);

            groupService.save(admin);
        }

        if (!userService.existsAdminUser()) {
            User defaultAdmin = new User();
            defaultAdmin.setEmail(defaultAdminEmail);
            defaultAdmin.setName(defaultAdminName);
            defaultAdmin.setPassword(encoder.encode(defaultAdminPassword));
            defaultAdmin.getGroups().add(groupService.read(adminGroupId));
            userRepository.save(defaultAdmin);
        }
    }

    private Permission getOrCreatePermission(Long id, String name, String description) {
        return permissionService.orElseGet(id).orElseGet(() -> {
            Permission permission = new Permission();
            permission.setPermissionId(id);
            permission.setName(name);
            permission.setDescription(description);
            return permissionService.save(permission);
        });
    }
}
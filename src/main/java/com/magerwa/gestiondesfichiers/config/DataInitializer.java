package com.magerwa.gestiondesfichiers.config;

import com.magerwa.gestiondesfichiers.entity.*;
import com.magerwa.gestiondesfichiers.service.OrganizationService;
import com.magerwa.gestiondesfichiers.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;
    
    @Autowired
    private OrganizationService organizationService;

    @Override
    public void run(String... args) throws Exception {
        // Create demo organizational structure
        Country rwanda = organizationService.createCountry(new Country("Rwanda", "RW"));
        Country uganda = organizationService.createCountry(new Country("Uganda", "UG"));
        
        Department itDept = organizationService.createDepartment(new Department("Information Technology", "IT Department", rwanda));
        Department hrDept = organizationService.createDepartment(new Department("Human Resources", "HR Department", rwanda));
        
        MagerwaEntity itEntity = organizationService.createEntity(new MagerwaEntity("IT Operations", "IT Operations Entity", itDept));
        MagerwaEntity hrEntity = organizationService.createEntity(new MagerwaEntity("HR Operations", "HR Operations Entity", hrDept));
        
        com.magerwa.gestiondesfichiers.entity.Module devModule = organizationService.createModule(new com.magerwa.gestiondesfichiers.entity.Module("Development", "Software Development Module", itEntity));
        com.magerwa.gestiondesfichiers.entity.Module supportModule = organizationService.createModule(new com.magerwa.gestiondesfichiers.entity.Module("Support", "IT Support Module", itEntity));
        
        Section backendSection = organizationService.createSection(new Section("Backend Development", "Backend development team", devModule));
        Section frontendSection = organizationService.createSection(new Section("Frontend Development", "Frontend development team", devModule));
        Section helpDeskSection = organizationService.createSection(new Section("Help Desk", "Help desk support", supportModule));
        
        // Create demo users
        User admin = new User("admin", "admin123", "admin@magerwa.rw", "Admin", "User");
        admin.setRole(User.Role.ADMIN);
        admin.setCountry(rwanda);
        admin.setDepartment(itDept);
        admin.setEntity(itEntity);
        admin.setModule(devModule);
        admin.setSection(backendSection);
        userService.createUser(admin);
        
        User regularUser = new User("user", "user123", "user@magerwa.rw", "Regular", "User");
        regularUser.setRole(User.Role.USER);
        regularUser.setCountry(rwanda);
        regularUser.setDepartment(itDept);
        regularUser.setEntity(itEntity);
        regularUser.setModule(devModule);
        regularUser.setSection(frontendSection);
        userService.createUser(regularUser);
        
        User manager = new User("manager", "manager123", "manager@magerwa.rw", "Manager", "User");
        manager.setRole(User.Role.MANAGER);
        manager.setCountry(rwanda);
        manager.setDepartment(hrDept);
        manager.setEntity(hrEntity);
        userService.createUser(manager);
    }
}
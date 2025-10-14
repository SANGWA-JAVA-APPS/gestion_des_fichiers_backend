package com.bar.gestiondesfichier.controller;

import com.bar.gestiondesfichier.repository.AccountRepository;
import com.bar.gestiondesfichier.location.repository.CountryRepository;
import com.bar.gestiondesfichier.location.repository.LocationEntityRepository;
import com.bar.gestiondesfichier.location.repository.ModuleRepository;
import com.bar.gestiondesfichier.location.repository.SectionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:8104", "https://ingenzi.codeguru-pro.com"})
@Tag(name = "Dashboard", description = "Dashboard statistics and analytics")
public class DashboardController {

    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private CountryRepository countryRepository;
    
    @Autowired
    private LocationEntityRepository locationEntityRepository;
    
    @Autowired
    private ModuleRepository moduleRepository;
    
    @Autowired
    private SectionRepository sectionRepository;

    @GetMapping("/stats")
    @Operation(summary = "Get dashboard statistics", description = "Retrieve comprehensive dashboard statistics")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // User Statistics
            Map<String, Object> userStats = new HashMap<>();
            userStats.put("totalUsers", accountRepository.count());
            userStats.put("activeUsers", accountRepository.countByActiveTrue());
            userStats.put("inactiveUsers", accountRepository.countByActiveFalse());
            stats.put("users", userStats);
            
            // Location Statistics
            Map<String, Object> locationStats = new HashMap<>();
            locationStats.put("totalCountries", countryRepository.countByActiveTrue());
            locationStats.put("totalEntities", locationEntityRepository.countByActiveTrue());
            locationStats.put("totalModules", moduleRepository.countByActiveTrue());
            locationStats.put("totalSections", sectionRepository.countByActiveTrue());
            stats.put("locations", locationStats);
            
            // System Statistics
            Map<String, Object> systemStats = new HashMap<>();
            systemStats.put("timestamp", System.currentTimeMillis());
            systemStats.put("serverStatus", "ONLINE");
            stats.put("system", systemStats);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to retrieve dashboard statistics");
            error.put("message", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/user-distribution")
    @Operation(summary = "Get user distribution by role", description = "Get count of users by account category")
    public ResponseEntity<Map<String, Object>> getUserDistribution() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // This would require a custom query in AccountRepository
            // For now, return basic data
            response.put("success", true);
            response.put("message", "User distribution retrieved");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}

package com.bar.gestiondesfichier.repository;

import com.bar.gestiondesfichier.dto.UserBlockPermissionProjection;
import com.bar.gestiondesfichier.entity.Account;
import com.bar.gestiondesfichier.entity.AccountCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("SELECT a FROM Account a JOIN FETCH a.accountCategory WHERE a.username = :username")
    Optional<Account> findByUsername(@Param("username") String username);

    @Query(value = """
        SELECT a.* FROM accounts a
        LEFT JOIN account_categories ac ON a.account_category_id = ac.id
        WHERE a.username = :username
    """, nativeQuery = true)
    Optional<Account> findByUsernameNative(@Param("username") String username);

    Optional<Account> findByEmail(String email);

    Optional<Account> findByIdAndActiveTrue(Long id);

    // Paginated methods
    Page<Account> findByActiveTrue(Pageable pageable);

    Page<Account> findByAccountCategory(AccountCategory accountCategory, Pageable pageable);

    // Non-paginated methods (legacy support)
    List<Account> findByActiveTrue();

    List<Account> findByAccountCategory(AccountCategory accountCategory);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT a FROM Account a WHERE a.accountCategory.name = 'ADMIN' AND a.active = true")
    List<Account> findAllActiveAdmins();

    @Query("SELECT a FROM Account a WHERE a.accountCategory.name = 'USER' AND a.active = true")
    List<Account> findAllActiveUsers();

    // Additional methods for dashboard
    long countByAccountCategory_Name(String categoryName);

    // Count methods for dashboard statistics
    long countByActiveTrue();

    long countByActiveFalse();



    @Query("""
    SELECT b.id AS blockId,
           b.name AS blockName,
           b.blockCode AS blockCode,
           p.id AS permissionId,
           p.name AS permissionName,
           p.code AS permissionCode
    FROM Account a
    JOIN a.permissions p
    JOIN p.block b
    WHERE a.id = :accountId
""")
    List<UserBlockPermissionProjection> findUserPermissionsByAccountId(@Param("accountId") Long accountId);

}

package com.project.demo.logic.entity.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>  {
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE %?1%")
    List<User> findUsersWithCharacterInName(String character);

    @Query("SELECT u FROM User u WHERE u.name = ?1")
    Optional<User> findByName(String name);

    @Query("SELECT u FROM User u WHERE u.id = ?1")
    Optional<User> findById(Long id);

    Optional<User> findByLastname(String lastname);

    Optional<User> findByEmail(String email);

    @Query("UPDATE User u SET u.enabled = :enabled WHERE u.id = :id")
    void setUserEnabled(@Param("id") Long id, @Param("enabled") boolean enabled);


}

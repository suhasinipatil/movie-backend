package com.example.moviebackend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    UserEntity findByUsername(String username);

    @Query("SELECT u FROM users u WHERE u.id = ?1")
    UserEntity findByUserId(Integer userId);
}

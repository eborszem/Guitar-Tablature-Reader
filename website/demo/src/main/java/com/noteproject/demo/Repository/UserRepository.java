package com.noteproject.demo.Repository;

import com.noteproject.demo.Entity.User;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    @NonNull Optional<User> findById(@NonNull Long id);
}

package com.wordle.blog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.wordle.blog.dto.UserResponseDTO;
import com.wordle.blog.enitity.User;
import com.wordle.blog.enums.Role;

@Repository
public interface UserRepository extends JpaRepository<User,Long>{

     Optional<User> findById(Long id);

     Page<User> findByIsActiveAndRole(Boolean isActive, Role role, Pageable pageable);

     Page<User> findByIsActive(Boolean isActive, Pageable pageable);

     Page<User> findByRole(Role role, Pageable pageable);

     boolean existsByEmail(String email);

     boolean existsByUsername(String username);

     @Query("select * from User")
     List<UserResponseDTO> findAllByProjection();
}

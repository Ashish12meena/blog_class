package com.wordle.blog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wordle.blog.enitity.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long>{

     Optional<User> findById(Long id);
}

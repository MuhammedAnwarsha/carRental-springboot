package com.example.carRental.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.carRental.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	User findByUsernameAndPassword(String username, String password);
    

    List<User> findByIsAdmin(boolean isAdmin);

}

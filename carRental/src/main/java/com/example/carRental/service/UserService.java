package com.example.carRental.service;



import com.example.carRental.model.User;

public interface UserService {
	User getUserById(int id);


	User saveUser(User user);


	User authenticateUser(String adminUsername, String adminPassword);




}

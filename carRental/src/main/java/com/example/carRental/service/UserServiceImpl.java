package com.example.carRental.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.carRental.model.User;
import com.example.carRental.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public User getUserById(int id) {
		return userRepository.findById(id).orElse(null);
	}

	@Override
	public User authenticateUser(String username, String password) {
	    return userRepository.findByUsernameAndPassword(username, password);
	}

	@Override
	public User saveUser(User user) {
		return userRepository.save(user);
	}


}

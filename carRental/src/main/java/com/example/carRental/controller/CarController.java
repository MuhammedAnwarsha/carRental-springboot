package com.example.carRental.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.carRental.model.Car;
import com.example.carRental.model.User;
import com.example.carRental.service.CarService;
import com.example.carRental.service.UserService;

@RestController
@RequestMapping("/cars")
public class CarController {

	@Autowired
	private CarService carService;

	@Autowired
	private UserService userService;

	private boolean bookingRequestsAllowed = true;  

	@GetMapping("/{username}/{password}")
	public ResponseEntity<?> getAllCars(@PathVariable String username,
			@PathVariable String password) {
		User loggedInUser = userService.authenticateUser(username, password);
		if (loggedInUser != null) {
			List<Car> cars = carService.getAllCars();
			return ResponseEntity.ok(cars);
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid credentials");
		}
	}

	@PostMapping
	public ResponseEntity<?> saveCar(@RequestBody Car car, @RequestParam String username,
			@RequestParam String password) {
		User loggedInUser = userService.authenticateUser(username, password);
		if (isAdmin(loggedInUser)) {
			if (car.getRentalPrice() <= 0) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Rental price must be greater than 0");
			}
			carService.saveCar(car);
			return ResponseEntity.ok(car);
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body("Invalid credentials or only admins can add car details");
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteCar(@PathVariable int id, @RequestParam String username,
			@RequestParam String password) {
		User loggedInUser = userService.authenticateUser(username, password);
		if (isAdmin(loggedInUser)) {
			carService.deleteCar(id);
			return ResponseEntity.ok("Car deleted successfully");
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body("Invalid credentials or only admins can delete cars");
		}
	}

	@PostMapping("/booking")
	public ResponseEntity<String> bookCar(@RequestParam int carId, @RequestParam String username,
			@RequestParam String password,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
		User loggedInUser = userService.authenticateUser(username, password);
		if (loggedInUser != null) {
			if (!bookingRequestsAllowed) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Booking requests are currently not allowed");
			}
			notifyAdminAboutBooking(carId, loggedInUser, fromDate, toDate);
			return ResponseEntity.ok("Booking request sent to admin");
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only logged-in users can make bookings");
		}
	}

	@PostMapping("/booking/action")
	public ResponseEntity<String> handleBookingAction(@RequestParam int carId, @RequestParam boolean accept,
			@RequestParam String adminUsername, @RequestParam String adminPassword) {
		User adminUser = userService.authenticateUser(adminUsername, adminPassword);

		if (isAdmin(adminUser)) {

			boolean bookingAccepted = carService.updateBookingStatus(carId, accept);

			if (bookingAccepted) {

				notifyUserAboutBookingAction(carId, accept);
				return ResponseEntity.ok("Booking request " + (accept ? "accepted" : "rejected"));
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car with ID " + carId + " not found");
			}
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body("Invalid credentials or only admins can handle booking actions");
		}
	}

	private void notifyUserAboutBookingAction(int carId, boolean accepted) {

		String action = accepted ? "accepted" : "rejected";
		System.out.println("User, your booking request for car ID: " + carId + " has been " + action + " by the admin");
	}

	private boolean isAdmin(User user) {
		return user != null && user.isAdmin();
	}

	private void notifyAdminAboutBooking(int carId, User user, LocalDate fromDate, LocalDate toDate) {

		System.out.println("Admin, new booking request for car ID: " + carId + " from user: " + user.getUsername()
				+ " from date is " + fromDate + " to date is " + toDate);
	}
}

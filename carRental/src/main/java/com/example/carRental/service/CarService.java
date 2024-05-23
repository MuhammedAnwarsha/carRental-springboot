package com.example.carRental.service;

import com.example.carRental.model.Car;

import java.util.List;

public interface CarService {
	List<Car> getAllCars();


	Car saveCar(Car car);

	void deleteCar(int id);

	boolean updateBookingStatus(int carId, boolean accept);
}

package com.example.carRental.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.carRental.model.Car;
import com.example.carRental.repository.CarRepository;

@Service
public class CarServiceImpl implements CarService {

	@Autowired
	private CarRepository carRepository;

	@Override
	public List<Car> getAllCars() {
		return carRepository.findAll();
	}

	@Override
	public Car saveCar(Car car) {
		return carRepository.save(car);
	}

	@Override
	public void deleteCar(int id) {
		carRepository.deleteById(id);
	}
	@Override
	public boolean updateBookingStatus(int carId, boolean accept) {
	    Optional<Car> optionalCar = carRepository.findById(carId);

	    if (optionalCar.isPresent()) {
	        Car car = optionalCar.get();

	        if (accept) {
	           
	            car.setBooked(true);
	        } else {
	        
	            car.setBooked(false);
	        }

	        carRepository.save(car);
	        return true;
	    } else {
	        return false; 
	    }
	}

}

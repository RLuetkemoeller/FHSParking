package parkingarea;

import logging.Log;
import cars.CarType;
import cars.Cars;
import interfaces.IGate;

public class Gate implements IGate {
	Parkingarea myArea = null;
	String name = "Unknown";
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Gate(Parkingarea myArea) {
		this.myArea = myArea; 
	}
	
	/**
	 * Handles a car for drive in
	 * Checks if there is Free Space in Parkingarea otherwise 
	 * sends Error Message
	 */
	@Override
	public void driveIn(Cars car) {
		if (myArea.hasFreeParkingPlace(CarType.NORMAL) || (car.getType() == CarType.HANDICAP && myArea.hasFreeParkingPlace(car.getType()))) {
			myArea.decreaseFreeParkingLots();
			this.myArea.placeCar(car);
			Log.println(Thread.currentThread().getName() + " - Gate opens for drive in  Car: " + car + " Free Parkinglots: " + myArea.getFreeParkingLots() );
		} else {
			Log.println("No free Parkinglots !!!");
		}
		
	}

	/**
	 * Handles the drive out of a Random Car from Parkingarea
	 * Checks if there is a parked car in Parkingarea
	 * otherwise sends Error Message
	 */
	@Override
	public void driveOut(Cars car) {
		if (myArea.getFreeParkingLots() < myArea.getFreeMaxParkingLots()) {
			myArea.increaseFreeParkingLots();
			myArea.removeCar(car);
			Log.println(Thread.currentThread().getName() + " - Gate opens for drive out - Free Parkinglots: " + myArea.getFreeParkingLots() );
		}
	}

}

package interfaces;

import java.util.ArrayList;
import java.util.List;

import cars.CarType;
import cars.Cars;
import parkingarea.ParkingPlace;

public interface IParkingarea {
	/**
	 * Sets a Car on a Random Parkingplace in Parkingarea
	 * @param car
	 */
	public abstract void placeCar(Cars car);
	
	/**
	 * Removes a Car from a Random Parkingplace in Parkingarea
	 * @param car
	 */
	public abstract void removeCar(Cars car);
	
	/**
	 * Check if the Parkingplace has free Parkingslots
	 * @param type
	 * @return
	 */
	public abstract boolean hasFreeParkingPlace(CarType type);
	
	/**
	 * Search a free Parkingplace in List of Parkingarea for Cartype type
	 * @param type
	 * @return a Parkingplace that is free for CarType type
	 */
	public abstract ParkingPlace searchFreeParkingPlace(CarType type);

}

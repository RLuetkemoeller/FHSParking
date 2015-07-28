package parkingarea;

import cars.CarType;
import cars.Cars;

/**
 * Parkingpalce
 * @author rene
 * Has Space for a Car and functions to contorl the Parkingplace
 */
public class ParkingPlace {
	private Cars car = null;
	private CarType type = CarType.NORMAL;
	private boolean StellplatzFrei = true;
	int id=0;
	
	public ParkingPlace(int id){
		this.id = id;
	}
	public ParkingPlace(int id, CarType type){
		this.id = id;
		this.type = type;
	}
	
	public int getID(){
		return this.id;
	}
	
	public CarType getType(){return this.type;}
	public void setType(CarType type){
		this.type =type;
	}
	public boolean isFree(){return this.StellplatzFrei;}
	
	/**
	 * Puts a Car on this Parkingplace
	 * @param car
	 */
	public void placeCar(Cars car) {
		this.StellplatzFrei = false;
		this.car = car;
	}
	
	/**
	 * returns car that is placed on this Parkingplace
	 * @return
	 */
	public Cars getCar(){
		return this.car;

	}
	
	/**
	 * Removes Car from Parkingplace
	 * @return
	 */
	public Cars removeCar(){
		this.StellplatzFrei = true;
		Cars help = this.car;
		this.car = null;
		return help;

	}
}

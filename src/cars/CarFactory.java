package cars;

public class CarFactory {
	// Create a Car
	public Cars getCar(){
		return new Cars();
	}
	// Create a Car with kfz & type
	public Cars getCar(String kfz, CarType type){
		return new Cars(kfz,type);
	}
	
}

package cars;

import java.util.Random;

import interfaces.IGate;

public class Cars{
	
	private CarType type = CarType.NORMAL;
	private String Kennzeichen = "Unknown";
	
	/**
	 * 
	 * @return type of the Car
	 */
	public CarType getType() {
		return type;
	}

	/**
	 * 
	 * @param Enum CarType type sets the Cars type
	 * 
	 */
	public void setType(CarType type) {
		this.type = type;
	}

	/**
	 * Konstruktor for Cars
	 */
	public Cars() {
		
		Random r = new Random();
		// Zufällig ein Behindertenauto erstellen
		if (r.nextFloat() > 0.8f){ this.type = CarType.HANDICAP;}
		
	}
	
	/**
	 * Konstruktor for Cars
	 * @param String kfz sets the numbersign
	 * @param Cartype type Handicaped / Normal
	 */
	public Cars(String kfz, CarType type) {
		this.Kennzeichen = kfz;
		this.type = type;
	}

	/**
	 * 
	 * @return String of numbersign
	 */
	public String getKennzeichen() {
		return Kennzeichen;
	}

	/**
	 * Sets the Numbersign of the Car
	 * @param String kennzeichen
	 */
	public void setKennzeichen(String kennzeichen) {
		Kennzeichen = kennzeichen;
	}
	
	


}

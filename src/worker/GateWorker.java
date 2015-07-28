package worker;

import java.util.Random;

import javax.sql.rowset.spi.SyncResolver;

import logging.Log;
import cars.CarType;
import cars.Cars;
import parkingarea.Gate;
import parkingarea.Parkingarea;

public class GateWorker implements Runnable {
	public static Object lock = new Object();
	private static Boolean einfahrsperre = false;
	private static Boolean ausfahrsperre = false;
	private Boolean active = true;
	

	private Gate myGate = null;
	private Parkingarea myArea = null;
	
	/* KONSTRUKTOR */
	/* ------------------------------------------------------------- */
	
	public GateWorker(Gate myFirstGate, Parkingarea myArea){
		this.myArea = myArea;
		this.myGate = myFirstGate;
	}
	
	/* MAIN LOOP FROM THREAD */
	/* ------------------------------------------------------------- */
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		this.active = true;
		
		while(true) {
			Random rand = new Random();
			if (this.active == false){
				try {
					synchronized (this) {
						this.wait();
						this.active = true;
					}
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			Cars car;
			
			try {
				Thread.sleep(rand.nextInt(4000));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (rand.nextFloat() > 0.3f) {
				// Fahrzeug Handling
				
				
				synchronized (myArea) {
					//Create new Car for drive in
					car = new Cars();
					if (this.einfahrsperre==false){
						myGate.driveIn(car);			
					} else {
						Log.println("Blocked car:" + car.getKennzeichen() + " reason: einfahrsperre active");
					}
				}
			} else {
				synchronized (myArea) {
					car = myArea.getRandomCar();
					if (this.ausfahrsperre==false){
						// Fahrzeug fährt raus
						myGate.driveOut(car);		
					} else {
						Log.println("Blocked car:" + car.getKennzeichen() + " reason: ausfahrsperre active");
					}				
				}
			}
		}
	}

	/* GETTERS UND SETTERS */
	/* ------------------------------------------------------------- */
	
	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
	
	public static Boolean getEinfahrsperre() {
		return einfahrsperre;
	}

	public static void setEinfahrsperre(Boolean einfahrsperre) {
		GateWorker.einfahrsperre = einfahrsperre;
	}

	public static Boolean getAusfahrsperre() {
		return ausfahrsperre;
	}

	public static void setAusfahrsperre(Boolean ausfahrsperre) {
		GateWorker.ausfahrsperre = ausfahrsperre;
	}

}

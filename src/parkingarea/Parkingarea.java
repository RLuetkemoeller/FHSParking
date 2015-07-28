package parkingarea;

import interfaces.ICars;
import interfaces.IParkingarea;

import java.io.Console;
import java.lang.Thread.State;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import worker.GateWorker;
import cars.CarType;
import cars.Cars;


/**
 * Main Class for Handling Parking Place
 * @author rene
 *
 */
public class Parkingarea implements IParkingarea {
	
	private ArrayList<Gate> myGates = null;
	private ArrayList<GateWorker> myGateWorker = null;
	private int maxParkingLots = 30;
	private int normalParkingLots = 26;
	private int HandicapParkingLots = 4;
	private int freeParkingLots = 30;
	private int gates = 3;
	
	// List for all Threads for GateWorkers
	private ArrayList<Thread> myThreads = null;
	
	// List for Parkingpalces
	private ArrayList<ParkingPlace> myParkingPlace = new ArrayList<ParkingPlace>(this.maxParkingLots);
	
	
	/**
	 * Initialise the Parkingarea with Threads, Gates and Gateworker etc.
	 * 
	 * @param NormalLots
	 * @param HandicapLots
	 * @param gates
	 */
	public void init(int NormalLots, int HandicapLots, int gates) {
		this.normalParkingLots = NormalLots;
		this.HandicapParkingLots = HandicapLots;
		this.maxParkingLots = this.normalParkingLots + this.HandicapParkingLots;
		this.freeParkingLots = this.maxParkingLots;
		this.gates = gates;		
		this.myGates = new ArrayList<Gate>();
		this.myGateWorker = new ArrayList<GateWorker>();
		this.myThreads = new ArrayList<Thread>();
		
		
		int i;
		for ( i=0;i<this.HandicapParkingLots;i++){
			this.myParkingPlace.add(new ParkingPlace(i+1, CarType.HANDICAP));			
		}
		
		for (;i<this.maxParkingLots;i++) {
			this.myParkingPlace.add(new ParkingPlace(i+1, CarType.NORMAL));			
		}
		
		for (i=0;i< this.gates;i++) {
			this.myGates.add(new Gate(this));	
			this.myGates.get(i).setName("Gate"+(i+1));
			this.myGateWorker.add(new GateWorker(this.myGates.get(i),this));
			this.myThreads.add(new Thread(this.myGateWorker.get(i), "Gate" + (i+1)));	
		}
	}
	
	/**
	 * Increase FreeParkingLotsCounter by 1
	 */
	public void increaseFreeParkingLots(){
		freeParkingLots++;
	}
	
	/**
	 * Decrease FreeParkinglotsCounter by 1
	 */
	public void decreaseFreeParkingLots(){
		freeParkingLots--;
	}
	
	/**
	 * Returns the number of free ParkingLots
	 * @return
	 */
	public int getFreeParkingLots() {
		return this.freeParkingLots;
	}
	
	/**
	 * Returns the Maximum ParkingLots
	 * @return
	 */
	public int getFreeMaxParkingLots() {
		return this.maxParkingLots;
	}
	
	/**
	 * Startingvalues for the Parkingplace 26 Normal Parkinglots, 4 Handicaped and 3 Gates
	 */
	public Parkingarea() {
		this.init(26, 4, 3);
	}

	/**
	 * Put Car on a Free ParkingSpace
	 */
	@Override
	public void placeCar(Cars car) {
		
		// Search for ParkingPlace in myParkingPlace
		ParkingPlace place = this.searchFreeParkingPlace(car.getType());
		if ( place == null && (car.getType() == CarType.HANDICAP)){
			place = this.searchFreeParkingPlace(CarType.NORMAL);
		}
		if (place.isFree() == false) {
			// throw exception
		}
		System.out.println("Auto: " + car + " auf Stellplatz :" + place.getID());
		place.placeCar(car);
	}

	@Override
	public void removeCar(Cars car) {
		boolean found = false;
		for (ParkingPlace place : this.myParkingPlace){
			if(place.getCar() == car){
				place.removeCar();
				found = true;
			}
		}
		if (found == false) {
			// throw exception
		}
	}

	/**
	 * Search for a Free Parkingplace in Parkingarea
	 */
	@Override
	public ParkingPlace searchFreeParkingPlace(CarType type) {
		Random r = new Random();
		ArrayList<ParkingPlace> help = new ArrayList<ParkingPlace>();
		for (ParkingPlace place : this.myParkingPlace) {			
			if (place.isFree() && place.getType() == type){
				help.add(place);
			}
		}
		if (help.size() > 0) {
		 return help.get(r.nextInt(help.size()));
		}
		
		return null;
	}

	/**
	 * Check if there are free Parkingplaces
	 */
	@Override
	public boolean hasFreeParkingPlace(CarType type) {
		for (ParkingPlace place : this.myParkingPlace) {			
			if (place.isFree() && place.getType() == type){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Get a Random Car from Parkingarea
	 * @return
	 */
	public Cars getRandomCar() {
		Random r = new Random();
		int x;
		while (this.freeParkingLots < this.maxParkingLots) {
			x = r.nextInt(this.maxParkingLots);
			if (this.myParkingPlace.get(x).isFree() == false) {
				return this.myParkingPlace.get(x).getCar();
			}
		}
		return null;
	}
	
	/**
	 * Main Function that starts all Gates(Threads)
	 */
	public void run() {
		for (Thread thread : this.myThreads) {
			thread.start();
		}	
	}
	
	/**
	 * Function for stopping a specific Gate
	 * @param gateNr
	 * @return
	 * @throws InterruptedException
	 */
	public int stopGate(int gateNr) throws InterruptedException{
		/*
		int i = 0;
		for (GateWorker gw : myGateWorker){
			i++;
			if (i == gateNr){
				gw.setActive(false);
				return 1;
			}
		}	
		*/
		
		
		int i = 0;
		for (Thread t : myThreads){
			i++;
			if (i==gateNr){
				synchronized (this) {
					t.suspend();
				}
				return 1;
			}
		}	
		
		return 0;
	}
	
	/**
	 * Functions for resuming a specific thread(Gate)
	 * @param gateNr
	 * @return
	 * @throws InterruptedException
	 */
	public int resumeGate(int gateNr) throws InterruptedException{
		int i = 0;
		for (Thread t : myThreads){
			if (t.getName().equals("Gate"+gateNr))				
				synchronized (this) {
					t.resume();
				}	
			return 1;
		}	
		return 0;
	}
	
	/**
	 * Stops all active Threads(GateWorker)
	 * @return
	 * @throws InterruptedException
	 */
	public int stopAllGates() throws InterruptedException{
		int i = 0;
		for (Thread t : myThreads){
			synchronized (this) {
				t.suspend();
			}
			i = 1;		
		}	
		return i;
	}
	
	/**
	 * Resumes all Threads(GatesWorker)
	 * @return
	 * @throws InterruptedException
	 */
	public int resumeAllGates() throws InterruptedException{
		int i = 0;
		for (Thread t : myThreads){
			synchronized (this) {
			t.resume();
			}
			i = 1;		
		}	
		return i;
	}
	
	/**
	 * Stops the Simulation(Parkingarea)
	 */
	@SuppressWarnings("deprecation")
	public void stop() {
		// TODO Auto-generated method stub
		for (Thread thread : this.myThreads) {
			 thread.stop();
		}
		
	}
	
}

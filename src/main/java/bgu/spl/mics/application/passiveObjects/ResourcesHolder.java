package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {
	private static ResourcesHolder instance = null;
	private ConcurrentHashMap<Integer, DeliveryVehicle> vehicleList;
	//private ConcurrentHashMap<DeliveryVehicle, Boolean> used;
	private DeliveryVehicle[] DeliveryVehicles;
	private Boolean[] used;
	private int num_of_vehicles;
	private  Semaphore available = new Semaphore(num_of_vehicles);

	private ResourcesHolder(){
		vehicleList= new ConcurrentHashMap<Integer, DeliveryVehicle>();
	}

	/**
     * Retrieves the single instance of this class.
     */
	public static ResourcesHolder getInstance() {
		if (instance == null) {
			instance = new ResourcesHolder();
		}
		return instance;
	}
	
	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public Future<DeliveryVehicle> acquireVehicle() throws InterruptedException {
		available.acquire();
		return getNextAvailableVehicle();
	}

	private synchronized Future<DeliveryVehicle> getNextAvailableVehicle() {
		Future<DeliveryVehicle> future = new Future<DeliveryVehicle>();
		for (int i = 0; i < num_of_vehicles; ++i) {
			if (!used[i]) {
				used[i] = true;
				future.resolve(DeliveryVehicles[i]);
				return future;
			}
		}

		return null; // not reached
	}
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {

		if (markAsUnused(vehicle)) {
			available.release();
		}
	}


	private synchronized boolean markAsUnused(DeliveryVehicle vehicle) {
		for (int i = 0; i < num_of_vehicles; ++i) {
			if (vehicle == DeliveryVehicles[i]) {
				if (used[i]) {
					used[i] = false;
					return true;
				} else
					return false;
			}
		}
		return false;
	}

	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		this.num_of_vehicles=vehicles.length;
		used= new Boolean[num_of_vehicles];
		DeliveryVehicles= new DeliveryVehicle[num_of_vehicles];
		for (int i = 0; i < vehicles.length; i++) {
			available.release();
			used[i] = false;
			DeliveryVehicles[i] = vehicles[i];
			vehicleList.put(vehicles[i].getLicense(), vehicles[i]);
		}
	}

}

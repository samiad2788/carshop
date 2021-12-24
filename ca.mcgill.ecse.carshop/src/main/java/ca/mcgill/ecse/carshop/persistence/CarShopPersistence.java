package ca.mcgill.ecse.carshop.persistence;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.ecse.carshop.application.CarShopApplication;
import ca.mcgill.ecse.carshop.model.BookableService;
import ca.mcgill.ecse.carshop.model.CarShop;
import ca.mcgill.ecse.carshop.model.User;

public class CarShopPersistence {
	
	private static String filename = "data.carshop";
	
	public static void save(CarShop carShop) {
		if(!CarShopApplication.getTesting()) {
			PersistenceObjectStream.setFilename(filename);
			PersistenceObjectStream.serialize(carShop);
		}
	}
	
	public static CarShop load() {
		PersistenceObjectStream.setFilename(filename);
		CarShop carShop = (CarShop) PersistenceObjectStream.deserialize();
		if(carShop == null) {
			carShop = new CarShop();
		}
		else {
			carShop.reinitialize();
		}
		
		return carShop;
	}
}

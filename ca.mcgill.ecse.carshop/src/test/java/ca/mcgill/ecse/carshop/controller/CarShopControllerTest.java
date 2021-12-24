
package ca.mcgill.ecse.carshop.controller;
import static org.junit.jupiter.api.Assertions.assertNotNull;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ca.mcgill.ecse.carshop.model.*;
import ca.mcgill.ecse.carshop.model.Technician.TechnicianType;
import ca.mcgill.ecse.carshop.application.CarShopApplication;

public class CarShopControllerTest {
  
  
  
  
  @BeforeEach
  public void setUp() {
    CarShop carShop = CarShopApplication.getCarShop();
    carShop.delete();
  }
  
  
  @Test
  public void testSetUpBusinessInfo () {
    // Background
    // Given a Carshop system exists
    CarShop carShop = CarShopApplication.getCarShop();
    // owner, customers and technicians
    Owner owner = new Owner("owner", "ownerPass", carShop);
    Customer customer1 = new Customer ("user1", "apple",0, carShop);
    Customer customer2 = new Customer ("user2", "grape",0, carShop);
    Technician technician1 = new Technician("Tire-Technician", "pass1", TechnicianType.Tire, carShop);
    Technician technician2 = new Technician("Engine-Technician", "pass2", TechnicianType.Engine, carShop);
    Technician technician3 = new Technician("Transmission-Technician", "pass3", TechnicianType.Transmission, carShop);
    Technician technician4 = new Technician("Electronics-Technician", "pass4", TechnicianType.Electronics, carShop);
    Technician technician5 = new Technician("Fluids-Technician", "pass5", TechnicianType.Fluids, carShop);
    carShop.setOwner(owner);
    carShop.addCustomer(customer1);
    carShop.addCustomer(customer2);
    carShop.addTechnician(technician1);
    carShop.addTechnician(technician2);
    carShop.addTechnician(technician3);
    carShop.addTechnician(technician4);
    carShop.addTechnician(technician5);
    // Given each technician has their own garage
    Garage garage1 = new Garage(carShop, technician1);
    Garage garage2 = new Garage(carShop, technician2);
    Garage garage3 = new Garage(carShop, technician3);
    Garage garage4 = new Garage(carShop, technician4);
    Garage garage5 = new Garage(carShop, technician5);
    technician1.setGarage(garage1);
    technician2.setGarage(garage2);
    technician3.setGarage(garage3);
    technician4.setGarage(garage4);
    technician5.setGarage(garage5);
    // Given no business exists 
    // no business was added, therefore no business exists
    // Given the system's time and date is "2021-02-01+11:00"
    Date today = Date.valueOf(LocalDate.of(2021, 2, 1));
    Time now = Time.valueOf(LocalTime.of(11, 0));
    
    // Scenario Outline: Set up basic business information
  }
  
  @Test
  public void testUpdateBusinessInfo () {
  }
}


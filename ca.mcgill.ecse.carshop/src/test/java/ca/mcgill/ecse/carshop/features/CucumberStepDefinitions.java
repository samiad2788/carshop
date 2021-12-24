package ca.mcgill.ecse.carshop.features;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ca.mcgill.ecse.carshop.application.CarShopApplication;
import ca.mcgill.ecse.carshop.controller.CarShopController;
import ca.mcgill.ecse.carshop.controller.InvalidInputException;
import ca.mcgill.ecse.carshop.controller.InvalidUserException;
import ca.mcgill.ecse.carshop.model.Appointment;
import ca.mcgill.ecse.carshop.model.Appointment.States;
import ca.mcgill.ecse.carshop.model.BookableService;
import ca.mcgill.ecse.carshop.model.Business;
import ca.mcgill.ecse.carshop.model.BusinessHour;
import ca.mcgill.ecse.carshop.model.CarShop;
import ca.mcgill.ecse.carshop.model.ComboItem;
import ca.mcgill.ecse.carshop.model.Customer;
import ca.mcgill.ecse.carshop.model.Garage;
import ca.mcgill.ecse.carshop.model.Owner;
import ca.mcgill.ecse.carshop.model.Service;
import ca.mcgill.ecse.carshop.model.ServiceBooking;
import ca.mcgill.ecse.carshop.model.ServiceCombo;
import ca.mcgill.ecse.carshop.model.Technician;
import ca.mcgill.ecse.carshop.model.Technician.TechnicianType;
import ca.mcgill.ecse.carshop.model.TimeSlot;
import ca.mcgill.ecse.carshop.model.User;
import ca.mcgill.ecse.carshop.model.BusinessHour.DayOfWeek;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CucumberStepDefinitions {
	private CarShop carshop;
	private String error;
	private int errorCnt;
	private String username;
	private String password;
	private String oldUsername;
	private String oldPassword;
	private int initialSize;
	private String oldServiceName;
	private List<String> businessInfo;
	private String endTime;
	private String startTime;
    private String day;
	private boolean res;
	private String oldServiceComboName;
	private int initialAppSize;
	
	//Step Definitions for Appointment Management
	@Given("{string} has {int} no-show records")
	public void setNoShow(String customer, int amount) {
		Customer cust = (Customer) getUserWithUsername(customer);
		cust.setNoShow(amount);
	}
	
	@When("{string} makes a {string} appointment for the date {string} and time {string} at {string}")
	public void makeAppointmentService(String customer, String serviceName, String dateStr, String timeStr, String currentStr) {
		this.setTimeAndDate(currentStr);
		
		Time startTime = convertToTime(timeStr);
		Date date = convertToDate(dateStr);
		
		this.logInUser(customer);
		
		try {
			CarShopController.makeAppointmentService(serviceName, date, startTime);
		} catch (InvalidInputException e) {
			error=e.getMessage();
			errorCnt++;
		}
	}
	
	@When("{string} attempts to update the date to {string} and time to {string} at {string}")
	public void updateServiceDateTime(String customer, String dateStr, String startStr, String currentStr) {
		this.updateDateTimes(customer, dateStr, startStr, currentStr);
	}
	
	@When("{string} attempts to update the date to {string} and start time to {string} at {string}")
	public void updateDateTimes(String customer, String dateStr, String startsStr, String currentStr) {
		this.setTimeAndDate(currentStr);
		
		Date d = convertToDate(dateStr);
		String[] starts = startsStr.split(",");
		List<Time> startTimes = new ArrayList<>();
		
		for(String start: starts) {
			startTimes.add(convertToTime(start));
		}
		
		Appointment app = CarShopController.getLastAddedAppointment();
		
		try {
			CarShopController.updateDateTimes(app, d, startTimes);
		} catch (InvalidInputException e) {
			error = e.getMessage();
			errorCnt++;
		}
	}
	
	@When("{string} makes a {string} appointment with service {string} for the date {string} and start time {string} at {string}")
	public void makeAppointmentCombo(String customer, String serviceName, String servicesStr, String dateStr, String timeStr, String currentStr) {
		this.setTimeAndDate(currentStr);
		
		Date date = convertToDate(dateStr);
		String[] times = timeStr.split(",");
		List<Time> startTimes = new ArrayList<>();
		for(String time: times) {
			startTimes.add(convertToTime(time));
		}
		
		this.logInUser(customer);
		
		try {
			CarShopController.makeAppointmentCombo(serviceName, Arrays.asList(servicesStr.split(",")), date, startTimes);
		}
		catch(InvalidInputException e) {
			error = e.getMessage();
			errorCnt++;
		}
	}
	
	@When("{string} attempts to change the service in the appointment to {string} at {string}")
	public void changeMainService(String customer, String serviceName, String currentStr) throws InvalidInputException {
		this.setTimeAndDate(currentStr);
		
		Appointment app = CarShopController.getLastAddedAppointment();
		if(app == null) {
			throw new InvalidInputException("No Appointment to modify");
		}
		
		CarShopController.setMainService(app, serviceName);
	}
	
	@When("the owner starts the appointment at {string}")
	public void startAppointment(String currentStr) {
		this.setTimeAndDate(currentStr);
		
		Appointment app = CarShopController.getLastAddedAppointment();
		if(app == null) {
			System.out.println("It is null");
		}
		
		CarShopController.startAppointment(app);
	}
	
	@When("the owner ends the appointment at {string}") 
	public void endAppointment(String currentStr) {
		this.setTimeAndDate(currentStr);
		
		Appointment app = CarShopController.getLastAddedAppointment();
		CarShopController.endAppointment(app);
	}
	@When("the owner attempts to end the appointment at {string}")
	public void attemptEndApp(String currentStr) {
		this.endAppointment(currentStr);
	}
	
	@When("the owner attempts to register a no-show for the appointment at {string}")
	public void registerNoShow(String currentStr) {
		this.setTimeAndDate(currentStr);
		Appointment app = CarShopController.getLastAddedAppointment();
		CarShopController.registerNoShow(app);
	}
	
	@When("{string} attempts to add the optional service {string} to the service combo with start time {string} in the appointment at {string}")
	public void addOptionalService(String customer, String addedService, String timeStr, String currentStr) {
		this.setTimeAndDate(currentStr);
		
		Appointment app = CarShopController.getLastAddedAppointment();
		Time startTime = convertToTime(timeStr);
		try {
			CarShopController.addOptionalService(app, addedService, startTime);
		} catch (InvalidInputException e) {
			error = e.getMessage();
			errorCnt++;
		}
	}
	@When("{string} attempts to cancel the appointment at {string}")
	public void customerCancelApp(String customer, String currentStr) {
		this.setTimeAndDate(currentStr);
		
		Appointment app = CarShopController.getLastAddedAppointment();
		if(app != null) {
			try {
				CarShopController.customerCancelApp(app);
			} catch (InvalidInputException e) {
				error = e.getMessage();
				errorCnt++;
			}
		}
	}
	
	@Then("the appointment shall be booked") 
	public void appointmentBooked() {
		Appointment app = CarShopController.getLastAddedAppointment();
		assertNotNull(app);
	
		States state = app.getStates();
		assertEquals(state, States.Booking);
	}
	
	@Then("the appointment shall be in progress")
	public void appointmentInProgress() {
		Appointment app = CarShopController.getLastAddedAppointment();
		assertNotNull(app);
		
		States state = app.getStates();
		assertEquals(state, States.AppointmentInProgress);
	}
	
	@Then("the service combo shall have {string} selected services")
	public void checkSelectedServices(String servicesStr) {
		String[] services = servicesStr.split(",");
		Appointment app = CarShopController.getLastAddedAppointment();
		
		assertNotNull(app);
		assertTrue(app.getBookableService() instanceof ServiceCombo);
	
		List<ServiceBooking> bookings = app.getServiceBookings();
		List<String> bookedServices = new ArrayList<>();
		for(ServiceBooking booking: bookings) {
			System.out.println(booking.getService().getName());
			bookedServices.add(booking.getService().getName());
		}
		
		int counter = 0;
		for(String service: services) {
			if(bookedServices.contains(service)) {
				counter++;
			}
		}
		
		assertEquals(services.length, counter);
	}
	
	@Then("the system shall have {int} (appointment|appointments)")
	public void checkNumbAppointments(int number) {
		assertEquals(carshop.getAppointments().size(), number);
	}
	
	@Then("the (service|service combo) in the appointment shall be {string}")
	public void checkServiceAppName(String name) {
		Appointment app = CarShopController.getLastAddedAppointment();
		assertNotNull(app);
		
		assertEquals(app.getBookableService().getName(), name);
	}
	
	@Then("the appointment shall be for the date {string} with start time {string} and end time {string}")
	public void checkCorrectTimes(String dateStr, String startStr, String endStr) {
		Appointment app = CarShopController.getLastAddedAppointment();
		assertNotNull(app);
		
		String[] startTimesStr = startStr.split(",");
		String[] endTimesStr = endStr.split(",");
		
		List<Time> startTimes = new ArrayList<>();
		List<Time> endTimes = new ArrayList<>();
		
		Date date = convertToDate(dateStr);
		
		for(int i = 0; i < startTimesStr.length; i++) {
			startTimes.add(convertToTime(startTimesStr[i]));
			endTimes.add(convertToTime(endTimesStr[i]));
		}
		
		for(int j = 0; j < app.getServiceBookings().size(); j++) {
			TimeSlot slot = app.getServiceBooking(j).getTimeSlot();
			assertEquals(slot.getStartDate(), date);
			assertEquals(slot.getEndDate(), date);
			
			assertEquals(slot.getStartTime(), startTimes.get(j));
			assertEquals(slot.getEndTime(), endTimes.get(j));
		}
	}
	
	@Then("the username associated with the appointment shall be {string}")
	public void checkCustomerName(String name) {
		Appointment app = CarShopController.getLastAddedAppointment();
		assertNotNull(app);
		
		assertEquals(app.getCustomer().getUsername(), name);
	}

	@Then("the user {string} shall have {int} no-show records")
	public void checkNoShow(String customer, int amount) {
		User u = getUserWithUsername(customer);
		
		assertTrue(u instanceof Customer);
		assertNotNull(u);
		
		Customer c = (Customer) u;
		assertEquals(c.getNoShow(), amount);
	}
	
	//Step Definitions for Appointments Handling
	@Given("{string} is logged in to their account")
	public void logInUser(String username) {
		try {
			User u = getUserWithUsername(username);
			CarShopController.logIn(u.getUsername(), u.getPassword());
		}
		catch(InvalidInputException e) {
			error=e.getMessage();
			errorCnt++;
		}
	}
	
	@Given("the system's time and date is {string}") 
	public void setTimeAndDate(String str) {
		String[] split1 = str.split("\\+");
		String[] dateSplit = split1[0].split("-");
		String[] timeSplit = split1[1].split(":");
		
	    Date d = Date.valueOf(LocalDate.of(Integer.valueOf(dateSplit[0]), Integer.valueOf(dateSplit[1]), Integer.valueOf(dateSplit[2])));
	    Time t = Time.valueOf(LocalTime.of(Integer.valueOf(timeSplit[0]), Integer.valueOf(timeSplit[1])));
	    CarShopController.setToday(d);
	    CarShopController.setTime(t);
	}
	
	@Given("the business has the following opening hours")
	public void businessOpeningHours(DataTable dataTable) {
		List<Map<String, String>> maps = dataTable.asMaps();
		for(Map<String, String> map: maps) {
			DayOfWeek day = DayOfWeek.valueOf(map.get("day"));
			Time startTime = convertToTime(map.get("startTime"));
			Time endTime = convertToTime(map.get("endTime"));
			
			BusinessHour hour = new BusinessHour(day, startTime, endTime, carshop);
			carshop.getBusiness().addBusinessHour(hour);
		}
	}
	@Given("all garages has the following opening hours")
	public void garageOpeningHours(DataTable dataTable) {
		List<Map<String, String>> maps = dataTable.asMaps();
		for(Map<String, String> map: maps) {
			DayOfWeek day = DayOfWeek.valueOf(map.get("day"));
			Time startTime = convertToTime(map.get("startTime"));
			Time endTime = convertToTime(map.get("endTime"));
			
			for(Garage g: carshop.getGarages()) {
				BusinessHour hour = new BusinessHour(day, startTime, endTime, carshop);
				g.addBusinessHour(hour);
			}
		}
	}
	
	@Given("the business has the following holidays")
	public void setHolidays(DataTable dataTable) {
		List<Map<String, String>> maps = dataTable.asMaps();
		for(Map<String, String> map: maps) {
			Date startDate = convertToDate(map.get("startDate"));
			Date endDate = convertToDate(map.get("endDate"));
			
			Time startTime = convertToTime(map.get("startTime"));
			Time endTime = convertToTime(map.get("endTime"));
			
			carshop.getBusiness().addHoliday(new TimeSlot(startDate, startTime, endDate, endTime, carshop));
			
		}
	}
	@Given("the following appointments exist in the system:")
	public void existingAppointments(DataTable dataTable) {
		List<Map<String, String>> maps = dataTable.asMaps();
		for(Map<String, String> map: maps) {
			Customer cust = (Customer) getUserWithUsername(map.get("customer"));
			ServiceCombo bookable = (ServiceCombo) getBookableFromName(map.get("serviceName"));
		
			List<Service> services = new ArrayList<>();
			services.add(bookable.getMainService().getService());
			
			List<String> optionalServices = Arrays.asList(map.get("optServices").split(","));
			for(String s: optionalServices) {
				services.add(this.getComboItemFromServiceName(bookable, s).getService());
			}
			
			String[] timeframes = map.get("timeSlots").split(",");
			
			Date date = convertToDate(map.get("date"));
			
			Appointment appointment = new Appointment(cust, bookable, carshop);
			for(int i = 0; i < timeframes.length; i++) {
				String[] times = timeframes[i].split("-");
				Time startTime = convertToTime(times[0]);
				Time endTime = convertToTime(times[1]);
				
				appointment.addServiceBooking(services.get(i), new TimeSlot(date, startTime, date, endTime, carshop));
			}
		}
	}
	
	@When("{string} schedules an appointment on {string} for {string} at {string}")
	public void makeAppointmentService(String customer, String date, String servicename, String startTime) {	
		try {
			this.initialAppSize = carshop.getAppointments().size();
			
			CarShopController.makeAppointmentService(servicename, convertToDate(date), convertToTime(startTime));
		}
		catch(InvalidInputException ex) {
			error += ex.getMessage();
	        errorCnt++;	
		}
	}
	@When("{string} schedules an appointment on {string} for {string} with {string} at {string}")
	public void makeAppointmentCombo(String customer, String dateStr, String bookableName, String optionalServices, String startTimes) {
		try {
			this.initialAppSize = carshop.getAppointments().size();
			
			Date date = convertToDate(dateStr);
			List<String> optServices = Arrays.asList(optionalServices.split(","));
			List<Time> startT = new ArrayList<>();
			for(String time: startTimes.split(",")) {
				startT.add(convertToTime(time));
			}
			
			CarShopController.makeAppointmentCombo(bookableName, optServices, date, startT);
		}
		catch(InvalidInputException ex) {
			error += ex.getMessage();
			errorCnt++;
		}
	}
	@When("{string} attempts to cancel their {string} appointment on {string} at {string}")
	public void cancelAppointment(String customerStr, String serviceName, String dateStr, String timeStr) {
		try {
			this.initialAppSize = carshop.getAppointments().size();
			
			CarShopController.cancelAppointment(serviceName, convertToDate(dateStr), convertToTime(timeStr));
		}
		catch(InvalidInputException ex) {
			error += ex.getMessage();
			errorCnt++;
			System.out.println(ex.getMessage());
		}
	}
	@When("{string} attempts to cancel {string}'s {string} appointment on {string} at {string}")
	public void cancelAnotherAppointment(String loggedInUser, String apppointmentUser, String serviceName, String dateStr, String timeStr) {
		try {
			this.initialAppSize = carshop.getAppointments().size();
			
			CarShopController.cancelAppointment(serviceName, convertToDate(dateStr), convertToTime(timeStr));
		}
		catch(InvalidInputException ex) {
			error += ex.getMessage();
			errorCnt++;
		}
	}
	
	@Then("{string}'s {string} appointment on {string} at {string} shall be removed from the system")
	public void appointmentRemoved(String customer, String serviceName, String dateStr, String timeStr) {
		Customer cust = (Customer) getUserWithUsername(customer);
		assertNotNull(cust);
		
		BookableService bookable = getBookableFromName(serviceName);
		assertNotNull(bookable);
		
		assertNull(getAppointment(cust, bookable, convertToDate(dateStr), Arrays.asList(new Time[] {convertToTime(timeStr)})));
	}
	@Then("{string} shall have a {string} appointment on {string} at {string} with the following properties")
	public void checkAppointmentProperties(String customer, String serviceName, String dateStr, String timeStr, DataTable dataTable) {
		Customer cust = (Customer) getUserWithUsername(customer);
		assertNotNull(cust);
		
		List<Map<String, String>> maps = dataTable.asMaps();
		for(Map<String, String> map: maps) {
			BookableService bookable = getBookableFromName(map.get("serviceName"));
			assertNotNull(bookable);
			
			Date date = convertToDate(map.get("date"));
			assertNotNull(date);
			
			String[] timeslots = map.get("timeSlots").split(",");
			List<Time> startTimes = new ArrayList<>();
			for(String timeslot: timeslots) {
				startTimes.add(convertToTime(timeslot.split("-")[0]));
			}
			
			assertNotNull(getAppointment(cust, bookable, date, startTimes));
		}
	}
	
	@Then("{string} shall have a {string} appointment on {string} from {string} to {string}")
	public void checkServiceAppointment(String customer, String serviceName, String date, String startTime, String endTime) {		
		Customer cust = (Customer) getUserWithUsername(customer);
		assertNotNull(cust);
		
		BookableService bookable = getBookableFromName(serviceName);
		assertNotNull(bookable);
		
		Date d = convertToDate(date);
		
		List<Time> startT = new ArrayList<>();
		for(String time: startTime.split(",")) {
			startT.add(convertToTime(time));
		}
		
		List<Time> endT = new ArrayList<>();
		for(String time: endTime.split(",")) {
			endT.add(convertToTime(time));
		}
		
		assertNotNull(getAppointment(cust, bookable, d, startT));
	}
	
	@Then("there shall be {int} more appointment in the system")
	public void checkAdditionalAppointments(int addition) {
		assertEquals(this.initialAppSize+addition, carshop.getAppointments().size());
	}
	@Then("there shall be {int} less appointment in the system")
	public void checkLessAppointments(int subtraction) {
		assertEquals(this.initialAppSize - subtraction, carshop.getAppointments().size());
	}
	
	@Then("the system shall report {string}")
	public void checkError(String errorMessage) {
		assertNotNull(this.error);
		assertEquals(this.error, errorMessage);
	}
	
	private BookableService getBookableFromName(String name) {
		for(BookableService bookable: carshop.getBookableServices()) {
			if(bookable.getName().equals(name)) {
				return bookable;
			}
		}
		
		return null;
	}
	private ComboItem getComboItemFromServiceName(ServiceCombo combo, String name) {
		for(ComboItem item: combo.getServices()) {
			if(item.getService().getName().equals(name)) {
				return item;
			}
		}
		  
		return null;
	}
	
	private Appointment getAppointment(Customer cust, BookableService bookable, Date d, List<Time> startTimes) {
		for(Appointment appointment: cust.getAppointments()) {
			if(appointment.getBookableService().equals(bookable)) {
				if(appointment.getServiceBookings().size() == startTimes.size()) {
					int count = 0;
					
					for(int i = 0; i < startTimes.size(); i++) {
						ServiceBooking booking = appointment.getServiceBooking(i);
						if(booking.getTimeSlot().getStartDate().equals(d) && booking.getTimeSlot().getStartTime().equals(startTimes.get(i))) {
							count += 1;
						}
					}
					
					if(count == appointment.getServiceBookings().size()) {
						return appointment;
					}
				}
			}
		}
		
		return null;
	}
	
	
	//End of Step Definitions for appointments handling.
	
	// Step Definitions for UpdateGarageOpeningHours. Written by Hadi Ghaddar
	@Given("a business exists with the following information:")
	// adds a business
	public void a_business_exists_with_the_following_information(io.cucumber.datatable.DataTable dataTable) {
		List<Map<String, String>> listRepresentation = dataTable.asMaps(String.class, String.class);
		for(Map<String, String> list: listRepresentation) {
			String name = list.get("name");
			String address = list.get("address");
			String phoneNumber = list.get("phone number");
			String email = list.get("email");
						
			Business business = new Business(name, address, phoneNumber, email, carshop);

		}
	}

	@Given("the business has the following opening hours:")
	//adds opening hours to the business
	public void the_business_has_the_following_opening_hours(io.cucumber.datatable.DataTable dataTable) {
		List<Map<String, String>> listRepresentation = dataTable.asMaps(String.class, String.class);
		for(Map<String, String> list: listRepresentation) {;
			Time sTime = convertToTime(list.get("startTime"));
		    Time eTime = convertToTime(list.get("endTime"));
			DayOfWeek dayOfWeek = DayOfWeek.valueOf(list.get("day"));
			
			Business business = carshop.getBusiness();
			business.addBusinessHour(new BusinessHour(dayOfWeek, sTime, eTime, carshop));
		}
	}

	@When("the user tries to add new business hours on {string} from {string} to {string} to garage belonging to the technician with type {string}")
	//when user tries to add business hours for a certain garage, we make sure the user is the appropriate technician
	public void the_user_tries_to_add_new_business_hours_on_from_to_to_garage_belonging_to_the_technician_with_type(String day, String startTime, String endTime, String type) {
        Time sTime = convertToTime(startTime);
        Time eTime = convertToTime(endTime);
        DayOfWeek dayOfWeek = DayOfWeek.valueOf(day);
        TechnicianType techType = getTechnicianTypeFromString(type);
	    try {
	    	
		    CarShopController.updateGarageOpeningHours(dayOfWeek, sTime, eTime, techType);
	    } 
	    catch (InvalidInputException e) {
	        error += e.getMessage();
	        errorCnt++;			
	    }
	}
	        
	@Then("the garage belonging to the technician with type {string} should have opening hours on {string} from {string} to {string}")
	//makes sure the garage has the added opening hours
	public void the_garage_belonging_to_the_technician_with_type_should_have_opening_hours_on_from_to(String string, String string2, String string3, String string4) {
	    Garage g = getGarageOfTechnician(getTechnicianTypeFromString(string));
	    assertNotNull(g.getBusinessHours());
	    
	    
	    
	    boolean found = false;
	    for(BusinessHour hours: g.getBusinessHours()) {
	    	if(hours.getDayOfWeek() == DayOfWeek.valueOf(string2)) {
	    		if(hours.getStartTime().equals(convertToTime(string3)) && hours.getEndTime().equals(convertToTime(string4))) {
	    			found = true;
	    		}
	    	}
	    }
	    assertTrue(found);
	}

	@Given("there are opening hours on {string} from {string} to {string} for garage belonging to the technician with type {string}")
	//adds opening hours to the garage belonging to the approprate technician
	public void there_are_opening_hours_on_from_to_for_garage_belonging_to_the_technician_with_type(String string, String string2, String string3, String string4) {
		Garage g = getGarageOfTechnician(getTechnicianTypeFromString(string4));
		Time startTime = convertToTime(string2);
		Time endTime = convertToTime(string3);
		DayOfWeek day = DayOfWeek.valueOf(string);
	  
		g.addBusinessHour(new BusinessHour(day, startTime, endTime, carshop));
	}

	@When("the user tries to remove opening hours on {string} from {string} to {string} to garage belonging to the technician with type {string}")
	//when user tries to remove business hours for a certain garage, we make sure the user is the appropriate technician
	public void the_user_tries_to_remove_opening_hours_on_from_to_to_garage_belonging_to_the_technician_with_type(String string, String string2, String string3, String string4) {
	    DayOfWeek dayOfWeek = DayOfWeek.valueOf(string);
	    Time startTime = convertToTime(string2);
	    Time endTime = convertToTime(string3);
	    TechnicianType techType = getTechnicianTypeFromString(string4);
	    
	    try {
		    CarShopController.removeGarageBusinessHours(dayOfWeek, startTime, endTime, techType);
	    } 
	    catch (InvalidInputException e) {
	        error += e.getMessage();
	        errorCnt++;			
	    }
	    
	}

	@Then("the garage belonging to the technician with type {string} should not have opening hours on {string} from {string} to {string}")
	//makes sure the garage does not have the added opening hours
	public void the_garage_belonging_to_the_technician_with_type_should_not_have_opening_hours_on_from_to(String string, String string2, String string3, String string4) {
	    Garage g = getGarageOfTechnician(getTechnicianTypeFromString(string));
	    assertNotNull(g.getBusinessHours());
	    
	    
	    boolean found = false;
	    for(BusinessHour hours: g.getBusinessHours()) {
	    	if(hours.getDayOfWeek() == DayOfWeek.valueOf(string2)) {
	    		if(hours.getStartTime().equals(convertToTime(string3)) && hours.getEndTime().equals(convertToTime(string4))) {
	    			found = true;
	    		}
	    	}
	    }
	    assertTrue(!found);
	}
	// End of the UpdateGarageOpeningHours
	// Step Definitions for LogIn. Written by Hadi Ghaddar
	
	@When("the user tries to log in with username {string} and password {string}")
	//checks if the user enters the correct username/password
	public void the_user_tries_to_log_in_with_username_and_password(String string, String string2) {
		username=string;
		password=string2;
		initialSize=getCountOfUsers();
		
		try{
	     	 CarShopController.logIn(username,password);
	     }
	     catch(InvalidInputException e){
	    	 error=e.getMessage();
		 	 errorCnt++;
	     }
	}

	@Then("the user should be successfully logged in")
	//checks that the user is logged in
	public void the_user_should_be_successfully_logged_in() {
		assertNotNull(CarShopController.getLoggedInUser());
		assertEquals(username,CarShopController.getLoggedInUser().getUsername());
		assertEquals(password,CarShopController.getLoggedInUser().getPassword());
	}
	@Then("the user should not be logged in")
	//checks that the user is not logged in
	public void the_user_should_not_be_logged_in() {
		assertNull(CarShopController.getLoggedInUser());
	}
	@Then("a new account shall be created")
	public void a_new_account_shall_be_created() {
	//checks that a new account is created
		assertEquals(getCountOfUsers(),initialSize+1);
	}

	@Then("the user shall be successfully logged in")
	//checks that the user is logged in
	public void the_user_shall_be_successfully_logged_in() {
		assertNotNull(CarShopController.getLoggedInUser());
		assertEquals(username,CarShopController.getLoggedInUser().getUsername());
		assertEquals(password,CarShopController.getLoggedInUser().getPassword());
	}

	@Then("the account shall have username {string}, password {string} and technician type {string}")
	//technician user should have a username, password and technician type
	public void the_account_shall_have_username_password_and_technician_type(String username, String password, String type) {
	    if(getUserWithUsername(username)!=null) {
			assertNotNull(CarShopController.getLoggedInUser());
			assertEquals(username,CarShopController.getLoggedInUser().getUsername());
			assertEquals(password,CarShopController.getLoggedInUser().getPassword());
			User u = CarShopController.getLoggedInUser();
			assertTrue(u instanceof Technician);
			Technician tech = (Technician) u;
			assertEquals(tech.getType(), getTechnicianTypeFromString(type));
	    }    
	    else {
	    	throw new AssertionError();
	    }
	}

	@Then("the corresponding garage for the technician shall be created")
	//makes sure the corresponding garage for the technician is created
	public void the_corresponding_garage_for_the_technician_shall_be_created() {
		assertNotNull(CarShopController.getLoggedInUser());
		User u = CarShopController.getLoggedInUser();
		assertTrue(u instanceof Technician);
		Technician tech = (Technician) u;
		assertNotNull(tech.getGarage());
	}

	@Then("the garage should have the same opening hours as the business")
	//make sure the garage has the same opening hours as the business
	public void the_garage_should_have_the_same_opening_hours_as_the_business() {
		User u = CarShopController.getLoggedInUser();
		assertTrue(u instanceof Technician);
		
		Technician tech = (Technician) u;
		Garage g = tech.getGarage();
		if(carshop.getBusiness() == null) {
			 assertTrue(g.getBusinessHours().size() == 0);
		}
		else {
			for(DayOfWeek dayOfWeek: DayOfWeek.values()) {
				List<BusinessHour> hoursOfShop = getBusinessHoursOfShopByDay(dayOfWeek);
				if(hoursOfShop.size() != 0) {
					for(BusinessHour bHour: hoursOfShop) {
						List<BusinessHour> hoursOfGarage = getBussinessHoursOfDayByGarage(g, dayOfWeek);
						boolean found = false;
						for(BusinessHour bHourGarage: hoursOfGarage) {
							if(bHour.getStartTime().equals(bHourGarage.getStartTime()) && bHour.getEndTime().equals(bHourGarage.getEndTime())) {
								found = true;
							}
						}
						if(!found) 
							fail();
					}
				}
			}
		}
	}
	
	//End of the LogIn code
	
	
	//This is the CucumberStepDefinitions code for signUpCustomer. Coded by Sami Ait Ouahmane
	
	//We create a new instance of carShop 
	@Given("a Carshop system exists")
	public void a_carshop_system_exists() {
		carshop = CarShopApplication.resetForTesting();
		
		error = "";
		errorCnt = 0;
	}
	
	//We first log out of an account if we were logged in so that no error is thrown when we try to access
	//methods that can't be used when signed up as an owner or technician (like signUpCustomerAccount)
	//We delete the instance that has the username string
	//
	@Given("there is no existing username {string}")
	public void there_is_no_existing_username(String string) {
		CarShopController.logOut();
		User i=getUserWithUsername(string);
		if(i!=null) {
			i.delete();
		}
	}
	//We sign up for customer account account and save the username, password, and
	//size of customer list in two global variables for later on
	@When("the user provides a new username {string} and a password {string}")
	public void the_user_provides_a_new_username_and_a_password(String string, String string2) {
	    username=string;
	    password=string2;
	    initialSize=carshop.getCustomers().size();
	     try{
	     	 CarShopController.signUpCustomerAccount(username,password);
	     }
	     catch(InvalidInputException e){
	    	 error=e.getMessage();
		 	 errorCnt++;
	     }
	}
	
	//If a new customer account is created, the size of the customer list should increase by 1
	//We make sure that an account with this username exists
	@Then("a new customer account shall be created")
	public void a_new_customer_account_shall_be_created() {
		assertEquals(initialSize+1, carshop.getCustomers().size());
		assertNotNull(getUserWithUsername(username));	
	}

	//If the user with a certain username exists, make sure that his password matches the given password
	@Then("the account shall have username {string} and password {string}")
	public void the_account_shall_have_username_and_password(String string, String string2) {
	    if(getUserWithUsername(string)!=null) {
	    	assertEquals(string2,getUserWithUsername(string).getPassword());
	    }
	    else {
	    	throw new AssertionError();
	    }
	    
	}

	//If not account is created then initial size of customer list should remain the same
	@Then("no new account shall be created")
	public void no_new_account_shall_be_created() {
		assertEquals(initialSize, carshop.getCustomers().size());
	}

	//raise an error message
	@Then("an error message {string} shall be raised")
	public void an_error_message_shall_be_raised(String string) {
	    assertEquals(this.error,string);
	    error="";
	}

	//We make sure that User with username string exists
	@Given("there is an existing username {string}")
	public void there_is_an_existing_username(String string) {
		CarShopController.logOut();
	    if(getUserWithUsername(string)==null) {
	    	if(string.equals("owner")) {
	    		carshop.setOwner(new Owner(string,string,carshop));
	    	}
	    	else if(string.equals("Tire-Technician") || string.equals("Engine-Technician") || string.equals("Fluids-Technician") || string.equals("Electronics-Technician") || string.equals("Transmission-Technician")) {
	    		carshop.addTechnician(string,string,getTechTypeFromUsername(string));
	    	}
	    	else {
	    		try {
	    			CarShopController.signUpCustomerAccount(string, string);
	    		}
	    		catch(InvalidInputException e) {
	    			error=e.getMessage();
	    			errorCnt++;
	    		}
	    	}
	    }
	}

	//We make sure that a user is logged in with an account with username string
	@Given("the user is logged in to an account with username {string}")
	public void the_user_is_logged_in_to_an_account_with_username(String string) {
		try {
			User u = getUserWithUsername(string);
			CarShopController.logIn(u.getUsername(), u.getPassword());
		}
		catch(InvalidInputException e) {
			error=e.getMessage();
			errorCnt++;
		}
	    
	}

	// end of sign up code coded by Sami Ait Ouahmane
	
	//Start of Update account coded by Sami Ait Ouahmane
	
	//If an owner exists, we set his password to string2. Otherwise, we set an owner t our carshop system 
	@Given("an owner account exists in the system with username {string} and password {string}")
	public void an_owner_account_exists_in_the_system_with_username_and_password(String string, String string2) {
	    if(getUserWithUsername(string)!=null) {
	    	getUserWithUsername(string).setPassword(string2);
	    }
	    else {
	    	carshop.setOwner(new Owner(string,string2,carshop));
	    }
	}
	
	//We update the username with new username string and password string2
	@When("the user tries to update account with a new username {string} and password {string}")
	public void the_user_tries_to_update_account_with_a_new_username_and_password(String string, String string2) {
		
		username=string;
		password=string2;
		
		if(CarShopController.getLoggedInUser()!=null) {
			oldUsername=CarShopController.getLoggedInUser().getUsername();
			oldPassword=CarShopController.getLoggedInUser().getPassword();
		}
		try {
			CarShopController.updateCustomerAccount(string, string2);
		}
	    catch(InvalidInputException e) {
	    	error=e.getMessage();
	    	errorCnt++;
	    }
	}
	
	
	//We make sure that the account hasn't been updated
	@Then("the account shall not be updated")
	public void the_account_shall_not_be_updated() {
		assertEquals(CarShopController.getLoggedInUser().getUsername(),oldUsername);
	    assertEquals(CarShopController.getLoggedInUser().getPassword(),oldPassword);
	}
	
	//End of update account coded by Sami Ait Ouahmane
	
	//Start of code written by Mario Bouzakhm
	
	@Given("an owner account exists in the system")
	public void thereIsAnOwner()  {
		if(getUserWithUsername("owner") == null) {
			Owner owner = new Owner("owner", "password", this.carshop);
		}
	}
	
	@Given("a business exists in the system")
	public void thereIsABusiness() {
		Business business = new Business("Car Shop", "McGill", "mario.bouzakhm@mail.mcgill.ca", "(514) 123-1342", this.carshop);
	}
	
	@Given("the following technicians exist in the system:")
	public void thereIsTechnicians(DataTable dataTable) {
		List<Map<String, String>> listReresentation = dataTable.asMaps(String.class, String.class);
		//Converts the Datatable to a List of Maps and then adds the technicians one by one to the system.
		for(Map<String, String> list: listReresentation) {
			String username = list.get("username");
			String password = list.get("password");
			String type = list.get("type");
			
			TechnicianType techType = getTechnicianTypeFromString(type);
			
			Technician technician = new Technician(username, password, techType, this.carshop);
		}
	}
	
	@Given("each technician has their own garage")
	public   void eachTechnicianHasGarage() {
		//Makes sure to create a corresponding garage for each technician.
		for(Technician tech: this.carshop.getTechnicians()) {
			if(tech.getGarage() == null) {
				Garage garage = new Garage(carshop, tech);
			}
		}
	}
	
	@Given("the following services exist in the system:")
	public void exisitingServiceInSystem(DataTable dataTable) {
		List<Map<String, String>> listReresentation = dataTable.asMaps(String.class, String.class);
		//Convert the datatable to a List of Maps and add the services mentioned to the system.
		for(Map<String, String> list: listReresentation) {
			String username = list.get("name");
			int duration = Integer.valueOf(list.get("duration"));
			
			Garage g = getGarageOfTechnician(getTechnicianTypeFromString(list.get("garage")));
			Service service = new Service(username, carshop, Integer.valueOf(duration), g);	
		}
	}
	
	@Given("the Owner with username {string} is logged in")
	public void ownerWithUsernameLoggedIn(String username) {
		try {
			CarShopController.logIn(username, "password");
		} catch (InvalidInputException e) {
			error = e.getMessage();
			errorCnt++;
		}
	}
	@Given("the user with username {string} is logged in")
	public void userLoggedIn(String username) {
		//Makes sure that the user with username is logged into the system (can be customer, technician or Owner).
		try {
			User u = getUserWithUsername(username);
			CarShopController.logIn(username, u.getPassword());
		} catch (InvalidInputException e) {
			error = e.getMessage();
			errorCnt++;
		}
	}
	
	@Given("the following customers exist in the system:")
	public void existingCustomer(DataTable dataTable) {
		List<Map<String, String>> listReresentation = dataTable.asMaps(String.class, String.class);
		//Convert the datatable to a list of maps and then adds the corresponding customers to the system.
		for(Map<String, String> list: listReresentation) {
			String username = list.get("username");
			String password = list.get("password");
			
			Customer cust = new Customer(username, password, 0, carshop);
		}
	}
	
	@When("{string} initiates the addition of the service {string} with duration {string} belonging to the garage of {string} technician")
	public void initiatesServiceAdded(String username, String name, String duration, String garageStr) {
		try {
			//Initiates the creation of a new service to the corresponding garage.
			Garage garage = getGarageOfTechnician(getTechnicianTypeFromString(garageStr));
			CarShopController.createService(name, Integer.valueOf(duration), garage);
		}
		catch(InvalidInputException ex) {
			error = ex.getMessage();
			errorCnt++;
		}
		catch(RuntimeException ex) {
			error = ex.getMessage();
			errorCnt++;
		}
	}
	@When("{string} initiates the update of the service {string} to name {string}, duration {string}, belonging to the garage of {string} technician")
	public void updateService(String username, String oldName, String newName, String duration, String garageStr) throws InvalidInputException {
		try	{
			//Initiates the update of the service to the new parameters.
			Garage garage = getGarageOfTechnician(getTechnicianTypeFromString(garageStr));
			//Used to keep track of the old name of the service for later testing.
			this.oldServiceName = oldName;
			
			CarShopController.updateService(oldName, newName, Integer.valueOf(duration), garage);
		}
		catch(InvalidInputException ex) {
			error = ex.getMessage();
			errorCnt++;
		}
		catch(RuntimeException ex) {
			error = ex.getMessage();
			errorCnt++;
		}
	}

	@Then("the service {string} shall exist in the system")
	public void checkServiceInSystem(String name) {
		assertNotNull(getServiceFromName(name));
	}
	@Then("the service {string} shall belong to the garage of {string} technician")
	public void checkServiceInGarage(String name, String garage) {
		Garage g = getGarageOfTechnician(getTechnicianTypeFromString(garage));
		assertNotNull(getServiceFromNameInGarage(name, g));
	}
	@Then("the number of services in the system shall be {string}")
	public void checkServiceNumbers(String number) {
		assertEquals(Integer.valueOf(number), getNumberOfServicesInSystem());
	}
	@Then("an error message with content {string} shall be raised")
	public void checkErrorRaised(String errorMessage) {
		assertEquals(errorMessage, this.error);
		this.error = null;
	}
	@Then("the service {string} shall not exist in the system")
	public void checkNotExistService(String name) {
		assertNull(getServiceFromName(name));
	}
	@Then("the service {string} shall still preserve the following properties:")
	public void checkServiceProperties(String name, DataTable table) {
		//Convert the Datatable to a list of maps and then check that the system with name 'name' has the properties mentioned in the table.
		List<Map<String, String>> maps = table.asMaps();
		for(Map<String, String> map: maps) {
			Service service = getServiceFromName(name);
			assertNotNull(service);
			assertEquals(service.getDuration(), Integer.valueOf(map.get("duration")));
			Garage g = getGarageOfTechnician(getTechnicianTypeFromString(map.get("garage")));
			assertEquals(service.getGarage(), g);
		}
		
	}
	
	@Then("the service {string} shall be updated to name {string}, duration {string}")
	public void checkServiceUpdated(String oldName, String newName, String newDuration) {
		//Makes sure that the service with oldname was update to the new name (if it is not the same) and to its new duration.
		if(!oldName.equals(newName)) {
			assertNotNull(this.oldServiceName);
			assertNull(getServiceFromName(this.oldServiceName));
		}
		

		assertEquals(oldName, this.oldServiceName);
		
		Service newService = getServiceFromName(newName);
		assertNotNull(newService);
		assertEquals(Integer.valueOf(newDuration), newService.getDuration());
		this.oldServiceName = null;
	}
	//End of code written by Mario Bouzakhm.
	
	// Author: Hyunbum Cho (below this until specified is all Hyunbum's code)----------------------------------------------------------
	// set up and update business info
    @Given("no business exists")
    public void noBuisnessExists() {
      if (carshop.hasBusiness()) {
        carshop.getBusiness().delete();
      }
    }
    
    @When("the user tries to set up the business information with new {string} and {string} and {string} and {string}")
    public void userTriesToSetUpTheBusinessInfo(String name, String address, String phoneNumber, String email) {
      try {
        CarShopController.setBusinessInfo(name, address, phoneNumber, email);
      } catch (InvalidUserException e) {
        error += e.getMessage();
        errorCnt++;
      } catch (InvalidInputException e) {
        error += e.getMessage();
        errorCnt++;
      }
    }
    
    @Then("a new business with new {string} and {string} and {string} and {string} shall {string} created")
    public void aNewBuisnessWithNameAddressPhoneNumberEmailIsCreated(String name, String address, String phoneNumber, String email, String result) {
      if (!result.contains("not")) {
        assertEquals(name, carshop.getBusiness().getName());
        assertEquals(address, carshop.getBusiness().getAddress());
        assertEquals(phoneNumber, carshop.getBusiness().getPhoneNumber());
        assertEquals(email, carshop.getBusiness().getEmail());
      } else {
        assertEquals(null, carshop.getBusiness());
      }
    }
    
    @Then("an error message {string} shall {string} raised")
    public void anErrorMessageIsRaised(String errorMsg, String result) {
      if (!result.contains("not")) {
        assertTrue(error.contains(errorMsg));
      } else {
        System.out.println(error);
        assertTrue(error=="");
      }
    }
    
    @Given("the business has a business hour on {string} with start time {string} and end time {string}")
    public void theBusinessHasABusinessHourOn(String day, String startTime, String endTime) {
      DayOfWeek dayOfWeek = DayOfWeek.valueOf(day);
      Time sTime = convertToTime(startTime);
      Time eTime = convertToTime(endTime);
      BusinessHour bH = new BusinessHour(dayOfWeek, sTime, eTime, carshop);
      carshop.getBusiness().addBusinessHour(bH);
      initialSize = carshop.getBusiness().getBusinessHours().size();
    }
    
    @When("the user tries to add a new business hour on {string} with start time {string} and end time {string}")
    public void theUserTriesToAddANewBusinessHourOn(String day, String startTime, String endTime) {
      Time sTime = convertToTime(startTime);
      Time eTime = convertToTime(endTime);
      DayOfWeek dayOfWeek = DayOfWeek.valueOf(day);
      try {
        initialSize = carshop.getBusiness().getBusinessHours().size();
        CarShopController.addBusinessHour(dayOfWeek, sTime, eTime);
      } catch (InvalidUserException e) {
        error += e.getMessage();
        errorCnt++;
      } catch (InvalidInputException e) {
        error += e.getMessage();
        errorCnt++;
      }
    }
    
    @Then("a new business hour shall {string} created")
    public void aNewBusinessHourShallbeCreated(String result) {
      if (!result.contains("not")) {
        assertEquals(initialSize + 1, carshop.getBusiness().getBusinessHours().size());
      } else {
        assertEquals(initialSize, carshop.getBusiness().getBusinessHours().size());
      }  
    }
    
    @When("the user tries to access the business information")
    public void theUserTriesToAccessTheBusinessInfo() {
      businessInfo = CarShopController.getBusinessInfo();
    }
    
    @Then("the {string} and {string} and {string} and {string} shall be provided to the user")
    public void businessInforShallbeProvidedToTheUser(String name, String address, String phoneNumber, String email) {
      assertEquals(name, businessInfo.get(0));
      assertEquals(address, businessInfo.get(1));
      assertEquals(phoneNumber, businessInfo.get(2));
      assertEquals(email, businessInfo.get(3));
    }

    
    @Given("a {string} time slot exists with start time {string} at {string} and end time {string} at {string}")
    public void aTimeSlotExistsWithStartTimeAndEndTime(String type, String startDate, String startTime, String endDate, String endTime) {
      Date sDate = convertToDate(startDate);
      Date eDate = convertToDate(endDate);
      Time sTime = convertToTime(startTime);
      Time eTime = convertToTime(endTime);

      TimeSlot tSlot = new TimeSlot(sDate,sTime,eDate,eTime,carshop);
      if (type.contains("holiday")) {
        carshop.getBusiness().addHoliday(tSlot);
      } else if (type.contains("vacation")) {
        carshop.getBusiness().addVacation(tSlot);
      }
      
    }
    
    @When("the user tries to add a new {string} with start date {string} at {string} and end date {string} at {string}")
    public void theUserTriesToAddANewTimeslot(String type, String startDate, String startTime, String endDate, String endTime) {
      Date sDate = convertToDate(startDate);
      Date eDate = convertToDate(endDate);
      Time sTime = convertToTime(startTime);
      Time eTime = convertToTime(endTime);
      
      try {
        CarShopController.addNewTimeSlot(type, sDate, sTime, eDate, eTime);
      } catch (InvalidUserException e) {
        error += e.getMessage();
        errorCnt++;
      } catch (InvalidInputException e) {
        error += e.getMessage();
        errorCnt++;
      } 
    }
    
    @Then("a new {string} shall {string} be added with start date {string} at {string} and end date {string} at {string}")
    public void aNewTimeslotShallBeAddedWith(String type, String result, String startDate, String startTime, String endDate, String endTime) {
      boolean contains = false;
      List<TimeSlot> timeslots;
      Date sDate = convertToDate(startDate);
      Date eDate = convertToDate(endDate);
      Time sTime = convertToTime(startTime);
      Time eTime = convertToTime(endTime);
      if (type.contains("holiday")) {
        timeslots = carshop.getBusiness().getHolidays();
        for (TimeSlot t: timeslots) {
          if (t.getStartDate().equals(sDate) && t.getStartTime().equals(sTime) && t.getEndDate().equals(eDate) && t.getEndTime().equals(eTime)) {
            contains = true;
            break;
          }
        }
      } else if (type.contains("vacation")) {
        timeslots = carshop.getBusiness().getVacations();
        for (TimeSlot t: timeslots) {
          if (t.getStartDate().equals(sDate) && t.getStartTime().equals(sTime) && t.getEndDate().equals(eDate) && t.getEndTime().equals(eTime)) {
            contains = true;
            break;
          }
        }
      }
      if (!result.contains("not")) {
        assertTrue(contains);
      } else { // not be
        assertFalse(contains);
      }
      
    }
    
    @When("the user tries to update the business information with new {string} and {string} and {string} and {string}")
    public void theUserTriesToUpdateTheBusinessInfoWith(String name, String address, String phoneNumber, String email) {
      try {
        CarShopController.updateBusinessInfo(name, address, phoneNumber, email);
      } catch (InvalidUserException e) {
        error += e.getMessage();
        errorCnt++;
      } catch (InvalidInputException e) {
        error += e.getMessage();
        errorCnt++;
      }
    }
    
    @Then("the business information shall {string} updated with new {string} and {string} and {string} and {string}")
    public void theBusinessInfoShallBeUpdatedWith(String result, String name, String address, String phoneNumber, String email) {
      if (!result.contains("not")) { // be
        assertEquals(name, carshop.getBusiness().getName());
        assertEquals(address, carshop.getBusiness().getAddress());
        assertEquals(phoneNumber, carshop.getBusiness().getPhoneNumber());
        assertEquals(email, carshop.getBusiness().getEmail());       
      } else {
        assertNotEquals(name, carshop.getBusiness().getName());
        assertNotEquals(address, carshop.getBusiness().getAddress());
        assertNotEquals(phoneNumber, carshop.getBusiness().getPhoneNumber());
        assertNotEquals(email, carshop.getBusiness().getEmail());
      }
    }
    
    @When("the user tries to change the business hour {string} at {string} to be on {string} starting at {string} and ending at {string}")
    public void theUserTriesToChangeTheBusinessHourAt(String day, String startTime, String newDay, String newStartTime, String newEndTime) {
      DayOfWeek day1 = DayOfWeek.valueOf(day);
      DayOfWeek day2 = DayOfWeek.valueOf(newDay);
      Time oldStartTime = convertToTime(startTime);
      Time nStartTime = convertToTime(newStartTime);
      Time nEndTime = convertToTime(newEndTime);
      res = false;
      try {
        CarShopController.updateBusinessHour(day1, oldStartTime, day2, nStartTime, nEndTime);
        res = true;
      } catch (InvalidUserException e) {
        error += e.getMessage();
        errorCnt++;
      } catch (InvalidInputException e) {
        error += e.getMessage();
        errorCnt++;
      }
    }
    
    @Then("the business hour shall {string} be updated")
    public void theBusinessHourShallBeUpdated(String result) {
      if (!result.contains("not")) { //be
        assertTrue(res);
      } else {
        assertFalse(res);
      }
    }
    
    @When("the user tries to remove the business hour starting {string} at {string}")
    public void theUserTriesToRemoveTheBusinessHourStarting(String day, String startTime) {
      DayOfWeek day1 = DayOfWeek.valueOf(day);
      Time sTime = convertToTime(startTime);
      try {
        CarShopController.removeBusinessHour(day1, sTime);
      } catch (InvalidUserException e) {
        error += e.getMessage();
        errorCnt++;
      } catch (InvalidInputException e) {
        error += e.getMessage();
        errorCnt++;
      }
    }
    
    @Then("the business hour starting {string} at {string} shall {string} exist")
    public void theBusinessHourStartingShallExist(String day, String startTime, String result) {
      List<BusinessHour> bHours = carshop.getBusiness().getBusinessHours();
      res = false;
      DayOfWeek day1 = DayOfWeek.valueOf(day);
      Time sTime = convertToTime(startTime);
      for (BusinessHour b: bHours) {
        if (b.getDayOfWeek().equals(day1) && b.getStartTime().equals(sTime)) {
          res = true;
          break;
        }
      }
      if (!result.contains("not")) { // be
        assertTrue(res);
      } else {
        assertFalse(res);
      }
    }
    
    @Then("an error message {string} shall {string} be raised")
    public void anErrorMessageShallBeRaised(String errorMsg, String result) {
      if (!result.contains("not")) { // be
        assertTrue(error.contains(errorMsg));
      } else {
        assertTrue(error=="");
      }
    }
    
    @When("the user tries to change the {string} on {string} at {string} to be with start date {string} at {string} and end date {string} at {string}")
    public void theUserTriesToChangeTheTimeSlotOn(String type, String oldStartDate, String oldStartTime, String newStartDate, String newStartTime, String newEndDate, String newEndTime) {
      Date oStartDate = convertToDate(oldStartDate);
      Date nStartDate = convertToDate(newStartDate);
      Date nEndDate = convertToDate(newEndDate);
      Time oStartTime = convertToTime(oldStartTime);
      Time nStartTime = convertToTime(newStartTime);
      Time nEndTime = convertToTime(newEndTime);
      try {
        if (type.contains("vacation")) {
          CarShopController.updateVacation(oStartDate, oStartTime, nStartDate, nStartTime, nEndDate, nEndTime);
        } else if (type.contains("holiday")) {
          CarShopController.updateHoliday(oStartDate, oStartTime, nStartDate, nStartTime, nEndDate, nEndTime);
        }
      } catch (InvalidUserException e) {
        error += e.getMessage();
        errorCnt++;
      } catch (InvalidInputException e) {
        error += e.getMessage();
        errorCnt++;
      }
    }
    
    @Then("the {string} shall {string} updated with start date {string} at {string} and end date {string} at {string}")
    public void theTimeSlotShallBeUpdatedWith(String type, String result, String newStartDate, String newStartTime, String newEndDate, String newEndTime) {
      Date nStartDate = convertToDate(newStartDate);
      Date nEndDate = convertToDate(newEndDate);
      Time nStartTime = convertToTime(newStartTime);
      Time nEndTime = convertToTime(newEndTime);
      res = false;
      List<TimeSlot> tSlots;
      
      if (type.contains("holiday")) {
        tSlots = carshop.getBusiness().getHolidays();
        for (TimeSlot ts: tSlots) {
          if (ts.getStartDate().equals(nStartDate) && ts.getStartTime().equals(nStartTime) && ts.getEndDate().equals(nEndDate) && ts.getEndTime().equals(nEndTime)) {
            res = true;
            break;
          }
        }
      } else {
        tSlots = carshop.getBusiness().getVacations();
        for (TimeSlot ts: tSlots) {
          if (ts.getStartDate().equals(nStartDate) && ts.getStartTime().equals(nStartTime) && ts.getEndDate().equals(nEndDate) && ts.getEndTime().equals(nEndTime)) {
            res = true;
            break;
          }
        }
      }
      
      if (!result.contains("not")) { // be
        assertTrue(res);
      } else {
        assertFalse(res);
      }
    }
    
    @When("the user tries to remove an existing {string} with start date {string} at {string} and end date {string} at {string}")
    public void theUserTriesToRemoveAnExistingTimeSlotWith(String type, String startDate, String startTime, String endDate, String endTime) {
      Date sDate = convertToDate(startDate);
      Date eDate = convertToDate(endDate);
      Time sTime = convertToTime(startTime);
      Time eTime = convertToTime(endTime);
      try {
        CarShopController.removeTimeSlot(type, sDate, sTime, eDate, eTime);
      } catch (InvalidUserException e) {
        error += e.getMessage();
        errorCnt++;
      } catch (InvalidInputException e) {
        error += e.getMessage();
        errorCnt++;
      }
    }
    
    @Then("the {string} with start date {string} at {string} shall {string} exist")
    public void theTimeSlotWithShallExist(String type, String startDate, String startTime, String result) {
      Date sDate = convertToDate(startDate);
      Time sTime = convertToTime(startTime);
      res = false;
      List<TimeSlot> tSlots;
      if (type.contains("holiday")) {
        tSlots = carshop.getBusiness().getHolidays();
        for (TimeSlot ts: tSlots) {
          if (ts.getStartDate().equals(sDate) && ts.getStartTime().equals(sTime)) {
            res = true;
            break;
          }
        }
      } else {
        tSlots = carshop.getBusiness().getVacations();
        for (TimeSlot ts: tSlots) {
          if (ts.getStartDate().equals(sDate) && ts.getStartTime().equals(sTime)) {
            res = true;
            break;
          }
        }
      }
      if (!result.contains("not")) { // be
        assertTrue(res);
      } else {
        assertFalse(res);
      }
    }
    
    // convert from String to Time
    private static Time convertToTime(String t) {
      int hour;
      int minute;
      String[] tokens = t.split(":");
      hour = Integer.parseInt(tokens[0]);
      minute = Integer.parseInt(tokens[1]);
      Time myTime = Time.valueOf(LocalTime.of(hour, minute));
      return myTime;
    }
    
    // convert from String to Date
    private static Date convertToDate(String d) {
      int year;
      int month;
      int date;
      String[] tokens = d.split("-");
      year = Integer.parseInt(tokens[0]);
      month = Integer.parseInt(tokens[1]);
      date = Integer.parseInt(tokens[2]);
      Date myDate = Date.valueOf(LocalDate.of(year, month, date));
      return myDate;
    }
    
    // end of Hyunbum's code -------------------------------------------------------------------------
	
    //This is the start of Youssef's CucumberStepDefinitions code for DefineServiceCombo
  	//Initiates the definition of a service combo with its corresponding name, main service, services and mandatory settings for the services
    	@When("{string} initiates the definition of a service combo {string} with main service {string}, services {string} and mandatory setting {string}")
    	public void initiates_the_definition_of_a_service_combo_with_main_service_services_and_mandatory_setting(String username, String name, String mainServiceStr, String servicesStr, String mandatoryStr) {
    	    try {
    	    	String comboName = name;
    	    	List<String> servicesString = Arrays.asList(servicesStr.split(","));
    	    	List<Boolean> servicesBoolean = new ArrayList<>();
    	    	String[] booleanStr = mandatoryStr.split(",");
    	    	for(String s: booleanStr) {
    	    		servicesBoolean.add(Boolean.valueOf(s));
    	    	}
    	    	
    	    	CarShopController.defineCombo(comboName, mainServiceStr, servicesString, servicesBoolean);
    	    }
    	    catch(InvalidInputException ex) {
    	        error += ex.getMessage();
    	        errorCnt++;
    	    }
    	    catch(RuntimeException ex) {
    	    	error += ex.getMessage();
    	        errorCnt++;
    	    }
    	}
    	
    //Makes sure that the service combo exists
    	@Then("the service combo {string} shall exist in the system")
    	public void the_service_combo_shall_exist_in_the_system(String name) {
    		assertNotNull(getServiceComboFromName(name));
    	}

    	//Makes sure that the service combo contains existing services with their respective mandatory settings
    	@Then("the service combo {string} shall contain the services {string} with mandatory setting {string}")
    	public void the_service_combo_shall_contain_the_services_with_mandatory_setting(String name, String servicesStr, String mandatoryStr) {
    		ServiceCombo serviceCombo = getServiceComboFromName(name);
    		List<String> servicesString = Arrays.asList(servicesStr.split(","));
      	List<Boolean> servicesBoolean = new ArrayList<>();
      	String[] booleanStr = mandatoryStr.split(",");
      	for(String s: booleanStr) {
      		servicesBoolean.add(Boolean.valueOf(s));
      	}
      	
      	int checkedItems = 0;
      	List<ComboItem> comboItems = serviceCombo.getServices();
      	for(ComboItem co: comboItems) {
      		int index = servicesString.indexOf(co.getService().getName());
      		if(index == -1) {
      			fail();
      		}
      		
      		if(co.getMandatory() == Boolean.valueOf(servicesBoolean.get(index))) {
      			checkedItems++;
      		}
      	}
      	
      	assertEquals(checkedItems, comboItems.size());
    	}
    	
    	//Makes sure the entered service is the service combo's main service
    	@Then("the main service of the service combo {string} shall be {string}")
    	public void the_main_service_of_the_service_combo_shall_be(String name, String mainService) {
    	    assertEquals(getServiceFromName(mainService), getMainServiceFromComboName(name).getService());
    	}
    	
    	//Makes sure the the combo and service exist then checks if the latter is mandatory
    	@Then("the service {string} in service combo {string} shall be mandatory")
    	public void the_service_in_service_combo_shall_be_mandatory(String serviceName, String comboName) {
    		Service service = getServiceFromName(serviceName);
    		ServiceCombo combo = getServiceComboFromName(comboName);
    		
    		assertNotNull(service);
    		assertNotNull(combo);
    		
    		for(ComboItem co: combo.getServices()) {
    			if(co.getService().equals(service)) {
    				assertTrue(co.getMandatory());
    				return;
    			}
    		}
    		
    		fail();
    		
    	}

    	//Makes sure the service combos are registered in the system
    	@Then("the number of service combos in the system shall be {string}")
    	public void the_number_of_service_combos_in_the_system_shall_be(String number) {
    	    assertEquals(Integer.valueOf(number), getNumberOfServiceCombosInSystem());
    	}
    	
    	//Creates service combos in the system while assigning them their main service and combo items
    	@Given("the following service combos exist in the system:")
    	public void the_following_service_combos_exist_in_the_system(DataTable dataTable) {
    		
    		List<Map<String, String>> listReresentation = dataTable.asMaps(String.class, String.class);
    		for(Map<String, String> list: listReresentation) {
    			String name = list.get("name");
    			String mainServiceStr = list.get("mainService");
    			String services = list.get("services");
    			String mandatory = list.get("mandatory");
    			
    			ServiceCombo serviceCombo = new ServiceCombo(name, this.carshop);
    			
    	    	List<String> servicesString = Arrays.asList(services.split(","));
    	    	List<Boolean> servicesBoolean = new ArrayList<>();
    	    	String[] booleanStr = mandatory.split(",");
    	    	for(String s: booleanStr) {
    	    		servicesBoolean.add(Boolean.valueOf(s));
    	    	}
    	    	
    	    	Service mainService = getServiceFromName(mainServiceStr);
    	    	
    	    	for(int i = 0; i < servicesString.size(); i++) {
    	    		Service service = getServiceFromName(servicesString.get(i));
    	    		ComboItem co = new ComboItem(servicesBoolean.get(i), service, serviceCombo);
    	    		if(service.equals(mainService)) {
    	    			serviceCombo.setMainService(co);
    	    		}
    	    	}
    		}
    	}

    	//Makes sure the service combo does not exist
    	@Then("the service combo {string} shall not exist in the system")
    	public void the_service_combo_shall_not_exist_in_the_system(String name) {
    		assertNull(getServiceComboFromName(name));
    	}

    	//Makes sure the service combo maintains its properties i.e name, main service, combo items, combo items mandatory settings
    	@Then("the service combo {string} shall preserve the following properties:")
    	public void the_service_combo_shall_preserve_the_following_properties(String name, DataTable dataTable) {
    		List<Map<String, String>> maps = dataTable.asMaps();
    		for(Map<String, String> map: maps) {
    			ServiceCombo combo = getServiceComboFromName(name);
    	  		assertNotNull(combo);
    			
    			String mainServiceName = map.get("mainService");
    			assertEquals(combo.getMainService().getService().getName(), mainServiceName);
    			
    			List<String> servicesString = Arrays.asList(map.get("services").split(","));
    	    	List<Boolean> servicesBoolean = new ArrayList<>();
    	    	String[] booleanStr = map.get("mandatory").split(",");
    	    	for(String s: booleanStr) {
    	    		servicesBoolean.add(Boolean.valueOf(s));
    	    	}
    	    	
    	    	
    	    	int checkedItems = 0;
    	    	List<ComboItem> comboItems = combo.getServices();
    	    	for(ComboItem co: comboItems) {
    	    		int index = servicesString.indexOf(co.getService().getName());
    	    		if(index == -1) {
    	    			fail();
    	    		}
    	    		
    	    		if(co.getMandatory() == Boolean.valueOf(servicesBoolean.get(index))) {
    	    			checkedItems++;
    	    		}
    	    	}
    	    	
    	    	assertEquals(checkedItems, comboItems.size());
    		}
    	}
      // End of DefineServiceCombo
    	
    	//This is the start of Youssef's CucumberStepDefinitions code for UpdateServiceCombo
    	
    	//Initiates the update of an existing service combo into a new one with an updated name, main service, services and mandatory settings
    	@When("{string} initiates the update of service combo {string} to name {string}, main service {string} and services {string} and mandatory setting {string}")
    	public void initiates_the_update_of_service_combo_to_name_main_service_and_services_and_mandatory_setting(String username, String oldName, String newName, String mainServiceStr, String servicesStr, String mandatoryStr) {
  		try {
  			List<String> servicesString = Arrays.asList(servicesStr.split(","));
    	    	List<Boolean> servicesBoolean = new ArrayList<>();
    	    	String[] booleanStr = mandatoryStr.split(",");
    	    	for(String s: booleanStr) {
    	    		servicesBoolean.add(Boolean.valueOf(s));
    	    	}
  			
  			this.oldServiceComboName = oldName;
  			CarShopController.updateCombo(oldName, newName, mainServiceStr, servicesString, servicesBoolean);
  		}
  		catch(InvalidInputException ex) {
  			error = ex.getMessage();
  			errorCnt++;
  		}
  		catch(RuntimeException ex) {
  			error = ex.getMessage();
  			errorCnt++;
  		}
    	}

    	//Makes sure a service combo exists then updates its name
    	@Then("the service combo {string} shall be updated to name {string}")
    	public void the_service_combo_shall_be_updated_to_name(String oldName, String newName) {
    		if(!oldName.equals(newName)) {
    			assertNotNull(this.oldServiceComboName);
    			assertNull(getServiceComboFromName(this.oldServiceComboName));
    		}
    		
    		assertNotNull(getServiceComboFromName(newName));
    		this.oldServiceComboName = null;
    	}
    	
    	//End of UpdateServiceCombo
  	
	@After
	public void tearDown() {
		carshop.delete();
	}
	
	
	private TechnicianType getTechnicianTypeFromString(String type) {
		TechnicianType techType;
		switch(type) {
			case "Tire":
				techType = TechnicianType.Tire;
				break;
			case "Engine":
				techType = TechnicianType.Engine;
				break;
			case "Transmission":
				techType = TechnicianType.Transmission;
				break;
			case "Electronics":
				techType = TechnicianType.Electronics;
				break;
			case "Fluids":
				techType = TechnicianType.Fluids;
				break;
			default:
				techType = null;
				break;
		}
		return techType;
	}
	
	private Garage getGarageOfTechnician(TechnicianType techType) {
		if(this.carshop == null) {
			return null;
		}
		
		List<Garage> garages = this.carshop.getGarages();
		for(Garage garage: garages) {
			if (garage.getTechnician().getType() == techType) {
				return garage;
			}
		}
		return null;
	}
	
	private int countServicesInSystem() {
		int count = 0;
		for(BookableService bookableService: carshop.getBookableServices()) {
			if(bookableService instanceof Service) {
				count++;
			}
		}
		
		return count;
	}
	
	
	private Service getServiceFromName(String name) {
		for(BookableService bookableService: carshop.getBookableServices()) {
			if(bookableService instanceof Service && bookableService.getName().equals(name)) {
				return (Service) bookableService;
			}
		}
		
		return null;
 	}
	private Service getServiceFromNameInGarage(String name, Garage garage) {
		for(Service service: garage.getServices()) {
			if(service.getName().equals(name)) {
				return service;
			}
		}
		return null;
	}
	private int getNumberOfServicesInSystem() {
		int count = 0;
		
		for(BookableService bookableService: carshop.getBookableServices()) {
			if(bookableService instanceof Service) {
				count++;
			}
		}
		
		return count;
	}
	private User getUserWithUsername(String username) {
		for(Customer c: carshop.getCustomers()) {
			if(c.getUsername().equals(username)) {
				return c;
			}
		}
		
		for(Technician tech: carshop.getTechnicians()) {
			if(tech.getUsername().equals(username)) {
				return tech;
			}
		}
		
		if(carshop.getOwner()!=null) {
			if(username.equals(carshop.getOwner().getUsername())) {
				return carshop.getOwner();
			}
		}
	
		return null;
	}
	
	private static TechnicianType getTechTypeFromUsername(String username) {
		  switch(username) {
			case "Tire-Technician":
				  return TechnicianType.Tire;
			case "Engine-Technician":
				  return TechnicianType.Engine;
			case "Fluids-Technician":
				return TechnicianType.Fluids;
			case "Electronics-Technician":
				return TechnicianType.Electronics;
			case "Transmission-Technician":
				return TechnicianType.Transmission;
			default:
				return null;
		  }
	  }
	private int getCountOfUsers() {
		int count = 0;
		count += carshop.getCustomers().size();
		count += carshop.getTechnicians().size();
		if(carshop.getOwner() != null) {
			count += 1;
		}
		
		return count;
	}
	private int getNumberOfServiceCombosInSystem() {
		int count = 0;
		
		for(BookableService bookableService: carshop.getBookableServices()) {
			if(bookableService instanceof ServiceCombo) {
				count++;
			}
		}
		
		return count;
	}
	private boolean getMainServiceMandatoryFromComboName(String name) {
		for(BookableService bookableService: carshop.getBookableServices()) {
			if(bookableService instanceof ServiceCombo && bookableService.getName().equals(name)) {
				ServiceCombo combo = (ServiceCombo) bookableService;
				ComboItem mainService = combo.getMainService();
				return mainService.getMandatory();
			}
	}
		return false;
	}

	private ComboItem getMainServiceFromComboName(String name) {
		for(BookableService bookableService: carshop.getBookableServices()) {
			if(bookableService instanceof ServiceCombo && bookableService.getName().equals(name)) {
				ServiceCombo combo = (ServiceCombo) bookableService;
				return combo.getMainService();
			}
	}
		return null;
	}
	
	private ServiceCombo getServiceComboFromName(String name) {
		for(BookableService bookableService: carshop.getBookableServices()) {
			if(bookableService instanceof ServiceCombo && bookableService.getName().equals(name)) {
				return (ServiceCombo) bookableService;
			}
		}
		
		return null;
 	}
	
	private List<BusinessHour> getBussinessHoursOfDayByGarage(Garage g, DayOfWeek day) {
		List<BusinessHour> businessHourPerGarage = g.getBusinessHours();
		List<BusinessHour> dayBusinessHours = new ArrayList<BusinessHour>();
		for(BusinessHour hours: businessHourPerGarage) {
			if(hours.getDayOfWeek() == day) {
				dayBusinessHours.add(hours);
			}
		}
		
		return dayBusinessHours;
	}
	
	private List<BusinessHour> getBusinessHoursOfShopByDay(DayOfWeek day) {
		List<BusinessHour> dayHours = new ArrayList<BusinessHour>();
		for(BusinessHour bh: carshop.getBusiness().getBusinessHours()) {
			if(bh.getDayOfWeek() == day) {
				dayHours.add(bh);
			}
		}
		
		return dayHours;
	}
	
}
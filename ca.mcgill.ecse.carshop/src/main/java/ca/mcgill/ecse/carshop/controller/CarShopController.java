package ca.mcgill.ecse.carshop.controller;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import ca.mcgill.ecse.carshop.application.CarShopApplication;
import ca.mcgill.ecse.carshop.model.Appointment;
import ca.mcgill.ecse.carshop.model.BookableService;
import ca.mcgill.ecse.carshop.model.Business;
import ca.mcgill.ecse.carshop.model.BusinessHour;
import ca.mcgill.ecse.carshop.model.BusinessHour.DayOfWeek;
import ca.mcgill.ecse.carshop.model.Technician.TechnicianType;
import ca.mcgill.ecse.carshop.model.CarShop;
import ca.mcgill.ecse.carshop.model.Customer;
import ca.mcgill.ecse.carshop.model.Garage;
import ca.mcgill.ecse.carshop.model.Owner;
import ca.mcgill.ecse.carshop.model.Service;
import ca.mcgill.ecse.carshop.model.ServiceBooking;
import ca.mcgill.ecse.carshop.model.TimeSlot;
import ca.mcgill.ecse.carshop.model.Technician;
import ca.mcgill.ecse.carshop.model.User;
import ca.mcgill.ecse.carshop.model.Appointment.States;
import ca.mcgill.ecse.carshop.persistence.CarShopPersistence;

import java.util.ArrayList;


import ca.mcgill.ecse.carshop.model.ComboItem;
import ca.mcgill.ecse.carshop.model.ServiceCombo;


public class CarShopController {
	
	private static User loggedInUser;
	private static Appointment lastAddedAppointment;
	
	private static Date today = Date.valueOf(LocalDate.of(2021, 2, 1));
	private static Time now = Time.valueOf(LocalTime.of(11, 0));
	
	public static void testView() {
	  Owner owner = new Owner("owner", "owner pass", CarShopApplication.getCarShop());
	  try {
	    logIn("owner", "owner pass");
	  } catch (InvalidInputException e) {
	    System.out.println("incorrect password");
	  }
	  Technician tech = CarShopApplication.getCarShop().addTechnician("technician 1", "password", TechnicianType.Electronics);
	  CarShopApplication.getCarShop().addGarage(tech);
	}
	   
    public static List<TOService> getServices(){
      ArrayList<TOService> services = new ArrayList<TOService>();
      for (BookableService bookableService: CarShopApplication.getCarShop().getBookableServices()) {
        if (bookableService instanceof Service) {
          String technicianUsername = ((Service) bookableService).getGarage().getTechnician().getUsername();
          String technicianType = ((Service) bookableService).getGarage().getTechnician().getType().toString();
          TOGarage garage = new TOGarage(technicianUsername, technicianType);
          TOService toService = new TOService(bookableService.getName(), ((Service) bookableService).getDuration(), garage);
          services.add(toService);
        }   
      }
      return services;
    }
    
    public static List<TOServiceCombo> getCombos() {
		ArrayList<TOServiceCombo> combos = new ArrayList<TOServiceCombo>();
		for(BookableService bookableService: CarShopApplication.getCarShop().getBookableServices()) {
			if(bookableService instanceof ServiceCombo) {
				ServiceCombo combo = (ServiceCombo) bookableService;
				String name = combo.getName();
				String main = combo.getMainService().getService().getName();
				
				List<String> required = new ArrayList<>();
				List<String> optional = new ArrayList<>();
				
				for(ComboItem item: combo.getServices()) {
					if(item.getService().getName().equals(main)) {
						continue;
					}
					
					if(item.getMandatory()) {
						required.add(item.getService().getName());
					}
					else {
						optional.add(item.getService().getName());
					}
				}
				
				combos.add(new TOServiceCombo(name, main, required, optional));
			}
		}
		
		return combos;
	}
    
	public static List<TOGarage> getGarages(){
	  ArrayList<TOGarage> garages = new ArrayList<TOGarage>();
	  for (Garage garage: CarShopApplication.getCarShop().getGarages()) {
	    TOGarage toGarage = new TOGarage(garage.getTechnician().getUsername(), garage.getTechnician().getType().toString());
	    garages.add(toGarage);
      }
      return garages;
    }
	
	public static Garage getGarageFromTechnicianType(String username) throws InvalidInputException {
	  List<Garage> garages = CarShopApplication.getCarShop().getGarages();
	  Garage g = null;
	  for (Garage garage: garages) {
	    if (garage.getTechnician().getUsername() == username) {
	      g = garage;
	      break;
	    }
	  }
	  if (g == null) {
	    throw new InvalidInputException("Couldn't find any garage with technican type" + username);
	  }
	  return g;
	}
	
	//Appointment Management
	public static void customerCancelApp(Appointment app) throws InvalidInputException {
		
		if(!loggedInUser.equals(app.getCustomer())) {
			throw new InvalidInputException("You cannot cancel another customer's appointment");
		}
		
		States state = app.getStates();
		if(state == States.Booking) {
			if(!app.getServiceBooking(0).getTimeSlot().getStartDate().equals(today)) {
				app.Cancel();
			}
			else {
				throw new InvalidInputException("You cannot cancel an appointment the same day");
			}
		}
		
		CarShopPersistence.save(CarShopApplication.getCarShop());
	}
	
	public static void customerCancelAppTO(TOAppointment app) throws InvalidInputException {
		Appointment appt = getAppointmentFromTO(app);
		CarShopController.customerCancelApp(appt);
	}
	
	public static void setMainService(Appointment app, String serviceName) throws InvalidInputException {
		if(!loggedInUser.equals(app.getCustomer())) {
			throw new InvalidInputException("Only the customer can change their appointment");
		}
		
		CarShop carShop = CarShopApplication.getCarShop();
		
		Service service = getServiceFromName(serviceName);
		Time startTime = app.getServiceBooking(0).getTimeSlot().getStartTime();
		Date d = app.getServiceBooking(0).getTimeSlot().getStartDate();
		
		int minutes = (startTime.getMinutes() == 60) ? service.getDuration() : (startTime.getMinutes() + service.getDuration());
		int hours = (minutes / 60) + startTime.getHours();
		while(minutes >= 60) {
			minutes -= 60;
		}
		
		Time endTime = new Time(hours, minutes, 0);
		
		if(CarShopController.checkValidTime(d, startTime, endTime, service) && !CarShopController.getToday().equals(d)) {
			app.SetMain(service, new TimeSlot(d, startTime, d, endTime, carShop));
		}
		
		CarShopPersistence.save(carShop);
	}
	
	public static void setMainServiceTO(TOAppointment to, String serviceName) throws InvalidInputException {
		CarShopController.setMainService(getAppointmentFromTO(to), serviceName);
	}
	
	public static void updateDateTimes(Appointment app, Date d, List<Time> startTimes) throws InvalidInputException {
		if(!loggedInUser.equals(app.getCustomer())) {
			throw new InvalidInputException("Only the customer can change their appointment");
		}
		
		if(app.getServiceBooking(0).getTimeSlot().getStartDate().equals(today)) {
			return;
		}
		
		CarShop carShop = CarShopApplication.getCarShop();
		States state = app.getStates();
		if(state == States.Booking) {
			if(today.equals(d)) {
				return;
			}
			
			List<Service> services = new ArrayList<>();
			for(ServiceBooking booking: app.getServiceBookings()) {
				services.add(booking.getService());
			}
			
			List<Time> existingStarts = getStartTimes(app);
			List<Time> endTimes = new ArrayList<>();
			
			Date existingDate = app.getServiceBooking(0).getTimeSlot().getStartDate();
			
			for(int i = 0; i < services.size(); i++) {
				Time startTime = startTimes.get(i);
				Time existingStart = existingStarts.get(i);
				
				Time endTime;
				
				if(startTime.equals(existingStart) && (existingDate.equals(d))) {
					continue;
				}
				
				Service service = services.get(i);
				
				int minutes = (startTime.getMinutes() == 60) ? service.getDuration() : (startTime.getMinutes() + service.getDuration());
				int hours = (minutes / 60) + startTime.getHours();
				while(minutes >= 60) {
					minutes -= 60;
				}
				
				endTime = new Time(hours, minutes, 0);
				endTimes.add(endTime);
				
				//Checks that the slots we are trying to use are available.
				if(!checkValidTime(d, startTime, endTime, service)) {
					return;
				}
			}
			
			//Checks that none of the times are overlapping
			if(!checkOverlappingTimes(startTimes, endTimes)) {
				return;
			}
			
			for(int i = 0; i < app.getServiceBookings().size(); i++) {
				ServiceBooking booking = app.getServiceBooking(i);
				booking.setTimeSlot(new TimeSlot(d, startTimes.get(i), d, endTimes.get(i), carShop));
			}
		}
		
		CarShopPersistence.save(carShop);
		
	}
	
	public static void updateDateTimesTO(TOAppointment to, Date d, List<Time> startTimes) throws InvalidInputException {
		CarShopController.updateDateTimes(getAppointmentFromTO(to), d, startTimes);
	}
	
	private static List<Time> getStartTimes(Appointment app) {
		List<Time> startTimes = new ArrayList<>();
		for(ServiceBooking booking: app.getServiceBookings()) {
			startTimes.add(booking.getTimeSlot().getStartTime());
		}
		
		return startTimes;
	}
	
	public static void addOptionalService(Appointment app, String addedService, Time startTime) throws InvalidInputException {
		if(!loggedInUser.equals(app.getCustomer())) {
			throw new InvalidInputException("Only the customer can change their appointment");
		}
		
		CarShop carShop = CarShopApplication.getCarShop();
		if(app.getBookableService() instanceof ServiceCombo) {
			States state = app.getStates();
			if(state == States.Booking) {
				if(today.equals(app.getServiceBooking(0).getTimeSlot().getStartDate())) {
					return;
				}
			}
			
			
			Service service = getServiceFromName(addedService);
			Date d = app.getServiceBooking(0).getTimeSlot().getStartDate();
			
			int minutes = (startTime.getMinutes() == 60) ? service.getDuration() : (startTime.getMinutes() + service.getDuration());
			int hours = (minutes / 60) + startTime.getHours();
			while(minutes >= 60) {
				minutes -= 60;
			}
			
			
			
			Time endTime = new Time(hours, minutes, 0);
			
			if(!checkValidTime(d, startTime, endTime, service)) {
				return;
			}
			
			
			List<Time> startTimes = new ArrayList<>();
			List<Time> endTimes = new ArrayList<>();
			
			for(ServiceBooking booking: app.getServiceBookings()) {
				startTimes.add(booking.getTimeSlot().getStartTime());
				endTimes.add(booking.getTimeSlot().getEndTime());
			}
			
			
			startTimes.add(startTime);
			endTimes.add(endTime);
			
			if(!checkOverlappingTimes(startTimes, endTimes)) {
				return;
			}
			
			app.AddBooking(service, new TimeSlot(d, startTime, d, endTime, carShop));
		
			CarShopPersistence.save(carShop);
		}
	}
	public static void addOptionalServiceTO(TOAppointment to, String addedService, Time startTime) throws InvalidInputException {
		CarShopController.addOptionalService(getAppointmentFromTO(to), addedService, startTime);
	}
	
	public static void startAppointment(Appointment app) {
		States state = app.getStates();
		if(States.Booking == state) {
			Time startTime = app.getServiceBooking(0).getTimeSlot().getStartTime();
			Date d = app.getServiceBooking(0).getTimeSlot().getStartDate();
			
			if(today.equals(d) && (now.after(startTime) || startTime.equals(now))) {
				app.Start();
			}
		}
		
		CarShopPersistence.save(CarShopApplication.getCarShop());
	}
	
	public static void startAppointmentTO(TOAppointment toAppointment) {
		CarShopController.startAppointment(getAppointmentFromTO(toAppointment));
		
	}
	
	public static void endAppointment(Appointment app) {
		States state = app.getStates();
		
		if(States.AppointmentInProgress == state) {
			int length = app.getServiceBookings().size();
			Time endTime = app.getServiceBooking(length - 1).getTimeSlot().getEndTime();
			Date endDate = app.getServiceBooking(length - 1).getTimeSlot().getEndDate();
			
			if(today.equals(endDate) && (now.after(endTime) || now.equals(endTime))) {
				app.End();
			}
		}
		
		CarShopPersistence.save(CarShopApplication.getCarShop());
	}
	
	public static void endApponitmentTO(TOAppointment toAppointment) {
		CarShopController.endAppointment(getAppointmentFromTO(toAppointment));
	}
	
	public static void registerNoShow(Appointment app) {
		States state = app.getStates();
		if(States.Booking == state) {
			app.NoShow();
		}
		
		CarShopPersistence.save(CarShopApplication.getCarShop());
	}
	
	public static void registerNoShowTO(TOAppointment toAppointment) {
		CarShopController.registerNoShow(getAppointmentFromTO(toAppointment));
		
	}
	// End Of Appointment Management
	
	//Part that handle the appointment taking procedure
	public static void makeAppointmentService(String serviceName, Date date, Time startTime) throws InvalidInputException {
	    CarShopController.lastAddedAppointment = null;
		
		// Makes sure that the person that is taking the appointment is a customer
		if (!(loggedInUser instanceof Customer)) {
		      throw new InvalidInputException("Only customers can make an appointment");
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
		
		
		if(today.after(date) || (today.equals(date) && now.after(startTime))) {
			throw new InvalidInputException("There are no available slots for "+serviceName+" on " + date.toString() + " at " + sdf.format(startTime));
		}
		
		CarShop carShop = CarShopApplication.getCarShop();
		Business business = carShop.getBusiness();
		Customer cust = (Customer) loggedInUser;
		 
		Service service = getServiceFromName(serviceName);
		if(service == null) {
			throw new InvalidInputException("Service does not exist in the system.");
		}
		
		Garage g = service.getGarage();
		
		//Computes the endTime based on the duration of the service and the startTime.
		
		int minutes = (startTime.getMinutes() == 60) ? service.getDuration() : (startTime.getMinutes() + service.getDuration());
		int hours = (minutes / 60) + startTime.getHours();
		while(minutes >= 60) {
			minutes -= 60;
		}
		
		Time endTime = new Time(hours, minutes, 0);
		
		if(!checkValidTime(date, startTime, endTime, service)) {
			throw new InvalidInputException("There are no available slots for "+serviceName+" on " + date.toString() + " at " + sdf.format(startTime));
		}
		
		Appointment appointment = new Appointment(cust, service, carShop);
		appointment.SetMain(service, new TimeSlot(date, startTime, date, endTime, carShop));
				
		CarShopController.lastAddedAppointment = appointment;
		CarShopPersistence.save(carShop);
	}
	
	public static boolean checkValidTime(Date date, Time startTime, Time endTime, Service service) {
		DayOfWeek dayOfWeek = getDayOfWeekFromDate(date);
		
		if(!isInBusinessHours(dayOfWeek, startTime, endTime)) {
			return false;
		}
		
		//Checks if the service booking is a holiday
		if(isHoliday(date, startTime, endTime)) {
			return false;
		}
		
		//Checks if the service booking is a vacation
		if(isVacation(date, startTime, endTime)) {
			return false;
		}
		
		//Checks if the service is in the business hours of the garage
		if(!isInBusinessHoursGarage(dayOfWeek, startTime, endTime, service.getGarage())) {
			return false;
		}
		
		//Checks if the spot is available to be used in the garage
		if(!checkAvailableSlot(date, startTime, endTime, service.getGarage())) {
			return false;
		}
		
		return true;
	}
	

	 
	public static void makeAppointmentCombo(String comboName, List<String> optionalName, Date date, List<Time> startTimes) throws InvalidInputException {
		CarShopController.lastAddedAppointment = null;
		
		if (!(loggedInUser instanceof Customer)) {
		      throw new InvalidInputException("Only customers can make an appointment");
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
		
		if(today.after(date)) {
			throw new InvalidInputException("There are no available slots for "+comboName+" on " + date.toString() + " at " + sdf.format(startTimes.get(0)));
		}
		
		CarShop carShop = CarShopApplication.getCarShop();
		Business business = carShop.getBusiness();
		Customer cust = (Customer) loggedInUser;
		
		ServiceCombo combo = getServiceComboFromName(comboName);
		if(combo == null) {
			throw new InvalidInputException("Service Comco does not exist in the system.");
		}
		
		DayOfWeek dayOfWeek = getDayOfWeekFromDate(date);
		
		List<Time> endTimes = new ArrayList<>();
		List<Service> services = new ArrayList<>();
		
		for(int i = 0; i < startTimes.size(); i++) {
			//Handles the case for the first element of the list - Main Service
			Service service = null;
			Time startTime = startTimes.get(i);
			Time endTime = null;
			
			
			if(i == 0) {
				service = combo.getMainService().getService();							
			}
			else {
				service = getComboItemFromServiceName(combo, optionalName.get(i-1)).getService();
			}
			
			if(service == null) {
				throw new InvalidInputException("Service does not exist in the system");
			}
			
			services.add(service);
			
			int minutes = (startTime.getMinutes() == 60) ? service.getDuration() : (startTime.getMinutes() + service.getDuration());
			int hours = (minutes / 60) + startTime.getHours();
			while(minutes >= 60) {
				minutes -= 60;
			}
			
			endTime = new Time(hours, minutes, 0);
			endTimes.add(endTime);
			
			if(!checkValidTime(date, startTime, endTime, service)) {
				throw new InvalidInputException("There are no available slots for "+comboName+" on " + date.toString() + " at " + sdf.format(startTimes.get(0)));
			}
		}
		
		//Used to check time conflicts between the time slots
		if(!checkOverlappingTimes(startTimes, endTimes)) {
			throw new InvalidInputException("Time slots for two services are overlapping");
		}
		
		
		Appointment appointment = new Appointment(cust, combo, carShop);
		
		//Create the service bookings for each required time slot and adding it to the 
		for(int i = 0; i < startTimes.size(); i++) {
			ServiceBooking booking = new ServiceBooking(services.get(i), new TimeSlot(date, startTimes.get(i), date, endTimes.get(i), carShop), appointment);
		}
		
		CarShopController.lastAddedAppointment = appointment;
		
		CarShopPersistence.save(carShop);
	}
	
	public static void cancelAppointment(String bookableName, Date date, Time startTime) throws InvalidInputException {
		if (loggedInUser instanceof Owner) {
		      throw new InvalidInputException("An owner cannot cancel an appointment");
		}
		if(loggedInUser instanceof Technician) {
			throw new InvalidInputException("A technician cannot cancel an appointment");
		}
		
		Customer loggedInCust = (Customer) loggedInUser; 
		
		if(today.equals(date)) {
			throw new InvalidInputException("Cannot cancel an appointment on the appointment date");
		}
		
		
		CarShop carShop = CarShopApplication.getCarShop();
		BookableService bookable = getBookableFromName(bookableName);
		if(bookable == null) {
			throw new InvalidInputException("BookableService does not exist in the system");
		}
		
		for(Appointment appointment: carShop.getAppointments()) {
			if(appointment.getBookableService().equals(bookable)) {
				ServiceBooking booking = appointment.getServiceBooking(0);
				if(booking.getTimeSlot().getStartDate().equals(date) && booking.getTimeSlot().getEndDate().equals(date)) {
					if(booking.getTimeSlot().getStartTime().equals(startTime)) {
						Customer cust = appointment.getCustomer();
						if(!cust.equals(loggedInUser)) {
							throw new InvalidInputException("A customer can only cancel their own appointments");
						}
						
						appointment.delete();
						return;
					}
				}
			}
		}
		
		CarShopPersistence.save(carShop);
	}
	
	public static boolean isOverlapping(Time start1, Time end1, Time start2, Time end2) {
	    return (start1.before(end2) || start1.equals(end2)) && (start2.before(end1) || start2.equals(end1));
	}
	
	
	public static boolean checkOverlappingTimes(List<Time> startTimes, List<Time> endTimes) {
		for(int i = 0; i < startTimes.size(); i++) {
			Time startTime = startTimes.get(i);
			Time endTime = endTimes.get(i);
			
			for(int j = 0; j < startTimes.size(); j++) {
				if (i == j) {
					continue;
				}
				
				Time startToCheck = startTimes.get(j);
				Time endToCheck = endTimes.get(j);
								
				if(isOverlapping(startTime, endTime, startToCheck, endToCheck)) {
					return false;
				}
			}
		}
		return true;
	}
	
	private static boolean checkAvailableSlot(Date d, Time startTime, Time endTime, Garage g) {
		for(Service s: g.getServices()) {
			for(ServiceBooking booking: s.getServiceBookings()) {
				Date startDate = booking.getTimeSlot().getStartDate();
				
				if(startDate.equals(d)) {
					Time slotStart = booking.getTimeSlot().getStartTime();
					Time slotEnd = booking.getTimeSlot().getEndTime();
										
					if(isOverlapping(startTime, endTime, slotStart, slotEnd)) {
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	private static boolean isInBusinessHours(DayOfWeek dayOfWeek, Time startTime, Time endTime) {
		Business business = CarShopApplication.getCarShop().getBusiness();
		for(BusinessHour hour: business.getBusinessHours()) {
			
			if (hour.getDayOfWeek() == dayOfWeek) {
				//System.out.println("Day Of Week Matching for " + dayOfWeek);
				if(isOverlapping(startTime, endTime, hour.getStartTime(), hour.getEndTime())) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private static boolean isInBusinessHoursGarage(DayOfWeek dayOfWeek, Time startTime, Time endTime, Garage g) {
		for(BusinessHour hour: g.getBusinessHours()) {
			if(hour.getDayOfWeek() == dayOfWeek) {
				if((hour.getStartTime().before(startTime) || hour.getStartTime().equals(startTime)) && (hour.getEndTime().after(endTime) || hour.getEndTime().equals(endTime))) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private static boolean isVacation(Date d, Time startTime, Time endTime) {
		Business b = CarShopApplication.getCarShop().getBusiness();
		for(TimeSlot slot: b.getVacations()) {
			Date startDate = slot.getStartDate();
			Date endDate = slot.getEndDate();
			
			// Date to check is in range.
			if((d.equals(startDate) || startDate.before(d)) && (d.equals(endDate) || endDate.after(d))) {
				Time slotStart = slot.getStartTime();
				Time slotEnd = slot.getEndTime();
				
				
				if (isOverlapping(slotStart, slotEnd, startTime, endTime)) {
					return true;
				}
			}
		}
		
		return false;
	}
	private static boolean isHoliday(Date d, Time startTime, Time endTime) {
		Business b = CarShopApplication.getCarShop().getBusiness();
		for(TimeSlot slot: b.getHolidays()) {
			Date startDate = slot.getStartDate();
			Date endDate = slot.getEndDate();
			
			// Date to check is in range.
			if((startDate.equals(d) || startDate.before(d)) && (endDate.equals(d) || endDate.after(d))) {
				Time slotStart = slot.getStartTime();
				Time slotEnd = slot.getEndTime();
				
				
				if (isOverlapping(slotStart, slotEnd, startTime, endTime)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	// signUpCustomerAccount was coded by Sami Ait Ouahmane
	// It it doesn't find any exceptions, it signs up the user for a new customer account
	public static void signUpCustomerAccount(String username, String password) throws InvalidInputException {
		CarShop carShop=CarShopApplication.getCarShop();
		//If the username string is null or empty, throw an exception 
		if(username == null || username.length()==0) {
			throw new InvalidInputException("The user name cannot be empty");
		}
		//If the password string is null or empty, throw an exception 
		if( password == null || password.length()==0) {
			throw new InvalidInputException("The password cannot be empty");
		}
		
		//If the loggedInUser isn't null, verify whether it represents the owner or a technician. If it does, throw an error
		if(loggedInUser!=null) {
			if(loggedInUser.getUsername().equals("owner")) {
				throw new InvalidInputException("You must log out of the owner account before creating a customer account");
			}
			if(loggedInUser.getUsername().equals("Tire-Technician") || loggedInUser.getUsername().equals("Engine-Technician") || loggedInUser.getUsername().equals("Transmission-Technician") || loggedInUser.getUsername().equals("Electronics-Technician") || loggedInUser.getUsername().equals("Fluids-Technician") ) {
				throw new InvalidInputException("You must log out of the technician account before creating a customer account");
			}
		}
		
		//Add the customer. Catch the error if any is thrown.
		try {
			carShop.addCustomer(username, password, 0);
		}
		catch(RuntimeException e) {
			if(e.getMessage().startsWith("Cannot create due to duplicate")) {
				throw new InvalidInputException("The username already exists");
			}
			throw new InvalidInputException(e.getMessage());
		}
		
		CarShopPersistence.save(carShop);
	}
	
	//updateCustomerAccount was coded by Sami Ait Ouahmane
	public static void updateCustomerAccount(String newUsername, String newPassword) throws InvalidInputException {
		
		//If the new username string is null or empty, throw an exception 
		if(newUsername==null || newUsername.length()==0) {
			throw new InvalidInputException("The user name cannot be empty");
		}
		
		//If the new password string is null or empty, throw an exception 
		if(newPassword==null || newPassword.length()==0) {
			throw new InvalidInputException("The password cannot be empty");
		}
		
		//If the logged-in user is the owner and that the new username is different from "owner", throw an error
		if(loggedInUser instanceof Owner && !newUsername.equals("owner")) {
			throw new InvalidInputException("Changing username of owner is not allowed");
		}
		
		//If the logged-in user is a technician and that the new username is different from that technician old username, throw an error
		if(loggedInUser instanceof Technician && !(loggedInUser.getUsername().equals(newUsername))) {
			throw new InvalidInputException("Changing username of technician is not allowed");
		}
		
		//Try updating username and password and throw an error message if a runtime exception occurs
		try {
			//If setUsername returns false, then the username is already taken by somebody else
			if(loggedInUser.setUsername(newUsername)==false ) {
				throw new InvalidInputException("Username not available");
			}
			
			//set Username and passowrd
			loggedInUser.setUsername(newUsername);
			loggedInUser.setPassword(newPassword);
		}
		catch(RuntimeException e) {
			throw new InvalidInputException(e.getMessage());	
		}
		
		CarShopPersistence.save(CarShopApplication.getCarShop());
	}
	
	// logIn was coded by Hadi Ghaddar
	public static void logIn(String username, String password) throws InvalidInputException {
		CarShop carShop = CarShopApplication.getCarShop();
		TechnicianType techType = getTechTypeFromUsername(username);
		//case for owner log in
		if(username.equals("owner")) {
			Owner owner = carShop.getOwner();
			//creates a new owner if an owner account doesn't exist with username and password as "owner" and sets loggedInUser as owner
			if(owner == null) {
				owner = new Owner("owner", password, carShop);
				loggedInUser = owner;
			}
			else {
				//if entered password doesn't match owner's password
				if(!password.equals(owner.getPassword())) {
					loggedInUser = null;
					throw new InvalidInputException("Username/password not found");
				}
				else {
					loggedInUser = owner;
				}
			}
			
			
		}
		// case for technician log in
		else if(techType != null) {
			Technician existingTechAccount = getTechnicianWithTechType(techType);
			//creates a technician account with username, password and appropriate type
			if(existingTechAccount == null) {
				existingTechAccount = new Technician(username, password, techType, carShop);
				//corresponding garage for the technician is created
				Garage garage = new Garage(carShop, existingTechAccount);
				
				//the garage has the same opening hours as the business
				loggedInUser = existingTechAccount;
				
				if(carShop.getBusiness() != null) {
					for(DayOfWeek day: DayOfWeek.values()) {
						List<BusinessHour> dayBusinessHours = getBusinessHoursOfShopByDay(day);
						if(dayBusinessHours.size() != 0) {
							for(BusinessHour b: dayBusinessHours) {
								CarShopController.updateGarageOpeningHours(day, b.getStartTime(), b.getEndTime(), existingTechAccount.getType());
							}
						}
					}
				}
			}
			else {
				//throws error if entered password doesn't match technician's password
				if(!password.equals(existingTechAccount.getPassword())) {
					loggedInUser = null;
					throw new InvalidInputException("Username/password not found");
				}
				//sets loggedInUser as technician
				else {
					loggedInUser = existingTechAccount;
				}
			}
		}
		else {
			//case for customer log in
			Customer cust = getCustomerWithUsername(username);
			if(cust != null) {
				String custPassword = cust.getPassword();
				if(custPassword.equals(password)) {
					//sets loggedInUser as customer
					loggedInUser = cust;
					return;
				}
				
			}
			//throws error if the username/password doesn't match any of the records
			loggedInUser = null;
			throw new InvalidInputException("Username/password not found");
		}
	}
	
	// UpdateGarageOpeningHours was coded by Hadi Ghaddar
	public static void updateGarageOpeningHours(DayOfWeek day, Time startTime, Time endTime, TechnicianType techType) throws InvalidInputException {
		CarShop carShop = CarShopApplication.getCarShop();
	    //throws an error if the loggedInUser isn't a technician
	    if (!(loggedInUser instanceof Technician)) {
	      throw new InvalidInputException("You are not authorized to perform this operation");
	    }
	    //throws an error if the start time is after the end time
	    if (startTime.after(endTime)) {
	      throw new InvalidInputException("Start time must be before end time");
	    }
	    Technician tech = (Technician) loggedInUser;
	    //throws an error if the technician's type doesn't match 
	    if(tech.getType() != techType) {
	    	throw new InvalidInputException("You are not authorized to perform this operation");
	    }
	    Time startTimeDayShop = getOpeningTimeShopPerDay(day);
	    Time endTimeDayShop = getClosingTimeShopPerDay(day);
	    
	    
	    //throws an error if the start and end time aren't defined, or if the start time is after end time/end time is before start time
	    if (startTimeDayShop == null || endTimeDayShop == null|| startTime.before(startTimeDayShop) || endTime.after(endTimeDayShop)) {
	    	throw new InvalidInputException("The opening hours are not within the opening hours of the business");        
	    }
	    //sets the garage type as the logged in technician's type
	    Garage g = tech.getGarage();
	    
	    List<BusinessHour> bHourGarage = getBussinessHoursOfDayByGarage(g,day);
	    //adds opening hours if there aren't any defined
	    if (bHourGarage.size() == 0) {
	    	BusinessHour bHour = new BusinessHour(day, startTime, endTime, carShop);
	    	g.addBusinessHour(bHour);
	    }
	    else {
	    	for(BusinessHour existingHours: bHourGarage) {
	    		//throws an error if opening hours overlap with the business hours
	    		if (overlappingBusinessHours(existingHours.getStartTime(), existingHours.getEndTime(), startTime, endTime)) {
	    			throw new InvalidInputException("The opening hours cannot overlap");
	    		}
	    	}
	    }
	    
	    CarShopPersistence.save(carShop);
	}
	
	public static void removeGarageBusinessHours(DayOfWeek day, Time startTime, Time endTime, TechnicianType techType) throws InvalidInputException {
		CarShop carShop = CarShopApplication.getCarShop();
		 
	    if (!(loggedInUser instanceof Technician)) {
	    	throw new InvalidInputException("You are not authorized to perform this operation");
	    }
	    
	    Technician tech = (Technician) loggedInUser;
	    if(tech.getType() != techType) {
	    	throw new InvalidInputException("You are not authorized to perform this operation");
	    }
	    
	    Garage g = tech.getGarage();

	    List<BusinessHour> bHourGarage = getBussinessHoursOfDayByGarage(g, day);
	    if(bHourGarage.size() != 0) {
	    	for(BusinessHour hour: bHourGarage) {
		    	if(hour.getStartTime().equals(startTime) && hour.getEndTime().equals(endTime)) {
		    		g.removeBusinessHour(hour);
		    	}
	    	}
	    }
	    else {
	    	throw new InvalidInputException("Garage does not have opening hours on these days");
	    }
	    
	    CarShopPersistence.save(carShop);
	}
	
	//Code Written by Mario Bouzakhm
	public static void createService(String name, int duration, Garage garage) throws RuntimeException, InvalidInputException {
		//Checks that the logged in User is the owner
		if(loggedInUser == null  || !loggedInUser.getUsername().equals("owner") || !(loggedInUser instanceof Owner)) {
			throw new RuntimeException("You are not authorized to perform this operation");
		}
		
		CarShop carShop = CarShopApplication.getCarShop();
		
		// Takes care of ensuring the duration is positive.
		if(duration <= 0) {
			throw new RuntimeException("Duration must be positive");
		}
		// Makes sure that we are not creating a duplicate piece of code.
		if(getServiceFromName(name, carShop) != null) {
			throw new InvalidInputException("Service "+name+ " already exists");
		}
		   
		try {
			//Creates a new service with the garage mentioned (using referential integrity)
	        Service service = new Service(name, carShop, duration, garage);
	        CarShopPersistence.save(carShop);
	    }
		catch(RuntimeException ex) {
			//Catches any invalid inputs and raises the corresponding error
			throw new InvalidInputException(ex.getMessage());
		}
	}
	
	public static void updateService(String oldName, String newName, int newDuration, Garage newGarage) throws InvalidInputException {
		//Checks that the logged in User is the owner
		if(loggedInUser == null  || !loggedInUser.getUsername().equals("owner") || !(loggedInUser instanceof Owner)) {
			throw new RuntimeException("You are not authorized to perform this operation");
		}
		
		CarShop carShop = CarShopApplication.getCarShop();
		
		
		try {
			
			Service serviceToModify = getServiceFromName(oldName, carShop);
			
			//Makes sure that the new name we are choosing is not already taken.
			if(!oldName.equals(newName) && getServiceFromName(newName, carShop) != null) {
				throw new InvalidInputException("Service "+newName+ " already exists");
			}
			//Makes sure we don't have a service with negative/zero duration.
			if(newDuration <= 0) {
				throw new InvalidInputException("Duration must be positive");
			}
			
			if(serviceToModify != null) {
				//Makes the necessary changes to the service, (new name, new duration, new garage if needed).
				serviceToModify.setName(newName);
				serviceToModify.setDuration(newDuration);
				serviceToModify.setGarage(newGarage);
			}
			
			CarShopPersistence.save(carShop);
		}
		catch(RuntimeException ex) {
			//Catches any error and adds the appropriate message.
			throw new InvalidInputException(ex.getMessage());
			
		}
	}
	//End of Portion written by Mario Bouzakhm
		  

	//DefineCombo was written by Youssef Farouk
	public static void defineCombo(String name, String mainString, List<String> servicesStrings, List<Boolean> mandatory) throws InvalidInputException {
		CarShop carShop = CarShopApplication.getCarShop();
		
		//Checks that the logged in User is the owner
		if(!(loggedInUser instanceof Owner)) {
			throw new RuntimeException("You are not authorized to perform this operation");
		}
		
		//Makes sure that the service does not already exist
		if(getServiceComboFromName(name) != null) {
			throw new InvalidInputException("Service combo "+name+" already exists");
		}
		
		//Makes sure that the main service exists
		Service mainService = getServiceFromName(mainString);
		if(mainService == null) {
			throw new InvalidInputException("Service "+ mainString+" does not exist");
		}
		
		//Makes sure that the entered services all exist
		List<Service> services = new ArrayList<Service>();
		for(String str: servicesStrings) {
			Service s = getServiceFromName(str);
			if(s == null) {
				throw new InvalidInputException("Service "+ str+" does not exist");
			}
			else {
				services.add(s);
			}
		}
		
		//Makes sure that we have more than 2 services in the Combo
		if(services.size() < 2) {
			throw new InvalidInputException("A service Combo must contain at least 2 services");
		}
		
		//Makes sure the entered services include the main service
		if(!services.contains(mainService)) {
			throw new InvalidInputException("Main service must be included in the services");
		}
		
		//Makes sure the main service is mandatory
		if(mandatory.get(services.indexOf(mainService)) != true) {
			throw new InvalidInputException("Main service must be mandatory");
		}
	
		//Creates a new service combo
		ServiceCombo serviceCombo = new ServiceCombo(name, carShop);
		
		//Creates a combo item for services and sets main service for previously created combo
		for(Service service: services) {
			boolean  correspondingBoolean= mandatory.get(services.indexOf(service));
			ComboItem item = new ComboItem(correspondingBoolean, service, serviceCombo);
			if(service.equals(mainService)) {
				serviceCombo.setMainService(item);
			}
		}
		
		CarShopPersistence.save(carShop);
	}
	
	//UpdateCombo was written by Youssef Farouk
	public static void updateCombo(String oldName, String newName, String mainString, List<String> newServices, List<Boolean> mandatory) throws InvalidInputException {
		CarShop carShop = CarShopApplication.getCarShop();
		
		//Checks that the logged in User is the owner
		if(!(loggedInUser instanceof Owner)) {
			throw new RuntimeException("You are not authorized to perform this operation");
		}
		
		//Makes sure that the service combo the user wishes to update exists
		ServiceCombo existingCombo = getServiceComboFromName(oldName);
		if(existingCombo == null) {
			throw new InvalidInputException("Service combo "+oldName+" does not exists");
		}
		
		//Makes sure that a service combo with the same name does not already exist
		if(!newName.equals(oldName)) {
			ServiceCombo overlappingCombo = getServiceComboFromName(newName);
			if(overlappingCombo != null){
				throw new InvalidInputException("Service "+newName+" already exists");
			}
		}
		
		//Makes sure that the main service exists
		Service mainService = getServiceFromName(mainString);
		if(mainService == null) {
			throw new InvalidInputException("Service "+ mainString+" does not exist");
		}
		
		//Makes sure that the entered services all exist
		List<Service> services = new ArrayList<Service>();
		for(String str: newServices) {
			Service s = getServiceFromName(str);
			if(s == null) {
				throw new InvalidInputException("Service "+ str+" does not exist");
			}
			else {
				services.add(s);
			}
		}
		
		//Makes sure that we have more than 2 services in the Combo
		if(services.size() < 2) {
			throw new InvalidInputException("A service Combo must contain at least 2 services");
		}
		
		//Makes sure the entered services include the main service
		if(!services.contains(mainService)) {
			throw new InvalidInputException("Main service must be included in the services");
		}
		
		//Makes sure the main service is mandatory
		if(mandatory.get(services.indexOf(mainService)) != true) {
			throw new InvalidInputException("Main service must be mandatory");
		}
		
		//Updates name for the service combo
		existingCombo.setName(newName);
		
		//Sets the services and main service for the updated combo
		for(Service service: services) {
			ComboItem existingCItem = comboHasService(existingCombo, service);
			boolean  correspondingBoolean= mandatory.get(services.indexOf(service));
			if(existingCItem != null) {
				existingCItem.setMandatory(correspondingBoolean);
				if(service.equals(mainService)) {
					existingCombo.setMainService(existingCItem);
				}
			}
			else {
				ComboItem item = new ComboItem(correspondingBoolean, service, existingCombo);
				if(service.equals(mainService)) {
					existingCombo.setMainService(item);
				}
			}
		}
		
		//Removes legacy services
		for(ComboItem item: existingCombo.getServices()) {
			if(!services.contains(item.getService())) {
				existingCombo.removeService(item);
			}
		}
		
		CarShopPersistence.save(carShop);
	}
	
	//Returns combo item from entered combo if the latter contains the entered service
	private static ComboItem comboHasService(ServiceCombo combo, Service service) {
		for(ComboItem co: combo.getServices()) {
			if(co.getService().equals(service)) {
				return co;
			}
		}
		return null;
	}
	
	//End of code written by Youssef Farouk	
	
	public static User getLoggedInUser() {
		return loggedInUser;
	}
	
	private static ServiceCombo getServiceComboFromName(String name, CarShop carShop) {
		List<BookableService> bookableServices = carShop.getBookableServices();
		for(BookableService bookableService: bookableServices) {
			if(bookableService instanceof ServiceCombo) {
				ServiceCombo serviceCombo = (ServiceCombo) bookableService;
				if(serviceCombo.getName().equals(name)) {
					return serviceCombo;
				}
			}
		}

		return null;
	}
	private static Service getServiceFromName(String name, CarShop carShop) {
		List<BookableService> bookableServices = carShop.getBookableServices();
		for(BookableService bookableService: bookableServices) {
			if(bookableService instanceof Service) {
				Service service = (Service) bookableService;
				if(service.getName().equals(name)) {
					return service;
				}
			}
		}

		return null;
	}
	
	
	// Author: Hyunbum Cho (below this until specified is all Hyunbum's code) -----------------------------------------------------
	// update, set up business info
	// set up business info using provided inputs
	public static void setBusinessInfo (String aName, String aAddress, String aPhoneNumber, String aEmail) throws InvalidInputException, InvalidUserException {
	    if (CarShopController.getLoggedInUser() != CarShopApplication.getCarShop().getOwner()) {
	      throw new InvalidUserException("No permission to set up business information");
	    } else if (!isValidEmailAddress(aEmail)) {
	      throw new InvalidInputException("Invalid email");
	    } else if (aPhoneNumber.length()!=13) {
	      throw new InvalidInputException("Invalid phone number");
	    }
	    Business business = new Business(aName, aAddress, aPhoneNumber, aEmail, CarShopApplication.getCarShop());
	    CarShopApplication.getCarShop().setBusiness(business);
	    
	    CarShopPersistence.save(CarShopApplication.getCarShop());
	  }
	  
	// update the business info with provided inputs
	  public static void updateBusinessInfo (String aName, String aAddress, String aPhoneNumber, String aEmail) throws InvalidInputException, InvalidUserException {
	    if (CarShopController.getLoggedInUser() != CarShopApplication.getCarShop().getOwner()) {
	      throw new InvalidUserException("No permission to update business information");
	    } else if (!isValidEmailAddress(aEmail)) {
	      throw new InvalidInputException("Invalid email");
	    } else if (aPhoneNumber.length()!=13) {
	      throw new InvalidInputException("Invalid phone number");
	    }
	    
	    Business business = CarShopApplication.getCarShop().getBusiness();
	    business.setName(aName);
	    business.setAddress(aAddress);
	    business.setPhoneNumber(aPhoneNumber);
	    business.setEmail(aEmail);
	    
	    CarShopPersistence.save(CarShopApplication.getCarShop());
	    }
	  
	  // add business hour
	  public static void addBusinessHour (DayOfWeek day, Time newStartTime, Time newEndTime) throws InvalidInputException, InvalidUserException {
	    Business business = CarShopApplication.getCarShop().getBusiness();
	    List<BusinessHour> bHour = business.getBusinessHours();
	    BusinessHour newBHour;
	    if (CarShopController.getLoggedInUser() != CarShopApplication.getCarShop().getOwner()) {
	      throw new InvalidUserException("No permission to update business information");
	    } else if (newStartTime.after(newEndTime)) {
	      throw new InvalidInputException("Start time must be before end time");
	    }
	    for (BusinessHour b: bHour) {
	      if (b.getDayOfWeek().equals(day)) {
	        if (newStartTime.before(b.getEndTime()) && newEndTime.after(b.getStartTime())) {
	          throw new InvalidInputException("The business hours cannot overlap");
	        }
	      }
	    }
	    
	    newBHour = new BusinessHour(day, newStartTime, newEndTime, business.getCarShop());
	    business.addBusinessHour(newBHour);
	    
	    CarShopPersistence.save(CarShopApplication.getCarShop());
	  }
	  
	  // update business hour
	  public static void updateBusinessHour (DayOfWeek oldDay, Time oldStartTime, DayOfWeek day, Time newStartTime, Time newEndTime) throws InvalidInputException, InvalidUserException {
	    Business business = CarShopApplication.getCarShop().getBusiness();
        List<BusinessHour> bHours = business.getBusinessHours();
        BusinessHour bHour = null;
	    if (CarShopController.getLoggedInUser() != CarShopApplication.getCarShop().getOwner()) {
	      throw new InvalidUserException("No permission to update business information");
	    } else if (newStartTime.after(newEndTime)) {
	      throw new InvalidInputException("Start time must be before end time");
	    }
	    for (BusinessHour b: bHours) {
	      if (b.getDayOfWeek().equals(day)) {
	        if (newStartTime.before(b.getEndTime()) && newEndTime.after(b.getStartTime())) {
	          if (!oldDay.equals(b.getDayOfWeek()) && !oldStartTime.equals(b.getStartTime())) {
	            throw new InvalidInputException("The business hours cannot overlap");
	          }
	        }
	      }
	    }
	    
	    for (BusinessHour b: bHours) {
	      if (oldDay.equals(b.getDayOfWeek()) && oldStartTime.equals(b.getStartTime())) {
	        bHour = b;
	      }
	    }
	    if (bHour != null) {
	      bHour.setStartTime(newStartTime);
	      bHour.setEndTime(newEndTime);
	    }
	    
	    CarShopPersistence.save(CarShopApplication.getCarShop());
	  }
	  
	  // remove business hour
	  public static void removeBusinessHour (DayOfWeek day, Time startTime) throws InvalidInputException, InvalidUserException {
	    Business business = CarShopApplication.getCarShop().getBusiness();
        List<BusinessHour> bHours = business.getBusinessHours();
        BusinessHour bHour = null;
	    if (CarShopController.getLoggedInUser() != CarShopApplication.getCarShop().getOwner()) {
	      throw new InvalidUserException("No permission to update business information");
	    }
	    
	    for (BusinessHour b: bHours) {
	      if (b.getDayOfWeek().equals(day) && b.getStartTime().equals(startTime)) {
	        bHour = b;
	      }
	    }
	    if (bHour != null) business.removeBusinessHour(bHour);
	    
	    CarShopPersistence.save(CarShopApplication.getCarShop());
	  }
	  
	  // view business info
	  public static List<String> getBusinessInfo(){
	    Business business = CarShopApplication.getCarShop().getBusiness();
	    ArrayList<String> businessInfo = new ArrayList<String>(4);
	    businessInfo.add(business.getName());
	    businessInfo.add(business.getAddress());
	    businessInfo.add(business.getPhoneNumber());
	    businessInfo.add(business.getEmail());
	    return businessInfo;
	  }
	  
	  // add new vacation or holiday
	  public static void addNewTimeSlot(String type, Date startDate, Time startTime, Date endDate, Time endTime) throws InvalidInputException, InvalidUserException {
	    Business business = CarShopApplication.getCarShop().getBusiness();
	    List<TimeSlot> holidays;
	    List<TimeSlot> vacations;
	    
	    if (CarShopController.getLoggedInUser() != CarShopApplication.getCarShop().getOwner()) {
	      throw new InvalidUserException("No permission to update business information");
	    }
	    if (endDate.before(startDate)) {
	      throw new InvalidInputException("Start time must be before end time");
	    } else if ((startDate.equals(endDate)) && (endTime.before(startTime))) { // same day
	      throw new InvalidInputException("Start time must be before end time");
	    } else if (!type.contains("holiday") && !type.contains("vacation")) {
	      throw new InvalidInputException("Invalild type");
	    } else if (startDate.before(today)) {
	      if (type.contains("holiday")) {
	        throw new InvalidInputException("Holiday cannot start in the past");
	      } else {
	        throw new InvalidInputException("Vacation cannot start in the past ");
	      }
	    }
	    holidays = business.getHolidays();
	    vacations = business.getVacations();
	    
	    for (TimeSlot h: holidays) {
	      if (startDate.before(h.getEndDate())) { // new start date is before end date of holiday
	        if (endDate.after(h.getStartDate())) { // new end date is after start date of holiday
	          // overlap with holiday
	          if (type.contains("holiday")) {
	            throw new InvalidInputException("Holiday times cannot overlap");
	          } else { // vacation
	            throw new InvalidInputException("Holiday and vacation times cannot overlap");
	          }
	        } else if (endDate.equals(h.getStartDate())) { // ends at start date of holiday
	          if (endTime.after(h.getStartTime())) { // end time after start time of holiday
	            if (type.contains("holiday")) {
	              throw new InvalidInputException("Holiday times cannot overlap");
	            } else { // vacation
	              throw new InvalidInputException("Holiday and vacation times cannot overlap");
	            }
	          }
	        }
	      } 
	    }
	    
	    for (TimeSlot v: vacations) {
	      if (startDate.before(v.getEndDate())) { // new start date is before end date of vacation
	        if (endDate.after(v.getStartDate())) { // new end date is after start date of vacation
	          // overlap with vacation
	          if (type.contains("vacation")) {
	            throw new InvalidInputException("Vacation times cannot overlap");
	          } else { // holiday
	            throw new InvalidInputException("Holiday and vacation times cannot overlap");
	          }
	        } else if (endDate.equals(v.getStartDate())) { // ends at start date of vacation
	          if (endTime.after(v.getStartTime())) { // end time after start time of vacation
	            if (type.contains("vacation")) {
	              throw new InvalidInputException("Vacation times cannot overlap");
	            } else { // holiday
	              throw new InvalidInputException("Holiday and vacation times cannot overlap");
	            }
	          }
	        }
	      } 
	    }
	    
	    // no overlaps
	    TimeSlot ts = new TimeSlot(startDate, startTime, endDate, endTime, CarShopApplication.getCarShop());
	    if (type.contains("holiday")) {
	      business.addHoliday(ts);
	    } else {
	      business.addVacation(ts);
	    }
	    
	    CarShopPersistence.save(CarShopApplication.getCarShop());
	  }
	  
	  // update vacation
	  public static void updateVacation (Date oldDate, Time oldStartTime, Date startDate, Time startTime, Date endDate, Time endTime) throws InvalidInputException, InvalidUserException {
	    Business business = CarShopApplication.getCarShop().getBusiness();
	    List<TimeSlot> holidays;
	    List<TimeSlot> vacations;
	    TimeSlot vacation = null;
	    
	    if (CarShopController.getLoggedInUser() != CarShopApplication.getCarShop().getOwner()) {
	      throw new InvalidUserException("No permission to update business information");
	    }
	    if (endDate.before(startDate)) {
	      throw new InvalidInputException("Start time must be before end time");
	    } else if ((startDate.equals(endDate)) && (endTime.before(startTime))) { // same day
	      throw new InvalidInputException("Start time must be before end time");
	    } else if (startDate.before(today)) {
	      throw new InvalidInputException("Vacation cannot start in the past");
	    }

	    holidays = business.getHolidays();
	    vacations = business.getVacations();
	    
	    for (TimeSlot h: holidays) {
	      if (startDate.before(h.getEndDate())) { // new start date is before end date of holiday
	        if (endDate.after(h.getStartDate())) { // new end date is after start date of holiday
	          // overlap with holiday
	          throw new InvalidInputException("Holiday and vacation times cannot overlap");
	        } else if (endDate.equals(h.getStartDate())) { // ends at start date of holiday
	          if (endTime.after(h.getStartTime())) { // end time after start time of holiday
	            throw new InvalidInputException("Holiday and vacation times cannot overlap");
	          }
	        }
	      } 
	    }
	    
	    for (TimeSlot v: vacations) {
	      if (oldDate.equals(v.getStartDate()) && oldStartTime.equals(v.getStartTime())) {
	        vacation = v;
	        continue;
	      }
	      if (startDate.before(v.getEndDate())) { // new start date is before end date of v
	        if (endDate.after(v.getStartDate())) { // new end date is after start date of v
	          throw new InvalidInputException("Vacation times cannot overlap");
	        } else if (endDate.equals(v.getStartDate())) { // ends at start date of v
	          if (endTime.after(v.getStartTime())) { // end time after start time of v
	            throw new InvalidInputException("Vacation times cannot overlap");
	          }
	        }
	      } 
	    }
	    
	    // no overlaps
	    vacation.setStartDate(startDate);
	    vacation.setStartTime(startTime);
	    vacation.setEndDate(endDate);
	    vacation.setEndTime(endTime);
	    
	    CarShopPersistence.save(CarShopApplication.getCarShop());
	  }
	  
	  
	  // update holiday
	  public static void updateHoliday (Date oldDate, Time oldStartTime, Date startDate, Time startTime, Date endDate, Time endTime) throws InvalidInputException, InvalidUserException {
	    Business business = CarShopApplication.getCarShop().getBusiness();
	    List<TimeSlot> holidays;
	    List<TimeSlot> vacations;
	    TimeSlot holiday = null;
	    
	    if (CarShopController.getLoggedInUser() != CarShopApplication.getCarShop().getOwner()) {
	      throw new InvalidUserException("No permission to update business information");
	    }
	    if (endDate.before(startDate)) {
	      throw new InvalidInputException("Start time must be before end time");
	    } else if ((startDate.equals(endDate)) && (endTime.before(startTime))) { // same day
	      throw new InvalidInputException("Start time must be before end time");
	    } else if (startDate.before(today)) {
	      throw new InvalidInputException("Holiday cannot start in the past");
	    }

	    holidays = business.getHolidays();
	    vacations = business.getVacations();
	    
	    for (TimeSlot v: vacations) {
	      if (startDate.before(v.getEndDate())) { // new start date is before end date of holiday
	        if (endDate.after(v.getStartDate())) { // new end date is after start date of holiday
	          // overlap with holiday
	          throw new InvalidInputException("Holiday and vacation times cannot overlap");
	        } else if (endDate.equals(v.getStartDate())) { // ends at start date of holiday
	          if (endTime.after(v.getStartTime())) { // end time after start time of holiday
	            throw new InvalidInputException("Holiday and vacation times cannot overlap");
	          }
	        }
	      } 
	    }
	    
	    for (TimeSlot h: holidays) {
	      if (oldDate.equals(h.getStartDate()) && oldStartTime.equals(h.getStartTime())) {
	        holiday = h;
	        continue;
	      }
	      if (startDate.before(h.getEndDate())) { // new start date is before end date of v
	        if (endDate.after(h.getStartDate())) { // new end date is after start date of v
	          throw new InvalidInputException("Holiday times cannot overlap");
	        } else if (endDate.equals(h.getStartDate())) { // ends at start date of v
	          if (endTime.after(h.getStartTime())) { // end time after start time of v
	            throw new InvalidInputException("Holiday times cannot overlap");
	          }
	        }
	      } 
	    }
	    
	    // no overlaps
	    holiday.setStartDate(startDate);
	    holiday.setStartTime(startTime);
	    holiday.setEndDate(endDate);
	    holiday.setEndTime(endTime);
	    
	    CarShopPersistence.save(CarShopApplication.getCarShop());
	  }
	  
	  // remove a vacation or holiday
	  public static void removeTimeSlot (String type, Date startDate, Time startTime, Date endDate, Time endTime) throws InvalidInputException, InvalidUserException {
	    Business business = CarShopApplication.getCarShop().getBusiness();
	    List<TimeSlot> holidays;
	    List<TimeSlot> vacations;
	    TimeSlot holiday = null;
	    TimeSlot vacation = null;
	    if (CarShopController.getLoggedInUser() != CarShopApplication.getCarShop().getOwner()) {
	      throw new InvalidUserException("No permission to update business information");
	    } else if (!type.contains("holiday") && !type.contains("vacation")) {
	      throw new InvalidInputException("Invalild type");
	    }
	    
	    if (type.contains("holiday")) {
	      holidays = business.getHolidays();
	      for (TimeSlot h: holidays) {
	        if (h.getStartDate().equals(startDate) && h.getStartTime().equals(startTime) && h.getEndDate().equals(endDate) && h.getEndTime().equals(endTime)) {
	          holiday = h;
	          break;
	        }
	      }
	      
	      if (holiday == null) {
	        throw new InvalidInputException("No such holiday exists");
	      } else {
	        business.removeHoliday(holiday);
	      }
	    } else { // vacation
	      vacations = business.getVacations();
	      for (TimeSlot v: vacations) {
	        if (v.getStartDate().equals(startDate) && v.getStartTime().equals(startTime) && v.getEndDate().equals(endDate) && v.getEndTime().equals(endTime)) {
	          vacation =v;
	          break;
	        }
	      }
	      
	      if (vacation == null) {
	        throw new InvalidInputException("No such vacation exists");
	      } else {
	        business.removeVacation(vacation);
	      }
	    }
	    CarShopPersistence.save(CarShopApplication.getCarShop());
	  }
	  
	  // set today
	  public static void setToday (Date d) {
	    today = d;
	  }
	  
	  // set current time
	  public static void setTime (Time t) {
	    now = t;
	  }
	  
	  // check for email validity
	  private static boolean isValidEmailAddress(String email) {
	    boolean valid = true;
	    if(!email.contains("@")) {
	      valid = false; 
	    } else if (!email.contains(".")) {
	      valid = false;
	    } else if (email.charAt(0)=='@') { // starts with @
	      valid = false;
	    } else if (email.charAt(email.length()-1) == '.') { // ends with .
	      valid = false;
	    } else if (email.charAt(email.length() - 1) == '@' || email.charAt(email.length() - 2) == '@') { // @ is last or second last character
	      valid = false;
	    } else if(!email.substring(email.indexOf('@') + 2, email.length() - 1).contains(".")) { 
	      // . has to be at least two characters after @ (need at least one character between them)
	      valid = false;
	    }
	    
	    
	    return valid;
	  }
	  
	  // End of Hyunbum's code -------------------------------------------------------------------------
	  
	  private static BookableService getBookableFromName(String name) {
		  CarShop carShop = CarShopApplication.getCarShop();
		  for(BookableService bookable: carShop.getBookableServices()) {
			  if(bookable.getName().equals(name)) {
				  return bookable;
			  }
		  }
		  
		  return null;
	  }
	  
	  private static ComboItem getComboItemFromServiceName(ServiceCombo combo, String name) {
		  for(ComboItem item: combo.getServices()) {
			  if(item.getService().getName().equals(name)) {
				  return item;
			  }
		  }
		  
		  return null;
	  }
	  
	  
	  private static List<BusinessHour> getBussinessHoursOfDayByGarage(Garage g, DayOfWeek day) {
			List<BusinessHour> businessHourPerGarage = g.getBusinessHours();
			List<BusinessHour> dayBusinessHours = new ArrayList<BusinessHour>();
			for(BusinessHour hours: businessHourPerGarage) {
				if(hours.getDayOfWeek() == day) {
					dayBusinessHours.add(hours);
				}
			}
			
			return dayBusinessHours;
		}
	  
	  @SuppressWarnings("deprecation")
	  private static DayOfWeek getDayOfWeekFromDate(Date d) {
		  switch(d.getDay()) {
		  	case 0:
		  		return DayOfWeek.Sunday;
		  	case 1:
		  		return DayOfWeek.Monday;
		  	case 2:
		  		return DayOfWeek.Tuesday;
		  	case 3:
		  		return DayOfWeek.Wednesday;
		  	case 4:
		  		return DayOfWeek.Thursday;
		  	case 5:
		  		return DayOfWeek.Friday;
		  	case 6:
		  		return DayOfWeek.Saturday;
		  	default:
		  		return null;
	
		  }
	  }
	  
	  private static Garage getGarageOfTechnician(TechnicianType techType) {
			CarShop carshop = CarShopApplication.getCarShop();
			if(carshop == null) {
				return null;
			}
			
			List<Garage> garages = carshop.getGarages();
			for(Garage garage: garages) {
				if (garage.getTechnician().getType() == techType) {
					return garage;
				}
			}
			return null;
		}
	  private static Customer getCustomerWithUsername(String username) {
		  CarShop carShop = CarShopApplication.getCarShop();
		  for(Customer c: carShop.getCustomers()) {
			  if(c.getUsername().equals(username)) {
				  return c;
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
	  
	  private static Technician getTechnicianWithTechType(TechnicianType techType) {
		  CarShop carShop = CarShopApplication.getCarShop();
		  for(Technician tech: carShop.getTechnicians()) {
			  if(tech.getType() == techType) {
				  return tech;
			  }
		  }
		  
		  return null;
	  }
	  
	private static List<BusinessHour> getBusinessHoursOfShopByDay(DayOfWeek day) {
		CarShop carShop = CarShopApplication.getCarShop();
		List<BusinessHour> dayHours = new ArrayList<BusinessHour>();
		for(BusinessHour bh: carShop.getBusiness().getBusinessHours()) {
			if(bh.getDayOfWeek() == day) {
				dayHours.add(bh);
			}
		}
		
		return dayHours;
	}
	
	  private static Time getOpeningTimeShopPerDay(DayOfWeek day) {
		  List<BusinessHour> bHours = getBusinessHoursOfShopByDay(day);
		  Time startTime = null;
		  for(BusinessHour bHour: bHours) {
			  if(startTime == null || bHour.getStartTime().before(startTime)) {
				  startTime = bHour.getStartTime();
			  }
		  }
		  
		  return startTime;
	  }
	  
	 private static List<ServiceBooking> getAppointmentsForDayGarage(Garage g, Date d) {
		 CarShop carShop = CarShopApplication.getCarShop();
		 List<ServiceBooking> serviceBookings = new ArrayList<>();
		 for(Service service: g.getServices()) {
			 for(ServiceBooking serviceBooking: service.getServiceBookings()) {
				 if(serviceBooking.getTimeSlot().getStartDate().equals(d) && serviceBooking.getTimeSlot().getEndDate().equals(d)) {
					 serviceBookings.add(serviceBooking);
				 }
			 }
		 }
		 
		 return serviceBookings;
	 }
	  
	  private static Time getClosingTimeShopPerDay(DayOfWeek day) {
		  List<BusinessHour> bHours = getBusinessHoursOfShopByDay(day);
		  Time endTime = null;
		  for(BusinessHour bHour: bHours) {
			  if(endTime == null || bHour.getEndTime().after(endTime)) {
				  endTime = bHour.getEndTime();
			  }
		  }
		  
		  return endTime;
	  }
	  
		private static Service getServiceFromName(String name) {
			CarShop carShop = CarShopApplication.getCarShop();
			for(BookableService bookableService: carShop.getBookableServices()) {
				if(bookableService instanceof Service && bookableService.getName().equals(name)) {
					return (Service) bookableService;
				}
			}
			
			return null;
	 	}
	  
	  private static boolean overlappingBusinessHours(Time hour1Start, Time hour1End, Time hour2Start, Time hour2End) {
		  boolean overlap1 = hour1Start.after(hour2Start) && hour1Start.before(hour2End);
		  boolean overlap2 = hour1End.after(hour2Start) && hour1End.before(hour2End);
		  boolean overlap3 = hour2Start.after(hour1Start) && hour2Start.before(hour1End);
		  boolean overlap4 = hour2End.after(hour1Start) && hour2End.before(hour1End);
		  
		  return overlap1 || overlap2 || overlap3 || overlap4;
	  }
	  
	  private static ServiceCombo getServiceComboFromName(String name) {
		  CarShop carshop = CarShopApplication.getCarShop();
		  
		  for(BookableService bookableService: carshop.getBookableServices()) {
				if(bookableService instanceof ServiceCombo && bookableService.getName().equals(name)) {
					return (ServiceCombo) bookableService;
				}
			}
			
			return null;
	 	}
	  
	  public static List<TOAppointment> getAppointments() {
		  List<TOAppointment> appointments = new ArrayList<>();
		  
		  for(Appointment app: CarShopApplication.getCarShop().getAppointments()) {
			  String username = app.getCustomer().getUsername();
			  
			  Date d = app.getServiceBookings().get(0).getTimeSlot().getStartDate();
			  
			  List<String> services = new ArrayList<>();
			  List<Time> startTimes = new ArrayList<>();
			  List<Time> endTimes = new ArrayList<>();
			  
			  for(ServiceBooking bookings: app.getServiceBookings()) {
				  services.add(bookings.getService().getName());
				  startTimes.add(bookings.getTimeSlot().getStartTime());
				  endTimes.add(bookings.getTimeSlot().getEndTime());
			  }
			  
			  TOAppointment toApp = new TOAppointment(username, app.getBookableService().getName(), d, services, startTimes, endTimes, app.getBookableService() instanceof ServiceCombo);
			  appointments.add(toApp);
		  }
		  
		  return appointments;
	  }
	  
	  
	  
	  public static List<TOAppointment> getAppointmentsOfCustomer(String username) {
		  List<TOAppointment> appointments = new ArrayList<>();
		  Customer cust = getCustomerWithUsername(username);
		  
		  for(Appointment app: cust.getAppointments()) {
			  
			  Date d = app.getServiceBookings().get(0).getTimeSlot().getStartDate();
			  
			  List<String> services = new ArrayList<>();
			  List<Time> startTimes = new ArrayList<>();
			  List<Time> endTimes = new ArrayList<>();
			  
			  for(ServiceBooking bookings: app.getServiceBookings()) {
				  services.add(bookings.getService().getName());
				  startTimes.add(bookings.getTimeSlot().getStartTime());
				  endTimes.add(bookings.getTimeSlot().getEndTime());
			  }
			  
			  TOAppointment toApp = new TOAppointment(username, app.getBookableService().getName(), d, services, startTimes, endTimes, app.getBookableService() instanceof ServiceCombo);
			  appointments.add(toApp);
		  }
		  
		  return appointments;
	  }
	  
	  public static List<TOBookableService> getTOBookables() {
		  List<TOBookableService> bookables = new ArrayList<>();
		  
		  for(BookableService bookable: CarShopApplication.getCarShop().getBookableServices()) {
			  bookables.add(new TOBookableService(bookable.getName(), bookable instanceof ServiceCombo));
		  }
		  
		  return bookables;
	  }
	  
	  public static Appointment getAppointmentFromTO(TOAppointment to) {
		  Customer c = (Customer) getCustomerWithUsername(to.getUsername());
		  
		  for(Appointment app: c.getAppointments()) {
			  ServiceBooking booking1 = app.getServiceBookings().get(0);
			  if(booking1.getTimeSlot().getStartDate().equals(to.getDate()) && booking1.getTimeSlot().getStartTime().equals(to.getStartTimes().get(0))) {
				  return app;
			  }
		  }
		  
		  return null;
	  }
	  
	  public static TOUser getTOLoggedIn() {
		  if(loggedInUser == null) {
			  return null;
		  }
		  
		  if(loggedInUser instanceof Owner) {
			  return new TOUser(loggedInUser.getUsername(), true, false, false);
		  }
		  else if(loggedInUser instanceof Technician) {
			  return new TOUser(loggedInUser.getUsername(), false, false, true);
		  }
		  else {
			  return new TOUser(loggedInUser.getUsername(), false, true, false);
		  }
	  }
	  
	  public static TOBusiness getBusiness() {
			TOBusiness biz;
			Business bus=CarShopApplication.getCarShop().getBusiness();
			if(bus==null) return null;
			else {
				biz=new TOBusiness(bus.getName(),bus.getAddress(),bus.getEmail(),bus.getPhoneNumber());
				return biz;
			}	
	  }
	  
	  public static void logOut() {
		  loggedInUser=null;
	  }
	  
	  public static Appointment getLastAddedAppointment() {
		  return CarShopController.lastAddedAppointment;
	  }
		
	  public static Time getCurrentTimes() {
			return now;
	  }
	  public static Date getToday() {
		  return today;
	  }

	
}

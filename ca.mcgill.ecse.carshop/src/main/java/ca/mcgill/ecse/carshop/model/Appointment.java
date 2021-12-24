/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.30.1.5099.60569f335 modeling language!*/

package ca.mcgill.ecse.carshop.model;
import java.io.Serializable;
import java.sql.Time;
import java.sql.Date;
import java.util.*;

// line 80 "../../../../../carshopPersistence.ump"
// line 1 "../../../../../carshopStates.ump"
// line 145 "../../../../../carshop.ump"
public class Appointment implements Serializable
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Appointment State Machines
  public enum States { Booking, AppointmentInProgress, FinalState }
  private States states;

  //Appointment Associations
  private Customer customer;
  private BookableService bookableService;
  private List<ServiceBooking> serviceBookings;
  private CarShop carShop;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Appointment(Customer aCustomer, BookableService aBookableService, CarShop aCarShop)
  {
    boolean didAddCustomer = setCustomer(aCustomer);
    if (!didAddCustomer)
    {
      throw new RuntimeException("Unable to create appointment due to customer. See http://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    boolean didAddBookableService = setBookableService(aBookableService);
    if (!didAddBookableService)
    {
      throw new RuntimeException("Unable to create appointment due to bookableService. See http://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    serviceBookings = new ArrayList<ServiceBooking>();
    boolean didAddCarShop = setCarShop(aCarShop);
    if (!didAddCarShop)
    {
      throw new RuntimeException("Unable to create appointment due to carShop. See http://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    setStates(States.Booking);
  }

  //------------------------
  // INTERFACE
  //------------------------

  public String getStatesFullName()
  {
    String answer = states.toString();
    return answer;
  }

  public States getStates()
  {
    return states;
  }

  public boolean SetMain(Service s,TimeSlot t)
  {
    boolean wasEventProcessed = false;
    
    States aStates = states;
    switch (aStates)
    {
      case Booking:
        // line 4 "../../../../../carshopStates.ump"
        setMainService(s, t);
        setStates(States.Booking);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean AddBooking(Service s,TimeSlot t)
  {
    boolean wasEventProcessed = false;
    
    States aStates = states;
    switch (aStates)
    {
      case Booking:
        // line 5 "../../../../../carshopStates.ump"
        this.addServiceBooking(s, t);
        setStates(States.Booking);
        wasEventProcessed = true;
        break;
      case AppointmentInProgress:
        // line 16 "../../../../../carshopStates.ump"
        this.addServiceBooking(s, t);
        setStates(States.AppointmentInProgress);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean UpdateDateTimes(Date d,List<Time> startTimes,List<Time> endTimes)
  {
    boolean wasEventProcessed = false;
    
    States aStates = states;
    switch (aStates)
    {
      case Booking:
        // line 6 "../../../../../carshopStates.ump"
        updateDateTimes(d, startTimes, endTimes);
        setStates(States.Booking);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean Cancel()
  {
    boolean wasEventProcessed = false;
    
    States aStates = states;
    switch (aStates)
    {
      case Booking:
        // line 7 "../../../../../carshopStates.ump"
        delete();
        setStates(States.FinalState);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean Start()
  {
    boolean wasEventProcessed = false;
    
    States aStates = states;
    switch (aStates)
    {
      case Booking:
        setStates(States.AppointmentInProgress);
        wasEventProcessed = true;
        break;
      case AppointmentInProgress:
        setStates(States.AppointmentInProgress);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean NoShow()
  {
    boolean wasEventProcessed = false;
    
    States aStates = states;
    switch (aStates)
    {
      case Booking:
        // line 11 "../../../../../carshopStates.ump"
        customer.incrNoShow(); delete();
        setStates(States.FinalState);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean End()
  {
    boolean wasEventProcessed = false;
    
    States aStates = states;
    switch (aStates)
    {
      case AppointmentInProgress:
        // line 17 "../../../../../carshopStates.ump"
        delete();
        setStates(States.FinalState);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  private void setStates(States aStates)
  {
    states = aStates;

    // entry actions and do activities
    switch(states)
    {
      case FinalState:
        delete();
        break;
    }
  }
  /* Code from template association_GetOne */
  public Customer getCustomer()
  {
    return customer;
  }
  /* Code from template association_GetOne */
  public BookableService getBookableService()
  {
    return bookableService;
  }
  /* Code from template association_GetMany */
  public ServiceBooking getServiceBooking(int index)
  {
    ServiceBooking aServiceBooking = serviceBookings.get(index);
    return aServiceBooking;
  }

  public List<ServiceBooking> getServiceBookings()
  {
    List<ServiceBooking> newServiceBookings = Collections.unmodifiableList(serviceBookings);
    return newServiceBookings;
  }

  public int numberOfServiceBookings()
  {
    int number = serviceBookings.size();
    return number;
  }

  public boolean hasServiceBookings()
  {
    boolean has = serviceBookings.size() > 0;
    return has;
  }

  public int indexOfServiceBooking(ServiceBooking aServiceBooking)
  {
    int index = serviceBookings.indexOf(aServiceBooking);
    return index;
  }
  /* Code from template association_GetOne */
  public CarShop getCarShop()
  {
    return carShop;
  }
  /* Code from template association_SetOneToMany */
  public boolean setCustomer(Customer aCustomer)
  {
    boolean wasSet = false;
    if (aCustomer == null)
    {
      return wasSet;
    }

    Customer existingCustomer = customer;
    customer = aCustomer;
    if (existingCustomer != null && !existingCustomer.equals(aCustomer))
    {
      existingCustomer.removeAppointment(this);
    }
    customer.addAppointment(this);
    wasSet = true;
    return wasSet;
  }
  /* Code from template association_SetOneToMany */
  public boolean setBookableService(BookableService aBookableService)
  {
    boolean wasSet = false;
    if (aBookableService == null)
    {
      return wasSet;
    }

    BookableService existingBookableService = bookableService;
    bookableService = aBookableService;
    if (existingBookableService != null && !existingBookableService.equals(aBookableService))
    {
      existingBookableService.removeAppointment(this);
    }
    bookableService.addAppointment(this);
    wasSet = true;
    return wasSet;
  }
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfServiceBookings()
  {
    return 0;
  }
  /* Code from template association_AddManyToOne */
  public ServiceBooking addServiceBooking(Service aService, TimeSlot aTimeSlot)
  {
    return new ServiceBooking(aService, aTimeSlot, this);
  }

  public boolean addServiceBooking(ServiceBooking aServiceBooking)
  {
    boolean wasAdded = false;
    if (serviceBookings.contains(aServiceBooking)) { return false; }
    Appointment existingAppointment = aServiceBooking.getAppointment();
    boolean isNewAppointment = existingAppointment != null && !this.equals(existingAppointment);
    if (isNewAppointment)
    {
      aServiceBooking.setAppointment(this);
    }
    else
    {
      serviceBookings.add(aServiceBooking);
    }
    wasAdded = true;
    return wasAdded;
  }

  public boolean removeServiceBooking(ServiceBooking aServiceBooking)
  {
    boolean wasRemoved = false;
    //Unable to remove aServiceBooking, as it must always have a appointment
    if (!this.equals(aServiceBooking.getAppointment()))
    {
      serviceBookings.remove(aServiceBooking);
      wasRemoved = true;
    }
    return wasRemoved;
  }
  /* Code from template association_AddIndexControlFunctions */
  public boolean addServiceBookingAt(ServiceBooking aServiceBooking, int index)
  {  
    boolean wasAdded = false;
    if(addServiceBooking(aServiceBooking))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfServiceBookings()) { index = numberOfServiceBookings() - 1; }
      serviceBookings.remove(aServiceBooking);
      serviceBookings.add(index, aServiceBooking);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveServiceBookingAt(ServiceBooking aServiceBooking, int index)
  {
    boolean wasAdded = false;
    if(serviceBookings.contains(aServiceBooking))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfServiceBookings()) { index = numberOfServiceBookings() - 1; }
      serviceBookings.remove(aServiceBooking);
      serviceBookings.add(index, aServiceBooking);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addServiceBookingAt(aServiceBooking, index);
    }
    return wasAdded;
  }
  /* Code from template association_SetOneToMany */
  public boolean setCarShop(CarShop aCarShop)
  {
    boolean wasSet = false;
    if (aCarShop == null)
    {
      return wasSet;
    }

    CarShop existingCarShop = carShop;
    carShop = aCarShop;
    if (existingCarShop != null && !existingCarShop.equals(aCarShop))
    {
      existingCarShop.removeAppointment(this);
    }
    carShop.addAppointment(this);
    wasSet = true;
    return wasSet;
  }

  public void delete()
  {
    Customer placeholderCustomer = customer;
    this.customer = null;
    if(placeholderCustomer != null)
    {
      placeholderCustomer.removeAppointment(this);
    }
    BookableService placeholderBookableService = bookableService;
    this.bookableService = null;
    if(placeholderBookableService != null)
    {
      placeholderBookableService.removeAppointment(this);
    }
    while (serviceBookings.size() > 0)
    {
      ServiceBooking aServiceBooking = serviceBookings.get(serviceBookings.size() - 1);
      aServiceBooking.delete();
      serviceBookings.remove(aServiceBooking);
    }
    
    CarShop placeholderCarShop = carShop;
    this.carShop = null;
    if(placeholderCarShop != null)
    {
      placeholderCarShop.removeAppointment(this);
    }
  }

  // line 26 "../../../../../carshopStates.ump"
   private void setMainService(Service s, TimeSlot t){
    if(bookableService instanceof Service) {
	    this.setBookableService(s);
	    if(this.getServiceBookings().size() == 0) {
	        this.addServiceBooking(s, t);
	    }
	    else {
	        ServiceBooking booking = this.getServiceBooking(0);
	        booking.setService(s);
	        booking.setTimeSlot(t);
	    }
	}
  }

  // line 40 "../../../../../carshopStates.ump"
   private void updateDateTimes(Date d, List<Time> startTimes, List<Time> endTimes){
    for(int i = 0; i < startTimes.size(); i++) {
  	  Time startTime = startTimes.get(i);
  	  Time endTime = endTimes.get(i);
  	  
  	  ServiceBooking booking = this.getServiceBooking(i);
  	  booking.setTimeSlot(new TimeSlot(d, startTime, d, endTime, carShop));
  	}
  }
  
  //------------------------
  // DEVELOPER CODE - PROVIDED AS-IS
  //------------------------
  
  // line 83 "../../../../../carshopPersistence.ump"
  private static final long serialVersionUID = 1943199919927718071L ;

  
}
/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.30.1.5099.60569f335 modeling language!*/

package ca.mcgill.ecse.carshop.controller;
import java.util.List;
import java.sql.Time;
import java.sql.Date;

// line 3 "../../../../../carshopTO.ump"
public class TOAppointment
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //TOAppointment Attributes
  private String username;
  private String name;
  private Date date;
  private List<String> services;
  private List<Time> startTimes;
  private List<Time> endTimes;
  private boolean isComboApp;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public TOAppointment(String aUsername, String aName, Date aDate, List<String> aServices, List<Time> aStartTimes, List<Time> aEndTimes, boolean aIsComboApp)
  {
    username = aUsername;
    name = aName;
    date = aDate;
    services = aServices;
    startTimes = aStartTimes;
    endTimes = aEndTimes;
    isComboApp = aIsComboApp;
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setUsername(String aUsername)
  {
    boolean wasSet = false;
    username = aUsername;
    wasSet = true;
    return wasSet;
  }

  public boolean setName(String aName)
  {
    boolean wasSet = false;
    name = aName;
    wasSet = true;
    return wasSet;
  }

  public boolean setDate(Date aDate)
  {
    boolean wasSet = false;
    date = aDate;
    wasSet = true;
    return wasSet;
  }

  public boolean setServices(List<String> aServices)
  {
    boolean wasSet = false;
    services = aServices;
    wasSet = true;
    return wasSet;
  }

  public boolean setStartTimes(List<Time> aStartTimes)
  {
    boolean wasSet = false;
    startTimes = aStartTimes;
    wasSet = true;
    return wasSet;
  }

  public boolean setEndTimes(List<Time> aEndTimes)
  {
    boolean wasSet = false;
    endTimes = aEndTimes;
    wasSet = true;
    return wasSet;
  }

  public boolean setIsComboApp(boolean aIsComboApp)
  {
    boolean wasSet = false;
    isComboApp = aIsComboApp;
    wasSet = true;
    return wasSet;
  }

  public String getUsername()
  {
    return username;
  }

  public String getName()
  {
    return name;
  }

  public Date getDate()
  {
    return date;
  }

  public List<String> getServices()
  {
    return services;
  }

  public List<Time> getStartTimes()
  {
    return startTimes;
  }

  public List<Time> getEndTimes()
  {
    return endTimes;
  }

  public boolean getIsComboApp()
  {
    return isComboApp;
  }
  /* Code from template attribute_IsBoolean */
  public boolean isIsComboApp()
  {
    return isComboApp;
  }

  public void delete()
  {}


  public String toString()
  {
    return super.toString() + "["+
            "username" + ":" + getUsername()+ "," +
            "name" + ":" + getName()+ "," +
            "isComboApp" + ":" + getIsComboApp()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "date" + "=" + (getDate() != null ? !getDate().equals(this)  ? getDate().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "services" + "=" + (getServices() != null ? !getServices().equals(this)  ? getServices().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "startTimes" + "=" + (getStartTimes() != null ? !getStartTimes().equals(this)  ? getStartTimes().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "endTimes" + "=" + (getEndTimes() != null ? !getEndTimes().equals(this)  ? getEndTimes().toString().replaceAll("  ","    ") : "this" : "null");
  }
}
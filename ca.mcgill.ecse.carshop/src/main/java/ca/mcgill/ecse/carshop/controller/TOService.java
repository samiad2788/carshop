/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.30.1.5099.60569f335 modeling language!*/

package ca.mcgill.ecse.carshop.controller;

// line 36 "../../../../../carshopTO.ump"
public class TOService
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //TOService Attributes
  private String name;
  private int duration;
  private TOGarage garage;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public TOService(String aName, int aDuration, TOGarage aGarage)
  {
    name = aName;
    duration = aDuration;
    garage = aGarage;
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setName(String aName)
  {
    boolean wasSet = false;
    name = aName;
    wasSet = true;
    return wasSet;
  }

  public boolean setDuration(int aDuration)
  {
    boolean wasSet = false;
    duration = aDuration;
    wasSet = true;
    return wasSet;
  }

  public boolean setGarage(TOGarage aGarage)
  {
    boolean wasSet = false;
    garage = aGarage;
    wasSet = true;
    return wasSet;
  }

  public String getName()
  {
    return name;
  }

  public int getDuration()
  {
    return duration;
  }

  public TOGarage getGarage()
  {
    return garage;
  }

  public void delete()
  {}


  public String toString()
  {
    return super.toString() + "["+
            "name" + ":" + getName()+ "," +
            "duration" + ":" + getDuration()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "garage" + "=" + (getGarage() != null ? !getGarage().equals(this)  ? getGarage().toString().replaceAll("  ","    ") : "this" : "null");
  }
}
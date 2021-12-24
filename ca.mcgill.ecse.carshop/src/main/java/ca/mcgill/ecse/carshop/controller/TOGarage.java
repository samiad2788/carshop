/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.30.1.5099.60569f335 modeling language!*/

package ca.mcgill.ecse.carshop.controller;

// line 52 "../../../../../carshopTO.ump"
public class TOGarage
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //TOGarage Attributes
  private String technicianUsername;
  private String technicianType;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public TOGarage(String aTechnicianUsername, String aTechnicianType)
  {
    technicianUsername = aTechnicianUsername;
    technicianType = aTechnicianType;
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setTechnicianUsername(String aTechnicianUsername)
  {
    boolean wasSet = false;
    technicianUsername = aTechnicianUsername;
    wasSet = true;
    return wasSet;
  }

  public boolean setTechnicianType(String aTechnicianType)
  {
    boolean wasSet = false;
    technicianType = aTechnicianType;
    wasSet = true;
    return wasSet;
  }

  public String getTechnicianUsername()
  {
    return technicianUsername;
  }

  public String getTechnicianType()
  {
    return technicianType;
  }

  public void delete()
  {}


  public String toString()
  {
    return super.toString() + "["+
            "technicianUsername" + ":" + getTechnicianUsername()+ "," +
            "technicianType" + ":" + getTechnicianType()+ "]";
  }
}
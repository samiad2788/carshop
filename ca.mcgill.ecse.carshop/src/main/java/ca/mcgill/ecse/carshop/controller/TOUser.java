/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.30.1.5099.60569f335 modeling language!*/

package ca.mcgill.ecse.carshop.controller;

// line 22 "../../../../../carshopTO.ump"
public class TOUser
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //TOUser Attributes
  private String username;
  private boolean isOwner;
  private boolean isCustomer;
  private boolean isTechnician;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public TOUser(String aUsername, boolean aIsOwner, boolean aIsCustomer, boolean aIsTechnician)
  {
    username = aUsername;
    isOwner = aIsOwner;
    isCustomer = aIsCustomer;
    isTechnician = aIsTechnician;
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

  public boolean setIsOwner(boolean aIsOwner)
  {
    boolean wasSet = false;
    isOwner = aIsOwner;
    wasSet = true;
    return wasSet;
  }

  public boolean setIsCustomer(boolean aIsCustomer)
  {
    boolean wasSet = false;
    isCustomer = aIsCustomer;
    wasSet = true;
    return wasSet;
  }

  public boolean setIsTechnician(boolean aIsTechnician)
  {
    boolean wasSet = false;
    isTechnician = aIsTechnician;
    wasSet = true;
    return wasSet;
  }

  public String getUsername()
  {
    return username;
  }

  public boolean getIsOwner()
  {
    return isOwner;
  }

  public boolean getIsCustomer()
  {
    return isCustomer;
  }

  public boolean getIsTechnician()
  {
    return isTechnician;
  }
  /* Code from template attribute_IsBoolean */
  public boolean isIsOwner()
  {
    return isOwner;
  }
  /* Code from template attribute_IsBoolean */
  public boolean isIsCustomer()
  {
    return isCustomer;
  }
  /* Code from template attribute_IsBoolean */
  public boolean isIsTechnician()
  {
    return isTechnician;
  }

  public void delete()
  {}


  public String toString()
  {
    return super.toString() + "["+
            "username" + ":" + getUsername()+ "," +
            "isOwner" + ":" + getIsOwner()+ "," +
            "isCustomer" + ":" + getIsCustomer()+ "," +
            "isTechnician" + ":" + getIsTechnician()+ "]";
  }
}
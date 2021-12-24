/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.30.1.5099.60569f335 modeling language!*/

package ca.mcgill.ecse.carshop.controller;

// line 29 "../../../../../carshopTO.ump"
public class TOBusiness
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //TOBusiness Attributes
  private String name;
  private String address;
  private String email;
  private String phoneNumber;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public TOBusiness(String aName, String aAddress, String aEmail, String aPhoneNumber)
  {
    name = aName;
    address = aAddress;
    email = aEmail;
    phoneNumber = aPhoneNumber;
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

  public boolean setAddress(String aAddress)
  {
    boolean wasSet = false;
    address = aAddress;
    wasSet = true;
    return wasSet;
  }

  public boolean setEmail(String aEmail)
  {
    boolean wasSet = false;
    email = aEmail;
    wasSet = true;
    return wasSet;
  }

  public boolean setPhoneNumber(String aPhoneNumber)
  {
    boolean wasSet = false;
    phoneNumber = aPhoneNumber;
    wasSet = true;
    return wasSet;
  }

  public String getName()
  {
    return name;
  }

  public String getAddress()
  {
    return address;
  }

  public String getEmail()
  {
    return email;
  }

  public String getPhoneNumber()
  {
    return phoneNumber;
  }

  public void delete()
  {}


  public String toString()
  {
    return super.toString() + "["+
            "name" + ":" + getName()+ "," +
            "address" + ":" + getAddress()+ "," +
            "email" + ":" + getEmail()+ "," +
            "phoneNumber" + ":" + getPhoneNumber()+ "]";
  }
}
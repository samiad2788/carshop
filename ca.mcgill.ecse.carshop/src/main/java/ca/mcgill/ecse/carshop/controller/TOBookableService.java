/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.30.1.5099.60569f335 modeling language!*/

package ca.mcgill.ecse.carshop.controller;

// line 17 "../../../../../carshopTO.ump"
public class TOBookableService
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //TOBookableService Attributes
  private String name;
  private boolean isCombo;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public TOBookableService(String aName, boolean aIsCombo)
  {
    name = aName;
    isCombo = aIsCombo;
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

  public boolean setIsCombo(boolean aIsCombo)
  {
    boolean wasSet = false;
    isCombo = aIsCombo;
    wasSet = true;
    return wasSet;
  }

  public String getName()
  {
    return name;
  }

  public boolean getIsCombo()
  {
    return isCombo;
  }
  /* Code from template attribute_IsBoolean */
  public boolean isIsCombo()
  {
    return isCombo;
  }

  public void delete()
  {}


  public String toString()
  {
    return super.toString() + "["+
            "name" + ":" + getName()+ "," +
            "isCombo" + ":" + getIsCombo()+ "]";
  }
}
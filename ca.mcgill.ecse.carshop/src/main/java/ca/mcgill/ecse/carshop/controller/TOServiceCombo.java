/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.30.1.5099.60569f335 modeling language!*/

package ca.mcgill.ecse.carshop.controller;
import java.util.List;

// line 42 "../../../../../carshopTO.ump"
public class TOServiceCombo
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //TOServiceCombo Attributes
  private String name;
  private String main;
  private List<String> required;
  private List<String> optional;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public TOServiceCombo(String aName, String aMain, List<String> aRequired, List<String> aOptional)
  {
    name = aName;
    main = aMain;
    required = aRequired;
    optional = aOptional;
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

  public boolean setMain(String aMain)
  {
    boolean wasSet = false;
    main = aMain;
    wasSet = true;
    return wasSet;
  }

  public boolean setRequired(List<String> aRequired)
  {
    boolean wasSet = false;
    required = aRequired;
    wasSet = true;
    return wasSet;
  }

  public boolean setOptional(List<String> aOptional)
  {
    boolean wasSet = false;
    optional = aOptional;
    wasSet = true;
    return wasSet;
  }

  public String getName()
  {
    return name;
  }

  public String getMain()
  {
    return main;
  }

  public List<String> getRequired()
  {
    return required;
  }

  public List<String> getOptional()
  {
    return optional;
  }

  public void delete()
  {}


  public String toString()
  {
    return super.toString() + "["+
            "name" + ":" + getName()+ "," +
            "main" + ":" + getMain()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "required" + "=" + (getRequired() != null ? !getRequired().equals(this)  ? getRequired().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "optional" + "=" + (getOptional() != null ? !getOptional().equals(this)  ? getOptional().toString().replaceAll("  ","    ") : "this" : "null");
  }
}
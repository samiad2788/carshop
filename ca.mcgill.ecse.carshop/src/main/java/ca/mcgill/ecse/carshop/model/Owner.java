/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.30.1.5099.60569f335 modeling language!*/

package ca.mcgill.ecse.carshop.model;
import java.io.Serializable;
import java.util.*;

// line 15 "../../../../../carshopPersistence.ump"
// line 44 "../../../../../carshop.ump"
public class Owner extends User implements Serializable
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Owner Associations
  private CarShop carShop;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Owner(String aUsername, String aPassword, CarShop aCarShop)
  {
    super(aUsername, aPassword);
    // line 49 "../../../../../carshop.ump"
    if(!aUsername.equals("owner")) {
      		throw new RuntimeException("Owner account must have 'owner' username");
      	}
    // END OF UMPLE BEFORE INJECTION
    boolean didAddCarShop = setCarShop(aCarShop);
    if (!didAddCarShop)
    {
      throw new RuntimeException("Unable to create owner due to carShop. See http://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
  }

  //------------------------
  // INTERFACE
  //------------------------
  /* Code from template attribute_Set_subclass */
  public boolean setUsername(String aUsername)
  {
    boolean wasSet = false;
    // line 49 "../../../../../carshop.ump"
    if(!aUsername.equals("owner")) {
      		throw new RuntimeException("Owner account must have 'owner' username");
      	}
    // END OF UMPLE BEFORE INJECTION
      wasSet = super.setUsername(aUsername);
    return wasSet;
  }
  /* Code from template association_GetOne */
  public CarShop getCarShop()
  {
    return carShop;
  }
  /* Code from template association_SetOneToOptionalOne */
  public boolean setCarShop(CarShop aNewCarShop)
  {
    boolean wasSet = false;
    if (aNewCarShop == null)
    {
      //Unable to setCarShop to null, as owner must always be associated to a carShop
      return wasSet;
    }
    
    Owner existingOwner = aNewCarShop.getOwner();
    if (existingOwner != null && !equals(existingOwner))
    {
      //Unable to setCarShop, the current carShop already has a owner, which would be orphaned if it were re-assigned
      return wasSet;
    }
    
    CarShop anOldCarShop = carShop;
    carShop = aNewCarShop;
    carShop.setOwner(this);

    if (anOldCarShop != null)
    {
      anOldCarShop.setOwner(null);
    }
    wasSet = true;
    return wasSet;
  }

  public void delete()
  {
    CarShop existingCarShop = carShop;
    carShop = null;
    if (existingCarShop != null)
    {
      existingCarShop.setOwner(null);
    }
    super.delete();
  }
  
  //------------------------
  // DEVELOPER CODE - PROVIDED AS-IS
  //------------------------
  
  // line 18 "../../../../../carshopPersistence.ump"
  private static final long serialVersionUID = 5643193616927798071L ;

  
}
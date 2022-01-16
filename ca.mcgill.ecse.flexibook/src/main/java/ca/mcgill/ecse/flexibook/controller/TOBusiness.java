/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.30.1.5099.60569f335 modeling language!*/

package ca.mcgill.ecse.flexibook.controller;

// line 3 "../../../../../TransferObject.ump"
public class TOBusiness
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //TOBusiness Attributes
  private String name;
  private String phoneNumber;
  private String address;
  private String email;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public TOBusiness(String aName, String aPhoneNumber, String aAddress, String aEmail)
  {
    name = aName;
    phoneNumber = aPhoneNumber;
    address = aAddress;
    email = aEmail;
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

  public boolean setPhoneNumber(String aPhoneNumber)
  {
    boolean wasSet = false;
    phoneNumber = aPhoneNumber;
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

  public String getName()
  {
    return name;
  }

  public String getPhoneNumber()
  {
    return phoneNumber;
  }

  public String getAddress()
  {
    return address;
  }

  public String getEmail()
  {
    return email;
  }

  public void delete()
  {}


  public String toString()
  {
    return super.toString() + "["+
            "name" + ":" + getName()+ "," +
            "phoneNumber" + ":" + getPhoneNumber()+ "," +
            "address" + ":" + getAddress()+ "," +
            "email" + ":" + getEmail()+ "]";
  }
}
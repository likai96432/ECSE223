/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.30.1.5099.60569f335 modeling language!*/

package ca.mcgill.ecse.flexibook.controller;

// line 34 "../../../../../TransferObject.ump"
public class TOAppointment
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //TOAppointment Attributes
  private String customer;
  private String bookableServiceName;
  private String time;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public TOAppointment(String aCustomer, String aBookableServiceName, String aTime)
  {
    customer = aCustomer;
    bookableServiceName = aBookableServiceName;
    time = aTime;
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setCustomer(String aCustomer)
  {
    boolean wasSet = false;
    customer = aCustomer;
    wasSet = true;
    return wasSet;
  }

  public boolean setBookableServiceName(String aBookableServiceName)
  {
    boolean wasSet = false;
    bookableServiceName = aBookableServiceName;
    wasSet = true;
    return wasSet;
  }

  public boolean setTime(String aTime)
  {
    boolean wasSet = false;
    time = aTime;
    wasSet = true;
    return wasSet;
  }

  public String getCustomer()
  {
    return customer;
  }

  public String getBookableServiceName()
  {
    return bookableServiceName;
  }

  public String getTime()
  {
    return time;
  }

  public void delete()
  {}


  public String toString()
  {
    return super.toString() + "["+
            "customer" + ":" + getCustomer()+ "," +
            "bookableServiceName" + ":" + getBookableServiceName()+ "," +
            "time" + ":" + getTime()+ "]";
  }
}
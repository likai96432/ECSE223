/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.30.1.5099.60569f335 modeling language!*/

package ca.mcgill.ecse.flexibook.controller;

// line 28 "../../../../../TransferObject.ump"
public class TOCustomer
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //TOCustomer Attributes
  private String username;
  private String password;
  private int gugutime;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public TOCustomer(String aUsername, String aPassword, int aGugutime)
  {
    username = aUsername;
    password = aPassword;
    gugutime = aGugutime;
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

  public boolean setPassword(String aPassword)
  {
    boolean wasSet = false;
    password = aPassword;
    wasSet = true;
    return wasSet;
  }

  public boolean setGugutime(int aGugutime)
  {
    boolean wasSet = false;
    gugutime = aGugutime;
    wasSet = true;
    return wasSet;
  }

  public String getUsername()
  {
    return username;
  }

  public String getPassword()
  {
    return password;
  }

  public int getGugutime()
  {
    return gugutime;
  }

  public void delete()
  {}


  public String toString()
  {
    return super.toString() + "["+
            "username" + ":" + getUsername()+ "," +
            "password" + ":" + getPassword()+ "," +
            "gugutime" + ":" + getGugutime()+ "]";
  }
}

package ca.mcgill.ecse.flexibook.controller;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.sql.Date;
import java.util.*;

// Local imports
import ca.mcgill.ecse.flexibook.application.FlexiBookApplication;
import ca.mcgill.ecse.flexibook.model.Appointment;
import ca.mcgill.ecse.flexibook.model.BookableService;
import ca.mcgill.ecse.flexibook.model.Business;
import ca.mcgill.ecse.flexibook.model.BusinessHour;
import ca.mcgill.ecse.flexibook.model.ComboItem;
import ca.mcgill.ecse.flexibook.model.Customer;
import ca.mcgill.ecse.flexibook.model.FlexiBook;
import ca.mcgill.ecse.flexibook.model.Owner;
import ca.mcgill.ecse.flexibook.model.Service;
import ca.mcgill.ecse.flexibook.model.ServiceCombo;
import ca.mcgill.ecse.flexibook.model.TimeSlot;
import ca.mcgill.ecse.flexibook.model.User;
import ca.mcgill.ecse.flexibook.persistence.FlexiBookPersistence;

// Java imports
import java.util.regex.*;

public class FlexiBookController implements FlexiBookControllerInterface {

	// ----------------------------------- Kevin Li
	// -------------------------------------------------------

	/**
	 * @throws InvalidInputException
	 * @throws Exception
	 * @author Kevin Li
	 */
	public static void registerNoShow(Date startDate, Time startTime) throws Exception {
		FlexiBook fb = FlexiBookApplication.getFlexiBook();
		int gugutime = findAppbyTime(startDate, startTime).getCustomer().getGugutime();

		findAppbyTime(startDate, startTime).registerNoShow(FlexiBookApplication.getCurrentDate(),
				FlexiBookApplication.getCurrentTime());
		if (gugutime == findAppbyTime(startDate, startTime).getCustomer().getGugutime()) {
			throw new InvalidInputException("Can not register noshow");
		}
		FlexiBookPersistence.save(fb);
	}

	/**
	 * acess customers by transfer objects author kevinli
	 *
	 * @return customers
	 */

	public static List<TOCustomer> getCustomer() {
		ArrayList<TOCustomer> customers = new ArrayList<TOCustomer>();
		for (Customer c : FlexiBookApplication.getFlexiBook().getCustomers()) {
			if (c instanceof Customer) {
				TOCustomer tocustomer = new TOCustomer(c.getUsername(), c.getPassword(), c.getGugutime());
				customers.add(tocustomer);
			}
		}
		return customers;
	}

	/**
	 * acess Service by transfer objects
	 * 
	 * @return service
	 * @author kevinli
	 */
	public static List<TOService> getService() {
		ArrayList<TOService> service = new ArrayList<TOService>();
		for (BookableService s : FlexiBookApplication.getFlexiBook().getBookableServices()) {
			if (s instanceof Service) {

				TOService tos = new TOService(((Service) s).getName(), ((Service) s).getDuration(),
						((Service) s).getDowntimeStart(), ((Service) s).getDowntimeDuration());
				service.add(tos);
			}
		}
		return service;
	}

	/**
	 * access appointments by transfer objects but limited by current user
	 * 
	 * @return app
	 * @author kevin li
	 */
	public static List<TOAppointment> getapps() {

		ArrayList<TOAppointment> app = new ArrayList<TOAppointment>();
		String username = FlexiBookApplication.getCurrentUser().getUsername();

		for (Appointment a : FlexiBookApplication.getFlexiBook().getAppointments()) {
			if (username.equals("owner")) {
				String[] date = a.getTimeSlot().getStartDate().toString().split("-");
				String d = date[1] + "-" + date[2];
				String time = d + " " + changeForm(a.getTimeSlot().getStartTime().toString()) + "-"
						+ changeForm(a.getTimeSlot().getEndTime().toString());

				TOAppointment ap = new TOAppointment(a.getCustomer().getUsername(), a.getBookableService().getName(),
						time);
				app.add(ap);
			} else {
				String[] date = a.getTimeSlot().getStartDate().toString().split("-");
				String d = date[1] + "-" + date[2];
				String time = d + " " + changeForm(a.getTimeSlot().getStartTime().toString()) + "-"
						+ changeForm(a.getTimeSlot().getEndTime().toString());
				if (a.getCustomer().getUsername().contentEquals(username)) {
					TOAppointment ap = new TOAppointment(a.getCustomer().getUsername(),
							a.getBookableService().getName(), time);
					app.add(ap);
				}
			}

		}
		return app;
	}

	/**
	 * helper method for change forms
	 * 
	 * @param s
	 * @return result
	 */
	private static String changeForm(String s) {
		String result = "";
		String[] a = s.split(":");
		result = a[0] + ":" + a[1];

		return result;
	}

	/**
	 * acess owner by transfer objects author kevinli
	 *
	 * @return toowner
	 */

	public static TOOwner getowner() {

		TOOwner toowner = new TOOwner(FlexiBookApplication.getFlexiBook().getOwner().getUsername(),
				FlexiBookApplication.getFlexiBook().getOwner().getPassword());

		return toowner;
	}

	/**
	 * Create a new customer account for a user
	 *
	 * @param username
	 * @param password
	 * @throws InvalidInputException
	 * @author kevinli
	 */

	public static void signUpCustomerAccount(String username, String password) throws InvalidInputException {
		String error = "";
		User user = FlexiBookApplication.getCurrentUser();
		if (FlexiBookApplication.getCurrentUser() != null
				&& FlexiBookApplication.getCurrentUser().getUsername().equals("owner")) {
			error = "You must log out of the owner account before creating a customer account";
		}

		if (username.length() == 0 || username == null) {
			error = "The user name cannot be empty";
		}
		if (password.length() == 0 || password == null) {
			error = "The password cannot be empty";
		}
		if (error.length() > 0) {
			throw new InvalidInputException(error.trim());
		}

		FlexiBook flexibook = FlexiBookApplication.getFlexiBook();
		try {
			// Customer a=new Customer(username,password, flexibook);
			flexibook.addCustomer(username, password);
			FlexiBookPersistence.save(flexibook);
			// FlexiBookApplication.setCurrentUser(a);
		} catch (RuntimeException e) {
			error = e.getMessage();
			if (error.equals(
					"Cannot create due to duplicate username. See http://manual.umple.org?RE003ViolationofUniqueness.html")) {
				error = "The username already exists";
			}
			throw new InvalidInputException(error);
		}
	}

	/**
	 * Update the user name and password for an existing account
	 *
	 * @param newname
	 * @param password
	 * @throws InvalidInputException
	 * @author kevin li
	 */
	public static void updateAccount(String newname, String password) throws InvalidInputException {
		User user = FlexiBookApplication.getCurrentUser();
		FlexiBook flexibook = FlexiBookApplication.getFlexiBook();
		String error = "";
		if (user != null && (user.getUsername().equals("owner") && (newname.equals("owner") == false))) {
			error = "Changing username of owner is not allowed";
		}
		if (newname.length() == 0) {
			error = "The user name cannot be empty";
		}
		if (password.length() == 0) {
			error = "The password cannot be empty";
		}
		if (error.length() > 0) {
			throw new InvalidInputException(error.trim());
		}
		try {
			if (user.getUsername().equals("owner") && error.length() == 0) {
				user.setPassword(password);
				FlexiBookPersistence.save(flexibook);
			} else {

				boolean a = user.setUsername(newname);
				if (a == false) {
					throw new InvalidInputException("Username not available ");
				} else
					user.setPassword(password);
			}
		} catch (RuntimeException e) {
			throw new InvalidInputException(e.getMessage());
		}

	}

	/**
	 * Delete a customer account
	 *
	 * @param target
	 * @throws InvalidInputException
	 * @author kevin li
	 */
	public static void deleteCustomerAccount(String target) throws InvalidInputException {
		String error = "";
		FlexiBook flexibook = FlexiBookApplication.getFlexiBook();

		User user = FlexiBookApplication.getCurrentUser();
		if (user != null && (user.getUsername().equals("owner") || user.getUsername().equals(target) == false)) {
			error = "You do not have permission to delete this account";
		}
		if (target.length() == 0 || target == null) {
			error = "comeon, do I need to teach you how to do this";
		}
		if (error.length() > 0) {
			throw new InvalidInputException(error.trim());
		}
		try {

			user.delete();

			FlexiBookApplication.setCurrentUser(null);
			FlexiBookPersistence.save(flexibook);

		} catch (RuntimeException e) {
			throw new InvalidInputException(e.getMessage());
		}

	}

	// ----------------------------------- Ing Tian
	// -------------------------------------------------------

	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{1,6}$",
			Pattern.CASE_INSENSITIVE);

	/**
	 * Creates a new Business in the system with provided basic information. Email
	 * must be of a valid form.
	 *
	 * @param newBusiness
	 * @throws InvalidInputException
	 * @author Ing Tian
	 */
	public static void setupBasicBusinessInformation(TOBusiness newBusiness) throws InvalidInputException {
		try {
			String name = newBusiness.getName(), address = newBusiness.getAddress(),
					phoneNumber = newBusiness.getPhoneNumber(), email = newBusiness.getEmail();

			if (name == null || name.equals("") || email == null || email.equals("") || phoneNumber == null
					|| phoneNumber.equals("") || address == null || address.equals(""))
				throw new Exception("Input string cannot be null or empty.");

			/*
			 * Input Validation
			 */
			User user = FlexiBookApplication.getCurrentUser();
			if (!(user instanceof Owner))
				throw new Exception("No permission to set up business information");
			if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).find())
				throw new Exception("Invalid email");

			/*
			 * Modification
			 */
			FlexiBook app = FlexiBookApplication.getFlexiBook();
			Business entity = new Business(name, address, phoneNumber, email, app);
			app.setBusiness(entity);
			FlexiBookPersistence.save(app);
		} catch (Exception e) {
			throw new InvalidInputException(e.getMessage());
		}
	}

	/**
	 * Add a new business hour to the existing business. The new business hour
	 * cannot overlap with existing ones.
	 *
	 * @param dayOfWeek
	 * @param startTime
	 * @param endTime
	 * @throws InvalidInputException
	 * @author Ing Tian
	 */
	public static void addNewBusinessHours(int dayOfWeek, Time startTime, Time endTime) throws InvalidInputException {
		try {
			/*
			 * Input Validation
			 */

			if (startTime.equals(endTime) && startTime.equals(java.sql.Time.valueOf("00:00:00")))
				endTime = java.sql.Time.valueOf("24:00:00");

			User user = FlexiBookApplication.getCurrentUser();
			if (!(user instanceof Owner))
				throw new Exception("No permission to update business information");
			if (!startTime.before(endTime))
				throw new Exception("Start time must be before end time");
			if (dayOfWeek < 1 || dayOfWeek > 7)
				throw new Exception("Day of week must be between 1 and 7");

			/*
			 * Modification
			 */
			BusinessHour.DayOfWeek dayOW = parseDayOfWeek(dayOfWeek);
			Business business = FlexiBookApplication.getFlexiBook().getBusiness();
			if (business.numberOfBusinessHours() != 0) {
				for (BusinessHour businessHour : business.getBusinessHours()) {
					if (businessHour.getDayOfWeek() == dayOW) {
						Time eleStartTime = businessHour.getStartTime(), eleEndTime = businessHour.getEndTime();
						if ((startTime.before(eleStartTime) && endTime.after(eleStartTime))
								|| (startTime.after(eleStartTime) && startTime.before(eleEndTime)))
							throw new Exception("The business hours cannot overlap");
					}
				}
			}

			FlexiBook app = FlexiBookApplication.getFlexiBook();
			BusinessHour newBusinessHour = new BusinessHour(dayOW, startTime, endTime,
					FlexiBookApplication.getFlexiBook());
			business.addBusinessHour(newBusinessHour);
			FlexiBookPersistence.save(app);
		} catch (Exception e) {
			throw new InvalidInputException(e.getMessage());
		}
	}

	/**
	 * @return
	 * @throws Exception
	 * @author Ing Tian
	 */
	public static List<TOBusinessHour> getBusinessHours() throws Exception {
		List<TOBusinessHour> result = new ArrayList<>();
		try {
			FlexiBook fb = FlexiBookApplication.getFlexiBook();
			List<BusinessHour> businessHours = fb.getBusiness().getBusinessHours();
			for (BusinessHour businessHour : businessHours) {
				BusinessHour.DayOfWeek dayOfWeek = businessHour.getDayOfWeek();
				int dayOW = 0;
				if (dayOfWeek.equals(BusinessHour.DayOfWeek.Monday))
					dayOW = 1;
				if (dayOfWeek.equals(BusinessHour.DayOfWeek.Tuesday))
					dayOW = 2;
				if (dayOfWeek.equals(BusinessHour.DayOfWeek.Wednesday))
					dayOW = 3;
				if (dayOfWeek.equals(BusinessHour.DayOfWeek.Thursday))
					dayOW = 4;
				if (dayOfWeek.equals(BusinessHour.DayOfWeek.Friday))
					dayOW = 5;
				if (dayOfWeek.equals(BusinessHour.DayOfWeek.Saturday))
					dayOW = 6;
				if (dayOfWeek.equals(BusinessHour.DayOfWeek.Sunday))
					dayOW = 7;
				result.add(new TOBusinessHour(dayOW, businessHour.getStartTime(), businessHour.getEndTime()));
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return result;
	}

	/**
	 * Update address, phone number, email, and name of a business. Email must be of
	 * a valid form.
	 *
	 * @param businessInfo
	 * @author Ing Tian
	 */
	public static void updateBasicBusinessInformation(TOBusiness businessInfo) throws InvalidInputException {
		try {

			String newName = businessInfo.getName(), newEmail = businessInfo.getEmail(),
					newPhoneNumber = businessInfo.getPhoneNumber(), newAddress = businessInfo.getAddress();

			if (newName == null || newName.equals("") || newEmail == null || newEmail.equals("")
					|| newPhoneNumber == null || newPhoneNumber.equals("") || newAddress == null
					|| newAddress.equals(""))
				throw new Exception("Input string cannot be null or empty.");

			/*
			 * Input validation
			 */
			User user = FlexiBookApplication.getCurrentUser();
			if (!(user instanceof Owner))
				throw new Exception("No permission to update business information");
			if (!VALID_EMAIL_ADDRESS_REGEX.matcher(newEmail).find())
				throw new Exception("Invalid email");

			/*
			 * Modification
			 */
			FlexiBook app = FlexiBookApplication.getFlexiBook();
			Business entity = app.getBusiness();
			entity.setName(newName);
			entity.setAddress(newAddress);
			entity.setEmail(newEmail);
			entity.setPhoneNumber(newPhoneNumber);
			FlexiBookPersistence.save(app);
		} catch (Exception e) {
			throw new InvalidInputException(e.getMessage());
		}
	}

	/**
	 * Updating an exisiting business hour to the specified time slot. New business
	 * hour cannot overlap with existing ones.
	 *
	 * @param dayOfWeek
	 * @param time
	 * @param targetDayOfWeek
	 * @param timeSlot
	 * @throws InvalidInputException
	 * @author Ing Tian
	 */
	public static void updateBusinessHour(int dayOfWeek, Time time, int targetDayOfWeek, TOTimeSlot timeSlot)
			throws InvalidInputException {
		try {

			Time targetStartTime = timeSlot.getStartTime(), targetEndTime = timeSlot.getEndTime();
			if ((dayOfWeek < 1 && dayOfWeek > 7) || (time == null) || (targetDayOfWeek < 1 && targetDayOfWeek > 7)
					|| (timeSlot == null))
				throw new Exception("Input data cannot be null or empty. Day of week must be of [1,7].");

			/*
			 * Input validation
			 */
			User user = FlexiBookApplication.getCurrentUser();
			if (!(user instanceof Owner))
				throw new Exception("No permission to update business information");

			if (targetStartTime.after(targetEndTime))
				throw new Exception("Start time must be before end time");

			/*
			 * Modification
			 */
			FlexiBook fb = FlexiBookApplication.getFlexiBook();
			Business business = fb.getBusiness();
			BusinessHour businessHour = null;
			BusinessHour.DayOfWeek dayOW = parseDayOfWeek(dayOfWeek), targetDayOW = parseDayOfWeek(targetDayOfWeek);

			// Checking for overlaps between the new time slot and existing ones.
			for (BusinessHour hour : business.getBusinessHours()) {
				if (hour.getDayOfWeek() == dayOW && hour.getStartTime().equals(time))
					businessHour = hour;
				if (hour.getDayOfWeek() == targetDayOW && hour != businessHour)
					if ((targetStartTime.before(hour.getStartTime()) && targetEndTime.after(hour.getStartTime()))
							|| (targetStartTime.after(hour.getStartTime())
									&& targetStartTime.before(hour.getEndTime())))
						throw new Exception("The business hours cannot overlap");
			}

			if (businessHour == null)
				throw new Exception("Existing business hour cannot be found");

			businessHour.setDayOfWeek(targetDayOW);
			businessHour.setStartTime(targetStartTime);
			businessHour.setEndTime(targetEndTime);
			FlexiBookPersistence.save(fb);
		} catch (Exception e) {
			throw new InvalidInputException(e.getMessage());
		}
	}

	/**
	 * Remove an existing business hour.
	 *
	 * @param dayOfWeek
	 * @param startTime
	 * @author Ing Tian
	 */
	public static void removeExisitingBusinessHour(int dayOfWeek, Time startTime) throws InvalidInputException {
		try {

			/*
			 * Input validation
			 */
			User user = FlexiBookApplication.getCurrentUser();
			if (!(user instanceof Owner))
				throw new Exception("No permission to update business information");

			/*
			 * Modification
			 */
			BusinessHour.DayOfWeek dayOW;
			switch (dayOfWeek) {
			case 1:
				dayOW = BusinessHour.DayOfWeek.Monday;
				break;
			case 2:
				dayOW = BusinessHour.DayOfWeek.Tuesday;
				break;
			case 3:
				dayOW = BusinessHour.DayOfWeek.Wednesday;
				break;
			case 4:
				dayOW = BusinessHour.DayOfWeek.Thursday;
				break;
			case 5:
				dayOW = BusinessHour.DayOfWeek.Friday;
				break;
			case 6:
				dayOW = BusinessHour.DayOfWeek.Saturday;
				break;
			case 7:
				dayOW = BusinessHour.DayOfWeek.Sunday;
				break;
			default:
				dayOW = BusinessHour.DayOfWeek.Monday;
				break;
			}

			FlexiBook fb = FlexiBookApplication.getFlexiBook();
			Business business = fb.getBusiness();
			// Removes the matched business hour.
			for (int i = 0; i < business.getBusinessHours().size(); i++) {
				BusinessHour cur = business.getBusinessHour(i);
				if (dayOW.compareTo(cur.getDayOfWeek()) < 0 || (dayOW.compareTo(cur.getDayOfWeek()) == 0
						&& (startTime.before(cur.getStartTime()) || startTime.equals(cur.getStartTime()))))
					business.removeBusinessHour(cur);
			}
			FlexiBookPersistence.save(fb);

		} catch (Exception e) {
			throw new InvalidInputException(e.getMessage());
		}
	}

	/**
	 * Update an existing vacation or holiday time slot to a new time slot. The new
	 * time slot cannot overlap with existing ones.
	 *
	 * @param type
	 * @param date
	 * @param time
	 * @param newTimeSlot
	 * @author Ing Tian
	 */
	public static void updateTimeSlotInformation(String type, Date date, Time time, TOTimeSlot newTimeSlot)
			throws InvalidInputException {
		try {

			Time targetStartTime = newTimeSlot.getStartTime(), targetEndTime = newTimeSlot.getEndTime();
			Date targetStartDate = newTimeSlot.getStartDate(), targetEndDate = newTimeSlot.getEndDate();

			User user = FlexiBookApplication.getCurrentUser();
			if (!(user instanceof Owner))
				throw new Exception("No permission to update business information");

			if (!targetStartDate.before(targetEndDate)) {
				if (targetStartDate.equals(targetEndDate)) {
					if (!targetStartTime.before(targetEndTime))
						throw new Exception("Start time must be before end time");
				} else
					throw new Exception("Start time must be before end time");
			}

			Date sysCurrentDate = FlexiBookApplication.getCurrentDate();
			Time sysCurrentTime = FlexiBookApplication.getCurrentTime();
			if (!sysCurrentDate.before(targetStartDate)) {
				if (targetStartDate.equals(sysCurrentDate)) {
					if (!targetStartTime.before(sysCurrentTime))
						if (type.equals("vacation"))
							throw new Exception(type.substring(0, 1).toUpperCase() + type.substring(1)
									+ " cannot start in the past");
						else
							throw new Exception(
									type.substring(0, 1).toUpperCase() + type.substring(1) + " cannot be in the past");
				} else {
					if (type.equals("vacation"))
						throw new Exception(
								type.substring(0, 1).toUpperCase() + type.substring(1) + " cannot start in the past");
					else
						throw new Exception(
								type.substring(0, 1).toUpperCase() + type.substring(1) + " cannot be in the past");
				}
			}

			/*
			 * Modification
			 */
			FlexiBook fb = FlexiBookApplication.getFlexiBook();
			Business business = fb.getBusiness();
			TimeSlot timeSlot = null;
			List<TimeSlot> targetSet = null, complementSet = null;
			if (type.equals("holiday")) {
				targetSet = business.getHolidays();
				complementSet = business.getVacation();
			} else {
				targetSet = business.getVacation();
				complementSet = business.getHolidays();
			}
			TOTimeSlot targetTimeSlot = new TOTimeSlot(targetStartDate, targetStartTime, targetEndDate, targetEndTime);

			// Check for overlaps in the list where this time slot belongs.
			for (TimeSlot ts : targetSet) {
				if (ts.getStartDate().equals(date) && ts.getStartTime().equals(time))
					timeSlot = ts;
				if (ts != timeSlot && hasOverlap(targetTimeSlot,
						new TOTimeSlot(ts.getStartDate(), ts.getStartTime(), ts.getEndDate(), ts.getEndTime())))
					throw new Exception(type + " times cannot overlap");
			}

			// Check for overlaps in the other list.
			for (TimeSlot ts : complementSet)
				if (ts != timeSlot && hasOverlap(targetTimeSlot,
						new TOTimeSlot(ts.getStartDate(), ts.getStartTime(), ts.getEndDate(), ts.getEndTime())))
					throw new Exception("Holiday and vacation times cannot overlap");

			// If there is no overlaps, update the info.
			timeSlot.setStartDate(targetStartDate);
			timeSlot.setStartTime(targetStartTime);
			timeSlot.setEndDate(targetEndDate);
			timeSlot.setEndTime(targetEndTime);
			FlexiBookPersistence.save(fb);
		} catch (Exception e) {
			throw new InvalidInputException(e.getMessage());
		}
	}

	/**
	 * Removes an existing time slot with the specified characteristics.
	 *
	 * @param type
	 * @param timeSlotToBeRemoved
	 * @author Ing Tian
	 */
	public static void removeTimeSlot(String type, TOTimeSlot timeSlotToBeRemoved) throws InvalidInputException {
		try {

			Date startDate = timeSlotToBeRemoved.getStartDate(), endDate = timeSlotToBeRemoved.getEndDate();
			Time startTime = timeSlotToBeRemoved.getStartTime(), endTime = timeSlotToBeRemoved.getEndTime();

			if (startDate == null || endDate == null || startTime == null || endTime == null)
				throw new Exception("Date and time information cannot be null.");

			/*
			 * Input validation
			 */
			User user = FlexiBookApplication.getCurrentUser();
			if (!(user instanceof Owner))
				throw new Exception("No permission to update business information");

			/*
			 * Modification
			 */
			FlexiBook fb = FlexiBookApplication.getFlexiBook();
			Business business = fb.getBusiness();
			List<TimeSlot> timeSlots = type.equals("holiday") ? business.getHolidays() : business.getVacation();
			TimeSlot targetTimeSlot = null;
			for (TimeSlot ts : timeSlots) {
				if (ts.getStartTime().equals(startTime) && ts.getStartDate().equals(startDate)
						&& ts.getEndDate().equals(endDate) && ts.getEndTime().equals(endTime))
					targetTimeSlot = ts;

			}
			if (targetTimeSlot == null)
				throw new Exception("The exisiting time slot cannot be found");

			// Remove the time slot from corresponding list.
			if (type.equals("holiday"))
				business.removeHoliday(targetTimeSlot);
			else {
				business.removeVacation(targetTimeSlot);
			}
			FlexiBookPersistence.save(fb);
		} catch (Exception e) {
			throw new InvalidInputException(e.getMessage());
		}
	}

	/**
	 * Convert an int representing weekday into BusinessHour.DayOfWeek
	 *
	 * @param dayOfWeek
	 * @return
	 * @author Ing Tian
	 */
	private static BusinessHour.DayOfWeek parseDayOfWeek(int dayOfWeek) {
		switch (dayOfWeek) {
		case 1:
			return BusinessHour.DayOfWeek.Monday;
		case 2:
			return BusinessHour.DayOfWeek.Tuesday;
		case 3:
			return BusinessHour.DayOfWeek.Wednesday;
		case 4:
			return BusinessHour.DayOfWeek.Thursday;
		case 5:
			return BusinessHour.DayOfWeek.Friday;
		case 6:
			return BusinessHour.DayOfWeek.Saturday;
		case 7:
			return BusinessHour.DayOfWeek.Sunday;
		default:
			return BusinessHour.DayOfWeek.Monday;
		}
	}

	/**
	 * Retrieves basic information of the business.
	 *
	 * @return TOBusiness
	 * @throws InvalidInputException
	 * @author Ing Tian
	 */
	public static TOBusiness getBusinessInformation() throws InvalidInputException {
		try {
			/*
			 * Query
			 */
			FlexiBook app = FlexiBookApplication.getFlexiBook();
			Business business = app.getBusiness();
			return new TOBusiness(business.getName(), business.getPhoneNumber(), business.getAddress(),
					business.getEmail());
		} catch (Exception e) {
			throw new InvalidInputException(e.getMessage());
		}
	}

	/**
	 * Add a new vacation or holiday time slot to the business. The new time slot
	 * cannot overlap with existing ones.
	 *
	 * @param type
	 * @param startDate
	 * @param startTime
	 * @param endDate
	 * @param endTime
	 * @throws InvalidInputException
	 * @author Ing Tian
	 */
	public static void addNewTimeSlot(String type, Date startDate, Time startTime, Date endDate, Time endTime)
			throws InvalidInputException {
		try {
			/*
			 * Input validation
			 */
			User user = FlexiBookApplication.getCurrentUser();
			if (!(user instanceof Owner))
				throw new Exception("No permission to update business information");
			if (!startDate.before(endDate)) {
				if (startDate.equals(endDate)) {
					if (!startTime.before(endTime))
						throw new Exception("Start time must be before end time");
				} else
					throw new Exception("Start time must be before end time");
			}
			Date sysCurrentDate = FlexiBookApplication.getCurrentDate();
			Time sysCurrentTime = FlexiBookApplication.getCurrentTime();
			if (!sysCurrentDate.before(startDate)) {
				if (startDate.equals(sysCurrentDate)) {
					if (!startTime.before(sysCurrentTime))
						throw new Exception(
								type.substring(0, 1).toUpperCase() + type.substring(1) + " cannot start in the past");
				} else
					throw new Exception(
							type.substring(0, 1).toUpperCase() + type.substring(1) + " cannot start in the past");
			}

			/*
			 * Modification
			 */
			TOTimeSlot newTimeSlot = new TOTimeSlot(startDate, startTime, endDate, endTime);
			Business business = FlexiBookApplication.getFlexiBook().getBusiness();
			List<TimeSlot> timeSlotArray = type.equals("holiday") ? business.getHolidays() : business.getVacation();
			List<TimeSlot> complementTimeSlotArray = type.equals("holiday") ? business.getVacation()
					: business.getHolidays();

			// Check for overlaps in the list where the added time slot belongs
			for (TimeSlot timeSlot : timeSlotArray) {
				TOTimeSlot eleTimeSlot = new TOTimeSlot(timeSlot.getStartDate(), timeSlot.getStartTime(),
						timeSlot.getEndDate(), timeSlot.getEndTime());
				if (hasOverlap(newTimeSlot, eleTimeSlot))
					throw new Exception(
							type.substring(0, 1).toUpperCase() + type.substring(1) + " times cannot overlap");
			}

			// Check for overlaps in complement time slot array
			for (TimeSlot timeSlot : complementTimeSlotArray) {
				TOTimeSlot eleTimeSlot = new TOTimeSlot(timeSlot.getStartDate(), timeSlot.getStartTime(),
						timeSlot.getEndDate(), timeSlot.getEndTime());
				if (hasOverlap(newTimeSlot, eleTimeSlot))
					throw new Exception("Holiday and vacation times cannot overlap");
			}

			// Add new time slot to the corresponding list.
			if (type.equals("holiday"))
				business.addHoliday(
						new TimeSlot(startDate, startTime, endDate, endTime, FlexiBookApplication.getFlexiBook()));
			else
				business.addVacation(
						new TimeSlot(startDate, startTime, endDate, endTime, FlexiBookApplication.getFlexiBook()));
		} catch (Exception e) {
			throw new InvalidInputException(e.getMessage());
		}
	}

	/**
	 * Helper Method: Check whether two time slots have overlaps.
	 *
	 * @param timeSlot1
	 * @param timeSlot2
	 * @return
	 * @author Ing Tian
	 * @author Byron Chen, Mcgill ID: 260892558
	 */

	private static boolean hasOverlap(TOTimeSlot timeSlot1, TOTimeSlot timeSlot2) {
		if (timeSlot1.getStartDate().equals(timeSlot2.getStartDate())
				&& timeSlot1.getEndDate().equals(timeSlot2.getEndDate())) {
			Time startTime = timeSlot1.getStartTime(), eleStartTime = timeSlot2.getStartTime(),
					endTime = timeSlot1.getEndTime(), eleEndTime = timeSlot2.getEndTime();

			return (!((startTime.before(eleStartTime) && (endTime.before(eleStartTime) || endTime.equals(eleStartTime)))
					|| (endTime.after(eleEndTime) && (startTime.after(eleEndTime) || startTime.equals(eleEndTime)))));

		} else if (timeSlot1.getStartDate().equals(timeSlot2.getStartDate())) {
			if (!(timeSlot1.getEndDate().equals(timeSlot1.getStartDate())
					&& timeSlot2.getEndDate().equals(timeSlot2.getStartDate())))
				return true;

			else if (timeSlot1.getEndDate().equals(timeSlot1.getStartDate())) {
				return timeSlot1.getStartTime().after(timeSlot2.getStartTime())
						|| timeSlot1.getEndTime().after(timeSlot2.getStartTime());

			} else {
				return timeSlot2.getStartTime().after(timeSlot1.getStartTime())
						|| timeSlot2.getEndTime().after(timeSlot1.getStartTime());
			}

		} else if (timeSlot1.getEndDate().equals(timeSlot2.getEndDate())) {
			if (!(timeSlot1.getEndDate().equals(timeSlot1.getStartDate())
					&& timeSlot2.getEndDate().equals(timeSlot2.getStartDate())))
				return true;

			else if (timeSlot1.getEndDate().equals(timeSlot1.getStartDate())) {
				return !timeSlot1.getStartTime().after(timeSlot2.getEndTime());

			} else {
				return !timeSlot2.getStartTime().after(timeSlot1.getEndTime());
			}

		} else {
			return (timeSlot1.getStartDate().before(timeSlot2.getStartDate())
					&& timeSlot1.getEndDate().after(timeSlot2.getEndDate()))
					|| (timeSlot1.getStartDate().after(timeSlot2.getStartDate())
							&& timeSlot1.getStartDate().before(timeSlot2.getEndDate()));
		}
	}

	// ----------------------------------- Jianmo Li
	// -------------------------------------------------------

	/**
	 * Add a service to the system.
	 *
	 * @param aTOService
	 * @throws Exception
	 * @author Li Jianmo
	 */
	public static void addService(TOService aTOService) throws Exception {
		String error = "";
		if (aTOService.getDuration() <= 0) {
			error = "Duration must be positive ";
		}
		if (aTOService.getDowntimeStart() > 0 & aTOService.getDowntimeDuration() <= 0) {
			error = "Downtime duration must be positive ";
		}
		if (aTOService.getDowntimeStart() == 0 & aTOService.getDowntimeDuration() < 0) {
			error = "Downtime duration must be 0 ";
		}
		if (aTOService.getDowntimeDuration() > 0 & aTOService.getDowntimeStart() == 0) {
			error = "Downtime must not start at the beginning of the service ";
		}
		if (aTOService.getDowntimeStart() < 0) {
			error = "Downtime must not start before the beginning of the service ";
		}
		if (aTOService.getDowntimeStart() > 0
				& aTOService.getDowntimeDuration() > aTOService.getDuration() - aTOService.getDowntimeStart()) {
			error = "Downtime must not end after the service ";
		}
		if (aTOService.getDuration() > 0 && aTOService.getDowntimeStart() > aTOService.getDuration()) {
			error = "Downtime must not start after the end of the service ";
		}
		if (error.length() > 0) {
			throw new InvalidInputException(error.trim());
		}
		FlexiBook flexibook = FlexiBookApplication.getFlexiBook();
		User user = FlexiBookApplication.getCurrentUser();
		if (!(user.getUsername().equals("owner"))) {
			throw new Exception("You are not authorized to perform this operation");
		}

		try {
			Service service = new Service(aTOService.getName(), flexibook, aTOService.getDuration(),
					aTOService.getDowntimeDuration(), aTOService.getDowntimeStart());
			flexibook.addBookableService(service);
			FlexiBookPersistence.save(flexibook);
		} catch (Exception e) {
			error = e.getMessage();

			if (error.equals(
					"Cannot create due to duplicate name. See http://manual.umple.org?RE003ViolationofUniqueness.html")) {
				error = "Service " + aTOService.getName() + " already exists";
				throw new Exception(error);

			}
			// else throw new Exception(e.getMessage());
		}
	}

	/**
	 * Update a service to the system.
	 *
	 * @param targetName
	 * @param aTOService
	 * @throws Exception
	 * @author Li Jianmo
	 */
	public static void updateService(String targetName, TOService aTOService) throws Exception {

		// String targetName, String name, int duration, int downtimeStart,
		// int downtimeDuration
		String error = "";
		if (aTOService.getDuration() <= 0) {
			error = "Duration must be positive ";
		}
		if (aTOService.getDowntimeStart() > 0 & aTOService.getDowntimeDuration() <= 0) {
			error = "Downtime duration must be positive ";
		}
		if (aTOService.getDowntimeStart() == 0 & aTOService.getDowntimeDuration() < 0) {
			error = "Downtime duration must be 0 ";
		}
		if (aTOService.getDowntimeDuration() > 0 & aTOService.getDowntimeStart() == 0) {
			error = "Downtime must not start at the beginning of the service ";
		}
		if (aTOService.getDowntimeStart() < 0) {
			error = "Downtime must not start before the beginning of the service ";
		}
		if (aTOService.getDowntimeStart() > 0
				& aTOService.getDowntimeDuration() > aTOService.getDuration() - aTOService.getDowntimeStart()) {
			error = "Downtime must not end after the service ";
		}
		if (aTOService.getDuration() > 0 && aTOService.getDowntimeStart() > aTOService.getDuration()) {
			error = "Downtime must not start after the end of the service ";
		}
		if (error.length() > 0) {
			throw new InvalidInputException(error.trim());
		}

		FlexiBook flexibook = FlexiBookApplication.getFlexiBook();
		User user = FlexiBookApplication.getCurrentUser();
		if (!(user.getUsername().equals("owner"))) {
			throw new Exception("You are not authorized to perform this operation");
		}

		Service targetService = findService(targetName);
		if (targetService != null) {
			targetService.delete();
			try {
				Service service = new Service(aTOService.getName(), flexibook, aTOService.getDuration(),
						aTOService.getDowntimeDuration(), aTOService.getDowntimeStart());
				flexibook.addBookableService(service);
				FlexiBookPersistence.save(flexibook);
			} catch (Exception e) {
				error = e.getMessage();
				if (error.equals(
						"Cannot create due to duplicate name. See http://manual.umple.org?RE003ViolationofUniqueness.html")) {
					error = "Service " + aTOService.getName() + " already exists";
					throw new Exception(error);
				}
				// else throw new Exception(e.getMessage());
			}
		} else {
			throw new Exception("can not find such service");
		}
	}

	/**
	 * Delete a service from the system.
	 *
	 * @param name
	 * @throws Exception
	 * @author Li Jianmo
	 */
	public static void deleteService(String name) throws Exception {
		FlexiBook flexibook = FlexiBookApplication.getFlexiBook();
		User user = FlexiBookApplication.getCurrentUser();
		if (!(user.getUsername().equals("owner"))) {
			throw new Exception("You are not authorized to perform this operation");
		}
		if (findService(name) == null) {
			throw new Exception("Can not find such service");
		}

		for (Customer customer : flexibook.getCustomers()) {
			for (Appointment appointment : customer.getAppointments()) {
				if (appointment.getBookableService().getName().equals(name)) {
					if (appointment.getTimeSlot().getEndDate().after(FlexiBookApplication.getCurrentDate())) {
						throw new Exception("The service contains future appointments");
					} else if (appointment.getTimeSlot().getEndDate().equals(FlexiBookApplication.getCurrentDate())
							&& appointment.getTimeSlot().getEndTime().after(FlexiBookApplication.getCurrentTime())) {
						throw new Exception("The service contains future appointments");
					}
				}
			}
		}

		for (int i = 0; i < FlexiBookApplication.getFlexiBook().getBookableServices().size(); i++) {
			System.out.println(FlexiBookApplication.getFlexiBook().getBookableServices().size());
			BookableService service = FlexiBookApplication.getFlexiBook().getBookableServices().get(i);
			System.out.println(name);
			System.out.println(service.getName());
			if (service instanceof ServiceCombo) {
				ServiceCombo aServiceCombo = (ServiceCombo) FlexiBookApplication.getFlexiBook().getBookableServices()
						.get(i);
				System.out.println(aServiceCombo.getMainService().getService().getName());
				if (aServiceCombo.getMainService().getService().getName().equals(name)) {
					aServiceCombo.delete();
					i -= 1;
				} else {
					for (int j = 0; j < aServiceCombo.getServices().size(); j++) {
						ComboItem item = aServiceCombo.getService(j);
						if (item.getService().getName().equals(name)) {
							item.delete();
							j -= 1;
						}
					}
				}
			}
			if (name.equals(service.getName())) {
				service.delete();
				i -= 1;
			}
		}
		FlexiBookPersistence.save(flexibook);
	}

	/**
	 * @param name
	 * @return
	 * @author Jianmo Li
	 */
	private static Service findService(String name) {
		Service foundService = null;
		for (BookableService service : FlexiBookApplication.getFlexiBook().getBookableServices()) {
			if (service instanceof Service & service.getName().equals(name)) {
				foundService = (Service) service;
				break;
			}
		}
		return foundService;
	}

	/**
	 * A query method which use transfer object
	 *
	 * @param name
	 * @return
	 * @author Jianmo.li
	 */
	public static TOService getService(String name) {
		TOService aTOService = new TOService(null, 0, 0, 0);
		for (BookableService service : FlexiBookApplication.getFlexiBook().getBookableServices()) {
			if (service instanceof Service & service.getName().equals(name)) {
				Service aService = (Service) service;
				aTOService.setName(aService.getName());
				aTOService.setDuration(aService.getDuration());
				aTOService.setDowntimeStart(aService.getDowntimeStart());
				aTOService.setDowntimeDuration(aService.getDowntimeDuration());
				return aTOService;
			}
		}
		return null;

	}

	// ----------------------------------- Xiang Li
	// -------------------------------------------------------

	/**
	 * Create a service combo in the system.
	 *
	 * @param name
	 * @param mainService
	 * @param services
	 * @param mandatorySettings
	 * @throws Exception
	 * @author LI Xiang
	 */
	public static void defineServiceCombo(String name, ComboItem mainService, List<Service> services,
			List<Boolean> mandatorySettings) throws Exception {
		FlexiBook F = FlexiBookApplication.getFlexiBook();
		User user = FlexiBookApplication.getCurrentUser();
		String error = "";
		if (services.size() < 2)
			error = "A service Combo must contain at least 2 services";
		if (!services.contains(mainService))
			error = "Main service must be included in the services";
		if (!mandatorySettings.get(services.indexOf(mainService)))
			error = "A service Combo must contain at least 2 services";
		if (!F.getBookableServices().contains(mainService))
			error = "Service " + mainService.getService().getName() + " does not exist";
		if (findServiceCombo(name) != null)
			error = "Service combo " + name + " already exists";
		if (!user.getUsername().equals("owner"))
			error = "You are not authorized to perform this operation";
		if (error.length() > 0)
			throw new Exception(error.trim());
		try {
			ServiceCombo serviceCombo = new ServiceCombo(name, F);
			for (int i = 0; i < services.size(); i++) {
				serviceCombo.addService(mandatorySettings.get(i), services.get(i));
			}
			FlexiBookPersistence.save(F);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * Update a service combo in the system.
	 *
	 * @param oldname
	 * @param name
	 * @param mainService
	 * @param services
	 * @param mandatorySettings
	 * @throws Exception
	 * @author LI Xiang
	 */
	public static void updateServiceCombo(String oldname, String name, ComboItem mainService, List<Service> services,
			List<Boolean> mandatorySettings) throws Exception {
		FlexiBook F = FlexiBookApplication.getFlexiBook();
		User user = FlexiBookApplication.getCurrentUser();
		String error = "";
		if (services.size() < 2)
			error = "A service Combo must contain at least 2 services";
		if (!services.contains(mainService))
			error = "Main service must be included in the services";
		if (!mandatorySettings.get(services.indexOf(mainService)))
			error = "A service Combo must contain at least 2 services";
		if (!F.getBookableServices().contains(mainService))
			error = "Service " + mainService.getService().getName() + " does not exist";
		if (findServiceCombo(name) != null)
			error = "Service combo " + name + " already exists";
		if (!user.getUsername().equals("owner"))
			error = "You are not authorized to perform this operation";
		if (error.length() > 0)
			throw new Exception(error.trim());
		ServiceCombo temp = findServiceCombo(oldname);
		if (temp != null)
			temp.delete();
		defineServiceCombo(name, mainService, services, mandatorySettings);
		FlexiBookPersistence.save(F);
	}

	/**
	 * Delete a service combo from the system.
	 *
	 * @param name
	 * @throws Exception
	 * @author LI Xiang
	 */
	public static void deleteServiceCombo(String name) throws Exception {
		FlexiBook F = FlexiBookApplication.getFlexiBook();
		User user = FlexiBookApplication.getCurrentUser();
		String error = "";
		ServiceCombo temp = findServiceCombo(name);
		if (temp == null)
			error = "Service combo " + name + " does not exist";
		if (!user.getUsername().equals("owner"))
			error = "You are not authorized to perform this operation";
		if (error.length() > 0)
			throw new Exception(error.trim());
		for (Customer C : F.getCustomers()) {
			for (Appointment B : C.getAppointments()) {
				if (B.getTimeSlot().getEndDate().compareTo(FlexiBookApplication.getCurrentDate()) >= 0) {
					if ((B.getBookableService() instanceof ServiceCombo)) {
						if (B.getBookableService().getName().equals(name)) {
							throw new Exception("Service combo " + name + " has future appointments");
						}
					} else if (B.getBookableService() instanceof Service) {
						if (temp.getServices().contains(B))
							throw new Exception("Service combo " + name + " has future appointments");
					}
				}
			}
		}
		temp.delete();
		FlexiBookPersistence.save(F);
	}

	/**
	 * Retrieves service combo info from the system.
	 *
	 * @return
	 * @author LI Xiang
	 */
	public static List<TOServiceCombo> getServiceCombo() {
		ArrayList<TOServiceCombo> serviceCombos = new ArrayList<TOServiceCombo>();
		for (BookableService SC : FlexiBookApplication.getFlexiBook().getBookableServices()) {
			if (SC instanceof BookableService) {
				TOServiceCombo toServiceCombo = new TOServiceCombo(SC.getName());
				serviceCombos.add(toServiceCombo);
			}
		}
		return serviceCombos;
	}

	/**
	 * Locate a service combo by its name.
	 *
	 * @param name
	 * @return
	 * @author LI Xiang
	 */
	public static ServiceCombo findServiceCombo(String name) {
		for (BookableService service : FlexiBookApplication.getFlexiBook().getBookableServices()) {
			if (service.getName().equals(name) && (service instanceof ServiceCombo)) {
				return (ServiceCombo) service;
			}
		}
		return null;
	}

//----------------------------------- Ao Shen -------------------------------------------------------

	/**
	 * login with username and password
	 *
	 * @param username
	 * @param password
	 * @throws InvalidInputException
	 * @author Ao Shen
	 */
	public static void Login(String username, String password) throws InvalidInputException {
		FlexiBook flexibook = FlexiBookApplication.getFlexiBook();
		FlexiBookApplication.setCurrentUser(null);
		try {
			if (flexibook.getOwner() != null && flexibook.getOwner().getUsername().equals(username)
					&& flexibook.getOwner().getPassword().equals(password)) {
				FlexiBookApplication.setCurrentUser(flexibook.getOwner());
				FlexiBookPersistence.save(flexibook);
				return;
			}
			for (Customer customer : flexibook.getCustomers()) {
				if (customer.getUsername().equals(username) && customer.getPassword().equals(password)) {
					FlexiBookApplication.setCurrentUser(customer);
					FlexiBookPersistence.save(flexibook);
					return;
				}
			}
			if (flexibook.getOwner() == null && username.equals("owner")) {
				new Owner(username, password, flexibook);

				FlexiBookApplication.setCurrentUser(flexibook.getOwner());
				FlexiBookPersistence.save(flexibook);
				return;
			}
			throw new InvalidInputException("Username/password not found");
		} catch (RuntimeException e) {
			throw new InvalidInputException(e.getMessage());
		}
	}

	/**
	 * current user log out
	 *
	 * @throws InvalidInputException
	 * @author Ao Shen
	 */
	public static void Logout() throws InvalidInputException {
		FlexiBook flexibook = FlexiBookApplication.getFlexiBook();
		try {
			if (FlexiBookApplication.getCurrentUser() != null) {
				FlexiBookApplication.setCurrentUser(null);
				FlexiBookPersistence.save(flexibook);
			} else {
				throw new InvalidInputException("The user is already logged out");
			}
		} catch (RuntimeException e) {
			throw new InvalidInputException(e.getMessage());
		}
	}

	/**
	 * get a lsit of TimeSlot according to the customer's decision (view a day/week)
	 *
	 * @param datein
	 * @param isDay
	 * @throws InvalidInputException
	 * @author Ao Shen
	 */
	public static List<List<TOTimeSlot>> viewCalendar(String datein, boolean isDay) throws InvalidInputException {
		try {

			Date date = Date.valueOf(datein);
			List<TimeSlot> all = new ArrayList<TimeSlot>();
			List<TOTimeSlot> unavailable = new ArrayList<TOTimeSlot>();
			List<TOTimeSlot> available = new ArrayList<TOTimeSlot>();
			List<List<TOTimeSlot>> result = new ArrayList<List<TOTimeSlot>>();
			FlexiBook flexibook = FlexiBookApplication.getFlexiBook();

			for (Appointment appointment : flexibook.getAppointments()) {
				List<TimeSlot> timeslot_in_appointment = new ArrayList<TimeSlot>();
				BookableService bookableservice = appointment.getBookableService();
				if (bookableservice instanceof ServiceCombo) {
					ServiceCombo BookedServiceCombo = (ServiceCombo) bookableservice;
					Time startTime = appointment.getTimeSlot().getStartTime();
					Time endTime = appointment.getTimeSlot().getEndTime();
					Date startdate = appointment.getTimeSlot().getStartDate();
					Date enddate = appointment.getTimeSlot().getEndDate();
					for (ComboItem comboitem : BookedServiceCombo.getServices()) {
						if (comboitem.getService().getDowntimeStart() > 0) {
							LocalTime localtime = startTime.toLocalTime();
							localtime = localtime.plusMinutes(comboitem.getService().getDowntimeStart());
							Time inter = Time.valueOf(localtime);
							timeslot_in_appointment.add(new TimeSlot(startdate, startTime, enddate, inter, flexibook));
							localtime = localtime.plusMinutes(comboitem.getService().getDowntimeDuration());
							startTime = Time.valueOf(localtime);
						} else {
							LocalTime localtime = startTime.toLocalTime();
							localtime = localtime.plusMinutes(comboitem.getService().getDuration());
							endTime = Time.valueOf(localtime);

							timeslot_in_appointment
									.add(new TimeSlot(startdate, startTime, enddate, endTime, flexibook));

							startTime = endTime;
						}
					}

				} else {
					timeslot_in_appointment.add(appointment.getTimeSlot());
				}
				int i = 1;
				List<TimeSlot> merged_timeslot_in_appointment = new ArrayList<TimeSlot>();
				TimeSlot currentTimeSlot = timeslot_in_appointment.get(0);
				if (timeslot_in_appointment.size() == i) {
					merged_timeslot_in_appointment.add(currentTimeSlot);
				}
				while (i < timeslot_in_appointment.size()) {
					if (currentTimeSlot.getEndTime() == timeslot_in_appointment.get(i).getStartTime()) {
						currentTimeSlot.setEndTime(timeslot_in_appointment.get(i).getEndTime());
					} else {
						merged_timeslot_in_appointment.add(currentTimeSlot);
						currentTimeSlot = timeslot_in_appointment.get(i);
					}
					if (i == timeslot_in_appointment.size() - 1) {
						merged_timeslot_in_appointment.add(currentTimeSlot);
					}
					i++;
				}
				all.addAll(merged_timeslot_in_appointment);
			}
			Time max = Time.valueOf("23:59:00");
			Time min = Time.valueOf("00:00:00");
			for (TimeSlot holiday : flexibook.getBusiness().getHolidays()) {

				int diff = holiday.getEndDate().compareTo(holiday.getStartDate());
				if (diff != 0) {
					all.add(new TimeSlot(holiday.getStartDate(), holiday.getStartTime(), holiday.getStartDate(), max,
							flexibook));
					Date next_day = new Date(holiday.getStartDate().getTime() + 24 * 60 * 60 * 1000);
					while (diff != 0) {
						all.add(new TimeSlot(next_day, min, next_day, max, flexibook));
						next_day = new Date(next_day.getTime() + 24 * 60 * 60 * 1000);
						diff--;
					}
				} else {
					all.add(holiday);
				}
			}
			if (isDay == true) {
				unavailable = oneDayUnavailable(datein, flexibook, all);
				available = oneDayAvailable(datein, unavailable, flexibook);
			} else {
				for (int i = 0; i < 7; i++) {
					List<TOTimeSlot> inter = new ArrayList<TOTimeSlot>();
					inter = oneDayUnavailable(datein, flexibook, all);
					for (BusinessHour businesshours : flexibook.getBusiness().getBusinessHours()) {
						ca.mcgill.ecse.flexibook.model.BusinessHour.DayOfWeek today = businesshours.getDayOfWeek();
						if (getWeek(datein).equalsIgnoreCase(today.name())) {
							Time start = businesshours.getStartTime();
							Time end = businesshours.getEndTime();
							for (TOTimeSlot timeslot : inter) {
								if (timeslot.getEndTime().toString().compareTo(end.toString()) > 0
										&& timeslot.getStartTime().toString().compareTo(end.toString()) < 0) {
									timeslot.setEndTime(end);
								}
								if (timeslot.getStartTime().toString().compareTo(start.toString()) < 0) {
									timeslot.setStartTime(start);
								}
							}
						}
					}
					unavailable.addAll(inter);
					inter = oneDayAvailable(datein, inter, flexibook);
					available.addAll(inter);
					date = new Date(date.getTime() + 24 * 60 * 60 * 1000);
					datein = date.toString();
				}
			}
			result.add(unavailable);
			result.add(available);
			// FlexiBookPersistence.save(flexibook);
			return result;
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(datein + " is not a valid date");
		}
	}

	/**
	 * to return List<TOTimeSlot> unavailable in one specific day
	 *
	 * @param datein
	 * @param all
	 * @param flexibook
	 * @throws @author Ao Shen
	 */
	public static List<TOTimeSlot> oneDayUnavailable(String datein, FlexiBook flexibook, List<TimeSlot> all) {
		Time max = Time.valueOf("23:59:00");
		Time min = Time.valueOf("00:00:00");
		Date date = Date.valueOf(datein);
		List<TOTimeSlot> unavailable = new ArrayList<TOTimeSlot>();
		for (TimeSlot timeslot : all) {
			String date1 = timeslot.getStartDate().toString();
			String date2 = date.toString();
			int s = date1.toString().compareTo(date2.toString());
			if (s == 0) {
				if (date1.compareTo(timeslot.getEndDate().toString()) < 0) {
					TOTimeSlot time = new TOTimeSlot(timeslot.getStartDate(), timeslot.getStartTime(),
							timeslot.getStartDate(), max);
					unavailable.add(time);
					Date datea = new Date(date.getTime() + 24 * 60 * 60 * 1000);
					while (datea.toString().compareTo(timeslot.getEndDate().toString()) < 0) {
						TOTimeSlot timea = new TOTimeSlot(timeslot.getStartDate(), min, timeslot.getStartDate(), max);
						unavailable.add(timea);
						datea = new Date(datea.getTime() + 24 * 60 * 60 * 1000);
					}
					if (timeslot.getEndDate().toString().compareTo(datea.toString()) == 0) {
						TOTimeSlot timeb = new TOTimeSlot(datea, min, datea, timeslot.getEndTime());
						unavailable.add(timeb);
					}
				} else {
					TOTimeSlot time = new TOTimeSlot(timeslot.getStartDate(), timeslot.getStartTime(),
							timeslot.getEndDate(), timeslot.getEndTime());
					unavailable.add(time);
				}
			}
		}
		return unavailable;
	}

	/**
	 * to return List<TOTimeSlot> available in one specific day
	 *
	 * @param datein
	 * @param unavailable
	 * @param flexibook
	 * @throws @author Ao Shen
	 */
	public static List<TOTimeSlot> oneDayAvailable(String datein, List<TOTimeSlot> unavailable, FlexiBook flexibook) {
		List<TOTimeSlot> available = new ArrayList<TOTimeSlot>();
		Date date = Date.valueOf(datein);
		for (BusinessHour businesshours : flexibook.getBusiness().getBusinessHours()) {
			ca.mcgill.ecse.flexibook.model.BusinessHour.DayOfWeek today = businesshours.getDayOfWeek();
			if (getWeek(datein).equalsIgnoreCase(today.name())) {
				Time start = businesshours.getStartTime();
				Time end = businesshours.getEndTime();
				if (unavailable.size() == 0) {
					available.add(new TOTimeSlot(date, start, date, end));
				}
				int i = 1;
				boolean a = true;
				boolean b = false;
				for (TOTimeSlot timeslot : unavailable) {
					if (timeslot.getStartTime().toString().compareTo(start.toString()) >= 0
							&& timeslot.getStartTime().toString().compareTo(end.toString()) < 0) {
						if (i == 1 && timeslot.getStartTime().toString().compareTo(start.toString()) > 0) {
							TOTimeSlot time = new TOTimeSlot(date, start, date, unavailable.get(0).getStartTime());
							available.add(time);
							a = false;
						} else if (timeslot.getStartTime().toString().compareTo(start.toString()) > 0
								&& timeslot.getStartTime().toString().compareTo(end.toString()) < 0 && a) {

							TOTimeSlot time = new TOTimeSlot(date, start, date, timeslot.getStartTime());
							available.add(time);
							a = false;
						}
						start = timeslot.getEndTime();

					}
					if (timeslot == unavailable.get(unavailable.size() - 1) && (!start.equals(end))) {
						if (timeslot.getStartTime().toString().compareTo(businesshours.getStartTime().toString()) >= 0
								&& timeslot.getStartTime().toString().compareTo(end.toString()) < 0) {
							available.add(new TOTimeSlot(date, timeslot.getEndTime(), date, end));
							a = false;
						}
						// else available.add(new TOTimeSlot(date, start, date, end));
					} else if (!start.equals(end)) {
						if (timeslot.getStartTime().toString().compareTo(businesshours.getStartTime().toString()) >= 0
								&& timeslot.getStartTime().toString().compareTo(end.toString()) < 0) {
							TOTimeSlot time = new TOTimeSlot(date, start, date, end);
							if (unavailable.get(i).getStartTime().toString().compareTo(end.toString()) < 0) {
								time = new TOTimeSlot(date, start, date, unavailable.get(i).getStartTime());
							}

							available.add(time);
							start = timeslot.getEndTime();
							a = false;
						}
						i++;
					}
					if (unavailable.get(unavailable.size() - 1).equals(timeslot) && a) {
						available.add(new TOTimeSlot(date, start, date, end));
					}
				}
			}
		}
		return available;
	}

	/**
	 * find the week of day by string
	 *
	 * @param date
	 * @throws @author Ao Shen
	 */
	public static String getWeek(String date) {
		date = date + " 00:00";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

		LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
		return dateTime.getDayOfWeek().name();
	}

	// ----------------------------------- Byron Chen
	// -------------------------------------------------------

	private static int DTStartTime;

	/**
	 * Helper Method: Calculate Endtime, if the date is changed, throw an exception
	 *
	 * @param startTime
	 * @param duration
	 * @return
	 * @throws InvalidInputException
	 * @auther Byron Chen, Mcgill ID: 260892558
	 */

	private static Time getEndTTTime(Time startTime, int duration) {
		Time endTime = (Time) startTime.clone();
		int minsForEndTime = startTime.getMinutes() + duration; // get the mins of time and add the duration
		int hoursForEndTime = (minsForEndTime / 60) + startTime.getHours(); // get the hours of time and add the
		// duration

		if (minsForEndTime >= 60) { // if the mins for Endtime is more than 60 mins
			endTime.setMinutes(minsForEndTime % 60);
			endTime.setHours(hoursForEndTime);
		} else {
			endTime.setMinutes(minsForEndTime);
		}

		return endTime;

	}

	/**
	 * Helper Method: Calculate the total duration for the list of comboItems
	 *
	 * @return
	 * @throws InvalidInputException
	 * @auther Byron Chen, Mcgill ID: 260892558
	 */

	private static int getDurationOfCombo(List<ComboItem> Services) {
//			try {

		int duration = 0; // get the duration of the main service

		for (int i = 0; i < Services.size(); i++) { // add durations of each comboItem chosen together
			duration = duration + Services.get(i).getService().getDuration();

		}

		return duration;
	}

	/**
	 * Helper Method: get all the appointments on the given date in an inefficient
	 * way
	 *
	 * @param fb
	 * @param date
	 * @return
	 * @throws InvalidInputException
	 * @auther Byron Chen, Mcgill ID: 260892558
	 */

	private static List<Appointment> getAppointmentsOn(FlexiBook fb, Date date) {
		List<Appointment> appointments = fb.getAppointments();
		List<Appointment> appointmentsMade = new LinkedList<Appointment>();
		int i = 0;

		while (i < appointmentsMade.size()) {
			if (appointmentsMade.get(i).getTimeSlot().getStartDate().equals(date)) {
				appointments.add(appointmentsMade.get(i));
			}
			i++;
		}

		return appointments;

	}

	/**
	 * Helper Method: get all the appointments on the given date in an efficient way
	 *
	 * @param fb
	 * @param date
	 * @return
	 * @throws InvalidInputException
	 * @author Byron Chen, Mcgill ID: 260892558
	 * @warning All the appointments should be sorted by the TimeSolts before using
	 *          this method !!!!
	 */

	private static List<Appointment> effGetAppointmentsOn(FlexiBook fb, Date date) throws InvalidInputException {
		List<Appointment> appointments = null;
		List<Appointment> appointmentsMade = fb.getAppointments();
		int i = 0;
		boolean flag = false;

		try {
			while (i < appointmentsMade.size()) {
				if (appointmentsMade.get(i).getTimeSlot().getStartDate().equals(date)) { // Find the first Appointment
					// made on this day
					flag = true;
					break;
				}
				i++;
			}

			if (flag) { // If any appointment is made on this day
				while (appointmentsMade.get(i).getTimeSlot().getStartDate().equals(date)
						&& i < appointmentsMade.size()) { // Get all the Appointments made on this day
					appointments.add(fb.getAppointment(i));
					i++;
				}
			}

			return appointmentsMade;

		} catch (RuntimeException e) {
			throw new InvalidInputException(e.getMessage());

		}

	}

	/**
	 * Helper Method: get the down time slot of a service, if it doesn't have one,
	 * returns null
	 *
	 * @param fb
	 * @param aService
	 * @param appointment
	 * @return
	 * @author Byron Chen, Mcgill ID: 260892558
	 */

	private static TimeSlot getDownTimeSlotOfService(FlexiBook fb, Service aService, Appointment appointment) {
		TimeSlot ts = null;
		int StartTime = aService.getDowntimeStart() + DTStartTime; // get the time past when down time start
		int Duration = aService.getDowntimeDuration(); // get the time duration for the down timeint

		Time startTime;
		Time endTime;

		if (Duration != 0) { // if has down time

			startTime = getEndTTTime(appointment.getTimeSlot().getStartTime(), StartTime); // get the start time of the
			// down time
			endTime = getEndTTTime(startTime, Duration); // get the end time of the down time

			ts = new TimeSlot(appointment.getTimeSlot().getStartDate(), startTime,
					appointment.getTimeSlot().getEndDate(), endTime, fb); // create the ts for the down time slot

		}

		DTStartTime = DTStartTime + aService.getDuration();

		return ts;
	}

	/**
	 * Helper Method: get the down time in timeSlot type, if doesn't have any down
	 * time, return null
	 *
	 * @param fb
	 * @param appointment
	 * @return
	 * @throws InvalidInputException
	 * @author Byron Chen, Mcgill ID: 260892558
	 */

	private static List<TimeSlot> getDownTimeSlots(FlexiBook fb, Appointment appointment) {
		BookableService aBookableService = appointment.getBookableService();
		List<TimeSlot> downTimes = new LinkedList<TimeSlot>();
		TimeSlot temp;
		DTStartTime = 0;

		if (aBookableService instanceof Service) { // If it is a service
			Service aService = (Service) aBookableService; // get the subclass Service

			temp = getDownTimeSlotOfService(fb, aService, appointment);

			if (temp != null) { // If have the down time
				downTimes.add(temp); // add into the list
			}

		} else {
			List<ComboItem> comboItems = appointment.getChosenItems(); // get all the Items chosen in the appointment
			int i = 0;

			while (i < comboItems.size()) { // for all the Items chosen in the appointment
				temp = getDownTimeSlotOfService(fb, comboItems.get(i).getService(), appointment);

				if (temp != null) { // If have the down time
					downTimes.add(temp); // add into the list
				}

				i++;
			}
		}

		return downTimes;

	}

	/**
	 * Helper Method: Check whether two time slots have overlaps.
	 *
	 * @param timeSlot1
	 * @param timeSlot2
	 * @return
	 * @author Ing Tian
	 * @author Byron Chen, Mcgill ID: 260892558
	 */

	private static boolean hasOverlap(TimeSlot timeSlot1, TimeSlot timeSlot2) {

		if (timeSlot1.getStartDate().equals(timeSlot2.getStartDate())
				&& timeSlot1.getEndDate().equals(timeSlot2.getEndDate())) {
			Time startTime = timeSlot1.getStartTime(), eleStartTime = timeSlot2.getStartTime(),
					endTime = timeSlot1.getEndTime(), eleEndTime = timeSlot2.getEndTime();

			return (!((startTime.before(eleStartTime) && (endTime.before(eleStartTime) || endTime.equals(eleStartTime)))
					|| (endTime.after(eleEndTime) && (startTime.after(eleEndTime) || startTime.equals(eleEndTime)))));

		} else if (timeSlot1.getStartDate().equals(timeSlot2.getStartDate())) {
			if (!(timeSlot1.getEndDate().equals(timeSlot1.getStartDate())
					&& timeSlot2.getEndDate().equals(timeSlot2.getStartDate())))
				return true;

			else if (timeSlot1.getEndDate().equals(timeSlot1.getStartDate())) {
				return timeSlot1.getStartTime().after(timeSlot2.getStartTime())
						|| timeSlot1.getEndTime().after(timeSlot2.getStartTime());

			} else {
				return timeSlot2.getStartTime().after(timeSlot1.getStartTime())
						|| timeSlot2.getEndTime().after(timeSlot1.getStartTime());
			}

		} else if (timeSlot1.getEndDate().equals(timeSlot2.getEndDate())) {
			if (!(timeSlot1.getEndDate().equals(timeSlot1.getStartDate())
					&& timeSlot2.getEndDate().equals(timeSlot2.getStartDate())))
				return true;

			else if (timeSlot1.getEndDate().equals(timeSlot1.getStartDate())) {
				return !timeSlot1.getStartTime().after(timeSlot2.getEndTime());

			} else {
				return !timeSlot2.getStartTime().after(timeSlot1.getEndTime());
			}

		} else {
			return (timeSlot1.getStartDate().before(timeSlot2.getStartDate())
					&& timeSlot1.getEndDate().after(timeSlot2.getEndDate()))
					|| (timeSlot1.getStartDate().after(timeSlot2.getStartDate())
							&& timeSlot1.getStartDate().before(timeSlot2.getEndDate()));
		}
	}

	/**
	 * Find the BookableService by name in string
	 *
	 * @param name
	 * @return
	 * @author jianmo.li
	 */
	private static BookableService findServicePlus(String name) {
		BookableService foundService = null;
		for (BookableService service : FlexiBookApplication.getFlexiBook().getBookableServices()) {
			System.out.println(service.getName());
			if (service.getName().equals(name)) {
				foundService = service;
				break;
			}
		}
		return foundService;
	}

	/**
	 * To let a customer make an appointment
	 *
	 * @param date
	 * @param aBookableService
	 * @param items
	 * @param startTime
	 * @throws InvalidInputException
	 * @auther Byron Chen, Mcgill ID: 260892558
	 */

	public static void makeAppoinment(Date date, String aBookableService, String items, Time startTime)
			throws InvalidInputException {

		BookableService serviceName = findServicePlus(aBookableService);
		List<ComboItem> optionalServices = new LinkedList<ComboItem>();

		if (!items.equals("none")) {
			String[] inputComboItems = items.split(",");
			int i = 0;

			while (i < inputComboItems.length) {
				optionalServices.add(findComboItem(aBookableService, inputComboItems[i]));
				i++;
			}
		}

		try {
			// Declaration
			User currUser = FlexiBookApplication.getCurrentUser();
			FlexiBook fb = FlexiBookApplication.getFlexiBook();

			if (currUser == null)
				throw new InvalidInputException("You need to log in!");

			if (currUser.getUsername().equals("owner")) { // If current user is an owner
				throw new InvalidInputException("An owner cannot make an appointment");
			}

			Customer currCustomer = (Customer) currUser;

			createAppoinment(currCustomer, fb, date, serviceName, optionalServices, startTime);
			FlexiBookPersistence.save(fb);
		} catch (RuntimeException e) {
			throw new InvalidInputException(e.getMessage());

		}
	}

	/**
	 * Helper Method: Find the comboItem by the strings
	 *
	 * @param comboName
	 * @param comboItemName
	 * @return
	 * @auther Byron Chen, Mcgill ID: 260892558
	 */
	private static ComboItem findComboItem(String comboName, String comboItemName) {
		ComboItem aComboItem = null;
		List<ComboItem> comboItems = findServiceCombo(comboName).getServices();
		int i = 0;

		while (i < comboItems.size()) {
			if (comboItems.get(i).getService().getName().equals(comboItemName)) {
				aComboItem = comboItems.get(i);
				break;
			}
			i++;
		}
		return aComboItem;
	}

	/**
	 * Helper Method: Add a new appointment into the system
	 *
	 * @param currCustomer
	 * @param fb
	 * @param date
	 * @param serviceName
	 * @param optionalServices
	 * @param startTime
	 * @return
	 * @throws InvalidInputException
	 * @auther Byron Chen, Mcgill ID: 260892558
	 */

	private static boolean createAppoinment(Customer currCustomer, FlexiBook fb, Date date, BookableService serviceName,
			List<ComboItem> optionalServices, Time startTime) throws InvalidInputException {

		try {

			boolean flag = false;
			Appointment newAppointment = constructAnAppointment(currCustomer, fb, date, serviceName, optionalServices,
					startTime);

			if (isAvailable(fb, newAppointment)) {

				fb.removeAppointment(newAppointment);

				fb.addAppointment(newAppointment);

			} else {
				newAppointment.delete();
				fb.removeAppointment(newAppointment);

				String startTimeString = startTime.toString();
				String[] list = startTimeString.split(":");
				startTimeString = list[0] + ":" + list[1];

				if (startTimeString.charAt(0) == ('0')) {
					startTimeString = startTimeString.substring(1, 5);
				}

				throw new InvalidInputException("There are no available slots for " + serviceName.getName() + " on "
						+ date + " at " + startTimeString);
			}

			return flag;

		} catch (RuntimeException e) {
			throw new InvalidInputException(e.getMessage());

		}
	}

	private static Appointment constructAnAppointment(Customer currCustomer, FlexiBook fb, Date date,
			BookableService serviceName, List<ComboItem> optionalServices, Time startTime) {

		Appointment newAppointment = null;

		if (optionalServices == null || optionalServices.size() == 0) { // If it is a Service
			Service aService = (Service) serviceName; // get the subclass Service from BookableService
			TimeSlot ts = new TimeSlot(date, startTime, date, getEndTTTime(startTime, aService.getDuration()), fb);

			newAppointment = new Appointment(currCustomer, aService, ts, fb);

		} else {
			ServiceCombo aServiceCombo = (ServiceCombo) serviceName; // get the subclass ServiceCombo from
			// BookableService

			List<ComboItem> allServicesInCombo = aServiceCombo.getServices();

			List<ComboItem> chosenItemsForCheck = optionalServices;
			chosenItemsForCheck.add(aServiceCombo.getMainService()); // Have all comboItems chosen but not ordered

			List<ComboItem> chosenItemsToAdd = new LinkedList<ComboItem>();

			int i = 0;

			while (i < allServicesInCombo.size()) {
				if (chosenItemsForCheck.contains(allServicesInCombo.get(i))) {
					chosenItemsToAdd.add(allServicesInCombo.get(i));
				}
				i++;
			}

			TimeSlot ts = new TimeSlot(date, startTime, date,
					getEndTTTime(startTime, getDurationOfCombo(chosenItemsToAdd)), fb);
			newAppointment = new Appointment(currCustomer, aServiceCombo, ts, fb);

			int j = 0;

			while (j < chosenItemsToAdd.size()) { // Add the chosen items in the appointment in order !!!!!!!!
				// MainService Is Included!!!!!!!!!!!
				newAppointment.addChosenItem(chosenItemsToAdd.get(j));
				j++;
			}

		}

		return newAppointment;
	}

	/**
	 * Helper Method: Check if it is possible to get make an appointment
	 *
	 * @param fb
	 * @param newAppointment
	 * @return
	 * @throws InvalidInputException
	 * @author Byron Chen, Mcgill ID: 260892558
	 */
	private static boolean isAvailable(FlexiBook fb, Appointment newAppointment) {
		TimeSlot tsForCurrAppointment = newAppointment.getTimeSlot();
		TimeSlot tsForCheck = fb.getAppointments().get(0).getTimeSlot(); // initialize the variable for check
		List<TimeSlot> tsForDownTimeCheck = new LinkedList(); // initialize the variable for check
		List<Appointment> appointmentsForCheck;

		int i = 0;
		int j = 0;
		boolean flag = false;

		// -------Check if the time in the schedule is booked by others and if has
		// enough down time for it

		// -------Checking the existing appointment conflicts---------
		if (getAppointmentsOn(fb, tsForCurrAppointment.getStartDate()) != null) { // If any appointments on this day
			appointmentsForCheck = getAppointmentsOn(fb, tsForCurrAppointment.getStartDate()); // fetch all the
			// appointments on this
			// day

			while (i < appointmentsForCheck.size()) { // check each appointments on this day

				Appointment appPtr = appointmentsForCheck.get(i);

				if (!(appPtr.getCustomer().equals(newAppointment.getCustomer())
						&& appPtr.getTimeSlot().equals(newAppointment.getTimeSlot()))) {

					flag = false;
					tsForCheck = appPtr.getTimeSlot();

					if (hasOverlap(tsForCurrAppointment, tsForCheck)) { // if the time of existing appointment overlap
						// with the new appointment's

						// -------Checking the down time availability for the INSERTING
						// Appointment---------

						if (getDownTimeSlots(fb, newAppointment).size() != 0) { // If the new Appointment has down time

							tsForDownTimeCheck = getDownTimeSlots(fb, newAppointment); // get all the down time as
							// TimeSlots
							int k = 0;
							TimeSlot temp;

							while (k < tsForDownTimeCheck.size()) { // Check all the down time slots

								temp = tsForDownTimeCheck.get(k);

								// ---------------Check if any of the down time is enough for this appointment
								if (temp.getStartTime().before(tsForCheck.getStartTime()) || // if startTime of dt is
								// before the one of
								// existed Appointment
										temp.getStartTime().equals(tsForCheck.getStartTime())) { // OR equals
									if (temp.getEndTime().after(tsForCheck.getEndTime()) || // AND if down time ends at
									// the same time of ending
									// of existed Appointment
											temp.getEndTime().equals(tsForCheck.getEndTime())) { // OR equals

										flag = true; // Down time enough!
										break; // Stop checking
									}

								}
								k++;
							}
						}

						// -------Checking the down time availability for the EXISTING
						// Appointments---------

						if (getDownTimeSlots(fb, appointmentsForCheck.get(i)).size() != 0 && !flag) { // If the
							// overlapped
							// Appointment
							// has down time
							// and the
							// availability
							// for the
							// INSERTING
							// Appointment
							// is false

							tsForDownTimeCheck = getDownTimeSlots(fb, appointmentsForCheck.get(i)); // get all the down
							// time as TimeSlots
							int r = 0;
							TimeSlot temp1;

							while (r < tsForDownTimeCheck.size()) { // Check all the down slots

								temp1 = tsForDownTimeCheck.get(r);

								// Check if any of the down time is enough for this appointment
								if (temp1.getStartTime().before(newAppointment.getTimeSlot().getStartTime()) || // if
								// startTime
								// of dt
								// is
								// before
								// the
								// one
								// of
								// newAppointment
										temp1.getStartTime().equals(newAppointment.getTimeSlot().getStartTime())) { // OR
									// equals
									if (temp1.getEndTime().after(newAppointment.getTimeSlot().getEndTime()) || // AND if
									// down
									// time
									// ends
									// at
									// the
									// same
									// time
									// of
									// ending
									// of
									// newAppointment
											temp1.getEndTime().equals(newAppointment.getTimeSlot().getEndTime())) { // OR
										// equals

										flag = true;
										break; // Stop checking
									}
								}
								r++;
							}

						}

						if (!flag) { // if the down time isn't enough for this appointment

							return false;

						}

					}
					if (!hasOverlap(tsForCurrAppointment, tsForCheck)) {
						flag = true;
					}
				}

				i++;
			}
		}

		// --------Check if the Appointment is made in a business hour
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(newAppointment.getTimeSlot().getStartDate());
		int DayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		if (DayOfWeek == 1)
			DayOfWeek = 7;
		else
			DayOfWeek -= 1;

		boolean inWorkingHour = false;

		for (BusinessHour businessHour : fb.getBusiness().getBusinessHours()) {
			if (!businessHour.getDayOfWeek().equals(parseDayOfWeek(DayOfWeek)))
				continue;
			Time startTime = businessHour.getStartTime(), endTime = businessHour.getEndTime();
			if (!(startTime.after(newAppointment.getTimeSlot().getStartTime()))
					&& !(endTime.before(newAppointment.getTimeSlot().getEndTime()))) {
				inWorkingHour = true;
			}
		}

		if (!inWorkingHour)
			return false;

		// ---------Check if the appointment is in the past
		Date currDate = FlexiBookApplication.getCurrentDate(); // get current date
		Time currTime = FlexiBookApplication.getCurrentTime();

		if (newAppointment.getTimeSlot().getStartDate().toString().compareTo(currDate.toString()) < 0

		) {
			return false;

		}

		// ------Check if the Appointment is made in Holiday and Vacation

		Date ad = newAppointment.getTimeSlot().getStartDate();
		Time at = newAppointment.getTimeSlot().getStartTime();
		Time aEt = newAppointment.getTimeSlot().getEndTime();

		for (int k = 0; k < fb.getBusiness().getHolidays().size(); k++) {
			Date sdate = fb.getBusiness().getHolidays().get(k).getStartDate();
			Date edate = fb.getBusiness().getHolidays().get(k).getEndDate();
			Time stime = fb.getBusiness().getHolidays().get(k).getStartTime();
			Time etime = fb.getBusiness().getHolidays().get(k).getEndTime();
			if (ad.after(sdate) && ad.before(edate)) {
				return false;
			} else if (ad.equals(sdate)) {
				if (aEt.before(stime)) {
					flag = true;
				} else
					return false;
			} else if (ad.equals(edate)) {
				if (at.after(etime)) {
					flag = true;
				} else
					return false;
			}
		}

		for (int k = 0; k < fb.getBusiness().getVacation().size(); k++) {
			Date sdate = fb.getBusiness().getVacation().get(k).getStartDate();
			Date edate = fb.getBusiness().getVacation().get(k).getEndDate();
			Time stime = fb.getBusiness().getVacation().get(k).getStartTime();
			Time etime = fb.getBusiness().getVacation().get(k).getEndTime();
			if (ad.after(sdate) && ad.before(edate)) {
				return false;
			} else if (ad.equals(sdate)) {
				if (aEt.before(stime)) {
					return true;
				} else
					return false;
			} else if (ad.equals(edate)) {
				if (at.after(etime)) {
					return true;
				} else
					return false;
			}

		}

		return true;
	}

	/**
	 * To let the customer update the appointment
	 *
	 * @param user
	 * @param startDate
	 * @param startTime
	 * @param aBookableService
	 * @param newStartDate
	 * @param newStartTime
	 * @param item
	 * @param action
	 * @throws Exception
	 * @auther Byron Chen, Mcgill ID: 260892558
	 */
	public static void updateAppoinment(String user, Date startDate, Time startTime, String aBookableService,
			Date newStartDate, Time newStartTime, String item, String action) throws Exception {
		ComboItem optionalService;
		BookableService serviceName = findServicePlus(aBookableService);

		if (item.equals("none")) {
			optionalService = null;
		} else {
			optionalService = findComboItem(aBookableService, item);
		}

		boolean flag = false;
		String err = "";
		User currUser = FlexiBookApplication.getCurrentUser();
		FlexiBook fb = FlexiBookApplication.getFlexiBook();
		Appointment thisAppointment = getAppointmentByUser(startDate, serviceName, startTime, fb);

		try {

			if (currUser instanceof Owner)
				throw new Exception("Error: An owner cannot update a customer's appointment");
			if (!user.equals(currUser.getUsername()))
				throw new Exception("Error: A customer can only update their own appointments");

			if (action.equals("add")) { // change the start time only
				flag = addServiceToAppointment(thisAppointment, optionalService, fb);
				// FlexiBookPersistence.save(fb);
			} else if (action.equals("remove")) {
				flag = removeServiceFromAppointment(thisAppointment, optionalService, fb);
				// FlexiBookPersistence.save(fb);
			} else {
				flag = updateTimeOfAppointment(thisAppointment, newStartDate, newStartTime, fb);

			}
			FlexiBookPersistence.save(fb);
			if (!flag) {
				throw new InvalidInputException("unsuccessful");
			} else {
				throw new InvalidInputException("successful");
			}

		} catch (RuntimeException e) {
			throw new InvalidInputException(e.getMessage());
		}

	}

	/**
	 * Change or update the service of the appointment.
	 *
	 * @param sysDate
	 * @param sysTime
	 * @param startDate
	 * @param startTime
	 * @param username
	 * @param newServiceName
	 * @throws InvalidInputException
	 * @auther Byron Chen, Mcgill ID: 260892558
	 */
	public static void changeAppointmentService(Date sysDate, Time sysTime, Date startDate, Time startTime,
			String username, String newServiceName) throws InvalidInputException {
		try {

			/*
			 * Fetch the appointment to be modified.
			 */
			Appointment appointmentToBeModified = getAppointment(startDate, startTime, username);
			if (appointmentToBeModified == null)
				throw new Exception("The appointment by \"" + username + "\" on " + startDate.toString() + " at "
						+ startTime.toString() + " does not exist.");

			/*
			 * Check availability
			 */
			Customer currCustomer = null;
			for (Customer customerPtr : FlexiBookApplication.getFlexiBook().getCustomers())
				if (customerPtr.getUsername().equals(username)) {
					currCustomer = customerPtr;
					break;
				}

			Service service = findService(newServiceName);

			Appointment testAppointment = constructAnAppointment(currCustomer, FlexiBookApplication.getFlexiBook(),
					startDate, service, null, startTime);

			// Keep a copy of the appointment, if not available, switch back.
			TimeSlot oldTimeSlot = appointmentToBeModified.getTimeSlot();
			BookableService oldService = appointmentToBeModified.getBookableService();

			TimeSlot newTS = testAppointment.getTimeSlot();
			appointmentToBeModified.updateServiceOrServiceCombo(sysDate, newTS, service, null);

			testAppointment.delete();

			/*
			 * If available, change the appointment accordingly.
			 */
			if (!isAvailable(FlexiBookApplication.getFlexiBook(), appointmentToBeModified))
				appointmentToBeModified.updateServiceOrServiceCombo(sysDate, oldTimeSlot, oldService, null);

		} catch (Exception e) {
			throw new InvalidInputException(e.getMessage());
		}
	}

	private static Appointment getAppointment(Date startDate, Time startTime, String username) {
		FlexiBook flexiBook = FlexiBookApplication.getFlexiBook();
		for (Appointment appPtr : flexiBook.getAppointments()) {
			TimeSlot ts = appPtr.getTimeSlot();
			if (appPtr.getCustomer().getUsername().equals(username) && ts.getStartDate().equals(startDate)
					&& ts.getStartTime().equals(startTime))
				return appPtr;
		}
		return null;
	}

	/**
	 * Helper Method: update the time of the appointment
	 *
	 * @param thisAppointment
	 * @param newDate
	 * @param newStartTime
	 * @param fb
	 * @return
	 * @throws InvalidInputException
	 * @auther Byron Chen, Mcgill ID: 260892558
	 * @Warning !!!! ChosenItems in appointment includes all the services chosen
	 */
	private static boolean updateTimeOfAppointment(Appointment thisAppointment, Date newDate, Time newStartTime,
			FlexiBook fb) {
		Time newEndTime;

		// -------Calculate the new endTime----------
		if (thisAppointment.getChosenItems().size() == 0) { // if it's a service
			newEndTime = getEndTTTime(newStartTime, ((Service) thisAppointment.getBookableService()).getDuration());

		} else { // if its a combo
			ServiceCombo aServiceCombo = (ServiceCombo) thisAppointment.getBookableService(); // get the subclass
			// ServiceCombo from
			// BookableService

			List<ComboItem> items = thisAppointment.getChosenItems();
			// items.add(aServiceCombo.getMainService());

			newEndTime = getEndTTTime(newStartTime, getDurationOfCombo(items)); // get the duration time of the
			// appointment when serviceCombo
		}

		// create a new appointment with new timeSlot
		TimeSlot oldTimeSlot = thisAppointment.getTimeSlot(); // save the old time slot
		Date oldTimeSlotStartDate = oldTimeSlot.getStartDate(), oldTimeSlotEndDate = oldTimeSlot.getEndDate();
		Time oldTimeSlotStartTime = oldTimeSlot.getStartTime(), oldTimeSlotEndTime = oldTimeSlot.getEndTime();

		Date sysDate = FlexiBookApplication.getCurrentDate();
		Time sysTime = FlexiBookApplication.getCurrentTime();
		System.out.println(sysDate);
		Date d = thisAppointment.getTimeSlot().getStartDate();
		Time t = thisAppointment.getTimeSlot().getStartTime();

		thisAppointment.updateDateAndTime(sysDate, sysTime, newDate, newStartTime, newDate, newEndTime);
		if (d.toString().equals(thisAppointment.getTimeSlot().getStartDate().toString())
				&& t.toString().equals(thisAppointment.getTimeSlot().getStartTime().toString()))
			return false;

		// Check if possible to change the time and do the change; if not available,
		// throw the exception
		if (isAvailable(fb, thisAppointment)) {
			return true;

		} else {
			thisAppointment.updateDateAndTime(sysDate, sysTime, oldTimeSlotStartDate, oldTimeSlotStartTime,
					oldTimeSlotEndDate, oldTimeSlotEndTime);
			return false;
		}

	}

	/**
	 * Helper Method: remove the service and update the timeSlot
	 *
	 * @param thisAppointment
	 * @param optionalService
	 * @param fb
	 * @return
	 * @throws InvalidInputException
	 * @auther Byron Chen, Mcgill ID: 260892558
	 * @Warning !!!! ChosenItems in appointment includes all the services chosen
	 */

	private static boolean removeServiceFromAppointment(Appointment thisAppointment, ComboItem optionalService,
			FlexiBook fb) throws InvalidInputException {

		ServiceCombo aServiceCombo = (ServiceCombo) thisAppointment.getBookableService(); // get the subclass
		// ServiceCombo from
		// BookableService

		try {
			if (optionalService.getMandatory() == true) { // can't remove mandatory item
				return false;

			} else if (aServiceCombo.getMainService().equals(optionalService)) { // can't remove main service
				return false;

			} else {

				// ---------------------------------- Do the update
				// ------------------------------------
				List<ComboItem> items = new LinkedList<ComboItem>(thisAppointment.getChosenItems()); // get the chosen
				// items
				items.remove(optionalService); // remove the item
				Time newEndTime = getEndTTTime(thisAppointment.getTimeSlot().getStartTime(), getDurationOfCombo(items)); // and
				// get
				// the
				// new
				// end
				// time
				Time oldEndTime = thisAppointment.getTimeSlot().getEndTime(); // get the old end time time

				thisAppointment.getTimeSlot().setEndTime(newEndTime); // set the new end time

				if (isAvailable(fb, thisAppointment)) { // check the availability
					thisAppointment.removeChosenItem(optionalService);
					return true;

				} else {
					thisAppointment.getTimeSlot().setEndTime(oldEndTime); // set the old time back
					return false;
				}
			}

		} catch (RuntimeException e) {
			throw new InvalidInputException(e.getMessage());
		}

	}

	/**
	 * Helper Method: add the service and update the timeSlot
	 *
	 * @param appointment
	 * @param optionalService
	 * @param fb
	 * @return
	 * @throws InvalidInputException
	 * @auther Byron Chen, Mcgill ID: 260892558
	 * @Warning !!!! ChosenItems in appointment includes all the services chosen
	 */
	private static boolean addServiceToAppointment(Appointment appointment, ComboItem optionalService, FlexiBook fb)
			throws InvalidInputException {

		ServiceCombo aServiceCombo = (ServiceCombo) appointment.getBookableService(); // get the subclass
		// ServiceCombo from
		// BookableService

		try {
			// ------- Get the new appointment after the update --------------

			List<ComboItem> items = new LinkedList<ComboItem>(aServiceCombo.getServices()); // get the combo items in
			// orders

			List<ComboItem> itemsNotOrdered = new LinkedList<ComboItem>(appointment.getChosenItems()); // get the
			// chosen
			// items
			itemsNotOrdered.add(optionalService); // add the item but make it not in orders

			List<ComboItem> itemsOrdered = new LinkedList<ComboItem>();

			for (int i = 0; i < items.size(); i++) {
				if (itemsNotOrdered.contains(items.get(i))) {
					itemsOrdered.add(items.get(i));
				}
			}

			Time newEndTime = getEndTTTime(appointment.getTimeSlot().getStartTime(), getDurationOfCombo(itemsOrdered)); // and
			// get
			// the
			// new
			// end
			// time
			Time oldEndTime = appointment.getTimeSlot().getEndTime(); // get the old end time time

			appointment.getTimeSlot().setEndTime(newEndTime); // set the new end time

			if (isAvailable(fb, appointment)) { // check the availability
				appointment.addOptionalServices(optionalService);
				System.out.println("can adddddd");
				return true;

			} else {
				appointment.getTimeSlot().setEndTime(oldEndTime);
				System.out.println("can not adddddd");// set the old time back
				return false;
			}

		} catch (RuntimeException e) {
			throw new InvalidInputException(e.getMessage());
		}

	}

	// ------------------------ CancelAppointment
	// --------------------------------------

	/**
	 * To let a customer cancel his own appointment before the date
	 *
	 * @param date
	 * @param startTime
	 * @param aBookableService
	 * @throws InvalidInputException
	 * @author Byron Chen, Mcgill ID: 260892558
	 */
	public static void cancelAppointment(Date date, Time startTime, String aBookableService)
			throws InvalidInputException {

		BookableService serviceName = findServicePlus(aBookableService);
		User currUser = FlexiBookApplication.getCurrentUser();
		FlexiBook fb = FlexiBookApplication.getFlexiBook();
		Date currDate = FlexiBookApplication.getCurrentDate(); // get current date

		String err = "";
		Appointment thisAppointment = getAppointmentByUser(date, serviceName, startTime, fb);

		try {

			if (currDate.toString().equals(date.toString())) {
				throw new InvalidInputException("Cannot cancel an appointment on the appointment date");
			}

			if (currUser.getUsername().equals("owner")) {
				err = "An owner cannot cancel an appointment";

			} else if (!thisAppointment.getCustomer().equals(currUser)) {
				err = "A customer can only cancel their own appointments";
			}

			if (err.length() > 0) {
				throw new InvalidInputException(err.trim());
			}

			thisAppointment.delete();
			FlexiBookPersistence.save(fb);

		} catch (RuntimeException e) {
			throw new InvalidInputException(e.getMessage());
		}
	}

	/**
	 * Helper Method: Get the appointment by the service name, date and time
	 *
	 * @param startDate
	 * @param serviceName
	 * @param startTime
	 * @param fb
	 * @return
	 * @throws InvalidInputException
	 * @author Byron Chen, Mcgill ID: 260892558
	 */

	private static Appointment getAppointmentByUser(Date startDate, BookableService serviceName, Time startTime,
			FlexiBook fb) throws InvalidInputException {

		List<Appointment> appointmentsOnDate = getAppointmentsOn(fb, startDate);
		int i = 0;

		if (appointmentsOnDate == null) {
			throw new InvalidInputException("Appointment you want to cancel is not found");
		}

		try {
			while (i < appointmentsOnDate.size()) {
				if (appointmentsOnDate.get(i).getBookableService().equals(serviceName)
						&& appointmentsOnDate.get(i).getTimeSlot().getStartTime().equals(startTime)) { // get the same
					// appointment
					// the user want

					Appointment thisAppointment = appointmentsOnDate.get(i);
					return thisAppointment;
				}
				i++;
			}

			throw new InvalidInputException("Appointment you want to cancel is not found"); // If nothing is found

		} catch (RuntimeException e) {
			throw new InvalidInputException(e.getMessage());
		}
	}

	/**
	 * 
	 * @param startdate
	 * @param startime
	 * 
	 * @author Byron Chen
	 */
	public static void endApp(Date startdate, Time startime) {

		FlexiBook fb = FlexiBookApplication.getFlexiBook();
		findAppbyTime(startdate, startime).endAppointment();

		FlexiBookPersistence.save(fb);
	}

	/**
	 * Get the appointment by given time
	 * 
	 * @param startdate
	 * @param startTime
	 * @return
	 * 
	 * @author Byron Chen
	 */
	private static Appointment findAppbyTime(Date startdate, Time startTime) {
		FlexiBook flexibook = FlexiBookApplication.getFlexiBook();
		Appointment result = null;
		for (Appointment a : flexibook.getAppointments()) {
			if (a.getTimeSlot().getStartDate().equals(startdate) && a.getTimeSlot().getStartTime().equals(startTime)) {
				result = a;
				break;
			}
		}
		return result;
	}

	/**
	 * @author Xiang Li
	 * @throws InvalidInputException
	 */
	public static void startApp(Date startdate, Time startime) {

		FlexiBook fb = FlexiBookApplication.getFlexiBook();

		findAppbyTime(startdate, startime).startAppointment();
		FlexiBookPersistence.save(fb);

	}

}
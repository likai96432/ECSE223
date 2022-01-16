package ca.mcgill.ecse.flexibook.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ca.mcgill.ecse.flexibook.controller.*;

import ca.mcgill.ecse.flexibook.application.FlexiBookApplication;
import ca.mcgill.ecse.flexibook.model.Appointment;
import ca.mcgill.ecse.flexibook.model.BookableService;
import ca.mcgill.ecse.flexibook.model.Business;
import ca.mcgill.ecse.flexibook.model.BusinessHour;
import ca.mcgill.ecse.flexibook.model.BusinessHour.DayOfWeek;
import ca.mcgill.ecse.flexibook.model.ComboItem;
import ca.mcgill.ecse.flexibook.model.Customer;
import ca.mcgill.ecse.flexibook.model.FlexiBook;
import ca.mcgill.ecse.flexibook.model.Owner;
import ca.mcgill.ecse.flexibook.model.Service;
import ca.mcgill.ecse.flexibook.model.ServiceCombo;
import ca.mcgill.ecse.flexibook.model.TimeSlot;
import ca.mcgill.ecse.flexibook.model.User;
import ca.mcgill.ecse.flexibook.persistence.FlexiBookPersistence;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.text.ParseException;

public class CucumberStepDefinitions {
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final String TIME_FORMAT = "HH:mm";
	private FlexiBook flexiBook;
	private static String filename = "testdata.flexibook";

	private String err;
	private int errorCtr;
	private int oldsize;
	private boolean businessHourUpdated = false;

	@Before
	public static void setUp() {
		FlexiBookPersistence.setFilename(filename);
		// remove test file
		File f = new File(filename);
		f.delete();
		// clear all data
		FlexiBookApplication.getFlexiBook().delete();
		FlexiBookApplication.setTestMode(true);
	}

	/**
	 * @author kevinli
	 */
	@Given("a Flexibook system exists")
	public void a_flexibook_system_exists() {
		// app = new FlexiBookApplication();
		flexiBook = FlexiBookApplication.getFlexiBook();
		// app.getFlexiBook();
		err = "";
		errorCtr = 0;
	}

	/**
	 * @param username
	 * @author kevinli
	 */
	@Given("there is no existing username {string}")
	public void noduplicateusername(String username) {
		if (findUser(username) != null) {
			findUser(username).delete();
		}
	}

	/**
	 * @param username
	 * @param password
	 * @author kevinli
	 */
	@When("the user provides a new username {string} and a password {string}")
	public void aNewAccountisCreated(String username, String password) {
		try {
			FlexiBookController.signUpCustomerAccount(username, password);
		} catch (InvalidInputException e) {
			err += e.getMessage();
			errorCtr++;
		}
	}

	/**
	 * @author kevinli
	 */
	@Then("a new customer account shall be created")
	public void thesystemhasnewuser() {
		assertEquals(1, flexiBook.getCustomers().size());
	}

	/**
	 * @param name
	 * @param password
	 * @author kevinli
	 */
	@Then("the account shall have username {string} and password {string}")
	public void usernameandpassword(String name, String password) {
		if (name.equals("owner")) {
			assertEquals(password, flexiBook.getOwner().getPassword());
		} else {
			assertEquals(name, flexiBook.getCustomer(0).getUsername());
			assertEquals(password, flexiBook.getCustomer(0).getPassword());
		}
	}

	/**
	 * @author kevinli
	 */
	@After
	public void teardown() {
		FlexiBookApplication.setCurrentUser(null);
		FlexiBookApplication.setCurrentDate(null);
		FlexiBookApplication.setCurrentTime(null);
		this.currentAppointment = null;
		flexiBook.delete();
		FlexiBookApplication.setTestMode(false);

	}

	/**
	 * @param name
	 * @return
	 * @author kevinli
	 */
	private User findUser(String name) {
		User result = null;
		if (name.equals("owner")) {
			return flexiBook.getOwner();
		}
		FlexiBook flexibook = FlexiBookApplication.getFlexiBook();
		List<Customer> users = flexibook.getCustomers();

		for (Customer a : users) {
			if (a.getUsername().equals(name)) {
				result = a;
				break;
			}
		}

		return result;
	}

	/**
	 * @author kevinli
	 */
	@Then("no new account shall be created")
	public void nonewaccount() {
		assertEquals(oldsize, flexiBook.getCustomers().size());
	}

	/**
	 * @param string
	 * @author kevinli
	 */
	@Then("an error message {string} shall be raised")
	public void an_error_message_with_content_shall_be_raise(String string) {
		assertTrue(err.contains(string));
	}

	/**
	 * @param name
	 * @author kevinli
	 */
	@Given("there is an existing username {string}")
	public void thereIsAUsername(String name) {
		if (name.equals("owner")) {
			Owner a = new Owner("owner", "123", flexiBook);
			flexiBook.setOwner(a);
		} else {
			flexiBook.addCustomer(name, "123");
			oldsize++;
		}
	}

	/**
	 * @param name
	 * @author kevinli
	 */
	@Given("the user is logged in to an account with username {string}")
	public void userLoggedin(String name) {
		// user=findUser(name);
		FlexiBookApplication.setCurrentUser(findUser(name));

	}

	/**
	 * @param name
	 * @param password
	 * @author kevinli
	 */
	@When("the user tries to update account with a new username {string} and password {string}")
	public void updateAccount(String name, String password) {
		try {
			FlexiBookController.updateAccount(name, password);
		} catch (InvalidInputException e) {
			err += e.getMessage();
			errorCtr++;
		}
	}

	/**
	 * @param target
	 * @author kevinli
	 */

	@Given("the account with username {string} has pending appointments")
	public void hasPendingAppountement(String target) {
		Service b = new Service("name", flexiBook, 1, 2, 4);
		// Date aStartDate, Time aStartTime, Date aEndDate, Time aEndTime,
		TimeSlot c = new TimeSlot(new Date(1990), new Time(10, 30, 2020), new Date(1990), new Time(10, 30, 2020),
				flexiBook);
		Appointment a = new Appointment(((Customer) findUser(target)), b, c, flexiBook);
		((Customer) findUser(target)).addAppointment(a);
	}

	/**
	 * @param target
	 * @author kevinli
	 */

	@Then("the account with the username {string} does not exist")
	public void doesNotExist(String target) {
		assertEquals(null, findUser(target));
	}

	/**
	 * @param target
	 * @author kevinli
	 */

	@Then("all associated appointments of the account with the username {string} shall not exist")
	public void noAppointement(String target) {
		List<Appointment> a = flexiBook.getAppointments();
		for (Appointment b : a) {
			assertTrue(b.getCustomer().getUsername().equals(target));
		}
	}

	/**
	 * @author kevinli
	 */
	@Then("the user shall be logged out")
	public void shouldLogOut() {
		assertEquals(null, FlexiBookApplication.getCurrentUser());
	}

	/**
	 * @param name
	 * @param password
	 * @author kevinli
	 */
	@Given("an owner account exists in the system with username {string} and password {string}")
	public void thereIsAOwner(String name, String password) {
		Owner a = new Owner(name, password, flexiBook);

		flexiBook.setOwner(a);
	}

	/**
	 * @param table
	 * @author kevinli
	 */

	@Given("the following customers exist in the system:")
	public void thereAreUsers(DataTable table) {
		List<Map<String, String>> rows = table.asMaps(String.class, String.class);
		FlexiBook flexiBook = this.flexiBook;
		for (Map<String, String> row : rows) {
			String username = row.get("username"), password = row.get("password");
			Customer customer = new Customer(username, password, flexiBook);
			flexiBook.addCustomer(customer);
		}
	}

	/**
	 * @author kevinli
	 */

	@Then("the account shall not be updated")
	public void noUpdate() {
		boolean a = true;
		if (FlexiBookApplication.getCurrentUser().getUsername().length() == 0
				|| FlexiBookApplication.getCurrentUser().getPassword().length() == 0) {
			a = false;
		}
		assertTrue(a);
	}

	/**
	 * @param name
	 * @author kevinli
	 */
	@When("the user tries to delete account with the username {string}")
	public void deleteAccount(String name) {
		try {
			FlexiBookController.deleteCustomerAccount(name);
		} catch (InvalidInputException e) {
			err += e.getMessage();
			errorCtr++;
		}
	}

	/**
	 * @param name
	 * @author kevinli
	 */
	@Then("the account with the username {string} exists")
	public void stillHere(String name) {
		assertTrue(findUser(name) != null);
	}

	/**
	 * @param table
	 * @author kevinli
	 */
	@Given("the business has the following opening hours")
	public void businessHas(DataTable table) {
		List<Map<String, String>> rows = table.asMaps(String.class, String.class);
		for (Map<String, String> info : rows) {
			String day = info.get("day"), start = info.get("startTime"), end = info.get("endTime");
			DayOfWeek a = null;
			if (day.equals("Monday")) {
				a = BusinessHour.DayOfWeek.Monday;
			} else if (day.equals("Tuesday")) {
				a = BusinessHour.DayOfWeek.Tuesday;
			} else if (day.equals("Wednesday")) {
				a = BusinessHour.DayOfWeek.Wednesday;
			} else if (day.equals("Thursday")) {
				a = BusinessHour.DayOfWeek.Thursday;
			} else if (day.equals("Friday")) {
				a = BusinessHour.DayOfWeek.Friday;
			} else if (day.equals("Saturday")) {
				a = BusinessHour.DayOfWeek.Saturday;
			} else if (day.equals("Sunday")) {
				a = BusinessHour.DayOfWeek.Sunday;
			}
			Time st = Time.valueOf(start + ":00");
			Time et = Time.valueOf(end + ":00");
			BusinessHour Toadd = new BusinessHour(a, st, et, this.flexiBook);
			this.flexiBook.getBusiness().addBusinessHour(Toadd);

		}
	}

	/**
	 * @param table
	 * @author kevinli
	 */
	@Given("the business has the following holidays")
	public void hasHolidays(DataTable table) {
		// TimeSlot(Date aStartDate, Time aStartTime, Date aEndDate, Time aEndTime,
		// FlexiBook aFlexiBook)
		List<Map<String, String>> rows = table.asMaps(String.class, String.class);
		for (Map<String, String> info : rows) {
			String sd = info.get("startDate"), ed = info.get("endDate"), st = info.get("startTime"),
					et = info.get("endTime");
			TimeSlot toAdd = new TimeSlot(Date.valueOf(sd), Time.valueOf(st + ":00"), Date.valueOf(ed),
					Time.valueOf(et + ":00"), this.flexiBook);
			this.flexiBook.getBusiness().addHoliday(toAdd);
		}

	}

	/**
	 * @author Jianmo.Li
	 */
	@Given("an owner account exists in the system")
	public void ownerExists() {
		if (flexiBook.getOwner() == null) {
			Owner owner = new Owner("owner", "123456", flexiBook);
			flexiBook.setOwner(owner);
		}
	}

	/**
	 * @author Jianmo.Li
	 */
	@Given("a business exists in the system")
	public void businessExists() {
		if (flexiBook.getBusiness() == null) {
			Business aNewBusiness = new Business("qwe", "asd", "zxc", "rty", flexiBook);
			flexiBook.setBusiness(aNewBusiness);
		}

	}

	/**
	 * @param string
	 * @author Jianmo.Li
	 */
	@Given("the Owner with username {string} is logged in")
	public void ownerLoggedIn(String string) {
		FlexiBookApplication.setCurrentUser(findUser("owner"));
	}

	/**
	 * @param accountName
	 * @param string2
	 * @param string3
	 * @param string4
	 * @param string5
	 * @throws Exception
	 * @author Jianmo.Li
	 */
	@When("{string} initiates the addition of the service {string} with duration {string}, start of down time {string} and down time duration {string}")
	public void addService(String accountName, String string2, String string3, String string4, String string5)
			throws Exception {
		int duration = Integer.parseInt(string3);
		int downtimeStart = Integer.parseInt(string4);
		int downtimeDuration = Integer.parseInt(string5);
		TOService aTOService = new TOService(string2, duration, downtimeStart, downtimeDuration);
		try {
			FlexiBookController.addService(aTOService);
			this.err = "";
		} catch (Exception e) {

			this.err += e.getMessage();
			System.out.println(e.getMessage());
			this.errorCtr += 1;
		}
	}

	/**
	 * @param string
	 * @author Jianmo.Li
	 */
	@Then("the service {string} shall exist in the system")
	public void servcieExists(String string) {
		int helper = 0;
		for (BookableService service : flexiBook.getBookableServices()) {
			if (service.getName().equals(string)) {
				helper += 1;
				break;
			}
		}
		assertTrue(helper > 0);
	}

	/**
	 * @param string
	 * @param string2
	 * @param string3
	 * @param string4
	 * @author Jianmo.Li
	 */
	@Then("the service {string} shall have duration {string}, start of down time {string} and down time duration {string}")
	public void serviceInformation(String string, String string2, String string3, String string4) {
		String name = string;
		int duration = Integer.parseInt(string2);
		int downtimeStart = Integer.parseInt(string3);
		int downtimeDuration = Integer.parseInt(string4);
		int helper = 0;
		for (BookableService service : flexiBook.getBookableServices()) {
			if (service.getName().equals(name)) {
				if (service instanceof Service) {
					service = (Service) service;
					if (((Service) service).getDowntimeDuration() == downtimeDuration
							&& ((Service) service).getDowntimeStart() == downtimeStart
							&& ((Service) service).getDuration() == duration) {
						helper += 1;
						break;
					}
				}
			}
		}
		assertTrue(helper > 0);
	}

	/**
	 * @param string
	 * @author Jianmo.Li
	 */
	@Then("the number of services in the system shall be {string}")
	public void numberOfServices(String string) {
		int flag = Integer.parseInt(string);
		int count = 0;
		for (BookableService service : flexiBook.getBookableServices()) {
			if (service instanceof Service) {
				count += 1;
			}
		}
		assertTrue(count == flag);
	}

	/**
	 * @param string
	 * @author Jianmo.Li
	 */
	@Then("the service {string} shall not exist in the system")
	public void serviceNotExist(String string) {
		int helper = 0;
		for (BookableService service : flexiBook.getBookableServices()) {
			if (service.getName().equals(string) && service instanceof Service) {
				helper += 1;
				break;
			}
		}
		assertTrue(helper == 0);
	}

	/**
	 * @param dataTable
	 * @author Jianmo.Li
	 */
	@Given("the following services exist in the system:")
	public void servixesExist(io.cucumber.datatable.DataTable dataTable) {
		// Write code here that turns the phrase above into concrete actions
		// For automatic transformation, change DataTable to one of
		// E, List<E>, List<List<E>>, List<Map<K,V>>, Map<K,V> or
		// Map<K, List<V>>. E,K,V must be a String, Integer, Float,
		// Double, Byte, Short, Long, BigInteger or BigDecimal.
		//
		// For other transformations you can register a DataTableType.
		// | name | duration | downtimeStart | downtimeDuration |
		List<Map<String, String>> rows = dataTable.asMaps();
		for (int i = 0; i < rows.size(); i++) {
			// System.out.println(row);
			Map<String, String> row = rows.get(i);
			String name = row.get("name");
			int duration = Integer.parseInt(row.get("duration"));
			int downtimeStart = Integer.parseInt(row.get("downtimeStart"));
			int downtimeDuration = Integer.parseInt(row.get("downtimeDuration"));
			BookableService service = new Service(name, flexiBook, duration, downtimeDuration, downtimeStart);
			flexiBook.addBookableService(service);
		}
	}

	/**
	 * @param string
	 * @param dataTable
	 * @author Jianmo.Li
	 */
	@Then("the service {string} shall still preserve the following properties:")
	public void servicePreservesProperties(String string, io.cucumber.datatable.DataTable dataTable) {
		// Write code here that turns the phrase above into concrete actions
		// For automatic transformation, change DataTable to one of
		// E, List<E>, List<List<E>>, List<Map<K,V>>, Map<K,V> or
		// Map<K, List<V>>. E,K,V must be a String, Integer, Float,
		// Double, Byte, Short, Long, BigInteger or BigDecimal.
		//
		// For other transformations you can register a DataTableType.

		int flag = 0;
		List<Map<String, String>> rows = dataTable.asMaps();

		for (Map<String, String> row : rows) {
			System.out.println(row);
			if (row.get("name").equals(string)) {
				flag = 1;
			}
		}
		assertTrue(flag == 1);
	}

	/**
	 * @param name
	 * @author Jianmo.Li
	 */
	@Given("Customer with username {string} is logged in")
	public void loggedIn(String name) {
		FlexiBookApplication.setCurrentUser(findUser(name));
	}

	/**
	 * @param string
	 * @param string2
	 * @param string3
	 * @param string4
	 * @param string5
	 * @param string6
	 * @throws Exception
	 * @author Jianmo.Li
	 */
	@When("{string} initiates the update of the service {string} to name {string}, duration {string}, start of down time {string} and down time duration {string}")
	public void updateService(String string, String string2, String string3, String string4, String string5,
			String string6) throws Exception {
		try {
			String targetName = string2;
			String name = string3;
			int duration = Integer.parseInt(string4);
			int downtimeStart = Integer.parseInt(string5);
			int downtimeDuration = Integer.parseInt(string6);
			TOService aTOService = new TOService(name, duration, downtimeStart, downtimeDuration);
			FlexiBookController.updateService(targetName, aTOService);
		} catch (Exception e) {
			err += e.getMessage();
			errorCtr++;
		}
	}

	/**
	 * @param string
	 * @param string2
	 * @param string3
	 * @param string4
	 * @param string5
	 * @author Jianmo.Li
	 */
	@Then("the service {string} shall be updated to name {string}, duration {string}, start of down time {string} and down time duration {string}")
	public void serviceUpdated(String string, String string2, String string3, String string4, String string5) {
		int flag = 0;
		String targetName = string;
		String name = string2;
		int duration = Integer.parseInt(string3);
		int downtimeStart = Integer.parseInt(string4);
		int downtimeDuration = Integer.parseInt(string5);
		for (BookableService service : flexiBook.getBookableServices()) {
			if (service.getName().equals(string2) && service instanceof Service) {
				Service aService = (Service) service;
				if (aService.getDowntimeDuration() == downtimeDuration && aService.getDowntimeStart() == downtimeStart
						&& aService.getDuration() == duration) {
					flag = 1;
				}
			}
		}
		assertTrue(flag == 1);
	}

	/**
	 * @param systemTime
	 * @throws Exception
	 * @author Ing Tian
	 */
	@Given("the system's time and date is {string}")
	public void setSystemTime(String systemTime) throws Exception {
		String date = systemTime.split("\\+")[0];
		String time = systemTime.split("\\+")[1];
		FlexiBookApplication.setCurrentDate(Date.valueOf(date));
		FlexiBookApplication.setCurrentTime(Time.valueOf(time + ":00"));
	}

	/**
	 * @author Jianmo.Li
	 * @param dataTable
	 */

	private static int i;

	@Given("the following appointments exist in the system:")
	public void appointmentExists(io.cucumber.datatable.DataTable dataTable) {
		// Write code here that turns the phrase above into concrete actions
		// For automatic transformation, change DataTable to one of
		// E, List<E>, List<List<E>>, List<Map<K,V>>, Map<K,V> or
		// Map<K, List<V>>. E,K,V must be a String, Integer, Float,
		// Double, Byte, Short, Long, BigInteger or BigDecimal.
		//
		// For other transformations you can register a DataTableType.

		// | customer | serviceName | selectedComboItems | date | startTime | endTime |
		List<Map<String, String>> rows = dataTable.asMaps();
		for (int i = 0; i < rows.size(); i++) {
			// System.out.println(row);
			Map<String, String> row = rows.get(i);
			String customer = row.get("customer");
			String serviceName = row.get("serviceName");
			Date startDate = Date.valueOf(row.get("date"));
			Time startTime = Time.valueOf(row.get("startTime") + ":00");
			Date endDate = Date.valueOf(row.get("date"));
			Time endTime = Time.valueOf(row.get("endTime") + ":00");
			User aCustomer = findUser(customer);
			Customer aaCustomer = (Customer) aCustomer;
			TimeSlot aTimeSlot = new TimeSlot(startDate, startTime, endDate, endTime, flexiBook);

			BookableService bookedService = findServicePlus(serviceName);
			Appointment aAppointment = new Appointment(aaCustomer, bookedService, aTimeSlot, flexiBook);

			/*
			 * Add comboitems to the appointment Comboitems are excluded if the bookable
			 * service is a service
			 */
			String optService = row.get("optServices");
			if (optService != null && !optService.equals("none") && !optService.equals("")) {

				ServiceCombo comboService = findServiceCombo(serviceName);
				List<ComboItem> comboItems = comboService.getServices();
				String mainServiceName = comboService.getMainService().getService().getName();
				optService += "," + mainServiceName;
				String[] optServiceList = optService.split(",");
				List<String> optSL = Arrays.asList(optServiceList);

				for (ComboItem item : comboItems) {
					String itemName = item.getService().getName();
					if (optSL.contains(itemName))
						aAppointment.addChosenItem(item);
				}
			}

			flexiBook.addAppointment(aAppointment);
		}

//---------------------------------------------------------------------------------------------------------------------------------------------------------
		i = flexiBook.getAppointments().size();

	}

	/**
	 * @param string
	 * @param string2
	 * @throws Exception
	 * @author Jianmo.Li
	 */
	@When("{string} initiates the deletion of service {string}")
	public void deleteService(String string, String string2) throws Exception {
		try {
			String name = string2;
			FlexiBookController.deleteService(name);
		} catch (Exception e) {
			err += e.getMessage();
			errorCtr++;
		}

	}

	/**
	 * @param string
	 * @param string2
	 * @author Jianmo.li
	 */
	@Then("the number of appointments in the system with service {string} shall be {string}")
	public void numberOfAppointmentsWithService(String string, String string2) {
		int count = 0;
		int flag = Integer.parseInt(string2);
		for (Appointment aAppointment : flexiBook.getAppointments()) {
			if (aAppointment.getBookableService().getName().equals(string)) {
				count += 1;
			}
		}
		assertTrue(count == flag);
	}

	/**
	 * @param string
	 * @author Jianmo.li
	 */
	@Then("the number of appointments in the system shall be {string}")
	public void numberOfAppointments(String string) {
		int flag = Integer.parseInt(string);
		int count = flexiBook.getAppointments().size();
		assertTrue(flag == count);
	}

	/**
	 * @param dataTable
	 * @author Jianmo.li
	 */
	@Given("the following service combos exist in the system:")
	public void combosExist(io.cucumber.datatable.DataTable dataTable) {
		// Write code here that turns the phrase above into concrete actions
		// For automatic transformation, change DataTable to one of
		// E, List<E>, List<List<E>>, List<Map<K,V>>, Map<K,V> or
		// Map<K, List<V>>. E,K,V must be a String, Integer, Float,
		// Double, Byte, Short, Long, BigInteger or BigDecimal.
		//
		// For other transformations you can register a DataTableType.
		List<Map<String, String>> rows = dataTable.asMaps();
		for (int i = 0; i < rows.size(); i++) {
			// System.out.println(row);
			Map<String, String> row = rows.get(i);
			String name = row.get("name");
			String mainserviceName = row.get("mainService");
			String servicesName = row.get("services");
			String[] serviceName_array = servicesName.split(",");
			String mandatory = row.get("mandatory");
			String[] mandatory_array = mandatory.split(",");

			ServiceCombo aServiceCombo = new ServiceCombo(name, flexiBook);
			for (int j = 0; j < serviceName_array.length; j++) {
				if (serviceName_array[j].equals(mainserviceName)) {
					ComboItem aMainComboItem = new ComboItem(true, findService(serviceName_array[j]), aServiceCombo);
					aServiceCombo.setMainService(aMainComboItem);
				} else {
					aServiceCombo.addService(mandatory_array[j].equals("true"), findService(serviceName_array[j]));
				}

			}

			flexiBook.addBookableService(aServiceCombo);
		}
	}

	/**
	 * @param string
	 * @author Jianmo.LI
	 */
	@Then("the service combos {string} shall not exist in the system")
	public void combosNotExist(String string) {
		int flag = 0;
		if (findServiceCombo(string) != null) {
			flag = 1;
		}
		assertTrue(flag == 0);
	}

	@Then("the service combos {string} shall not contain service {string}")
	public void notContainService(String string, String string2) {
		int flag = 0;
		ServiceCombo serviceCombo = findServiceCombo(string);
		if (serviceCombo != null) {
			for (ComboItem aComboItem : serviceCombo.getServices()) {
				System.out.println(aComboItem.getService().getName());
				if (aComboItem.getService().getName().equals(string2)) {
					flag = 1;
					break;
				}
			}
		}
		assertTrue(flag == 0);
	}

	/**
	 * @param string
	 * @author Jianmo.li
	 */
	@Then("the number of service combos in the system shall be {string}")
	public void numberOfCombos(String string) {
		int flag = Integer.parseInt(string);
		int count = 0;
		for (BookableService service : flexiBook.getBookableServices()) {
			if (service instanceof ServiceCombo) {
				count += 1;
			}
		}
		assertTrue(count == flag);
	}

	/**
	 * @param name
	 * @return
	 * @author Jianmo.li
	 */
	private Service findService(String name) {
		Service foundService = null;
		for (BookableService service : FlexiBookApplication.getFlexiBook().getBookableServices()) {
			if (service.getName().equals(name) && service instanceof Service) {
				foundService = (Service) service;
				break;
			}
		}
		return foundService;
	}

	/**
	 * @param name
	 * @return
	 * @author jianmo.li
	 */
	private ServiceCombo findServiceCombo(String name) {
		ServiceCombo foundServiceCombo = null;
		for (BookableService service : FlexiBookApplication.getFlexiBook().getBookableServices()) {
			System.out.println(service.getName());
			if (service.getName().equals(name) && service instanceof ServiceCombo) {
				foundServiceCombo = (ServiceCombo) service;
				break;
			}
		}
		return foundServiceCombo;
	}

	/**
	 * @param name
	 * @return
	 * @author jianmo.li
	 */
	private BookableService findServicePlus(String name) {
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

	@Then("an error message with content {string} shall be raised")
	public void errorRaised(String string) {
		assertTrue(err.contains(string));
	}

	// define service combo

	/**
	 * @param string  user name
	 * @param string2 combo name
	 * @param string3 main service name
	 * @param string4 list of services
	 * @param string5 list of mandatories
	 * @author LI Xiang
	 */
	@When("{string} initiates the definition of a service combo {string} with main service {string}, services {string} and mandatory setting {string}")
	public void defServiceCombo(String string, String string2, String string3, String string4, String string5) {
		// Write code here that turns the phrase above into concrete actions
		this.err = "";
		if (!flexiBook.getOwner().getUsername().equals(string))
			err += "You are not authorized to perform this operation";
		String[] inputService = string4.split(",");
		String[] inputManda = string5.split(",");
		if (inputService.length < 2)
			err += "A service Combo must contain at least 2 services ";
		boolean flag = false;
		for (int i = 0; i < inputService.length; i++)
			if (inputService[i].equals(string3)) {
				if (inputManda[i].equals("false"))
					err += "Main service must be mandatory ";
				flag = true;
			}
		if (!flag)
			err += "Main service must be included in the services ";
		for (int i = 0; i < inputService.length; i++) {
			flag = false;
			for (int j = 0; j < flexiBook.getBookableServices().size(); j++) {
				if (flexiBook.getBookableServices().get(j).getName().equals(inputService[i])) {
					flag = true;
					break;
				}
			}
			if (!flag)
				err += ("Service " + inputService[i] + " does not exist ");
		}
		flag = false;
		for (int i = 0; i < flexiBook.getBookableServices().size(); i++)
			if (flexiBook.getBookableServices().get(i).getName().equals(string3)) {
				flag = true;
				break;
			}
		if (!flag)
			err += ("Service " + string3 + " does not exist ");
		flag = true;
		for (int i = 0; i < flexiBook.getBookableServices().size(); i++)
			if (flexiBook.getBookableServices().get(i).getName().equals(string2)) {
				flag = false;
				break;
			}
		if (!flag)
			err += ("Service combo " + string2 + " already exists ");
		if (err.length() > 0)
			return;
		ServiceCombo aServiceCombo = new ServiceCombo(string2, flexiBook);
		for (int i = 0; i < inputService.length; i++) {
			flag = false;
			for (int j = 0; j < flexiBook.getBookableServices().size(); j++) {
				if (flexiBook.getBookableServices().get(j).getName().equals(inputService[i])) {
					flag = true;
					if (inputService[i].equals(string3))
						aServiceCombo.setMainService(new ComboItem(inputManda[i].equals("true"),
								(Service) flexiBook.getBookableServices().get(j), aServiceCombo));
					else
						aServiceCombo.addService(inputManda[i].equals("true"),
								(Service) flexiBook.getBookableServices().get(j));
				}
			}
			if (!flag) {
				aServiceCombo.delete();
				err += ("Service " + inputService[i] + " does not exist ");
			}
		}
	}

	/**
	 * @param string combo name
	 * @author LI Xiang
	 */
	@Then("the service combo {string} shall exist in the system")
	public void comboExists(String string) {
		assertTrue(findServiceCombo(string) != null);
	}

	/**
	 * @param string combo name
	 * @author LI Xiang
	 */
	@Then("the service combo {string} shall not exist in the system")
	public void comboNotExists(String string) {
		assertTrue(findService(string) == null);
	}

	/**
	 * @param string  combo name
	 * @param string2 list of services
	 * @param string3 list of mandatories
	 * @throws Exception
	 * @author LI Xiang
	 */
	@Then("the service combo {string} shall contain the services {string} with mandatory setting {string}")
	public void containServices(String string, String string2, String string3) {
		ServiceCombo temp = findServiceCombo(string);
		String inputService[] = string2.split(",");
		String inputMandatory[] = string3.split(",");
		boolean flag = true;
		for (int i = 0; i < temp.getServices().size(); i++) {
			if (!temp.getServices().get(i).getService().getName().equals(inputService[i])
					|| !(temp.getServices().get(i).getMandatory() == (inputMandatory[i].equals("true")))) {
				flag = false;
				break;
			}
		}
		assertTrue(flag);
	}

	/**
	 * @param string  combo name
	 * @param string2 main service name
	 * @author LI Xiang
	 */
	@Then("the main service of the service combo {string} shall be {string}")
	public void mainService(String string, String string2) {
		// Write code here that turns the phrase above into concrete actions
		ServiceCombo temp = findServiceCombo(string);
		List<ComboItem> L = temp.getServices();
		ComboItem aim = null;
		for (int i = 0; i < L.size(); i++)
			if (L.get(i).getService().getName().equals(string2))
				aim = L.get(i);
		assertTrue(aim != null);
		// throw new io.cucumber.java.PendingException();
	}

	/**
	 * @param string  service name
	 * @param string2 combo name
	 * @author LI Xiang
	 */
	@Then("the service {string} in service combo {string} shall be mandatory")
	public void serviceMandatory(String string, String string2) {
		// Write code here that turns the phrase above into concrete actions
		ServiceCombo temp = findServiceCombo(string2);
		List<ComboItem> L = temp.getServices();
		ComboItem aim = null;
		for (int i = 0; i < L.size(); i++)
			if (L.get(i).getService().getName().equals(string))
				aim = L.get(i);
		assertTrue(aim != null && aim.getMandatory());
		// throw new io.cucumber.java.PendingException();
	}

	/**
	 * @param string    combo name
	 * @param dataTable dataTable for service combo
	 * @author LI Xiang
	 */
	@Then("the service combo {string} shall preserve the following properties:")
	public void comboPreserveProperties(String string, io.cucumber.datatable.DataTable dataTable) {
		// Write code here that turns the phrase above into concrete actions
		// For automatic transformation, change DataTable to one of
		// E, List<E>, List<List<E>>, List<Map<K,V>>, Map<K,V> or
		// Map<K, List<V>>. E,K,V must be a String, Integer, Float,
		// Double, Byte, Short, Long, BigInteger or BigDecimal.
		//
		// For other transformations you can register a DataTableType.
		boolean flag = false, flag2 = true, flag3 = true;
		List<Map<String, String>> rows = dataTable.asMaps();
		ServiceCombo aim = null;
		Map<String, String> row = rows.get(0);
		String name = row.get("name");
		String mainserviceName = row.get("mainService");
		String servicesName = row.get("services");
		String[] serviceName_array = servicesName.split(",");
		String mandatory = row.get("mandatory");
		String[] mandatory_array = mandatory.split(",");
		aim = findServiceCombo(name);
		if (aim.getMainService().getService().getName().equals(mainserviceName))
			flag = true;
		for (int i = 0; i < aim.getServices().size(); i++) {
			if (!aim.getServices().get(i).getService().getName().equals(serviceName_array[i])) {
				flag2 = false;
				break;
			}
			if (aim.getServices().get(i).getMandatory() != (mandatory_array[i].equals("true"))) {
				flag3 = false;
				break;
			}
		}
		assertTrue(flag && flag2 && flag3);
	}

	// update service combo

	/**
	 * @param user          the user
	 * @param originName    name of the combo to be updated
	 * @param newName       new name of that combo, might be the same
	 * @param mainName      name of main service
	 * @param serviceList   list of services
	 * @param mandatoryList list of mandatories
	 * @author LI Xiang
	 */
	@When("{string} initiates the update of service combo {string} to name {string}, main service {string} and services {string} and mandatory setting {string}")
	public void updateCombo(String user, String originName, String newName, String mainName, String serviceList,
			String mandatoryList) {
		// Write code here that turns the phrase above into concrete actions
		this.err = "";
		if (!flexiBook.getOwner().getUsername().equals(user))
			err += "You are not authorized to perform this operation";
		String[] inputService = serviceList.split(",");
		String inputManda[] = mandatoryList.split(",");
		if (inputService.length < 2)
			err += "A service Combo must have at least 2 services ";
		boolean flag = false;
		for (int i = 0; i < inputService.length; i++)
			if (inputService[i].equals(mainName)) {
				if (inputManda[i].equals("false"))
					err += "Main service must be mandatory ";
				flag = true;
			}
		if (!flag)
			err += "Main service must be included in the services ";
		for (int i = 0; i < inputService.length; i++) {
			flag = false;
			for (int j = 0; j < flexiBook.getBookableServices().size(); j++) {
				if (flexiBook.getBookableServices().get(j).getName().equals(inputService[i])) {
					flag = true;
					break;
				}
			}
			if (!flag)
				err += ("Service " + inputService[i] + " does not exist ");
		}
		flag = false;
		for (int i = 0; i < flexiBook.getBookableServices().size(); i++)
			if (flexiBook.getBookableServices().get(i).getName().equals(mainName)) {
				flag = true;
				break;
			}
		if (!flag)
			err += ("Service " + mainName + " does not exist ");
		if (!originName.equals(newName)) {
			flag = true;
			for (int i = 0; i < flexiBook.getBookableServices().size(); i++)
				if (flexiBook.getBookableServices().get(i).getName().equals(newName)) {
					flag = false;
					break;
				}
			if (!flag)
				err += ("Service combo " + newName + " already exists ");
		}
		if (err.length() > 0)
			return;
		findServiceCombo(originName).delete();
		ServiceCombo aServiceCombo = new ServiceCombo(newName, flexiBook);
		for (int i = 0; i < inputService.length; i++) {
			flag = false;
			for (int j = 0; j < flexiBook.getBookableServices().size(); j++) {
				if (flexiBook.getBookableServices().get(j).getName().equals(inputService[i])) {
					flag = true;
					if (inputService[i].equals(mainName))
						aServiceCombo.setMainService(new ComboItem(inputManda[i].equals("true"),
								(Service) flexiBook.getBookableServices().get(j), aServiceCombo));
					else
						aServiceCombo.addService(inputManda[i].equals("true"),
								(Service) flexiBook.getBookableServices().get(j));
				}
			}
			if (!flag) {
				aServiceCombo.delete();
				err += ("Service " + inputService[i] + " does not exist ");
			}
		}
	}

	/**
	 * @param string  the origin service combo
	 * @param string2 the new service combo
	 * @author LI Xiang
	 */
	@Then("the service combo {string} shall be updated to name {string}")
	public void comboUpdated(String string, String string2) {
		// Write code here that turns the phrase above into concrete actions
		if (!string.equals(string2))
			assertTrue((findServiceCombo(string) == null) && (findServiceCombo(string2) != null));
		else
			assertTrue(findServiceCombo(string2) != null);
	}

	// delete service combo

	/**
	 * @param string  the user
	 * @param string2 the combo to be deleted
	 * @author LI Xiang
	 */
	@When("{string} initiates the deletion of service combo {string}")
	public void deleteCombo(String string, String string2) {
		this.err = "";
		if (!flexiBook.getOwner().getUsername().equals(string))
			err += "You are not authorized to perform this operation";
		try {
			FlexiBookController.deleteServiceCombo(string2);
		} catch (Exception e) {
			err += e.getMessage() + " ";
			errorCtr++;
		}
	}

	/**
	 * @author Ing Tian
	 */
	@Given("no business exists")
	public void thereIsNoBusiness() {
		this.flexiBook.setBusiness(null);
	}

	/**
	 * @param name
	 * @param address
	 * @param phoneNumber
	 * @param email
	 * @throws InvalidInputException
	 * @author Ing Tian
	 */
	@When("the user tries to set up the business information with new {string} and {string} and {string} and {string}")
	public void doBasicBusinessInformation(String name, String address, String phoneNumber, String email)
			throws InvalidInputException {
		try {
			FlexiBookController.setupBasicBusinessInformation(new TOBusiness(name, phoneNumber, address, email));
			this.err = "";
		} catch (Exception e) {
			this.err = e.getMessage();
			this.errorCtr += 1;
		}
	}

	/**
	 * @param name
	 * @param address
	 * @param phoneNumber
	 * @param email
	 * @param result
	 * @author Ing Tian
	 */
	@Then("a new business with new {string} and {string} and {string} and {string} shall {string} created")
	public void testBusinessInformation(String name, String address, String phoneNumber, String email, String result) {
		Business business = flexiBook.getBusiness();
		boolean match = business != null && business.getName().equals(name) && business.getAddress().equals(address)
				&& business.getPhoneNumber().equals(phoneNumber) && business.getEmail().equals(email);
		if (result.equals("be"))
			assertTrue(match);
		else
			assertTrue(!match);
	}

	/**
	 * @param errorMsg
	 * @param resultError
	 * @author Ing Tian
	 */
	@Then("an error message {string} shall {string} raised")
	public void testErrorMessage(String errorMsg, String resultError) {
		if (resultError.equals("be"))
			assertEquals(errorMsg, this.err);
		else
			assertEquals("", this.err);
	}

	/**
	 * @param errorMsg
	 * @param resultError
	 * @author Ing Tian
	 */
	@Then("an error message {string} shall {string} be raised")
	public void testErrorMessageInverse(String errorMsg, String resultError) {
		if (resultError.equals("not"))
			assertEquals("", this.err);
		else
			assertEquals(errorMsg, this.err);
	}

	/**
	 * Parse date of type String in format format into a java.sql.Date object.
	 *
	 * @param date
	 * @param format
	 * @return
	 * @throws ParseException
	 * @author Ing Tian
	 */
	private static Date parseDate(String date, String format) throws ParseException {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
		java.util.Date d = dateFormatter.parse(date);
		return new Date(d.getTime());
	}

	/**
	 * Parse time of type String in format format into a java.sql.Time object.
	 *
	 * @param time
	 * @param format
	 * @return
	 * @throws ParseException
	 * @author Ing Tian
	 */
	private static Time parseTime(String time, String format) throws ParseException {
		SimpleDateFormat timeFormatter = new SimpleDateFormat(format);
		java.util.Date t = timeFormatter.parse(time);
		return new Time(t.getTime());
	}

	/**
	 * 
	 * @param table
	 * @author Ing Tian
	 */
	@Given("a business exists with the following information:")
	public void thereIsABusiness(DataTable table) {
		List<Map<String, String>> rows = table.asMaps(String.class, String.class);
		Map<String, String> info = rows.get(0);
		String name = info.get("name"), address = info.get("address"), phoneNumber = info.get("phone number"),
				email = info.get("email");
		flexiBook.setBusiness(new Business(name, address, phoneNumber, email, flexiBook));
	}

	/**
	 * @param day
	 * @param startTime
	 * @param endTime
	 * @throws ParseException
	 * @author Ing Tian
	 */
	@Given("the business has a business hour on {string} with start time {string} and end time {string}")
	public void thereIsABusinessHour(String day, String startTime, String endTime) throws ParseException {

		BusinessHour.DayOfWeek dayOW = null;
		switch (day) {
		case "Monday":
			dayOW = BusinessHour.DayOfWeek.Monday;
			break;
		case "Tuesday":
			dayOW = BusinessHour.DayOfWeek.Tuesday;
			break;
		case "Wednesday":
			dayOW = BusinessHour.DayOfWeek.Wednesday;
			break;
		case "Thursday":
			dayOW = BusinessHour.DayOfWeek.Thursday;
			break;
		case "Friday":
			dayOW = BusinessHour.DayOfWeek.Friday;
			break;
		case "Saturday":
			dayOW = BusinessHour.DayOfWeek.Saturday;
			break;
		case "Sunday":
			dayOW = BusinessHour.DayOfWeek.Sunday;
			break;
		default:
			break;
		}

		Business business = flexiBook.getBusiness();
		BusinessHour businessHour = new BusinessHour(dayOW, parseTime(startTime, TIME_FORMAT),
				parseTime(endTime, TIME_FORMAT), flexiBook);
		business.addBusinessHour(businessHour);
	}

	/**
	 * @param day
	 * @param newStartTime
	 * @param newEndTime
	 * @throws ParseException
	 * @author Ing Tian
	 */
	@When("the user tries to add a new business hour on {string} with start time {string} and end time {string}")
	public void doAddNewBusinessHour(String day, String newStartTime, String newEndTime) throws ParseException {
		int dayOW = parseDayOfWeek(day);

		try {
			FlexiBookController.addNewBusinessHours(dayOW, parseTime(newStartTime, TIME_FORMAT),
					parseTime(newEndTime, TIME_FORMAT));
		} catch (Exception e) {
			this.err = e.getMessage();
			this.errorCtr += 1;
		}
	}

	/**
	 * @param result
	 * @author Ing Tian
	 */
	@Then("a new business hour shall {string} created")
	public void testExistsNewBusinessHour(String result) {
		if (result.equals("be"))
			assertTrue(flexiBook.getBusiness().getBusinessHours().size() > 1);
		else
			assertTrue(flexiBook.getBusiness().getBusinessHours().size() == 1);
	}

	private TOBusiness retrievedBusinessInfo;

	/**
	 * @author Ing Tian
	 */
	@When("the user tries to access the business information")
	public void doRetrieveBusinessInfo() {
		try {
			this.retrievedBusinessInfo = FlexiBookController.getBusinessInformation();
		} catch (Exception e) {
			this.err = e.getMessage();
			this.errorCtr += 1;
		}
	}

	/**
	 * @param name
	 * @param address
	 * @param phoneNumber
	 * @param email
	 * @author Ing Tian
	 */
	@Then("the {string} and {string} and {string} and {string} shall be provided to the user")
	public void testRetrievedInfoCorrect(String name, String address, String phoneNumber, String email) {
		assertTrue(this.retrievedBusinessInfo != null && name.equals(retrievedBusinessInfo.getName())
				&& address.equals(retrievedBusinessInfo.getAddress()) && phoneNumber.equals(phoneNumber)
				&& email.equals(retrievedBusinessInfo.getEmail()));
	}

	/**
	 * @param type
	 * @param startDate
	 * @param startTime
	 * @param endDate
	 * @param endTime
	 * @throws ParseException
	 * @author Ing Tian
	 */
	@Given("a {string} time slot exists with start time {string} at {string} and end time {string} at {string}")
	public void thereIsATimeSlot(String type, String startDate, String startTime, String endDate, String endTime)
			throws ParseException {
		TimeSlot ts = new TimeSlot(parseDate(startDate, DATE_FORMAT), parseTime(startTime, TIME_FORMAT),
				parseDate(endDate, DATE_FORMAT), parseTime(endTime, TIME_FORMAT), flexiBook);

		if (type.equals("holiday"))
			flexiBook.getBusiness().addHoliday(ts);
		else
			flexiBook.getBusiness().addVacation(ts);
	}

	/**
	 * @param type
	 * @param startDate
	 * @param startTime
	 * @param endDate
	 * @param endTime
	 * @throws ParseException
	 * @author Ing Tian
	 */
	@When("the user tries to add a new {string} with start date {string} at {string} and end date {string} at {string}")
	public void doAddTimeSlot(String type, String startDate, String startTime, String endDate, String endTime)
			throws ParseException {
		try {
			FlexiBookController.addNewTimeSlot(type, parseDate(startDate, DATE_FORMAT),
					parseTime(startTime, TIME_FORMAT), parseDate(endDate, DATE_FORMAT),
					parseTime(endTime, TIME_FORMAT));
		} catch (Exception e) {
			this.err = e.getMessage();
			this.errorCtr += 1;
		}
	}

	/**
	 * @param type
	 * @param result
	 * @param startDate
	 * @param startTime
	 * @param endDate
	 * @param endTime
	 * @throws ParseException
	 * @author Ing Tian
	 */
	@Then("a new {string} shall {string} be added with start date {string} at {string} and end date {string} at {string}")
	public void testThereIsTimeSlot(String type, String result, String startDate, String startTime, String endDate,
			String endTime) throws ParseException {

		Date sD = parseDate(startDate, DATE_FORMAT), eD = parseDate(endDate, DATE_FORMAT);
		Time sT = parseTime(startTime, TIME_FORMAT), eT = parseTime(endTime, TIME_FORMAT);

		Business business = flexiBook.getBusiness();

		boolean found = false;

		if (type.equals("holiday")) {
			for (TimeSlot ts : business.getHolidays())
				if (ts.getStartDate().equals(sD) && ts.getStartTime().equals(sT) && ts.getEndDate().equals(eD)
						&& ts.getEndTime().equals(eT)) {
					found = true;
					break;
				}
		} else
			for (TimeSlot ts : business.getVacation())
				if (ts.getStartDate().equals(sD) && ts.getStartTime().equals(sT) && ts.getEndDate().equals(eD)
						&& ts.getEndTime().equals(eT)) {
					found = true;
					break;
				}

		if (result.equals("be"))
			assertTrue(found);
		else
			assertFalse(found);
	}

	/**
	 * @param newName
	 * @param newAddress
	 * @param newPhoneNumber
	 * @param newEmail
	 * @author Ing Tian
	 */
	@When("the user tries to update the business information with new {string} and {string} and {string} and {string}")
	public void doUpdateBusinessInfomation(String newName, String newAddress, String newPhoneNumber, String newEmail) {
		try {
			TOBusiness businessInfo = new TOBusiness(newName, newPhoneNumber, newAddress, newEmail);
			FlexiBookController.updateBasicBusinessInformation(businessInfo);
		} catch (Exception e) {
			this.err = e.getMessage();
			this.errorCtr += 1;
		}
	}

	/**
	 * @param result
	 * @param name
	 * @param address
	 * @param phoneNumber
	 * @param email
	 * @author Ing Tian
	 */
	@Then("the business information shall {string} updated with new {string} and {string} and {string} and {string}")
	public void testBasicBusinessInformationUpdated(String result, String name, String address, String phoneNumber,
			String email) {
		Business business = flexiBook.getBusiness();
		boolean match = business.getName().equals(name) && business.getAddress().equals(address)
				&& business.getPhoneNumber().equals(phoneNumber) && business.getEmail().equals(email);
		if (result.equals("be"))
			assertTrue(match);
		else
			assertFalse(match);
	}

	/**
	 * Convert an String representing weekday into int.
	 *
	 * @param dayOfWeek
	 * @return
	 * @author Ing Tian
	 */
	private static int parseDayOfWeek(String dayOfWeek) {
		switch (dayOfWeek) {
		case "Monday":
			return 1;
		case "Tuesday":
			return 2;
		case "Wednesday":
			return 3;
		case "Thursday":
			return 4;
		case "Friday":
			return 5;
		case "Saturday":
			return 6;
		case "Sunday":
			return 7;
		default:
			return 1;
		}
	}

	/**
	 * @param originalDayOfWeek
	 * @param originalStartTime
	 * @param newDayOfWeek
	 * @param newStartTime
	 * @param newEndTime
	 * @author Ing Tian
	 */
	@When("the user tries to change the business hour {string} at {string} to be on {string} starting at {string} and ending at {string}")
	public void doUpdateBusinessHour(String originalDayOfWeek, String originalStartTime, String newDayOfWeek,
			String newStartTime, String newEndTime) {
		try {
			this.businessHourUpdated = false;
			int orgDayOW = parseDayOfWeek(originalDayOfWeek), newDayOW = parseDayOfWeek(newDayOfWeek);

			FlexiBookController.updateBusinessHour(orgDayOW, parseTime(originalStartTime, TIME_FORMAT), newDayOW,
					new TOTimeSlot(null, parseTime(newStartTime, TIME_FORMAT), null,
							parseTime(newEndTime, TIME_FORMAT)));
			this.businessHourUpdated = true;

		} catch (Exception e) {
			this.err = e.getMessage();
			this.errorCtr += 1;
		}
	}

	/**
	 * @param result
	 * @author Ing Tian
	 */
	@Then("the business hour shall {string} be updated")
	public void testBusinessHourUpdated(String result) {
		if (result.equals("be"))
			assertTrue(this.businessHourUpdated);
		else
			assertFalse(this.businessHourUpdated);
	}

	/**
	 * @param dayOfWeek
	 * @param startTime
	 * @author Ing Tian
	 */
	@When("the user tries to remove the business hour starting {string} at {string}")
	public void doRemoveBusinessHour(String dayOfWeek, String startTime) {
		try {

			int newDayOW = parseDayOfWeek(dayOfWeek);

			FlexiBookController.removeExisitingBusinessHour(newDayOW, parseTime(startTime, TIME_FORMAT));

		} catch (Exception e) {
			this.err = e.getMessage();
			this.errorCtr += 1;
		}
	}

	/**
	 * @param dayOfWeek
	 * @param startTime
	 * @param result
	 * @throws ParseException
	 * @author Ing Tian
	 */
	@Then("the business hour starting {string} at {string} shall {string} exist")
	public void testThereIsBusinessHour(String dayOfWeek, String startTime, String result) throws ParseException {
		BusinessHour.DayOfWeek newDayOW = BusinessHour.DayOfWeek.Monday;

		switch (dayOfWeek) {
		case "Monday":
			newDayOW = BusinessHour.DayOfWeek.Monday;
			break;
		case "Tuesday":
			newDayOW = BusinessHour.DayOfWeek.Tuesday;
			break;
		case "Wednesday":
			newDayOW = BusinessHour.DayOfWeek.Wednesday;
			break;
		case "Thursday":
			newDayOW = BusinessHour.DayOfWeek.Thursday;
			break;
		case "Friday":
			newDayOW = BusinessHour.DayOfWeek.Friday;
			break;
		case "Saturday":
			newDayOW = BusinessHour.DayOfWeek.Saturday;
			break;
		case "Sunday":
			newDayOW = BusinessHour.DayOfWeek.Sunday;
			break;
		default:
			break;
		}

		Time st = parseTime(startTime, TIME_FORMAT);

		boolean found = false;

		for (BusinessHour businessHour : flexiBook.getBusiness().getBusinessHours())
			if (newDayOW.compareTo(businessHour.getDayOfWeek()) < 0
					|| (newDayOW.compareTo(businessHour.getDayOfWeek()) == 0 && (st.before(businessHour.getStartTime())
							|| startTime.equals(businessHour.getStartTime()))))
				found = true;

		if (result.equals("not"))
			assertFalse(found);
		else
			assertTrue(found);
	}

	/**
	 * @param type
	 * @param orgStartDate
	 * @param orgStartTime
	 * @param newStartDate
	 * @param newStartTime
	 * @param newEndDate
	 * @param newEndTime
	 * @throws ParseException
	 * @author Ing Tian
	 */
	@When("the user tries to change the {string} on {string} at {string} to be with start date {string} at {string} and end date {string} at {string}")
	public void doUpdateTimeSlot(String type, String orgStartDate, String orgStartTime, String newStartDate,
			String newStartTime, String newEndDate, String newEndTime) throws ParseException {
		try {
			FlexiBookController.updateTimeSlotInformation(type, parseDate(orgStartDate, DATE_FORMAT),
					parseTime(orgStartTime, TIME_FORMAT),
					new TOTimeSlot(parseDate(newStartDate, DATE_FORMAT), parseTime(newStartTime, TIME_FORMAT),
							parseDate(newEndDate, DATE_FORMAT), parseTime(newEndTime, TIME_FORMAT)));
		} catch (Exception e) {
			this.err = e.getMessage();
			this.errorCtr += 1;
		}
	}

	/**
	 * @param type
	 * @param result
	 * @param startDate
	 * @param startTime
	 * @param endDate
	 * @param endTime
	 * @throws ParseException
	 * @author Ing Tian
	 */
	@Then("the {string} shall {string} updated with start date {string} at {string} and end date {string} at {string}")
	public void testTimeSlotUpdated(String type, String result, String startDate, String startTime, String endDate,
			String endTime) throws ParseException {
		boolean found = false;
		Date sd = parseDate(startDate, DATE_FORMAT), ed = parseDate(endDate, DATE_FORMAT);
		Time st = parseTime(startTime, TIME_FORMAT), et = parseTime(endTime, TIME_FORMAT);

		Business business = flexiBook.getBusiness();
		if (type.equals("holiday")) {
			for (TimeSlot ts : business.getHolidays())
				if (ts.getStartDate().equals(sd) && ts.getStartTime().equals(st) && ts.getEndDate().equals(ed)
						&& ts.getEndTime().equals(et)) {
					found = true;
					break;
				}
		} else
			for (TimeSlot ts : business.getVacation())
				if (ts.getStartDate().equals(sd) && ts.getStartTime().equals(st) && ts.getEndDate().equals(ed)
						&& ts.getEndTime().equals(et)) {
					found = true;
					break;
				}

		if (result.equals("be"))
			assertTrue(found);
		else
			assertFalse(found);
	}

	/**
	 * @param type
	 * @param startDate
	 * @param startTime
	 * @param endDate
	 * @param endTime
	 * @author Ing Tian
	 */
	@When("the user tries to remove an existing {string} with start date {string} at {string} and end date {string} at {string}")
	public void doRemoveTimeSlot(String type, String startDate, String startTime, String endDate, String endTime) {
		try {
			FlexiBookController.removeTimeSlot(type,
					new TOTimeSlot(parseDate(startDate, DATE_FORMAT), parseTime(startTime, TIME_FORMAT),
							parseDate(endDate, DATE_FORMAT), parseTime(endTime, TIME_FORMAT)));
		} catch (Exception e) {
			this.err = e.getMessage();
			this.errorCtr += 1;
		}
	}

	/**
	 * @param type
	 * @param startDate
	 * @param startTime
	 * @param result
	 * @throws ParseException
	 * @author Ing Tian
	 */
	@Then("the {string} with start date {string} at {string} shall {string} exist")
	public void testTimeSlotRemoved(String type, String startDate, String startTime, String result)
			throws ParseException {
		Date sd = parseDate(startDate, DATE_FORMAT);
		Time st = parseTime(startTime, TIME_FORMAT);

		boolean found = false;

		Business business = flexiBook.getBusiness();
		if (type.equals("holiday")) {
			for (TimeSlot ts : business.getHolidays())
				if (ts.getStartDate().equals(sd) && ts.getStartTime().equals(st)) {
					found = true;
					break;
				}
		} else
			for (TimeSlot ts : business.getVacation())
				if (ts.getStartDate().equals(sd) && ts.getStartTime().equals(st)) {
					found = true;
					break;
				}

		if (result.equals("not"))
			assertFalse(found);
		else
			assertTrue(found);
	}

	List<List<TOTimeSlot>> view_result = new ArrayList<List<TOTimeSlot>>();

	/**
	 * @param username
	 * @param password
	 * @author Ao Shen
	 */
	@When("the user tries to log in with username {string} and password {string}")
	public void userLoggedin(String username, String password) {
		try {
			FlexiBookController.Login(username, password);
		} catch (InvalidInputException e) {
			err += e.getMessage();
			errorCtr++;
		}
	}

	/**
	 * @author Ao Shen
	 */
	@Then("the user should be successfully logged in")
	public void loginsuccessfully() {
		assertTrue(FlexiBookApplication.getCurrentUser() != null);
	}

	/**
	 * @author Ao Shen
	 */
	@Then("the user shall be successfully logged in")
	public void ownerloginsuccessfully() {
		assertTrue(FlexiBookApplication.getCurrentUser() != null);
	}

	/**
	 * @author Ao Shen
	 */
	@Then("the user should not be logged in")
	public void loginnotsuccessfully() {
		assertTrue(FlexiBookApplication.getCurrentUser() == null);
	}

	/**
	 * @author Ao Shen
	 */
	@Given("the user is logged out")
	public void logout() {
		try {
			FlexiBookController.Logout();
		} catch (InvalidInputException e) {

		}
	}

	/**
	 * @param user
	 * @param datein
	 * @throws InvalidInputException
	 * @author Ao Shen
	 */
	@When("{string} requests the appointment calendar for the week starting on {string}")
	public void viewweek(String user, String datein) throws InvalidInputException {
		try {
			FlexiBookApplication.setCurrentUser(findUser(user));
			view_result = FlexiBookController.viewCalendar(datein, false);
		} catch (InvalidInputException e) {
			err += e.getMessage();
			errorCtr++;
		}
	}

	/**
	 * @param user
	 * @param datein
	 * @throws Exception
	 * @author Ao Shen
	 */
	@When("{string} requests the appointment calendar for the day of {string}")
	public void viewday(String user, String datein) throws Exception {
		try {
			FlexiBookApplication.setCurrentUser(findUser(user));
			view_result = FlexiBookController.viewCalendar(datein, true);
		} catch (Exception e) {
			err += e.getMessage();
			errorCtr++;
		}
	}

	/**
	 * @param table
	 * @author Ao Shen
	 */
	@Then("the following slots shall be unavailable:")
	public void unavailableview(DataTable table) {
		boolean result = true;
		List<Map<String, String>> rows = table.asMaps(String.class, String.class);
		int i = 0;
		for (Map<String, String> row : rows) {
			String date = (view_result.get(0)).get(i).getStartDate().toString();
			String starttime = ((view_result.get(0)).get(i).getStartTime().toString()).substring(0,
					((view_result.get(0)).get(i).getStartTime().toString().length() - 3));
			String endtime = (view_result.get(0)).get(i).getEndTime().toString().substring(0,
					((view_result.get(0)).get(i).getEndTime().toString().length() - 3));
			if (starttime.compareTo("10:00") < 0) {
				starttime = starttime.substring(1, starttime.length());
			}
			if (endtime.compareTo("10:00") < 0) {
				endtime = endtime.substring(1, endtime.length());
			}

			if ((row.get("date").compareTo(date) != 0) || (row.get("startTime").compareTo(starttime) != 0)
					|| (row.get("endTime").compareTo(endtime) != 0)) {
				result = false;
			}
			i++;
		}
		assertTrue(result);
	}

	/**
	 * @param table
	 * @author Ao Shen
	 */
	@Then("the following slots shall be available:")
	public void availableview(DataTable table) {
		boolean result = true;
		List<Map<String, String>> rows = table.asMaps(String.class, String.class);
		int i = 0;
		for (Map<String, String> row : rows) {
			String date = (view_result.get(1)).get(i).getStartDate().toString();
			String starttime = ((view_result.get(1)).get(i).getStartTime().toString()).substring(0,
					((view_result.get(0)).get(i).getStartTime().toString().length() - 3));
			String endtime = (view_result.get(1)).get(i).getEndTime().toString().substring(0,
					((view_result.get(0)).get(i).getEndTime().toString().length() - 3));
			if (starttime.compareTo("10:00") < 0) {
				starttime = starttime.substring(1, starttime.length());
			}
			if (endtime.compareTo("10:00") < 0) {
				endtime = endtime.substring(1, endtime.length());
			}

			if ((row.get("date").compareTo(date) != 0) || (row.get("startTime").compareTo(starttime) != 0)
					|| (row.get("endTime").compareTo(endtime) != 0)) {
				result = false;
			}
			i++;
		}
		assertTrue(result);
	}

	/**
	 * @throws InvalidInputException
	 * @author Ao Shen
	 */
	@When("the user tries to log out")
	public void trylogout() {
		try {
			FlexiBookController.Logout();
		} catch (InvalidInputException e) {
			err += e.getMessage();
			errorCtr++;
		}
	}

	@Then("a new account shall be created")
	public void thesystemhasnewUser() {
		assertTrue(flexiBook.getOwner() != null);
	}

//---------------------------- Byron Chen -------------------------------------------------------------

	/**
	 * @param a
	 * @author Byron Chen
	 */

	@Then("there shall be {int} more appointment in the system")
	public void theyShallBeApp(int a) {

		assertEquals(a, flexiBook.getAppointments().size() - i);
//		assertTrue(true);	//==================================================================================
	}

	/**
	 * @param user
	 * @author Byron Chen
	 */

	@Given("{string} is logged in to their account")
	public void isLoggedIn(String user) {
		// Write code here that turns the phrase above into concrete actions
		FlexiBookApplication.setCurrentUser(findUser(user));
	}

	/**
	 * @param user
	 * @param date
	 * @param aBookableService
	 * @param time
	 * @author Byron Chen
	 */

	@When("{string} schedules an appointment on {string} for {string} at {string}")
	public void schedulesAnAppointmentOn(String user, String date, String aBookableService, String time) {
		// Write code here that turns the phrase above into concrete actions
		try {

			FlexiBookController.makeAppoinment(Date.valueOf(date), aBookableService, "none",
					Time.valueOf(time + ":00"));
		} catch (Exception e) {
			this.err = e.getMessage();
			this.errorCtr += 1;
		}
	}

	/**
	 * @param user
	 * @param aBookableService
	 * @param date
	 * @param startTime
	 * @param endTime
	 * @author Byron Chen
	 */

	@Then("{string} shall have a {string} appointment on {string} from {string} to {string}")
	public void haveAnAppointment(String user, String aBookableService, String date, String startTime, String endTime) {
		// Write code here that turns the phrase above into concrete actions
		int i = 0;
		boolean flag = false;
		Appointment thisAppointment = null;

		while (i < ((Customer) findUser(user)).getAppointments().size()) {
			thisAppointment = ((Customer) findUser(user)).getAppointment(i);

			if (// thisAppointment.getCustomer().getUsername().equals(string) &&
			thisAppointment.getBookableService().getName().equals(aBookableService)
					&& thisAppointment.getTimeSlot().getStartDate().equals(Date.valueOf(date))
					&& thisAppointment.getTimeSlot().getStartTime().equals(Time.valueOf(startTime + ":00"))
					&& thisAppointment.getTimeSlot().getEndTime().equals(Time.valueOf(endTime + ":00"))) {
				flag = true;
				break;
			}
			i++;
		}
		assertTrue(flag);
	}

	/**
	 * find the comboItem by the string of comboName and ComboItemName
	 *
	 * @param comboName
	 * @param comboItemName
	 * @return
	 * @author Byron Chen
	 */

	private ComboItem findComboItem(String comboName, String comboItemName) {
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
	 * @param user
	 * @param date
	 * @param aBookableService
	 * @param items
	 * @param time
	 * @author Byron Chen
	 */

	@When("{string} schedules an appointment on {string} for {string} with {string} at {string}")
	public void schedulesAppointmentWith(String user, String date, String aBookableService, String items, String time) {

		try {

			FlexiBookController.makeAppoinment(Date.valueOf(date), aBookableService, items, Time.valueOf(time + ":00"));
		} catch (Exception e) {
			this.err = e.getMessage();
			this.errorCtr += 1;
		}
	}

	/**
	 * @param string
	 * @author Byron Chen
	 */

	@Then("the system shall report {string}")
	public void systemReports(String string) {
		assertTrue(err.contains(string));
	}

	/**
	 * @param user
	 * @param aBookableService
	 * @param date
	 * @param time
	 * @author Byron Chen
	 */

	@When("{string} attempts to cancel their {string} appointment on {string} at {string}")
	public void cancelAppointment(String user, String aBookableService, String date, String time) {

		try {

			FlexiBookController.cancelAppointment(Date.valueOf(date), Time.valueOf(time + ":00"), aBookableService);

		} catch (Exception e) {
			this.err = e.getMessage();
			this.errorCtr += 1;
		}
	}

	/**
	 * @param user
	 * @param aBookableService
	 * @param date
	 * @param time
	 * @author Byron Chen
	 */

	@Then("{string}'s {string} appointment on {string} at {string} shall be removed from the system")
	public void removeSbAppointment(String user, String aBookableService, String date, String time) {

		int i = 0;
		boolean flag = true;
		Appointment thisAppointment = null;

		while (i < ((Customer) findUser(user)).getAppointments().size()) {
			thisAppointment = ((Customer) findUser(user)).getAppointment(i);

			if (thisAppointment.getCustomer().getUsername().equals(user)
					&& thisAppointment.getBookableService().getName().equals(aBookableService)
					&& thisAppointment.getTimeSlot().getStartDate().equals(Date.valueOf(date))
					&& thisAppointment.getTimeSlot().getStartTime().equals(Time.valueOf(time + ":00"))) {
				flag = false;
				break;
			}
			i++;
		}
		assertTrue(flag);
	}

	/**
	 * @param a
	 * @author Byron Chen
	 */

	@Then("there shall be {int} less appointment in the system")
	public void lessAppointment(Integer a) {

		assertEquals(a, i - flexiBook.getAppointments().size());
	}

	/**
	 * @param user
	 * @param user2
	 * @param aBookableService
	 * @param date
	 * @param time
	 * @author Byron Chen
	 */

	@When("{string} attempts to cancel {string}'s {string} appointment on {string} at {string}")
	public void cancelAppointment(String user, String user2, String aBookableService, String date, String time) {

		try {

			FlexiBookController.cancelAppointment(Date.valueOf(date), Time.valueOf(time + ":00"), aBookableService);

		} catch (Exception e) {
			this.err = e.getMessage();
			this.errorCtr += 1;
		}
	}

	private static String updatedResultOfAppointment;

	/**
	 * @param customer
	 * @param aBookableService
	 * @param oldDate
	 * @param oldTime
	 * @param newDate
	 * @param newTime
	 * @author Byron Chen
	 */

	@When("{string} attempts to update their {string} appointment on {string} at {string} to {string} at {string}")
	public void updateAppointmentsTo(String customer, String aBookableService, String oldDate, String oldTime,
			String newDate, String newTime) {

		try {

			FlexiBookController.updateAppoinment(customer, Date.valueOf(oldDate), Time.valueOf(oldTime + ":00"),
					aBookableService, Date.valueOf(newDate), Time.valueOf(newTime + ":00"), "none", "");

		} catch (Exception e) {
			this.err = e.getMessage();

			if (err != null) {
				updatedResultOfAppointment = err;
			}

			this.errorCtr += 1;
		}

	}

	/**
	 * @param string
	 * @author Byron Chen
	 */

	@Then("the system shall report that the update was {string}")
	public void systemReportsUpdate(String string) {
		assertEquals(string, updatedResultOfAppointment);

	}

	/**
	 * get the end time when knows the start time and duration
	 *
	 * @param startTime
	 * @param duration
	 * @return
	 * @author Byron Chen
	 */
	private static Time getEndTime(Time startTime, int duration) {
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
	 * @param customer
	 * @param aBookableService
	 * @param optionalSevices
	 * @param startDate
	 * @param startTime
	 * @author Byron Chen
	 */

	@Given("{string} has a {string} appointment with optional sevices {string} on {string} at {string}")
	public void appointmentWithOptionalService(String customer, String aBookableService, String optionalSevices,
			String startDate, String startTime) {

		ServiceCombo comboService = findServiceCombo(aBookableService);
		List<ComboItem> comboItems = comboService.getServices();

		String mainServiceName = comboService.getMainService().getService().getName();
		optionalSevices += "," + mainServiceName;
		String[] optServiceList = optionalSevices.split(",");
		List<String> optSL = Arrays.asList(optServiceList);

		TimeSlot ts = new TimeSlot(Date.valueOf(startDate), Time.valueOf(startTime + ":00"), Date.valueOf(startDate),
				getEndTime(Time.valueOf(startTime + ":00"), 1), flexiBook);

		Appointment newAppointment = new Appointment((Customer) findUser(customer), comboService, ts, flexiBook);

		for (ComboItem item : comboItems) {
			String itemName = item.getService().getName();
			if (optSL.contains(itemName))
				newAppointment.addChosenItem(item);
		}

		newAppointment.getTimeSlot().setEndTime(
				getEndTime(Time.valueOf(startTime + ":00"), getDurationOfCombo(newAppointment.getChosenItems())));

		flexiBook.addAppointment(newAppointment);

		i++; // ????????????????????????????
	}

	/**
	 * Helper Method: Calculate the duration for a Service Combo
	 *
	 * @return
	 * @throws InvalidInputException
	 * @auther Byron Chen, Mcgill ID: 260892558
	 */

	private static int getDurationOfCombo(List<ComboItem> Services) {
		int duration = 0; // get the duration of the main service

		for (int i = 0; i < Services.size(); i++) { // add durations of each comboItem chosen together
			duration = duration + Services.get(i).getService().getDuration();

		}

		return duration;
	}

	/**
	 * @param customer
	 * @param action
	 * @param optionalSevice
	 * @param aBookableService
	 * @param date
	 * @param time
	 * @author Byron Chen
	 */

	@When("{string} attempts to {string} {string} from their {string} appointment on {string} at {string}")
	public void attemptsToFromTheirAppointmentOnAt(String customer, String action, String optionalSevice,
			String aBookableService, String date, String time) {

		try {

			FlexiBookController.updateAppoinment(customer, Date.valueOf(date), Time.valueOf(time + ":00"),
					aBookableService, null, null, optionalSevice, action);

		} catch (Exception e) {
			this.err = e.getMessage();

			if (err != null) {
				updatedResultOfAppointment = err;
			}

			this.errorCtr += 1;
		}

	}

	/**
	 * @param user1
	 * @param user2
	 * @param aBookableService
	 * @param oldDate
	 * @param oldTime
	 * @param newDate
	 * @param newTime
	 * @author Byron Chen
	 */

	@When("{string} attempts to update {string}'s {string} appointment on {string} at {string} to {string} at {string}")
	public void updateSbAppointment(String user1, String user2, String aBookableService, String oldDate, String oldTime,
			String newDate, String newTime) {

		try {

			FlexiBookController.updateAppoinment(user2, Date.valueOf(oldDate), Time.valueOf(oldTime + ":00"),
					aBookableService, Date.valueOf(newDate), Time.valueOf(newTime + ":00"), "none", "");

		} catch (Exception e) {
			this.err = e.getMessage();

			if (err != null) {
				updatedResultOfAppointment = err;
			}

			this.errorCtr += 1;
		}

	}

//============================================================================================================

	/**
	 * @param dataTable
	 * @auther kevinli
	 */
	@Given("the business has the following opening hours:")
	public void businessHasOpeningHours(io.cucumber.datatable.DataTable dataTable) {

		List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
		for (Map<String, String> info : rows) {
			String day = info.get("day"), start = info.get("startTime"), end = info.get("endTime");
			DayOfWeek a = null;
			if (day.equals("Monday")) {
				a = BusinessHour.DayOfWeek.Monday;
			} else if (day.equals("Tuesday")) {
				a = BusinessHour.DayOfWeek.Tuesday;
			} else if (day.equals("Wednesday")) {
				a = BusinessHour.DayOfWeek.Wednesday;
			} else if (day.equals("Thursday")) {
				a = BusinessHour.DayOfWeek.Thursday;
			} else if (day.equals("Friday")) {
				a = BusinessHour.DayOfWeek.Friday;
			} else if (day.equals("Saturday")) {
				a = BusinessHour.DayOfWeek.Saturday;
			} else if (day.equals("Sunday")) {
				a = BusinessHour.DayOfWeek.Sunday;
			}
			Time st = Time.valueOf(start + ":00");
			Time et = Time.valueOf(end + ":00");
			BusinessHour Toadd = new BusinessHour(a, st, et, this.flexiBook);
			this.flexiBook.getBusiness().addBusinessHour(Toadd);

		}
	}

	/**
	 * @param dataTable
	 * @auther kevinli
	 */
	@Given("the business has the following holidays:")
	public void businessHasHolidays(io.cucumber.datatable.DataTable dataTable) {

		// TimeSlot(Date aStartDate, Time aStartTime, Date aEndDate, Time aEndTime,
		// FlexiBook aFlexiBook)

		List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
		for (Map<String, String> info : rows) {
			String sd = info.get("startDate"), ed = info.get("endDate"), st = info.get("startTime"),
					et = info.get("endTime");
			TimeSlot toAdd = new TimeSlot(Date.valueOf(sd), Time.valueOf(st + ":00"), Date.valueOf(ed),
					Time.valueOf(et + ":00"), this.flexiBook);
			this.flexiBook.getBusiness().addHoliday(toAdd);
		}

	}

// Iteration 3 ===============================================================================================

	private Appointment currentAppointment = null;

	/**
	 * returns how many no-show records
	 * 
	 * @param username
	 * @param noshow
	 * @author P7Team
	 */

	@Given("{string} has {int} no-show records")
	public void noShowRecords(String username, Integer noshow) {
		((Customer) findUser(username)).setGugutime(noshow);
	}

	/**
	 * user makes an appointment
	 * 
	 * @param username
	 * @param service
	 * @param date
	 * @param time
	 * @param systemTime
	 * @author P7Team
	 */

	@When("{string} makes a {string} appointment for the date {string} and time {string} at {string}")
	public void makeAnAppointment(String username, String service, String date, String time, String systemTime) {
		FlexiBookApplication.setCurrentUser(findUser(username));
		String cdate = systemTime.split("\\+")[0];
		String ctime = systemTime.split("\\+")[1];
		FlexiBookApplication.setCurrentDate(Date.valueOf(cdate));
		FlexiBookApplication.setCurrentTime(Time.valueOf(ctime + ":00"));
		try {
			FlexiBookController.makeAppoinment(Date.valueOf(date), service, "none", Time.valueOf(time + ":00"));
			currentAppointment = this.findApp(Date.valueOf(date), Time.valueOf(time + ":00"), username,
					this.findServicePlus(service).getName());
		} catch (Exception e) {
			this.err = e.getMessage();
			this.errorCtr += 1;
		}
	}

	/**
	 * user changes an appointment
	 * 
	 * @param username
	 * @param newServiceName
	 * @param sysTime
	 * @author P7Team
	 */
	@When("{string} attempts to change the service in the appointment to {string} at {string}")
	public void changeAnAppointment(String username, String newServiceName, String sysTime) {
		// Write code here that turns the phrase above into concrete actions
		try {
			String[] timeInfo = sysTime.split("\\+");
			TimeSlot currentTs = currentAppointment.getTimeSlot();
			FlexiBookController.changeAppointmentService(Date.valueOf(timeInfo[0]), Time.valueOf(timeInfo[1] + ":00"),
					currentTs.getStartDate(), currentTs.getStartTime(), username, newServiceName);
		} catch (Exception e) {
			this.err = e.getMessage();
			this.errorCtr += 1;
		}
	}

	/**
	 * check the appointment is booked
	 * 
	 * @author P7Team
	 */
	@Then("the appointment shall be booked")
	public void appointmentBooked() {
		assertTrue(currentAppointment != null);
	}

	/**
	 * check the services of appointment
	 * 
	 * @param string
	 * @author P7Team
	 */
	@Then("the service in the appointment shall be {string}")
	public void serviceInTheAppointment(String string) {
		boolean flag = false;
		if (currentAppointment != null) {
			if (currentAppointment.getBookableService().getName().equals(string))
				flag = true;
		}
		assertTrue(flag);
	}

	/**
	 * check the date, start time and end time of an appointment
	 * 
	 * @param string
	 * @param string2
	 * @param string3
	 * @author P7Team
	 */
	@Then("the appointment shall be for the date {string} with start time {string} and end time {string}")
	public void appointmentInformation(String string, String string2, String string3) {
		Customer user = (Customer) FlexiBookApplication.getCurrentUser();
		Date date = Date.valueOf(string);
		Time startTime = Time.valueOf(string2 + ":00");
		Time endTime = Time.valueOf(string3 + ":00");
		boolean flag = false;
		if (currentAppointment != null) {
			System.out.println(currentAppointment.getTimeSlot().getStartDate().toString());
			System.out.println(currentAppointment.getBookableService().getName());
			if (currentAppointment.getTimeSlot().getStartDate().equals(date)
					&& currentAppointment.getTimeSlot().getStartTime().equals(startTime)
					&& currentAppointment.getTimeSlot().getEndTime().equals(endTime)) {
				flag = true;
			}
		}
		assertTrue(flag);
	}

	/**
	 * check the username associated to the appointment
	 * 
	 * @param string
	 * @author P7Team
	 */
	@Then("the username associated with the appointment shall be {string}")
	public void usernameForAppointment(String string) {
		boolean flag = false;
		if (currentAppointment.getCustomer().getUsername().equals(string))
			flag = true;
		assertTrue(flag);
	}

	/**
	 * the number of no-show records
	 * 
	 * @param string
	 * @param int1
	 * @author P7Team
	 */
	@Then("the user {string} shall have {int} no-show records")
	public void noShowRecordsNumber(String string, Integer int1) {
		int a = ((Customer) (findUser(string))).getGugutime();
		System.out.println(currentAppointment);
		System.out.println("fuck");
		assertEquals(int1.intValue(), a);
	}

	/**
	 * number of appointments in the system
	 * 
	 * @param int1
	 * @author P7Team
	 */
	@Then("the system shall have {int} appointments")
	public void haveAppointments(Integer int1) {
		assertEquals(int1.intValue(), flexiBook.getAppointments().size());
	}

	/**
	 * find a certain appointment
	 * 
	 * @param date
	 * @param time
	 * @param username
	 * @param servicename
	 * @return
	 * @author P7Team
	 */
	private Appointment findApp(Date date, Time time, String username, String servicename) {
		Appointment result = null;
		for (Appointment a : flexiBook.getAppointments()) {
			if (a.getTimeSlot().getStartDate().equals(date) && a.getTimeSlot().getStartTime().equals(time)
					&& a.getCustomer().getUsername().equals(username)
					&& a.getBookableService().getName().equals(servicename)) {
				result = a;
			}

		}
		return result;

	}

	/**
	 * makes an appointment without optional services
	 * 
	 * @param username
	 * @param comboName
	 * @param startDate
	 * @param startTime
	 * @param sysDateAndTime
	 * @author P7Team
	 */
	@When("{string} makes a {string} appointment without choosing optional services for the date {string} and time {string} at {string}")
	public void appointmentWithoutOptional(String username, String comboName, String startDate, String startTime,
			String sysDateAndTime) {
		FlexiBookApplication.setCurrentUser(findUser(username));
		String cdate = sysDateAndTime.split("\\+")[0];
		String ctime = sysDateAndTime.split("\\+")[1];
		FlexiBookApplication.setCurrentDate(Date.valueOf(cdate));
		FlexiBookApplication.setCurrentTime(Time.valueOf(ctime + ":00"));

		ServiceCombo combo = null;
		for (BookableService item : this.flexiBook.getBookableServices())
			if (item instanceof ServiceCombo && item.getName().equals(comboName)) {
				combo = (ServiceCombo) item;
				break;
			}

		List<ComboItem> listOfServices = new ArrayList<>();
		for (ComboItem item : combo.getServices())
			if (item.getMandatory())
				listOfServices.add(item);

		String items = "";

		for (ComboItem CI : listOfServices) {
			items += CI.getService().getName() + ",";
		}

		items.charAt(items.length() - 1); // remove the last ','

		try {
			FlexiBookController.makeAppoinment(Date.valueOf(startDate), combo.getName(), items,
					Time.valueOf(startTime + ":00"));

			this.currentAppointment = this.findApp(Date.valueOf(startDate), Time.valueOf(startTime + ":00"), username,
					combo.getName());
			System.out.println(currentAppointment);
			System.out.println(currentAppointment.getTimeSlot().getStartDate());
			System.out.println("bith");
		} catch (Exception e) {
			this.err = e.getMessage();
			this.errorCtr += 1;
		}
	}

	/**
	 * adds optional services
	 * 
	 * @param username
	 * @param newOptionalService
	 * @param sysDateAndTime
	 * @author P7Team
	 */
	@When("{string} attempts to add the optional service {string} to the service combo in the appointment at {string}")
	public void addOptional(String username, String newOptionalService, String sysDateAndTime) {
		FlexiBookApplication.setCurrentUser(findUser(username));
		String cdate = sysDateAndTime.split("\\+")[0];
		String ctime = sysDateAndTime.split("\\+")[1];
		FlexiBookApplication.setCurrentDate(Date.valueOf(cdate));
		FlexiBookApplication.setCurrentTime(Time.valueOf(ctime + ":00"));

		ComboItem opService = null;

		ServiceCombo serviceCombo = (ServiceCombo) currentAppointment.getBookableService();
		for (ComboItem item : serviceCombo.getServices())
			if (item.getService().getName().equals(newOptionalService)) {
				opService = item;
				break;
			}

		try {
			FlexiBookController.updateAppoinment(username, currentAppointment.getTimeSlot().getStartDate(),
					currentAppointment.getTimeSlot().getStartTime(), serviceCombo.getName(), null, null,
					opService.getService().getName(), "add");
		} catch (Exception e) {
			this.err = e.getMessage();
			this.errorCtr += 1;
		}
	}

	/**
	 * check the service combo
	 * 
	 * @param string
	 * @author P7Team
	 */
	@Then("the service combo in the appointment shall be {string}")
	public void comboInTheAppointment(String string) {
		String res = currentAppointment.getChosenItem(0).getServiceCombo().getName();
		assertTrue(string.equals(res));
	}

	/**
	 * check the selected services
	 * 
	 * @param string
	 * @author P7Team
	 */
	@Then("the service combo shall have {string} selected services")
	public void haveSelectedServices(String string) {
		String resultString = string + ',';
		List<ComboItem> selectedServices = currentAppointment.getChosenItems();
		boolean flag = true;
		for (ComboItem service : selectedServices) {
			if (resultString.contains(service.getService().getName() + ',') == false) {
				flag = false;
			}
		}

		assertTrue(flag);
	}

	/**
	 * user updates the start time
	 * 
	 * @param username
	 * @param newStartDate
	 * @param newStartTime
	 * @param sysTime
	 * @author P7Team
	 */
	@When("{string} attempts to update the date to {string} and time to {string} at {string}")
	public void updateDateAndTime(String username, String newStartDate, String newStartTime, String sysTime) {
		FlexiBookApplication.setCurrentUser(findUser(username));
		String cdate = sysTime.split("\\+")[0];
		String ctime = sysTime.split("\\+")[1];
		FlexiBookApplication.setCurrentDate(Date.valueOf(cdate));
		FlexiBookApplication.setCurrentTime(Time.valueOf(ctime + ":00"));
		try {
			FlexiBookController.updateAppoinment(username, currentAppointment.getTimeSlot().getStartDate(),
					currentAppointment.getTimeSlot().getStartTime(), currentAppointment.getBookableService().getName(),
					Date.valueOf(newStartDate), Time.valueOf(newStartTime + ":00"), "none", "Update date and time");
		} catch (Exception e) {
			this.err = e.getMessage();
			this.errorCtr += 1;
		}
	}

	/**
	 * user cancels the appointment
	 * 
	 * @param string
	 * @param systemTime
	 * @author P7Team
	 */
	@When("{string} attempts to cancel the appointment at {string}")
	public void cancelAppointmentAt(String string, String systemTime) {
		FlexiBookApplication.setCurrentUser(findUser(string));
		String cdate = systemTime.split("\\+")[0];
		String ctime = systemTime.split("\\+")[1];
		FlexiBookApplication.setCurrentDate(Date.valueOf(cdate));
		FlexiBookApplication.setCurrentTime(Time.valueOf(ctime + ":00"));
		try {
			FlexiBookController.cancelAppointment(currentAppointment.getTimeSlot().getStartDate(),
					currentAppointment.getTimeSlot().getStartTime(), currentAppointment.getBookableService().getName());
		} catch (Exception e) {
			this.err = e.getMessage();
			this.errorCtr += 1;
		}
	}

	/**
	 * check number of appointments
	 * 
	 * @param int1
	 * @author P7Team
	 */
	@Then("the system shall have {int} appointment")
	public void systemHasAppointment(Integer int1) {
		assertEquals(int1.intValue(), flexiBook.getAppointments().size());
	}

	/**
	 * set the start time
	 * 
	 * @param systemTime
	 * @author P7Team
	 */
	@When("the owner starts the appointment at {string}")
	public void ownerStartsAppointment(String systemTime) {
		String cdate = systemTime.split("\\+")[0];
		String ctime = systemTime.split("\\+")[1];
		FlexiBookApplication.setCurrentDate(Date.valueOf(cdate));
		FlexiBookApplication.setCurrentTime(Time.valueOf(ctime + ":00"));

		FlexiBookController.startApp(currentAppointment.getTimeSlot().getStartDate(),
				currentAppointment.getTimeSlot().getStartTime());
	}

	/**
	 * @author P7Team
	 */
	@Then("the appointment shall be in progress")
	public void appointmentInProgress() {
		assertEquals("InProgress", currentAppointment.getAppointmentStatusFullName());

	}

	/**
	 * set the end time
	 * 
	 * @param systemTime
	 * @author P7Team
	 */
	@When("the owner ends the appointment at {string}")
	public void ownerEndsAppointment(String systemTime) {
		String cdate = systemTime.split("\\+")[0];
		String ctime = systemTime.split("\\+")[1];
		FlexiBookApplication.setCurrentDate(Date.valueOf(cdate));
		FlexiBookApplication.setCurrentTime(Time.valueOf(ctime + ":00"));

		FlexiBookController.endApp(currentAppointment.getTimeSlot().getStartDate(),
				currentAppointment.getTimeSlot().getStartTime());

	}

	/**
	 * set a no-show
	 * 
	 * @param systemTime
	 * @author P7Team
	 */
	@When("the owner attempts to register a no-show for the appointment at {string}")
	public void registerARecord(String systemTime) {
		String cdate = systemTime.split("\\+")[0];
		String ctime = systemTime.split("\\+")[1];
		FlexiBookApplication.setCurrentDate(Date.valueOf(cdate));
		FlexiBookApplication.setCurrentTime(Time.valueOf(ctime + ":00"));

		try{
			FlexiBookController.registerNoShow(currentAppointment.getTimeSlot().getStartDate(),
					currentAppointment.getTimeSlot().getStartTime());
		}
		catch(Exception e){

		}
	}

	/**
	 * owner ends appointment
	 * 
	 * @param systemTime
	 * @author P7Team
	 */
	@When("the owner attempts to end the appointment at {string}")
	public void endAppointmentAt(String systemTime) {
		String cdate = systemTime.split("\\+")[0];
		String ctime = systemTime.split("\\+")[1];
		FlexiBookApplication.setCurrentDate(Date.valueOf(cdate));
		FlexiBookApplication.setCurrentTime(Time.valueOf(ctime + ":00"));

		FlexiBookController.endApp(currentAppointment.getTimeSlot().getStartDate(),
				currentAppointment.getTimeSlot().getStartTime());
	}
}

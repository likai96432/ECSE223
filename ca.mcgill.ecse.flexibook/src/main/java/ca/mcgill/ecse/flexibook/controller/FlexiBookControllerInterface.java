package ca.mcgill.ecse.flexibook.controller;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

import ca.mcgill.ecse.flexibook.model.BookableService;
import ca.mcgill.ecse.flexibook.model.ComboItem;
import ca.mcgill.ecse.flexibook.model.FlexiBook;
import ca.mcgill.ecse.flexibook.model.Service;
import ca.mcgill.ecse.flexibook.model.ServiceCombo;
import ca.mcgill.ecse.flexibook.model.TimeSlot;

public interface FlexiBookControllerInterface {
	public static void signUpCustomerAccount(String username, String password) throws InvalidInputException {
	};

	public static void updateAccount(String newname, String password) throws InvalidInputException {
	};

	public static void deleteCustomerAccount(String target) throws InvalidInputException {
	};

	public static void setupBasicBusinessInformation(String name, String address, String email, String phoneNumber)
			throws InvalidInputException {
	}

	public static void addNewBusinessHours(int dayOfWeek, Time startTime, Time endTime) throws InvalidInputException {
	}

	public static TOBusiness getBusinessInformation() throws InvalidInputException {
		return null;
	}

	public static void addNewTimeSlot(String type, Date startDate, Time startTime, Date endDate, Time endTime)
			throws InvalidInputException {
	}

	public static void updateBasicBusinessInformation(String newName, String newEmail, String newPhoneNumber,
			String newAddress) throws InvalidInputException {
	}

	public static void updateBusinessHour(int dayOfWeek, Time time, int targetDayOfWeek, Time targetStartTime,
			Time targetEndTime) throws InvalidInputException {
	}

	public static void removeExisitingBusinessHour(int dayOfWeek, Time startTime) throws InvalidInputException {
	}

	public static void updateTimeSlotInformation(String type, Date date, Time time, Date targetStartDate,
			Time targetStartTime, Date targetEndDate, Time targetEndTime) throws InvalidInputException {
	}

	public static void removeTimeSlot(String type, Date startDate, Time startTime, Date endDate, Time endTime)
			throws InvalidInputException {
	}

	public static void AddService(String name, int duration, int downtimeStart, int downtimeDuration) throws Exception {
	}

	public static void UpdateService(String targetName, String name, int duration, int downtimeStart,
			int downtimeDuration) throws Exception {
	}

	public static void DeleteService(String name) throws Exception {
	}

	public static void defineServiceCombo(String name, ComboItem mainService, List<Service> services,
			List<Boolean> mandatorySettings) throws Exception {
	}

	public static void updateServiceCombo(String oldname, String name, ComboItem mainService, List<Service> services,
			List<Boolean> mandatorySettings) throws Exception {
	}

	public static void deleteServiceCombo(String name) throws Exception {
	}

	public static List<TOServiceCombo> getServiceCombo() {
		return null;
	}

	public static ServiceCombo findServiceCombo(String name) {
		return null;
	}

	public static void Login(String username, String password) throws InvalidInputException {

	}

	public static void Logout() throws InvalidInputException {
	}

	public static List<List<TOTimeSlot>> ViewCalendar(String datein, boolean isDay) throws InvalidInputException {
		return null;
	}

	public static List<TOTimeSlot> OneDayUnavailable(String datein, FlexiBook flexibook, List<TimeSlot> all) {
		return null;
	}

	public static List<TOTimeSlot> OneDayAvailable(String datein, List<TOTimeSlot> unavailable, FlexiBook flexibook) {
		return null;
	}

	public static String getWeek(String date) {
		return null;
	}

	public static void MakeAppoinment(Date date, BookableService serviceName, List<ComboItem> optionalServices,
			Time startTime) throws InvalidInputException {

	}

	public static void UpdateAppoinment(String oldUser, Date date, Time startTime, BookableService serviceName,
			Date newDate, Time newStartTime, ComboItem optionalService, String action) throws InvalidInputException {

	}

	public static void CancelAppointment(Date date, Time startTime, BookableService serviceName)
			throws InvalidInputException {
	}
}

package ca.mcgill.ecse.flexibook.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import ca.mcgill.ecse.flexibook.application.FlexiBookApplication;
import ca.mcgill.ecse.flexibook.controller.FlexiBookController;
import ca.mcgill.ecse.flexibook.controller.InvalidInputException;
import ca.mcgill.ecse.flexibook.controller.TOAppointment;
import ca.mcgill.ecse.flexibook.controller.TOService;
import ca.mcgill.ecse.flexibook.controller.TOBusiness;
import ca.mcgill.ecse.flexibook.controller.TOBusinessHour;
import ca.mcgill.ecse.flexibook.controller.TOTimeSlot;

import javax.swing.JTextField;
import javax.swing.JButton;

import java.util.List;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import java.awt.Color;
import javax.swing.JScrollBar;


public class UIPage {

	
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final String TIME_FORMAT = "HH:mm";

	public JFrame frmFlexibook;
	private String error;
	private JTextArea errorMessageTextArea;

	
	/**
	 * all the components for the account
	 * 
	 * @author whole Team
	 */
	private JTextField usernameInputTextField;
	private JTextField passwordInputTextField;
	private JTextField newUserNameTextField;
	private JTextField newPasswordTextField;
	private JTextField targetUserNameTextField;
	private JLabel currentUserNameDesLabel;
	
	/**
	 * all the components for the view Calender
	 * 
	 * @author whole Team
	 */
	private JTextField calendarYearInputTextField;
	private JTextField calendarMonthInputTextField;
	private JTextField calendarDayInputTextField;
	
	/**
	 * all the components for the businessInfo
	 * 
	 * @author whole Team
	 */
	private JTextField businessAddressInputTextField;
	private JTextField businessPhoneInputTextField;
	private JTextField businessEmailInputTextField;
	private JTextField businessNameInputTextField;

	/**
	 * all the components for the Appoitment
	 * 
	 * @author whole Team
	 */
	private JTextField makeAppointmentYearTextField;
	private JTextField makeAppointmentMonthTextField;
	private JTextField makeAppointmentDayTextField;
	private JTextField MakeAppointmentTimeTextField;
	private JTextField cancelAppointmentYearTextField;
	private JTextField cancelAppointmentMonthTextField;
	private JTextField cancelAppointmentDayTextField;
	private JTextField updateAppointmentNewYearTextField;
	private JTextField updateAppointmentOldMonthTextField;
	private JTextField updateAppointmentOldDateTextField;
	private JTextField updateAppointmentOldYearTextField;
	private JTextField updateAppointmentNewMonthTextField;
	private JTextField updateAppointmentNewDateTextField;
	private JComboBox<String> MAServicecomboBox;
	private JTextField CancelAppoitmentTimeTextField;
	private JTextField UpdateAppointmentTimeTextField;
	private JTextField UpdateAppointmentOldTimeTextField;
	private JComboBox<String> CAServicecomboBox;
	private JComboBox<String> UAServicecomboBox;

	/**
	 * all the components for the service
	 * 
	 * @author whole Team
	 */
	private JTextField serviceNameTextField;
	private JTextField serviceDurationTextField;
	private JTextField downtimeDurationTextField;
	private JTextField downtimeStartTextField;
	private JTextField deleteServiceNameTextField;
	private JTextField targetServiceNameTextField;
	private JTextField newDurationTextField;
	private JTextField newDowntimeDurationTextField;
	private JTextField newDowntimeStartTextField;
	private JTextField newServiceNameTextField;
	
	/**
	 * all the components for the currentBusinessInfo and businessHour
	 * 
	 * @author whole Team
	 */
	private JLabel currentBusinessInfoPanelNameLbl;
	private JLabel currentBusinessInfoPanelPhoneNumberLbl;
	private JLabel currentBusinessInfoPanelAddressLbl;
	private JLabel currentBusinessInfoPanelEmailLbl;
	private JTextField businessHourStartTimeInputField;
	private JTextField businessHourEndTimeInputField;
	private JTextField businessHourCurrentDayOfWeekInputField;
	private JTextField businessHourCurrentStartTimeInputField;
	private JTextField businessHourNewDayOfWeekInputField;
	private JTextField businessHourNewStartTimeInputField;
	private JTextField businessHourNewEndTimeInputField;
	private JTextField businessHourDayOfWeekInputField;
	private JTextArea businessHourDisplayTextArea;
	
	/**
	 * all the components for the Appointment control
	 * 
	 * @author whole Team
	 */
	private JTextField noShowAppointmentYearTextField;
	private JTextField noShowAppointmentMonthTextField;
	private JTextField noShowAppointmentDateTextField;
	private JTextField noShowAppointmentTimeTextField;
	private JTable table;
	private JRadioButton viewUnavailable;
	private JPanel calendarPanel;
	private DefaultTableModel model;
	
	
	/**
	 * Create the application.
	 * @author whole Team
	 */
	public UIPage() {
		initialize();
		refreshData();
	}

    /** 
     * Initialize the contents of the frame.
     *
     * @author whole Team
     */
	private void initialize() {
		frmFlexibook = new JFrame();
		frmFlexibook.setTitle("FlexiBook");
		frmFlexibook.setBounds(100, 100, 1423, 1070);
		frmFlexibook.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmFlexibook.getContentPane().setLayout(null);
		// ------------------------------Account------------------------------------------------------------
		// signup

		JPanel signupLoginPanel = new JPanel();
		signupLoginPanel.setBounds(10, 51, 267, 125);
		frmFlexibook.getContentPane().add(signupLoginPanel);
		signupLoginPanel.setLayout(null);

		JLabel userNameLabel = new JLabel("Username:");
		userNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		userNameLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		userNameLabel.setBounds(10, 43, 81, 28);
		signupLoginPanel.add(userNameLabel);

		JLabel passwordLabel = new JLabel("Password:");
		passwordLabel.setHorizontalAlignment(SwingConstants.CENTER);
		passwordLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		passwordLabel.setBounds(10, 74, 81, 28);
		signupLoginPanel.add(passwordLabel);

		usernameInputTextField = new JTextField();
		usernameInputTextField.setBounds(101, 46, 161, 21);
		signupLoginPanel.add(usernameInputTextField);
		usernameInputTextField.setColumns(10);

		passwordInputTextField = new JTextField();
		passwordInputTextField.setColumns(10);
		passwordInputTextField.setBounds(101, 77, 161, 21);
		signupLoginPanel.add(passwordInputTextField);

		// login
		JButton loginButton = new JButton("Login");
		loginButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
		loginButton.setBounds(10, 99, 93, 23);
		signupLoginPanel.add(loginButton);
		
		// sign up
		JButton signupButton = new JButton("Sign up");
		signupButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
		signupButton.setBounds(168, 99, 93, 23);
		signupLoginPanel.add(signupButton);
		
		// login
		JLabel loginSignUpPageTitle = new JLabel("Welcome");
		loginSignUpPageTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
		loginSignUpPageTitle.setHorizontalAlignment(SwingConstants.CENTER);
		loginSignUpPageTitle.setBounds(68, 6, 125, 28);
		signupLoginPanel.add(loginSignUpPageTitle);
		
		// --------------------------------------- error ---------------------------------------
		JPanel errorPanel = new JPanel();
		errorPanel.setBounds(10, 10, 1383, 40);
		frmFlexibook.getContentPane().add(errorPanel);
		errorPanel.setLayout(null);

		JLabel errorPanelTitle = new JLabel("Errors:");
		errorPanelTitle.setHorizontalAlignment(SwingConstants.CENTER);
		errorPanelTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
		errorPanelTitle.setBounds(6, 10, 98, 16);
		errorPanel.add(errorPanelTitle);

		errorMessageTextArea = new JTextArea();
		errorMessageTextArea.setForeground(Color.RED);
		errorMessageTextArea.setEditable(false);
		errorMessageTextArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
		errorMessageTextArea.setBounds(87, 10, 1286, 26);
		errorPanel.add(errorMessageTextArea);
		
		// --------------------------------------- display user info ---------------------------------------
		JPanel currentUserPanel = new JPanel();
		currentUserPanel.setBounds(0, 177, 279, 274);
		frmFlexibook.getContentPane().add(currentUserPanel);
		currentUserPanel.setLayout(null);
		
		JLabel currentUserUsernameLabel = new JLabel("Username: ");
		currentUserUsernameLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		currentUserUsernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		currentUserUsernameLabel.setBounds(10, 38, 79, 26);
		currentUserPanel.add(currentUserUsernameLabel);
		
		currentUserNameDesLabel = new JLabel("Your username");
		currentUserNameDesLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		currentUserNameDesLabel.setHorizontalAlignment(SwingConstants.CENTER);
		currentUserNameDesLabel.setBounds(99, 38, 154, 26);
		currentUserPanel.add(currentUserNameDesLabel);
		
		// update Account
		JLabel currentUserTitle = new JLabel("Current User");
		currentUserTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
		currentUserTitle.setHorizontalAlignment(SwingConstants.CENTER);
		currentUserTitle.setBounds(84, 10, 107, 18);
		currentUserPanel.add(currentUserTitle);
		
		// update Account
		JLabel newUserNameLabel = new JLabel("New Username:");
		newUserNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		newUserNameLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		newUserNameLabel.setBounds(10, 113, 99, 26);
		currentUserPanel.add(newUserNameLabel);
		
		// update Account
		JLabel newPasswordLabel = new JLabel("New Password:");
		newPasswordLabel.setHorizontalAlignment(SwingConstants.CENTER);
		newPasswordLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		newPasswordLabel.setBounds(10, 149, 99, 26);
		currentUserPanel.add(newPasswordLabel);

		// update Account
		JButton updateAccountButton = new JButton("Update Account");
		updateAccountButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
		updateAccountButton.setBounds(71, 185, 137, 31);
		currentUserPanel.add(updateAccountButton);

		// delete account
		JButton deleteAccountButton = new JButton("Delete Account");
		deleteAccountButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
		deleteAccountButton.setBounds(74, 249, 117, 26);
		currentUserPanel.add(deleteAccountButton);
		
		// delete account
		JLabel targetUsernameLabel = new JLabel("Target Username:");
		targetUsernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		targetUsernameLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		targetUsernameLabel.setBounds(10, 226, 99, 26);
		currentUserPanel.add(targetUsernameLabel);
		
		// update Account
		newUserNameTextField = new JTextField();
		newUserNameTextField.setFont(new Font("SansSerif", Font.PLAIN, 12));
		newUserNameTextField.setBounds(119, 117, 123, 22);
		currentUserPanel.add(newUserNameTextField);
		newUserNameTextField.setColumns(10);
		
		// update Account
		newPasswordTextField = new JTextField();
		newPasswordTextField.setFont(new Font("SansSerif", Font.PLAIN, 12));
		newPasswordTextField.setColumns(10);
		newPasswordTextField.setBounds(119, 153, 123, 22);
		currentUserPanel.add(newPasswordTextField);
		
		// update Account
		targetUserNameTextField = new JTextField();
		targetUserNameTextField.setFont(new Font("SansSerif", Font.PLAIN, 12));
		targetUserNameTextField.setColumns(10);
		targetUserNameTextField.setBounds(119, 226, 123, 22);
		currentUserPanel.add(targetUserNameTextField);
		
		// Log Out
		JButton logOutButton = new JButton("Log Out");
		logOutButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
		logOutButton.setBounds(74, 72, 117, 29);
		currentUserPanel.add(logOutButton);
		
		// --------------------------------------- all the view features ---------------------------------------
		calendarPanel = new JPanel();
		calendarPanel.setBounds(278, 51, 454, 333);
		frmFlexibook.getContentPane().add(calendarPanel);
		calendarPanel.setLayout(null);
		
		JLabel calendarPanelTitleLabel = new JLabel("Calendar & ViewService");
		calendarPanelTitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		calendarPanelTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		calendarPanelTitleLabel.setBounds(116, 6, 153, 26);
		calendarPanel.add(calendarPanelTitleLabel);

		JLabel calendarDateLabel = new JLabel("Date (yyyy-MM-dd):");
		calendarDateLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		calendarDateLabel.setHorizontalAlignment(SwingConstants.CENTER);
		calendarDateLabel.setBounds(10, 252, 126, 15);
		calendarPanel.add(calendarDateLabel);

		calendarYearInputTextField = new JTextField();
		calendarYearInputTextField.setFont(new Font("SansSerif", Font.PLAIN, 12));
		calendarYearInputTextField.setBounds(148, 248, 66, 21);
		calendarPanel.add(calendarYearInputTextField);
		calendarYearInputTextField.setColumns(10);

		calendarMonthInputTextField = new JTextField();
		calendarMonthInputTextField.setFont(new Font("SansSerif", Font.PLAIN, 12));
		calendarMonthInputTextField.setColumns(10);
		calendarMonthInputTextField.setBounds(224, 248, 66, 21);
		calendarPanel.add(calendarMonthInputTextField);

		calendarDayInputTextField = new JTextField();
		calendarDayInputTextField.setFont(new Font("SansSerif", Font.PLAIN, 12));
		calendarDayInputTextField.setColumns(10);
		calendarDayInputTextField.setBounds(300, 248, 66, 21);
		calendarPanel.add(calendarDayInputTextField);

		JButton calendarQueryByDateButton = new JButton("Query by Date");
		calendarQueryByDateButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
		calendarQueryByDateButton.setBounds(20, 279, 137, 26);
		calendarPanel.add(calendarQueryByDateButton);

		JButton calendarQueryByWeekButton = new JButton("Query by Week");
		calendarQueryByWeekButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
		calendarQueryByWeekButton.setBounds(234, 279, 137, 26);
		calendarPanel.add(calendarQueryByWeekButton);

		viewUnavailable = new JRadioButton("view Unavailable");
		viewUnavailable.setBounds(10, 225, 160, 21);
		calendarPanel.add(viewUnavailable);

		table = new JTable();
		table.setBounds(6, 27, 442, 195);
		String title[] = { "Date", "StartTime", "EndTime" };
		model = new DefaultTableModel();
		model.setColumnIdentifiers(title);
		table.setModel(model);

		calendarPanel.add(table);
		
		//  view service in calendar
		JButton viewServiceButton = new JButton("View Service");
		viewServiceButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
		viewServiceButton.setBounds(234, 306, 137, 26);
		calendarPanel.add(viewServiceButton);
		viewServiceButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				viewServiceButtonActionPerformed(evt);
			}
		});
		
		// --------------------------------------- view all the appointments ---------------------------------------
		JButton VAButton = new JButton("Appointments");
		VAButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
		VAButton.setBounds(20, 306, 137, 26);
		calendarPanel.add(VAButton);
		
		// --------------------------------------- bussiness info ---------------------------------------
		JPanel updateBusinessInformation = new JPanel();
		updateBusinessInformation.setBounds(0, 453, 295, 200);
		frmFlexibook.getContentPane().add(updateBusinessInformation);
		updateBusinessInformation.setLayout(null);
		
		//update Business Info
		JLabel updateBusinessInfoLabel = new JLabel("Update Business Info Label");
		updateBusinessInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		updateBusinessInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		updateBusinessInfoLabel.setBounds(110, 10, 178, 32);
		updateBusinessInformation.add(updateBusinessInfoLabel);
		
		//curr Business Info
		JLabel businessNameLabel = new JLabel("Business name:");
		businessNameLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		businessNameLabel.setBounds(10, 38, 108, 32);
		updateBusinessInformation.add(businessNameLabel);
		
		//curr Business Info
		JLabel businessAddressLabel = new JLabel("Business address:");
		businessAddressLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		businessAddressLabel.setBounds(10, 70, 108, 32);
		updateBusinessInformation.add(businessAddressLabel);
		
		//curr Business Info
		JLabel businessPhoneLabel = new JLabel("Business phone:");
		businessPhoneLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		businessPhoneLabel.setBounds(10, 94, 108, 32);
		updateBusinessInformation.add(businessPhoneLabel);
		
		//curr Business Info
		JLabel businessEmailLabel = new JLabel("Business email:");
		businessEmailLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		businessEmailLabel.setBounds(10, 119, 108, 32);
		updateBusinessInformation.add(businessEmailLabel);
		
		//show Business Info
		businessNameInputTextField = new JTextField();
		businessNameInputTextField.setBounds(120, 43, 169, 21);
		updateBusinessInformation.add(businessNameInputTextField);
		businessNameInputTextField.setColumns(10);
		
		//show Business Info
		businessAddressInputTextField = new JTextField();
		businessAddressInputTextField.setColumns(10);
		businessAddressInputTextField.setBounds(119, 75, 169, 21);
		updateBusinessInformation.add(businessAddressInputTextField);
		
		//show Business Info
		businessPhoneInputTextField = new JTextField();
		businessPhoneInputTextField.setColumns(10);
		businessPhoneInputTextField.setBounds(119, 105, 169, 21);
		updateBusinessInformation.add(businessPhoneInputTextField);
		
		//show Business Info
		businessEmailInputTextField = new JTextField();
		businessEmailInputTextField.setColumns(10);
		businessEmailInputTextField.setBounds(119, 130, 169, 21);
		updateBusinessInformation.add(businessEmailInputTextField);
		
		//sign up button for the business
		JButton signupBusinessInfoButton = new JButton("Sign up");
		signupBusinessInfoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				signUpBusinessInfoActionPerformed(arg0);
			}
		});
		signupBusinessInfoButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
		signupBusinessInfoButton.setBounds(10, 163, 93, 23);
		updateBusinessInformation.add(signupBusinessInfoButton);

		JButton updateBusinessInfoButton = new JButton("Update");
		updateBusinessInfoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				updateBusinessInfoActionPerformed(arg0);
			}
		});
		updateBusinessInfoButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
		updateBusinessInfoButton.setBounds(139, 163, 93, 23);
		updateBusinessInformation.add(updateBusinessInfoButton);
		
		JPanel currentBusinessInfoPanel = new JPanel();
		currentBusinessInfoPanel.setBounds(0, 643, 295, 164);
		frmFlexibook.getContentPane().add(currentBusinessInfoPanel);
		currentBusinessInfoPanel.setLayout(null);
		
		//business info label
		JLabel currentBusinessInfoTitleLbl = new JLabel("Current Business Info");
		currentBusinessInfoTitleLbl.setHorizontalAlignment(SwingConstants.CENTER);
		currentBusinessInfoTitleLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
		currentBusinessInfoTitleLbl.setBounds(77, 10, 135, 22);
		currentBusinessInfoPanel.add(currentBusinessInfoTitleLbl);
		
		//business info label
		JLabel currentBusinessInfoPanelNameDesLbl = new JLabel("Name:");
		currentBusinessInfoPanelNameDesLbl.setHorizontalAlignment(SwingConstants.CENTER);
		currentBusinessInfoPanelNameDesLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
		currentBusinessInfoPanelNameDesLbl.setBounds(10, 42, 135, 22);
		currentBusinessInfoPanel.add(currentBusinessInfoPanelNameDesLbl);
		
		//business info label
		JLabel currentBusinessInfoPanelPhoneDesLbl = new JLabel("Phone Number:");
		currentBusinessInfoPanelPhoneDesLbl.setHorizontalAlignment(SwingConstants.CENTER);
		currentBusinessInfoPanelPhoneDesLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
		currentBusinessInfoPanelPhoneDesLbl.setBounds(10, 74, 135, 22);
		currentBusinessInfoPanel.add(currentBusinessInfoPanelPhoneDesLbl);
		
		//business info label
		JLabel currentBusinessInfoPanelAddressDesLbl = new JLabel("Address:");
		currentBusinessInfoPanelAddressDesLbl.setHorizontalAlignment(SwingConstants.CENTER);
		currentBusinessInfoPanelAddressDesLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
		currentBusinessInfoPanelAddressDesLbl.setBounds(10, 106, 135, 22);
		currentBusinessInfoPanel.add(currentBusinessInfoPanelAddressDesLbl);
		
		//business info label
		JLabel currentBusinessInfoPanelEmailDesLbl = new JLabel("Email:");
		currentBusinessInfoPanelEmailDesLbl.setHorizontalAlignment(SwingConstants.CENTER);
		currentBusinessInfoPanelEmailDesLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
		currentBusinessInfoPanelEmailDesLbl.setBounds(10, 138, 135, 22);
		currentBusinessInfoPanel.add(currentBusinessInfoPanelEmailDesLbl);
		
		//business info label
		currentBusinessInfoPanelNameLbl = new JLabel("");
		currentBusinessInfoPanelNameLbl.setHorizontalAlignment(SwingConstants.CENTER);
		currentBusinessInfoPanelNameLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
		currentBusinessInfoPanelNameLbl.setBounds(155, 42, 135, 22);
		currentBusinessInfoPanel.add(currentBusinessInfoPanelNameLbl);
		
		//business info label
		currentBusinessInfoPanelPhoneNumberLbl = new JLabel("");
		currentBusinessInfoPanelPhoneNumberLbl.setHorizontalAlignment(SwingConstants.CENTER);
		currentBusinessInfoPanelPhoneNumberLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
		currentBusinessInfoPanelPhoneNumberLbl.setBounds(155, 74, 135, 22);
		currentBusinessInfoPanel.add(currentBusinessInfoPanelPhoneNumberLbl);
		
		//business info label
		currentBusinessInfoPanelAddressLbl = new JLabel("");
		currentBusinessInfoPanelAddressLbl.setHorizontalAlignment(SwingConstants.CENTER);
		currentBusinessInfoPanelAddressLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
		currentBusinessInfoPanelAddressLbl.setBounds(155, 106, 135, 22);
		currentBusinessInfoPanel.add(currentBusinessInfoPanelAddressLbl);
		
		//business info label
		currentBusinessInfoPanelEmailLbl = new JLabel("");
		currentBusinessInfoPanelEmailLbl.setHorizontalAlignment(SwingConstants.CENTER);
		currentBusinessInfoPanelEmailLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
		currentBusinessInfoPanelEmailLbl.setBounds(155, 138, 135, 22);
		currentBusinessInfoPanel.add(currentBusinessInfoPanelEmailLbl);
		
		// --------------------------------------- Business Hour ---------------------------------------
		JPanel modifyBusinessHourPanel = new JPanel();
		modifyBusinessHourPanel.setBounds(307, 386, 406, 404);
		frmFlexibook.getContentPane().add(modifyBusinessHourPanel);
		modifyBusinessHourPanel.setLayout(null);
		
		//business Hour Input label
		JLabel modifyBusinessHourTitelLbl = new JLabel("Add or Update Business Hour");
		modifyBusinessHourTitelLbl.setHorizontalAlignment(SwingConstants.CENTER);
		modifyBusinessHourTitelLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
		modifyBusinessHourTitelLbl.setBounds(93, 6, 213, 36);
		modifyBusinessHourPanel.add(modifyBusinessHourTitelLbl);
		
		//business Hour Input label
		JLabel businessHourDayOfWeekLbl = new JLabel("Day of Week:");
		businessHourDayOfWeekLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
		businessHourDayOfWeekLbl.setBounds(10, 44, 87, 23);
		modifyBusinessHourPanel.add(businessHourDayOfWeekLbl);
		
		//business Hour Input label
		JLabel businessHourStartTimeLbl = new JLabel("Start at (hh:mm):");
		businessHourStartTimeLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
		businessHourStartTimeLbl.setBounds(10, 77, 107, 23);
		modifyBusinessHourPanel.add(businessHourStartTimeLbl);
		
		//business Hour Input label
		JLabel businessHourEndTimeLbl = new JLabel("End at (hh:mm):");
		businessHourEndTimeLbl.setToolTipText("");
		businessHourEndTimeLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
		businessHourEndTimeLbl.setBounds(10, 110, 107, 23);
		modifyBusinessHourPanel.add(businessHourEndTimeLbl);
		
		//business Hour Input
		businessHourDayOfWeekInputField = new JTextField();
		businessHourDayOfWeekInputField.setBounds(127, 46, 138, 21);
		modifyBusinessHourPanel.add(businessHourDayOfWeekInputField);
		businessHourDayOfWeekInputField.setColumns(10);
		
		//business Hour Input
		businessHourStartTimeInputField = new JTextField();
		businessHourStartTimeInputField.setBounds(127, 79, 138, 21);
		modifyBusinessHourPanel.add(businessHourStartTimeInputField);
		businessHourStartTimeInputField.setColumns(10);
		
		//business Hour Input
		businessHourEndTimeInputField = new JTextField();
		businessHourEndTimeInputField.setBounds(126, 112, 138, 21);
		modifyBusinessHourPanel.add(businessHourEndTimeInputField);
		businessHourEndTimeInputField.setColumns(10);

		JButton addBusinessHourButton = new JButton("Add");
		addBusinessHourButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addBusinessHour(arg0);
			}
		});
		addBusinessHourButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
		addBusinessHourButton.setBounds(24, 336, 93, 23);
		modifyBusinessHourPanel.add(addBusinessHourButton);

		JLabel businessHourOldDayOfWeekLbl = new JLabel("Current Day of Week:");
		businessHourOldDayOfWeekLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
		businessHourOldDayOfWeekLbl.setBounds(10, 158, 115, 23);
		modifyBusinessHourPanel.add(businessHourOldDayOfWeekLbl);

		JLabel businessHourOldStartTimeLbl = new JLabel("Currently Start at:");
		businessHourOldStartTimeLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
		businessHourOldStartTimeLbl.setBounds(10, 191, 107, 23);
		modifyBusinessHourPanel.add(businessHourOldStartTimeLbl);

		JLabel businessHourNewDayOfWeekLbl = new JLabel("New Day of Week:");
		businessHourNewDayOfWeekLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
		businessHourNewDayOfWeekLbl.setBounds(10, 224, 115, 23);
		modifyBusinessHourPanel.add(businessHourNewDayOfWeekLbl);

		JLabel businessHourNewStartTimeLbl = new JLabel("Start at (hh:mm):");
		businessHourNewStartTimeLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
		businessHourNewStartTimeLbl.setBounds(10, 257, 107, 23);
		modifyBusinessHourPanel.add(businessHourNewStartTimeLbl);

		JLabel businessHourNewEndTimeLbl = new JLabel("End at (hh:mm):");
		businessHourNewEndTimeLbl.setToolTipText("");
		businessHourNewEndTimeLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
		businessHourNewEndTimeLbl.setBounds(10, 290, 107, 23);
		modifyBusinessHourPanel.add(businessHourNewEndTimeLbl);

		businessHourCurrentDayOfWeekInputField = new JTextField();
		businessHourCurrentDayOfWeekInputField.setColumns(10);
		businessHourCurrentDayOfWeekInputField.setBounds(126, 160, 138, 21);
		modifyBusinessHourPanel.add(businessHourCurrentDayOfWeekInputField);

		businessHourCurrentStartTimeInputField = new JTextField();
		businessHourCurrentStartTimeInputField.setColumns(10);
		businessHourCurrentStartTimeInputField.setBounds(127, 193, 138, 21);
		modifyBusinessHourPanel.add(businessHourCurrentStartTimeInputField);

		businessHourNewDayOfWeekInputField = new JTextField();
		businessHourNewDayOfWeekInputField.setColumns(10);
		businessHourNewDayOfWeekInputField.setBounds(126, 226, 138, 21);
		modifyBusinessHourPanel.add(businessHourNewDayOfWeekInputField);

		businessHourNewStartTimeInputField = new JTextField();
		businessHourNewStartTimeInputField.setColumns(10);
		businessHourNewStartTimeInputField.setBounds(126, 259, 138, 21);
		modifyBusinessHourPanel.add(businessHourNewStartTimeInputField);

		businessHourNewEndTimeInputField = new JTextField();
		businessHourNewEndTimeInputField.setColumns(10);
		businessHourNewEndTimeInputField.setBounds(127, 292, 138, 21);
		modifyBusinessHourPanel.add(businessHourNewEndTimeInputField);

		JButton updateBusinessHourButton = new JButton("Update");
		updateBusinessHourButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				updateBusinessHour(arg0);
			}
		});
		updateBusinessHourButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
		updateBusinessHourButton.setBounds(172, 336, 93, 23);
		modifyBusinessHourPanel.add(updateBusinessHourButton);

		JPanel currentBusinessHourPanel = new JPanel();
		currentBusinessHourPanel.setBounds(10, 838, 295, 152);
		frmFlexibook.getContentPane().add(currentBusinessHourPanel);
		currentBusinessHourPanel.setLayout(null);

		JLabel currentBusinessHourPanelTitleLbl = new JLabel("Business Hours");
		currentBusinessHourPanelTitleLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
		currentBusinessHourPanelTitleLbl.setHorizontalAlignment(SwingConstants.CENTER);
		currentBusinessHourPanelTitleLbl.setBounds(96, 10, 90, 16);
		currentBusinessHourPanel.add(currentBusinessHourPanelTitleLbl);

		businessHourDisplayTextArea = new JTextArea();
		businessHourDisplayTextArea.setBounds(10, 30, 275, 112);
		currentBusinessHourPanel.add(businessHourDisplayTextArea);
		
		// --------------------------------------- Appointments ---------------------------------------
		JPanel makeAppointmentPanel = new JPanel();
		makeAppointmentPanel.setLayout(null);
		makeAppointmentPanel.setBounds(731, 91, 406, 176);
		frmFlexibook.getContentPane().add(makeAppointmentPanel);

		JLabel makeAppointmentPanelTitleLabel = new JLabel("Make an Appointment");
		makeAppointmentPanelTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		makeAppointmentPanelTitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		makeAppointmentPanelTitleLabel.setBounds(129, 13, 158, 26);
		makeAppointmentPanel.add(makeAppointmentPanelTitleLabel);

		JLabel makeAppointmentDateLabel = new JLabel("Date (yyyy-MM-dd):");
		makeAppointmentDateLabel.setHorizontalAlignment(SwingConstants.CENTER);
		makeAppointmentDateLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		makeAppointmentDateLabel.setBounds(14, 55, 125, 15);
		makeAppointmentPanel.add(makeAppointmentDateLabel);

		makeAppointmentYearTextField = new JTextField();
		makeAppointmentYearTextField.setFont(new Font("SansSerif", Font.PLAIN, 12));
		makeAppointmentYearTextField.setColumns(10);
		makeAppointmentYearTextField.setBounds(139, 51, 66, 21);
		makeAppointmentPanel.add(makeAppointmentYearTextField);

		makeAppointmentMonthTextField = new JTextField();
		makeAppointmentMonthTextField.setFont(new Font("SansSerif", Font.PLAIN, 12));
		makeAppointmentMonthTextField.setColumns(10);
		makeAppointmentMonthTextField.setBounds(221, 52, 66, 21);
		makeAppointmentPanel.add(makeAppointmentMonthTextField);

		makeAppointmentDayTextField = new JTextField();
		makeAppointmentDayTextField.setFont(new Font("SansSerif", Font.PLAIN, 12));
		makeAppointmentDayTextField.setColumns(10);
		makeAppointmentDayTextField.setBounds(301, 51, 66, 21);
		makeAppointmentPanel.add(makeAppointmentDayTextField);

		JButton makeAppointmentButton = new JButton("Book It !!");
		makeAppointmentButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
		makeAppointmentButton.setBounds(321, 133, 79, 26);
		makeAppointmentPanel.add(makeAppointmentButton);

		JLabel makeAppointmentTimeLabel = new JLabel("Time(hh:mm)");
		makeAppointmentTimeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		makeAppointmentTimeLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		makeAppointmentTimeLabel.setBounds(14, 85, 125, 15);
		makeAppointmentPanel.add(makeAppointmentTimeLabel);

		MakeAppointmentTimeTextField = new JTextField();
		MakeAppointmentTimeTextField.setFont(new Font("SansSerif", Font.PLAIN, 12));
		MakeAppointmentTimeTextField.setColumns(10);
		MakeAppointmentTimeTextField.setBounds(139, 82, 66, 21);
		makeAppointmentPanel.add(MakeAppointmentTimeTextField);

		MAServicecomboBox = new JComboBox<String>();
		MAServicecomboBox.setBounds(136, 107, 231, 27);
		makeAppointmentPanel.add(MAServicecomboBox);

		JLabel MAServiceLabel = new JLabel("Service Name:");
		MAServiceLabel.setHorizontalAlignment(SwingConstants.CENTER);
		MAServiceLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		MAServiceLabel.setBounds(14, 112, 109, 15);
		makeAppointmentPanel.add(MAServiceLabel);

		JPanel cancelAppointmentPanel = new JPanel();
		cancelAppointmentPanel.setLayout(null);
		cancelAppointmentPanel.setBounds(731, 279, 406, 164);
		frmFlexibook.getContentPane().add(cancelAppointmentPanel);

		JLabel cancelAppointmentPanelTitleLabel = new JLabel("Cancel an Appointment");
		cancelAppointmentPanelTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		cancelAppointmentPanelTitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		cancelAppointmentPanelTitleLabel.setBounds(129, 13, 158, 26);
		cancelAppointmentPanel.add(cancelAppointmentPanelTitleLabel);

		JLabel cancelAppointmentDateLabel = new JLabel("Date (yyyy-MM-dd):");
		cancelAppointmentDateLabel.setHorizontalAlignment(SwingConstants.CENTER);
		cancelAppointmentDateLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		cancelAppointmentDateLabel.setBounds(14, 55, 126, 15);
		cancelAppointmentPanel.add(cancelAppointmentDateLabel);

		cancelAppointmentYearTextField = new JTextField();
		cancelAppointmentYearTextField.setFont(new Font("SansSerif", Font.PLAIN, 12));
		cancelAppointmentYearTextField.setColumns(10);
		cancelAppointmentYearTextField.setBounds(139, 51, 66, 21);
		cancelAppointmentPanel.add(cancelAppointmentYearTextField);

		cancelAppointmentMonthTextField = new JTextField();
		cancelAppointmentMonthTextField.setFont(new Font("SansSerif", Font.PLAIN, 12));
		cancelAppointmentMonthTextField.setColumns(10);
		cancelAppointmentMonthTextField.setBounds(221, 52, 66, 21);
		cancelAppointmentPanel.add(cancelAppointmentMonthTextField);

		cancelAppointmentDayTextField = new JTextField();
		cancelAppointmentDayTextField.setFont(new Font("SansSerif", Font.PLAIN, 12));
		cancelAppointmentDayTextField.setColumns(10);
		cancelAppointmentDayTextField.setBounds(301, 51, 66, 21);
		cancelAppointmentPanel.add(cancelAppointmentDayTextField);

		JLabel cancelAppointmentServiceLabel = new JLabel("Service Name:");
		cancelAppointmentServiceLabel.setHorizontalAlignment(SwingConstants.CENTER);
		cancelAppointmentServiceLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		cancelAppointmentServiceLabel.setBounds(14, 89, 109, 15);
		cancelAppointmentPanel.add(cancelAppointmentServiceLabel);

		JButton cancelAppointmentButton = new JButton("Cancel It !!");
		cancelAppointmentButton.setBounds(307, 128, 89, 26);
		cancelAppointmentPanel.add(cancelAppointmentButton);
		cancelAppointmentButton.setFont(new Font("SansSerif", Font.PLAIN, 12));

		JLabel CancelAppointmentTimeLabel = new JLabel("Time(hh:mm)");
		CancelAppointmentTimeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		CancelAppointmentTimeLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		CancelAppointmentTimeLabel.setBounds(15, 116, 125, 15);
		cancelAppointmentPanel.add(CancelAppointmentTimeLabel);

		CancelAppoitmentTimeTextField = new JTextField();
		CancelAppoitmentTimeTextField.setFont(new Font("SansSerif", Font.PLAIN, 12));
		CancelAppoitmentTimeTextField.setColumns(10);
		CancelAppoitmentTimeTextField.setBounds(139, 113, 66, 21);
		cancelAppointmentPanel.add(CancelAppoitmentTimeTextField);

		CAServicecomboBox = new JComboBox<String>();
		CAServicecomboBox.setSelectedIndex(-1);
		CAServicecomboBox.setBounds(136, 84, 231, 27);
		cancelAppointmentPanel.add(CAServicecomboBox);

		JPanel updateAppointmentPanel = new JPanel();
		updateAppointmentPanel.setLayout(null);
		updateAppointmentPanel.setBounds(731, 453, 406, 200);
		frmFlexibook.getContentPane().add(updateAppointmentPanel);

		JLabel updateAppointmentPanelTitleLabel = new JLabel("Update an Appointment");
		updateAppointmentPanelTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		updateAppointmentPanelTitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		updateAppointmentPanelTitleLabel.setBounds(129, 13, 158, 26);
		updateAppointmentPanel.add(updateAppointmentPanelTitleLabel);

		JLabel updateAppointmentOldDateLabel = new JLabel("Old Date (yyyy-MM-dd):");
		updateAppointmentOldDateLabel.setHorizontalAlignment(SwingConstants.CENTER);
		updateAppointmentOldDateLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		updateAppointmentOldDateLabel.setBounds(6, 57, 144, 15);
		updateAppointmentPanel.add(updateAppointmentOldDateLabel);

		updateAppointmentNewYearTextField = new JTextField();
		updateAppointmentNewYearTextField.setFont(new Font("SansSerif", Font.PLAIN, 12));
		updateAppointmentNewYearTextField.setColumns(10);
		updateAppointmentNewYearTextField.setBounds(152, 89, 66, 21);
		updateAppointmentPanel.add(updateAppointmentNewYearTextField);

		updateAppointmentOldMonthTextField = new JTextField();
		updateAppointmentOldMonthTextField.setFont(new Font("SansSerif", Font.PLAIN, 12));
		updateAppointmentOldMonthTextField.setColumns(10);
		updateAppointmentOldMonthTextField.setBounds(234, 54, 66, 21);
		updateAppointmentPanel.add(updateAppointmentOldMonthTextField);

		updateAppointmentOldDateTextField = new JTextField();
		updateAppointmentOldDateTextField.setFont(new Font("SansSerif", Font.PLAIN, 12));
		updateAppointmentOldDateTextField.setColumns(10);
		updateAppointmentOldDateTextField.setBounds(314, 53, 66, 21);
		updateAppointmentPanel.add(updateAppointmentOldDateTextField);

		JLabel updateAppointmentServiceLabel = new JLabel("Service Name:");
		updateAppointmentServiceLabel.setHorizontalAlignment(SwingConstants.CENTER);
		updateAppointmentServiceLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		updateAppointmentServiceLabel.setBounds(14, 127, 109, 15);
		updateAppointmentPanel.add(updateAppointmentServiceLabel);

		JLabel updateAppointmentNewDateLabel = new JLabel("New Date (yyyy-MM-dd):");
		updateAppointmentNewDateLabel.setHorizontalAlignment(SwingConstants.CENTER);
		updateAppointmentNewDateLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		updateAppointmentNewDateLabel.setBounds(0, 92, 150, 15);
		updateAppointmentPanel.add(updateAppointmentNewDateLabel);

		updateAppointmentOldYearTextField = new JTextField();
		updateAppointmentOldYearTextField.setFont(new Font("SansSerif", Font.PLAIN, 12));
		updateAppointmentOldYearTextField.setColumns(10);
		updateAppointmentOldYearTextField.setBounds(152, 55, 66, 21);
		updateAppointmentPanel.add(updateAppointmentOldYearTextField);

		updateAppointmentNewMonthTextField = new JTextField();
		updateAppointmentNewMonthTextField.setFont(new Font("SansSerif", Font.PLAIN, 12));
		updateAppointmentNewMonthTextField.setColumns(10);
		updateAppointmentNewMonthTextField.setBounds(234, 89, 66, 21);
		updateAppointmentPanel.add(updateAppointmentNewMonthTextField);

		updateAppointmentNewDateTextField = new JTextField();
		updateAppointmentNewDateTextField.setFont(new Font("SansSerif", Font.PLAIN, 12));
		updateAppointmentNewDateTextField.setColumns(10);
		updateAppointmentNewDateTextField.setBounds(314, 88, 66, 21);
		updateAppointmentPanel.add(updateAppointmentNewDateTextField);

		JButton updateAppointmentButton = new JButton("Update It !!");
		updateAppointmentButton.setBounds(305, 158, 91, 26);
		updateAppointmentPanel.add(updateAppointmentButton);
		updateAppointmentButton.setFont(new Font("SansSerif", Font.PLAIN, 12));

		JLabel UpdateAppointmentTimeLabel = new JLabel("Time(hh:mm)");
		UpdateAppointmentTimeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		UpdateAppointmentTimeLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		UpdateAppointmentTimeLabel.setBounds(129, 154, 125, 15);
		updateAppointmentPanel.add(UpdateAppointmentTimeLabel);

		UpdateAppointmentTimeTextField = new JTextField();
		UpdateAppointmentTimeTextField.setFont(new Font("SansSerif", Font.PLAIN, 12));
		UpdateAppointmentTimeTextField.setColumns(10);
		UpdateAppointmentTimeTextField.setBounds(167, 173, 66, 21);
		updateAppointmentPanel.add(UpdateAppointmentTimeTextField);

		JLabel UpdateAppointmentOldTimeLabel = new JLabel("Old Time(hh:mm)");
		UpdateAppointmentOldTimeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		UpdateAppointmentOldTimeLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		UpdateAppointmentOldTimeLabel.setBounds(0, 154, 125, 15);
		updateAppointmentPanel.add(UpdateAppointmentOldTimeLabel);

		UpdateAppointmentOldTimeTextField = new JTextField();
		UpdateAppointmentOldTimeTextField.setFont(new Font("SansSerif", Font.PLAIN, 12));
		UpdateAppointmentOldTimeTextField.setColumns(10);
		UpdateAppointmentOldTimeTextField.setBounds(6, 173, 66, 21);
		updateAppointmentPanel.add(UpdateAppointmentOldTimeTextField);
		
		
		// --------------------------------------- Services ---------------------------------------
		UAServicecomboBox = new JComboBox<String>();
		UAServicecomboBox.setSelectedIndex(-1);
		UAServicecomboBox.setBounds(149, 122, 231, 27);
		updateAppointmentPanel.add(UAServicecomboBox);

		JPanel addService = new JPanel();
		addService.setBounds(1150, 91, 243, 274);
		frmFlexibook.getContentPane().add(addService);
		addService.setLayout(null);

		JLabel serviceNamePanelTitleLabel = new JLabel("Service Name:");
		serviceNamePanelTitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		serviceNamePanelTitleLabel.setBounds(6, 6, 93, 16);
		addService.add(serviceNamePanelTitleLabel);

		JLabel newLabelPanelTitleLabel = new JLabel("Duration:");
		newLabelPanelTitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		newLabelPanelTitleLabel.setBounds(6, 64, 61, 16);
		addService.add(newLabelPanelTitleLabel);

		JLabel downtimeStartPanelTitleLabel = new JLabel("Downtime Start:");
		downtimeStartPanelTitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		downtimeStartPanelTitleLabel.setBounds(6, 140, 108, 16);
		addService.add(downtimeStartPanelTitleLabel);

		JLabel downtimeDurationPanelTitleLabel = new JLabel("Downtime Duration: ");
		downtimeDurationPanelTitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		downtimeDurationPanelTitleLabel.setBounds(6, 186, 143, 16);
		addService.add(downtimeDurationPanelTitleLabel);

		serviceNameTextField = new JTextField();
		serviceNameTextField.setBounds(6, 24, 130, 26);
		addService.add(serviceNameTextField);
		serviceNameTextField.setColumns(10);

		serviceDurationTextField = new JTextField();
		serviceDurationTextField.setBounds(6, 79, 93, 26);
		addService.add(serviceDurationTextField);
		serviceDurationTextField.setColumns(10);

		downtimeDurationTextField = new JTextField();
		downtimeDurationTextField.setBounds(6, 204, 93, 26);
		addService.add(downtimeDurationTextField);
		downtimeDurationTextField.setColumns(10);

		downtimeStartTextField = new JTextField();
		downtimeStartTextField.setBounds(6, 161, 93, 26);
		addService.add(downtimeStartTextField);
		downtimeStartTextField.setColumns(10);

		JButton addServiceButton = new JButton("ADD");
		addServiceButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
		addServiceButton.setBounds(6, 242, 108, 26);
		addService.add(addServiceButton);

		JLabel lblMinutes = new JLabel("minutes");
		lblMinutes.setBounds(104, 84, 61, 16);
		addService.add(lblMinutes);

		JLabel lblMinutes_1 = new JLabel("minutes");
		lblMinutes_1.setBounds(104, 171, 61, 16);
		addService.add(lblMinutes_1);

		JLabel lblMinutes_2 = new JLabel("minutes");
		lblMinutes_2.setBounds(104, 209, 61, 16);
		addService.add(lblMinutes_2);

		JLabel lblNewLabel_1 = new JLabel("0 if not appplicable");
		lblNewLabel_1.setForeground(Color.ORANGE);
		lblNewLabel_1.setBounds(6, 112, 150, 39);
		addService.add(lblNewLabel_1);

		JPanel deleteService = new JPanel();
		deleteService.setBounds(1150, 375, 243, 91);
		frmFlexibook.getContentPane().add(deleteService);
		deleteService.setLayout(null);

		JLabel deleteServiceNamePanelTitleLabel = new JLabel("Delete Service Name:");
		deleteServiceNamePanelTitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		deleteServiceNamePanelTitleLabel.setBounds(6, 6, 189, 16);
		deleteService.add(deleteServiceNamePanelTitleLabel);

		deleteServiceNameTextField = new JTextField();
		deleteServiceNameTextField.setBounds(6, 25, 130, 26);
		deleteService.add(deleteServiceNameTextField);
		deleteServiceNameTextField.setColumns(10);

		JButton deleteServiceButton = new JButton("DELETE");
		deleteServiceButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
		deleteServiceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		deleteServiceButton.setBounds(4, 61, 117, 29);
		deleteService.add(deleteServiceButton);

		JPanel updateService = new JPanel();
		updateService.setLayout(null);
		updateService.setBounds(1150, 474, 243, 297);
		frmFlexibook.getContentPane().add(updateService);

		JLabel targetServiceNamePanelLabel = new JLabel("Target Service Name:");
		targetServiceNamePanelLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		targetServiceNamePanelLabel.setBounds(6, 6, 164, 16);
		updateService.add(targetServiceNamePanelLabel);

		JLabel newDurationPanelLabel = new JLabel("New Duration:");
		newDurationPanelLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		newDurationPanelLabel.setBounds(6, 120, 130, 16);
		updateService.add(newDurationPanelLabel);

		JLabel newDowntimeStartPanelLabel = new JLabel("New Downtime Start:");
		newDowntimeStartPanelLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		newDowntimeStartPanelLabel.setBounds(6, 217, 164, 16);
		updateService.add(newDowntimeStartPanelLabel);

		JLabel newDowntimeDurationPanelLabel = new JLabel("New Downtime Duration: ");
		newDowntimeDurationPanelLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		newDowntimeDurationPanelLabel.setBounds(6, 170, 190, 16);
		updateService.add(newDowntimeDurationPanelLabel);

		targetServiceNameTextField = new JTextField();
		targetServiceNameTextField.setColumns(10);
		targetServiceNameTextField.setBounds(6, 24, 130, 26);
		updateService.add(targetServiceNameTextField);

		newDurationTextField = new JTextField();
		newDurationTextField.setColumns(10);
		newDurationTextField.setBounds(6, 141, 130, 26);
		updateService.add(newDurationTextField);

		newDowntimeDurationTextField = new JTextField();
		newDowntimeDurationTextField.setColumns(10);
		newDowntimeDurationTextField.setBounds(6, 189, 130, 26);
		updateService.add(newDowntimeDurationTextField);

		newDowntimeStartTextField = new JTextField();
		newDowntimeStartTextField.setColumns(10);
		newDowntimeStartTextField.setBounds(6, 234, 130, 26);
		updateService.add(newDowntimeStartTextField);

		JButton updateServiceButton = new JButton("UPDATE");
		updateServiceButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
		updateServiceButton.setBounds(6, 265, 108, 26);
		updateService.add(updateServiceButton);

		JLabel newServiceNamePanelLabel = new JLabel("New Service Name:");
		newServiceNamePanelLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		newServiceNamePanelLabel.setBounds(6, 60, 130, 16);
		updateService.add(newServiceNamePanelLabel);

		newServiceNameTextField = new JTextField();
		newServiceNameTextField.setColumns(10);
		newServiceNameTextField.setBounds(6, 85, 130, 26);
		updateService.add(newServiceNameTextField);

		JPanel registerNoShowPanel = new JPanel();
		registerNoShowPanel.setBounds(731, 661, 406, 146);
		frmFlexibook.getContentPane().add(registerNoShowPanel);
		registerNoShowPanel.setLayout(null);
		
		// --------------------------------------- Appointment Control ---------------------------------------
		JLabel text = new JLabel("Appointment Control");
		text.setFont(new Font("SansSerif", Font.PLAIN, 12));
		text.setHorizontalAlignment(SwingConstants.CENTER);
		text.setBounds(131, 10, 181, 16);
		registerNoShowPanel.add(text);

		JLabel lblNewLabel = new JLabel("App Date");
		lblNewLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		lblNewLabel.setBounds(18, 43, 61, 16);
		registerNoShowPanel.add(lblNewLabel);
		
		
		noShowAppointmentYearTextField = new JTextField();
		noShowAppointmentYearTextField.setBounds(106, 38, 67, 26);
		registerNoShowPanel.add(noShowAppointmentYearTextField);
		noShowAppointmentYearTextField.setColumns(10);

		noShowAppointmentMonthTextField = new JTextField();
		noShowAppointmentMonthTextField.setBounds(183, 38, 61, 26);
		registerNoShowPanel.add(noShowAppointmentMonthTextField);
		noShowAppointmentMonthTextField.setColumns(10);

		noShowAppointmentDateTextField = new JTextField();
		noShowAppointmentDateTextField.setColumns(10);
		noShowAppointmentDateTextField.setBounds(251, 38, 61, 26);
		registerNoShowPanel.add(noShowAppointmentDateTextField);

		JButton noShowButton = new JButton("No Show");
		noShowButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
		noShowButton.setBounds(145, 108, 117, 29);
		registerNoShowPanel.add(noShowButton);

		noShowAppointmentTimeTextField = new JTextField();
		noShowAppointmentTimeTextField.setColumns(10);
		noShowAppointmentTimeTextField.setBounds(106, 70, 61, 26);
		registerNoShowPanel.add(noShowAppointmentTimeTextField);

		JLabel lblAppTime = new JLabel("App Time");
		lblAppTime.setFont(new Font("SansSerif", Font.PLAIN, 12));
		lblAppTime.setBounds(18, 71, 61, 16);
		registerNoShowPanel.add(lblAppTime);

		JButton startAppButton = new JButton("Start App");
		startAppButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
		startAppButton.setBounds(6, 108, 117, 29);
		registerNoShowPanel.add(startAppButton);

		JButton endAppButton = new JButton("End App");
		endAppButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
		endAppButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				endAppButtonActionPerformed(arg0);
			}
		});
		endAppButton.setBounds(279, 108, 117, 29);
		registerNoShowPanel.add(endAppButton);
		
		// --------------------------------------- listeners ---------------------------------------
		// listener for signup
		signupButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				signupButtonActionPerformed(evt);
			}
		});

		// listener for login
		loginButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				loginButtonActionPerformed(evt);
			}
		});

		// listener for logout
		logOutButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				logOutButtonActionPerformed(evt);
			}
		});

		// listener for update account
		updateAccountButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				updateAccountButtonActionPerformed(evt);
			}
		});

		// listener for delete account
		deleteAccountButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				deleteAccountButtonActionPerformed(evt);
			}
		});

		// listener for add service
		addServiceButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addServiceButtonActionPerformed(evt);
			}
		});

		// listener for delete service
		deleteServiceButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				deleteServiceButtonActionPerformed(evt);
			}
		});

		// listener for update service
		updateServiceButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				updateServiceButtonActionPerformed(evt);
			}
		});
		
		// listener for make Appointment
		makeAppointmentButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				makeAppointmentButtonActionPerformed(evt);
			}
		});
		
		// listener for update Appointment
		updateAppointmentButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				updateAppointmentButtonActionPerformed(evt);
			}
		});
		
		// listener for	cancel Appointment
		cancelAppointmentButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelAppointmentButtonActionPerformed(evt);
			}
		});

		// listener for No Show
		noShowButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				noShowButtonActionPerformed(evt);
			}
		});
		// listener for start app
		startAppButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				startAppButtonActionPerformed(evt);
			}
		});
		// listener for end app
		endAppButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				endAppButtonActionPerformed(evt);
			}
		});

		// listener for calenderQueryByDayButton
		calendarQueryByDateButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				calendarQueryByDayButtonActionPerformed(evt);
			}
		});
		
		// listener for calenderQueryByWeekButton
		calendarQueryByWeekButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				calendarQueryByWeekButtonActionPerformed(evt);
			}
		});
		
		// listenet for view appointments calendar
		VAButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				VAActionPerformed(evt);
			}
		});
	}
	
	
	/**make Appointment Button Action Performed
	 * 
	 * @param evt
	 * @author Ao Shen
	 */
	private void makeAppointmentButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// clear error message
		error = null;

		// call the controller
		try {
			String date = makeAppointmentYearTextField.getText() + "-" + makeAppointmentMonthTextField.getText() + "-"
					+ makeAppointmentDayTextField.getText();
			Date aDate = Date.valueOf(date);
			Time aTime = Time.valueOf(MakeAppointmentTimeTextField.getText() + ":00");
			int index = MAServicecomboBox.getSelectedIndex();
			String[] s = MAServicecomboBox.getItemAt(index).split("/");
			String aBookableService = s[0];

			FlexiBookController.makeAppoinment(aDate, aBookableService, "none", aTime);
		} catch (Exception e) {
			error = e.getMessage();
		}

		// update visuals
		refreshData();
	}
	
	/**update Appointment Button Action Performed
	
	 * 
	 * @param evt
	 * @author Ao Shen
	 */
	private void updateAppointmentButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// clear error message
		error = null;

		// call the controller
		try {
			String newDate = updateAppointmentNewYearTextField.getText() + "-"
					+ updateAppointmentNewMonthTextField.getText() + "-" + updateAppointmentNewDateTextField.getText();
			String oldDate = updateAppointmentOldYearTextField.getText() + "-"
					+ updateAppointmentOldMonthTextField.getText() + "-" + updateAppointmentOldDateTextField.getText();
			int index = UAServicecomboBox.getSelectedIndex();
			String[] s = UAServicecomboBox.getItemAt(index).split("/");
			String aBookableService = s[0];
			FlexiBookController.updateAppoinment(FlexiBookApplication.getCurrentUser().getUsername(),
					Date.valueOf(oldDate), Time.valueOf(UpdateAppointmentOldTimeTextField.getText() + ":00"),
					aBookableService, Date.valueOf(newDate),
					Time.valueOf(UpdateAppointmentTimeTextField.getText() + ":00"), "none", " ");
		} catch (Exception e) {
			error = e.getMessage();
		}

		// update visuals
		refreshData();
	}
	
	/**view Appointments by the help of j table
	 * 
	 * @param evt
	 * @author Ao Shen
	 */
	private void VAActionPerformed(java.awt.event.ActionEvent evt) {
		refreshtable();
		String[] rows1 = new String[3];
		// intial the title of the table
		rows1[0] = "Customer";
		rows1[1] = "Service";
		rows1[2] = "Time";
		model.addRow(rows1);
		List<TOAppointment> app = FlexiBookController.getapps();
		
		//for each row add the info
		for (int i = 0; i < app.size(); i++) {
			
			String[] rows = new String[3];
			rows[0] = app.get(i).getCustomer();
			rows[1] = app.get(i).getBookableServiceName();
			rows[2] = app.get(i).getTime();
			model.addRow(rows);
		}

	}
	
	/**
	 * @author Jianmo Li
	 */
	private void refreshtable() {
		while (model.getRowCount() > 0) {
			model.removeRow(0);
		}
	}
	
	/**cancel Appointment Button Action Performed
	 * 
	 * @param evt
	 * @author Ao Shen
	 */
	private void cancelAppointmentButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// clear error message
		error = null;

		// call the controller
		try {
			String date = cancelAppointmentYearTextField.getText() + "-" + cancelAppointmentMonthTextField.getText()
					+ "-" + cancelAppointmentDayTextField.getText();
			int index = CAServicecomboBox.getSelectedIndex();
			String[] s = CAServicecomboBox.getItemAt(index).split("/");
			String aBookableService = s[0];
			Date d = Date.valueOf(date);
			Time t = Time.valueOf(CancelAppoitmentTimeTextField.getText() + ":00");
			FlexiBookController.cancelAppointment(d, t, aBookableService);

		} catch (Exception e) {
			error = e.getMessage();
		}

		// update visuals
		refreshData();
	}
	
	/**update Service Button Action Performed
	 * 
	 * @param evt
	 * @author Jianmo li
	 */
	private void updateServiceButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// clear error message
		error = null;

		String newServiceName;
		int newDuration, newDownTimeStart, newDowntimeDuration, newDowntimeStart;

		// call the controller
		try {
			if (newServiceNameTextField.getText().equals(null)) {
				newServiceName = FlexiBookController.getService(targetServiceNameTextField.getText()).getName();
			} else {
				newServiceName = newServiceNameTextField.getText();
			}
			if (newDurationTextField.getText().equals(null)) {
				newDuration = FlexiBookController.getService(targetServiceNameTextField.getText()).getDuration();
			} else {
				newDuration = Integer.parseInt(newDurationTextField.getText());
			}
			if (newDowntimeStartTextField.getText().equals(null)) {
				newDowntimeStart = FlexiBookController.getService(targetServiceNameTextField.getText())
						.getDowntimeStart();
			} else {
				newDowntimeStart = Integer.parseInt(newDowntimeStartTextField.getText());
			}
			if (newDowntimeDurationTextField.getText().equals(null)) {
				newDowntimeDuration = FlexiBookController.getService(targetServiceNameTextField.getText())
						.getDowntimeDuration();
			} else {
				newDowntimeDuration = Integer.parseInt(newDowntimeDurationTextField.getText());
			}

			TOService aTOService = new TOService(newServiceName, newDuration, newDowntimeStart, newDowntimeDuration);
			FlexiBookController.updateService(targetServiceNameTextField.getText(), aTOService);
		} catch (Exception e) {
			error = e.getMessage();
		}

		// update visuals
		refreshData();
	}
	
	/**delete Service Button Action Performed
	 * 
	 * @param evt
	 * @author Jianmo li
	 */
	private void deleteServiceButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// clear error message
		error = null;

		// call the controller
		try {
			FlexiBookController.deleteService(deleteServiceNameTextField.getText());
		} catch (Exception e) {
			error = e.getMessage();
		}

		// update visuals
		refreshData();
	}
	
	/**add Service Button Action Performed
	 * 
	 * @param evt
	 * @author Jianmo li
	 */
	private void addServiceButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// clear error message
		error = null;

		// call the controller
		try {
			TOService aTOService = new TOService(serviceNameTextField.getText(),
					Integer.parseInt(serviceDurationTextField.getText()),
					Integer.parseInt(downtimeStartTextField.getText()),
					Integer.parseInt(downtimeDurationTextField.getText()));
			FlexiBookController.addService(aTOService);
		} catch (Exception e) {
			error = e.getMessage();
		}

		// update visuals
		refreshData();
	}
	
	/**sign up Button Action Performed
	 * 
	 * @param evt
	 * @author Kevin li
	 */
	private void signupButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// clear error message
		error = null;

		// call the controller
		try {

			FlexiBookController.signUpCustomerAccount(usernameInputTextField.getText(),
					passwordInputTextField.getText());
		} catch (InvalidInputException e) {
			error = e.getMessage();

		}

		// update visuals
		refreshData();
	}
	
	/**log in Button Action Performed
	 * 
	 * @param evt
	 * @author Kevin li
	 */
	private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// clear error message
		error = null;
		try {
			FlexiBookController.Login(usernameInputTextField.getText(), passwordInputTextField.getText());
		} catch (InvalidInputException e) {
			error = e.getMessage();

		}

		refreshData();
	}
	
	/**log out Button Action Performed
	 * 
	 * @param evt
	 * @author Kevin li
	 */
	private void logOutButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// clear error message
		error = null;
		try {
			FlexiBookController.Logout();
		} catch (InvalidInputException e) {
			error = e.getMessage();
		}

		refreshData();
	}
	
	/**update account Button Action Performed
	 * 
	 * @param evt
	 * @author Kevin li
	 */
	private void updateAccountButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// clear error message
		error = null;
		try {
			FlexiBookController.updateAccount(newUserNameTextField.getText(), newPasswordTextField.getText());
		} catch (InvalidInputException e) {
			error = e.getMessage();

		}

		refreshData();
	}
	
	/**delete account Button Action Performed
	 * 
	 * @param evt
	 * @author Kevin li
	 */
	private void deleteAccountButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// clear error message
		error = null;
		try {
			FlexiBookController.deleteCustomerAccount(targetUserNameTextField.getText());
		} catch (InvalidInputException e) {
			error = e.getMessage();

		}

		refreshData();
	}

	// ----------------------------------------- Ing Tian ------------------------------------------------
	/**sign Up Business Info Action Performed
	 * 
	 * @param evt
	 * @author Ing Tian
	 */
	private void signUpBusinessInfoActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			String businessName = businessNameInputTextField.getText(),
					businessAddress = businessAddressInputTextField.getText(),
					businessPhoneNumber = businessPhoneInputTextField.getText(),
					businessEmail = businessEmailInputTextField.getText();
			FlexiBookController.setupBasicBusinessInformation(
					new TOBusiness(businessName, businessPhoneNumber, businessAddress, businessEmail));
		} catch (Exception e) {
			this.error = e.getMessage();
		}

		this.refreshData();
	}
	
	/**update Business Info Action Performed
	 * 
	 * @param evt
	 * @author Ing Tian
	 */
	private void updateBusinessInfoActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			String businessName = businessNameInputTextField.getText(),
					businessAddress = businessAddressInputTextField.getText(),
					businessPhoneNumber = businessPhoneInputTextField.getText(),
					businessEmail = businessEmailInputTextField.getText();
			FlexiBookController.updateBasicBusinessInformation(
					new TOBusiness(businessName, businessPhoneNumber, businessAddress, businessEmail));
		} catch (Exception e) {
			this.error = e.getMessage();
		}

		refreshData();
	}

	/**
	 * Parse time of type String in format format into a java.sql.Time object.
	 *
	 * @param time
	 * @param format
	 * @return
	 * @throws ParseException
	 * @author Jianmo Li
	 */
	private static Time parseTime(String time, String format) throws ParseException {
		SimpleDateFormat timeFormatter = new SimpleDateFormat(format);
		java.util.Date t = timeFormatter.parse(time);
		return new Time(t.getTime());
	}
	
	/**add Business Info Action Performed
	 * 
	 * @param evt
	 * @author Ing Tian
	 */
	private void addBusinessHour(java.awt.event.ActionEvent evt) {
		try {
			String dayOfWeek = businessHourDayOfWeekInputField.getText(),
					startTime = businessHourStartTimeInputField.getText() + ":00",
					endTime = businessHourEndTimeInputField.getText() + ":00";
			int dayOW = Integer.parseInt(dayOfWeek);
			if(startTime.equals(endTime)){
				this.error="start time cant not equal or after end time";
			}else {
			FlexiBookController.addNewBusinessHours(dayOW, parseTime(startTime, TIME_FORMAT),
					parseTime(endTime, TIME_FORMAT));
			}
		} catch (Exception e) {
			this.error = e.getMessage();
		}
		this.refreshData();
	}
	
	/**update Business hour Action Performed
	 * 
	 * @param evt
	 * @author Ing Tian
	 */
	private void updateBusinessHour(java.awt.event.ActionEvent evt) {
		try {
			String oldDayOfWeek = businessHourCurrentDayOfWeekInputField.getText(),
					oldStartTime = businessHourCurrentStartTimeInputField.getText() + ":00",
					newDayOfWeek = businessHourNewDayOfWeekInputField.getText(),
					newStartTime = businessHourNewStartTimeInputField.getText() + ":00",
					newEndTime = businessHourNewEndTimeInputField.getText() + ":00";
			FlexiBookController.updateBusinessHour(Integer.parseInt(oldDayOfWeek), java.sql.Time.valueOf(oldStartTime),
					Integer.parseInt(newDayOfWeek),
					new TOTimeSlot(null, java.sql.Time.valueOf(newStartTime), null, java.sql.Time.valueOf(newEndTime)));
		} catch (Exception e) {
			this.error = e.getMessage();
		}

		this.refreshData();
	}
	
	/**contorl part of the appointment 
	 * 
	 * @param evt
	 * @author Byron Chen
	 */
	private void noShowButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// clear error message
		this.error = null;
		try {
			if (currentUserNameDesLabel.getText().equals("owner")) {
				String year = noShowAppointmentYearTextField.getText(),
						month = noShowAppointmentMonthTextField.getText(),
						date = noShowAppointmentDateTextField.getText();
				String time = noShowAppointmentTimeTextField.getText();
				String dateStr = year + "-" + month + "-" + date;
				FlexiBookController.registerNoShow(java.sql.Date.valueOf(dateStr), java.sql.Time.valueOf(time+":00"));
			} else
				throw new InvalidInputException("No permission to register no-show.");
		} catch (Exception e) {
			this.error = e.getMessage();
		}

		this.refreshData();
	}
	
	/**contorl part of the appointment 
	 * 
	 * @param evt
	 * @author Byron Chen
	 */
	private void startAppButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// clear error message
		this.error = null;
		try {
			if (currentUserNameDesLabel.getText().equals("owner")) {
				String year = noShowAppointmentYearTextField.getText(),
						month = noShowAppointmentMonthTextField.getText(),
						date = noShowAppointmentDateTextField.getText();
				String time = noShowAppointmentTimeTextField.getText();
				String dateStr = year + "-" + month + "-" + date;
				FlexiBookController.startApp(java.sql.Date.valueOf(dateStr), java.sql.Time.valueOf(time+":00"));
			} else
				throw new InvalidInputException("No permission to start the appointment.");
		} catch (Exception e) {
			this.error = e.getMessage();
		}

		this.refreshData();
	}
	
	/**contorl part of the appointment 
	 * 
	 * @param evt
	 * @author Byron Chen
	 */
	private void endAppButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// clear error message
		this.error = null;
		try {
			if (currentUserNameDesLabel.getText().equals("owner")) {
				String year = noShowAppointmentYearTextField.getText(),
						month = noShowAppointmentMonthTextField.getText(),
						date = noShowAppointmentDateTextField.getText();
				String time = noShowAppointmentTimeTextField.getText();
				String dateStr = year + "-" + month + "-" + date;
				FlexiBookController.endApp(java.sql.Date.valueOf(dateStr), java.sql.Time.valueOf(time+":00"));
			} else
				throw new InvalidInputException("No permission to register no-show.");
		} catch (Exception e) {
			this.error = e.getMessage();
		}

		this.refreshData();
	}

	/** view calendar
	 * 
	 * @param evt
	 * @author Xiang Li
	 */
	private void calendarQueryByDayButtonActionPerformed(java.awt.event.ActionEvent evt) {
		refreshtable();
		error = null;
		String[] rows1 = new String[3];
		//initialize the header of the table
		rows1[0] = "Date";
		rows1[1] = "StartTime";
		rows1[2] = "EndTime";

		model.addRow(rows1);

		try {
			String Date = calendarYearInputTextField.getText() + "-" + calendarMonthInputTextField.getText() + "-"
					+ calendarDayInputTextField.getText();
			
			//return a 2D array list which contains a list of unavailable and a list of available time slots at given Date
			List<List<TOTimeSlot>> serviceCalender = FlexiBookController.viewCalendar(Date, true);
			
			
			//display the info base on the view unavailable is selected or not
			if (viewUnavailable.isSelected()) {
				for (int i = 0; i < serviceCalender.get(0).size(); i++) {

					String[] rows = new String[3];
					rows[0] = serviceCalender.get(0).get(i).getStartDate().toString();
					rows[1] = serviceCalender.get(0).get(i).getStartTime().toString();
					rows[2] = serviceCalender.get(0).get(i).getEndTime().toString();
					model.addRow(rows);
				}
				
			} else {
				for (int i = 0; i < serviceCalender.get(1).size(); i++) {
					String[] rows = new String[3];
					rows[0] = serviceCalender.get(1).get(i).getStartDate().toString();
					rows[1] = serviceCalender.get(1).get(i).getStartTime().toString();
					rows[2] = serviceCalender.get(1).get(i).getEndTime().toString();
					model.addRow(rows);
				}
			}

		} catch (Exception e) {
			this.error = e.getMessage();
		}
		this.refreshError();
	}
	
	/** view calendar
	 * 
	 * @param evt
	 * @author Xiang Li
	 */
	private void calendarQueryByWeekButtonActionPerformed(java.awt.event.ActionEvent evt) {
		refreshtable();
		error = null;
		String[] rows1 = new String[3];
		rows1[0] = "Date";
		rows1[1] = "StartTime";
		rows1[2] = "EndTime";

		model.addRow(rows1);
		try {
			String Date = calendarYearInputTextField.getText() + "-" + calendarMonthInputTextField.getText() + "-"
					+ calendarDayInputTextField.getText();
			
			//return a 2D array list which contains a list of unavailable and a list of available time slots at given Date
			List<List<TOTimeSlot>> serviceCalender = FlexiBookController.viewCalendar(Date, false);
			
			//display the info base on the view unavailable is selected or not
			if (viewUnavailable.isSelected()) {
				for (int i = 0; i < serviceCalender.get(0).size(); i++) {
					String[] rows = new String[3];
					rows[0] = serviceCalender.get(0).get(i).getStartDate().toString();
					rows[1] = serviceCalender.get(0).get(i).getStartTime().toString();
					rows[2] = serviceCalender.get(0).get(i).getEndTime().toString();

					model.addRow(rows);
				}
			} else {
				for (int i = 0; i < serviceCalender.get(1).size(); i++) {
					String[] rows = new String[3];
					rows[0] = serviceCalender.get(1).get(i).getStartDate().toString();
					rows[1] = serviceCalender.get(1).get(i).getStartTime().toString();
					rows[2] = serviceCalender.get(1).get(i).getEndTime().toString();
					model.addRow(rows);
				}
			}

		} catch (Exception e) {
			error = e.getMessage();
		}
		this.refreshError();
	}
	
	
	/** view service in calendar
	 * 
	 * @param evt
	 * @author Xiang Li
	 */
	private void viewServiceButtonActionPerformed(java.awt.event.ActionEvent evt) {
		refreshtable();
		error = null;
		String[] rows1 = new String[3];
		//initial the headers
		rows1[0] = "Name";
		rows1[1] = "Duration";
		rows1[2] = "DownTimeStart/Duration";
		model.addRow(rows1);
		try {
			List<TOService> Services = FlexiBookController.getService();
			for (int i = 0; i < Services.size(); i++) {
				String row[] = new String[34];
				row[0] = Services.get(i).getName();
				row[1] = ((Integer) Services.get(i).getDuration()).toString();
				row[2] = ((Integer) Services.get(i).getDowntimeStart()).toString() + "/"
						+ ((Integer) Services.get(i).getDowntimeDuration()).toString();
				model.addRow(row);
			}
		} catch (Exception e) {
			error = e.getMessage();
		}

	}
	
	/** refresh Data after all the actions
	 * 
	 * @param evt
	 * @author Kevin Li
	 */
	private void refreshData() {

		MAServicecomboBox.removeAllItems();
		for (TOService s : FlexiBookController.getService()) {
			MAServicecomboBox.addItem(s.getName() + "/ duration: " + s.getDuration());
		}

		MAServicecomboBox.setSelectedIndex(-1);
		CAServicecomboBox.removeAllItems();
		for (TOService s : FlexiBookController.getService()) {
			CAServicecomboBox.addItem(s.getName() + "/ duration: " + s.getDuration());
		}
		CAServicecomboBox.setSelectedIndex(-1);
		UAServicecomboBox.removeAllItems();
		for (TOService s : FlexiBookController.getService()) {
			UAServicecomboBox.addItem(s.getName() + "/ duration: " + s.getDuration());
		}
		UAServicecomboBox.setSelectedIndex(-1);

		table = new JTable();

		this.refreshError();
		this.refreshUserInfo();
		this.refreshBasicBusinessInfo();
		this.refreshBusinessHours();
		this.refreshAppointment();
		this.refreshNoShowPanel();
		this.refreshAddService();
		this.refreshDeleteService();
		this.refreshUpdateService();
		this.refreshCalendar();
		refreshtable();
	}
	
	/** refresh Calendar 
	 * 
	 * @param evt
	 * @author Xiang Li
	 */
	private void refreshCalendar() {
		this.calendarYearInputTextField.setText("");
		this.calendarMonthInputTextField.setText("");
		this.calendarDayInputTextField.setText("");

	}
	
	/** refresh the control part of the appointment
	 * 
	 * @param evt
	 * @author Byron Chen
	 */
	private void refreshNoShowPanel() {
		this.noShowAppointmentYearTextField.setText("");
		this.noShowAppointmentMonthTextField.setText("");
		this.noShowAppointmentDateTextField.setText("");
		this.noShowAppointmentTimeTextField.setText("");
	}
	
	/** refresh the the appointments
	 * 
	 * @param evt
	 * @author Ao Shen
	 */
	private void refreshAppointment() {

		makeAppointmentYearTextField.setText("");
		makeAppointmentMonthTextField.setText("");
		makeAppointmentDayTextField.setText("");
		MakeAppointmentTimeTextField.setText("");

		cancelAppointmentYearTextField.setText("");
		cancelAppointmentMonthTextField.setText("");
		cancelAppointmentDayTextField.setText("");

		updateAppointmentNewYearTextField.setText("");
		updateAppointmentOldMonthTextField.setText("");
		updateAppointmentOldDateTextField.setText("");

		updateAppointmentOldYearTextField.setText("");
		updateAppointmentNewMonthTextField.setText("");
		updateAppointmentNewDateTextField.setText("");

		CancelAppoitmentTimeTextField.setText("");
		UpdateAppointmentTimeTextField.setText("");
		UpdateAppointmentOldTimeTextField.setText("");

	}

	/**
	 * Print out error messages on UI. If no error, print nothing.
	 * 
	 * @author Xiang Li
	 */
	private void refreshError() {
		errorMessageTextArea.setText(this.error);
	}

	/**
	 * Refresh user info
	 * 
	 *  @author Jianmo Li
	 */
	private void refreshUserInfo() {
		if (error == null || error.length() == 0) {
			// About user info
			usernameInputTextField.setText("");
			passwordInputTextField.setText("");
			newUserNameTextField.setText("");
			newPasswordTextField.setText("");
			targetUserNameTextField.setText("");
		}
		
		if (FlexiBookApplication.getCurrentUser() != null) {
			currentUserNameDesLabel.setText(FlexiBookApplication.getCurrentUser().getUsername());
		} else
			currentUserNameDesLabel.setText("");
	}

	/**
	 * Refresh current business info. Name, address, phone number, email
	 *  @author Ing Tian
	 */
	private void refreshBasicBusinessInfo() {
		businessHourStartTimeInputField.setText("");
		businessHourEndTimeInputField.setText("");
		businessHourCurrentDayOfWeekInputField.setText("");
		businessHourCurrentStartTimeInputField.setText("");
		businessHourNewDayOfWeekInputField.setText("");
		businessHourNewStartTimeInputField.setText("");
		businessHourNewEndTimeInputField.setText("");
		businessHourDayOfWeekInputField.setText("");
		businessAddressInputTextField.setText("");
		businessPhoneInputTextField.setText("");
		businessEmailInputTextField.setText("");
		businessNameInputTextField.setText("");

		try {
			TOBusiness businessInfo = FlexiBookController.getBusinessInformation();
			currentBusinessInfoPanelNameLbl.setText(businessInfo.getName());
			currentBusinessInfoPanelAddressLbl.setText(businessInfo.getAddress());
			currentBusinessInfoPanelPhoneNumberLbl.setText(businessInfo.getPhoneNumber());
			currentBusinessInfoPanelEmailLbl.setText(businessInfo.getEmail());
		} catch (Exception e) {
		}
	}

	/**
	 * Refresh Business Hour
	 * 
	 * @author Ing Tian
	 */
	private void refreshBusinessHours() {
		businessHourStartTimeInputField.setText("");
		businessHourEndTimeInputField.setText("");
		businessHourCurrentDayOfWeekInputField.setText("");
		businessHourCurrentStartTimeInputField.setText("");
		businessHourNewDayOfWeekInputField.setText("");
		businessHourNewStartTimeInputField.setText("");
		businessHourNewEndTimeInputField.setText("");
		businessHourDayOfWeekInputField.setText("");
		businessAddressInputTextField.setText("");
		businessPhoneInputTextField.setText("");
		businessEmailInputTextField.setText("");
		businessNameInputTextField.setText("");

		try {
			List<TOBusinessHour> businessHours = FlexiBookController.getBusinessHours();
			StringBuilder sb = new StringBuilder();
			for (TOBusinessHour businessHour : businessHours) {
				int dayOW = businessHour.getDayOfWeek();
				String startTime = businessHour.getStartTime().toString();
				String endTime = businessHour.getEndTime().toString();
				sb.append("Day: " + dayOW + " Start at: " + startTime + " End at: " + endTime);
				sb.append("\n");
			}
			businessHourDisplayTextArea.setText(sb.toString());
		} catch (Exception e) {

		}
	}
	
	/**
	 * Refresh Serrvice added
	 * 
	 * @author Byron Chen
	 */
	private void refreshAddService() {
		try {
			serviceNameTextField.setText("");
			serviceDurationTextField.setText("");
			downtimeStartTextField.setText("");
			downtimeDurationTextField.setText("");
			this.refreshError();
		} catch (Exception e) {
		}
	}
	
	/**
	 * Refresh deleted Service
	 * 
	 * @author Byron Chen
	 */
	private void refreshDeleteService() {
		try {
			deleteServiceNameTextField.setText("");
			this.refreshError();
		} catch (Exception e) {

		}
	}
	
	/**
	 * Refresh Update Service
	 * 
	 * @author Jianmo Li
	 */
	private void refreshUpdateService() {
		try {
			targetServiceNameTextField.setText("");
			newServiceNameTextField.setText("");
			newDurationTextField.setText("");
			newDowntimeStartTextField.setText("");
			newDowntimeDurationTextField.setText("");
			this.refreshError();
		} catch (Exception e) {

		}
	}
}

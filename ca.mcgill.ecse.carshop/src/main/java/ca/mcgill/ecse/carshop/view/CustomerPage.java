package ca.mcgill.ecse.carshop.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.SqlDateModel;

import ca.mcgill.ecse.carshop.controller.CarShopController;
import ca.mcgill.ecse.carshop.controller.InvalidInputException;
import ca.mcgill.ecse.carshop.controller.TOAppointment;
import ca.mcgill.ecse.carshop.controller.TOBookableService;
import ca.mcgill.ecse.carshop.controller.TOGarage;
import ca.mcgill.ecse.carshop.controller.TOService;
import ca.mcgill.ecse.carshop.controller.TOServiceCombo;
import ca.mcgill.ecse.carshop.controller.TOUser;

//Coded by Sami Ait Ouahmane/Steven Cho
public class CustomerPage extends JFrame {
	
	private static final long serialVersionUID = 143839453L;

	private JButton logOutButton;
	
	//Error Label
	private JLabel errorMessage;
	private String error = null;
	
	//Customer Make Appointment
	private JTextField appointmentDate;
	private JLabel appointmentDateLabel;
	
	private JTextField appointmentStartTimes;
	private JLabel appointmentStartTimesLabel;
	
	private JTextField bookableNameAppointment;
	private JLabel bookableNameAppointmentLabel;
	
	private JTextField optionalServicesAppointment;
	private JLabel optionalServicesAppointmentLabel;
	
	private JButton makeAppointment;
	
	//Customer Delete Appointment-Update Appointment
	private JComboBox<String> customerAppointmentList;
	private JLabel customerAppointmentsLabel;
	
	private JButton deleteButton;
	private JButton updateButton;
	
	private JTextField newDate;
	private JLabel newDateLabel;
	
	private JTextField newTimes;
	private JLabel newTimesLabel;
	
	private JTextField changeMainService;
	private JLabel changeMainServiceLabel;
	
	private JTextField addOptionalService;
	private JLabel addOptionalServiceLabel;
	
	//Change Date and Time
	private JLabel setTime;
	private JLabel setDate;
	private JTextField setTimeField;
	private JTextField setDateField;
	private JButton setTimeButton;
	private JButton setDateButton;
	
	//View Appointment By Date
	private JTable overviewTable;
	private JScrollPane overviewScrollPane;
	
	private JDatePickerImpl overviewDatePicker;
	private JLabel overviewDateLabel;
	
	private HashMap<Integer, TOAppointment> customerAppointments;
	
	private static final int HEIGHT_OVERVIEW_TABLE = 200;
	
	private DefaultTableModel overviewDtm;

	private String overviewColumnNames[] = {"Bookable", "Date", "Services", "Start Times"};

	//Add Services/Services Combos Tables
	private JTable serviceTable;
	private JScrollPane serviceTableScrollPane;
	
	private JTable combosTable;
	private JScrollPane combosTableScrollPane;
	
	private static final int HEIGHT_SERVICE_TABLE = 150;
	private static final int HEIGHT_COMBOS_TABLE = 150;
	
	private DefaultTableModel serviceDtm;
	private String serviceColumnNames[] = {"Name", "Duration", "Garage"};
	
	private DefaultTableModel serviceCombosDtm;
	private String combosColumnNames[] = {"Name", "Main Service", "Required", "Optional"};
	
		
	public CustomerPage() {
		this.setPreferredSize(new Dimension(800, 700));
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("Car Shop System");
		
		initComponents();
		refreshData();
	}
	
	private void initComponents() {
		errorMessage = new JLabel();
		errorMessage.setForeground(Color.red);
		
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("Car Shop System");
		
		logOutButton = new JButton();
		logOutButton.setText("Log Out");
		
		//Settings the time
		setTimeField = new JTextField();
		setDateField = new JTextField();
		
		setTime = new JLabel("Time: ");
		setDate = new JLabel("Date: ");
		
		setTimeButton = new JButton();
		setTimeButton.setText("Set Time");
		
		setDateButton = new JButton();
		setDateButton.setText("Set Date");
		
		//Elements for error message
		errorMessage = new JLabel();
		errorMessage.setForeground(Color.RED);
		
		//Make Appointment
		appointmentDate = new JTextField();
		appointmentDateLabel = new JLabel("Date: ");
		
		appointmentStartTimes = new JTextField();
		appointmentStartTimesLabel = new JLabel("Start Time(s): (seperated by ',')");
		
		bookableNameAppointment = new JTextField();
		bookableNameAppointmentLabel = new JLabel("Bookable Name: ");
		
		optionalServicesAppointment = new JTextField();
		optionalServicesAppointmentLabel = new JLabel("Optional Services: ");
		
		makeAppointment = new JButton();
		makeAppointment.setText("Make Appointment");
		
		//Customer Delete - Update
		customerAppointmentList = new JComboBox<>(new String[0]);
		customerAppointmentsLabel = new JLabel("Appointments: ");
		
		deleteButton = new JButton();
		deleteButton.setText("Delete");
		updateButton = new JButton();
		updateButton.setText("Update");
		
		newDate = new JTextField();
		newDateLabel = new JLabel("New Date: ");
		
		newTimes = new JTextField();
		newTimesLabel = new JLabel("New Times: ");
		
		changeMainService = new JTextField();
		changeMainServiceLabel = new JLabel("New Main: ");
		
		addOptionalService = new JTextField();
		addOptionalServiceLabel = new JLabel("Optional Service: ");
		
		// elements for daily overview
		SqlDateModel overviewModel = new SqlDateModel();
		LocalDate now = LocalDate.now();
		overviewModel.setDate(now.getYear(), now.getMonthValue() - 1, now.getDayOfMonth());
		overviewModel.setSelected(true);
		Properties pO = new Properties();
		pO.put("text.today", "Today");
		pO.put("text.month", "Month");
		pO.put("text.year", "Year");
		JDatePanelImpl overviewDatePanel = new JDatePanelImpl(overviewModel, pO);
		overviewDatePicker = new JDatePickerImpl(overviewDatePanel, new DateLabelFormatter());
		overviewDateLabel = new JLabel();
		overviewDateLabel.setText("Date for Overview:");
		
		overviewTable = new JTable();
		overviewScrollPane = new JScrollPane(overviewTable);
		this.add(overviewScrollPane);
		Dimension d = overviewTable.getPreferredSize();
		overviewScrollPane.setPreferredSize(new Dimension(d.width, HEIGHT_OVERVIEW_TABLE));
		overviewScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		serviceTable = new JTable();
		serviceTableScrollPane = new JScrollPane(serviceTable);
		this.add(serviceTableScrollPane);
		Dimension d1 = serviceTable.getPreferredSize();
		serviceTableScrollPane.setPreferredSize(new Dimension(d1.width, HEIGHT_SERVICE_TABLE));
		serviceTableScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		combosTable = new JTable();
		combosTableScrollPane = new JScrollPane(combosTable);
		this.add(combosTableScrollPane);
		Dimension d2 = combosTable.getPreferredSize();
		combosTableScrollPane.setPreferredSize(new Dimension(d2.width, HEIGHT_COMBOS_TABLE));
		combosTableScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		deleteButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				deleteButton(evt);
			}
		});
		
		makeAppointment.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				makeAppointmentListener(evt);
			}
		});
		
		updateButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				updateButton(evt);
			}
		});
		
		logOutButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				logOutButton(evt);
			}
		});
		
		setDateButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setDateButton(evt);
			}
		});
		
		setTimeButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setTimeButton(evt);
			}
		});
		
		overviewDatePicker.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				refreshAppointmentsList();
			}
		});
		
		JSeparator seperator1 = new JSeparator();
		JSeparator seperator2 = new JSeparator();
		
		
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		layout.setHorizontalGroup (
			layout.createParallelGroup()
			.addComponent(errorMessage)
			.addComponent(seperator1)
			.addComponent(seperator2)
			.addComponent(logOutButton)
			.addGroup(
				layout.createSequentialGroup()
				.addGroup(
					layout.createParallelGroup()
					.addComponent(appointmentDateLabel)
					.addComponent(appointmentStartTimesLabel)
					.addComponent(bookableNameAppointmentLabel)
					.addComponent(optionalServicesAppointmentLabel)
					
				)
				.addGroup(
					layout.createParallelGroup()
					.addComponent(appointmentDate, 200, 200, 400)	
					.addComponent(appointmentStartTimes, 200, 200, 400)
					.addComponent(bookableNameAppointment, 200, 200, 400)
					.addComponent(optionalServicesAppointment, 200, 200, 400)
					.addComponent(makeAppointment)
				)
				.addGroup(
					layout.createParallelGroup()
					.addComponent(customerAppointmentsLabel)
					.addComponent(newDateLabel)
					.addComponent(newTimesLabel)
					.addComponent(changeMainServiceLabel)
					.addComponent(addOptionalServiceLabel)
						
				)
				.addGroup(
					layout.createParallelGroup()
					.addComponent(customerAppointmentList)
					.addComponent(newDate, 200, 200, 400)
					.addComponent(newTimes, 200, 200, 400)
					.addComponent(changeMainService, 200, 200, 400)
					.addComponent(addOptionalService, 200, 200, 400)
					.addGroup(
						layout.createSequentialGroup()
						.addComponent(deleteButton)
						.addComponent(updateButton)
					)
				)
				
			)
			.addGroup(
				layout.createParallelGroup()
				.addComponent(serviceTableScrollPane)
				.addComponent(combosTableScrollPane)
			)
			.addGroup(
				layout.createSequentialGroup()
				.addGroup(
					layout.createParallelGroup()
					.addComponent(setTime)
					.addComponent(setDate)
				)
				.addGroup(
					layout.createParallelGroup()
					.addComponent(setTimeField, 200, 200, 200)
					.addComponent(setDateField, 200, 200, 200)
				)
				.addGroup(
					layout.createParallelGroup()
					.addComponent(setTimeButton)
					.addComponent(setDateButton)
				)
			)
			.addGroup(
				layout.createParallelGroup()
				.addComponent(overviewScrollPane)
			)
			.addGroup(
				layout.createSequentialGroup()
				.addComponent(overviewDateLabel)
				.addComponent(overviewDatePicker)
			)
			
		);
		
		layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[] {customerAppointmentList, newDate, newTimes, changeMainService, addOptionalService, appointmentDate, appointmentStartTimes, bookableNameAppointment, optionalServicesAppointment});

		
		layout.setVerticalGroup (
			layout.createSequentialGroup()
			.addComponent(errorMessage)
			.addGroup(
				layout.createParallelGroup()
			)
			.addGroup(
				layout.createParallelGroup()
				.addComponent(bookableNameAppointmentLabel)
				.addComponent(bookableNameAppointment)		
				.addComponent(customerAppointmentsLabel)
				.addComponent(customerAppointmentList)
			)
			.addGroup(
				layout.createParallelGroup()
				.addComponent(appointmentDateLabel)
				.addComponent(appointmentDate)
				.addComponent(newDateLabel)
				.addComponent(newDate)
			)
			.addGroup(
				layout.createParallelGroup()
				.addComponent(appointmentStartTimesLabel)
				.addComponent(appointmentStartTimes)

				.addComponent(newTimes)
				.addComponent(newTimesLabel)
			)
			.addGroup(
				layout.createParallelGroup()
				.addComponent(optionalServicesAppointmentLabel)
				.addComponent(optionalServicesAppointment)
				.addComponent(changeMainService)
				.addComponent(changeMainServiceLabel)
			)
			.addGroup(
				layout.createParallelGroup()
				.addComponent(makeAppointment)
				.addComponent(addOptionalService)
				.addComponent(addOptionalServiceLabel)
			)
			.addGroup(
				layout.createParallelGroup()
				.addComponent(updateButton)
				.addComponent(deleteButton)
			)
			.addComponent(seperator1)
			.addComponent(serviceTableScrollPane)
			.addComponent(combosTableScrollPane)
			.addComponent(seperator2)
			.addGroup(
				layout.createParallelGroup()
				.addComponent(overviewDateLabel)
				.addComponent(overviewDatePicker)
			)
			.addGroup(
				layout.createParallelGroup()
				.addComponent(overviewScrollPane)
			)
			.addGroup(
				layout.createParallelGroup()
					.addComponent(setTime)
					.addComponent(setTimeField)
					.addComponent(setTimeButton)
				)
				.addGroup(
					layout.createParallelGroup()
					.addComponent(setDate)
					.addComponent(setDateField)
					.addComponent(setDateButton)
				)
			.addGap(10)
			.addComponent(logOutButton)
		);
			
		pack();
	}
	
	private void refreshData() {
		errorMessage.setText(error);
		
		if(error == null || error.length() == 0) {
			appointmentDate.setText("");
			bookableNameAppointment.setText("");
			appointmentStartTimes.setText("");
			optionalServicesAppointment.setText("");
			
			setDateField.setText("");
			setTimeField.setText("");
			
			newDate.setText("");
			newTimes.setText("");
			changeMainService.setText("");
			addOptionalService.setText("");
			
			TOUser loggedInUser = CarShopController.getTOLoggedIn();
			
			customerAppointments = new HashMap<Integer, TOAppointment>();
			customerAppointmentList.removeAllItems();
			int index = 0;
			
			List<TOAppointment> appointments = CarShopController.getAppointmentsOfCustomer(loggedInUser.getUsername());
			sortedAppointments(appointments);
			Collections.reverse(appointments);
			
			for(TOAppointment app: appointments) {
				customerAppointments.put(index, app);
				customerAppointmentList.addItem(app.getUsername() + ": " + app.getName() +", "+ app.getDate()+ ", "+app.getStartTimes().get(0));
				index++;
			}
			
			this.customerAppointmentList.setSelectedIndex(-1);
			
			
			refreshAppointmentsList();
			refreshAvailableServices();
			refreshAvailableCombos();
		}
	}
	
	private void refreshAvailableServices() {
	      serviceDtm = new DefaultTableModel(0, 0);
	      serviceDtm.setColumnIdentifiers(serviceColumnNames);
	      serviceTable.setModel(serviceDtm);
	      for (TOService service : CarShopController.getServices()) {
	        String serviceName = service.getName();
	        int duration = service.getDuration();
	        TOGarage garage = service.getGarage();
	        String garageName =  garage.getTechnicianUsername() + "'s garage";
	        Object[] obj = {serviceName, duration, garageName};
	        serviceDtm.addRow(obj);
	    }
		  
	    Dimension d = serviceTable.getPreferredSize();
		serviceTableScrollPane.setPreferredSize(new Dimension(d.width, HEIGHT_SERVICE_TABLE));
	}
	
	private void refreshAvailableCombos() {
		serviceCombosDtm = new DefaultTableModel(0, 0);
		serviceCombosDtm.setColumnIdentifiers(this.combosColumnNames);
		combosTable.setModel(serviceCombosDtm);
		
		for(TOServiceCombo combo: CarShopController.getCombos()) {
			String name = combo.getName();
			String main = combo.getMain();
			
			
			String required = "";
			for(int i = 0; i < combo.getRequired().size(); i++) {
				if(i == combo.getRequired().size() - 1) {
					required += combo.getRequired().get(i);
				}
				else {
					required += combo.getRequired().get(i) + ",";
				}
			}
			
			String optional = "";
			for(int i = 0; i < combo.getOptional().size(); i++) {
				if(i == combo.getOptional().size() - 1) {
					optional += combo.getOptional().get(i);
				}
				else {
					optional += combo.getOptional().get(i) + ",";
				}
			}
			
			Object[] obj = {name, main, required, optional};
			serviceCombosDtm.addRow(obj);
		}
		
		Dimension d = combosTable.getPreferredSize();
		this.combosTableScrollPane.setPreferredSize(new Dimension(d.width, HEIGHT_COMBOS_TABLE));
	}
	
	private void refreshAppointmentsList() {
		overviewDtm = new DefaultTableModel(0, 0);
		overviewDtm.setColumnIdentifiers(overviewColumnNames);
		overviewTable.setModel(overviewDtm);
		
		
		TOUser toLoggedIn = CarShopController.getTOLoggedIn();
		for(TOAppointment to: CarShopController.getAppointmentsOfCustomer(toLoggedIn.getUsername())) {
			if(overviewDatePicker.getModel().getValue() != null) {
				int dayPicker = overviewDatePicker.getModel().getDay();
				int dayTo = to.getDate().getDate();
				
				int monthPicker = overviewDatePicker.getModel().getMonth();
				int monthTo = to.getDate().getMonth();
				
				int yearPicker = overviewDatePicker.getModel().getYear();
				int yearTo = to.getDate().getYear()+1900;
				
				if(dayPicker != dayTo || monthPicker != monthTo || yearPicker != yearTo) {
					continue;
				}
			}
			
			String bookable = to.getName();
			Date d = to.getDate();
			
			String services = "";
			for(int i = 0; i < to.getServices().size(); i++) {
				if(i == to.getServices().size() - 1) {
					services += to.getServices().get(i);
				}
				else {
					services += to.getServices().get(i) + ",";
				}
			}
			
			String startTimesStr = "";
			for(int j = 0; j < to.getStartTimes().size(); j++) {
				if(j == to.getStartTimes().size() - 1) {
					startTimesStr += to.getStartTimes().get(j);
				}
				else {
					startTimesStr += to.getStartTimes().get(j) + ",";
				}
			}
			
			Object[] obj = {bookable, d, services, startTimesStr};
			overviewDtm.addRow(obj);
		}
		
		Dimension d = overviewTable.getPreferredSize();
		overviewScrollPane.setPreferredSize(new Dimension(d.width, HEIGHT_OVERVIEW_TABLE));
	}
	
	private void makeAppointmentListener(ActionEvent evt) {
		error = null;
		
		String dateStr = appointmentDate.getText();
		String timesStr = appointmentStartTimes.getText();
		String bookableName = bookableNameAppointment.getText();
		String optionalServices = optionalServicesAppointment.getText();
		
		if(!dateStr.equals("") && !timesStr.equals("") && !bookableName.equals("")) {
			Date date = null;
			try {
				date = convertToDate(dateStr);
			}
			catch(Exception ex) {
				error = "Error with date format entered.";
				refreshData();
				return;
			}
				
			
			List<Time> startTimes = new ArrayList<>();
			for(String s: timesStr.split(",")) {
				try {
					startTimes.add(convertToTime(s));
				}
				catch(Exception ex) {
					error = "Error with time format entered.";
					refreshData();
					return;
				}		
			}
			
			TOBookableService bookable = this.getTOBookableFromName(bookableName);
			if(bookable != null) {
				try {
					if(bookable.getIsCombo() && !optionalServices.equals("")) {
						List<String> optionalS = Arrays.asList(optionalServices.split(","));
						CarShopController.makeAppointmentCombo(bookableName, optionalS, date, startTimes);
					}
					else {
						CarShopController.makeAppointmentService(bookableName, date, startTimes.get(0));
					}
				}
				catch(Exception e) {
					error = e.getMessage();
				}
			}
			else {
				error = "Service Mentioned does not exist";
			}
		}
		else {
			error = "Please complete the first three required fields to process any request.";
		}
		
		refreshData();
	}
	
	private void deleteButton(ActionEvent evt) {
		error = null;
		
		int selectedAppointment = customerAppointmentList.getSelectedIndex();
		if(selectedAppointment < 0) {
			error = "Appointment need to be selected to be deleted";
		}
		
		if(error == null) {
			try {
				TOAppointment to = customerAppointments.get(selectedAppointment);
				CarShopController.customerCancelAppTO(to);
			} catch (InvalidInputException e) {
				error = e.getMessage();
			}
			finally {
				refreshData();
			}
		}
	}
	
	private void logOutButton(ActionEvent evt) {
		CarShopController.logOut();
		this.setVisible(false);
		
		new LogInPage().setVisible(true);
	}
	
	private void updateButton(ActionEvent evt) {
		error = null;
		
		int selectedAppointment = customerAppointmentList.getSelectedIndex();
		if(selectedAppointment < 0) {
			error = "Appointment need to be selected to be updated";
		}
		
		if(error == null) {
			String newDateStr = newDate.getText();
			String newTimesStr = newTimes.getText();
			String setMainStr = changeMainService.getText();
			String addOptionalStr = addOptionalService.getText();
			
			//First we update the optional service.
			if(!addOptionalStr.equals("") && !newTimesStr.equals("")) {
				TOAppointment to = customerAppointments.get(selectedAppointment);
				if(to.getIsComboApp()) {
					TOBookableService toB = getTOBookableFromName(addOptionalStr);
					
					Time newTime = null;
					
					try {
						newTime = convertToTime(newTimesStr.split(",")[0]);
					}
					catch(Exception ex) {
						error = "Error in time format";
						refreshData();
						return;
					}
					
					if(toB == null || newTime == null) {
						error = "Invalid Input.";
					}
					else {
						try {
							CarShopController.addOptionalServiceTO(to, addOptionalStr, newTime);
						}
						catch(InvalidInputException ex) {
							error = ex.getMessage();
						}
					}
				}
			}
			//Second we update the main service.
			else if(!setMainStr.equals("")) {
				TOAppointment to = customerAppointments.get(selectedAppointment);
				if(!to.getIsComboApp()) {
					TOBookableService toB = getTOBookableFromName(setMainStr);
					if(toB == null) {
						error = "Service does not exist in the system";
					}
					else {
						try {
							CarShopController.setMainServiceTO(to, setMainStr);
						} catch (InvalidInputException e) {
							error = e.getMessage();
						}
					}
				}
				else {
					error = "Cannot change the main service of a service combo.";
				}
			}
			//Finally we update the new date and times
			else if(!newDateStr.equals("") && !newTimesStr.equals("")) {
				
				Date d = null;
				try {
					d = convertToDate(newDateStr);
				}
				catch(Exception ex) {
					error = "Error in date format";
					refreshData();
					return;
				}
					
				List<Time> startTimes = new ArrayList<>();
				
				for(String time: newTimesStr.split(",")) {
					try {
						startTimes.add(convertToTime(time));
					}
					catch(Exception ex) {
						error = "Error in time format";
						refreshData();
						return;
					}

				}
				
				try {
					CarShopController.updateDateTimesTO(customerAppointments.get(selectedAppointment), d, startTimes);
				}
				catch(InvalidInputException e) {
					error = e.getMessage();
				}
			}
			else {
				refreshData();
			}
		}
		
		refreshData();
	}
	
	private void setDateButton(ActionEvent evt) {
		String dateStr = this.setDateField.getText();
		if(!dateStr.equals("")) {
			
			try {
				Date date = convertToDate(dateStr);
				CarShopController.setToday(date);
			}
			catch(Exception ex) {
				error = "Error with date format entered.";
			}
		}
		
		refreshData();
	}
	
	private void setTimeButton(ActionEvent evt) {
		String timeStr = this.setTimeField.getText();
		if(!timeStr.equals("")) {
			
			try {
				Time time = convertToTime(timeStr);
				CarShopController.setTime(time);
			}
			catch(Exception ex) {
				error = "Error with time format entered.";
			}
		}
		
		refreshData();
	}
	
	
	private Date convertToDate(String date) throws NumberFormatException {
		String[] splits = date.split("-");
		
		int year = Integer.parseInt(splits[0]) - 1900;
		int month = Integer.parseInt(splits[1]) - 1;
		int day = Integer.parseInt(splits[2]);
		
		return new Date(year, month, day);
	}
	
	private Time convertToTime(String time) throws NumberFormatException {
		String[] splits = time.split(":");
		int hours = Integer.parseInt(splits[0]);
		int minutes = Integer.parseInt(splits[1]);
		
		return new Time(hours, minutes, 0);
	}
	
	private void sortedAppointments(List<TOAppointment> tos) {
		for(int i = 0; i < tos.size(); i++) {
			int min_index = i;
			
			for(int j = i+ 1; j < tos.size(); j++) {
				Date currentDate = tos.get(j).getDate();
				
				if(currentDate.before(tos.get(min_index).getDate())) {
					min_index = j;
				}
			}
			
			TOAppointment temp = tos.get(min_index);
			tos.set(min_index, tos.get(i));
			tos.set(i, temp);
			
		} 
	}
	
	private TOBookableService getTOBookableFromName(String name) {
		for(TOBookableService to: CarShopController.getTOBookables()) {
			if(to.getName().equals(name)) {
				return to;
			}
		}
		
		return null;
	}
	
	

}

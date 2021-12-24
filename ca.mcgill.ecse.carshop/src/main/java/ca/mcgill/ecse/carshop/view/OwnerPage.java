package ca.mcgill.ecse.carshop.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import ca.mcgill.ecse.carshop.controller.InvalidUserException;
import ca.mcgill.ecse.carshop.controller.TOAppointment;
import ca.mcgill.ecse.carshop.controller.TOBusiness;
import ca.mcgill.ecse.carshop.controller.TOGarage;
import ca.mcgill.ecse.carshop.controller.TOService;
import ca.mcgill.ecse.carshop.controller.TOServiceCombo;

//Coded by Mario Bouzakhm/Hadi Ghaddar
public class OwnerPage extends JFrame {
	private static final long serialVersionUID = 1633915862703837868L;
	
	private JLabel errorMessage;
	private String error = null;
	
	private JButton logOutButton;
	
	//Business Info setter element
	private JTextField BNameTextField;
	private JTextField BAddressTextField;
	private JTextField BPhoneNumberTextField;
	private JTextField BEmailTextField;
	private JButton setBizInfoButton;
	private JLabel BNameLabel;
	private JLabel BAddressLabel;
	private JLabel BPhoneNumberLabel;
	private JLabel BEmailLabel;
	
	//Business Info displayer element
	private JLabel BInfoPresenter;
	private JLabel BNameDisplayerLabel;
	private JLabel BAddressDisplayerLabel;
	private JLabel BPhoneNumberDisplayerLabel;
	private JLabel BEmailDisplayerLabel;
	private JLabel BNameDisplayerField;
	private JLabel BAddressDisplayerField;
	private JLabel BPhoneNumberDisplayerField;
	private JLabel BEmailDisplayerField;
	
	//Handles the appointment management
	private JComboBox<String> appointmentList;
	private JLabel appointmentListLabel;
	private JButton startButton;
	private JButton endButton;
	private JButton registerNoShow;
	
	//View Appointment By Date
	private JTable overviewTable;
	private JScrollPane overviewScrollPane;
	
	//Change Date and Time
	private JLabel setTime;
	private JLabel setDate;
	private JTextField setTimeField;
	private JTextField setDateField;
	private JButton setTimeButton;
	private JButton setDateButton;
	
	//Add Service 
	private JLabel serviceNameLabel;
	private JTextField serviceNameTextField;
	private JLabel serviceDurationLabel;
	private JTextField serviceDurationTextField;
	private JComboBox<String> garageList;
	private JButton addServiceButton;
	
	// Book Combo
	private JTextField comboNameTextField;
	private JLabel comboNameLabel;
	private JComboBox<String> mainServiceList;
	private JLabel mainServiceLabel;
	private JTextField servicesTextField;
	private JLabel servicesLabel;
	private JTextField mandatoryTextField;
	private JLabel mandatoryLabel;
	private JButton defineComboButton;
	
	private JTable serviceTable;
	private JScrollPane serviceTableScrollPane;
	
	private JTable combosTable;
	private JScrollPane combosTableScrollPane;
	
	private JDatePickerImpl overviewDatePicker;
	private JLabel overviewDateLabel;
	
	private static final int HEIGHT_SERVICE_TABLE = 100;
	private static final int HEIGHT_COMBOS_TABLE = 100;
	private static final int HEIGHT_OVERVIEW_TABLE = 120;
	
	//Appointment Management
	private HashMap<Integer, TOAppointment> appointments;
	private HashMap<Integer, TOGarage> garages;
	private HashMap<Integer, String> existingServices;

	private DefaultTableModel overviewDtm;
	private String overviewColumnNames[] = {"Bookable", "Customer", "Date", "Services", "Start Times"};
	
	private DefaultTableModel serviceDtm;
	private String serviceColumnNames[] = {"Name", "Duration", "Garage"};
	
	private DefaultTableModel serviceCombosDtm;
	private String combosColumnNames[] = {"Name", "Main Service", "Required", "Optional"};
	
	public OwnerPage() {
		this.setPreferredSize(new Dimension(1050, 820));
		
		initComponents();
		refreshData();
	}
	
	private void initComponents() {
		errorMessage = new JLabel();
		errorMessage.setForeground(Color.red);
		
		logOutButton = new JButton();
		logOutButton.setText("Log Out");
		
		//elements for BusinessInfoSetter
		BNameTextField = new JTextField();
		BAddressTextField = new JTextField();
		BPhoneNumberTextField = new JTextField();
		BEmailTextField = new JTextField();
		BNameLabel = new JLabel();
		BNameLabel.setText("Name: ");
		BAddressLabel = new JLabel();
		BAddressLabel.setText("Address: ");
		BPhoneNumberLabel = new JLabel();
		BPhoneNumberLabel.setText("Phone Number: ");
		BEmailLabel = new JLabel();
		BEmailLabel.setText("Email: ");
		setBizInfoButton = new JButton();
		setBizInfoButton.setText("Set Business Information");
		
		//elements for BusinessInfoDisplayer
		BInfoPresenter=new JLabel();
		BInfoPresenter.setText("Business Information: ");
		BNameDisplayerLabel=new JLabel();
		BNameDisplayerLabel.setText("Name: ");
		BAddressDisplayerLabel=new JLabel();
		BAddressDisplayerLabel.setText("Address: ");
		BPhoneNumberDisplayerLabel=new JLabel();
		BPhoneNumberDisplayerLabel.setText("Phone Number: ");
		BEmailDisplayerLabel= new JLabel();
		BEmailDisplayerLabel.setText("Email: ");
		BNameDisplayerField= new JLabel();
		BNameDisplayerField.setText( "" );
		BAddressDisplayerField= new JLabel();
		BAddressDisplayerField.setText("");
		BPhoneNumberDisplayerField= new JLabel();
		BPhoneNumberDisplayerField.setText("");
		BEmailDisplayerField= new JLabel();
		BEmailDisplayerField.setText("");
		
		//Settings the time
		setTimeField = new JTextField();
		setDateField = new JTextField();
		
		setTime = new JLabel("Time: ");
		setDate = new JLabel("Date: ");
		
		setTimeButton = new JButton();
		setTimeButton.setText("Set Time");
		
		setDateButton = new JButton();
		setDateButton.setText("Set Date");
		
		//Elements for Owner Appointment Management.
		appointmentList = new JComboBox<>(new String[0]);
		appointmentListLabel = new JLabel("Appointments: ");
		
		startButton = new JButton();
		startButton.setText("Start Appointment");
		endButton = new JButton();
		endButton.setText("End Button");
		registerNoShow = new JButton();
		registerNoShow.setText("No Show");
		
		overviewTable = new JTable();
		overviewScrollPane = new JScrollPane(overviewTable);
		this.add(overviewScrollPane);
		Dimension d = overviewTable.getPreferredSize();
		overviewScrollPane.setPreferredSize(new Dimension(d.width, HEIGHT_OVERVIEW_TABLE));
		overviewScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		// elements for add service
		serviceNameLabel = new JLabel();
		serviceNameLabel.setText("Name: ");
		serviceNameTextField = new JTextField();
		serviceDurationLabel = new JLabel();
		serviceDurationLabel.setText("Duration: ");
		serviceDurationTextField = new JTextField();
		garageList = new JComboBox<String>(new String[0]);
		addServiceButton = new JButton();
		addServiceButton.setText("Add Service");
		  
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
		
		
		// elements for combo booking
		comboNameTextField = new JTextField();
		comboNameLabel = new JLabel();
		comboNameLabel.setText("Enter Combo Name: ");
		
		mainServiceList = new JComboBox<String>(new String[0]);
		mainServiceLabel = new JLabel();
		mainServiceLabel.setText("Select Main Service: ");
		
		servicesTextField = new JTextField();
		servicesLabel = new JLabel();
		servicesLabel.setText("Enter Required Services (separated by ','): ");
		
		mandatoryTextField = new JTextField();
		mandatoryLabel = new JLabel();
		mandatoryLabel.setText("Enter Optional Services (separated by ','): ");
		
		defineComboButton = new JButton();
		defineComboButton.setText("Add New Service Combo");
		
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
	
		// global settings
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("Car Shop System");
		
		//add listener to setup business info
		setBizInfoButton.addActionListener(new java.awt.event.ActionListener() {
		    public void actionPerformed(java.awt.event.ActionEvent evt) {
		        SetUpBusinessInfoAccountButtonActionPerformed(evt);
		    }
		});
		
		startButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				startButtonActionPerformed(evt);
			}
		});
		
		endButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				endButtonActionPerformed(evt);
			}
		});
		
		registerNoShow.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				registerNoShow(evt);
			}
		});
		
		// action listeners for service
		addServiceButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
	          addServiceButtonActionPerformed(evt);
	        }
	    });
		
		// listeners for define Combo
		defineComboButton.addActionListener(new java.awt.event.ActionListener() {
		    public void actionPerformed(java.awt.event.ActionEvent evt) {
		        defineComboButtonActionPerformed(evt);
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
		
		JSeparator line1 = new JSeparator();
		JSeparator line2 = new JSeparator();
		JSeparator line3 = new JSeparator();
		JSeparator line4 = new JSeparator();
		
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
			.addComponent(errorMessage)
			.addComponent(line1)
			.addComponent(line2)
			.addComponent(line3)
			.addComponent(line4)
			.addComponent(logOutButton)
			.addGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(BNameLabel)
      				.addComponent(BAddressLabel)
      				.addComponent(BEmailLabel)
      				.addComponent(BPhoneNumberLabel)
					.addComponent(serviceNameLabel)
	                .addComponent(serviceDurationLabel)
	      		)
				.addGroup(layout.createParallelGroup()
					.addComponent(BNameTextField, 200, 200, 200)
      				.addComponent(BAddressTextField)
      				.addComponent(BEmailTextField)
      				.addComponent(BPhoneNumberTextField)
      				.addComponent(setBizInfoButton)
      				
					.addComponent(serviceNameTextField)
                    .addComponent(serviceDurationTextField)
                    .addComponent(garageList)
                    .addComponent(addServiceButton)	
	      		)
				.addGroup(layout.createParallelGroup()
					.addComponent(BInfoPresenter)
      				.addComponent(BNameDisplayerLabel)
      				.addComponent(BAddressDisplayerLabel)
      				.addComponent(BEmailDisplayerLabel)
      				.addComponent(BPhoneNumberDisplayerLabel)
      				
					.addComponent(comboNameLabel)
					.addComponent(mainServiceLabel)
					.addComponent(servicesLabel)
					.addComponent(mandatoryLabel)
	      		)
				.addGroup(layout.createParallelGroup()
					.addComponent(BNameDisplayerField)
      				.addComponent(BAddressDisplayerField)
      				.addComponent(BEmailDisplayerField)
      				.addComponent(BPhoneNumberDisplayerField)
      				
					.addComponent(comboNameTextField, 200, 200, 200)
					.addComponent(mainServiceList)
					.addComponent(servicesTextField)
					.addComponent(mandatoryTextField)
					.addComponent(defineComboButton)
	      		)
			)
			.addComponent(serviceTableScrollPane)
			.addComponent(combosTableScrollPane)
			.addGroup(
				layout.createSequentialGroup()
				.addComponent(appointmentListLabel)
				.addGroup(
					layout.createParallelGroup()
					.addComponent(appointmentList)
					.addGroup(
						layout.createSequentialGroup()
						.addComponent(startButton)
						.addComponent(endButton)
						.addComponent(registerNoShow)		
					)	
				)
			)
			.addGroup(
				layout.createSequentialGroup()
				.addComponent(setTime)
				.addComponent(setTimeField)
				.addComponent(setTimeButton)
				.addComponent(setDate)
				.addComponent(setDateField)
				.addComponent(setDateButton)

//				.addGroup(
//					layout.createParallelGroup()
//					.addComponent(setTime)
//					.addComponent(setDate)
//				)
//				.addGroup(
//					layout.createParallelGroup()
//					.addComponent(setTimeField, 200, 200, 200)
//					.addComponent(setDateField, 200, 200, 200)
//				)
//				.addGroup(
//					layout.createParallelGroup()
//					.addComponent(setTimeButton)
//					.addComponent(setDateButton)
//				)
			)
			.addGroup(
				layout.createSequentialGroup()
				.addComponent(overviewDateLabel)
				.addComponent(overviewDatePicker)
			)
			.addComponent(overviewScrollPane)
			.addComponent(logOutButton)
		);
		
		layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[] {setBizInfoButton, BPhoneNumberTextField, BEmailTextField,BAddressTextField,BNameTextField, serviceNameTextField, serviceDurationTextField, addServiceButton, garageList});
		layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[] {defineComboButton, comboNameTextField, mainServiceList, servicesTextField, mandatoryTextField});
		
		layout.setVerticalGroup(
			layout.createSequentialGroup()
			.addComponent(errorMessage)
			.addGroup(
				layout.createParallelGroup()
				.addComponent(BNameLabel)
				.addComponent(BNameTextField)
				.addComponent(BInfoPresenter)
			)
			.addGroup(
				layout.createParallelGroup()
				.addComponent(BAddressLabel)
				.addComponent(BAddressTextField)
				.addComponent(BNameDisplayerLabel)
				.addComponent(BNameDisplayerField)
			)
			.addGroup(
				layout.createParallelGroup()
				.addComponent(BEmailLabel)
				.addComponent(BEmailTextField)
				.addComponent(BAddressDisplayerLabel)
				.addComponent(BAddressDisplayerField)
			)
			.addGroup(
				layout.createParallelGroup()
				.addComponent(BPhoneNumberLabel)
				.addComponent(BPhoneNumberTextField)
				.addComponent(BEmailDisplayerLabel)
				.addComponent(BEmailDisplayerField)
			)
			.addGroup(
				layout.createParallelGroup()
				.addComponent(setBizInfoButton)
				.addComponent(BPhoneNumberDisplayerLabel)
				.addComponent(BPhoneNumberDisplayerField)
			)
			.addComponent(line1)
			.addGroup(
				layout.createParallelGroup()
				.addComponent(serviceNameLabel)
				.addComponent(serviceNameTextField)
				.addComponent(comboNameLabel)
				.addComponent(comboNameTextField)
			)
			.addGroup(
				layout.createParallelGroup()
				.addComponent(serviceDurationLabel)
				.addComponent(serviceDurationTextField)
				.addComponent(mainServiceLabel)
				.addComponent(mainServiceList)
			)
			.addGroup(
				layout.createParallelGroup()
				.addComponent(garageList)
				.addComponent(servicesLabel)
				.addComponent(servicesTextField)
			)
			.addGroup(
				layout.createParallelGroup()
				.addComponent(addServiceButton)
				.addComponent(mandatoryLabel)
				.addComponent(mandatoryTextField)
			)
			.addComponent(defineComboButton)
			.addComponent(line4)
			.addComponent(serviceTableScrollPane)
			.addComponent(combosTableScrollPane)
			.addComponent(line3)
			.addGroup(
				layout.createParallelGroup()
				.addComponent(appointmentListLabel)
				.addComponent(appointmentList)
			)
			.addGroup(
				layout.createParallelGroup()
				.addComponent(startButton)
				.addComponent(endButton)
				.addComponent(registerNoShow)
			)
			.addComponent(line2)
			.addGroup(
				layout.createParallelGroup()
				.addComponent(overviewDateLabel)
				.addComponent(overviewDatePicker)
			)
			.addComponent(overviewScrollPane)
			.addGroup(
				layout.createParallelGroup()
				.addComponent(setTime)
				.addComponent(setTimeField)
				.addComponent(setTimeButton)
				.addComponent(setDate)
				.addComponent(setDateField)
				.addComponent(setDateButton)
			)
//			.addGroup(
//				layout.createParallelGroup()

//			)
			.addGap(10)
			.addComponent(logOutButton)
//			
		);
		
		layout.linkSize(SwingConstants.VERTICAL, new java.awt.Component[] {setBizInfoButton, BPhoneNumberTextField, BEmailTextField,BAddressTextField,BNameTextField, serviceNameTextField, serviceDurationTextField, addServiceButton, garageList});
		
		pack();
	}
	
	private void refreshData() {
		errorMessage.setText(error);
		if(error == null || error.length() == 0) {
			
			setTimeField.setText("");
			setDateField.setText("");
			
			BNameTextField.setText("");
			BAddressTextField.setText("");
			BPhoneNumberTextField.setText("");;
			BEmailTextField.setText("");;
			
			//Appointments List
			appointments = new HashMap<Integer, TOAppointment>();
			appointmentList.removeAllItems();
			int index = 0;
			
			List<TOAppointment> sortAppointments = CarShopController.getAppointments();
			sortedAppointments(sortAppointments);
			Collections.reverse(sortAppointments);
			
			for(TOAppointment app: sortAppointments) {
				appointments.put(index, app);
				appointmentList.addItem(app.getUsername() + ": " + app.getName() +", "+ app.getDate()+ ", "+app.getStartTimes().get(0));
				index++;
			}
			
			appointmentList.setSelectedIndex(-1);
			
			// service name
	        serviceNameTextField.setText("");
	        // duration
	        serviceDurationTextField.setText("");

	        garages = new HashMap<Integer, TOGarage>();
	        garageList.removeAllItems();
	        index = 0;
	        for (TOGarage garage : CarShopController.getGarages()) {
	          garages.put(index, garage);
	          garageList.addItem(garage.getTechnicianUsername() + "'s garage (" + garage.getTechnicianType() + " technician)");
	            index++;
	        };
	        garageList.setSelectedIndex(-1);
			
	        // populate page with data
	    	// comboName
	    	comboNameTextField.setText("");
	    	// services
	    	servicesTextField.setText("");
	    	// mandatory
	    	mandatoryTextField.setText("");
	    	
	    	// define combo - main service
	    	existingServices = new HashMap<Integer, String>();
	    	mainServiceList.removeAllItems();
	    	index = 0;
	    	
	    	for (TOService service : CarShopController.getServices()) {
	    		existingServices.put(index,service.getName());
	    		mainServiceList.addItem(service.getName());
	    		index++;		
	    	}
	    	mainServiceList.setSelectedIndex(-1);
			    
			refreshAppointmentsList();
			refreshDataBusinessInfo();
			refreshAvailableServices();
			refreshAvailableCombos();
		}
	}
	
	private void refreshDataBusinessInfo(){
		TOBusiness biz = CarShopController.getBusiness();
		if(biz==null) {
			BNameDisplayerField.setText("");;
			BAddressDisplayerField.setText("");
			BPhoneNumberDisplayerField.setText("");;
			BEmailDisplayerField.setText("");
		}
		else {
			BNameDisplayerField.setText(biz.getName());;
			BAddressDisplayerField.setText(biz.getAddress());
			BPhoneNumberDisplayerField.setText(biz.getPhoneNumber());;
			BEmailDisplayerField.setText(biz.getEmail());
		}
	}
	
	private void refreshAppointmentsList() {
		overviewDtm = new DefaultTableModel(0, 0);
		overviewDtm.setColumnIdentifiers(overviewColumnNames);
		overviewTable.setModel(overviewDtm);
		
		for(TOAppointment to: CarShopController.getAppointments()) {
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
			
			Object[] obj = {bookable, to.getUsername(), d, services, startTimesStr};
			overviewDtm.addRow(obj);
		}
		
		Dimension d = overviewTable.getPreferredSize();
		overviewScrollPane.setPreferredSize(new Dimension(d.width, HEIGHT_OVERVIEW_TABLE));
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
	
	private void SetUpBusinessInfoAccountButtonActionPerformed(java.awt.event.ActionEvent evt) {
		error="";
		try {
			if(CarShopController.getBusiness() == null) {
				CarShopController.setBusinessInfo(BNameTextField.getText(), BAddressTextField.getText(), BPhoneNumberTextField.getText(), BEmailTextField.getText());

			}
			else {
				CarShopController.updateBusinessInfo(BNameTextField.getText(), BAddressTextField.getText(), BPhoneNumberTextField.getText(), BEmailTextField.getText());
			}			
		}
		catch(InvalidInputException e) {
			error=e.getMessage();
		}
		catch(InvalidUserException e) {
			error=e.getMessage();
		}
		
		refreshData();
	}
	
	private void startButtonActionPerformed(ActionEvent evt) {
		error = null;
		
		int selectedAppointment = appointmentList.getSelectedIndex();
		if(selectedAppointment < 0) {
			error = "Appointment need to be selected to be started";
		}
		
		if(error == null) {
			CarShopController.startAppointmentTO(appointments.get(selectedAppointment));
		}
		
		refreshData();
	}
	
	private void endButtonActionPerformed(ActionEvent evt) {
		error = null;
		
		int selectedAppointment = appointmentList.getSelectedIndex();
		if(selectedAppointment < 0) {
			error = "Appointment need to be selected to be ended";
		}
		
		if(error == null) {
			CarShopController.endApponitmentTO(appointments.get(selectedAppointment));
		}
		
		refreshData();
	}
	
	private void registerNoShow(ActionEvent evt) {
		error = null;
		
		int selectedAppointment = appointmentList.getSelectedIndex();
		if(selectedAppointment < 0) {
			error = "Appointment need to be selected to be registered no-show";
		}
		
		if(error == null) {
			CarShopController.registerNoShowTO(appointments.get(selectedAppointment));
		}
		
		refreshData();
	}
	
	private void addServiceButtonActionPerformed(java.awt.event.ActionEvent evt) {
      // clear error message
      error = "";
      
	      
      int selectedGarage = garageList.getSelectedIndex();
      if (selectedGarage < 0) {
        error = "Garage has to be selected to add a service!";
      }
      if (error.length() == 0) {
        try {
          TOGarage garage = garages.get(selectedGarage);
          String technicianUsername = garage.getTechnicianUsername();
          CarShopController.createService(serviceNameTextField.getText(), Integer.parseInt(serviceDurationTextField.getText()), CarShopController.getGarageFromTechnicianType(technicianUsername));
        } catch (InvalidInputException e) {
            error = e.getMessage();
        } catch (RuntimeException e) {
          error = e.getMessage();
        } 
      }        
      // update visuals
      refreshData();
	}
	
	private void defineComboButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// clear error message and basic input validation
		error = "";
		
		int selectedService = mainServiceList.getSelectedIndex();
		if (selectedService < 0)
			error = "Service needs to be selected for definition! ";
		String comboName = comboNameTextField.getText();
		if (comboName.equals(""))
			error = error + "Combo Name canot be empty! ";
		
		String services = servicesTextField.getText();
		String mandatorySettings = mandatoryTextField.getText();
		String main = existingServices.get(selectedService);
		
		List<String> requiredServices = new ArrayList<String>(Arrays.asList(services.split(",")));
		requiredServices.add(main);
		List<String> optionalServices = new ArrayList<String>(Arrays.asList(mandatorySettings.split(",")));
		
		List<String> servicesList = new ArrayList<String>();
		List<Boolean> mandatorySettingsBoolean = new ArrayList<>();
		
		for(int i = 0; i < requiredServices.size(); i++) {
			servicesList.add(requiredServices.get(i));
			mandatorySettingsBoolean.add(true);
		}
		
		for(int i = 0; i < optionalServices.size(); i++) {
			String service = optionalServices.get(i);
			if(!service.equals("")) {
				servicesList.add(optionalServices.get(i));
				mandatorySettingsBoolean.add(false);
			}
		}
		
		
		error = error.trim();
		if (error.length() == 0) {
			// call the controller
			try { 
				CarShopController.defineCombo(comboName, main, servicesList, mandatorySettingsBoolean);
			} catch (InvalidInputException e) {
				error = e.getMessage();
			}
		}
		// update visuals
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
				error = "Error with date format entered";
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
	
	private void logOutButton(ActionEvent evt) {
		CarShopController.logOut();
		this.setVisible(false);
		
		new LogInPage().setVisible(true);
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
}

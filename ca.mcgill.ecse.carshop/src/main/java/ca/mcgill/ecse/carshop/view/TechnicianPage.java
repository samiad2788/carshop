package ca.mcgill.ecse.carshop.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import ca.mcgill.ecse.carshop.controller.CarShopController;
import ca.mcgill.ecse.carshop.controller.TOGarage;
import ca.mcgill.ecse.carshop.controller.TOService;
import ca.mcgill.ecse.carshop.controller.TOServiceCombo;

//Coded by Youssef Farouk
public class TechnicianPage extends JFrame {
	private static final long serialVersionUID = 3247238947283L;
	
	private JLabel errorMessage;
	private String error = null;
	
	private JButton logOutButton;
	
	private JTable serviceTable;
	private JScrollPane serviceTableScrollPane;
	
	private JTable combosTable;
	private JScrollPane combosTableScrollPane;
	
	private static final int HEIGHT_SERVICE_TABLE = 100;
	private static final int HEIGHT_COMBOS_TABLE = 100;
	
	private DefaultTableModel serviceDtm;
	private String serviceColumnNames[] = {"Name", "Duration", "Garage"};
	
	private DefaultTableModel serviceCombosDtm;
	private String combosColumnNames[] = {"Name", "Main Service", "Required", "Optional"};
	
	public TechnicianPage() {
		this.setSize(new Dimension(700, 700));
		
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("Car Shop System");
		
		initComponents();
		refreshData();
	}
	
	private void initComponents() {
		errorMessage = new JLabel();
		errorMessage.setForeground(Color.red);
		
		logOutButton = new JButton();
		logOutButton.setText("Log Out");
		
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
		
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addComponent(errorMessage)
				.addComponent(logOutButton)
				.addComponent(serviceTableScrollPane)
				.addComponent(combosTableScrollPane)
				
		);
		
		layout.setVerticalGroup(
			layout.createSequentialGroup()
			.addComponent(errorMessage)
			.addComponent(serviceTableScrollPane)
			.addComponent(combosTableScrollPane)
			.addComponent(logOutButton)
		);
		
		logOutButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				logOutButton(evt);
			}
		});
	}
	
	private void refreshData() {
		errorMessage.setText(error);
		if(error == null || error.length() == 0) {
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
	
	private void logOutButton(ActionEvent evt) {
		CarShopController.logOut();
		this.setVisible(false);
		
		new LogInPage().setVisible(true);
	}
}

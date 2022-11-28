package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

public class VueController {

	@FXML
	TextField bill;
	
	@FXML
	TextField tipPerCent;
	
	@FXML
	TextField numberOfP;
	
	@FXML
	TextField tipsResult;
	
	@FXML
	TextField tipsTotal;
	
	@FXML
	DatePicker date;
	
	@FXML
	TextField errorField;
	
	public void calculateTips() throws ClassCastException {
		try {
			float bill = this.checkFloat(this.bill.getText(), "bill");	
			float tipPerCent = this.checkFloat(this.tipPerCent.getText(), "tipPerCent");
			float numberOfP = this.checkFloat(this.numberOfP.getText(), "numberOfP");	
			
			if(numberOfP == 0) throw new Exception("numberOfP is set to 0");
			
			float tipsResult = bill * (tipPerCent/100);	
			float tipsTotal = (bill + tipsResult) / numberOfP;
			
			this.tipsResult.setText(Float.toString(tipsResult/numberOfP)); 
			this.tipsTotal.setText(Float.toString(tipsTotal));
			
			String date = this.checkDate(this.date.getValue());
			
			if(this.findDate(date)) date="\n"+date;
			
			this.writeFile(date+","+bill+","+tipPerCent+","+numberOfP+"\r\n");
			
		}catch(Exception e) {
			this.errorField.setText("Veuillez recommencer le calcul car " + e.getMessage());
			System.out.println(e);
		}
	}
	
	private float checkFloat(String value, String type) throws NumberFormatException{
		try {
			if(Float.parseFloat(value) < 0) throw new NumberFormatException("");
			return Float.parseFloat(value);
		}catch(NumberFormatException e) {
			throw new NumberFormatException(type + " : est incorrect veuillez utiliser des chiffres > 0");
		}
	}
	
	private String checkDate(LocalDate date) throws Exception{
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date d = sdf.parse(date.toString());
			sdf.applyPattern("dd/MM/yyyy");
			return sdf.format(d);
		}catch(Exception e) {
			throw new Exception("date est null ou incorrect veuillez utiliser le format yyyy-mm-dd");
		}
	}
	
	
	public void writeFile(String value) throws IOException {
		try (FileWriter fichier = new FileWriter("src\\application\\logs.txt", true)) {
			fichier.write(value);
		}
	}
	
	private boolean findDate(String date) throws IOException{
		Scanner fileScanner = new Scanner(new File("src\\application\\logs.txt"));
		String allFil = "";
		boolean find = false;
		
		while (fileScanner.hasNextLine()) {
			String row = fileScanner.nextLine();
			if(row.contains(date)) {
				find = true;
			}else {
				allFil += row;				
			}
		 }
		
		if(find) {
			try (FileWriter fichier = new FileWriter("src\\application\\logs.txt")) {
				fichier.write(allFil);
			}
		}
		return find;
	}
	
	public void hydrate() throws Exception {
		Scanner fileScanner = new Scanner(new File("src\\application\\logs.txt"));
		String date = this.checkDate(this.date.getValue());
	
		while (fileScanner.hasNextLine()) {
			String row = fileScanner.nextLine();
			if(row.contains(date)) {
				String[] tab = row.split(",");
				this.bill.setText(tab[1]);
				this.tipPerCent.setText(tab[2]);
				this.numberOfP.setText(tab[3]);
			}
		 }
	}
}
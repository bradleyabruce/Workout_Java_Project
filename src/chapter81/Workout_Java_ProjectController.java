package chapter81;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;

import com.sun.javafx.scene.control.skin.LabeledText;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Alert.AlertType;

/**
* This class connects to the database.
* @author Bradley Bruce
*/
public class Workout_Java_ProjectController implements Initializable{

			@FXML
			private ComboBox<String> cboSelMuscleGroup;

			@FXML
			private ComboBox<String> cboSelExercise;

			@FXML
			private TextArea taInfo;

			@FXML
			private Button btnAddToPlan;

			@FXML
			private Button btnUpdateInfo;

			@FXML
			private Button btnDeleteExercise;

			@FXML
			private ComboBox<String> cboSelMuscleGroupNew;
	    
			@FXML
			private TextField tfExerciseName;

			@FXML
			private TextArea taInsertInfo;
	   
			@FXML
			private Button btnCreateExercise;
	    
			@FXML
			private CheckBox chkboxAddToPlan;
	    
			@FXML
			private Label lblStatus;
			
			@FXML
			private ListView<String> lstboxPlan;
	    
			@FXML
			private Button btnReset;
	    
			@FXML
			private Button btnDelItem;

			@FXML
			private Button btnUp;

			@FXML
			private Button btnDown;
	    
	    Connection conn;
	    
	    String CurrentBodyPartID;
	    String CurrentExerciseID;
	    String CurrentExerciseName;
	    
	    ObservableList<String> lstItems = FXCollections.observableArrayList();
	    ObservableList<String> lstItems2 = FXCollections.observableArrayList();
	    ObservableList<String> lstItems3 = FXCollections.observableArrayList();
	    
	    	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	    		// TODO Auto-generated method stub
		
	    	DBConnect();
	    	LoadComboBodyPart();
	    	btnCreateExercise.setOnAction(e->DBInsert());
	    	btnDeleteExercise.setOnAction(e->DBDelete());
			btnUpdateInfo.setOnAction(e->Update());	
			// adding action to first combobox
			cboSelMuscleGroup.getSelectionModel().selectedItemProperty().addListener(e->LoadComboExercise());
			// adding action to second combobox
			cboSelExercise.getSelectionModel().selectedItemProperty().addListener(e->LoadTextAreaInfo());
			btnAddToPlan.setOnAction(e->AddToPlan());
			btnReset.setOnAction(e->ResetPlan());
			btnDelItem.setOnAction(e->DeletedSelectedItem());
			btnUp.setOnAction(e->MoveUp());
			btnDown.setOnAction(e->MoveDown());
			lstboxPlan.setOnMouseClicked(e->PopUpInfo(e));
			
	}//Initializable
	

private void DBConnect(){
		try{
			//load the jdbc driver
			Class.forName("com.mysql.jdbc.Driver");
			
			//establish a connection
			conn = DriverManager.getConnection("jdbc:mysql://localhost/dbworkout", "scott", "tiger");
			
		}//end try
		catch(ClassNotFoundException | SQLException e){
			System.out.println(e.getMessage());
		}
}//end DBConnect
	
	
	/** skjdbvisfu
	 
	 */
	
	
private void DBInsert(){
			
			String lblPlan = "";
		
			GetBodyPartIDFromComboBoxInsert();//get BodyPartID from Selected BodyPart ComboBox
			
			if (CurrentBodyPartID == null || cboSelMuscleGroupNew.getValue() == null){//if no value, throw popup
			
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Warning");
				alert.setHeaderText(null);
				alert.setContentText("Please Select a Body Part Using the Drop-Down Box.");
				alert.showAndWait();
			
			}//end if
			
			else{
		
				//get new name and info from text boxes	
				
				String NewExerciseName = tfExerciseName.getText();
	
			if (tfExerciseName.getText().isEmpty()){//if no value, throw popup
				
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Warning");
				alert.setHeaderText(null);
				alert.setContentText("Please Name Your New Workout.");
				alert.showAndWait();
			}//end if
			
			else{
	
				String NewExerciseInfo = taInsertInfo.getText();
		
			if(taInsertInfo.getText().isEmpty()){//if no value, throw popup
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Confirmation Dialog");
				alert.setHeaderText("Your New Workout Does Not Have Any Info. Do You Want to Continue?");
				alert.setContentText("Confirm Entry?");
				Optional<ButtonType> result = alert.showAndWait();
			}//end if
		
			//insert data into database
			String sqlInsert = "INSERT INTO texercise(ExerciseName, BodyPartID, Info)" + " values (?, ?, ?)";
		
		try {
				PreparedStatement prest1 = (PreparedStatement)conn.prepareStatement(sqlInsert);
				prest1.setString(1, NewExerciseName.trim());
				prest1.setString(3, NewExerciseInfo.trim());
				prest1.setInt(2, Integer.parseInt(CurrentBodyPartID));
				prest1.executeUpdate();
			
			if (chkboxAddToPlan.isSelected()){//if checkbox is selected add workout to plan
				lstItems3.add(NewExerciseName);
				lstboxPlan.setItems((ObservableList)lstItems3);
				lblPlan = " and Added To Plan";
			}//end if
			
				lblStatus.setText(NewExerciseName + " Inserted" + lblPlan);
				cboSelMuscleGroupNew.setValue(null);
				tfExerciseName.setText("");
				taInsertInfo.setText(null);
				chkboxAddToPlan.setSelected(false);
				
		}//end try
		catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}//end catch	
			}//end else
			}//end else
}//end DBInsert
	
	

	
private void DBDelete(){
		
		try{
			GetExerciseIDFromComboBoxSelect();//get bodypartID from combo box selection
			
			if (CurrentExerciseID == null){//if no value, throw popup
				
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Warning");
				alert.setHeaderText(null);
				alert.setContentText("Please Select a Workout to Delete Using the Drop-Down Box.");
				alert.showAndWait();
			}//end if
			else{
				
			String ExerciseValueName = cboSelExercise.getValue();
			
			if (cboSelExercise.getValue() == null){//if no value, throw popup
					
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Warning");
				alert.setHeaderText(null);
				alert.setContentText("Please Select a Workout to Delete Using the Drop-Down Box.");
				alert.showAndWait();
			}//end if
			else{
				
				int ExerciseValueID = Integer.parseInt(CurrentExerciseID);//change CurrentExerciseID from String to int
			//delete entry
				String sqlUpdate = "Delete from texercise where ExerciseId = ?";
				PreparedStatement prest = conn.prepareStatement(sqlUpdate);
				prest.setInt(1, ExerciseValueID);
			//confirm delete
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Confirmation Dialog");
				alert.setHeaderText("Delete Confirmation Dialog");
				alert.setContentText("Confirm Delete?");
				Optional<ButtonType> result = alert.showAndWait();
			
			if (result.get() == ButtonType.OK){//if delete is confirmed. Then delete.
				
				prest.executeUpdate();
				lblStatus.setText(ExerciseValueName + " Deleted");
				lstItems2.remove(ExerciseValueName);
				cboSelExercise.setValue(null);
				cboSelMuscleGroup.setValue(null);
				taInfo.setText(null);	
			}//end if
			else {//if delete is not confirmed
				
			    lblStatus.setText("Delete Record Cancelled");
			}//end else
			}//end else
			}//end else
		}//end try
		catch (SQLException e){
			e.printStackTrace();
		}//end catch
}//end DBDelete
	
	
	

private void Update(){
		
	GetBodyPartIDFromComboBoxSelect();//get BodyPart ID from combo box selection
			
		if (CurrentBodyPartID == null){//if no value, throw popup
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Warning");
			alert.setHeaderText(null);
			alert.setContentText("Please Select a Body Part and Workout Using the Drop-Down Boxes.");
			alert.showAndWait();
		}//end if
		
		else{
			
	GetExerciseIDFromComboBoxSelect();//get Exercise ID from combo box selection
		
		if (CurrentExerciseID == null){//if no value, throw popup
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Warning");
			alert.setHeaderText(null);
			alert.setContentText("Please Select a Workout to Update Using the Drop-Down Box.");
			alert.showAndWait();	
		}//end if
					
		else{
			int intBodyPartID = Integer.parseInt(CurrentBodyPartID);
			int intExerciseID = Integer.parseInt(CurrentExerciseID);
				
			String sqlUpdate = "Update texercise set ExerciseName = ?, BodyPartID = ?, Info = ? where ExerciseID = ?";
			PreparedStatement prest;
	try {
			
		if (cboSelExercise.getValue() == null){//if no value, throw popup
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Warning");
			alert.setHeaderText(null);
			alert.setContentText("Please Select a Workout to Update Using the Drop-Down Box.");
			alert.showAndWait();
		}//end if
		
		//update selected entry
		else{
				prest = conn.prepareStatement(sqlUpdate);
				prest.setString(1, cboSelExercise.getValue().trim());
				prest.setString(3, taInfo.getText().trim());
				prest.setInt(2, intBodyPartID);
				prest.setInt(4, intExerciseID);
				prest.executeUpdate();
				lblStatus.setText(cboSelExercise.getValue().trim() + " Updated");
			}//end else
					
	}//end try
	catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	}//end catch
						
				cboSelExercise.setValue(null);
				cboSelMuscleGroup.setValue(null);
				taInfo.setText(null);
			}//end else
			}//end else
		
}//end Update




private void LoadComboExercise(){//Load entries of exercises based on bodypartID
		
			taInfo.clear();
			lstItems2.clear();
		GetBodyPartIDFromComboBoxSelect();
			int BodyPartValue = Integer.parseInt(CurrentBodyPartID);
			String sqlSelect = "SELECT ExerciseName FROM texercise INNER JOIN tbodypart ON tbodypart.BodyPartID = texercise.BodyPartID WHERE tbodypart.BodyPartID = ?";
			PreparedStatement prest;
	try {
			prest = conn.prepareStatement(sqlSelect);
			prest.setInt(1, BodyPartValue);
			ResultSet rs = prest.executeQuery();
		
	while(rs.next()){
			lstItems2.add(rs.getString(1));
	}//end while
			
			cboSelExercise.setItems((ObservableList)lstItems2);
		
	}//end try
	catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}//end catch

}//end LoadComboExercise
	

	
	
private void GetBodyPartIDFromComboBoxInsert(){//get BodyPart ID from BodyPart combo box on Insert Tab
		
			String BodyPartValue = cboSelMuscleGroupNew.getValue();
			String sqlSelect = "Select * from tbodypart where BodyPartName = ?";
			PreparedStatement prest;
	try {
			prest = conn.prepareStatement(sqlSelect);
			prest.setString(1, BodyPartValue);
			ResultSet rs = prest.executeQuery();
		if(rs.next()){
				CurrentBodyPartID = (rs.getString(1));
		}//end if
			
	}//end try
	catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
	}//end catch
	
}//end GetBodyPartIDFromComboBox

	
	
	
private void GetBodyPartIDFromComboBoxSelect(){//get BodyPart ID from BodyPart combo box on Workout Tab
		
			String BodyPartValue = cboSelMuscleGroup.getSelectionModel().getSelectedItem();
			String sqlSelect = "Select * from tbodypart where BodyPartName = ?";
			PreparedStatement prest;
	try {
			prest = conn.prepareStatement(sqlSelect);
			prest.setString(1, BodyPartValue);
			ResultSet rs = prest.executeQuery();
		if(rs.next()){
				CurrentBodyPartID = (rs.getString(1));
		}//end if
		
	}//end try
	catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
	}//end catch
	
}//end GetBodyPartIDFromComboBoxSelect
	
	
	
	
private void GetExerciseIDFromComboBoxSelect(){//get Exercise ID from Exercise ComboBox on Workout Tab
			String ExerciseValue = cboSelExercise.getValue();
			String sqlSelect = "Select * from texercise where ExerciseName = ?";
			PreparedStatement prest;
	try {
			prest = conn.prepareStatement(sqlSelect);
			prest.setString(1, ExerciseValue);
			ResultSet rs = prest.executeQuery();
		if(rs.next()){
				CurrentExerciseID = (rs.getString(1));
		}//end if
	}//end try
	catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
	}//end catch
			
}//end GetBodyPartIDFromComboBoxSelect
	
	
	
	
private void LoadComboBodyPart(){//Load BodyPart ComboBox from database
		
	try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("Select BodyPartName from tbodypart;");
		while(rs.next()){
				lstItems.add(rs.getString(1));
		}//end while
			cboSelMuscleGroup.setItems((ObservableList)lstItems);
			cboSelMuscleGroupNew.setItems((ObservableList)lstItems);
	}//end try
	catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	}//end catch
	
}//LoadComboBodyPart
	

	
private void LoadTextAreaInfo(){
		
			CurrentExerciseName = null;
			String CurrentExerciseName = cboSelExercise.getSelectionModel().getSelectedItem();
		
			String sqlSelect = "Select Info from texercise where ExerciseName = ?";
			PreparedStatement prest;
	try {
				prest = conn.prepareStatement(sqlSelect);
				prest.setString(1, CurrentExerciseName);
				ResultSet rs = prest.executeQuery();
			if(rs.next()){
					taInfo.setText(rs.getString(1));
			}//end if
	}//end try
	catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
	}//end catch

}//end LoadTextAreaInfo

	
	
	
private void AddToPlan(){//add selected workout to daily plan
			
			String AddExerciseToPlan = cboSelExercise.getValue();
			
		if (AddExerciseToPlan == null){//if no value, throw popup	
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Warning");
			alert.setHeaderText(null);
			alert.setContentText("Please Select a Workout Using the Drop-Down Boxes.");
			alert.showAndWait();
		}//end if

		else{
			AddExerciseToPlan.toString();
			lstItems3.add(AddExerciseToPlan);
			lstboxPlan.setItems((ObservableList)lstItems3);
			lblStatus.setText(AddExerciseToPlan + " Added To Plan");
			cboSelExercise.setValue(null);
			cboSelMuscleGroup.setValue(null);
			lstItems2.clear();
			taInfo.setText("");
		}//end else

}//end AddToPlan()
	
	
private void ResetPlan(){//wipes daily plan and daily plan list box
			lstItems3.clear();
			lstboxPlan.setItems((ObservableList)lstItems3);
			lblStatus.setText("Plan Reset");
}//end ResetPlan
	
	
private void DeletedSelectedItem(){//deletes selected workout from daily plan (does not affect database)
			String deletedItem = lstboxPlan.getSelectionModel().getSelectedItem();
		
			if (deletedItem == null){//if no value, throw popup
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Warning");
				alert.setHeaderText(null);
				alert.setContentText("Please Select a Workout to Remove From Plan.");
				alert.showAndWait();
			}//end if
			
			else{
				lstItems3.remove(deletedItem);
				lblStatus.setText(deletedItem + " Removed From Daily Plan");
			}//end else
			
}//end DeletedSelection
	
	
	
private void MoveUp(){//moves selected workout up one space in the array, unless it is in postion 0
	
			String tempnameUp;
			String tempnameDown;
			String ExerciseToMove = lstboxPlan.getSelectionModel().getSelectedItem();
			
		if (ExerciseToMove == null){//if no value, throw popup
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Warning");
			alert.setHeaderText(null);
			alert.setContentText("Please Select a Workout to Move Up.");
			alert.showAndWait();
		}//end if
		
			int ExerciseIndex = lstItems3.indexOf(ExerciseToMove);	
				
			for(int i = ExerciseIndex; i > ExerciseIndex - 1; i --){	

				if (i <= 0){
					return;
				}//end if
			
				else{
					tempnameDown = lstItems3.get((i-1));
					tempnameUp = lstItems3.get(i);
					lstItems3.set((i-1), tempnameUp);
					lstItems3.set(i, tempnameDown);
				}//end else
					lstboxPlan.refresh();
			}//end for
}//end MoveUp



private void MoveDown(){//moves selected workout down one space in the array, unless it is in last possible position
		
			lstboxPlan.setItems(lstItems3);
			String tempnameUp;
			String tempnameDown;
			String ExerciseToMove = lstboxPlan.getSelectionModel().getSelectedItem();
		
		if (ExerciseToMove == null){//if no value, throw popup		
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Warning");
			alert.setHeaderText(null);
			alert.setContentText("Please Select a Workout to Move Down.");
			alert.showAndWait();
		}//end if
	
	
			int ExerciseIndex = lstItems3.indexOf(ExerciseToMove);
	
			for(int i = ExerciseIndex; i < ExerciseIndex + 1; i ++){	

				if (i == lstItems3.size() - 1){
					return;
				}//end if
		
				else{
					tempnameDown = lstItems3.get((i+1));
					tempnameUp = lstItems3.get(i);
					lstItems3.set((i+1), tempnameUp);
					lstItems3.set(i, tempnameDown);
		
		   
				}//end else
					lstboxPlan.refresh();
			}//end for
}//end MoveUp



	

@SuppressWarnings("restriction")
public void PopUpInfo(MouseEvent click) {
    	
		if(click.getButton() == MouseButton.PRIMARY && click.getClickCount() == 2 &&
		(click.getTarget() instanceof LabeledText || (click.getTarget() instanceof ListCell ))) {
        		
				String Info = null;//intilizing string to be used later
           //Use ListView's getSelected Item
        		
				String currentItemSelected = lstboxPlan.getSelectionModel().getSelectedItem();
        		String sqlSelect = "Select info from texercise where ExerciseName = ?";
        		PreparedStatement prest;
        		
   	try {
   				prest = conn.prepareStatement(sqlSelect);
   				prest.setString(1, currentItemSelected);
   				ResultSet rs = prest.executeQuery();
   				
   		if(rs.next()){
   			Info = (rs.getString(1));
   		}//end if
   		
   	}//end try
   	catch (SQLException e) {
   				// TODO Auto-generated catch block
   				e.printStackTrace();
   	}//end catch
   			
   			Alert alert = new Alert(AlertType.INFORMATION);
   			alert.setTitle("Exercise Info");
   			alert.setHeaderText(null);
   			alert.setContentText(Info);
   			alert.showAndWait();
           
        }//end if

}//end PopUpInfo


	

}//end class
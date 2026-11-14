package antofruch.final_project.Contollers.MainView;

import antofruch.final_project.Facade;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


public class AddPointPaneController {
    MainViewController mainViewController;
    void setMainController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    @FXML
    private Button addButton;
    @FXML
    private TextField nameField;
    @FXML
    private TextField latField;
    @FXML
    private TextField lngField;
    @FXML
    private TextArea descField;

    @FXML
    private Label errorLabel;

    @FXML
    void handleAdd(){
        String name = nameField.getText();
        String desc = descField.getText();
        String lat = latField.getText();
        String lng = lngField.getText();
        if (name.isEmpty() || desc.isEmpty() || lat.isEmpty() || lng.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        try {
            double lat_db = Double.parseDouble(lat);
            double lng_db = Double.parseDouble(lng);

            Facade.getInstance().createNewPOI(name, lat_db, lng_db, desc);
            mainViewController.update();
        } catch (NumberFormatException e) {
            showError("Please enter valid numbers for latitude and longitude.");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        errorLabel.setText("");
    }

}

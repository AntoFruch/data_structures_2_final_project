package antofruch.final_project.Contollers.MainView;

import antofruch.final_project.Facade;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


public class GeneratePaneController {
    @FXML
    private TextField generateInputField;

    @FXML
    private Label errorLabel;

    MainViewController mainViewController;
    void setMainController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    @FXML
    private void handleGenerate(){
        try {
            int N = Integer.parseInt(generateInputField.getText());

            if (N < 0) {
                showError("Please enter a valid positive integer.");
                return;
            }
            hideError();
            Facade.getInstance().generateRandomData(N);
            if (mainViewController != null) {
                mainViewController.update();
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid positive integer.");
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

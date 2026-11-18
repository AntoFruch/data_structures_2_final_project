package antofruch.final_project.Contollers.MainView;

import antofruch.final_project.Facade;
import antofruch.final_project.POI;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.List;

public class FindNamePaneController {
    @FXML
    private TextField nameField;

    @FXML
    private TextField minLatField;

    @FXML
    private TextField maxLatField;

    @FXML
    private TextField minLngField;

    @FXML
    private TextField maxLngField;

    @FXML
    private Button searchButton;

    @FXML
    private Label errorLabel;

    private MainViewController mainController;

    public void setMainController(MainViewController mainController) {
        this.mainController = mainController;
    }

    public MainViewController getMainController() {
        return mainController;
    }

    @FXML
    private void handleSearch() {
        String name = nameField.getText();
        if (name == null || name.trim().isEmpty()) {
            showError("Please enter a name to search for.");
            return;
        }

        Double minLat = parseCoordinate(minLatField.getText());
        Double maxLat = parseCoordinate(maxLatField.getText());
        Double minLng = parseCoordinate(minLngField.getText());
        Double maxLng = parseCoordinate(maxLngField.getText());

        String validationError = validateCoordinates(minLat, maxLat, minLng, maxLng);
        if (validationError != null) {
            showError(validationError);
            return;
        }

        try {
            List<POI> results = Facade.getInstance().findPOIbyName(name, minLat, maxLat, minLng, maxLng);
            hideError();

            if (mainController != null) {
                mainController.display(results);
            }
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Search error: " + e.getMessage());
        }
    }

    public static Double parseCoordinate(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(text.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String validateCoordinates(Double minLat, Double maxLat, Double minLng, Double maxLng) {
        if (minLat == null || maxLat == null || minLng == null || maxLng == null) {
            return "Please enter valid decimal numbers for all coordinates.";
        }
        if (minLat > maxLat) {
            return "Min latitude must be less than or equal to max latitude.";
        }
        if (minLng > maxLng) {
            return "Min longitude must be less than or equal to max longitude.";
        }
        return null;
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

    public TextField getMinLatField() {
        return minLatField;
    }

    public TextField getMaxLatField() {
        return maxLatField;
    }

    public TextField getMinLngField() {
        return minLngField;
    }

    public TextField getMaxLngField() {
        return maxLngField;
    }

    public Button getSearchButton() {
        return searchButton;
    }

    public Label getErrorLabel() {
        return errorLabel;
    }

}

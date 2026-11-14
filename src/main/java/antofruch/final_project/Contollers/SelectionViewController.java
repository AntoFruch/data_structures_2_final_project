package antofruch.final_project.Contollers;

import antofruch.final_project.Contollers.MainView.MainViewController;
import antofruch.final_project.TreeType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class SelectionViewController {

    @FXML
    private ComboBox<String> structureComboBox;

    @FXML
    private Button chooseButton;

    @FXML
    private VBox boundsContainer;

    @FXML
    private TextField minLatField;

    @FXML
    private TextField maxLatField;

    @FXML
    private TextField minLngField;

    @FXML
    private TextField maxLngField;

    @FXML
    private TextField maxSubDepthField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button confirmButton;

    @FXML
    public void initialize() {
        structureComboBox.getItems().addAll(TreeType.QuadTree.name(), TreeType.KdTree.name(), TreeType.MixedTree.name());
        structureComboBox.getSelectionModel().selectFirst();

        structureComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            boundsContainer.setVisible(false);
            boundsContainer.setManaged(false);
            confirmButton.setVisible(false);
            confirmButton.setManaged(false);
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
            errorLabel.setText("");
        });
    }

    @FXML
    private void handleChoose() {
        TreeType selectedStructure = TreeType.valueOf(structureComboBox.getValue());

        boolean needsBounds = isBoundsRequired(selectedStructure);
        boundsContainer.setVisible(needsBounds);
        boundsContainer.setManaged(needsBounds);

        confirmButton.setVisible(true);
        confirmButton.setManaged(true);

        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        errorLabel.setText("");
    }

    @FXML
    private void handleConfirm() {
        TreeType selectedStructure = TreeType.valueOf(structureComboBox.getValue());

        if (isBoundsRequired(selectedStructure)) {
            Double minLat = parseCoordinate(minLatField.getText());
            Double maxLat = parseCoordinate(maxLatField.getText());
            Double minLng = parseCoordinate(minLngField.getText());
            Double maxLng = parseCoordinate(maxLngField.getText());
            int max_sub_depth = Integer.parseInt(maxSubDepthField.getText());

            String validationError = validateBounds(minLat, maxLat, minLng, maxLng);
            if (validationError != null) {
                showError(validationError);
                return;
            }

            switchToMainView(selectedStructure, minLat, maxLat, minLng, maxLng, max_sub_depth);
        } else {
            switchToMainView(selectedStructure, null, null, null, null, null);
        }
    }

    public static boolean isBoundsRequired(TreeType structure) {
        return structure == TreeType.MixedTree || structure == TreeType.QuadTree;
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

    public static String validateBounds(Double minLat, Double maxLat, Double minLng, Double maxLng) {
        if (minLat == null || maxLat == null || minLng == null || maxLng == null) {
            return "Please enter valid decimal numbers for all bounding coordinates.";
        }
        if (minLat >= maxLat) {
            return "Min latitude must be less than max latitude.";
        }
        if (minLng >= maxLng) {
            return "Min longitude must be less than max longitude.";
        }
        return null;
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void switchToMainView(TreeType structure, Double minLat, Double maxLat, Double minLng, Double maxLng, Integer max_sub_depth) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../main-view/main-view.fxml"));
            Scene mainScene = new Scene(loader.load(), 800, 600);

            MainViewController mainController = loader.getController();
            mainController.initData(structure, minLat, maxLat, minLng, maxLng, max_sub_depth);

            Stage stage = (Stage) confirmButton.getScene().getWindow();
            stage.setScene(mainScene);
            stage.centerOnScreen();
        } catch (IOException e) {
            showError("Failed to load main view.");
            e.printStackTrace();
        }
    }

    public ComboBox<String> getStructureComboBox() {
        return structureComboBox;
    }

    public Button getChooseButton() {
        return chooseButton;
    }

    public VBox getBoundsContainer() {
        return boundsContainer;
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

    public Label getErrorLabel() {
        return errorLabel;
    }

    public Button getConfirmButton() {
        return confirmButton;
    }
}

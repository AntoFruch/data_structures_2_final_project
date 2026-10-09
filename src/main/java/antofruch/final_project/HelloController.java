package antofruch.final_project;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    private Button actionButton;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("L'application est prête !");

        // Optionnel : tu peux désactiver le bouton ou changer son texte après le clic
        actionButton.setText("C'est fait !");
        actionButton.setDisable(true);
    }
}

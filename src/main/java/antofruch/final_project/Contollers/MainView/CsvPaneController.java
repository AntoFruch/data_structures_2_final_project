package antofruch.final_project.Contollers.MainView;

import antofruch.final_project.Facade;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class CsvPaneController {
    MainViewController mainViewController;
    void setMainController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    @FXML
    Button importButton;

    @FXML
    Button exportButton;

    @FXML
    void handleImport(javafx.event.ActionEvent event){
        FileChooser fc = new FileChooser();
        fc.setTitle("Open Csv file");

        // Show only Csv Files
        FileChooser.ExtensionFilter csvFilter =
                new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fc.getExtensionFilters().add(csvFilter);
        fc.setSelectedExtensionFilter(csvFilter);

        Stage stage = (Stage) ((Control) event.getSource()).getScene().getWindow();
        File file = fc.showOpenDialog(stage);

        if (file != null) {
            Facade.getInstance().importCsv(file);
        }
        mainViewController.update();

    }

    @FXML
    void handleExport(javafx.event.ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export to CSV");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv")
        );

        Stage stage = (Stage) ((Control) event.getSource()).getScene().getWindow();

        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            Facade.getInstance().exportCsv(file);
        }
    }

    private void saveDataToFile(File file) {

    }
}

package antofruch.final_project.Contollers.MainView;

import antofruch.final_project.Facade;
import antofruch.final_project.POI;
import antofruch.final_project.TreeType;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.List;

public class MainViewController {

    @FXML
    private Label infoLabel;

    @FXML
    private ListView<POI> resultListView;

    @FXML
    private RangeSearchPaneController rangeSearchPaneController;
    @FXML
    private GeneratePaneController generatePaneController;
    @FXML
    private AddPointPaneController addPointPaneController;
    @FXML
    private CsvPaneController csvPaneController;
    @FXML
    private FindNamePaneController findNamePaneController;

    private TreeType activeStructure;

    @FXML
    public void initialize() {
        Facade.getInstance();
        if (rangeSearchPaneController != null) {
            rangeSearchPaneController.setMainController(this);
        }
        if (generatePaneController != null) {
            generatePaneController.setMainController(this);
        }
        if(addPointPaneController != null) {
            addPointPaneController.setMainController(this);
        }
        if(csvPaneController != null) {
            csvPaneController.setMainController(this);
        }
        if(findNamePaneController != null) {
            findNamePaneController.setMainController(this);
        }
    }

    public void initData(TreeType structure, Double minLat, Double maxLat, Double minLng, Double maxLng, Integer max_sub_depth) {
        this.activeStructure = structure;
        String str = "Active structure: " + activeStructure;

        if (structure != TreeType.KdTree) {
            Facade.getInstance().createTree(structure, minLat, maxLat, minLng, maxLng, max_sub_depth);
            str += " [Lat: " + minLat + " to " + maxLat + ", Lng: " + minLng + " to " + maxLng + " max_depth: " + max_sub_depth + "]";
        } else {
            Facade.getInstance().createTree(structure, 0.0, 0.0, 0.0, 0.0, 0);
        }

        infoLabel.setText(str);
    }

    public void display(List<POI> points) {
        if (resultListView != null) {
            resultListView.getItems().clear();
            if (points != null) {
                resultListView.getItems().addAll(points);
            }
        }
    }

    public void update() {
        display(Facade.getInstance().findAllPOI());
    }
}

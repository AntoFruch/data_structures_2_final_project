package antofruch.final_project.Contollers.MainView;

import antofruch.final_project.POI;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.function.Consumer;

public class POIListCell extends ListCell<POI> {

    private HBox root;
    private Label nameLabel;
    private Label coordsLabel;
    private Button moreButton;
    private POI currentPoi;

    public POIListCell(Consumer<POI> onEditAction) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../main-view/POI-cell.fxml"));
            root = loader.load();

            nameLabel = (Label) root.lookup("#nameLabel");
            coordsLabel = (Label) root.lookup("#coordsLabel");
            moreButton = (Button) root.lookup("#moreButton");

            moreButton.setOnAction(event -> {
                if (currentPoi == null) return;

                ContextMenu menu = new ContextMenu();
                MenuItem editItem = new MenuItem("Edit");
                editItem.setOnAction(e -> onEditAction.accept(currentPoi));
                menu.getItems().add(editItem);

                menu.show(moreButton, javafx.geometry.Side.BOTTOM, 0, 0);
            });

        } catch (IOException e) {
            throw new RuntimeException("Impossible to load POI-cell.fxml", e);
        }
    }

    @Override
    protected void updateItem(POI poi, boolean empty) {
        super.updateItem(poi, empty);
        this.currentPoi = poi;

        if (empty || poi == null) {
            setText(null);
            setGraphic(null);
        } else {
            setText(null);
            // Adaptez les getters selon votre classe POI (ex: getName(), getLat(), getLon())
            nameLabel.setText(poi.getName());
            coordsLabel.setText(String.format("%.5f, %.5f", poi.getLatitude(), poi.getLongitude()));
            setGraphic(root);
        }
    }
}

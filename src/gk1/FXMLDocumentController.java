/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * FXML Controller class
 *
 * @author Kazimierz
 */
public class FXMLDocumentController implements Initializable {

    ContextMenu contextMenu = new ContextMenu();

    @FXML
    private Canvas drawing;

    @FXML
    private void automaticToggle(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        Reaction reaction = GK1.model.toggleAutomaticRelations(newValue);
        if (reaction.isShouldRender()) {
            GK1.model.draw(GK1.viewer);
        }

        if (reaction.isShouldChangeCursor()) {
            drawing.setCursor(reaction.getDesiredCursor());
        } else {
            drawing.setCursor(Cursor.DEFAULT);
        }
    }

    @FXML
    private void mouseMoved(MouseEvent mouseEvent) {
        Reaction reaction = GK1.model.mouseMoved(mouseEvent);
        if (reaction.isShouldRender()) {
            GK1.model.draw(GK1.viewer);
        }

        if (reaction.isShouldChangeCursor()) {
            drawing.setCursor(reaction.getDesiredCursor());
        } else {
            drawing.setCursor(Cursor.DEFAULT);
        }
    }

    @FXML
    private void mousePressed(MouseEvent mouseEvent) {
        contextMenu.hide();
        if (MouseButton.SECONDARY == mouseEvent.getButton()) {
            // display the contextmenu!
            ArrayList<MenuItem> menuItems = GK1.model.buildContextMenu(mouseEvent);
            if (menuItems.isEmpty()) {
                return;
            }
            contextMenu.getItems().clear();
            contextMenu.getItems().addAll(menuItems);
            contextMenu.show(drawing, mouseEvent.getScreenX(), mouseEvent.getScreenY());
        } else {
            // just clicked!
            Reaction reaction = GK1.model.mousePressed(mouseEvent);
            GK1.model.draw(GK1.viewer);

        }
    }

    @FXML
    private void mouseReleased(MouseEvent mouseEvent) {
        Reaction reaction = GK1.model.mouseReleased(mouseEvent);
        if (reaction.isShouldRender()) {
            GK1.model.draw(GK1.viewer);
        }
    }

    @FXML
    private void mouseDrag(MouseEvent mouseEvent) {
        mouseMoved(mouseEvent);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        GK1.model = new Model();
        GK1.viewer = new Viewer(drawing, 600, 600);
        GK1.model.draw(GK1.viewer);
    }
}

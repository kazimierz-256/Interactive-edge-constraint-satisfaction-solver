/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1;

import java.util.ArrayList;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Kazimierz
 */
public interface Drawable {

    double getZ();

    void draw(Viewer viewer);

    Reaction mouseMoved(MouseEvent mouseEvent);

    Reaction mousePressed(MouseEvent mouseEvent);

    Reaction mouseReleased(MouseEvent mouseEvent);

//    Reaction reactKey(UserAction.ActionType action, KeyEvent event);
    ArrayList<MenuItem> buildContextMenu(MouseEvent event);

}

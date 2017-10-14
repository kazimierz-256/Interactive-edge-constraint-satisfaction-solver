/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1;

import javafx.scene.Cursor;

/**
 *
 * @author Kazimierz
 */
public class Reaction {

    private boolean shouldRender;
    private boolean shouldChangeCursor;
    private Cursor desiredCursor;

    Reaction(boolean shouldRender, boolean shouldChangeCursor, Cursor desiredCursor) {
        this.shouldRender = shouldRender;
        this.shouldChangeCursor = shouldChangeCursor;
        this.desiredCursor = desiredCursor;
    }

    public Reaction() {
        shouldRender = false;
        shouldChangeCursor = false;
    }

    public void Merge(Reaction anotherReaction) {
        if (anotherReaction.isShouldChangeCursor()) {
            // verify priority...
            this.desiredCursor = anotherReaction.getDesiredCursor();
        }
        this.shouldRender |= anotherReaction.isShouldRender();
        this.shouldChangeCursor |= anotherReaction.isShouldChangeCursor();
    }

    public boolean isShouldRender() {
        return shouldRender;
    }

    public void setShouldRender(boolean shouldRender) {
        this.shouldRender = shouldRender;
    }

    public void mergeShouldRender(boolean shouldRender) {
        this.shouldRender |= shouldRender;
    }

    public boolean isShouldChangeCursor() {
        return shouldChangeCursor;
    }

    public void setShouldChangeCursor(boolean shouldChangeCursor) {
        this.shouldChangeCursor = shouldChangeCursor;
    }

    public Cursor getDesiredCursor() {
        return desiredCursor;
    }

    public void setDesiredCursor(Cursor desiredCursor) {
        this.desiredCursor = desiredCursor;
        this.shouldChangeCursor = true;
    }
}

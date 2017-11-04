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

    public boolean shouldRender = false;
    public boolean shouldChangeCursor = false;
    public Cursor desiredCursor = null;
    public byte cursorPriority = 0;
    public boolean hasTouched = false;

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
        if (anotherReaction.shouldChangeCursor
                && this.cursorPriority <= anotherReaction.cursorPriority) {
            this.desiredCursor = anotherReaction.desiredCursor;
        }
        this.shouldRender |= anotherReaction.shouldRender;
        this.shouldChangeCursor |= anotherReaction.shouldChangeCursor;
    }

    public void setShouldRender(boolean shouldRender) {
        this.shouldRender = shouldRender;
    }

    public void mergeShouldRender(boolean shouldRender) {
        this.shouldRender |= shouldRender;
    }

    public void setDesiredCursor(Cursor desiredCursor) {
        this.desiredCursor = desiredCursor;
        this.shouldChangeCursor = true;

        switch (desiredCursor.toString()) {
            case "MOVE":

                this.cursorPriority = 10;
                break;
        }
    }
}

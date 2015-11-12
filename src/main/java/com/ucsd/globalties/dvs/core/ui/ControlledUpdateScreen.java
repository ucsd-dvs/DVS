package com.ucsd.globalties.dvs.core.ui;

/**
 * Extend the ControlledScreen interface to provide update functionality
 *
 * @author sabitn2
 */
public interface ControlledUpdateScreen extends ControlledScreen {

    /**
     * Method to update a screen
     * This is needed because all the screens are loaded and
     * initialized on startup so when the screen gets drawn,
     * it needs to be updated w/ necessary data
     */
    public void update() throws Exception;
}

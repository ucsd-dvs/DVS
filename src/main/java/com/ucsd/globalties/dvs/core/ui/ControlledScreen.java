package com.ucsd.globalties.dvs.core.ui;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Common interface for our screens
 *
 * @author Sabit
 */
public interface ControlledScreen {

    /**
     * Provide reference to navigation controller to be able to swap screens
     *
     * @param navigationController
     */
    void setScreenParent(NavigationController navigationController);

    /**
     * Provide reference to root view to reference backend controller
     * TODO improve this interaction
     *
     * @param rootViewController
     */
    void setRootView(RootViewController rootViewController);

    /**
     * Reset screen state to default configuration
     */
    void resetState();

    /**
     * Updates the screen to draw components
     * This is needed because all the views are loaded on startup
     * and screen specific components and behaviors need to be set
     * whenever we switch to it
     * @throws Exception
     */
    default void update() throws Exception {
        bindButtons();
    }

    /**
     * Called after after update() and screens have been
     * switched to resize the new screen to whatever current
     * size is the window is
     */
    void onLoad();

    void bindButtons() throws IOException;
}

package com.ucsd.globalties.dvs.core.ui;

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
    public void setScreenParent(NavigationController navigationController);

    /**
     * Provide reference to root view to reference backend controller
     * TODO improve this interaction
     *
     * @param rootViewController
     */
    public void setRootView(RootViewController rootViewController);

    /**
     * Reset screen state to default configuration
     */
    public void resetState();
}

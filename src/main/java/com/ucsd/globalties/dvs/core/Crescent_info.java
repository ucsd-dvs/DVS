package com.ucsd.globalties.dvs.core;

public class Crescent_info {

    //Crescent information
    private double crescentSize;
    private boolean crescentIsAtTop;
    private boolean crescentIsAtBot;

    Crescent_info(double crescentSize, boolean crescentIsAtTop, boolean crescentIsAtBot) {
        this.crescentSize = crescentSize;
        this.crescentIsAtTop = crescentIsAtTop;
        this.crescentIsAtBot = crescentIsAtBot;
    }

    public double getCrescentSize() {
        return crescentSize;
    }

    public void setCrescentSize(double crescentSize) {
        this.crescentSize = crescentSize;
    }

    public boolean isCrescentIsAtTop() {
        return crescentIsAtTop;
    }

    public void setCrescentIsAtTop(boolean crescentIsAtTop) {
        this.crescentIsAtTop = crescentIsAtTop;
    }

    public boolean isCrescentIsAtBot() {
        return crescentIsAtBot;
    }

    public void setCrescentIsAtBot(boolean crescentIsAtBot) {
        this.crescentIsAtBot = crescentIsAtBot;
    }
}

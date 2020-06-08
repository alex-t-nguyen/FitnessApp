package com.example.fitnessapp.menuCards;

public class menu_item {
    private int background;
    private String title1, title2, mainTitle;
    //private int card_icon;

    public menu_item()
    {

    }

    public menu_item(int background, String title1, String title2, /* int card_icon, */String mainTitle) {
        this.background = background;
        this.title1 = title1;
        this.title2 = title2;
        //this.card_icon = card_icon;
        this.mainTitle = mainTitle;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public String getTitle1() {
        return title1;
    }

    public void setTitle1(String title1) {
        this.title1 = title1;
    }

    public String getTitle2() {
        return title2;
    }

    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    /*
    public int getCard_icon() {
        return card_icon;
    }

    public void setCard_icon(int card_icon) {
        this.card_icon = card_icon;
    }
    */
    public String getMainTitle() {
        return mainTitle;
    }

    public void setMainTitle(String mainTitle) {
        this.mainTitle = mainTitle;
    }
}

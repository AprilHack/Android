package com.liu.game2.gameDomin;

import android.widget.ImageView;

/**
 * 踏板类
 */
public class Padel {
    private int length;
    private int x,y;
    private ImageView ivPadel;

    public Padel(int length, int x, int y, ImageView ivPadel) {
        this.length = length;
        this.x = x;
        this.y = y;
        this.ivPadel = ivPadel;
    }



    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public ImageView getIvPadel() {
        return ivPadel;
    }

    public void setIvPadel(ImageView ivPadel) {
        this.ivPadel = ivPadel;
    }
}

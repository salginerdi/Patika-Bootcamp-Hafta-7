package com.patikadev.Helper;

public class Item { // combo box'lara item atmak için oluşturduğumuz sınıf.
    private int key;
    private String value;

    public Item(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString(){
        return this.value;
    }
}

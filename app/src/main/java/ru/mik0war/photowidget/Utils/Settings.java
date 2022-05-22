package ru.mik0war.photowidget.Utils;

public enum Settings {
    DEFAULT_DIRECTORY("PhotoWidget"),
    DEFAULT_FILE_NAME;

    String value;

    Settings(String value) {
        this.value = value;
    }

    Settings(){
        this.value = "";
    }

    public String getValue() {
        return value;
    }
}

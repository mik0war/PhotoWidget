package ru.mik0war.photowidget.Utils;

public enum RequestCode {

    CAMERA_REQUEST(1888),
    CAMERA_PERMISSION_CODE(100);

    int code;

    RequestCode(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

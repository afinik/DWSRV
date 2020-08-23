package ru.finik;

import java.util.Date;

public class Log {
    String text;
    Log(String text){
        this.text = text;
        System.out.println("Logging: " + text);
    }
    public static void logd(String text) {
        System.out.println("Logging: " + new Date().toString() + " " + text);
    }
    public static void logd(String hcClient,String text) {
        System.out.println("Logging: " + new Date().toString() + " <<" + hcClient + ">> " + text);
    }


}

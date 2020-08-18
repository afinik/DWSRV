package ru.finik;

public class Log {
    String text;
    Log(String text){
        this.text = text;
        System.out.println("Logging: " + text);
    }
    public static void logd(String text) {
        System.out.println("Logging: " + text);
    }

}

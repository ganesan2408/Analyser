package com.yhh.analyser.model;

import java.util.ArrayList;

/**
 * Created by yuanhh1 on 2015/11/12.
 */
public class BatteryModel {
    private static final String[] BATTERY_STATUS = {"Unknown", "Charging",
            "DisCharging", "Not Charging", "Full"};

    private static final String[]  BATTERY_HEALTH = {"Unknown", "Good","OverHeat",
            "Dead", "Over Voltage", "Unspecified Failure", "Cold"};

    private static final String[] BATTERY_PLUGGED = {"AC charger","Usb connected","None"};

    private int status;
    private int health;
    private int level;
    private int voltage;
    private int temperature;
    private int plugged;
    private boolean present;
    private String technology;

    public int getVoltage() {
        return voltage;
    }

    public void setVoltage(int voltage) {
        this.voltage = voltage;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getPlugged() {
        return plugged;
    }

    public void setPlugged(int plugged) {
        this.plugged = plugged;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public ArrayList<String> getInfo(){
        ArrayList<String> info = new ArrayList<>();
        info.add(String.valueOf(level));
        info.add(String.valueOf(temperature / 10.0));
        info.add(String.valueOf(voltage / 1000.0));
        info.add(String.valueOf(status));
        info.add(String.valueOf(health));
        info.add(String.valueOf(present));
        info.add(String.valueOf(technology));
        return  info;
    }

    public String getShowInfo(){
        StringBuilder sb = new StringBuilder();

        sb.append("battery level:   ").append(level).append("%").append("\n");
        sb.append("temperature:   ").append(temperature / 10.0).append("℃ ").append("\n");
        sb.append("voltage:   ").append(voltage / 1000.0).append("V").append("\n");
        sb.append("status:   ").append(BATTERY_STATUS[status - 1]).append("\n");
        sb.append("health:   ").append(BATTERY_HEALTH[health - 1]).append("\n");

        if(present){
            sb.append("present:   " + "OK" + "\n");
        }else{
            sb.append("present:   " + "Failure" + "\n");
        }
        sb.append("technology:   ").append(technology).append("\n");

        if (plugged == 1) {
            sb.append("plugged:   ").append(BATTERY_PLUGGED[0]).append("\n");
        } else if (plugged == 2) {
            sb.append("plugged:   ").append(BATTERY_PLUGGED[1]).append("\n");
        } else {
            sb.append("plugged:   ").append(BATTERY_PLUGGED[2]).append("\n");
        }

        return  sb.toString();
    }

}

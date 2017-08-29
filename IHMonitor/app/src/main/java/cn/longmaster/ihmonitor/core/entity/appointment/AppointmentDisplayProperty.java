package cn.longmaster.ihmonitor.core.entity.appointment;

import cn.longmaster.ihmonitor.R;


public class AppointmentDisplayProperty {
    private String department = "";
    private String type = "";
    private int background = R.drawable.bg_appointment_video;
    private int textColor = R.color.color_95d009;

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
}

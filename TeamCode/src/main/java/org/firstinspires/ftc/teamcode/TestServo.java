package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class TestServo extends TestItem{
    private Servo servo;
    private double on, off;

    /**
     *
     * @param description name for the servo
     * @param on value when servo on (-1>1)
     * @param off valoe when servo off (-1>1)
     * @param servo pos or rot servo accepted
     */

    public TestServo(String description, double on, double off, Servo servo) {
        super(description);
        this.servo = servo;
        this.on = on;
        this.off = off;

    }

    @Override
    public void run(boolean on, Telemetry telemetry) {
        if (on){
            servo.setPosition(this.on);
        }
        else {
            servo.setPosition(this.off);
        }
        telemetry.addData("Servo Angle or Speed", servo.getPosition());
    }
}

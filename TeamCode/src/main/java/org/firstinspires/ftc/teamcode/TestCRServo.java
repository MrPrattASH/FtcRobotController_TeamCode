package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class TestCRServo extends TestItem{
    private CRServo servo;
    private double on, off;

    /**
     *
     * @param description name for the servo
     * @param on value when servo on (-1>1)
     * @param off valoe when servo off (-1>1)
     * @param servo pos or rot servo accepted
     */

    public TestCRServo(String description, double on, double off, CRServo servo) {
        super(description);
        this.servo = servo;
        this.on = on;
        this.off = off;

    }

    @Override
    public void run(boolean on, Telemetry telemetry) {
        if (on){
            servo.setPower(this.on);
        }
        else {
            servo.setPower(this.off);
        }
        telemetry.addData("Servo Angle or Speed", this.on);
    }
}

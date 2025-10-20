package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class ArcadeDriveTwoWheel {
    private DcMotor LeftMotor,  RightMotor;

    public void init(HardwareMap hwMap) {
        LeftMotor = hwMap.get(DcMotor.class, "left_motor");
        RightMotor = hwMap.get(DcMotor.class, "right_motor");

        LeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        RightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        LeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void drive(double throttle, double spin) {
        double leftPower = throttle + spin;
        double rightPower = throttle - spin;
        double largest = Math.max(Math.abs(leftPower), Math.abs(rightPower));
        if (largest > 1.0) {
            leftPower /= largest;
            rightPower /= largest;
        }

        LeftMotor.setPower(leftPower);
        RightMotor.setPower(rightPower);
    }
}

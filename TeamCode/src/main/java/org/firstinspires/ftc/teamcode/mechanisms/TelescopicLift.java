package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class TelescopicLift {
    private DcMotor liftMotor;
    private int targetPosition = 0;

    public void init(HardwareMap hwMap) {
        liftMotor = hwMap.get(DcMotor.class, "lift_motor");
        liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotor.setTargetPosition(targetPosition);
        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    }

    public void setTargetPosition(int ticks, double power) {
        targetPosition = ticks;
        liftMotor.setTargetPosition(targetPosition);
        liftMotor.setPower(Math.abs(power));
    }
    /*
    public void setPower(double power) {
        liftMotor.setPower(power);
    }
     */

    public boolean isBusy() {
        return liftMotor.isBusy();
    }

    public int getCurrentPosition() {
        return liftMotor.getCurrentPosition();
    }

    public int getTargetPosition() {
        return targetPosition;
    }
}

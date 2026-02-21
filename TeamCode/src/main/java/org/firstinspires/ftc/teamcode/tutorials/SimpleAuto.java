package org.firstinspires.ftc.teamcode.tutorials;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;


// this program assumes that you are starting directly touching the goal, and move backwards to shoot

@Autonomous(name = "Standard Mecanum Auto")
public class SimpleAuto extends OpMode {

    // Motor Declarations
    private DcMotor frontLeft, frontRight, backLeft, backRight;

    // Robot-specific constants (NEEDS TO BE ADJUSTED FOR YOUR ROBOT)
    private static final double TICKS_PER_REVOLUTION = 537.7; // 19.2:1 312 RPM Gobilda
    private static final double WHEEL_DIAMETER_MM = 96.0;
    private static final double TICKS_PER_MM = TICKS_PER_REVOLUTION / (WHEEL_DIAMETER_MM * Math.PI);

    // Correction factor for strafing, change this
    private static final double STRAFE_CORRECTION = 1.0;

    private enum AutoState {
        START,
        MOVE_BACKWARD,
        STRAFE_RIGHT,
        SHOOTING,
        STOP
    }

    private AutoState autoState;

    @Override
    public void init() {
        // Initialize hardware
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

        // Set motor directions (you may need to reverse the other side)
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        // Set the initial state
        autoState = AutoState.START;

        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {

        // main state machine logic
        switch (autoState) {
            case START:
                // Transition to the first movement
                // Initiate the backward movement
                move(-1000, 0.5);
                autoState = AutoState.MOVE_BACKWARD;
                break;

            case MOVE_BACKWARD:
                // Transition to the next state after the movement is complete
                if (!areMotorsBusy()) {
                    // Initiate the shooting sequence
                    autoState = AutoState.SHOOTING;
                }
                break;

            case SHOOTING:
                // initialize shooting logic here
                if (true) {
                    // when shooting complete, transition to strafe to the right
                    strafe(-500, 0.5);
                    autoState = AutoState.STRAFE_RIGHT;
                }
            case STRAFE_RIGHT:
                // Transition to the final state after the movement is complete
                if (!areMotorsBusy()) {
                    autoState = AutoState.STOP;
                }
                break;

            case STOP:
                // Stop all motors and end the OpMode
                setMotorPower(0);
                break;
        }

        telemetry.addData("Current State", autoState.toString());
    }

    // Method to move forward or backward
    public void move(double distanceMm, double power) {
        // zero all drive motors
        setMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        int targetTicks = (int) (distanceMm * TICKS_PER_MM);

        frontLeft.setTargetPosition(targetTicks);
        frontRight.setTargetPosition(targetTicks);
        backLeft.setTargetPosition(targetTicks);
        backRight.setTargetPosition(targetTicks);

        setMotorMode(DcMotor.RunMode.RUN_TO_POSITION);

        setMotorPower(power);
    }

    // Method to strafe left or right
    public void strafe(double distanceMm, double power) {
        // zero all motors
        setMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        int targetTicks = (int) (distanceMm * TICKS_PER_MM * STRAFE_CORRECTION);

        frontLeft.setTargetPosition(targetTicks);
        frontRight.setTargetPosition(-targetTicks);
        backLeft.setTargetPosition(-targetTicks);
        backRight.setTargetPosition(targetTicks);

        setMotorMode(DcMotor.RunMode.RUN_TO_POSITION);

        setMotorPower(power);
    }

    // Helper method to set the mode for all motors
    private void setMotorMode(DcMotor.RunMode mode) {
        frontLeft.setMode(mode);
        frontRight.setMode(mode);
        backLeft.setMode(mode);
        backRight.setMode(mode);
    }

    // Helper method to set the power for all motors
    private void setMotorPower(double power) {
        frontLeft.setPower(power);
        frontRight.setPower(power);
        backLeft.setPower(power);
        backRight.setPower(power);
    }

    // Helper method to check if any of the motors are busy
    private boolean areMotorsBusy() {
        return frontLeft.isBusy()
                || frontRight.isBusy()
                || backLeft.isBusy()
                || backRight.isBusy();
    }
}

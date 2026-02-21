package org.firstinspires.ftc.teamcode.tutorialsExamples;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

public class TurretMechanismAdvanced {
    private DcMotorEx turret;

    // --- PD Constants ---
    private double kP = 0.002;       // Proportional Gain
    private double kD = 0.00001;       // Derivative Gain
    private double lastError = 0;
    private final ElapsedTime timer = new ElapsedTime();
    private final double GOAL_X = 0; // add or subtract to this to add an offset
    private final double ANGLE_TOLERANCE = 0.2; // degrees
    private final double MAX_POWER = 0.5;
    private double power = 0;

    private final int MIN_ENCODER_TICKS = -1000;
    private final int MAX_ENCODER_TICKS = 1000;
    private final double RESET_POS_POWER = 0.4;      // Power for moving to zero
    private final double UNWIND_POWER = 0.8; // power to unwind when at max limits

    // Define the states for our state machine
    public enum TurretState {
        AUTOMATIC_CONTROL, // Normal PD control using AprilTag
        MANUAL_RESET,      // Moving to the zero position
        UNWIND         // Swinging to the opposite limit to "catch up"
    }

    private TurretState currentState = TurretState.AUTOMATIC_CONTROL;


    public void init(HardwareMap hwMap) {
        turret = hwMap.get(DcMotorEx.class, "turret");
        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        currentState = TurretState.AUTOMATIC_CONTROL;
    }

    public void start() {
        timer.reset();
    }

    public double getkP() {
        return kP;
    }

    public void setkP(double newKP) {
        kP = newKP;
    }

    public double getkD() {
        return kD;
    }

    public void setkD(double newKD) {
        kD = newKD;
    }

    public TurretState getState() { return currentState; }
    public void resetTurret() {
        // Only start a reset if we are in the main control state
        if (currentState == TurretState.AUTOMATIC_CONTROL) {
            turret.setTargetPosition(0);
            turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            turret.setPower(RESET_POS_POWER);

            // transition
            currentState = TurretState.MANUAL_RESET;
        }
    }

    public int getCurrentPosition() {
        return turret.getCurrentPosition();
    }

    public void update(AprilTagDetection id) {
        // stop derivative spikes
        double deltaTime = timer.seconds();
        timer.reset();

        // check if it's a valid reading passed in
        switch (currentState) {
            case AUTOMATIC_CONTROL:
                if (id == null) {
                    turret.setPower(0);
                    lastError = 0;
                    return; // Exit if no target
                }

                // --- PD Calculation ---
                double error = GOAL_X - id.ftcPose.bearing;
                double pTerm = error * kP;
                double dTerm = 0;
                if (deltaTime > 0) {
                    dTerm = ((error - lastError) / deltaTime) * kD;
                }


                if (Math.abs(error) < ANGLE_TOLERANCE) {
                    power = 0;
                } else {
                    power = Range.clip(pTerm + dTerm, -MAX_POWER, MAX_POWER);
                }

                // --------- "Unwind" Logic --------------
                int currentPosition = turret.getCurrentPosition();
                // Check if we're at the max limit and still want to go further
                if (currentPosition >= MAX_ENCODER_TICKS && power > 0) {
                    // Transition to UNWIND state to swing to the min limit
                    turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    turret.setTargetPosition(MIN_ENCODER_TICKS);
                    turret.setPower(UNWIND_POWER);

                    currentState = TurretState.UNWIND;
                }
                // Check if we're at the min limit and still want to go further
                else if (currentPosition <= MIN_ENCODER_TICKS && power < 0) {
                    // Transition to UNWIND state to swing to the max limit
                    turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    turret.setTargetPosition(MAX_ENCODER_TICKS);
                    turret.setPower(UNWIND_POWER);
                    currentState = TurretState.UNWIND;
                } else {
                    // If not at a limit, apply normal power
                    turret.setPower(power);
                }
                lastError = error;
                break;

            case MANUAL_RESET:
            case UNWIND:
                // Both of these states use RUN_TO_POSITION.
                // Their only job is to wait until the motor is no longer busy.
                if (!turret.isBusy()) {
                    // Once the move is complete, stop the motor and return to automatic control
                    turret.setPower(0);
                    turret.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                    currentState = TurretState.AUTOMATIC_CONTROL;
                    // Reset error to prevent a sudden derivative spike
                    lastError = 0;
                }
                // While the motor is moving, isBusy() is true, and we do nothing else.
                break;
        }
    }
}

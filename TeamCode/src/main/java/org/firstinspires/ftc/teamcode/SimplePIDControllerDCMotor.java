package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

public class SimplePIDControllerDCMotor extends OpMode {

    private DcMotorEx shooterMotor;
    private final double TICKS_PER_REV = 28; // Standard GoBilda ticks per revolution
    private final double GEAR_RATIO = 1.0;  // I'm using a bare motor
    private final double GOAL_SHOT_RPM = 2500;
    private final double MINIMUM_SHOT_RPM = 2100;

    // State Machine
    private enum shooterState {
        STOPPED,
        SPINNING_UP,
        READY
    }

    private shooterState currentState = shooterState.STOPPED;

    private boolean aButton, bButton, prevAButton,prevBButton;

    @Override
    public void init() {
        shooterMotor = hardwareMap.get(DcMotorEx.class, "flywheel_motor"); // Replace "shooter_motor" with your motor's name

        // Motor Initialization
        shooterMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        shooterMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shooterMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        /* Tune these to your needs, they're a bit of a best guess currently.
        * F- you want to tune this to a point where your motor can just not quite overcome the friction
        * required to get moving.
        * P- likely needs to be higher at lower motor torques

         */
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(0.6, 0, 0.005, 0.02);
        shooterMotor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
    }

    @Override
    public void loop() {
        aButton = gamepad1.a;
        bButton = gamepad1.b;

        double currentVelocityRPM = getMotorVelocityRPM();

        switch (currentState) {
            case STOPPED:
                shooterMotor.setPower(0);

                if (aButton && !prevAButton) {
                    shooterMotor.setVelocity(rpmToTicksPerSecond(GOAL_SHOT_RPM));
                    currentState = shooterState.SPINNING_UP;
                }
                break;

            case SPINNING_UP:
                // The motor is already commanded to the GOAL_RPM
                // wait for it to get up to the minimum speed

                if (currentVelocityRPM >= MINIMUM_SHOT_RPM) {
                    currentState = shooterState.READY;
                }
                break;

            case READY:
                // onboard PID should* keeps us near our goal
                if (currentVelocityRPM <= (MINIMUM_SHOT_RPM*.985)) { // check if we're still holding speed, while acounting for fluctuations
                    currentState = shooterState.SPINNING_UP;
                }
                break;
        }

        // always allow us to stop the motor controlled by the state machine.
        if (bButton && !prevBButton) {
            // Setting power to 0 is a more immediate way to stop than setting velocity to 0.
            shooterMotor.setPower(0);
            currentState = shooterState.STOPPED;
        }

        telemetry.addData("State", currentState.toString());
        telemetry.addData("Current Velocity (RPM)", currentVelocityRPM);

        // update button debounces
        prevAButton = aButton;
        prevBButton = bButton;
    }

    private double getMotorVelocityRPM() {
        // getVelocity() returns ticks per second.
        double ticksPerSecond = shooterMotor.getVelocity();
        return (ticksPerSecond / (TICKS_PER_REV * GEAR_RATIO)) * 60;
    }

    private double rpmToTicksPerSecond(double rpm) {
        return (rpm / 60) * (TICKS_PER_REV * GEAR_RATIO);
    }
}



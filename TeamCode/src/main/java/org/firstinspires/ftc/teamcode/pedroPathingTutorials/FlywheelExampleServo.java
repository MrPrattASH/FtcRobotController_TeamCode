package org.firstinspires.ftc.teamcode.pedroPathingTutorials;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class FlywheelExampleServo {
    private Servo gateServo;
    private CRServo flywheelServo;

    private ElapsedTime stateTimer = new ElapsedTime();
    private enum FlywheelState {
        IDLE,
        SPIN_UP,
        LAUNCH,
        RESET_GATE
    }

    private  FlywheelState flywheelState;

    private double GATE_CLOSED = 0;
    private double GATE_OPEN = 90;

    private double GATE_OPEN_TIME = 0.5;

    private double GATE_RESET_TIME = 0.2;

    private int shotsRemaining = 0;

    private double flywheelVelocity; // placeholder

    private double MIN_FLYWHEEL_RPM = 800;
    private double TARGET_FLYWHEEL_RPM = 1.0; // set to actual rpm

    private double FLYWHEEL_MAX_SPINUP_TIME = 2.5;


    public void init(HardwareMap hwMap) {
        gateServo = hwMap.get(Servo.class, "servo");
        flywheelServo = hwMap.get(CRServo.class, "cr_servo");

        flywheelState = FlywheelState.IDLE;

        flywheelVelocity = 0; // placeholder
        gateServo.setPosition(GATE_CLOSED);

    }

    public void update() {
        switch (flywheelState) {
            case IDLE:
                // check if we are still requesting shots or not?
                if (shotsRemaining > 0) {
                    // start spinup timer safety and transition to spinning up
                    stateTimer.reset();
                    flywheelState = FlywheelState.SPIN_UP;

                }
                break;
            case SPIN_UP:
                // spinup flywheel and close gate
                flywheelServo.setPower(TARGET_FLYWHEEL_RPM);
                gateServo.setPosition(GATE_CLOSED);

                // check current flywheel RPM OR elapsed time
                if (getFlywheelVelocity() > MIN_FLYWHEEL_RPM || stateTimer.seconds() > FLYWHEEL_MAX_SPINUP_TIME) {
                    // open the gate and fire a ball
                    gateServo.setPosition(GATE_OPEN);
                    stateTimer.reset();

                    flywheelState = FlywheelState.LAUNCH;
                }
                break;
            case LAUNCH:
                if (stateTimer.seconds() > GATE_OPEN_TIME) {
                    shotsRemaining--; //fired a shot
                    gateServo.setPosition(GATE_CLOSED);

                    stateTimer.reset();
                    flywheelState = FlywheelState.RESET_GATE;
                }
                break;
            case RESET_GATE:
                // give the gate time to actually close
                if (stateTimer.seconds() > GATE_RESET_TIME) {
                    // do we still need shots?
                    if (shotsRemaining > 0) {
                        stateTimer.reset();
                        flywheelState = FlywheelState.SPIN_UP;
                    }
                    else {
                        flywheelServo.setPower(0); // turn off flywheel
                        flywheelState = FlywheelState.IDLE;
                    }
                }
        }
    }

    public double getFlywheelVelocity() {
        return flywheelVelocity;
    }

    public void fireShots(int numberOfShots) {
        // only fire if the flywheel isn't actually firing
        if (flywheelState == FlywheelState.IDLE) {
            shotsRemaining = numberOfShots;
        }
    }

    public boolean isBusy() {
        // check if the state machine is busy
        return flywheelState != FlywheelState.IDLE;
    }
}

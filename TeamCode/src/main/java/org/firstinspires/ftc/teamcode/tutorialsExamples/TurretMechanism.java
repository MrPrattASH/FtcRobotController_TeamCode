package org.firstinspires.ftc.teamcode.tutorialsExamples;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

public class TurretMechanism {
    private DcMotorEx turret;

    // --- PD Constants ---
    private double kP = 0.0019;       // Proportional Gain
    private double kD = 0.00001;       // Derivative Gain
    private double lastError = 0;
    private final ElapsedTime timer = new ElapsedTime();
    private final double GOAL_X = 0; // add or subtract to this to add an offset
    private final double ANGLE_TOLERANCE = 0.2; // degrees
    private final double MAX_POWER = 0.8;
    private double power = 0;

    private final int MIN_ENCODER_TICKS = -1000;
    private final int MAX_ENCODER_TICKS = 1000;

    public void init(HardwareMap hwMap) {
        turret = hwMap.get(DcMotorEx.class, "turret");
        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
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

    public void update(AprilTagDetection id) {
        double deltaTime = timer.seconds();
        timer.reset();
        // check if it's a valid reading passed in
        if (id == null) {
            turret.setPower(0);
            lastError = 0;
            return;
        }

        double error = GOAL_X - id.ftcPose.bearing;

        double pTerm = error * kP;

        double dTerm = 0;
        if (deltaTime > 0) {
            dTerm = ((error - lastError) / deltaTime) * kD;
        }

        if (Math.abs(error) < ANGLE_TOLERANCE) {
            power = 0; // stop the motor
        } else {
            power = Range.clip(pTerm + dTerm, -MAX_POWER, MAX_POWER);
        }

        // --- Encoder Protection Logic ---
        int currentPosition = turret.getCurrentPosition();
        // If the turret is at or beyond the max limit and trying to move further, stop it.
        if (currentPosition >= MAX_ENCODER_TICKS && power > 0) {
            power = 0;
        }
        // If the turret is at or beyond the min limit and trying to move further, stop it.
        else if (currentPosition <= MIN_ENCODER_TICKS && power < 0) {
            power = 0;
        }

        turret.setPower(power);
        lastError = error;

    }
}

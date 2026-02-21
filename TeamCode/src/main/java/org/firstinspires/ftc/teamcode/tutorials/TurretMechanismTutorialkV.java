package org.firstinspires.ftc.teamcode.tutorials;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

public class TurretMechanismTutorialkV {
    private DcMotorEx turret;

    private double kP = 0.014;
    private double kD = 0.000001;
    private double kV = 0.005; // TUNE THIS!  this is your Feed forward
    private double goalX = 0;
    private double lastError = 0;
    private double angleTolerance = 0.2;
    private final double MAX_POWER = 0.6;
    private double power = 0;

    private final ElapsedTime timer = new ElapsedTime();

    public void init(HardwareMap hwMap) {
        turret = hwMap.get(DcMotorEx.class, "turret");
        turret.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void setkP(double newKP) {
        kP = newKP;
    }

    public double getkP() {
        return kP;
    }
    public void setkD(double newKD) {
        kD = newKD;
    }

    public double getkD() {
        return kD;
    }

    public void setkV(double newKV) {
        kV = newKV;
    }

    public double getkV() {
        return kV;
    }

    public void resetTimer(){
        timer.reset();
    }

    public void update(AprilTagDetection curID) {
        double deltaTime = timer.seconds();
        timer.reset();

        if (curID == null) {
            turret.setPower(0);
            lastError = 0;
            return;
        }

        // -------- start PD controller -----------

        double error = goalX - curID.ftcPose.bearing;
        double pTerm = error * kP;

        double dTerm = 0;
        if (deltaTime > 0) {
            dTerm = ((error - lastError) /deltaTime) * kD;
        }
        // this applies your feed forward to overcome static friction
        double kVTerm = Math.signum(error) * kV;

        if (Math.abs(error) < angleTolerance) {
            power = 0;
        } else {
            power = Range.clip(pTerm + dTerm + kVTerm, -MAX_POWER, MAX_POWER);
        }

        // safety encoder check OR magentic limit switch check, OR other safeties
        // If < safety threshold, power = 0, for example

        turret.setPower(power);
        lastError = error;
    }

}

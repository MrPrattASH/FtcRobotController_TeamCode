package org.firstinspires.ftc.teamcode.tutorials;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.mechanisms.AprilTagWebcam;
import org.firstinspires.ftc.teamcode.mechanisms.MecanumDrive;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

@TeleOp
public class TurretAutoAlignOpModeTutorial extends OpMode {
    private AprilTagWebcam aprilTagWebcam = new AprilTagWebcam();
    private TurretMechanismTutorial turret = new TurretMechanismTutorial();


    // ---------------- used to auto update P and D ---------------------
    double[] stepSizes = {0.1, 0.01, 0.001, 0.0001, 0.00001, 0.000001, 0.0000001};
    // Index to select the current step size from the array.
    int stepIndex = 4;


    @Override
    public void init() {
        aprilTagWebcam.init(hardwareMap, telemetry);
        turret.init(hardwareMap);
        telemetry.addLine("Initialized all mechanisms");
    }

    public void start() {
        turret.resetTimer();
    }

    @Override
    public void loop() {

        // vision logic
        aprilTagWebcam.update();
        AprilTagDetection id20 = aprilTagWebcam.getTagBySpecificId(20);

        turret.update(id20);

        // update P and D on the fly
        // 'B' button cycles through the different step sizes for tuning precision.
        if (gamepad1.bWasPressed()) {
            stepIndex = (stepIndex + 1) % stepSizes.length; // Modulo wraps the index back to 0.
        }

        // D-pad left/right adjusts the P gain.
        if (gamepad1.dpadLeftWasPressed()) {
            turret.setkP(turret.getkP() - stepSizes[stepIndex]);
        }
        if (gamepad1.dpadRightWasPressed()) {
            turret.setkP(turret.getkP() + stepSizes[stepIndex]);
        }

        // D-pad up/down adjusts the D gain.
        if (gamepad1.dpadUpWasPressed()) {
            turret.setkD(turret.getkD() + stepSizes[stepIndex]);
        }
        if (gamepad1.dpadDownWasPressed()) {
            turret.setkD(turret.getkD() - stepSizes[stepIndex]);
        }

        if (id20 != null) {
            telemetry.addData("cur ID", aprilTagWebcam);
        } else {
            telemetry.addLine("No Tag Detected. Stopping Turret Motor");
        }
        telemetry.addLine("-----------------------------");
        telemetry.addData("Tuning P", "%.7f (D-Pad L/R)", turret.getkP());
        telemetry.addData("Tuning D", "%.7f (D-Pad U/D)", turret.getkD());
        telemetry.addData("Step Size", "%.7f (B Button)", stepSizes[stepIndex]);

    }
}

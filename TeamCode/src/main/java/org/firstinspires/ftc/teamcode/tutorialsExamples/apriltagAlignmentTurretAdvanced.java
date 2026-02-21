package org.firstinspires.ftc.teamcode.tutorialsExamples;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.AprilTagWebcam;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

@TeleOp
public class apriltagAlignmentTurretAdvanced extends OpMode {

    // --- Hardware ---
    private final AprilTagWebcam aprilTagWebcam = new AprilTagWebcam();

    private final TurretMechanismAdvanced turret = new TurretMechanismAdvanced();

    double forward, rotate, strafe;

    // ---------------- used to auto update P and D ---------------------
    double[] stepSizes = {0.1, 0.001, 0.0001, 0.00001};
    // Index to select the current step size from the array.
    int stepIndex = 2;

    @Override
    public void init() {
        // Initialize webcam
        aprilTagWebcam.init(hardwareMap, telemetry);
        // Initialize Drive Motors
        turret.init(hardwareMap);

        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void start() {
        turret.start();
    }

    @Override
    public void loop() {
        // ------------- april tag webcam  ---------------
        aprilTagWebcam.update();
        AprilTagDetection id20 = aprilTagWebcam.getTagBySpecificId(20); // we care about id20

        // ----------- turret logic ------------------
        turret.update(id20);

        if (gamepad1.y) {
            turret.resetTurret();
        }

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

        // ------------ telemetry -------------
        if (id20 != null) {
            aprilTagWebcam.displayDetectionTelemetry(id20);
        } else {
            telemetry.addLine("No Tag Found");
        }
        telemetry.addLine("-----------------------------");
        telemetry.addData("Tuning P", "%.5f (D-Pad L/R)", turret.getkP());
        telemetry.addData("Tuning D", "%.5f (D-Pad U/D)", turret.getkD());
        telemetry.addData("Step Size", "%.5f (B Button)", stepSizes[stepIndex]);
        telemetry.addLine("-----------------------------");
        telemetry.addData("Turret State", turret.getState());
        telemetry.addData("Turret Encoder Pos", turret.getCurrentPosition());
    }

}
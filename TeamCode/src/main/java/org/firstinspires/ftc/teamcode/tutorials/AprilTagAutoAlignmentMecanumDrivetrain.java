package org.firstinspires.ftc.teamcode.tutorials;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.mechanisms.AprilTagWebcam;
import org.firstinspires.ftc.teamcode.mechanisms.MecanumDrive;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

@TeleOp
public class AprilTagAutoAlignmentMecanumDrivetrain extends OpMode {
    private final AprilTagWebcam aprilTagWebcam = new AprilTagWebcam();
    private final MecanumDrive drive = new MecanumDrive();

    // --------------------- PD controller ----------------------
    double kP = 0.019;
    double error = 0;
    double lastError = 0;
    double goalX = 0; // offset here
    double angleTolerance = 0.4;
    double kD = 0.0001;
    double curTime = 0;
    double lastTime = 0;

    // ------------------ driving setup ------------------------
    double forward, strafe, rotate;

    // -------------------- controller based PD tuning -----------------------

    double[] stepSizes = {0.1, 0.001, 0.0001};
    int stepIndex = 1;

    @Override
    public void init() {
        aprilTagWebcam.init(hardwareMap, telemetry);
        drive.init(hardwareMap, false);

        telemetry.addLine("Initialized");
    }

    public void start() {
        resetRuntime();
        curTime = getRuntime();
    }

    @Override
    public void loop() {
        // ------------- get mecanum drive inputs ---------------
        forward = -gamepad1.left_stick_y;
        strafe = gamepad1.left_stick_x;
        rotate = gamepad1.right_stick_x;

        // ---------------- get april tag info -----------------
        aprilTagWebcam.update();
        AprilTagDetection id20 = aprilTagWebcam.getTagBySpecificId(20);

        // --------------- auto align rotation logic ----------------

        if (gamepad1.left_trigger > 0.3) {
            if (id20 != null) {
                error = goalX - id20.ftcPose.bearing; // tx

                if (Math.abs(error) < angleTolerance) {
                    rotate = 0;
                } else {
                    double pTerm = error * kP;

                    curTime = getRuntime();
                    double dT = curTime - lastTime;
                    double dTerm = ((error - lastError) / dT) * kD;

                    rotate = Range.clip(pTerm + dTerm, -0.4, 0.4);

                    lastError = error;
                    lastTime = curTime;
                }
            } else {
                lastTime = getRuntime();
                lastError = 0;
            }
        } else {
            lastError = 0;
            lastTime = getRuntime();
        }

        // drive our motors!
        drive.drive(forward, strafe, rotate);

        // update P and D on the fly
        // 'B' button cycles through the different step sizes for tuning precision.
        if (gamepad1.bWasPressed()) {
            stepIndex = (stepIndex + 1) % stepSizes.length; // Modulo wraps the index back to 0.
        }

        // D-pad left/right adjusts the P gain.
        if (gamepad1.dpadLeftWasPressed()) {
            kP -= stepSizes[stepIndex];
        }
        if (gamepad1.dpadRightWasPressed()) {
            kP += stepSizes[stepIndex];
        }

        // D-pad up/down adjusts the D gain.
        if (gamepad1.dpadUpWasPressed()) {
            kD += stepSizes[stepIndex];
        }
        if (gamepad1.dpadDownWasPressed()) {
            kD -= stepSizes[stepIndex];
        }

        // ------------ telemetry -------------
        if (id20 != null) {
            if (gamepad1.left_trigger > 0.3) {
                telemetry.addLine("AUTO ALIGN");
            }
            aprilTagWebcam.displayDetectionTelemetry(id20);
            telemetry.addData("Error", error);
        } else {
            telemetry.addLine("MANUAL Rotate Mode");
        }
        telemetry.addLine("-----------------------------");
        telemetry.addData("Tuning P", "%.4f (D-Pad L/R)", kP);
        telemetry.addData("Tuning D", "%.4f (D-Pad U/D)", kD);
        telemetry.addData("Step Size", "%.4f (B Button)", stepSizes[stepIndex]);

    }
}

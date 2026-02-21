package org.firstinspires.ftc.teamcode.tutorialsExamples;

import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.mechanisms.AprilTagWebcam;
import org.firstinspires.ftc.teamcode.mechanisms.MecanumDrive;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

import java.util.List;

import kotlinx.coroutines.debug.internal.HashedWeakRef;

@TeleOp
public class apriltagAlignmentMecanum extends OpMode {

    // --- Hardware ---
    private final MecanumDrive drive = new MecanumDrive();
    private final AprilTagWebcam aprilTagWebcam = new AprilTagWebcam();

    // --- PD Constants ---
    double kP = 0.019;       // Proportional Gain
    double kD = 0.0001;       // Derivative Gain

    double lastError = 0;
    double error = 0;
    double goalX = 0; // add or subtract to this to add an offset
    double curTime = 0;
    double lastTime = 0; // we'll use this to save this as a

    final double angleTolerance = 0.2; // degrees

    double forward, rotate, strafe;

    // ---------------- used to auto update P and D ---------------------
    double[] stepSizes = {0.1, 0.01, 0.001, 0.0001};
    // Index to select the current step size from the array.
    int stepIndex = 1;

    @Override
    public void init() {
        // Initialize webcam
        aprilTagWebcam.init(hardwareMap, telemetry);
        // Initialize Drive Motors
        drive.init(hardwareMap, false);

        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void start() {
        resetRuntime(); // begin our timer anew
        lastTime = getRuntime();
    }

    @Override
    public void loop() {
        // ------------- drive inputs ---------------------
        forward  = -gamepad1.left_stick_y;
        strafe =  gamepad1.left_stick_x;
        rotate =  gamepad1.right_stick_x;



        // ------------- april tag webcam  ---------------
        aprilTagWebcam.update();
        AprilTagDetection id20 = aprilTagWebcam.getTagBySpecificId(20); // we care about id20


        // ---------------- auto alignment logic -------------------
       // left trigger is our auto align request
        if (gamepad1.left_trigger > 0.5) {
            if (id20 != null) {
                //  we have a valid target from the webcame

                // error is where we WANT to be minus where we ARE
                error = goalX - id20.ftcPose.bearing;


                if (Math.abs(error) < angleTolerance) {
                    rotate = 0; // if we are within our tolerance, don't rotate
                } else {
                    // Proportional control calculations
                    double pTerm = error * kP;

                    // Derivative control calculations
                    curTime = getRuntime();
                    // deltaTime is the "change" in time from cur reading to last
                    double deltaTime = curTime - lastTime;
                    // standard derivative calculation
                    double dTerm = ((error - lastError) / deltaTime) * kD;

                    // change rotate value and "clamp" to maximum 60% rotate speed
                    rotate = Range.clip(pTerm + dTerm, -0.4, 0.4);

                    // setup logic for the next derivative calculation
                    lastError = error;
                    lastTime = curTime;
                }

            } else {
                lastError = 0;
                lastTime = getRuntime();
                rotate =  gamepad1.right_stick_x;
                // if we don't do any auto rotation on the webcam, IE, we have NO valid target
                // we simply use the rotate value from the gamepad originally from the top of the loop
                ;
            }
        } else {
            lastError = 0;
            lastTime = getRuntime();
        }

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

        // command
        drive.drive(forward, strafe, rotate);

        // ------------ telemetry -------------
        if (id20 != null) {
            telemetry.addLine("AUTO ALIGN");
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
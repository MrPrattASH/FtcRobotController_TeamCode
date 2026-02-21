package org.firstinspires.ftc.teamcode.cl;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad1;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

import java.util.List;

public class IMU3ACalibration extends OpMode {

    // --- Class-level variables ---
    private IMU imu;
    private Limelight3A limelight;
    private DcMotor turretMotor; // Assuming you have an encoder on the turret

    // This variable will store the calculated drift of the IMU.
    private double imuHeadingOffset = 0.0;

    // You must calibrate this value for your specific turret setup.
// It's the number of encoder ticks for one degree of turret rotation.
    private double TURRET_TICKS_PER_DEGREE = 14.22; // EXAMPLE VALUE: Please calibrate this!

// --- Inside your OpMode loop or a dedicated method ---


    @Override
    public void init() {

    }

    public void recalibrateImuWithAprilTag() {
        LLResult result = limelight.getLatestResult();
        if (result == null || !result.isValid()) {
            telemetry.addData("Recalibration", "No valid Limelight result.");
            return;
        }

        List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();
        if (fiducials.isEmpty()) {
            telemetry.addData("Recalibration", "No AprilTags detected.");
            return;
        }

        // We can use the first detected fiducial for this.
        // In a more advanced setup, you might average the poses from multiple visible tags.
        LLResultTypes.FiducialResult fiducial = fiducials.get(0);

        // 1. Get the TURRET'S orientation from the Limelight
        // getRobotPoseFieldSpace() returns a Pose3D object.
        Pose3D robotPoseFieldSpace = fiducial.getRobotPoseFieldSpace();
        double turretFieldYaw = robotPoseFieldSpace.getOrientation().getYaw(); // Assuming Z is the axis of rotation (yaw)

        // 2. Get the turret's current angle relative to the chassis
        double turretChassisAngle = turretMotor.getCurrentPosition() / TURRET_TICKS_PER_DEGREE;

        // 3. Calculate the CHASSIS'S "ground truth" orientation
        double chassisFieldYaw = turretFieldYaw - turretChassisAngle;

        // 4. Get the IMU's current (potentially drifted) heading
        double imuYaw = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);

        // 5. Calculate and store the offset
        // This offset is the difference between the true heading and the IMU's reading.
        imuHeadingOffset = chassisFieldYaw - imuYaw;

        telemetry.addData("Recalibration", "Success!");
        telemetry.addData("IMU Offset", imuHeadingOffset);
    }

    /**
     * Returns the IMU's yaw, corrected by the calculated offset.
     * Use this method for all your robot's orientation needs (e.g., field-centric drive).
     */
    public double getCorrectedChassisYaw() {
        double rawImuYaw = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
        return rawImuYaw + imuHeadingOffset;
    }

    // --- In your teleop loop ---
    @Override
    public void loop() {
        // ... your driver control code for drivetrain, turret, etc. ...

        // Trigger recalibration with a button press
        if (gamepad1.a) {
            recalibrateImuWithAprilTag();
        }

        // Example of using the corrected heading to aim the turret
        double correctedRobotHeading = getCorrectedChassisYaw();
        // Now use this `correctedRobotHeading` in your turret aiming logic.

        telemetry.addData("Corrected Heading", correctedRobotHeading);
        telemetry.update();
    }
}

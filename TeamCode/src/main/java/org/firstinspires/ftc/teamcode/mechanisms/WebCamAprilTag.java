package org.firstinspires.ftc.teamcode.mechanisms;

import android.hardware.HardwareBuffer;
import android.util.Size;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;
import java.util.List;

public class WebCamAprilTag {

    private AprilTagProcessor aprilTagProcessor;
    private VisionPortal visionPortal;
    private List<AprilTagDetection> detectedTags = new ArrayList<>(); // Stores detections
    private Telemetry telemetry;

    public void init(HardwareMap hwMap, Telemetry telemetry) {
        // pull in nice telemetry data from main DS
        this.telemetry = telemetry;
        // create april tag processor

        aprilTagProcessor = new AprilTagProcessor.Builder()
                .setDrawAxes(true)
                .setDrawTagID(true)
                .setDrawTagOutline(true)
                .setDrawCubeProjection(true)
                .setOutputUnits(DistanceUnit.MM, AngleUnit.DEGREES)
                .build();

        //create vision portal
        VisionPortal.Builder builder = new VisionPortal.Builder()
        .setCamera(hwMap.get(WebcamName.class, "Webcam 1"))
        .setCameraResolution(new Size(640,480))
        .addProcessor(aprilTagProcessor);
//
        // build vision portal
        visionPortal = builder.build();
    }

    public void update() {
        detectedTags = aprilTagProcessor.getDetections();
    }

    private void displayDetectionTelemetry(AprilTagDetection detection) {
        if (detection == null) {return;} // empty tags
        if (detection.metadata != null) {
            telemetry.addLine(String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name));
            telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (mm)", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
            telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)", detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw));
            telemetry.addLine(String.format("RBE %6.1f %6.1f %6.1f  (mm, deg, deg)", detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation));
        } else {
            telemetry.addLine(String.format("\n==== (ID %d) Unknown", detection.id));
            telemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y));
        }
    }

    public List<AprilTagDetection> getDetectedTags(boolean displayTelemetry) {
        if (displayTelemetry) {

            
            for (AprilTagDetection detection : detectedTags) {
                displayDetectionTelemetry(detection);
            }
        }
        return detectedTags;
    }

    public AprilTagDetection getDetectionByID(int id, boolean displayTelemetry) {
        for (AprilTagDetection detection : detectedTags) {
            if (detection.id == id) {
                if (displayTelemetry) {
                    displayDetectionTelemetry(detection);
                }
                return detection;
            }
        }
        return null;
    }

    public void stop() {
        if(visionPortal != null) {
            visionPortal.close();
        }
    }
}

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.WebCamAprilTag;

@TeleOp(name = "AprilTagsClass", group = "AprilTag")
public class AprilTagWebcamClassExample extends OpMode {
    WebCamAprilTag aprilTag = new WebCamAprilTag();

    public void init() {
        aprilTag.init(hardwareMap, telemetry);
    }

    public void loop() {
        aprilTag.update();
        aprilTag.getDetectionByID(20, true);
    }
}

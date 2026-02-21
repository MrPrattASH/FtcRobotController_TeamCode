package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

@TeleOp(name = "Teleop", group = "Standard Mecanum Driving")
public class TeamOrangeTeleop extends OpMode {
    org.firstinspires.ftc.teamcode.mechanisms.MecanumDrive mecanumDrive = new org.firstinspires.ftc.teamcode.mechanisms.MecanumDrive();

    boolean gobilda = false;

    @Override
    public void init() {
        mecanumDrive.init(hardwareMap, gobilda);
        telemetry.addData("Robot", "Initialized");
            }

    @Override
    public void loop() {
        //update our pinpoint
        if (gobilda) {
            mecanumDrive.updatePinpoint();
            Pose2D pose2D = mecanumDrive.getPosition();
        }

        mecanumDrive.driveFieldRelative(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);

    }
}

/*
1. Create a separate class in a mechanisms package
2. The goal of this class is to simply return a variety of telemetry messages.
3. Create an opMode that uses this class, and can print out the variety of telementy messages


 */

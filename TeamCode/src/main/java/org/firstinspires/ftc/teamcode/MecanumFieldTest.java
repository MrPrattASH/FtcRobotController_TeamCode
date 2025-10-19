package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.mechanisms.MecanumDrive;

@TeleOp
public class MecanumFieldTest extends OpMode {
    MecanumDrive drive = new MecanumDrive();



    @Override
    public void init() {
        drive.init(hardwareMap, true);
        telemetry.addData("Robot", "Initialized");
    }

    @Override
    public void loop() {
        //update our pinpoint
        drive.updatePinpoint();
        Pose2D pose2D = drive.getPosition();

        drive.driveFieldRelative(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);

        telemetry.addData("X", pose2D.getX(DistanceUnit.MM));
        telemetry.addData("Y", pose2D.getY(DistanceUnit.MM));
        telemetry.addData("Heading", pose2D.getHeading(AngleUnit.DEGREES));
    }
}

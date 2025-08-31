package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@Disabled
@TeleOp
public class UseRobotLocationOpMode extends OpMode {
    RobotLocationRadians robotLocationRadians = new RobotLocationRadians(0);

    @Override
    public void init() {
        robotLocationRadians.setAngle(0);
    }

    @Override
    public void loop() {
        if (gamepad1.a) {
            robotLocationRadians.turn(0.1);
        }
        else if (gamepad1.b) {
            robotLocationRadians.turn(-0.1);
        }
        if (gamepad1.dpad_left) {
            robotLocationRadians.changeX(-0.1);
        }
        else if (gamepad1.dpad_right) {
            robotLocationRadians.changeX(0.1);
        }
        if (gamepad1.dpad_up) {
            robotLocationRadians.changeY(0.1);
        }
        else if (gamepad1.dpad_down) {
            robotLocationRadians.changeY(-0.1);
        }
        telemetry.addData("Location", robotLocationRadians);
        telemetry.addData("Heading", robotLocationRadians.getHeading());
        telemetry.addData("Angle", robotLocationRadians.getAngle());
        telemetry.addData("X", robotLocationRadians.getX());
        telemetry.addData("Y", robotLocationRadians.getY());
    }
}

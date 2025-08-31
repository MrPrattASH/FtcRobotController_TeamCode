package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@Disabled
@TeleOp
public class GamePadTest extends OpMode {
    @Override
    public void init() {
        boolean turbo = false;
    }

    @Override
    public void loop() {
        boolean turbo;
        double multiplier;

        if(gamepad1.a) {
            turbo = true;
        }
        else {
            turbo = false;
        }


        if(gamepad1.left_stick_y < 0){
            telemetry.addData("left stick", "is negative");
        }
        if(turbo) {
            multiplier = 1.0;
        }
        else {
            multiplier = 0.5;
        }
        telemetry.addData("left stick y", gamepad1.left_stick_y * multiplier);

        }
    }


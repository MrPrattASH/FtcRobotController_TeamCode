package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

public class GamePadVariableRumble extends OpMode {

    @Override
    public void init() {

    }

    @Override
    public void loop() {
        telemetry.addLine("Press left trigger for right rumble, and vice versa");
        gamepad1.rumble(gamepad1.left_trigger, gamepad1.right_trigger,
                Gamepad.RUMBLE_DURATION_CONTINUOUS);
    }
}

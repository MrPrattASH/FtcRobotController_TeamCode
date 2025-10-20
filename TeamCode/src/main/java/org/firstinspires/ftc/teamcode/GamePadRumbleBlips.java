package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class GamePadRumbleBlips extends OpMode {
    boolean aButton, wasA;

    @Override
    public void init() {

    }

    @Override
    public void loop() {
        aButton = gamepad1.a;
        if (aButton && !wasA) {
            gamepad1.rumbleBlips(3);
        }

        wasA = aButton;
    }
}

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class GamePadRumbleFeedbackEndGame extends OpMode {
    boolean isEndGame;
    double endGameStartTime;

    @Override
    public void init() {
        isEndGame = false;
    }

    @Override
    public void start() {
        endGameStartTime = getRuntime() + 90;

    }

    @Override
    public void loop() {
        if (getRuntime() > endGameStartTime && !isEndGame) {
            isEndGame = true;
            gamepad1.rumbleBlips(3);
        }
    }
}

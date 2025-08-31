package org.firstinspires.ftc.teamcode;

import android.speech.tts.TextToSpeech;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.mechanisms.TestBench;

@Autonomous
public class AutoTime extends OpMode {
    TestBench testBench = new TestBench();
    enum State {
        QUARTER,
        HALF,
        THREE_QUARTER,
        FULL,
        RUNNING,
        DONE
    }
    State state = State.RUNNING;
    double lastTime;
    double distance;


    @Override
    public void init() {
        testBench.init(hardwareMap);
    }

    @Override
    public void start() {
        state = State.RUNNING;
        resetRuntime();
        lastTime = getRuntime();
        testBench.setServoPos(0.5);
    }

    @Override
    public void loop() {
        distance = testBench.getDistance(DistanceUnit.CM);
        telemetry.addData("State", state);
        telemetry.addData("Distance", distance);

        switch (state) {
            case QUARTER:
                testBench.setMotorSpeed(0.25);
                if (getRuntime() >= 0.25) {
                    state = State.HALF;
                    lastTime = getRuntime();
                }
                break;
            case HALF:
                testBench.setMotorSpeed(0.5);
                if (getRuntime() >= lastTime + 0.25) {
                    state = State.THREE_QUARTER;
                    lastTime = getRuntime();
                }
                break;
            case THREE_QUARTER:
                testBench.setMotorSpeed(0.75);
                if (getRuntime() >= lastTime + 0.25) {
                    state = State.FULL;
                    lastTime = getRuntime();
                }
                break;
            case FULL:
                testBench.setMotorSpeed(1.0);
                if (testBench.isTouchSensorPressed()) {
                    state = State.DONE;
                    lastTime = getRuntime();
                }
                break;
            case RUNNING:
                testBench.setMotorSpeed(0.5);

                if (distance < 10 || getRuntime() >= 5.0) {
                    state = State.DONE;
                    testBench.setServoPos(1.0);
                }
                break;
            default:
                testBench.setMotorSpeed(0.0);
                telemetry.addData("Auto", "Finished");

        }
    }
}

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.mechanisms.TestBench;

import java.util.ArrayList;
@Disabled
@TeleOp
public class TestWiring extends OpMode {
    TestBench testBench = new TestBench();
    ArrayList<TestItem> tests;
    boolean dpadUp, dpadDown, wasUp, wasDown, runTest;
    int testNum;

    @Override
    public void init() {
        testBench.init(hardwareMap);
        tests = testBench.getTests();
    }

    @Override
    public void loop() {
        dpadUp = gamepad1.dpad_up;
        dpadDown = gamepad1.dpad_down;
        runTest = gamepad1.a;

        //  move up list
        if (dpadUp && !wasUp) {
            testNum --;
            if (testNum < 0) {
                testNum = tests.size() - 1;
            }
        }

        // move the list down
        if (dpadDown && !wasDown) {
            testNum ++;
            if (testNum >= tests.size()) {
                testNum = 0;
            }
        }

        telemetry.addLine("Press up/down on the Dpad to cycle test");
        telemetry.addLine("Press A to run test");
        TestItem currTest = tests.get(testNum);
        telemetry.addData("Testing", currTest.getDescription());

        // based on a button press, run the test
        currTest.run(runTest, telemetry);

        // reassign prev states to current states at end of loop
        wasUp = dpadUp;
        wasDown = dpadDown;
    }
}

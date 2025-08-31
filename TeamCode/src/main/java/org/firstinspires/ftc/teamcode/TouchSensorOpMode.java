package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.mechanisms.TestBench;

@Disabled
@TeleOp
public class TouchSensorOpMode extends OpMode {
    TestBench testBench = new TestBench();
    @Override
    public void init () {
        testBench.init(hardwareMap);
    }

    @Override
    public void loop() {
        telemetry.addData("Touch Sensor Pressed", testBench.isTouchSensorPressed());
        telemetry.addData("Touch Sensor Released", testBench.isTouchSensorReleased());
    }
}

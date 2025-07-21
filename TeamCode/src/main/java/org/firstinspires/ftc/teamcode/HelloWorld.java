package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@Disabled
@Autonomous
public class HelloWorld extends OpMode {

    @Override
    public void init() {
        telemetry.addData("Hello", "Coach Pratt"); // My First Comment
    }

    @Override
    public void loop() {
    }

    /*
    1. Hello: World, change the telemetary data to display "Hello: Your name"
    2. Run this code in the Autonomous section of your DS
     */
}

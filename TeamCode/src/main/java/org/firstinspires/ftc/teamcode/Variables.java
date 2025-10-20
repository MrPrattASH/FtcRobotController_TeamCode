package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@Disabled
@TeleOp
public class Variables extends OpMode {
    public void init() {
        int teamNum = 23014;
        double range = 42.57;
        boolean started = false;
        String whoIsIt = "Brogan M Pratt";

        telemetry.addData("Team Num", teamNum);
        telemetry.addData("range", range);
        telemetry.addData("robot start", started);
        telemetry.addData("Name:", whoIsIt);
    }

    public void loop() {

    }
}

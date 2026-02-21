package org.firstinspires.ftc.teamcode.cl;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.List;

@Disabled

@Autonomous
public class LimeLightTest extends OpMode {
    private Limelight3A limelight3A;

    @Override
    public void init() {
        limelight3A = hardwareMap.get(Limelight3A.class, "limelight");
        limelight3A.pipelineSwitch(0); // 0 is blue and 1 is red.

    }

    @Override
    public void start() {
        limelight3A.start();
    }

    @Override
    public void loop() {
        // get IMU yaw

        LLResult llResult = limelight3A.getLatestResult();
        if (llResult != null & llResult.isValid()) {
            //telemetry.addData("Target X offset", llResult.getTx());
            //telemetry.addData("Target Y offset", llResult.getTy());
            //telemetry.addData("Target Area offset", llResult.getTa());


            List<LLResultTypes.FiducialResult> fiducials = llResult.getFiducialResults();
            for (LLResultTypes.FiducialResult fiducial : fiducials) {
                int aprilTagID = fiducial.getFiducialId();
                telemetry.addData("ID", aprilTagID);
            }
        }
    }
}

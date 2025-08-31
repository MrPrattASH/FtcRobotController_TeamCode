package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import javax.crypto.spec.OAEPParameterSpec;

@Autonomous
public class SimpleLimeLight extends OpMode {
    Limelight3A limelight3A;

    @Override
    public void init() {
        limelight3A = hardwareMap.get(Limelight3A.class, "limelight");

    }

    @Override
    public void start() {
        limelight3A.pipelineSwitch(0);
        limelight3A.start();
    }

    @Override
    public void loop() {
        LLResult llResult = limelight3A.getLatestResult();
        telemetry.addData("Pipeline", "Index: %d, Type: %s",
                llResult.getPipelineIndex(), llResult.getPipelineType());
        if (llResult != null && llResult.isValid()) {
            telemetry.addData("Tx", llResult.getTx());
            telemetry.addData("Ty", llResult.getTy());
            telemetry.addData("Ta", llResult.getTa());
        }
        else {
            telemetry.addLine("No Valid Results Found");
        }
    }
}

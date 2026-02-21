package org.firstinspires.ftc.teamcode.pedroPathingTutorials;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "Auto Flywheel (Iterative)")
public class PedroStateActions extends OpMode {

    FlywheelExampleServo shooter = new FlywheelExampleServo();
    ElapsedTime autoTimer = new ElapsedTime();

    // 1. Define the Steps of your Auto
    private enum AutoState {
        SHOOTING,       // Waiting for shots to finish
        DRIVING_AWAY,   // Moving to park
        DONE            // Auto finished
    }

    private AutoState currentState;

    @Override
    public void init() {
        shooter.init(hardwareMap);
        currentState = AutoState.SHOOTING; // Set the first state
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void start() {
        // 2. Trigger the first action as soon as Play is pressed
        autoTimer.reset();
        shooter.fireShots(3);
    }

    @Override
    public void loop() {
        // 3. ALWAYS update the mechanism first
        shooter.update();

        // 4. Handle the Auto Sequence
        switch (currentState) {
            case SHOOTING:
                telemetry.addData("State", "Shooting 3 Rings...");

                // Check if the mechanism is finished
                if (!shooter.isBusy()) {
                    // Transition to next step
                    currentState = AutoState.DRIVING_AWAY;
                    autoTimer.reset(); // Reset timer for the driving step
                }
                break;

            case DRIVING_AWAY:
                telemetry.addData("State", "Driving Away...");

                // Dummy Drive Code (e.g., set motor powers here)
                // leftDrive.setPower(0.5);
                // rightDrive.setPower(0.5);

                // Drive for 2 seconds
                if (autoTimer.seconds() > 2.0) {
                    // Stop motors here
                    // leftDrive.setPower(0);

                    currentState = AutoState.DONE;
                }
                break;

            case DONE:
                telemetry.addData("State", "Auto Complete");
                // Do nothing, just sit here
                break;
        }
    }
}
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
public class MotorTest extends LinearOpMode {

    private DcMotor frontLeftMotor;
    private DcMotor backLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backRightMotor;

    @Override
    public void runOpMode() {

        frontLeftMotor = hardwareMap.get(DcMotor.class, "front_left_motor");
        backLeftMotor = hardwareMap.get(DcMotor.class, "back_left_motor");
        frontRightMotor = hardwareMap.get(DcMotor.class, "front_right_motor");
        backRightMotor = hardwareMap.get(DcMotor.class, "back_right_motor");

        waitForStart();

        while (opModeIsActive()) {

            // A button -> front left motor
            if (gamepad1.a) {
                frontLeftMotor.setPower(0.5);
            } else {
                frontLeftMotor.setPower(0);
            }

            // B button -> front right motor
            if (gamepad1.b) {
                frontRightMotor.setPower(0.5);
            } else {
                frontRightMotor.setPower(0);
            }

            // X button -> back left motor
            if (gamepad1.x) {
                backLeftMotor.setPower(0.5);
            } else {
                backLeftMotor.setPower(0);
            }

            // Y button -> back right motor
            if (gamepad1.y) {
                backRightMotor.setPower(0.5);
            } else {
                backRightMotor.setPower(0);
            }
        }
    }
}

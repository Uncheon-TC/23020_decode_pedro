package org.firstinspires.ftc.teamcode.TeleOp;

import static org.firstinspires.ftc.teamcode.pedroPathing.Tuning.follower;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants.*;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Turret_Tracking;

@TeleOp(name = "decode 23020", group = "2024-2025 Test OP")
public class blue_test extends LinearOpMode {

    private DcMotor FrontLeftMotor, FrontRightMotor, BackLeftMotor, BackRightMotor;
    private DcMotor eat, SL, SR, SA;
    private Servo servo_S;
    private IMU imu;
    Turret_Tracking tracking = new Turret_Tracking();
    private Follower follower;
    private final Pose startPose = new Pose(72,72,90);

    @Override
    public void runOpMode() throws InterruptedException {

        Pose center = new Pose(72, 72, 90);

        follower = Constants.createFollower(hardwareMap);

        follower.setStartingPose(startPose);


        FrontLeftMotor  = hardwareMap.dcMotor.get("FL");
        FrontRightMotor = hardwareMap.dcMotor.get("FR");
        BackLeftMotor   = hardwareMap.dcMotor.get("BL");
        BackRightMotor  = hardwareMap.dcMotor.get("BR");

        FrontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        BackLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        FrontRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);



        eat = hardwareMap.dcMotor.get("eat");
        SL  = hardwareMap.dcMotor.get("SL");
        SR  = hardwareMap.dcMotor.get("SR");
        SA = hardwareMap.dcMotor.get("SA");

        eat.setDirection(DcMotorSimple.Direction.FORWARD);
        SL.setDirection(DcMotorSimple.Direction.FORWARD);
        SR.setDirection(DcMotorSimple.Direction.REVERSE);

        SA.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        SA.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        eat.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        SL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        SR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        eat.setPower(0);
        SL.setPower(0);
        SR.setPower(0);


        servo_S = hardwareMap.servo.get("servo_S");
        servo_S.setPosition(0.5); // 기본 위치


        imu = hardwareMap.get(IMU.class, "imu");
        IMU.Parameters parameters = new IMU.Parameters(
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                        RevHubOrientationOnRobot.UsbFacingDirection.UP
                )
        );

        imu.initialize(parameters);

       follower.setPose(center);

        waitForStart();

        while (opModeIsActive()) {

            follower.update();
            Pose current_robot_pos = follower.getPose();

            double y = -gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x;
            double rx = -gamepad1.right_stick_x;

            double slow = 1 - (0.8 * gamepad1.right_trigger);

            if (gamepad1.options) {
                imu.resetYaw();
            }

            double botHeading = imu.getRobotYawPitchRollAngles()
                    .getYaw(AngleUnit.RADIANS);

            double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
            double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

            rotX *= 1.1;

            double denominator = Math.max(
                    Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1
            );

            FrontLeftMotor.setPower((rotY + rotX - rx) / denominator * slow);
            BackLeftMotor.setPower((rotY - rotX - rx) / denominator * slow);
            FrontRightMotor.setPower((rotY - rotX + rx) / denominator * slow);
            BackRightMotor.setPower((rotY + rotX + rx) / denominator * slow);
            //메카넘 끝


            servo_S.setPosition(gamepad1.left_bumper ? 0.35 : 0.5);


            if (gamepad1.aWasPressed()) {
                eat.setPower(1);
            }

            if (gamepad1.bWasPressed()) {
                eat.setPower(0);
            }

            if (gamepad1.xWasPressed()) {
                SL.setPower(1);
                SR.setPower(1);
            }

            if (gamepad1.yWasPressed()) {
                SL.setPower(0);
                SR.setPower(0);
            }

            if (check_shooting_zone(current_robot_pos)) {
                SA.setTargetPosition(tracking.fix_to_goal_BLUE(current_robot_pos));
                SA.setPower(0.3);
                SA.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }

            telemetry.addData("eat Power", eat.getPower());
            telemetry.addData("SL Power", SL.getPower());
            telemetry.addData("SR Power", SR.getPower());
            telemetry.addData("Servo_S Pos", servo_S.getPosition());
            telemetry.addData("Heading (deg)",
                    imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES));
            telemetry.addData("target encoder", SA.getTargetPosition());
            telemetry.addData("current encoder", SA.getCurrentPosition());
            telemetry.update();
        }
    }

    public boolean check_shooting_zone(Pose pose) {
        if (pose.getY() >= Math.abs(pose.getX() - 72) + 72) return true;  // Y >= |x-72| + 72
        if (pose.getY() <= -Math.abs(pose.getX() - 72) + 72) return true; // Y <= -|x-72| + 72
        return false;
    }
}

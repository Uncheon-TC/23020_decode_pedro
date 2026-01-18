package org.firstinspires.ftc.teamcode;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

@Disabled
@TeleOp(name = "Test Graph")
public class sd extends OpMode {

    // PanelsTelemetry 인스턴스 (라이브러리 구조에 따라 접근 방식이 다를 수 있으나, 일반적으로 static field로 가정)
    private TelemetryManager panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();
    private ElapsedTime timer = new ElapsedTime();

    private double sinVariable = 0.0;
    private double cosVariable = 0.0;
    private double constVariable = 0.0;
    private double dampedSine = 0.0;
    private double lissajous = 0.0;
    private double ramp = 0.0;
    private double squareWave = 0.0;

    @Override
    public void init() {
        timer.reset();
        updateSignals();
    }

    @Override
    public void loop() {
        updateSignals();
    }

    private void updateSignals() {
        double t = timer.seconds();

        // Java.lang.Math 클래스 사용
        sinVariable = Math.sin(t);
        cosVariable = Math.cos(t);
        constVariable = 1.0;

        dampedSine = Math.exp(-0.2 * t) * Math.sin(2 * t);

        lissajous = Math.sin(3 * t + Math.PI / 2) * Math.cos(2 * t);

        ramp = (t % 5.0) / 5.0;

        // Kotlin의 if 식을 Java의 삼항 연산자로 변환
        squareWave = (Math.sin(2 * Math.PI * 0.5 * t) > 0) ? 1.0 : -1.0;

        // Panels Graph 데이터 추가
        panelsTelemetry.addData("sin", sinVariable);
        panelsTelemetry.addData("cos", cosVariable);
        panelsTelemetry.addData("dampedSine", dampedSine);
        panelsTelemetry.addData("lissajous", lissajous);
        panelsTelemetry.addData("ramp", ramp);
        panelsTelemetry.addData("square", squareWave);
        panelsTelemetry.addData("const", constVariable);

        // 여러 값을 한 줄에 추가 (문자열 연결 사용)
        panelsTelemetry.addLine("extra1:" + t + " extra2:" + (t * t) + " extra3:" + Math.sqrt(t));

        // OpMode의 기본 telemetry 객체를 인자로 전달
        panelsTelemetry.update(telemetry);
    }
}
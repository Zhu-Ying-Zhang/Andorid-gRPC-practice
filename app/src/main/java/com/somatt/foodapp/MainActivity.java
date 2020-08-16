package com.somatt.foodapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import dmax.dialog.SpotsDialog;
import io.grpc.ManagedChannel;
import io.grpc.okhttp.OkHttpChannelBuilder;
import somatt.hello.GreeterGrpc;
import somatt.hello.GreeterGrpc.GreeterBlockingStub;
import somatt.hello.HelloReply;
import somatt.hello.HelloRequest;
import android.view.View;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private TextView tvResult;
    private Button btnConnect;
    private EditText editTextTextPersonName;

    private final String SERVER_ADDRESS = "192.168.0.16";
    private final int PORT = 8080;

    private SpotsDialog dialog;
    private GreeterBlockingStub greeterBlockingStub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();

        dialog = new SpotsDialog(this, R.style.LoadingDialog);
        dialog.setCancelable(false);

        btnConnect.setOnClickListener((View v) -> {
            ManagedChannel channel = OkHttpChannelBuilder.forAddress(SERVER_ADDRESS, PORT)
                    .usePlaintext()
                    .keepAliveTime(1, TimeUnit.MINUTES)
                    .keepAliveTimeout(5, TimeUnit.SECONDS)
                    .keepAliveWithoutCalls(true)
                    .build();

            requestExampleService(channel);
        });
    }

    private void requestExampleService(ManagedChannel channel) {
        greeterBlockingStub = GreeterGrpc
                .newBlockingStub(channel);

        HelloRequest helloRequest = HelloRequest.newBuilder()
                .setName(editTextTextPersonName.getText().toString())
                .build();

        dialog.show();

        Thread backgroundThread = new Thread(() -> {
            try {
                HelloReply helloResponse = greeterBlockingStub.sayHello(helloRequest);
                showResultMessage("Response:  " + helloResponse.getMessage());
            } catch (Exception e) {
                showResultMessage("Error:   " + e.getMessage());
                e.printStackTrace();
            }
        });


        backgroundThread.start();

    }

    /**
     * show message on UI Thread
     * and dismiss loading dialog
     *
     * @param message
     */
    private void showResultMessage(String message) {
        runOnUiThread(() -> {
            tvResult.setText(message);
            dialog.dismiss();
        });
    }

    /**
     * find all components from view
     */
    private void findViews() {
        tvResult = findViewById(R.id.tv_result);
        btnConnect = findViewById(R.id.btn_server_connect);
        editTextTextPersonName = findViewById(R.id.editTextTextPersonName);
    }
}
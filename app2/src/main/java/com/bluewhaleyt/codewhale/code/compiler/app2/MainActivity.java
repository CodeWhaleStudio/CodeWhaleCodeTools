package com.bluewhaleyt.codewhale.code.compiler.app2;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bluewhaleyt.codewhale.code.compiler.core.CompileReporter;
import com.bluewhaleyt.codewhale.code.compiler.java.JavaCompilationResult;
import com.bluewhaleyt.codewhale.code.compiler.java.JavaCompileOptions;
import com.bluewhaleyt.codewhale.code.compiler.java.JavaCompiler;
import com.bluewhaleyt.codewhale.code.compiler.java.JavaProject;

import java.io.ByteArrayInputStream;
import java.io.File;

import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestAllFileAccess(this);
        setContentView(R.layout.activity_main);

        var rootDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CodeWhaleCodeCompiler";

        var compileOptions = new JavaCompileOptions();
        compileOptions.setInputStream(new ByteArrayInputStream(new byte[56]));

        var compiler = new JavaCompiler(
                this,
                new CompileReporter(),
                new JavaProject(
                        new File(rootDir + "/java"), // dataDir
                        new File(rootDir + "/java/projects/JavaTest") // projectDir
                ),
                compileOptions
        );

        var btnCompile = (Button) findViewById(R.id.btn_compile);
        var tvOutput = (TextView) findViewById(R.id.tv_output);

        btnCompile.setOnClickListener(v -> {
            tvOutput.setText(compiler.compile().getOutput());
        });
    }

    public static boolean isGrantedExternalStorageAccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        }
        return false;
    }

    public static void requestAllFileAccess(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!isGrantedExternalStorageAccess()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivity(intent);
            }
        } else {
            activity.startActivity(new Intent(activity, activity.getClass()));
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2000);
        }
    }
}
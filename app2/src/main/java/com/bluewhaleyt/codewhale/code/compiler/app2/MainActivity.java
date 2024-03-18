package com.bluewhaleyt.codewhale.code.compiler.app2;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bluewhaleyt.codewhale.code.core.compiler.CompileReport;
import com.bluewhaleyt.codewhale.code.core.compiler.CompileReporter;
import com.bluewhaleyt.codewhale.code.core.ExperimentalCompilerApi;
import com.bluewhaleyt.codewhale.code.core.compiler.CompileReporterKt;
import com.bluewhaleyt.codewhale.language.compiler.java.JavaCompileOptions;
import com.bluewhaleyt.codewhale.language.compiler.java.JavaCompiler;
import com.bluewhaleyt.codewhale.language.compiler.java.JavaProject;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @ExperimentalCompilerApi
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestAllFileAccess(this);
        setContentView(R.layout.activity_main);

        var btnCompile = (Button) findViewById(R.id.btn_compile);
        var tvOutput = (TextView) findViewById(R.id.tv_output);

        var rootDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CodeWhaleCodeTools";
        var project = new JavaProject(
                new File(rootDir + "/java"), // rootDir
                new File(rootDir + "/java/projects/JavaTest") // projectDir
        );
        var compileOptions = new JavaCompileOptions();
        var compiler = new JavaCompiler(
                this,
                new CompileReporter(),
                project,
                compileOptions
        );

        btnCompile.setOnClickListener(v -> { // recommend compile in background e.g AsyncTask
            tvOutput.setText(compiler.compile().getOutput());
        });

//        CompileReporter reporter = CompileReporterKt.createCompileReporter((report) -> {
//            if (!report.getMessage().isEmpty()) {
//                // ...
//            }
//            return null;
//        });
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
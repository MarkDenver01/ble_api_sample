package com.mpi.exam_sample.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.jakewharton.rxbinding3.view.RxView;
import com.mpi.exam_sample.R;
import com.mpi.exam_sample.model.intent.UserSavedData;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import kotlin.Unit;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    private Context context;

    public MainActivity() {
        this.context = MainActivity.this;
    }

    private Button btnSignUp;
    private Button btnLogin;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDesign();
        initButton();
    }

    @Override
    public void onBackPressed() {
    }

    private void initDesign() {
        setContentView(R.layout.activity_main);
        btnLogin = findViewById(R.id.button_log_in);
        btnSignUp = findViewById(R.id.button_sign_up);
    }

    private void initButton() {
        loginButtonClick();
        signUpButtonClick();
    }

    private void loginButtonClick() {
        compositeDisposable.add(RxView.clicks(btnLogin)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                    }
                }));
    }

    private void signUpButtonClick() {
        compositeDisposable.add(RxView.clicks(btnSignUp)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        Intent intent = new Intent(context, SignupActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                    }
                }));
    }
}

package com.mpi.exam_sample.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jakewharton.rxbinding3.view.RxView;
import com.mpi.exam_sample.R;
import com.mpi.exam_sample.model.intent.UserSavedData;
import com.mpi.exam_sample.model.local.LiveUserData;
import com.mpi.exam_sample.utility.CustomDialogBuilder;
import com.mpi.exam_sample.viewmodel.AccountViewModel;
import com.mpi.exam_sample.viewmodel.provider.AccountViewModelConstructor;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import kotlin.Unit;
import timber.log.Timber;

import static com.mpi.exam_sample.utility.Constants.INTENT_USER_DATA;
import static com.mpi.exam_sample.utility.Utils.isStringNullOrEmpty;

public class LoginActivity extends AppCompatActivity {
    private Context context;
    private EditText txtEmailAddress;
    private EditText txtPassword;
    private Button btnLogin;
    private Button btnBack;
    private AlertDialog singleButtonDialog;
    private AccountViewModel accountViewModel;
    private UserSavedData savedUserData = new UserSavedData();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public LoginActivity() {
        this.context = LoginActivity.this;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDesign();
        initViewModel();
        initButton();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        singleButtonDialog.dismiss();
        compositeDisposable.dispose();
    }

    @Override
    public void onBackPressed() {
    }

    private void initDesign() {
        setContentView(R.layout.activity_log_in);
        txtEmailAddress = findViewById(R.id.email_address_edit_text);
        txtPassword = findViewById(R.id.password_edit_text);
        btnLogin = findViewById(R.id.button_login);
        btnBack = findViewById(R.id.button_back);
    }

    private void initViewModel() {
        viewModelConstructor();
        userAccountViewModelResult();
    }

    private void viewModelConstructor() {
        accountViewModel = new ViewModelProvider(this, new AccountViewModelConstructor(context)).get(AccountViewModel.class);
    }

    private void userAccountViewModelResult() {
        accountViewModel.getUserStateAsLiveData().observe(this, new Observer<LiveUserData>() {
            @Override
            public void onChanged(LiveUserData liveUserData) {
                switch (liveUserData.getUserState()) {
                    case LOGIN_SUCCESS:
                        savedUserData.setName(liveUserData.getUserAccountResult().getName());
                        savedUserData.setMailAddress(liveUserData.getUserAccountResult().getMailAddress());
                        showAlertDialog("Login", "Login Success", "Proceed", new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(context, FirstPageActivity.class);
                                intent.putExtra(INTENT_USER_DATA, savedUserData);
                                startActivity(intent);
                                finish();
                            }
                        });
                        break;
                    case LOGIN_FAILED:
                        showAlertDialog("Login", "Login failed", "Close", null);
                        break;
                    case ERROR:
                        showAlertDialog("Login", "Unexpected error occur", "Close", null);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void initButton() {
        loginButtonClick();
        backButtonClick();
    }

    private void loginButtonClick() {
        compositeDisposable.add(RxView.clicks(btnLogin)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        // login restriction
                        if (isStringNullOrEmpty(txtEmailAddress.getText().toString())) {
                            showAlertDialog("Log in", "Email address is empty", "Close", null);
                            return;
                        }

                        if (isStringNullOrEmpty(txtPassword.getText().toString())) {
                            showAlertDialog("Log in", "Password is empty", "Close", null);
                            return;
                        }

                        accountViewModel.login(txtEmailAddress.getText().toString(), txtPassword.getText().toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                    }
                }));
    }

    private void backButtonClick() {
        compositeDisposable.add(RxView.clicks(btnBack)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        Intent intent = new Intent(context, MainActivity.class);
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

    private void showAlertDialog(String title, String description, String buttonName, Runnable runnable) {
        singleButtonDialog = CustomDialogBuilder.singleButtonDialogBox(
                context,
                title,
                description,
                buttonName,
                runnable
        );
        CustomDialogBuilder.showAlertDialog(singleButtonDialog);
    }
}

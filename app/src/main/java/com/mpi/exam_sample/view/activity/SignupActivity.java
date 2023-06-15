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
import com.mpi.exam_sample.utility.Constants;
import com.mpi.exam_sample.utility.CustomDialogBuilder;
import com.mpi.exam_sample.viewmodel.AccountViewModel;
import com.mpi.exam_sample.viewmodel.provider.AccountViewModelConstructor;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import kotlin.Unit;
import timber.log.Timber;

import static com.mpi.exam_sample.utility.Utils.isStringNullOrEmpty;

public class SignupActivity extends AppCompatActivity {
    private Context context;

    public SignupActivity() {
        this.context = SignupActivity.this;
    }

    private Button btnSignUp;
    private Button btnBack;
    private EditText txtName;
    private EditText txtEmailAddress;
    private EditText txtPassword;

    private AlertDialog singleButtonDialog;

    private UserSavedData savedUserData = new UserSavedData();

    private AccountViewModel accountViewModel;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDesign();
        initViewModel();
        initButton();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        singleButtonDialog.dismiss();
        compositeDisposable.dispose();
    }

    private void initDesign() {
        setContentView(R.layout.activity_sign_up);
        txtName = findViewById(R.id.name_edit_text);
        txtEmailAddress = findViewById(R.id.email_address_edit_text);
        txtPassword = findViewById(R.id.password_edit_text);
        btnSignUp = findViewById(R.id.button_sign_up);
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
                    case USER_ACCOUNT_EXIST:
                        showAlertDialog("Sign up", "Account is already exist", "Close", null);
                        break;
                    case USER_ACCOUNT_NOT_EXIST:
                        accountViewModel.createAccount(
                                txtName.getText().toString(),
                                txtEmailAddress.getText().toString(),
                                txtPassword.getText().toString());
                        break;
                    case USER_REGISTRATION_SUCCESSFUL:
                        savedUserData.setName(liveUserData.getUserAccountResult().getName());
                        savedUserData.setMailAddress(liveUserData.getUserAccountResult().getMailAddress());
                        showAlertDialog("Sign up", "Account has been created", "Proceed", new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(context, FirstPageActivity.class);
                                intent.putExtra(Constants.INTENT_USER_DATA, savedUserData);
                                startActivity(intent);
                                finish();
                            }
                        });
                        break;
                    case ERROR:
                        showAlertDialog("Sign up", "Unexpected error occour", "Close", null);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void initButton() {
        signUpButtonClick();
        backButtonClick();
    }

    private void signUpButtonClick() {
        compositeDisposable.add(RxView.clicks(btnSignUp)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        if (isStringNullOrEmpty(txtName.getText().toString())) {
                            showAlertDialog("Sign up", "Name field is empty", "Close", null);
                            return;
                        }

                        if (isStringNullOrEmpty(txtEmailAddress.getText().toString())) {
                            showAlertDialog("Sign up", "Email address field is empty", "Close", null);
                            return;
                        }

                        if (isStringNullOrEmpty(txtPassword.getText().toString())) {
                            showAlertDialog("Sign up", "Password field is empty", "Close", null);
                            return;
                        }

                        // validate user account
                        accountViewModel.validateUserAccount(txtEmailAddress.getText().toString());
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

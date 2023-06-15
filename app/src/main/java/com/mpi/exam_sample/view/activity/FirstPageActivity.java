package com.mpi.exam_sample.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding3.view.RxView;
import com.mpi.exam_sample.R;
import com.mpi.exam_sample.model.api.LiveUserInfoData;
import com.mpi.exam_sample.model.intent.UserSavedData;
import com.mpi.exam_sample.view.adapter.UserInfoAdapter;
import com.mpi.exam_sample.viewmodel.ApiViewModel;
import com.mpi.exam_sample.viewmodel.provider.ApiViewModelConstructor;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;
import timber.log.Timber;

import static com.mpi.exam_sample.utility.Constants.INTENT_USER_DATA;

public class FirstPageActivity extends AppCompatActivity {
    private Context context;

    public FirstPageActivity() {
        this.context = FirstPageActivity.this;
    }

    private TextView txtMenuName;
    private TextView txtMenuMailAddress;
    private Button btnLogout;

    private final UserInfoAdapter userInfoAdapter = new UserInfoAdapter();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final CompositeDisposable compositeDisposableRecycler = new CompositeDisposable();
    private UserSavedData savedUserData = new UserSavedData();
    private ApiViewModel apiViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initIntentData();
        initDesign();
        initViewModel();
        initButton();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposableRecycler.dispose();
        compositeDisposable.dispose();
    }

    private void initDesign() {
        setContentView(R.layout.activity_first_page);
        txtMenuName = findViewById(R.id.txt_menu_name);
        txtMenuMailAddress = findViewById(R.id.txt_menu_email);
        btnLogout = findViewById(R.id.button_log_out);

        // recycler view
        RecyclerView recyclerView = findViewById(R.id.recyclerview_user_info_list);
        recyclerView.setAdapter(userInfoAdapter);
        GridLayoutManager gridManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridManager);

        // display name and email
        if (savedUserData != null) {
            txtMenuName.setText(savedUserData.getName());
            txtMenuMailAddress.setText(savedUserData.getMailAddress());
        }
    }

    private void initViewModel() {
        viewModelConstructor();
        userInfoResponseResult();
    }

    private void viewModelConstructor() {
        apiViewModel = new ViewModelProvider(this, new ApiViewModelConstructor(context)).get(ApiViewModel.class);
    }

    private void userInfoResponseResult() {
        apiViewModel.getUserInfoResponse(); // call api
        apiViewModel.getUserInfoAsLiveData().observe(this, new Observer<LiveUserInfoData>() {
            @Override
            public void onChanged(LiveUserInfoData liveUserInfoData) {
                switch (liveUserInfoData.getUserInfoState()) {
                    case SUCCESS:
                        Timber.i("name " + liveUserInfoData.getUserInfoResponseList().get(0).getName()
                                + "\n email: " + liveUserInfoData.getUserInfoResponseList().get(0).getEmail());
                        userInfoAdapter.setUserInfoResponseList(liveUserInfoData.getUserInfoResponseList());
                        break;
                    case EMPTY:
                        Timber.i("Empty");
                        break;
                    case ERROR:
                        Timber.i("Error: " + liveUserInfoData.getErrorMessage());
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void initButton() {
        logoutClickButton();
        listClickButton();
    }

    private void logoutClickButton() {
        compositeDisposable.add(RxView.clicks(btnLogout)
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

    private void listClickButton() {
        compositeDisposableRecycler.clear();
        compositeDisposableRecycler.add(userInfoAdapter.observableClickedView()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<View>() {
                    @Override
                    public void onNext(View view) {
                        compositeDisposableRecycler.add(RxView.clicks(view)
                                .throttleFirst(500, TimeUnit.MILLISECONDS)
                                .subscribe(new Consumer<Unit>() {
                                    @Override
                                    public void accept(Unit unit) throws Exception {
                                        Timber.i("OK");
                                        Log.i("xxxxx", "xxxxxxx asd");
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        Timber.e(throwable);
                                    }
                                }));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Timber.i("onComplete");
                    }
                }));
    }

    private void initIntentData() {
        final Intent intent = getIntent();
        if (intent.getSerializableExtra(INTENT_USER_DATA) != null) {
            savedUserData = (UserSavedData) intent.getSerializableExtra(INTENT_USER_DATA);
        }
    }
}


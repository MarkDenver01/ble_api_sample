package com.mpi.exam_sample.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mpi.exam_sample.enums.UserState;
import com.mpi.exam_sample.model.local.LiveUserData;
import com.mpi.exam_sample.model.local.UserAccountResult;
import com.mpi.exam_sample.model.local.UserResponse;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;
import timber.log.Timber;

public class AccountViewModel extends ViewModel {
    private Realm realm;

    private Disposable disposable = new CompositeDisposable();

    private UserAccountResult userAccountResult = new UserAccountResult();

    private MutableLiveData<LiveUserData> userStateMutableLiveData;

    public LiveData<LiveUserData> getUserStateAsLiveData() {
        return userStateMutableLiveData;
    }

    public AccountViewModel(Context context) {
        this.userStateMutableLiveData = new MutableLiveData<>();
        Realm.init(context);
        this.realm = Realm.getDefaultInstance();
    }

    private Observable<RealmResults<UserResponse>> checkUserAccount(String mailAddress) {
        RealmResults<UserResponse> realmResults = realm.where(UserResponse.class)
                .equalTo("mailAddress", mailAddress).findAll();
        return Observable.just(realmResults);
    }

    private Observable<RealmResults<UserResponse>> checkLoginAccount(String mailAddress, String password) {
        RealmResults<UserResponse> realmResults = realm.where(UserResponse.class)
                .equalTo("mailAddress", mailAddress)
                .and()
                .equalTo("password", password)
                .findAll();
        return Observable.just(realmResults);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.dispose();
        realm.close();
    }

    public void validateUserAccount(String mailAddress) {
        disposable = checkUserAccount(mailAddress)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<RealmResults<UserResponse>>() {
                    @Override
                    protected void onStart() {
                        Timber.i("onStart");
                    }

                    @Override
                    public void onNext(RealmResults<UserResponse> userResponses) {
                        if (userResponses.size() > 0) {
                            userStateMutableLiveData.postValue(new LiveUserData(
                                    UserState.USER_ACCOUNT_EXIST));
                        } else {
                            userStateMutableLiveData.postValue(new LiveUserData(
                                    UserState.USER_ACCOUNT_NOT_EXIST));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                        userStateMutableLiveData.postValue(new LiveUserData(
                                UserState.ERROR));
                        dispose();
                    }

                    @Override
                    public void onComplete() {
                        Timber.i("onComplete");
                        dispose();
                    }
                });
    }

    public void createAccount(String name, String mailAddress, String password) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Timber.i("Saving account info...");
                UserResponse userResponse = realm.createObject(UserResponse.class);
                userResponse.setName(name);
                userResponse.setMailAddress(mailAddress);
                userResponse.setPassword(password);
                userAccountResult.setName(name);
                userAccountResult.setMailAddress(mailAddress);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Timber.i("Registration successful...");
                userStateMutableLiveData.postValue(new LiveUserData(
                        UserState.USER_REGISTRATION_SUCCESSFUL, userAccountResult));
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                userStateMutableLiveData.postValue(new LiveUserData(UserState.ERROR));
            }
        });
    }

    public void login(String mailAddress, String password) {
        disposable = checkLoginAccount(mailAddress, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<RealmResults<UserResponse>>() {
                    @Override
                    protected void onStart() {
                        Timber.i("onStart");
                    }

                    @Override
                    public void onNext(RealmResults<UserResponse> userResponses) {
                        if (userResponses.size() > 0) {
                            for (UserResponse userResponse : userResponses) {
                                userAccountResult.setName(userResponse.getName());
                                userAccountResult.setMailAddress(userResponse.getMailAddress());
                            }
                            userStateMutableLiveData.postValue(new LiveUserData(UserState.LOGIN_SUCCESS, userAccountResult));
                        } else {
                            userStateMutableLiveData.postValue(new LiveUserData(UserState.LOGIN_FAILED));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        userStateMutableLiveData.setValue(new LiveUserData(UserState.ERROR));
                        dispose();
                    }

                    @Override
                    public void onComplete() {
                        Timber.i("onComplete");
                        dispose();
                    }
                });
    }
}

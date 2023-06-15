package com.mpi.exam_sample.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mpi.exam_sample.BuildConfig;
import com.mpi.exam_sample.api.ApiService;
import com.mpi.exam_sample.enums.UserInfoState;
import com.mpi.exam_sample.model.api.LiveUserInfoData;
import com.mpi.exam_sample.model.api.UserInfoResponse;
import com.mpi.exam_sample.utility.Utils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class ApiViewModel extends ViewModel {
    private Context context;

    private ApiService apiService;
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/";

    private MutableLiveData<LiveUserInfoData> mutableUserInfo;

    public LiveData<LiveUserInfoData> getUserInfoAsLiveData() {
        return mutableUserInfo;
    }

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public ApiViewModel(Context context) {
        this.context = context;
        this.mutableUserInfo = new MutableLiveData<>();
    }

    private Observable<List<UserInfoResponse>> getUserInfo() {
        apiService = provideRetrofit().create(ApiService.class);
        return apiService.getUsers();
    }

    public void getUserInfoResponse() {
        apiService = provideRetrofit().create(ApiService.class);
        compositeDisposable.add(getUserInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<UserInfoResponse>>() {
                    @Override
                    public void accept(List<UserInfoResponse> userInfoResponses) throws Exception {
                        if (userInfoResponses.size() > 0) {
                            mutableUserInfo.postValue(new LiveUserInfoData(UserInfoState.SUCCESS, userInfoResponses));
                        } else {
                            mutableUserInfo.postValue(new LiveUserInfoData(UserInfoState.EMPTY));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                        mutableUserInfo.postValue(new LiveUserInfoData(UserInfoState.ERROR, throwable.getMessage()));
                    }
                }));
    }

    private Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(provideOkHttpClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(provideGson()))
                .build();
    }

    private OkHttpClient provideOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addNetworkInterceptor(provideHttpLoggingInterceptor())
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .connectionPool(provideConnectPool())
                .cache(provideCache())
                .addInterceptor(provideInterceptor());

        return builder.build();
    }

    private HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        if (BuildConfig.DEBUG) {
            return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE);
        }
    }

    private ConnectionPool provideConnectPool() {
        return new ConnectionPool(10, 10, TimeUnit.MINUTES);
    }

    private Cache provideCache() {
        int cacheSize = 10 * 1024 * 1024;
        return new Cache(context.getCacheDir(), cacheSize);
    }

    public Interceptor provideInterceptor() {
        return new Interceptor() {
            @NonNull
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder().build();

                if (Utils.isNetworkConnected(context)) {
                    CacheControl cacheControl = new CacheControl.Builder()
                            .maxAge(5, TimeUnit.MINUTES) // 5 minutes
                            .build();
                    newRequest = newRequest.newBuilder()
                            .cacheControl(cacheControl)
                            .header("Cache-Control", "public, max-age=" + 60)
                            .build();
                } else {
                    newRequest = newRequest.newBuilder()
                            .cacheControl(CacheControl.FORCE_CACHE) // forcing cache
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7)
                            .build();
                }

                return chain.proceed(newRequest);
            }
        };
    }

    private Gson provideGson() {
        return new GsonBuilder()
                .setLenient()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }
}

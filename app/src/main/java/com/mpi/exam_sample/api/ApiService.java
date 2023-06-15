package com.mpi.exam_sample.api;

import com.mpi.exam_sample.model.api.UserInfoResponse;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface ApiService {
    @GET("users")
    Observable<List<UserInfoResponse>> getUsers();
}

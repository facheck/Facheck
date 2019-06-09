package com.fzu.facheck.network.api;

import com.fzu.facheck.entity.RollCall.LoginInfo;
import com.fzu.facheck.entity.RollCall.StateInfo;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface LoginServer {
    @POST("/{cid}")
    Observable<StateInfo> getserver(@Path("cid") String cid, @Body RequestBody requestBody);
    @POST("/{cid}")
    Observable<LoginInfo> get_login_server(@Path("cid") String cid, @Body RequestBody requestBody);
}

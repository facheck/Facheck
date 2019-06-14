package com.fzu.facheck.network.api;

import com.fzu.facheck.entity.RollCall.RollCallInfo;
import com.fzu.facheck.entity.RollCall.RollCallResult;
import com.fzu.facheck.entity.RollCall.StartRollCallInfo;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * @date: 2019/4/25
 * @author: wyz
 * @version:
 * @description: 签到相关的api
 */
public interface SignInService {

    /**
     * 签到信息
     */
    @POST("/sign_in")
    Observable<RollCallInfo> postSignInInfo(@Body RequestBody requestBody);


}

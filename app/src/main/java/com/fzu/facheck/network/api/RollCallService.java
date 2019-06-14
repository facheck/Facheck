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
 * @description: 点名相关的api
 */
public interface RollCallService {

    /**
     * 点名班级信息
     */
    @POST("/get_user_class_info")
    Observable<RollCallInfo> getRollCallInfoById( @Body RequestBody requestBody);

    /**
     * 点名人相关信息
     */
    @POST("/start_roll_call")
    Observable<StartRollCallInfo> getStartRollCallById(@Body RequestBody requestBody);

    /**
     * 点名结果信息
     */
    @POST("/get_single_class_sign_in_info")
    Observable<RollCallResult> getRollCallResultById(@Body RequestBody requestBody);

    /**
     * 重置点名状态
     */
    @POST("/reset_able_roll_call")
    Observable<RollCallResult> resetAbleRollCall(@Body RequestBody requestBody);

}

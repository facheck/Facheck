package com.fzu.facheck.network.api;

import com.fzu.facheck.entity.RollCall.StudentInfo;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface StudentServer {
    @POST("{cid}")
    Observable<StudentInfo> getclassserver(@Path("cid") String cid, @Body RequestBody requestBody);
}

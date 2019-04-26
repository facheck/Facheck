package com.fzu.facheck.network;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.fzu.facheck.FacheckAPP;
import com.fzu.facheck.network.api.LoginServer;
import com.fzu.facheck.utils.CommonUtil;


import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @date: 2019/4/23
 * @author: wyz
 * @version:
 * @description: retrofit 帮助类
 */
public class RetrofitHelper {
    private static OkHttpClient mOkHttpClient;

    static {
        initOkHttpClient();
    }
//
//    public static LiveService getLiveAPI() {
//        return createApi(LiveService.class, ApiConstants.LIVE_BASE_URL);
//    }
//
//    public static BiliAppService getBiliAppAPI() {
//        return createApi(BiliAppService.class, ApiConstants.APP_BASE_URL);
//    }
//
//    public static BiliApiService getBiliAPI() {
//        return createApi(BiliApiService.class, ApiConstants.API_BASE_URL);
//    }
//
//    public static BiliGoService getBiliGoAPI() {
//        return createApi(BiliGoService.class, ApiConstants.BILI_GO_BASE_URL);
//    }
//
//    public static RankService getRankAPI() {
//        return createApi(RankService.class, ApiConstants.RANK_BASE_URL);
//    }
//
//    public static UserService getUserAPI() {
//        return createApi(UserService.class, ApiConstants.USER_BASE_URL);
//    }
//
//    public static VipService getVipAPI() {
//        return createApi(VipService.class, ApiConstants.VIP_BASE_URL);
//    }
//
//    public static BangumiService getBangumiAPI() {
//        return createApi(BangumiService.class, ApiConstants.BANGUMI_BASE_URL);
//    }
//
//    public static SearchService getSearchAPI() {
//        return createApi(SearchService.class, ApiConstants.SEARCH_BASE_URL);
//    }
//
//    public static AccountService getAccountAPI() {
//        return createApi(AccountService.class, ApiConstants.ACCOUNT_BASE_URL);
//    }
//
//    public static Im9Service getIm9API() {
//        return createApi(Im9Service.class, ApiConstants.IM9_BASE_URL);
//    }
    //注册、登入、忘记密码服务
    public static LoginServer getLoAPI(){
        return createApi(LoginServer.class,"http://172.26.93.218:5000");
    }
    /**
     * 根据传入的baseUrl和api创建retrofit
     */
    private static <T> T createApi(Class<T> clazz, String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(clazz);
    }


    /**
     * 初始化OKHttpClient,设置缓存,设置超时时间,设置打印日志
     */
    private static void initOkHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        if (mOkHttpClient == null) {
            synchronized (RetrofitHelper.class) {
                if (mOkHttpClient == null) {
                    //设置Http缓存
                    Cache cache = new Cache(new File(FacheckAPP.getInstance().getCacheDir(), "HttpCache"), 1024 * 1024 * 10);
                    mOkHttpClient = new OkHttpClient.Builder()
                            .cache(cache)
                            .addInterceptor(interceptor)
                            .addNetworkInterceptor(new CacheInterceptor())
                            .addNetworkInterceptor(new StethoInterceptor())
                            .retryOnConnectionFailure(true)
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(20, TimeUnit.SECONDS)
                            .readTimeout(20, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
    }



    /**
     * 为okhttp添加缓存
     */
    private static class CacheInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            // 有网络时 设置缓存超时时间1个小时
            int maxAge = 60 * 60;
            // 无网络时，设置超时为1天
            int maxStale = 60 * 60 * 24;
            Request request = chain.request();
            if (CommonUtil.isNetworkAvailable(FacheckAPP.getInstance())) {
                //有网络时只从网络获取
                request = request.newBuilder().cacheControl(CacheControl.FORCE_NETWORK).build();
            } else {
                //无网络时只从缓存中读取
                request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
            }
            Response response = chain.proceed(request);
            if (CommonUtil.isNetworkAvailable(FacheckAPP.getInstance())) {
                response = response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                response = response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
            return response;
        }
    }
}

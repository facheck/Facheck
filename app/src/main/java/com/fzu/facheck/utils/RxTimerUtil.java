package com.fzu.facheck.utils;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


/**
 * @date: 2019/4/29
 * @author: wyz
 * @version:
 * @description: 定时器工具类
 */
public class RxTimerUtil {

    private static Subscription mSubscription;


    /** minutes 分钟后执行next操作
     *
     * @param minutes
     * @param next
     */
    public static void timer(long minutes,final IRxNext next) {
        mSubscription = Observable.timer(minutes, TimeUnit.MINUTES)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long number) {
                        if(next!=null){
                            next.doNext(number);
                        }
                        //取消订阅
                        cancel();
                    }
                });
    }


    /** 每隔milliseconds毫秒后执行next操作
     *
     * @param milliseconds
     * @param next
     */
    public static void interval(long milliseconds,final IRxNext next){
        mSubscription=Observable.interval(milliseconds, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long number) {
                        if(next!=null){
                            next.doNext(number);
                        }
                    }
                });
    }


    /**
     * 取消订阅
     */
    public static void cancel(){
        if(mSubscription!=null&&!mSubscription.isUnsubscribed()){
            mSubscription.unsubscribe();
        }
    }


    public interface IRxNext{
        void doNext(long number);
    }

}

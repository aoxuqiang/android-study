package com.miqi.mystudy.retrofit;

import com.miqi.mystudy.bean.CommonResponse;

import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by aoxuqiang on 2017/12/29.
 */

public interface ApiService {
    @POST("login")
    Observable<CommonResponse> login(@Query("account")String account,@Query("passwd")String passwd);
}

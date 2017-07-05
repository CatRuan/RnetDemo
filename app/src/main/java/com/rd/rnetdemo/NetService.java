package com.rd.rnetdemo;


import com.rd.rnetdemo.bean.BaseResponse;
import com.rd.rnetdemo.bean.RealResponse;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import rx.Observable;

/**
 * Created by ruand on 2017/6/28.
 *
 */

public interface NetService {


    @POST("http://v.juhe.cn/weather/forecast3h")
    Observable<BaseResponse<RealResponse>> getPost(@Body RequestBody request);

    @Streaming
    @GET("http://v1.qzone.cc/pic/201303/28/14/53/5153e8d11f4bf030.jpg!600x600.jpg")
    Observable<Response<ResponseBody>> download();//直接使用网址下载

    @Streaming
    @GET("http://dldir1.qq.com/qqfile/qq/QQ8.9.2/20760/QQ8.9.2.exe")
    Observable<Response<ResponseBody>> downloadPro();//直接使用网址下载

}

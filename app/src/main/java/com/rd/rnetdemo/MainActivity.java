package com.rd.rnetdemo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rd.rnet.RNet;
import com.rd.rnet.callback.FileCallBack;
import com.rd.rnet.callback.FileProgressCallBack;
import com.rd.rnet.callback.NetCallback;
import com.rd.rnetdemo.bean.BaseResponse;
import com.rd.rnetdemo.bean.RealResponse;
import com.rd.rnetdemo.bean.Request;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;
import rx.Subscription;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnDownload;
    private Button mBnDownLoadPro;
    private Button mBtnPost;
    private RNet mRNet;
    private NetService mNetService;
    private ProgressDialog mProgressDialog;//进度窗口
    public static final String SD_PATH = Environment.getExternalStorageDirectory() + "/";//
    private Subscription mDownloadSubscription;
    private Subscription mDownloadProSubscription;
    private Subscription mPostSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRNet = MyApplication.getInstance().getRNet();
        mNetService = MyApplication.getInstance().getNetService();
        initView();
    }

    private void initView() {
        mBtnDownload = (Button) findViewById(R.id.btnDownload);
        mBnDownLoadPro = (Button) findViewById(R.id.btnDownLoadPro);
        mBtnPost = (Button) findViewById(R.id.btnPost);
        mBnDownLoadPro.setOnClickListener(this);
        mBtnDownload.setOnClickListener(this);
        mBtnPost.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDownload:
                downLoad();
                break;
            case R.id.btnDownLoadPro:
                downloadPro();
                break;
            case R.id.btnPost:
                post();
                break;
        }

    }

    /**
     * 文件下载，不带进度。适合小文件，如图片
     */
    private void downLoad() {
        Observable<Response<ResponseBody>> observable = mNetService.download();
        FileCallBack fileCallBack = new FileCallBack(SD_PATH, "test.jpg") {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "下载文件成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(String msg) {
                Toast.makeText(MainActivity.this, "下载失败:" + msg, Toast.LENGTH_SHORT).show();
            }
        };
        mRNet.showLoadingDialog(this);
        mDownloadSubscription = mRNet.download(getApplication(), observable, fileCallBack);

    }

    /**
     * 文件下载，带进度。适合大文件
     */
    private void downloadPro() {

        Observable<Response<ResponseBody>> observable = mNetService.downloadPro();
        FileProgressCallBack fileCallBack = new FileProgressCallBack(SD_PATH, "test.apk") {
            @Override
            public void progress(long progress, long total) {
                mProgressDialog.setMax((int) total / 1024 / 1024);
                mProgressDialog.setProgress((int) progress / 1024 / 1024);
            }

            @Override
            public void onStart() {
                mProgressDialog = new ProgressDialog(MainActivity.this);
                mProgressDialog.setTitle("下载中……");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.show();
            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "下载文件成功", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }

            @Override
            public void onFail(String msg) {
                Toast.makeText(MainActivity.this, "下载失败:" + msg, Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        };
        mDownloadProSubscription = mRNet.downloadWithProgress(getApplication(), observable, fileCallBack);

    }

    /**
     * post请求
     */
    private void post() {
        Request request = new Request(186878, "f5058990ccbfcfdfc3fcef8de72c6981", "json");
        Gson gson = new Gson();
        String jsonStr = gson.toJson(request);
        Observable<BaseResponse<RealResponse>> observable = mNetService
                .getPost(RequestBody.create(MediaType.parse("application/json"), jsonStr));
        NetCallback<BaseResponse<RealResponse>> netCallback = new NetCallback<BaseResponse<RealResponse>>(getApplication()) {
            @Override
            public void onSuccess(BaseResponse<RealResponse> response) {
                String msg = response.getResultcode();
                Toast.makeText(MainActivity.this, "请求成功:code ->" + msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(MainActivity.this, "请求失败:" + message, Toast.LENGTH_SHORT).show();
            }
        };
        mRNet.showLoadingDialog(this);
        mPostSubscription = mRNet.post(observable, netCallback);
    }


    @Override
    protected void onPause() {
        super.onPause();
        /**
         * cancel request
         */
        mRNet.cancelRequest(mDownloadSubscription);
        mRNet.cancelRequest(mDownloadProSubscription);
        mRNet.cancelRequest(mPostSubscription);
    }


}

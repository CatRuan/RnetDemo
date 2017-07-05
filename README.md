作者：catRuan（阮妹子）
联系方式：QQ:940472401 邮箱：940472401@qq.com
====
# 一、什么是RNet？<br> 
RNet是基于retrofit2和rxjava封装的一个网络请求框架。
# 二、RNet的基本功能<br> 
①、get请求
②、post请求
③、文件下载（不进度条）
④、文件下载（带进度）
⑤、Header的统一添加
⑥、网络返回预处理
⑦、会话保持（即cookie/session的自动化管理）
⑧、HTTPS信任模式
⑨、加载页选择显示和自动隐藏
⑩、取消网络请求

# 三、待添加的功能<br> 
①、文件上传
②、以比较优雅的方式取消网络请求
# 四、RNet使用<br> 
Rnet的引用：  Android stuodio   app的build.gradle中添加  compile 'com.rd:rnet:1.0'<br> 
                                               最新版：compile 'com.rd:rnet:2.0.0'<br> 
             eclipse  sorry暂时不提供下载jar包，复制粘贴吧 <br>   
最新版本： 2.0.0(移除fastjson作为默认解析工具，改用Gson)<br> 
Rnet在代码里的使用（以2.0.0为例）：
①、一RNet的初始化，建议在application中进行<br> 

public class MyApplication extends Application {  
  
    private HashMap<String, String> mHeaders;  
    private static final String BASE_URL = "https://github.com/";  
    public static RNet mRNet;  
    public static NetService mNetService;  
    private static MyApplication mApp;  
  
    @Override  
    public void onCreate() {  
        super.onCreate();  
        mApp = this;  
        initNetWork();  
    }  
  
    private void initNetWork() {  
        mHeaders = new HashMap<>();  
//        mHeaders.put("Content-Type", "application/json;encoding-utf-8");  
//        mHeaders.put("Accept", "application/json");  
        mRNet = RNet.getInstance(this, BASE_URL, mHeaders, false);  
        mNetService = mRNet.create(NetService.class);  
    }  
  
  
    public static MyApplication getInstance() {  
        return mApp;  
    }  
  
    public RNet getRNet() {  
        return mRNet;  
    }  
  
  
    public NetService getNetService() {  
        return mNetService;  
    }  
  
}  
②、编写网络请求接口，这里以post、带进度的下载和不带进度的下载为例子<br> 
[java] view plain copy
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
③执行网络请求<br> 
[java] view plain copy
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
  
   ……  
  
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

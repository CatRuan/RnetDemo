package com.rd.rnet.callback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;
import rx.exceptions.Exceptions;

/**
 * Created by ruand on 2017/5/17.
 * 下载（/上传）文件回调
 */

public abstract class FileCallBack {
    private String destFileDir;
    private String destFileName;

    public FileCallBack(String destFileDir, String destFileName) {
        this.destFileDir = destFileDir;
        this.destFileName = destFileName;
    }

    public abstract void onSuccess();

    public abstract void onFail(String msg);

    /**
     * @param body ResponseBody
     */
    public void saveFile(ResponseBody body) {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len;
        FileOutputStream fos = null;
        if (null == body) {
            throw new RuntimeException("the ResponseBody is null");
        } else {
            try {
                is = body.byteStream();
                File dir = new File(destFileDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir, destFileName);
                fos = new FileOutputStream(file);
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                }
                fos.flush();
            } catch (Exception e) {
                e.printStackTrace();
                //将catch到的异常重新抛出，使Rxjava的OnError捕获到该异常
                throw Exceptions.propagate(e);
            } finally {
                try {
                    if (is != null) is.close();
                    if (fos != null) fos.close();
                } catch (IOException e) {
                    // Log.e("saveFile", e.getMessage());
                    //将catch到的异常重新抛出，使Rxjava的OnError捕获到该异常
                    throw Exceptions.propagate(e);
                }
            }
        }

    }

}

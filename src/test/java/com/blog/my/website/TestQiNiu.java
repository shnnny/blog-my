package com.blog.my.website;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

import java.io.IOException;

/**
 * Created by zzh on 2018/9/28.
 */
public class TestQiNiu {


    public static void main(String[] args) {
      test();
    }

    public static void test(){

        Configuration cfg = new Configuration(Zone.zone0());

        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传
        String accessKey = "sGU1ZNSc5M9SxJk1Mw1l46TunZjmTKT_mhlzXBT-";
        String secretKey = "uVGeUNPIPTtzbFfo8ZiJIaebgLPUlVRSHuGTAW5l";
        String bucket = "shnnny";
        //如果是Windows情况下，格式是 D:\\qiniu\\test.png
        String localFilePath = "C:\\Users\\zzh\\Desktop\\monkey\\58c6b8cc459bed3558f71a13b3ea31a1.jpg";
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = null;

        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try {
            Response response = uploadManager.put(localFilePath, key, upToken);
            //  Response response = uploadManager.put(file.getBytes(), key, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println("putRet.key-->   "+putRet.key);
            System.out.println("putRet.hash-->   "+putRet.hash);
            System.out.println("http://pfr4i39yo.bkt.clouddn.com/"+putRet.key);
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

package com.Lechuang.app.Utils;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import www.xcd.com.mylibrary.entity.GlobalParam;

/**
 * Created by Android on 2017/7/20.
 */

public class HelpUtils {
    static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build();
    /**
     * 获取年月日
     * @return
     */
    public static String getCurrentData(){
        SimpleDateFormat formatter    =   new    SimpleDateFormat    ("yyyy-MM-dd");
        Date    curDate    =   new Date(System.currentTimeMillis());//获取当前时间
        String    data    =    formatter.format(curDate);
        return data;
    }
    /**
     * 获取年月日时分秒
     * HH 24小时制
     * hh 12小时制
     * @return
     */
    public static String getCurrentAllData(){
        SimpleDateFormat formatter    =   new    SimpleDateFormat    ("yyyy-MM-dd HH:mm:ss ");
        Date    curDate    =   new Date(System.currentTimeMillis());//获取当前时间
        String    data    =    formatter.format(curDate);
        return data;
    }
    public static String getTimeDifference(String starTime){
        StringBuffer sb = new StringBuffer();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String endString = getCurrentData();
        try {
            Date star = dateFormat.parse(starTime);
            Date end = dateFormat.parse(endString);

            long times = end.getTime() - star.getTime();//这样得到的差值是微秒级别
            if (times<0){
                return "-1";
            }
            long yearnumber = 1000L * 60L* 60L * 24L * 365L;
            long monthnumber = 1000L * 60L * 60L * 24L * 30L;
            long year = times/(yearnumber); //换算成年数
            if (year >= 1) {
                sb.append(year+"年");
                long surplus_data = times % (yearnumber);
                long surplus_month = surplus_data/ (monthnumber); //换算成月数
                if (surplus_month>=1){
                    sb.append(surplus_month+"月");
                    long surplus_days = surplus_data% (monthnumber);
                    if (surplus_days!=0){
                        long days = surplus_days/ (1000 * 60* 60 * 24); //换算成天数
                        sb.append((days+1)+"天");
                    }
                }else {
                    sb.append((surplus_month+1)+"天");
                }
            } else {
                long month = times/ (monthnumber); //换算成月数
                if (month >= 1){
                    sb.append(month+"月");
                    long surplus_month = month% (monthnumber);
                    if (surplus_month!=0){
                        long days = surplus_month/ (1000 * 60* 60 * 24); //换算成天数
                        sb.append((days+1)+"天");
                    }

                }else {
                    long days = times/ (1000 * 60* 60 * 24); //换算成天数
                    if (days>=0){
                        sb.append((days+1)+"天");
                    }else {
                        sb.append("不足一天");
                    }
//                    long hours =(times-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60); //换算成小时
//
//                    long minutes =(times-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60); //换算成分钟
                    return sb.toString();
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "-1";
        }
        return sb.toString();
    }
    /**
     * dp转px
     *
     * @param context
     * @param dpVal
     * @return
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    public static String uploadImg(String qk_id, List<File> listpath, String imagenema) {
        String reString = "";
        try {
            String path = GlobalParam.PICUPLOAD;
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

            //遍历listpath中所有图片绝对路径到builder，并约定key如“img”作为后台接受多张图片的key
            for (File imagepath : listpath) {
                Log.e("TAG_","imagenema="+imagenema+";imagepath="+imagepath);
                builder.addFormDataPart("img", imagenema, RequestBody.create(MEDIA_TYPE_PNG, imagepath));
            }
            builder.addFormDataPart("qk_id", qk_id);
            MultipartBody requestBody = builder.build();
            reString = postokHttpClient(path, requestBody);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return reString;
    }
    public static String postokHttpClient(String path, RequestBody formBody) throws Exception {
        Request request = new Request.Builder()
                .url(path)
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            String string = response.body().string();
            return string;
        } else {
//            throw new IOException("Unexpected code " + response);
            return String.valueOf(response.code());
        }
    }
}

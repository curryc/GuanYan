package com.scu.guanyan.utils.base;

import com.scu.guanyan.event.BaseEvent;
import com.scu.guanyan.event.WebEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2022/12/31 20:29
 * @description:
 **/
public class Web {
    private static final String POST_FEEDBACK = "http://1.117.68.73:8080";
    private static final String POST_CLASSICAL = "http://43.156.5.87:8888/api/gpt/";

    public static void postFeedback(String flag, String quz, String advise, String name, String tel) throws JSONException {
        OkHttpClient okHttpClient = new OkHttpClient();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("connection", tel);
        jsonObject.put("question", quz);
        jsonObject.put("advice", advise);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
                .url(POST_FEEDBACK + "/api/advice/creat")
                .addHeader("key", "value")
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Call arg0, Response response) throws IOException {
                try {
                    JSONObject ret = new JSONObject(response.body().string());
                    BaseEvent event = new WebEvent(flag,ret.toString(), ret.getString("message"), ret.getInt("code") == 200);
                    EventBus.getDefault().post(event);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
            }
        });
    }

    public static void postClassicalWords(String tag, String text) throws JSONException {
        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url(POST_CLASSICAL + text)
                .addHeader("key", "value")
                .post(null)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Call arg0, Response response) throws IOException {
                try {
                    JSONObject ret = new JSONObject(response.body().string());
                    BaseEvent event = new WebEvent(tag,ret.toString(), ret.getString("result"), ret.getInt("code") == 200);
                    EventBus.getDefault().post(event);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
            }
        });
    }
}

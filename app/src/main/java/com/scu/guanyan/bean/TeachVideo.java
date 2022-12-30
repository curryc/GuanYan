package com.scu.guanyan.bean;

import java.io.Serializable;

/**
 * @program: Guanyan
 * @author: 陈博文
 * @create: 2022-11-12 16:20
 * @description: 教学视频类，一个教学视频的各种信息
 **/
public class TeachVideo implements Serializable {
    private String title;// 标题
    private String cover;// 封面地址
    private String uri;// 视频地址

    public TeachVideo(String title, String cover, String uri) {
        this.title = title;
        this.cover = cover;
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}

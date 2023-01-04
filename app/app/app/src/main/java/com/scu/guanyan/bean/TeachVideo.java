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
    private String video;// 视频地址

    public TeachVideo(String title, String cover, String video) {
        this.title = title;
        this.cover = cover;
        this.video = video;
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

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}

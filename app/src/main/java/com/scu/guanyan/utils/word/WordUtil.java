package com.scu.guanyan.utils.word;

import android.app.Activity;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

/**
 * @author: 浦博威
 * @create: 2022-06-30 23:17
 * @description: python解释器，构造函数传入activity
 **/
public class WordUtil {

   public WordUtil(Activity activity){
       if (! Python.isStarted()) {
           Python.start(new AndroidPlatform(activity));
       }
   }

   public String cut(String seq){
       Python py = Python.getInstance();
       return py.getModule("lcut").callAttr("lcutseq",seq).toString();
   }
}


package com.scu.guanyan.utils.word;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author: 浦博威
 * @create: 2022-06-30 23:17
 * @description:
 **/
public class WordUtil {
    private static JiebaSegmenter mSegment = new JiebaSegmenter();

    private static List<String> cut(String seq) {
        List<SegToken> segTokenList = mSegment.process(seq, JiebaSegmenter.SegMode.SEARCH);
        List<String> strings = new ArrayList<>();
        for (SegToken segToken : segTokenList) {
            strings.add(segToken.word);
        }
        return strings;
    }

    public static List<String> cutSpecial(String str) {
        List<String> list = cut(str);
        List<String> result = new ArrayList<>();
        String regex = "[\" ()]";
        Pattern pattern = Pattern.compile(regex);
        boolean add = false;
        String tem = "";
        for (String s : list) {
            if (str.matches(regex) || strMatch(s)) {
                add = !add;
                if (!add)
                    result.add(tem);
                result.add(s);
                continue;
            }
            if (add) {
                tem = tem + s;
            } else {
                result.add(s);
                tem = "";
            }

        }
        return result;
    }

    private static boolean strMatch(String str) {
        return str.equals("”") || str.equals("“") || str.equals("（") || str.equals("）");
    }
}


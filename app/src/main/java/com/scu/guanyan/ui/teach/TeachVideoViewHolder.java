package com.scu.guanyan.ui.teach;

import android.view.View;
import androidx.annotation.NonNull;
import com.scu.guanyan.R;
import com.scu.guanyan.base.RecyclerViewHolder;
import com.scu.guanyan.bean.TeachVideo;
import org.jetbrains.annotations.NotNull;

/**
 * @program: Guanyan
 * @author: 陈博文
 * @create: 2022-11-12 16:37
 * @description:
 **/
public class TeachVideoViewHolder extends RecyclerViewHolder {
    public TeachVideoViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
    }

    @Override
    public void bind(Object data) {
        TeachVideo video = (TeachVideo) data;
        // 待写,绑定view
        setText(R.id.title, ((TeachVideo) data).getTitle());
    }
}

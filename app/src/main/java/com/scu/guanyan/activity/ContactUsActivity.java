package com.scu.guanyan.activity;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.scu.guanyan.R;
import com.scu.guanyan.base.BaseActivity;

/**
 * @program: Guanyan
 * @author: cbw
 * @create: 2023/2/17 11:51
 * @description:
 **/
public class ContactUsActivity extends BaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_contact;
    }

    @Override
    protected void initView() {
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.view_on_github).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://github.com/curryc/GuanYan"));
                startActivity(i);
            }
        });

        findViewById(R.id.email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto",
                        "currycake@163.com", null));
                i.putExtra(Intent.EXTRA_SUBJECT, "About Guanyan");
                i.putExtra(Intent.EXTRA_TEXT, "Your report here...");
                startActivity(Intent.createChooser(i, "Contact Developers"));
            }
        });

    }


}

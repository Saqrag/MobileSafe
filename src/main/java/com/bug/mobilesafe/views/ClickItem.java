package com.bug.mobilesafe.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bug.mobilesafe.R;

/**
 * Created by saqra on 2016/2/15.
 */
public class ClickItem extends RelativeLayout {
    static final String NAME_SPACE = "http://schemas.android.com/apk/res-auto";

    //用xml文件定义的属性值
    String mTitle;
    String mDesc;

    Boolean mChecked;

    //item里的控件
    TextView tvTitle;
    TextView tvDesc;
    CheckBox cbChecked;


    public ClickItem(Context context) {
        super(context);
        initView();
    }

    public ClickItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTitle = attrs.getAttributeValue(NAME_SPACE, "title");
        mDesc = attrs.getAttributeValue(NAME_SPACE, "desc");
        initView();
        setView();
    }

    public ClickItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View.inflate(getContext(), R.layout.item_click, this);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvDesc = (TextView) findViewById(R.id.tv_desc);
    }

    //用xml属性值初始化自定义控件
    private void setView() {
        if (!TextUtils.isEmpty(mTitle)) {
            tvTitle.setText(mTitle);
        }

        if (!TextUtils.isEmpty(mDesc)) {
                tvDesc.setText(mDesc);

        }
    }

    /**
     * 代码设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            mTitle = title;
            tvTitle.setText(title);
        }
    }
    public void setDesc(String desc) {
        if (!TextUtils.isEmpty(desc)) {
            mDesc = desc;
            tvDesc.setText(desc);
        }
    }

}


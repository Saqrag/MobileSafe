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
 * Created by saqra on 2016/1/29.
 */
public class CheckBoxItem extends RelativeLayout {
    static final String NAME_SPACE = "http://schemas.android.com/apk/res-auto";

    //用xml文件定义的属性值
    String mTitle;
    String mDescOn;
    String mDescOff;
    Boolean mChecked;

    //item里的控件
    TextView tvTitle;
    TextView tvDesc;
    CheckBox cbChecked;


    public CheckBoxItem(Context context) {
        super(context);
        initView();
    }

    public CheckBoxItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTitle = attrs.getAttributeValue(NAME_SPACE, "title");
        mDescOn = attrs.getAttributeValue(NAME_SPACE, "desc_on");
        mDescOff = attrs.getAttributeValue(NAME_SPACE, "desc_off");
        mChecked = attrs.getAttributeBooleanValue(NAME_SPACE, "checked", true);
        initView();
        setView();
    }

    public CheckBoxItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View.inflate(getContext(), R.layout.item_check_box, this);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvDesc = (TextView) findViewById(R.id.tv_desc);
        cbChecked = (CheckBox) findViewById(R.id.cb_checked);
    }

    //用xml属性值初始化自定义控件
    private void setView() {
        if (!TextUtils.isEmpty(mTitle)) {
            tvTitle.setText(mTitle);
        }

        if (mChecked != null) {
            if (mChecked) {
                cbChecked.setChecked(mChecked);
                tvDesc.setText(mDescOn);
            } else {
                cbChecked.setChecked(mChecked);
                tvDesc.setText(mDescOff);
            }
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

    public void setDescAndCheckBox(String descOn, String descOff) {
        if (!TextUtils.isEmpty(descOff)) {
            mDescOff = descOff;
        }

        if (!TextUtils.isEmpty(descOn)) {
            mDescOn = descOn;
        }
    }

    public void setChecked(Boolean checked) {
        if (checked != null) {
            mChecked = checked;
            if (mChecked) {
                tvDesc.setText(mDescOn);
                cbChecked.setChecked(mChecked);
            } else {
                tvDesc.setText(mDescOff);
                cbChecked.setChecked(mChecked);
            }
        }
    }

    /**
     * 判断是否选中复选框
     * @return
     */
    public Boolean isChecked(){
        return mChecked;
    }

}

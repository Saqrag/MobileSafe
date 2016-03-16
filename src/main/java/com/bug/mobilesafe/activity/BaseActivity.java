package com.bug.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by saqra on 2016/2/1.
 */
public abstract class BaseActivity extends FragmentActivity{

    private GestureDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFling();
    }
    /**
     * 滑动屏幕事件
     */
    private void setFling() {

        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            /**
             *
             * @param e1 开始位置
             * @param e2 结束位置
             * @param velocityX X方向的速度
             * @param velocityY Y方向的位置
             * @return
             */
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e2.getRawY() - e1.getRawY() > 150 || e1.getRawY() - e2.getRawY() > 150) {
                    Toast.makeText(BaseActivity.this, "您滑动的有点偏离哦", Toast.LENGTH_SHORT).show();
                    return true;
                }

                if (e2.getRawX() - e1.getRawX() > 200) {
//                    System.out.println("向右滑动");
                    showPreviousPage();
                }

                if (e1.getRawX() - e2.getRawX() > 200) {
                    showNextPage();
                    // System.out.println("向左滑动");
                }

                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }


    /**
     * 进入下一个Activity
     */
    public abstract void showNextPage();

    /**
     * 返回上一个Activity
     */
    public abstract void showPreviousPage();

    /**
     * 系统调用的Next按钮的onClick方法
     * @param view
     */
    public void next(View view){
        showNextPage();
    }

    /**
     * 系统调用的previous按钮的onClick方法
     * @param view
     */
    public void previous(View view){
        showPreviousPage();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);//托付给GestureDetector处理事件;
        return super.onTouchEvent(event);
    }

    /**
     * 监听手机的返回键
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0){
            showPreviousPage();
        }
        return super.onKeyDown(keyCode, event);
    }
}

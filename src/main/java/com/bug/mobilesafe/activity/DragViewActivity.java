package com.bug.mobilesafe.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bug.mobilesafe.R;
import com.bug.mobilesafe.utils.PageUtils;
import com.bug.mobilesafe.utils.ShareUtil;

/**
 * Created by saqra on 2016/2/17.
 */
public class DragViewActivity extends BaseActivity{

    private ImageView ivToast;
    private int startX;
    private int startY;
    private SharedPreferences sPre;
    private int mWidth;
    private int mHeight;
    long[] mHits = new long[2];// 数组长度表示要点击的次数
    private TextView tvUp;
    private TextView tvDown;

    @Override
    public void showNextPage() {

    }

    @Override
    public void showPreviousPage() {
        PageUtils.previousPage(this,SettingActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_view);
        sPre = getSharedPreferences(ShareUtil.CONFIG, MODE_PRIVATE);

        //获得屏幕的宽高
        mWidth = getWindowManager().getDefaultDisplay().getWidth();
        mHeight = getWindowManager().getDefaultDisplay().getHeight();

//        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    private void initView() {
        ivToast = (ImageView) findViewById(R.id.iv_toast);
        int lastX = sPre.getInt(ShareUtil.LAST_X, 0);
        int lastY = sPre.getInt(ShareUtil.LAST_Y, 0);

        RelativeLayout.LayoutParams layoutParams= (RelativeLayout.LayoutParams) ivToast.getLayoutParams();
        //设置参数
        layoutParams.leftMargin=lastX;
        layoutParams.topMargin=lastY;

        ivToast.setLayoutParams(layoutParams);//重新设置
        ivToast.setOnTouchListener(new MyTouchListener());

        tvUp = (TextView) findViewById(R.id.tv_up);
        tvDown = (TextView) findViewById(R.id.tv_down);

        //隐藏提示语
        if (lastY<mHeight/2){
            tvUp.setVisibility(View.INVISIBLE);
            tvDown.setVisibility(View.VISIBLE);
        }else {
            tvDown.setVisibility(View.INVISIBLE);
            tvUp.setVisibility(View.VISIBLE);
        }
    }

    class MyTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN://按下屏幕

                    //记录开始位置
                    startX = (int) event.getRawX();
                    startY = (int) event.getRawY();
                    break;

                case MotionEvent.ACTION_MOVE:

                    //记录最终位置
                    int endX= (int) event.getRawX();
                    int endY= (int) event.getRawY();

                    //计算差值
                    int dx=endX-startX;
                    int dy=endY-startY;

                    //更新距离
                    int l=ivToast.getLeft()+dx;
                    int r=ivToast.getRight()+dx;

                    int t=ivToast.getTop()+dy;
                    int b=ivToast.getBottom()+dy;

                    //限制屏幕之内拖动
                    if (l<0||t<0||r>mWidth||b>mHeight-40){
                        break;
                    }
                    

                    //隐藏提示语
                    if (ivToast.getTop()<mHeight/2){
                        tvUp.setVisibility(View.INVISIBLE);
                        tvDown.setVisibility(View.VISIBLE);
                    }else {
                        tvDown.setVisibility(View.INVISIBLE);
                        tvUp.setVisibility(View.VISIBLE);
                    }

                    //更新位置
                    ivToast.layout(l,t,r,b);

                    //重新初始化开始位置
                    startX = (int) event.getRawX();
                    startY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:

                    //保存位置
                    SharedPreferences.Editor edit = sPre.edit();
                    edit.putInt(ShareUtil.LAST_X,ivToast.getLeft());
                    edit.putInt(ShareUtil.LAST_Y,ivToast.getTop());
                    edit.putBoolean(ShareUtil.IS_TOAST_CENTRE,false);
                    edit.commit();
                    break;

            }
            return false;//不拦截事件所以onclick事件相应
        }
    }

    public void centre(View v){

        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();// 开机后开始计算的时间
        if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
//            System.out.println("双击");
            // 把图片居中
            ivToast.layout(
                    mWidth/2-ivToast.getWidth()/2,
                    mHeight/2-ivToast.getHeight()/2,
                    mWidth/2+ivToast.getWidth()/2,
                    mHeight/2+ivToast.getHeight()/2);

            //保存位置
            SharedPreferences.Editor edit = sPre.edit();
            edit.putInt(ShareUtil.LAST_X,ivToast.getLeft());
            edit.putInt(ShareUtil.LAST_Y,ivToast.getTop());
            edit.putBoolean(ShareUtil.IS_TOAST_CENTRE,true);
            edit.commit();
        }
    }
}

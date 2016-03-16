package com.bug.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bug.mobilesafe.R;
import com.bug.mobilesafe.adapter.MyAdapter;
import com.bug.mobilesafe.bean.BlackListBean;
import com.bug.mobilesafe.database.BlacklistDB;
import com.bug.mobilesafe.utils.PageUtils;

import java.util.List;

/**
 * Created by saqra on 2016/2/19.
 */
public class CallSafeActivity extends BaseActivity {

    private ListView lvCallSafe;
    private List<BlackListBean> listBeans;
    private BlacklistDB db;
    private View proBar;
    private int toalCount;
    private int startPosition = 0;//分页查询的初始开始查找位置
    private int maxCoutnt = 20;
    boolean state = true;//避免用户重复滑动已经到底的list
    private EditText et_name;
    private EditText et_number;
    private CheckBox cb_call;
    private CheckBox cb_sms;
    private AlertDialog dialog;

    @Override
    public void showNextPage() {

    }

    @Override
    public void showPreviousPage() {
        PageUtils.previousPage(this, HomeActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_safe);
        initParams();
        getTotalCount();
        queryList();
    }

    private MyBaseAdapter myBaseAdapter;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (myBaseAdapter == null) {
                    myBaseAdapter = new MyBaseAdapter(listBeans);
                    lvCallSafe.setAdapter(myBaseAdapter);
                } else {
                    myBaseAdapter.notifyDataSetChanged();
                }
                proBar.setVisibility(View.INVISIBLE);
                state = true;

            }
        }
    };

    private void queryList() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                if (listBeans == null || listBeans.size() == 0) {
                    listBeans = db.queryPage(maxCoutnt, startPosition);
                } else {
                    listBeans.addAll(db.queryPage(maxCoutnt, startPosition));
                }
                handler.sendEmptyMessage(0);
            }
        }.start();

    }


    private void getTotalCount() {
        toalCount = db.getToalCount();
    }

    private void initParams() {
        lvCallSafe = (ListView) findViewById(R.id.lv_call_safe);
        proBar = findViewById(R.id.ll_pro_bar);
        db = new BlacklistDB(this);
        lvCallSafe.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_IDLE://listview静止状态

                        int lastVisiblePosition = lvCallSafe.getLastVisiblePosition();
                        if (lastVisiblePosition == listBeans.size() - 1) {//表示已经滑到最后位置
                            if (!state) {
//                                Toast.makeText(CallSafeActivity.this, "别急客官,马上", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            state = false;
                            if (listBeans.size() >= toalCount) {
                                Toast.makeText(CallSafeActivity.this, "已经没有数据了", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            startPosition += maxCoutnt;
                            proBar.setVisibility(View.VISIBLE);
                            queryList();

                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private class MyBaseAdapter extends MyAdapter {
        public MyBaseAdapter(List list) {
            super(list);
        }

        @Override
        public int getCount() {
            return listBeans.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                convertView = View.inflate(CallSafeActivity.this, R.layout.item_call_safe, null);

                holder = new Holder();
                holder.tvName = (TextView) convertView.findViewById(R.id.tv_contact_name);
                holder.tvNumber = (TextView) convertView.findViewById(R.id.tv_contact_num);
                holder.ivDelete = (ImageView) convertView.findViewById(R.id.iv_delete);
                holder.tvMode = (TextView) convertView.findViewById(R.id.tv_mode);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            final BlackListBean listBean = listBeans.get(position);
            holder.tvName.setText(listBean.getName());
            holder.tvNumber.setText(listBean.getNumber());
            String mode = listBean.getMode();
            if (mode.equals("0")) {
                holder.tvMode.setText("电话拦截+短信拦截");
            } else if (mode.equals("1")) {
                holder.tvMode.setText("电话拦截");
            } else if (mode.equals("2")) {
                holder.tvMode.setText("短信拦截");
            }

            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    proBar.setVisibility(View.VISIBLE);
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            int delete = db.delete(listBean.getNumber());
                            if (delete != 0) {
                                listBeans.remove(listBean);
                                startPosition -= 1;
                                getTotalCount();
                                if (listBeans.size() == 0) {//如果listview里的数据都删完,重新加载
                                    queryList();
                                } else {
                                    handler.sendEmptyMessage(0);
                                }

                            } else {

                            }
                        }
                    }.start();


                }
            });
            return convertView;
        }

        class Holder {
            private TextView tvName;
            private TextView tvNumber;
            private ImageView ivDelete;
            private TextView tvMode;
        }
    }

    /**
     * 添加列表按钮单击事件
     * @param view
     */
    public void addList(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog = builder.create();
        View view1 = View.inflate(this, R.layout.item_add_list, null);

        et_name = (EditText) view1.findViewById(R.id.et_name);
        et_number = (EditText) view1.findViewById(R.id.et_number);
        cb_call = (CheckBox) view1.findViewById(R.id.cb_call);
        cb_sms = (CheckBox) view1.findViewById(R.id.cb_sms);
        Button bn_add = (Button) view1.findViewById(R.id.bn_add);
        Button bn_cancel = (Button) view1.findViewById(R.id.bn_cancel);
        Button bn_content = (Button) view1.findViewById(R.id.bn_content);


        MyOnClickListener myOnClickListener = new MyOnClickListener();
        bn_add.setOnClickListener(myOnClickListener);
        bn_cancel.setOnClickListener(myOnClickListener);
        bn_content.setOnClickListener(myOnClickListener);

//        dialog.setView(view1);
        dialog.setView(view1,0,0,0,0);//兼容2.x的版本
        dialog.show();
    }

    private class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bn_add:
                    String name = et_name.getText().toString().trim();
                    String number = et_number.getText().toString().trim();
                    boolean call = cb_call.isChecked();
                    boolean sms = cb_sms.isChecked();
                    String mode="0";
                    if (!TextUtils.isEmpty(number)) {
                        if (number.matches("^\\d+$")) {//匹配数字

                            if (call && sms) {
                                mode="0";
                            } else if (call) {
                                mode="1";
                            } else if (sms) {
                                mode="2";
                            } else {
                                Toast.makeText(CallSafeActivity.this, "拦截模式至少选一个,亲", Toast.LENGTH_SHORT).show();
                                return;
                            }


                        } else {
                            Toast.makeText(CallSafeActivity.this, "电话号码必需为数字", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        Toast.makeText(CallSafeActivity.this, "电话号码不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(name)){
                        name="未知联系人";
                    }
                    db.add(number,name,mode);
                    BlackListBean listBean=new BlackListBean(name,number,mode);
                    listBeans.add(0,listBean);
                    myBaseAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                    break;
                case R.id.bn_cancel:
                    dialog.dismiss();
                    break;
                case R.id.bn_content:
                    CallSafeActivity.this.startActivityForResult(
                            new Intent(CallSafeActivity.this, ContactActivity.class), 1);
                    break;

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode== Activity.RESULT_OK){
            String phone = data.getExtras().getString("phone");
            String name = data.getExtras().getString("name");
            et_number.setText(phone);
            et_name.setText(name);
        }
    }
}

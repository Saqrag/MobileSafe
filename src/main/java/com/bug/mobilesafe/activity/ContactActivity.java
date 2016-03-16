package com.bug.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.bug.mobilesafe.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by saqra on 2016/2/1.
 */
public class ContactActivity extends BaseActivity{

    private ArrayList<HashMap<String,String>> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        ListView lvContent= (ListView) findViewById(R.id.lvContent);
        getContact();
        lvContent.setAdapter(new SimpleAdapter(this, arrayList, R.layout.item_list_contact,
                new String[]{"name", "phone"}, new int[]{R.id.tv_contact_name, R.id.tv_contact_num}));
        lvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String phone=arrayList.get(position).get("phone");
                String name = arrayList.get(position).get("name");
                Intent intent=new Intent();
                intent.putExtra("phone",phone);
                intent.putExtra("name",name);
                setResult(Activity.RESULT_OK, intent);
                finish();
                ContactActivity.this.overridePendingTransition(R.anim.previous_in,R.anim.previous_out);
            }
        });
    }

    /**
     * 进入下一个Activity
     */
    @Override
    public void showNextPage() {

    }

    /**
     * 返回上一个Activity
     */
    @Override
    public void showPreviousPage() {
        finish();
        overridePendingTransition(R.anim.previous_in,R.anim.previous_out);
    }

    private void getContact(){
        arrayList = new ArrayList();
        Uri rawContentUri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri dataUri = Uri.parse("content://com.android.contacts/data");
        Cursor rawContactId = getContentResolver().query(
                rawContentUri, new String[]{"contact_id"}, null, null, null);
        if (rawContactId!=null){
            while(rawContactId.moveToNext()){
                HashMap<String,String> hashMap=new HashMap<>();
                String id = rawContactId.getString(0);
//                System.out.println("rawContactId:"+rawContactIdInt);
                Cursor data = getContentResolver().query(dataUri, new String[]{"mimetype", "data1"},
                        "contact_id=?", new String[]{id}, null);
//                System.out.println("-----------------------------------------------------------------");
                if (data!=null){
                    while(data.moveToNext()){
                        String mimetype = data.getString(data.getColumnIndex("mimetype"));
                        String data1 = data.getString(data.getColumnIndex("data1"));
//                        System.out.println("mimetype:"+mimetype+";-------------------;"+"data1:"+data1);
                        if ("vnd.android.cursor.item/name".equals(mimetype)) {
                            hashMap.put("name",data1);
                        }else if ("vnd.android.cursor.item/phone_v2".equals(mimetype)){
                            hashMap.put("phone",data1);
                        }

                    }
                }
//                System.out.println(hashMap.get("name"));

                arrayList.add(hashMap);
                data.close();
            }
        }
        rawContactId.close();
    }

}


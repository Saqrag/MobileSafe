package com.bug.mobilesafe;

import android.content.Context;
import android.test.AndroidTestCase;

import com.bug.mobilesafe.bean.BlackListBean;
import com.bug.mobilesafe.database.BlacklistDB;
import com.bug.mobilesafe.engine.AppInfoProvider;

import java.util.List;
import java.util.Random;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends AndroidTestCase {

    private Context mContext;
    private BlacklistDB db;

    @Override
    protected void setUp() throws Exception {
        mContext = getContext();
        db = new BlacklistDB(mContext);
        super.setUp();
    }

    public void testAdd() {
        for (int i = 0; i <= 100; i++) {
            db.add(13000000000l + i + "", "好人" + i, String.valueOf(new Random().nextInt(3) + ""));
        }

    }

    public void testDelete() {
        int delete = db.delete("1300000000");
        System.out.println(delete);
    }

    public void testQueryAll() {
        List<BlackListBean> list = db.queryPage(20, 0);
        for (BlackListBean bean:list) {
            System.out.println(bean.getNumber());
        }
    }

    public void testQuery(){
        String query = db.query("1300000001");
        System.out.println(query);
    }

    public void testTotalCount(){
        System.out.println(db.getToalCount());
    }

    public void test(){
        AppInfoProvider.getAppInfos(getContext());
    }
}
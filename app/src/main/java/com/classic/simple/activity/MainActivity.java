package com.classic.simple.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import butterknife.Bind;
import com.classic.adapter.BaseAdapterHelper;
import com.classic.adapter.CommonRecyclerAdapter;
import com.classic.core.log.Logger;
import com.classic.core.utils.DoubleClickExitHelper;
import com.classic.core.utils.SDcardUtil;
import com.classic.core.utils.ToastUtil;
import com.classic.simple.R;
import com.classic.simple.model.Demo;
import java.util.List;

/**
 * 通用适配器示例By RecyclerView
 */
public class MainActivity extends AppBaseActivity {
    @Bind(R.id.main_rv) RecyclerView mRecyclerView;

    private List<Demo> mDemos;
    private DoubleClickExitHelper mDoubleClickExitHelper;
    private DemoAdapter mAdapter;

    @Override public int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override public void onFirst() {
        super.onFirst();
        Logger.d("onFirst只有第一次才会执行");
        //这里可以做一些界面功能引导
    }

    /**
     * 方法执行顺序：
     * initData() --> initView() --> register()
     */
    @Override public void initData() {
        super.initData();
        mDemos = Demo.getDemos();
        //双击退出应用工具类使用方法，别忘了重写onKeyDown方法（见底部）
        mDoubleClickExitHelper = new DoubleClickExitHelper(this);
        //mDoubleClickExitHelper = new DoubleClickExitHelper(this)
        //.setTimeInterval(3000)
        //.setToastContent("再按一次退出demo");
    }

    /**
     * 方法执行顺序：
     * initData() --> initView() --> register()
     */
    @Override public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mRecyclerView.setOnClickListener(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new DemoAdapter(activity, R.layout.activity_main_item, mDemos);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new CommonRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder viewHolder, View view, int position) {
                itemClick(mDemos.get(position));
            }
        });
    }

    private class DemoAdapter extends CommonRecyclerAdapter<Demo> {

        public DemoAdapter(Context context, int layoutResId, List<Demo> data) {
            super(context, layoutResId, data);
        }

        @Override public void onUpdate(BaseAdapterHelper helper, Demo item, int position) {
            final CardView cardView = helper.getView(R.id.main_item_cardview);
            cardView.setCardBackgroundColor(item.bgColor);
            helper.setText(R.id.main_item_tv, item.title);
        }
    }

    @Override public void register() {
        super.register();
        //这里可以注册一些广播、服务
    }

    @Override public void unRegister() {
        super.unRegister();
        //注销广播、服务
    }

    private void itemClick(Demo demo) {
        switch (demo.type) {
            case Demo.TYPE_ADAPTER:
                startActivity(MainActivity.this, ListViewActivity.class);
                break;
            case Demo.TYPE_CRASH:
                crashTest();
                break;
            case Demo.TYPE_EVENT:
                startActivity(MainActivity.this, EventBusActivity.class);
                break;
            case Demo.TYPE_SPLASH:
                startActivity(MainActivity.this, SplashActivity.class);
                break;
            case Demo.TYPE_FRAGMENT:
                startActivity(MainActivity.this, FragmentActivity.class);
                break;
        }
    }

    private void crashTest() {
        ToastUtil.showLongToast(this, "程序即将崩溃，崩溃日志请查看：" + SDcardUtil.getLogDirPath());
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                throw new NullPointerException("666");
            }
        }, 3000);
    }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mDoubleClickExitHelper.onKeyDown(keyCode, event);
    }
}

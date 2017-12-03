package com.example.cloudy.cloudyvideo.pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.cloudy.cloudyvideo.R;
import com.example.cloudy.cloudyvideo.base.BasePager;
import com.example.cloudy.cloudyvideo.utils.Constants;
import com.example.cloudy.cloudyvideo.utils.LogUtil;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

public class RecommendPager extends BasePager {

    @ViewInject(R.id.listview)
    private ListView mListview;

    @ViewInject(R.id.tv_nonet)
    private TextView mTv_nonet;

    @ViewInject(R.id.pb_loading)
    private ProgressBar mProgressBar;

    public RecommendPager(Context context) {
        super(context);
    }

    /**
     * 初始化当前页面的控件，由父类调用
     * @return
     */
    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.netvideo_pager,null);
        //第一个参数是RecommendPager.this,第二个是布局
        x.view().inject(this,view);
        return view;
    }


    @Override
    public void initData() {
        super.initData();
        LogUtil.e("推荐的数据被初始化");
        //联网
        //视频内容
        RequestParams params = new RequestParams(Constants.NET_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("联网成功==" + result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("联网失败==" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled==" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished==");
            }
        });
    }
}

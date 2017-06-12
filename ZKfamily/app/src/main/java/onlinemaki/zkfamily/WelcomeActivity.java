package onlinemaki.zkfamily;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WelcomeActivity extends Activity {
    private TextView mTvBtnNetAgain;//再次获取网络按钮
    private RelativeLayout mRlytNoNetWork;//没有网络页面
    private LinearLayout mLytWelcome;//欢迎页面布局
    protected UtilActivity mBaseUtilAty = new UtilActivity(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mTvBtnNetAgain=(TextView)findViewById(R.id.net_again);
        mTvBtnNetAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onGetNetWorkClick(view);
            }
        });
        mRlytNoNetWork=(RelativeLayout)findViewById(R.id.rlyt_nonetwork);
        mLytWelcome=(LinearLayout)findViewById(R.id.lyt_welcome);
        if(!UtilCommon.isNetworkAvailable(this))
        {
            mLytWelcome.setVisibility(View.GONE);
            mRlytNoNetWork.setVisibility(View.VISIBLE);
            UtilToast.show(this, "当前网络不可用，请检查网络连接！", Toast.LENGTH_SHORT);
            return;
        }
        isFirstRun();
    }
    @Override
    protected void onResume() {
        super.onResume();

    }
    private void startWebHome()
    {
        WebView webview;
        //实例化WebView对象
        webview = new WebView(WelcomeActivity.this);
        //设置WebView属性，能够执行Javascript脚本
        webview.getSettings().setJavaScriptEnabled(true);
        //加载需要显示的网页
        webview.loadUrl(UtilCommon.SERVER_URL);
        //设置Web视图
        setContentView(webview);
    }
    /**
     * 暂停1.5秒进入登陆页面
     *
     * @data 2013-5-28 上午9:44:54
     * @author NiuFC
     * @version 1.0
     */
    private void startHome(final boolean isStart) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (isStart) {
                    mBaseUtilAty.startActivity(HomeActivity.class);
                    //startWebHome();
                } else {
                    //Intent intent = new Intent();
                    //intent.setClass(WelcomeActivity.this, HomeActivity.class);
                    //startActivity(intent);
                   // mBaseUtilAty.startActivity(HomeActivity.class);
                }
                WelcomeActivity.this.finish();
            }
        }.start();
    }
    /**
     * 暂停1.5秒进入引导页面
     *
     * @data 2013-5-28 上午9:44:54
     * @author NiuFC
     * @version 1.0
     */
    private void startNavi(final boolean isStart) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (isStart) {
                   // Intent intent = new Intent();
                   // intent.setClass(WelcomeActivity.this, WelcomeNavigationActivity.class);
                   // startActivity(intent);
                    mBaseUtilAty.startActivity(WelcomeNavigationActivity.class);
                }
                WelcomeActivity.this.finish();
            }
        }.start();
    }

    public void isFirstRun() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("share", MODE_PRIVATE);
        //判断是否是第一次运行，如果不是第一次则用true标记，并写入isFirstRun标记，下次启动程序时做判断
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isFirstRun) {
            startNavi(true);
            editor.putBoolean("isFirstRun", false);
            editor.commit();
        } else {
            startHome(true);

        }
    }

    //点击立即重试，再次获取网络，当前有可用网络时隐藏没有网络提示的界面，进入首页
  //  @OnClick(R.id.net_again)
    public void onGetNetWorkClick(View v) {
        if (UtilCommon.isNetworkAvailable(this)) {
//            UtilToast.show(getApplicationContext(), "当前有可用网络！", Toast.LENGTH_SHORT);
            mLytWelcome.setVisibility(View.VISIBLE);
            mRlytNoNetWork.setVisibility(View.GONE);
            mBaseUtilAty.startActivity(HomeActivity.class);
            finish();
        } else {
            UtilToast.show(getApplicationContext(), R.string.home_examine, Toast.LENGTH_SHORT);
        }
    }
}

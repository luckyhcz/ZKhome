package onlinemaki.zkfamily;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class WelcomeNavigationActivity extends Activity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private ViewPager mViewPager;

    private LinearLayout mLinearLayout;

    private Button mBtnBottom;

    private List<View> mListView;
    private int[] mImages = {R.drawable.img_welcome_one, R.drawable.img_welcome_two, R.drawable.img_welcome_three};
    private AdapterNavigation mAdapterNavi;
    private ImageView[] mPoint;
    private int mCurrIndex;//当前页下标

    //滑动
    private int mLastValut = -1;
    private boolean mIsScrolling = false;
    //判断只走一次
    private int mCount = 0;
    protected UtilActivity mBaseUtilAty = new UtilActivity(this);
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_navigation);
      //  ViewUtils.inject(this);

        initView();
        initData();
        initPoint();
    }
    private void startHome()
    {
        WebView webview;
        //实例化WebView对象
        webview = new WebView(WelcomeNavigationActivity.this);
        //设置WebView属性，能够执行Javascript脚本
        webview.getSettings().setJavaScriptEnabled(true);
        //加载需要显示的网页
        webview.loadUrl(UtilCommon.SERVER_URL);
        //设置Web视图
        setContentView(webview);
    }
    //初始化控件
    private void initView() {
        mViewPager=(ViewPager)findViewById(R.id.vp_view);
        mLinearLayout=(LinearLayout)findViewById(R.id.lyt_bottom);
        mBtnBottom=(Button)findViewById(R.id.btn_welcome);
        mBtnBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent();
              //  intent.setClass(WelcomeNavigationActivity.this, HomeActivity.class);
              //  startActivity(intent);
                //实例化WebView对象
               // startHome();
                mBaseUtilAty.startActivity(HomeActivity.class);
                WelcomeNavigationActivity.this.finish();
            }
        });

        mBtnBottom.setVisibility(View.GONE);
        mListView = new ArrayList<View>();
        mViewPager.addOnPageChangeListener(this);
    }

    private void initPoint() {
        mPoint = new ImageView[mImages.length];
        for (int i = 0; i < mLinearLayout.getChildCount(); i++) {
            mPoint[i] = (ImageView) mLinearLayout.getChildAt(i);
            mPoint[i].setImageResource(R.drawable.circle_gray);
            mPoint[i].setOnClickListener(this);
            mPoint[i].setTag(i);
        }
        mCurrIndex = 0;
        mPoint[mCurrIndex].setImageResource(R.drawable.circle_red);
    }

    //初始化数据
    private void initData() {
        for (int i : mImages) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(i);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            mListView.add(imageView);
        }
        mAdapterNavi = new AdapterNavigation(mListView);
        mViewPager.setAdapter(mAdapterNavi);
    }

    @Override
    public void onClick(View v) {
        int number = (Integer) v.getTag();
        mViewPager.setCurrentItem(number);
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
        if (mIsScrolling) {
            if (mLastValut == i2 && i == mListView.size() - 1 && mCount == 0) {
                mCount = 1;
            }
        }
        mLastValut = i2;
    }

    @Override
    public void onPageSelected(int i) {
        sdsd(i);
        mCount = 0;

        if (i == 2) {
            mBtnBottom.setVisibility(View.VISIBLE);
        } else {
            mBtnBottom.setVisibility(View.GONE);
        }
    }

    //底部小点
    private void sdsd(int ss) {
        for (int i = 0; i < mLinearLayout.getChildCount(); i++) {
            mPoint[i] = (ImageView) mLinearLayout.getChildAt(i);
            mPoint[i].setImageResource(R.drawable.circle_gray);
        }
        mPoint[ss].setImageResource(R.drawable.circle_red);
    }

    @Override
    public void onPageScrollStateChanged(int i) {
        if (i == 1) {
            mIsScrolling = true;
        } else {
            mIsScrolling = false;
        }
    }

  ///  @OnClick(R.id.btn_welcome)
    public void onBtnBottomClick(View v){
      //  LogUtils.e("==============>"+UtilCommon.gPSIsOPen(this));
        Intent intent = new Intent();
        intent.setClass(WelcomeNavigationActivity.this, HomeActivity.class);
        startActivity(intent);
        WelcomeNavigationActivity.this.finish();
    }


}

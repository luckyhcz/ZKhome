package onlinemaki.zkfamily;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
/**
 * Created by chengzhong.huang on 2017/6/12.
 */
public class HomeActivity extends Activity {
    private WebView webview;
    private int backClickCount=0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //实例化WebView对象
        webview = new WebView(this);
        //设置WebView属性，能够执行Javascript脚本
        webview.getSettings().setJavaScriptEnabled(true);
        //加载需要显示的网页
        webview.loadUrl(UtilCommon.SERVER_URL);

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
            //@Override
          //  public void onPageFinished(WebView view, String url) {
                // TODO 自动生成的方法存根


           // }

        });
        //设置Web视图
        setContentView(webview);
       // finish();
    }

    @Override
    //设置回退
    //覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack(); //goBack()表示返回WebView的上一页面
            backClickCount=0;
            return true;
        }
        if(backClickCount==0)
        {
            UtilToast.show(this,"再按一次退出周康之家");
            backClickCount++;
            new Thread()
            {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    backClickCount=0;//2 S  reset
            }

            }.start();
        }
        else  if(backClickCount>0) {
            finish();
            backClickCount=0;
        }
        return false;
    }

}

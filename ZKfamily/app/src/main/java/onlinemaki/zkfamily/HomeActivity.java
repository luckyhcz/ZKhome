package onlinemaki.zkfamily;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.webkit.WebChromeClient;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chengzhong.huang on 2017/6/12.
 */
public class HomeActivity extends Activity implements OnClickListener {
    private WebView webview;
    private int backClickCount=0;
    private View inflate;
    private TextView choosePhoto;
    private TextView takePhoto;
    private Dialog dialog;
    private ValueCallback<Uri> mUploadMessage;// 表单的数据信息
    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private final static int FILECHOOSER_RESULTCODE = 1;// 表单的结果回调</span>
    private Uri imageUri;
    private boolean dismissByClick=false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //实例化WebView对象
        webview = new WebView(this);
        WebSettings webSettings = webview.getSettings();
        //设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setGeolocationEnabled(true);
        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        webSettings.setGeolocationDatabasePath(dir);
        webSettings.setDatabasePath(dir);
        webSettings.setDomStorageEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        webSettings.setBlockNetworkImage(false);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= 11)
        {
            webSettings.setBuiltInZoomControls(true);
            webSettings.setDisplayZoomControls(false);
        }
        //加载需要显示的网页
        webview.loadUrl(UtilCommon.SERVER_URL);

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("zkfamily","shouldOverrideUrlLoading url == "+url);

                if (url.startsWith("tel:")) {
                    return handleCall(url);
                }
                else if (url.startsWith("sms:")) {

                    return handleSMS(url);
                }
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url);
                } //屏蔽掉错误的重定向url："baidumap://map/?src=webapp.default.all.callnaonopenwebapp?"
                return true;//super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                super.onPageStarted(view, url, favicon);
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onPageFinished(view, url);
            }

        });
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback)
            {
                Log.e("zkfamily","onGeolocationPermissionsShowPrompt origin == "+origin);
                Log.e("zkfamily","onGeolocationPermissionsShowPrompt callback == "+callback);
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }
            @Override
            public boolean onShowFileChooser(WebView webView,
                                             ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {
                super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
                mUploadCallbackAboveL=filePathCallback;
               // take();
                popupDialog();
               // PickPhotoUtil.mFilePathCallback = filePathCallback;
                return true;
            }


            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage=uploadMsg;
               // take();
                popupDialog();
            }
            public void openFileChooser(ValueCallback<Uri> uploadMsg,String acceptType) {
                mUploadMessage=uploadMsg;
                //take();
                popupDialog();
            }
            public void openFileChooser(ValueCallback<Uri> uploadMsg,String acceptType, String capture) {
                mUploadMessage=uploadMsg;
               // take();
                popupDialog();
               // PickPhotoUtil.mFilePathCallback4 = filePathCallback;
            }
        });

//设置支持js调用java
       // webview.addJavascriptInterface(new AndroidAndJSInterface(), "Android");
        //设置Web视图
        setContentView(webview);
        requestPermisstion();
       // finish();
    }
    /**
     * 调起系统发短信功能
     * @param phoneNumber
     * @param message
     */
    public void doSendSMSTo(String phoneNumber,String message){
        if(PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)){
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+phoneNumber));
            intent.putExtra("sms_body", message);
            startActivity(intent);
        }
    }

    private boolean handleCall(String url)
    {
        if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 0x11);

            return true;
        }
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
        Log.e("zkfamily","shouldOverrideUrlLoading intent == "+intent);
        HomeActivity.this.startActivity(intent);
        return true;
    }
    private boolean handleSMS(String oldurl)
    {
           if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.SEND_SMS}, 0x11);

            return true;
        }
        String url=Uri.decode(oldurl);
        String number= url.substring(url.indexOf("sms:")+4,url.indexOf("?body"));
        String body= url.substring(url.indexOf("?body=")+6);
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+number));
        intent.putExtra("sms_body", body);
        Log.e("zkfamily","handleSMS url == "+url);
        Log.e("zkfamily","handleSMS number == "+number);
        Log.e("zkfamily","handleSMS body == "+body);
        startActivity(intent);
        return true;
    }
    private void requestPermisstion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            int cameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
            int writePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int locationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            int callPermission = checkSelfPermission(Manifest.permission.CALL_PHONE);
            int smssendPermission = checkSelfPermission(Manifest.permission.SEND_SMS);
            List<String> permissionStrings = new ArrayList<String>();
            boolean request = false;
            if (cameraPermission != PackageManager.PERMISSION_GRANTED){
                request = true;
                permissionStrings.add(Manifest.permission.CAMERA);
            }
            if (callPermission != PackageManager.PERMISSION_GRANTED){
                request = true;
                permissionStrings.add(Manifest.permission.CALL_PHONE);
            }
            if (smssendPermission != PackageManager.PERMISSION_GRANTED){
                request = true;
                permissionStrings.add(Manifest.permission.SEND_SMS);
            }

            if (writePermission != PackageManager.PERMISSION_GRANTED){
                request = true;
                permissionStrings.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (locationPermission != PackageManager.PERMISSION_GRANTED){
                request = true;
                permissionStrings.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }

            if (request){

                String[] permissionList = new String[permissionStrings.size()];
                permissionList = permissionStrings.toArray(permissionList);
                requestPermissions(permissionList, 0x11);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0x11){
            boolean granted = true;
            if (grantResults != null){
                for (int grantResult : grantResults) {
                    granted = granted && (grantResult == PackageManager.PERMISSION_GRANTED);
                }
            }
            if (granted){

            }else {
                finish();
            }
        }
    }
    class AndroidAndJSInterface {
        /**
         * 该方法将被js调用
         * @param id
         * @param videoUrl
         * @param tile
         */
        @JavascriptInterface
        public void playVideo(int id,String videoUrl,String tile){
            //调起系统所有播放器
            Intent intent = new Intent();
            intent.setDataAndType(Uri.parse(videoUrl),"video/*");
            startActivity(intent);
        }
        @JavascriptInterface
        public void call(String phone) {
            Log.e("zkfamily","JavascriptInterface phone == "+phone);
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
            if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            HomeActivity.this.startActivity(intent);

        }
    }

    private void take(){
        File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "ZKFamily");
        // Create the storage directory if it does not exist
        if (! imageStorageDir.exists()){
            imageStorageDir.mkdirs();
        }
        File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        imageUri = Uri.fromFile(file);

        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            Log.e("ZKFamily","packageName == "+packageName);
            final Intent i = new Intent(captureIntent);
            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            i.setPackage(packageName);
            i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraIntents.add(i);

        }
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        Intent chooserIntent = Intent.createChooser(i,"Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
        startActivityForResult(chooserIntent,  FILECHOOSER_RESULTCODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("ZKfamily","onActivityResult  data == "+data);
        Log.e("ZKfamily","onActivityResult  resultCode == "+resultCode);
        if(resultCode==0)
        {
            if (mUploadCallbackAboveL != null) {
                mUploadCallbackAboveL.onReceiveValue(null);
                mUploadCallbackAboveL = null;
            }
            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(null);
                mUploadMessage = null;
            }
            return;
        }
        if(requestCode==FILECHOOSER_RESULTCODE)
        {
            if (null == mUploadMessage && null == mUploadCallbackAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (mUploadCallbackAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            }
            else  if (mUploadMessage != null) {

                if (result != null) {
                    String path = getPath(getApplicationContext(),
                            result);
                    Uri uri = Uri.fromFile(new File(path));
                    mUploadMessage
                            .onReceiveValue(uri);
                } else {
                    mUploadMessage.onReceiveValue(imageUri);
                }
                mUploadMessage = null;



            }
        }
    }



    @SuppressWarnings("null")
    @TargetApi(Build.VERSION_CODES.BASE)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
        if (requestCode != FILECHOOSER_RESULTCODE
                || mUploadCallbackAboveL == null) {
            return;
        }

        Uri[] results = null;

        if (resultCode == Activity.RESULT_OK) {

            if (data == null) {

                results = new Uri[]{imageUri};
            } else {
                String dataString = data.getDataString();
                ClipData clipData = data.getClipData();

                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }

                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        if(results!=null){
            mUploadCallbackAboveL.onReceiveValue(results);
            mUploadCallbackAboveL = null;
        }else{
            results = new Uri[]{imageUri};
            mUploadCallbackAboveL.onReceiveValue(results);
            mUploadCallbackAboveL = null;
        }

        return;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.takePhoto:
                take();
                break;
            case R.id.choosePhoto:
                choosePhoto();
                break;
        }
        dismissByClick=true;
        dialog.dismiss();
    }
    private void choosePhoto()
    {

// 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
        startActivityForResult(intent, FILECHOOSER_RESULTCODE);
    }
    /**
     * 弹窗，启动拍照或打开相册
     */
    public void popupDialog() {
        dialog = new Dialog(this,R.style.ActionSheetDialogStyle);
        //填充对话框的布局
        inflate = LayoutInflater.from(this).inflate(R.layout.pick_picture, null);
        //初始化控件
        choosePhoto = (TextView) inflate.findViewById(R.id.choosePhoto);
        takePhoto = (TextView) inflate.findViewById(R.id.takePhoto);
        choosePhoto.setOnClickListener(this);
        takePhoto.setOnClickListener(this);
        //将布局设置给Dialog
        dialog.setContentView(inflate);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener(){
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(dismissByClick)return;
                if (mUploadCallbackAboveL != null) {
                    mUploadCallbackAboveL.onReceiveValue(null);
                    mUploadCallbackAboveL = null;
                }
                if (mUploadMessage != null) {
                    mUploadMessage.onReceiveValue(null);
                    mUploadMessage = null;
                }
            }
        });

        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity( Gravity.BOTTOM);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 20;//设置Dialog距离底部的距离
//    将属性设置给窗体
        dialogWindow.setAttributes(lp);
        dialog.show();//显示对话框
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
    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }


    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    @Override
    protected void onResume() {
        super.onResume();
        dismissByClick=false;

    }

}

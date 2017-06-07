package com.monkey.testhttp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    public static final int TIME_OUT = 1*60*1000;//5分钟（单位：毫秒）
    public static final String HTTP_GET = "GET";//get请求
    public static final String HTTP_POST = "POST";//post请求

    private Button mBtnInterface;
    private TextView mTvInterface;
    private HttpURLConnection mHttpURLConnection;
    private int mResponseCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mBtnInterface = (Button)findViewById(R.id.btn_interface);
        mTvInterface = (TextView)findViewById(R.id.tv_interface);
        mBtnInterface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
    }

    private void getData() {
        AsyncTask<String, Integer, String> asyncTask = new AsyncTask<String, Integer, String>() {
            @Override
            protected String doInBackground(String... params) {
                StringBuffer sb = new StringBuffer();
                try {
                    String api = params[0];
                    URL url = new URL(api);
                    mHttpURLConnection = (HttpURLConnection) url.openConnection();
                    //设置连接超时时间 单位：毫秒
                    mHttpURLConnection.setConnectTimeout(TIME_OUT);
                    //设置读取的超时时间 单位：毫秒
                    mHttpURLConnection.setReadTimeout(TIME_OUT);
                    //mHttpURLConnection.setUseCaches(false);
                    mHttpURLConnection.setRequestMethod(HTTP_GET);
                    //设置是否向httpUrlConnection输出，如果是post请求，参数要放在http正文内，因此需要设为true, 默认情况下是false;
                    mHttpURLConnection.setDoOutput(false);
                    //设置是否从httpUrlConnection读入，默认情况下是true;
                    mHttpURLConnection.setDoInput(true);
                    //对mHttpURLConnection的设置要在这个方法之前
                    mHttpURLConnection.connect();

                    /**
                     * 这个方法在getInputStream()之前执行
                     * 例如：如果是404 先执行getInputStream()的话会出现FileNotFoundException
                     * 因为先通过状态码来判断是否成功过
                     */
                    mResponseCode = mHttpURLConnection.getResponseCode();
                    if (mResponseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = mHttpURLConnection.getInputStream();
                        //创建字符流（因为接受的是字符，所以用字符流更高效一点）
                        //这里也可以使用BufferedReader高效缓冲，增加性能
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                        char[] chars = new char[10];
                        int count = 0;
                        while ((count = inputStreamReader.read(chars, 0, chars.length)) != -1) {
                            sb.append(new String(chars, 0, count));
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return sb.toString();
            }

            @Override
            protected void onPostExecute(String result) {
                if(TextUtils.isEmpty(result)){
                    result = "接口请求失败 mResponseCode:"+mResponseCode;
                }
                mTvInterface.setText(result);
            }
        };
        String api = "http://gank.io/api/data/Android/10/1";
        //api = "http://cloudpad.bravowhale-qat.com/app/service?v=1.0&appKey=00001&format=json&method=jinbao.cms.content.get";
        //api = "http://gank.io/api/day/history";
        asyncTask.execute(api);
    }
}

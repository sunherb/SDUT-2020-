package com.example.viewnews;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.litepal.LitePal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
public class NewsFragment extends Fragment {
    private ListView newsListView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<NewsBean.ResultBean.DataBean> contentItems = new ArrayList<>();

    private static final int UPNEWS_INSERT = 0;

    private String currentTabName = "top";

    private int pageNo = 0, pageSize = 10;

    private FloatingActionButton fab;

    @SuppressLint("HandlerLeak")
    private Handler newsHandler = new Handler() {
        //主线程
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPNEWS_INSERT:
                    contentItems = ((NewsBean) msg.obj).getResult().getData();
                    TabAdapter adapter = new TabAdapter(getActivity(), contentItems);
                    newsListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    NewsInfoBean newsInfo;
                    for (int i = 0, len = contentItems.size(); i < len; ++i) {
                        newsInfo = new NewsInfoBean(contentItems.get(i));
                        newsInfo.save();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //加载新闻列表
        View view = inflater.inflate(R.layout.news_list, container, false);
        newsListView = (ListView) view.findViewById(R.id.newsListView);
        //tv = (TextView) view.findViewById(R.id.text_response);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onAttach(getContext());
        Log.d("上下文：", "Context: " + getContext());
        Log.d("NewsFragment", "Activity: " + getActivity());
        Bundle bundle = getArguments();
        final String category = bundle.getString("name", "top");
        currentTabName = category;
        Log.d("点击tab小标题为：", "onActivityCreated: " + category);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newsListView.smoothScrollToPosition(0);
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.colorRed);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                threadLoaderData(category);
            }
        });

        getDataFromNet(category);

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("子项的数据为", "onItemClick: " + contentItems.get(position));
                //获取点击条目的路径，传值显示WebView页面
                String url = contentItems.get(position).getUrl();
                Log.d("当前新闻子项的连接是：", "onItemClick: " + url);
                String uniquekey = contentItems.get(position).getUniquekey();
                String newsTitle = contentItems.get(position).getTitle();
                Intent intent = new Intent(getActivity(), WebActivity.class);
                intent.putExtra("pageUrl", url);
                intent.putExtra("uniquekey", uniquekey);
                intent.putExtra("news_title", newsTitle);
                System.out.println("当前账号2222222222222：" + MainActivity.currentUserId);
                startActivity(intent);
            }
        });
    }

    private void threadLoaderData(final String category) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //沉睡1.5s，本地刷新很快，以防看不到刷新效果
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (getActivity() == null)
                    return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loaderRefreshData(category);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private void loaderRefreshData(final String category) {
        //top，shehui，guonei，guoji，yule，tiyu，junshi，keji，caijing，shishang
        String categoryName = "头条";
        if (category.equals("top")) {
            categoryName = "头条";
        } else if (category.equals("shehui")) {
            categoryName = "社会";
        } else if (category.equals("guonei")) {
            categoryName = "国内";
        } else if (category.equals("guoji")) {
            categoryName = "国际";
        } else if (category.equals("yule")) {
            categoryName = "娱乐";
        } else if (category.equals("tiyu")) {
            categoryName = "体育";
        } else if (category.equals("junshi")) {
            categoryName = "军事";
        } else if (category.equals("keji")) {
            categoryName = "科技";
        } else if (category.equals("caijing")) {
            categoryName = "财经";
        } else if (category.equals("shishang")) {
            categoryName = "时尚";
        }
        ++pageNo;
        List<NewsBean.ResultBean.DataBean> dataBeanList = new ArrayList<>();
        NewsBean.ResultBean.DataBean bean = null;
        int offsetV = (pageNo - 1) * pageSize;
        Log.d("pageNo", "页数为: " + pageNo);
        Log.d("offsetV", "偏移量为: " + offsetV);
        Log.d("offsetV", "以下开始查询");
        List<NewsInfoBean> beanList = LitePal.where("category = ?", categoryName).limit(pageSize).offset(offsetV).find(NewsInfoBean.class);
        Log.d("TAG", "查询的数量为：" + beanList.size());
        if (beanList.size() == 0) {
            pageNo = 1;
            offsetV = (pageNo - 1) * pageSize;
            beanList = LitePal.where("category = ?", categoryName).limit(pageSize).offset(offsetV).find(NewsInfoBean.class);
            Log.d("分页查询", "loaderRefreshData: 已经超过最大页数，归零并重新查询！");
        }
        Log.d("刷新查到的数据大小", "run: " + beanList.size());
        for (int i = 0, len = beanList.size(); i < len; ++i) {
            bean = new NewsBean.ResultBean.DataBean();
            bean.setDataBean(beanList.get(i));
            dataBeanList.add(bean);
            Log.d("刷新id：", "run: " + beanList.get(i));
        }
        contentItems = dataBeanList;
        TabAdapter adapter = new TabAdapter(getActivity(), contentItems);
        newsListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    //异步消息处理机制
    private void getDataFromNet(final String data) {
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                String path = "http://v.juhe.cn/toutiao/index?type=" + data + "&key=547ee75ef186fc55a8f015e38dcfdb9a";
                URL url = null;
                try {
                    url = new URL(path);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(5000);
                    connection.setConnectTimeout(5000);
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) { // 200
                        InputStream inputStream = connection.getInputStream();
                        String json = streamToString(inputStream, "utf-8");
                        return json;
                    } else {
                        System.out.println(responseCode);
                        return 404 + data;
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return 404 + data;
            }

            protected void onPostExecute(final String result) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        NewsBean newsBean = null;
                        Log.d("后台处理的数据为：", "run: " + result);
                        if (!result.substring(0, 3).equals("404")) {
                            newsBean = new Gson().fromJson(result, NewsBean.class);
                            System.out.println(newsBean.getError_code());
                            if ("0".equals("" + newsBean.getError_code())) {
                                Message msg = newsHandler.obtainMessage();
                                msg.what = UPNEWS_INSERT;
                                msg.obj = newsBean;
                                newsHandler.sendMessage(msg);
                            } else {
                                Log.d("超过请求次数或者其他原因", "run: 现在从本地数据库中获取");
                                Log.d("当前tab名字为：", "run: " + currentTabName);
                                threadLoaderData(currentTabName);
                            }
                        } else {
                            threadLoaderData(result.substring(3));
                        }
                    }
                }).start();
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
            }
        };
        task.execute();
    }

    private String streamToString(InputStream inputStream, String charset) {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, charset);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String s = null;
            StringBuilder builder = new StringBuilder();
            while ((s = bufferedReader.readLine()) != null) {
                builder.append(s);
            }
            bufferedReader.close();
            inputStreamReader.close();
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
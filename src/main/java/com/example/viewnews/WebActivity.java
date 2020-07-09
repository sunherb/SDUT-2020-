package com.example.viewnews;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.example.viewnews.tools.BaseActivity;
import org.litepal.LitePal;
import java.util.List;


@SuppressLint("SetJavaScriptEnabled")
public class WebActivity extends BaseActivity {

    private WebView webView;

    private Toolbar navToolbar, commentToolBar;

    private String urlData, pageUniquekey, pageTtile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        webView = (WebView) findViewById(R.id.webView);
        navToolbar = (Toolbar) findViewById(R.id.toolbar_webView);
        commentToolBar = (Toolbar) findViewById(R.id.toolbar_webComment);
        findViewById(R.id.toolbar_webComment).bringToFront();
    }

    //活动由不可见变为可见时调用，避免oom,上次的bug在这里
    @Override
    protected void onStart() {
        super.onStart();
        urlData = getIntent().getStringExtra("pageUrl");
        pageUniquekey = getIntent().getStringExtra("uniquekey");
        pageTtile = getIntent().getStringExtra("news_title");

        System.out.println("当前新闻id为：" + pageUniquekey);
        System.out.println("当前新闻标题为：" + pageTtile);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setUseWideViewPort(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        settings.setLoadWithOverviewMode(true);
        settings.setDisplayZoomControls(false);
        webView.loadUrl(urlData);
        setSupportActionBar(commentToolBar);
        navToolbar.setTitle("头条新闻");
        setSupportActionBar(navToolbar);
        commentToolBar.inflateMenu(R.menu.tool_webbottom);
        commentToolBar.setTitle("感谢观看");

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                List<NewsCollectBean> beanList = LitePal.where("userIdNumer = ? AND newsId = ?", MainActivity.currentUserId == null ? "" : MainActivity.currentUserId, pageUniquekey).find(NewsCollectBean.class);
                MenuItem u = commentToolBar.getMenu().getItem(0);
                if(beanList.size() > 0) {
                    u.setIcon(R.drawable.ic_star_border_favourite_yes);
                } else {
                    u.setIcon(R.drawable.ic_star_border_favourite_no);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // 通过查看每个新闻的网页发现网页广告的div样式的选择器为body > div.top-wrap.gg-item.J-gg-item 然后去除这个样式，使其加载网页时去掉广告
                view.loadUrl("javascript:function setTop1(){document.querySelector('body > div.top-wrap.gg-item.J-gg-item').style.display=\"none\";}setTop1();");
                view.loadUrl("javascript:function setTop4(){document.querySelector('body > a.piclick-link').style.display=\"none\";}setTop4();");
                view.loadUrl("javascript:function setTop2(){document.querySelector('#news_check').style.display=\"none\";}setTop2();");
                view.loadUrl("javascript:function setTop3(){document.querySelector('body > div.articledown-wrap gg-item J-gg-item').style.display=\"none\";}setTop3();");
            }


            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){
            // 每次网页加载进度改变时，就会执行一次js代码，保证广告一出来就被干掉
            // 缺点也很明显，会执行很多次无效的js代码。
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                view.loadUrl("javascript:function setTop1(){document.querySelector('body > div.top-wrap.gg-item.J-gg-item').style.display=\"none\";}setTop1();");
                view.loadUrl("javascript:function setTop4(){document.querySelector('body > a.piclick-link').style.display=\"none\";}setTop4();");
                view.loadUrl("javascript:function setTop2(){document.querySelector('#news_check').style.display=\"none\";}setTop2();");
                view.loadUrl("javascript:function setTop3(){document.querySelector('body > div.articledown-wrap gg-item J-gg-item').style.display=\"none\";}setTop3();");
            }
        });


        commentToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.news_share:
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_SUBJECT, urlData);
                        intent.setType("text/plain");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(Intent.createChooser(intent, getTitle()));
                        break;
                    case R.id.news_collect:
                        if(!TextUtils.isEmpty(MainActivity.currentUserId)) {
                            MenuItem u = commentToolBar.getMenu().getItem(0);
                            List<NewsCollectBean> bean = LitePal.where("userIdNumer = ? AND newsId = ?", MainActivity.currentUserId, pageUniquekey).find(NewsCollectBean.class);
                            NewsCollectBean currentNews = null;
                            System.out.println(bean);
                            String answer = "";
                            if(bean.size() > 0) {
                                System.out.println("111111111111111");
                                int i = LitePal.deleteAll(NewsCollectBean.class, "userIdNumer = ? AND newsId = ?", MainActivity.currentUserId, pageUniquekey);
                                if(i > 0) {
                                    answer = "取消收藏！";
                                    u.setIcon(R.drawable.ic_star_border_favourite_no);
                                } else answer = "取消失败！";
                                System.out.println("6666666666666666");
                            } else {
                                currentNews = new NewsCollectBean();
                                currentNews.setUserIdNumer(MainActivity.currentUserId);
                                currentNews.setNewSTitle(pageTtile);
                                currentNews.setNewsId(pageUniquekey);
                                currentNews.setNewsUrl(urlData);
                                boolean isSave = currentNews.save();
                                System.out.println("收藏的新闻：" + currentNews);
                                if(isSave){
                                    answer = "收藏成功！";
                                    u.setIcon(R.drawable.ic_star_border_favourite_yes);
                                }
                                else answer = "收藏失败！";
                            }
                            Toast.makeText(WebActivity.this , answer, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(WebActivity.this, "请先登录后再收藏！", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                return true;
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_return_left);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        webView.getSettings().setJavaScriptEnabled(false);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_webview, menu);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent returnIntent = new Intent();
                setResult(RESULT_OK, returnIntent);
                WebActivity.this.finish();
                break;
            case R.id.news_setting:
                Toast.makeText(this, "夜间模式", Toast.LENGTH_SHORT).show();
                break;
            case R.id.news_feedback:
                Toast.makeText(this, "举报！", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }
}
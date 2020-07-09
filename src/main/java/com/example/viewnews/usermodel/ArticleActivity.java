package com.example.viewnews.usermodel;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viewnews.R;
import com.example.viewnews.tools.BaseActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class ArticleActivity extends BaseActivity {

    private List<Article> articleList = new ArrayList<>();

    private ArticleAdapter adapter;

    private String userIdNumber;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        userIdNumber = getIntent().getStringExtra("user_article_id");
        System.out.println("当前用户的账号为" + userIdNumber);
        Toolbar toolbar = findViewById(R.id.article_toolbar);
        toolbar.setTitle("我的文章");
        setSupportActionBar(toolbar);
        //获取到ActionBar的实例
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_return_left);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.article_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editArticleIntent = new Intent(ArticleActivity.this, EditArticleActivity.class);
                editArticleIntent.putExtra("userId", userIdNumber);
                startActivityForResult(editArticleIntent, 7);
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        initArticles();
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("回到我的文章列表这里！需要重新刷新一下列表！");
        //initArticles();
    }

    private void initArticles() {
        articleList.clear();
        List<Article> articles = LitePal.where("userId = ?", userIdNumber).find(Article.class);
        articleList.addAll(articles);
        adapter = new ArticleAdapter(articleList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ArticleActivity.this.finish();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 7:
                if(resultCode == RESULT_OK) {
                    initArticles();
                }
                break;
        }
    }
}
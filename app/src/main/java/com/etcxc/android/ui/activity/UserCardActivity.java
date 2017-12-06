package com.etcxc.android.ui.activity;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.ui.adapter.UserTabAdapter;
import com.etcxc.android.ui.fragment.FragmentActiva;
import com.etcxc.android.ui.fragment.FragmentCanceled;
import com.etcxc.android.ui.fragment.FragmentInUse;
import com.etcxc.android.ui.fragment.FragmentReport;
import com.etcxc.android.ui.fragment.FragmentTotal;
import com.etcxc.android.ui.view.materialsearchview.MaterialSearchView;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的卡
 * Created by LiuTao on 2017/8/26 0026.
 */

public class UserCardActivity extends BaseActivity {
    private static final String TAG = "UserCardActivity";
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private MaterialSearchView mSearchView;
    private List<Pair<String, Fragment>> mItems;
    public ArrayList<String> mDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_etc_card);
        initView();
        initData();
        init();
    }

    private void initData() {
        mDatas = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            mDatas.add(i, String.valueOf(i));
            LogUtil.e(TAG, mDatas.size() + "");
        }
    }

    private void initView() {

        getToolbar().setTitle(R.string.user_etc_card);
        setBarBack();
        mTabLayout = (TabLayout) findViewById(R.id.user_card_tab);
        mViewPager = (ViewPager) findViewById(R.id.user_card_viewpager);

    }

    private void init() {
        mItems = new ArrayList<>();
        mItems.add(new Pair<>(getString(R.string.card_total), new FragmentTotal(mDatas)));
        mItems.add(new Pair<>(getString(R.string.card_inUser), new FragmentInUse(mDatas)));
        mItems.add(new Pair<>(getString(R.string.card_activa), new FragmentActiva(mDatas)));
        mItems.add(new Pair<>(getString(R.string.card_report), new FragmentReport(mDatas)));
        mItems.add(new Pair<>(getString(R.string.card_cancled), new FragmentCanceled(mDatas)));
        mViewPager.setAdapter(new UserTabAdapter(getSupportFragmentManager(), mItems));
        mViewPager.setOffscreenPageLimit(5);
        setupTabLayout();
        setupSearch();
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mViewPager.setCurrentItem(position);
            }
        });

    }

    private void setupTabLayout() {
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mTabLayout.setupWithViewPager(mViewPager);
        LinearLayout linearLayout = (LinearLayout) mTabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerPadding(6);
        linearLayout.setPadding(0, 6, 0, 6);
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(this,
                R.drawable.layout_divider_vertical));
    }

    private void setupSearch() {
        mSearchView = (MaterialSearchView) findViewById(R.id.search_view);
        mSearchView.setVoiceSearch(false);
        mSearchView.setCursorDrawable(R.drawable.color_cursor_orange);
        mSearchView.setSuggestions(mDatas.toArray(new String[mDatas.size()]));
        //监听搜索结果点击事件
        mSearchView.setOnSuggestionClickListener(new MaterialSearchView.OnSuggestionClickListener() {
            @Override
            public void onSuggestionClick(final String name) {
                mSearchView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSearchView.closeSearch();
                        //todo 数据库查询,查看卡片的详情
                        ToastUtils.showToast(name);
                    }

                }, 200);
            }
        });
        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Snackbar.make(findViewById(R.id.container), "Query: " + query, Snackbar.LENGTH_LONG)
                        .show();
                ToastUtils.showToast("点击了");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                ToastUtils.showToast("改变了");
                return false;
            }
        });

        mSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
                ToastUtils.showToast("弹出");
            }

            @Override
            public void onSearchViewClosed() {
                ToastUtils.showToast("关闭");
                //Do some magic
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_card, menu);
        MenuItem search = menu.findItem(R.id.item_search);//搜索
        mSearchView.setMenuItem(search);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mSearchView.isSearchOpen()) {
            mSearchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }


}

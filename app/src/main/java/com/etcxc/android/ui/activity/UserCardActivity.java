package com.etcxc.android.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.ui.adapter.UserTabAdapter;
import com.etcxc.android.ui.fragment.FragmenTotal;
import com.etcxc.android.ui.fragment.FragmentActiva;
import com.etcxc.android.ui.fragment.FragmentCanceled;
import com.etcxc.android.ui.fragment.FragmentInUse;
import com.etcxc.android.ui.fragment.FragmentReport;
import com.etcxc.android.ui.view.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import static com.etcxc.android.utils.UIUtils.closeAnimator;


/**
 * 我的卡
 * Created by LiuTao on 2017/8/26 0026.
 */

public class UserCardActivity extends BaseActivity implements TabLayout.OnTabSelectedListener{
    private static final String TAG = "UserCardActivity";
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private MaterialSearchView mSearchView;
    private List<Pair<String, Fragment>> mItems;
    public  ArrayList<String> mDatas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_etc_card);
        init();
    }
    private void init() {
        mDatas = new ArrayList<>();
        for (int i = 'A'; i < 'z'; i++) {
            mDatas.add("ETC卡:" + (char) i);
        }
        mToolbar = (Toolbar) findViewById(R.id.user_card_toolbar);
        mToolbar.setTitle(R.string.user_etc_card);
        mSearchView = (MaterialSearchView) findViewById(R.id.search_view);
        mSearchView.setVoiceSearch(false);
        mSearchView.setCursorDrawable(R.drawable.color_cursor_orange);
        mSearchView.setSuggestions(mDatas.toArray(new String[mDatas.size()]));
        setBarBack(mToolbar);
        mTabLayout = (TabLayout) findViewById(R.id.user_card_tab);
        mViewPager = (ViewPager) findViewById(R.id.user_card_viewpager);
        mItems = new ArrayList<>();
        mItems.add(new Pair<>(getString(R.string.card_total), new FragmenTotal()));
        mItems.add(new Pair<>(getString(R.string.card_inUser), new FragmentInUse()));
        mItems.add(new Pair<>(getString(R.string.card_activa), new FragmentActiva()));
        mItems.add(new Pair<>(getString(R.string.card_report), new FragmentReport()));
        mItems.add(new Pair<>(getString(R.string.card_cancled), new FragmentCanceled()));
        mViewPager.setAdapter(new UserTabAdapter(getSupportFragmentManager(), mItems));
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.addOnTabSelectedListener(this);
        LinearLayout linearLayout = (LinearLayout) mTabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerPadding(6);
        linearLayout.setPadding(0,6,0,6);
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(this,
                R.drawable.layout_divider_vertical));
        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Snackbar.make(findViewById(R.id.container), "Query: " + query, Snackbar.LENGTH_LONG)
                        .show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        mSearchView .setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    private void setBarBack(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                closeAnimator(UserCardActivity.this);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search,menu);
        MenuItem item = menu.findItem(R.id.item_search);
        mSearchView.setMenuItem(item);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);

                if (!TextUtils.isEmpty(searchWrd)) {
                    mSearchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

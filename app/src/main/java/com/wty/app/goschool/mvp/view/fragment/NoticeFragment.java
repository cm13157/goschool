package com.wty.app.goschool.mvp.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.devspark.appmsg.AppMsg;
import com.wty.app.goschool.R;
import com.wty.app.goschool.adapter.NoticeAdapter;
import com.wty.app.goschool.data.dalex.local.MarketDynamicDALEx;
import com.wty.app.goschool.mvp.presenter.NoticePresenter;
import com.wty.app.goschool.mvp.view.impl.INoticeView;
import com.wty.app.library.adapter.BaseRecyclerViewAdapter;
import com.wty.app.library.fragment.BaseFragment;
import com.wty.app.library.utils.NetWorkUtils;
import com.wty.app.library.widget.DivItemDecoration;
import com.wty.app.library.widget.loadingview.LoadingState;
import com.wty.app.library.widget.loadingview.LoadingView;
import com.wty.app.library.widget.loadingview.OnEmptyListener;
import com.wty.app.library.widget.loadingview.OnRetryListener;
import com.wty.app.library.widget.xrecyclerview.ProgressStyle;
import com.wty.app.library.widget.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 主页->通知
 * @author wty
 */
public class NoticeFragment extends BaseFragment<NoticePresenter> implements INoticeView{

    BaseRecyclerViewAdapter adapter;

    private List<MarketDynamicDALEx> mDataList = new ArrayList<>();

    @Bind(R.id.listview_notice)
    XRecyclerView listview;
    @Bind(R.id.notice_fl_loading)
    LoadingView mLoadingView;

    @Override
    public NoticePresenter getPresenter() {
        return new NoticePresenter();
    }

    @Override
    public void onInitView(Bundle savedInstanceState) {
        adapter = new NoticeAdapter(getContext(),mDataList);
        listview.setLayoutManager(new LinearLayoutManager(getActivity()));
        listview.addItemDecoration(new DivItemDecoration(15, false));
        listview.setLoadingMoreProgressStyle(ProgressStyle.LineSpinFadeLoader);
        listview.setRefreshProgressStyle(ProgressStyle.BallClipRotatePulse);
        listview.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                mPresenter.refreshMoreComplain();
            }

            @Override
            public void onLoadMore() {
                mPresenter.loadMoreComplain();
            }

        });
        listview.setAdapter(adapter);

        mLoadingView.withOnEmptyListener(new OnEmptyListener() {
            @Override
            public void onClick() {
                mPresenter.refreshMoreComplain();
            }
        }).withOnRetryListener(new OnRetryListener() {
            @Override
            public void onRetry() {
                mPresenter.refreshMoreComplain();
            }
        }).build();

        // 初始化进入页面加载数据
        mPresenter.loadNoticeFirst();
    }

    @Override
    public int getLayoutResource() {
        return R.layout.fragment_notice;
    }

    @Override
    public void doWorkOnResume() {
        initFragmentActionBar("主页");
    }

    @Override
    public void initFragmentActionBar(String title) {
        super.initFragmentActionBar(title);
        activity.getDefaultNavigation().setRightButton("发表", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public boolean checkNet() {
        return NetWorkUtils.isNetworkConnected(activity);
    }

    @Override
    public void showNoNet() {
        if(adapter.getItemCount()==0){
            mLoadingView.setState(LoadingState.STATE_NO_NET);
        }else{
            mLoadingView.setVisibility(View.GONE);
            AppMsg.makeText(activity,"网络连接失败，请检查网路",AppMsg.STYLE_INFO).show();
        }
    }

    @Override
    public void refreshMore(List<MarketDynamicDALEx> list) {
        if(list.size()!=0){
            adapter.addData(list);
            mLoadingView.setVisibility(View.GONE);
        }else{
            if(adapter.getItemCount()==0){
                mLoadingView.setState(LoadingState.STATE_EMPTY);
            }else{
                mLoadingView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void loadMore(List<MarketDynamicDALEx> list) {

    }

    @Override
    public void onRefreshComplete() {
        listview.refreshComplete();
    }

    @Override
    public void onRefreshComplete(int result) {
        listview.refreshComplete(result + "条新内容");
    }

    @Override
    public void onLoadMoreComplete() {
        listview.loadMoreComplete();
    }

    @Override
    public void onLoadMoreComplete(int result) {
        listview.loadMoreComplete();
        if(result == 0 && adapter.getItemCount()!=0){
            listview.setNoMore("亲,已经是最后一页了！");
        }
    }

}

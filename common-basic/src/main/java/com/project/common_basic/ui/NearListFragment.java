package com.project.common_basic.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.project.common_basic.R;
import com.project.common_basic.mvp.NearListPresenter;
import com.project.common_basic.mvp.NearListView;
import com.project.common_basic.ui.anim.SlideInRightAnimator;
import com.rey.material.widget.ProgressView;

/**
 * 通用列表Fragment，处理下拉刷新和加载更多
 *
 * @author yamlee
 */
public class NearListFragment<M extends NearListPresenter> extends NearFragment<M>
        implements NearListView {
    public RecyclerView rvBaseListFragment;
    public SwipeRefreshLayout srlBaseListFragment;
    public ProgressView pvLoadmore;
    public View emptyView;
    public TextView tvEmpty;
    public View errorView;
    public FrameLayout flContentRoot;
    public LinearLayoutManager layoutManager;
    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvBaseListFragment = (RecyclerView) view.findViewById(R.id.rv_base_list_fragment);
        srlBaseListFragment = (SwipeRefreshLayout) view.findViewById(R.id.srl_base_list_fragment);
        pvLoadmore = (ProgressView) view.findViewById(R.id.pv_loadmore);
        emptyView = view.findViewById(R.id.common_v_empty);
        tvEmpty = (TextView) view.findViewById(R.id.common_tv_empty);
        errorView = view.findViewById(R.id.common_v_error);
        flContentRoot = (FrameLayout) view.findViewById(R.id.fl_content_root);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        srlBaseListFragment.setColorSchemeResources(R.color.palette_orange);
        rvBaseListFragment.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        rvBaseListFragment.setLayoutManager(layoutManager);
        SlideInRightAnimator slideInRightAnimator = new SlideInRightAnimator();
        slideInRightAnimator.setInterpolator(new DecelerateInterpolator());
        slideInRightAnimator.setAddDuration(300);
        rvBaseListFragment.setItemAnimator(slideInRightAnimator);
        srlBaseListFragment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.refresh();
            }
        });
        rvBaseListFragment.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                int firstCompleteVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition();
                int verticalScrollOffset = rvBaseListFragment.computeVerticalScrollOffset();
                if (firstCompleteVisibleItem == 0 || verticalScrollOffset == 0) {
                    srlBaseListFragment.setEnabled(true);
                } else {
                    srlBaseListFragment.setEnabled(false);
                }
                int totalItemCount = layoutManager.getItemCount();
                //lastVisibleItem >= totalItemCount - 4 表示剩下4个item自动加载，自由选择
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - 2 && dy > 0) {
                    presenter.loadMore();
                }
            }
        });
        handler = new Handler();
    }

    @Override
    public void startRefresh() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (srlBaseListFragment != null) {
                    srlBaseListFragment.setRefreshing(true);
                }
            }
        });
    }

    @Override
    public void stopRefresh() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (srlBaseListFragment != null) {
                    srlBaseListFragment.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void setLoadingMoreVisibility(boolean visibility) {
        if (visibility) {
            pvLoadmore.setVisibility(View.VISIBLE);
        } else {
            pvLoadmore.setVisibility(View.GONE);
        }
    }
}

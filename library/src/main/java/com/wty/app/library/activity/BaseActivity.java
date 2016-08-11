package com.wty.app.library.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.wty.app.library.R;
import com.wty.app.library.mvp.IBase;
import com.wty.app.library.mvp.presenter.BasePresenter;
import com.wty.app.library.mvp.view.IBaseView;
import com.wty.app.library.utils.NetWorkUtils;
import com.wty.app.library.utils.SystemBarTintManager;
import com.wty.app.library.widget.navigation.NavigationText;
import com.wty.app.library.widget.sweetdialog.OnDismissCallbackListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * @author wty
 * 所有activity的基类
 * 一个高大上的名字：模版方法设计模式
 **/
public abstract class BaseActivity extends AppCompatActivity implements IBase {

    private NavigationText navigation;
    protected BasePresenter mPresenter;
    protected View mRootView;
    protected Toolbar toolbar;
    private SystemBarTintManager tintManager;//沉浸式状态栏


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = getPresenter();
        if (mPresenter != null && this instanceof IBaseView) {
            mPresenter.attachView((IBaseView) this);
        }

        initStatusBar();
        mRootView = LayoutInflater.from(this).inflate(getLayoutResource(), null);
        setContentView(mRootView);
        ButterKnife.bind(this, mRootView);
        onInitView(savedInstanceState);
        setNavigation(getDefaultNavigation());
    }

    @Override
    public View getView() {
        return mRootView;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        if (mPresenter != null && this instanceof IBaseView) {
            mPresenter.detachView();
            mPresenter = null;
        }
    }

    /**
     * 功能描述：是否设置沉浸式(默认不打开)
     * @return
     */
    protected boolean isEnableStatusBar() {
        return false;
    }

    @TargetApi(19)
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && isEnableStatusBar()) {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.blue);
        }
    }

    /**
     * 功能描述：设置状态栏的颜色
     **/
    protected void setStatusBarTintRes(int color) {
        if (tintManager != null) {
            tintManager.setStatusBarTintResource(color);
        }
    }

    /**
     * 功能描述：设置标题栏
     **/
    private void setNavigation(View navigation){
        if(toolbar == null)return;
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setCustomView(navigation, layoutParams);
        }
    }

    public NavigationText getDefaultNavigation(){
        if(navigation==null){
            navigation = new NavigationText(this);
        }
        return navigation;
    }

    /**
     * @Decription 校验数据
     **/
    protected List<String> validate(){
        return new ArrayList<String>();
    }

    /**
     * @Decription 提交数据
     **/
    protected boolean submit(){
        List<String> validate = validate();
        if(validate.size()!=0){
            onToastErrorMsg(validate.get(0));
            return false;
        }else if(!NetWorkUtils.isNetworkConnected(this)){
            onToastErrorMsg(getString(R.string.network_failed));
            return false;
        }else{
            return true;
        }
    }

    /**
     * @Decription 弹框提示
     **/
    public void onToast(OnDismissCallbackListener callback){
        SweetAlertDialog dialog = new SweetAlertDialog(this,callback.alertType);
        dialog.setTitleText(callback.msg)
                .setConfirmText("确定")
                .setConfirmClickListener(callback)
                .changeAlertType(callback.alertType);
        dialog.show();
    }

    public void onToastSuccess(final String msg){
        onToast(new OnDismissCallbackListener(msg,SweetAlertDialog.SUCCESS_TYPE));
    }

    public void onToastErrorMsg(final String msg){
        onToast(new OnDismissCallbackListener(msg,SweetAlertDialog.ERROR_TYPE));
    }

}

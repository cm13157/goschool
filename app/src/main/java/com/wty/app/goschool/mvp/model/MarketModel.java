package com.wty.app.goschool.mvp.model;

import com.wty.app.goschool.data.dalex.local.PublishDynamicDALEx;
import com.wty.app.goschool.mvp.model.impl.IHelpModel;
import com.wty.app.goschool.mvp.model.impl.IMarketModel;
import com.wty.app.library.mvp.presenter.ICallBack;

import java.util.List;

/**
 * @author wty
 */
public class MarketModel implements IMarketModel{

    @Override
    public void loadMoreComplain(PublishDynamicDALEx data, ICallBack<List<PublishDynamicDALEx>> callBack) {

    }

    @Override
    public void refreshMoreComplain(PublishDynamicDALEx data, ICallBack<List<PublishDynamicDALEx>> callBack) {

    }
}

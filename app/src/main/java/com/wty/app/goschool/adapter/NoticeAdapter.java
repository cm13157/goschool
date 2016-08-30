package com.wty.app.goschool.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wty.app.goschool.R;
import com.wty.app.goschool.activity.ImagePagerActivity;
import com.wty.app.goschool.data.dalex.local.MarketDynamicDALEx;
import com.wty.app.goschool.entity.ImageSize;
import com.wty.app.library.adapter.BaseRecyclerViewMultiItemAdapter;
import com.wty.app.library.adapter.NineGridImageViewAdapter;
import com.wty.app.library.utils.ImageLoaderUtil;
import com.wty.app.library.utils.ScreenUtil;
import com.wty.app.library.viewholder.BaseRecyclerViewHolder;
import com.wty.app.library.widget.imageview.ColorFilterImageView;
import com.wty.app.library.widget.imageview.NineGridImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Decription 通知 适配器
 */
public class NoticeAdapter extends BaseRecyclerViewMultiItemAdapter<MarketDynamicDALEx> {
    public NoticeAdapter(Context context, List<MarketDynamicDALEx> data) {
        super(context,data);
        addItemType(MarketDynamicDALEx.No_Picture,R.layout.fragment_notice_onlytext);
        addItemType(MarketDynamicDALEx.OnlyOne_Picture, R.layout.fragment_notice_oneitem);
        addItemType(MarketDynamicDALEx.Multi_Picture,R.layout.fragment_notice_multiitem);
    }

    @Override
    protected void bindView(BaseRecyclerViewHolder helper, MarketDynamicDALEx item, int position) {
        TextView tv_content = helper.getView(R.id.tv_content);
        tv_content.setText(item.getGscontent());

        switch (helper.getItemViewType()){

            case MarketDynamicDALEx.No_Picture:
                break;

            case MarketDynamicDALEx.OnlyOne_Picture:
                final List<String> listOne = Arrays.asList(item.getGsImage().split(","));
                ColorFilterImageView img = helper.getView(R.id.oneImagView);
                ViewGroup.LayoutParams lp = img.getLayoutParams();
                int width,height;
                if(item.getGssinglesize()>1.0){
                    //宽>高
                    width = ScreenUtil.dp2px(mContext,200);
                    height = (int)(width / item.getGssinglesize());
                }else if(item.getGssinglesize()<1.0){
                    //高大于宽
                    width = ScreenUtil.dp2px(mContext,150);
                    height = (int)(width / item.getGssinglesize());
                }else {
                    //宽等于高
                    width = ScreenUtil.dp2px(mContext,150);
                    height = width;
                }

                lp.width = width;
                lp.height = height;

                img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                img.setLayoutParams(lp);

                ImageLoaderUtil.load(mContext,item.getGsImage(),img);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageSize imageSize = new ImageSize(v.getMeasuredWidth(),v.getMeasuredHeight());
                        ImagePagerActivity.startImagePagerActivity(mContext,listOne,0,imageSize);
                    }
                });
                break;

            case MarketDynamicDALEx.Multi_Picture:

                List<String> list = Arrays.asList(item.getGsImage().split(","));
                NineGridImageView imageView = helper.getView(R.id.multiImagView);
                imageView.setAdapter(mAdapter);
                List<String> data = new ArrayList<>();
                data.addAll(list);
                imageView.setImagesData(data);

                break;
            default:
                break;
        }
    }

    @Override
    protected int getItemMultiViewType(int position) {
        MarketDynamicDALEx item = getItem(position);
        int length = item.getGsImage().split(",").length;
        if(length > 1)
            return MarketDynamicDALEx.Multi_Picture;
        else if (length ==1)
            return MarketDynamicDALEx.OnlyOne_Picture;

        return MarketDynamicDALEx.No_Picture;
    }

    private NineGridImageViewAdapter<String> mAdapter = new NineGridImageViewAdapter<String>() {
        @Override
        public void onDisplayImage(Context context, ImageView imageView, String path) {
            ImageLoaderUtil.load(mContext,path,imageView);

        }

        @Override
        public void onItemImageClick(Context context,View v, int index, List<String> list) {
            ImageSize imageSize = new ImageSize(v.getMeasuredWidth(),v.getMeasuredHeight());
            ImagePagerActivity.startImagePagerActivity(context,list,index,imageSize);
        }
    };

}

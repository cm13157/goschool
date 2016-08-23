package com.wty.app.goschool.mvp.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ListHolder;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.wty.app.goschool.R;
import com.wty.app.goschool.adapter.DialogSelectListAdapter;
import com.wty.app.goschool.data.dalex.local.PublishDynamicDALEx;
import com.wty.app.goschool.data.dalex.local.PublishDynamicDALEx.PublishDynamicType;
import com.wty.app.goschool.data.dalex.local.PublishDynamicDALEx.PublishEntityType;
import com.wty.app.goschool.mvp.presenter.PublishPresenter;
import com.wty.app.goschool.mvp.view.impl.IPublishView;
import com.wty.app.library.activity.BaseActivity;
import com.wty.app.library.activity.ImageSelectorActivity;
import com.wty.app.library.adapter.PhotoGridViewAdapter;
import com.wty.app.library.base.AppConstant;
import com.wty.app.library.bean.DialogOptionModel;
import com.wty.app.library.entity.ImageModel;
import com.wty.app.library.entity.ImageUriEntity;
import com.wty.app.library.utils.CommonUtil;
import com.wty.app.library.utils.NetWorkUtils;
import com.wty.app.library.widget.DialogHeaderView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.Bind;

/**
 * @Decription 发布新内容
 * @author wty
 */
public class PublishActivity extends BaseActivity<PublishPresenter> implements IPublishView{

    @Bind(R.id.et_content)
    EditText et_content;
    @Bind(R.id.img_grid_select)
    RecyclerView img_gridview;
    @Bind(R.id.tv_label)
    TextView tv_label;
    @Bind(R.id.rlayout_type_select)
    RelativeLayout layout_type;
    @Bind(R.id.tv_type_select)
    TextView tv_type;

    PhotoGridViewAdapter adapter;

    public static void startPublishActivity(Context context){
        Intent intent = new Intent(context,PublishActivity.class);
        context.startActivity(intent);
    }

    @Override
    public PublishPresenter getPresenter() {
        return new PublishPresenter();
    }

    @Override
    public void onInitView(Bundle savedInstanceState) {
        getDefaultNavigation().setTitle("写内容");
        getDefaultNavigation().getLeftButton().setText("主页");
        getDefaultNavigation().setRightButton("发表", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        getDefaultNavigation().getRightButton().setEnabled(false);

        adapter = new PhotoGridViewAdapter(this);
        adapter.setGridItemClickListener(listener);
        img_gridview.setLayoutManager(new GridLayoutManager(this, 4));
        img_gridview.setAdapter(adapter);
        registerListener();
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_publish;
    }

    @Override
    protected boolean submit() {
        if(super.submit()){
            mPresenter.submit(getSubmitData());
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {//从选择图片页面返回
            if (requestCode == AppConstant.ActivityResult.Request_Image) {
                //拿到返回的图片路径
                boolean isCamera = data.getBooleanExtra(ImageSelectorActivity.OUTPUT_ISCAMERA, false);
                ArrayList<String> images = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
                if(isCamera){
                    adapter.addOneImage(images.get(0));
                }else{
                    adapter.bindImagesByPath(images);
                }
            }
        }
    }

    @Override
    protected boolean isEnableStatusBar() {
        return true;
    }

    /**
     * @Decription 注册监听
     **/
    private void registerListener(){
        et_content.clearFocus();
        et_content.addTextChangedListener(watcher);
        CommonUtil.keyboardControl(this,false,et_content);

        layout_type.setOnClickListener(new View.OnClickListener() {

            DialogHeaderView headerView = new DialogHeaderView(PublishActivity.this,"选择发布类型");

            @Override
            public void onClick(View v) {

                CommonUtil.keyboardControl(PublishActivity.this,false,et_content);

                List<DialogOptionModel> data = new ArrayList<DialogOptionModel>();
                for(PublishDynamicType item: PublishDynamicType.values()){
                    DialogOptionModel model = new DialogOptionModel(item.name,item.code);
                    data.add(model);
                }

                final DialogPlus dialog = DialogPlus.newDialog(PublishActivity.this)
                        .setContentHolder(new ListHolder())
                        .setCancelable(true)
                        .setGravity(Gravity.CENTER)
                        .setHeader(headerView)
                        .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setContentWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setContentBackgroundResource(R.drawable.bg_dialog_list)
                        .setAdapter(new DialogSelectListAdapter(PublishActivity.this, data))
                        .setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                tv_type.setText(((DialogOptionModel)item).getText());
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
            }
        });
    }

    TextWatcher watcher = new TextWatcher() {

        int MAX_LENGTH = 300;
        int rest_Length = 300;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            rest_Length = MAX_LENGTH - s.length();
            tv_label.setText("(还能输入" + rest_Length + "个字)");
            if(s.length()==0){
                getDefaultNavigation().getRightButton().setEnabled(false);
            }else{
                getDefaultNavigation().getRightButton().setEnabled(true);
            }
        }
    };

    private PhotoGridViewAdapter.OnGridItemClickListener listener = new PhotoGridViewAdapter.OnGridItemClickListener() {
        final int  MAX_NUM = 9;
        @Override
        public void onAddClick() {
            List<ImageUriEntity> selectedImages = new ArrayList<>();
            List<ImageModel> selected = new ArrayList<>();
            selectedImages.addAll(adapter.getSelectImages());
            for(ImageUriEntity item:selectedImages){
                ImageModel model = new ImageModel(item.uri,"");
                selected.add(model);
            }
            ImageSelectorActivity.start(PublishActivity.this,MAX_NUM,ImageSelectorActivity.MODE_MULTIPLE,true,true,false,selected);
        }

        @Override
        public void onReduceClick(int position, ImageUriEntity item) {
            adapter.remove(position,item);
        }

        @Override
        public void onItemClick(View view, int position) {

        }
    };

    @Override
    public boolean checkNet() {
        return NetWorkUtils.isNetworkConnected(this);
    }

    @Override
    public void showNoNet() {
        showFailed(getString(R.string.network_failed));
    }

    @Override
    public void finishActivity() {
        finish();
    }

    private PublishDynamicDALEx getSubmitData() {
        PublishDynamicDALEx dalex = PublishDynamicDALEx.get();
        dalex.setGsdynamicid(UUID.randomUUID().toString());//主键id
        dalex.setGscontent(et_content.getText().toString());//填写的内容
        dalex.setGsdynamictype(PublishDynamicType.matchCode(tv_type.getText().toString()));//动态类型
        dalex.setGsImage(adapter.getSelectImagesPath());//图片
        dalex.setGsaddress("");//地址
        dalex.setGssendname("wty");//发送人名
        dalex.setGssenderlogourl("");//发送人头像
        if(adapter.getSelectImages().size()==0){
            //纯文本
            dalex.setGsentitytype(PublishEntityType.OnlyText.code);
        }else{
            //图片类型
            dalex.setGsentitytype(PublishEntityType.Picture.code);
        }
        return dalex;
    }
}

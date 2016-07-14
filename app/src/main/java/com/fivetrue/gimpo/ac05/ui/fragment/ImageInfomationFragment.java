package com.fivetrue.gimpo.ac05.ui.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.ui.adapter.ImageInfomationRecyclerAdapter;
import com.fivetrue.gimpo.ac05.utils.Log;
import com.fivetrue.gimpo.ac05.vo.data.ImageInfo;
import com.fivetrue.gimpo.ac05.vo.data.ImageInfoEntry;
import com.fivetrue.gimpo.ac05.widget.PagerSlidingTabStrip;

/**
 * Created by kwonojin on 16. 7. 6..
 */
public class ImageInfomationFragment extends BaseFragment implements PagerSlidingTabStrip.PagerTabContent{

    private static final String TAG = "ImageInfomationFragment";

    public interface OnChooseImageInfomationListener{
        void onSelected(ImageInfo info, Bitmap bm);

        void onUpdateTabContent(PagerSlidingTabStrip.PagerTabContent content);
    }

    public ImageInfomationFragment(){

    }

    private RecyclerView mRecyclerView = null;
    private ImageInfomationRecyclerAdapter mAdapter = null;
    private ImageInfoEntry mEntry = null;

    private OnChooseImageInfomationListener mOnChooseImageInfomationListener = null;

    private boolean mShowingIcon = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(getActivity() != null && getActivity() instanceof OnChooseImageInfomationListener){
            mOnChooseImageInfomationListener = (OnChooseImageInfomationListener) getActivity();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEntry = getArguments().getParcelable(ImageInfoEntry.class.getName());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = initView(inflater);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setData(mEntry);
    }

    private View initView(LayoutInflater inflater){
        View view = inflater.inflate(R.layout.fragment_image_infomation, null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_fragment_image_infomation);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        return view;
    }

    public void setData(ImageInfoEntry entry){
        if(entry != null){
            if(entry.getImageInfos() != null && entry.getImageInfos().size() > 0) {
                if (mAdapter == null) {
                    mAdapter = new ImageInfomationRecyclerAdapter(entry.getImageInfos(), onInfomationImageItemClickListener);
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    mAdapter.setData(entry.getImageInfos());
                }

                for(ImageInfo info : entry.getImageInfos()){
                    if(info.getCreateTime() > System.currentTimeMillis() - (1000 * 60 * 60 * 24 * 7)){
                        mShowingIcon = true;
                        if(mOnChooseImageInfomationListener != null){
                            mOnChooseImageInfomationListener.onUpdateTabContent(this);
                        }
                        break;
                    }
                }
            }
        }
    }

    @Override
    public int getStringResource() {
        return R.string.infomation;
    }

    @Override
    public int getIconResource() {
        return R.drawable.new_noti_icon;
    }

    @Override
    public String getTabName() {
        ImageInfoEntry entry = getArguments().getParcelable(ImageInfoEntry.class.getName());
        if(entry != null && entry.getTitle() != null){
            return entry.getTitle();
        }
        return null;
    }

    @Override
    public boolean isShowingIcon() {
        return mShowingIcon;
    }

    private ImageInfomationRecyclerAdapter.OnInfomationImageItemClickListener onInfomationImageItemClickListener = new ImageInfomationRecyclerAdapter.OnInfomationImageItemClickListener() {
        @Override
        public void onClick(View view, ImageInfo info, Bitmap bitmap) {
            Log.i(TAG, "onClick: " + info.toString());
            if (mOnChooseImageInfomationListener != null) {
                mOnChooseImageInfomationListener.onSelected(info, bitmap);
            }
        }
    };
}

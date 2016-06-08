package com.fivetrue.gimpo.ac05.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by ojin.kwon on 2016-04-03.
 */

/**
 *
 * @param <T> List에 사용할 Data type을 Subclass에서 정의한다.
 * @param <H> ViewHolder로 사용할 Class를 Subclass에서 정의 한다.
 */
public abstract class BaseRecyclerAdapter<T, H extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<H> implements IBaseAdapter<T> {

    protected static final int INVALID_VALUE = -1;

    private List<T> mData = null;
    /**
     * 선택 항목을 체크하기 위한 BooleanArray
     */
    private SparseBooleanArray mSelectedArray = null;

    private int mResourceId = INVALID_VALUE;

    public BaseRecyclerAdapter(List<T> data, int resouceId){
        mData = data;
        mResourceId = resouceId;
    }

    @Override
    public void setData(List<T> data){
        mData = data;
        notifyDataSetChanged();
    }

    public List<T> getData(){
        return mData;
    }

    /**
     * @return 생성시 전달받은 resourceId를 전달한다
     */
    public int getResourceId() {
        return mResourceId;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public H onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mResourceId, null);
        return makeHolder(view);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public T getItem(int position){
        return mData.get(position);
    }

    /**
     * 특정 위치의 선택 여부를 확인 한다
     * @param position
     * @return
     */
    @Override
    public boolean isSelected(int position){
        return mSelectedArray.get(position);
    }

    /**
     * 특정 위치의 선택 여부를 toggle할 때 사용된다
     * @param position
     */
    @Override
    public void toggleSelected(int position){
        selectItem(position, !mSelectedArray.get(position));
    }

    /**
     * 지정된 position 의 선택 여부를 SparseBooleanArray에 저장한다
     * @param position
     * @param isSelected
     */
    @Override
    public void selectItem(int position, boolean isSelected){
        if(isSelected){
            mSelectedArray.put(position, isSelected);
        }else{
            mSelectedArray.delete(position);
        }
    }

    /**
     * SparseBooleanArray의 값들을 초기화한다
     */
    @Override
    public void clearSelections(){
        mSelectedArray.clear();
        notifyDataSetChanged();
    }


    /**
     * Subclass에서 정의하는 ViewHolder를 생성한다
     * @param view getView의 convertView가 전달된다
     * @return
     */
    protected abstract H makeHolder(View view);

    @Override
    public int getCount() {
        return mData.size();
    }
}


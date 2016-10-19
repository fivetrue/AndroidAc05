package com.fivetrue.gimpo.ac05.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.fivetrue.fivetrueandroid.image.ImageLoadManager;
import com.fivetrue.fivetrueandroid.ui.adapter.BaseRecyclerAdapter;
import com.fivetrue.fivetrueandroid.ui.fragment.BaseDialogFragment;
import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.chatting.ChatMessage;
import com.fivetrue.gimpo.ac05.ui.fragment.InputDialogFragment;
import com.fivetrue.gimpo.ac05.ui.fragment.UserInfoDialogFragment;
import com.fivetrue.gimpo.ac05.vo.user.FirebaseUserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by kwonojin on 2016. 9. 28..
 */

public class PersonalListAdapter extends BaseRecyclerAdapter<ChatMessage, PersonalListAdapter.PersonalItemViewHolder> {

    private static final String TAG = "BaseItemListAdapter";

    private SimpleDateFormat mSdf = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분");

    private FirebaseUserInfo mUserInfo;
    private FragmentManager mFm;

    public PersonalListAdapter(List<ChatMessage> data, FirebaseUserInfo userInfo, FragmentManager fm) {
        super(data, R.layout.item_personal_message_list);
        this.mUserInfo = userInfo;
        this.mFm = fm;
    }

    @Override
    protected PersonalItemViewHolder makeHolder(View view) {
        PersonalItemViewHolder holder = new PersonalItemViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final PersonalItemViewHolder holder, int position) {
        if(holder != null){
            final ChatMessage item = getItem(position);

            holder.userImage.setImageUrl(item.getUserImage(), ImageLoadManager.getImageLoader());
            holder.date.setText(mSdf.format(new Date(item.createTime)));
            holder.message.setText(item.message);
            holder.name.setText(item.getName());

            holder.userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putParcelable(ChatMessage.class.getName(), item);
                    UserInfoDialogFragment dialog = new UserInfoDialogFragment();
                    dialog.show(mFm, null
                            , v.getContext().getString(android.R.string.ok), null, b, new BaseDialogFragment.OnClickDialogFragmentListener() {
                                @Override
                                public void onClickOKButton(BaseDialogFragment f, Object data) {
                                    f.dismiss();
                                }

                                @Override
                                public void onClickCancelButton(BaseDialogFragment f, Object data) {
                                    f.dismiss();
                                }
                            });
                }
            });

            holder.reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputDialogFragment dialog = new InputDialogFragment();
                    dialog.show(mFm, v.getContext().getString(R.string.sending_message)
                            , v.getContext().getString(android.R.string.ok)
                            , v.getContext().getString(android.R.string.cancel), null
                            , new BaseDialogFragment.OnClickDialogFragmentListener<String>() {
                                @Override
                                public void onClickOKButton(BaseDialogFragment f, String data) {
                                    Log.d(TAG, "onClickOKButton() called with: f = [" + f + "], data = [" + data + "]");
                                    if(!TextUtils.isEmpty(data)){
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_DB_ROOT_PERSON).child(item.getUserId());
                                        ChatMessage message = new ChatMessage(null, data, null
                                                , mUserInfo.getEmail()
                                                , mUserInfo.getUid()
                                                , mUserInfo.getPhotoUrl(), 0);
                                        ref.child(Constants.FIREBASE_DB_MESSAGE).push().setValue(message.getValues());
                                    }
                                    f.dismiss();
                                }

                                @Override
                                public void onClickCancelButton(BaseDialogFragment f, String data) {
                                    f.dismiss();
                                }
                            });
                }
            });
        }

    }



    public static final class PersonalItemViewHolder extends RecyclerView.ViewHolder{

        public final View layout;
        public final View reply;
        public final NetworkImageView userImage;
        public final TextView message;
        public final TextView name;
        public final TextView date;

        public PersonalItemViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout_item_personal_container);
            userImage = (NetworkImageView) itemView.findViewById(R.id.iv_item_personal_image);
            message = (TextView) itemView.findViewById(R.id.tv_item_personal_message);
            name = (TextView) itemView.findViewById(R.id.tv_item_personal_sender);
            date = (TextView) itemView.findViewById(R.id.tv_item_personal_date);
            reply = itemView.findViewById(R.id.iv_item_personal_reply);
        }
    }
}

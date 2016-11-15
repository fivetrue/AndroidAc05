package com.fivetrue.gimpo.ac05.ui.adapter;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
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
import com.fivetrue.gimpo.ac05.database.ChatLocalDB;
import com.fivetrue.gimpo.ac05.firebase.database.MessageBoxDatabase;
import com.fivetrue.gimpo.ac05.firebase.model.ChatMessage;
import com.fivetrue.gimpo.ac05.ui.fragment.InputDialogFragment;
import com.fivetrue.gimpo.ac05.ui.fragment.UserInfoDialogFragment;
import com.fivetrue.gimpo.ac05.firebase.model.User;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by kwonojin on 2016. 9. 28..
 */

public class PersonMessageListAdapter extends BaseRecyclerAdapter<ChatMessage, PersonMessageListAdapter.PersonalItemViewHolder> {

    private static final String TAG = "PersonMessageListAdapter";

    private SimpleDateFormat mSdf = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분");

    private User mUserInfo;
    private FragmentManager mFm;

    public PersonMessageListAdapter(List<ChatMessage> data, User userInfo, FragmentManager fm) {
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
    public void onBindViewHolder(final PersonalItemViewHolder holder, final int position) {
        if(holder != null){
            final ChatMessage item = getItem(position);

            holder.userImage.setImageUrl(item.getUser().profileImage, ImageLoadManager.getImageLoader());
            holder.date.setText(mSdf.format(new Date(item.updateTime)));
            holder.message.setText(item.message);
            holder.name.setText(item.getUser().getDisplayName());

            holder.userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putParcelable(User.class.getName(), item.getUser());
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
                                    if(!TextUtils.isEmpty(data)){
                                        ChatMessage message = new ChatMessage(null, data, null
                                                , mUserInfo);
                                        new MessageBoxDatabase(item.user.uid)
                                                .getPersonReference().push().setValue(message.getValues());
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

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    new AlertDialog.Builder(v.getContext(), R.style.Base_Theme_AppCompat_Light_Dialog_Alert)
                            .setTitle(R.string.delete)
                            .setMessage(R.string.delete_chat_message)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new MessageBoxDatabase(mUserInfo.uid)
                                            .getPersonReference().child(item.key).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            long count = new ChatLocalDB(v.getContext()).removeChatMessage(Constants.PERSON_MESSAGE_ID, item);
                                            getData().remove(item);
                                            notifyItemRemoved(position);

                                        }
                                    });
                                }
                            }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }
            });
        }
    }



    public static final class PersonalItemViewHolder extends RecyclerView.ViewHolder{

        public final View layout;
        public final View reply;
        public final View delete;
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
            delete = itemView.findViewById(R.id.iv_item_personal_delete);
        }
    }
}

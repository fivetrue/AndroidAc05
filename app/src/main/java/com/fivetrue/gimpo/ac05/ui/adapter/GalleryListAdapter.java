package com.fivetrue.gimpo.ac05.ui.adapter;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.fivetrue.fivetrueandroid.google.FirebaseStorageManager;
import com.fivetrue.fivetrueandroid.image.ImageLoadManager;
import com.fivetrue.fivetrueandroid.ui.adapter.BaseRecyclerAdapter;
import com.fivetrue.fivetrueandroid.ui.diaglog.LoadingDialog;
import com.fivetrue.fivetrueandroid.ui.fragment.BaseDialogFragment;
import com.fivetrue.fivetrueandroid.view.CircleImageView;
import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.chatting.GalleryMessage;
import com.fivetrue.gimpo.ac05.database.GalleryMessageDatabase;
import com.fivetrue.gimpo.ac05.firebase.database.GalleryDatabase;
import com.fivetrue.gimpo.ac05.firebase.model.User;
import com.fivetrue.gimpo.ac05.ui.fragment.UserInfoDialogFragment;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by kwonojin on 2016. 9. 28..
 */

public class GalleryListAdapter extends BaseRecyclerAdapter<GalleryMessage, GalleryListAdapter.GalleryItemView> {

    private static final String TAG = "GalleryListAdapter";

    private SimpleDateFormat mSdf = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분");

    private FragmentManager mFm;
    private User mUser;

    public GalleryListAdapter(List<GalleryMessage> data, FragmentManager fm, User user) {
        super(data, R.layout.item_gallery_list);
        mFm = fm;
        mUser = user;
    }

    @Override
    protected GalleryItemView makeHolder(View view) {
        GalleryItemView holder = new GalleryItemView(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final GalleryItemView holder, final int position) {
        if(holder != null){
            final GalleryMessage item = getItem(position);

            holder.image.setImageUrl(item.image, ImageLoadManager.getImageLoader());
            holder.userImage.setImageUrl(item.getUser().profileImage);
            holder.userName.setText(item.getUser().getDisplayName());
            holder.message.setText(item.message);
            holder.date.setText(mSdf.format(new Date(item.updateTime)));
            holder.delete.setVisibility(mUser.uid.equals(item.user.uid) ? View.VISIBLE : View.GONE);

            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickItem(holder, item);
                }
            });

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

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.Base_Theme_AppCompat_Light_Dialog_Alert);
                    builder.setTitle(R.string.delete)
                            .setMessage(R.string.delete_gallery_message)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final LoadingDialog loadingDialog = new LoadingDialog(v.getContext());
                                    loadingDialog.show();
                                    new FirebaseStorageManager().deleteImageFromStorage(item.path).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            new GalleryDatabase().getReference().child(item.key)
                                                    .setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "onSuccess() called with: aVoid = [" + aVoid + "]");
                                                    long count = new GalleryMessageDatabase(v.getContext()).removeGalleryMessage(Constants.PERSON_MESSAGE_NOTIFICATION_ID, item);
                                                    Log.d(TAG, "onSuccess() called with: remove = [" + count + "]");
                                                    loadingDialog.dismiss();
                                                    getData().remove(item);
                                                    notifyItemRemoved(position);
                                                }
                                            });
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

    public static final class GalleryItemView extends RecyclerView.ViewHolder{

        public final View layout;
        public final NetworkImageView image;
        public final CircleImageView userImage;
        public final TextView userName;
        public final TextView message;
        public final TextView date;
        public final ImageView delete;

        public GalleryItemView(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout_item_gallery_container);
            image = (NetworkImageView) itemView.findViewById(R.id.iv_item_gallery_image);
            userImage = (CircleImageView) itemView.findViewById(R.id.iv_item_gallery_user_image);
            message = (TextView) itemView.findViewById(R.id.iv_item_gallery_user_message);
            userName = (TextView) itemView.findViewById(R.id.tv_item_gallery_user_id);
            date = (TextView) itemView.findViewById(R.id.tv_item_gallery_user_date);
            delete = (ImageView) itemView.findViewById(R.id.iv_item_gallery_delete);
        }
    }
}

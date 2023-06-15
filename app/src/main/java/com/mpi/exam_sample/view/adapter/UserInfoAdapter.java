package com.mpi.exam_sample.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mpi.exam_sample.R;
import com.mpi.exam_sample.model.api.UserInfoResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class UserInfoAdapter extends RecyclerView.Adapter<UserInfoAdapter.MyViewHolder> {
    private List<UserInfoResponse> userInfoResponseList = new ArrayList<>();
    private final PublishSubject<View> clickView = PublishSubject.create();

    public final Observable<View> observableClickedView() {
        return clickView;
    }

    public void setUserInfoResponseList(List<UserInfoResponse> data) {
        if (data == null) {
            return;
        }
        userInfoResponseList.clear();
        userInfoResponseList.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txtName.setText(userInfoResponseList.get(position).getName());
        holder.txtUsername.setText(userInfoResponseList.get(position).getUserName());
        holder.txtEmail.setText(userInfoResponseList.get(position).getEmail());
        holder.itemView.setTag(position);

        if (userInfoResponseList.size() > 0) {
            holder.imgViewSelection.setVisibility(View.VISIBLE);
        } else {
            holder.imgViewSelection.setVisibility(View.GONE);
        }

        clickView.onNext(holder.relativeUserInfoList);
    }

    @Override
    public int getItemCount() {
        return userInfoResponseList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        private TextView txtName;
        private TextView txtUsername;
        private TextView txtEmail;

        private ImageView imgViewSelection;

        private final View relativeUserInfoList;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            txtName = itemView.findViewById(R.id.name_label);
            txtUsername = itemView.findViewById(R.id.username_label);
            txtEmail = itemView.findViewById(R.id.email_label);
            relativeUserInfoList = itemView.findViewById(R.id.selectable);
            imgViewSelection = itemView.findViewById(R.id.img_next);
        }
    }
}

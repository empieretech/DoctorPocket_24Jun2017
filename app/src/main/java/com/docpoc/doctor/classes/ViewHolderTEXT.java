package com.docpoc.doctor.classes;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.docpoc.doctor.R;
import com.docpoc.doctor.utils.RobottoTextView;
import com.docpoc.doctor.utils.RoundedImageView;

public class ViewHolderTEXT extends RecyclerView.ViewHolder {

    public RobottoTextView tvMessage;

    public RoundedImageView imgPerson;


    public TextView tvTime;


    public ViewHolderTEXT(View itemView) {
        super(itemView);

        tvTime = (TextView) itemView.findViewById(R.id.tvTime);
        imgPerson = (RoundedImageView) itemView.findViewById(R.id.imgPerson);
        tvMessage = (RobottoTextView    ) itemView.findViewById(R.id.tvMessage);

    }
}
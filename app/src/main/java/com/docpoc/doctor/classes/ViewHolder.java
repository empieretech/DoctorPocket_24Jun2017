package com.docpoc.doctor.classes;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.docpoc.doctor.R;
import com.docpoc.doctor.utils.RobottoTextView;
import com.docpoc.doctor.utils.RoundCornerImageView;
import com.docpoc.doctor.utils.RoundedImageView;

public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imagep, imagecam;
        public RobottoTextView tvMessage;
        public RobottoTextView date_time;
        public RoundedImageView imgPerson;
        public RoundCornerImageView imgMessage;
        public ImageView imgDownload;
        public ProgressBar pBar;
        public TextView tvTime;
        public View viewTranspernt;


        public ViewHolder(View itemView) {
                super(itemView);

                tvTime = (TextView) itemView.findViewById(R.id.tvTime);
                imgPerson = (RoundedImageView) itemView.findViewById(R.id.imgPerson);
                imgMessage = (RoundCornerImageView) itemView.findViewById(R.id.imgMessage);
                imgDownload = (ImageView) itemView.findViewById(R.id.imgDownload);
                pBar = (ProgressBar) itemView.findViewById(R.id.pBar);
                viewTranspernt = (View) itemView.findViewById(R.id.viewTransprent);
                pBar.setMax(100);
        }
}
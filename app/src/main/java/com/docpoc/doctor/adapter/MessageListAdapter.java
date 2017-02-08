package com.docpoc.doctor.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.androidquery.AQuery;
import com.docpoc.doctor.utils.RobottoTextView;

import com.docpoc.doctor.ImageLoader.ImageLoader;
import com.docpoc.doctor.R;
import com.docpoc.doctor.webServices.GlobalBeans;

import java.util.List;

public class MessageListAdapter extends ArrayAdapter<GlobalBeans.MessageBean> {
    public AQuery aqList;
    Context context;
    boolean isDoctor;
    public MessageListAdapter(Context context, int resourceId,
            List<GlobalBeans.MessageBean> items, boolean isDoctor) {
        super(context, resourceId, items);
        this.context = context;
        this.isDoctor =  isDoctor;

        this.aqList = new AQuery(context);
    }
     
    /*private view holder class*/
    private class ViewHolder {
        ImageView imagep, imagecam ;
        RobottoTextView name;
        ProgressBar pBar;
        RobottoTextView date_time, tvMessage;

        
    }
     
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        GlobalBeans.MessageBean rowItem = getItem(position);
         
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.single_list_message, null);
            holder = new ViewHolder();
            holder.tvMessage = (RobottoTextView) convertView.findViewById(R.id.tvMessage);
            holder.name = (RobottoTextView) convertView.findViewById(R.id.txt_name);
            holder.date_time = (RobottoTextView) convertView.findViewById(R.id.txt_Date_time);
            holder.imagep = (ImageView) convertView.findViewById(R.id.img_person);
            holder.pBar = (ProgressBar) convertView.findViewById(R.id.pBar);
        //    holder.imagecam = (ImageView) convertView.findViewById(R.id.img_camera);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        if(!isDoctor){
            holder.name.setText("Doctor Name : " + rowItem.fName);
        } else {
            holder.name.setText("Patient Name : " + rowItem.fName);
        }


        holder.date_time.setText( "Date : "+rowItem.booking_date_time);
        holder.tvMessage.setText( "Booking Id : " +rowItem.booking_id);

        AQuery aq = aqList.recycle(convertView);
        aq.id(holder.imagep).progress(holder.pBar)
                .image(rowItem.image_url.replaceAll(" ","%20"), true, true, 0, R.drawable.aala1, null, 0, 1.0f);


      //  imgLoader.DisplayImage(bean.imageURL.replaceAll(" ","%20"), loader,holder.imageView);
      //  holder.imagep.setImageResource(rowItem.getImagep());
       // holder.imagecam.setImageResource(rowItem.getImagecam());


     /*   holder.imagecam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        return convertView;
    }
}

package com.docpoc.doctor.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;

import com.androidquery.AQuery;
import com.docpoc.doctor.Interface.ClickBook;
import com.docpoc.doctor.utils.RobottoTextView;

import com.docpoc.doctor.ImageLoader.ImageLoader;
import com.docpoc.doctor.R;
import com.docpoc.doctor.utils.RoundedImageView;
import com.docpoc.doctor.webServices.GlobalBeans;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DrSearchListViewAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    private List<GlobalBeans.DoctorBean> rowItemlist = new ArrayList<GlobalBeans.DoctorBean>();
    private ArrayList<GlobalBeans.DoctorBean> arraylist = new ArrayList<GlobalBeans.DoctorBean>();

    public AQuery aqList;
    public ClickBook clickBook;


    public DrSearchListViewAdapter(Fragment fr, int resourceId,
                                   List<GlobalBeans.DoctorBean> rowItemlist) {

        this.context = fr.getActivity();

        this.rowItemlist = rowItemlist;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<GlobalBeans.DoctorBean>();
        this.arraylist.addAll(rowItemlist);
        this.aqList = new AQuery(context);
        this.clickBook = (ClickBook) fr;

    }

    /*private view holder class*/
    private class ViewHolder {
        public RoundedImageView imageView;
        public ProgressBar pBar;
        public RobottoTextView tvFee, tvName, tvSpeciality,btnBook;

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;


        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.single_list_dr_search, null);
            holder = new ViewHolder();
            holder.tvFee = (RobottoTextView) convertView.findViewById(R.id.tvFee);
            holder.tvName = (RobottoTextView) convertView.findViewById(R.id.tvName);
            holder.tvSpeciality = (RobottoTextView) convertView.findViewById(R.id.tvSpeciality);
            holder.btnBook = (RobottoTextView) convertView.findViewById(R.id.btnBook);


            holder.imageView = (RoundedImageView) convertView.findViewById(R.id.img);
            holder.pBar = (ProgressBar) convertView.findViewById(R.id.pBar);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        final GlobalBeans.DoctorBean bean = rowItemlist.get(position);

        holder.tvSpeciality.setText(bean.speciality);
        holder.tvName.setText(bean.fName);
        holder.tvFee.setText("$ "+bean.fees);
        AQuery aq = aqList.recycle(convertView);
        aq.id(holder.imageView).progress(holder.pBar)
                .image(bean.imageURL.replaceAll(" ", "%20"), true, true, 0, R.drawable.aala1, null, 0, 1.0f);
        holder.btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBook.setOnBookClick(bean);
            }
        });


        return convertView;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        rowItemlist.clear();
        if (charText.length() == 0) {
            rowItemlist.addAll(arraylist);
        } else {
            for (GlobalBeans.DoctorBean bean : arraylist) {
                if (bean.fName.toLowerCase(Locale.getDefault())
                        .contains(charText) || bean.speciality.toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    rowItemlist.add(bean);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return rowItemlist.size();

    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return rowItemlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
}
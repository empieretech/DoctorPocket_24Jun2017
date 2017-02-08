package com.docpoc.doctor.adapter;


	
	

	import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
	import android.widget.ProgressBar;

	import com.androidquery.AQuery;
	import com.docpoc.doctor.utils.RobottoTextView;

import com.docpoc.doctor.ImageLoader.ImageLoader;
	import com.docpoc.doctor.R;

	import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.docpoc.doctor.webServices.GlobalBeans;

public class BookingHistorySearchDocAdapter extends BaseAdapter  {
	 
	    Context context;
	public AQuery aqList;
	private List<GlobalBeans.DrBookingHistoryBean> rowItemlist = new ArrayList<GlobalBeans.DrBookingHistoryBean>();
	private ArrayList<GlobalBeans.DrBookingHistoryBean> arraylist= new ArrayList<GlobalBeans.DrBookingHistoryBean>();
	 
	    public BookingHistorySearchDocAdapter(Context context, int resourceId,
	            List<GlobalBeans.DrBookingHistoryBean> items) {
	     //   super(context, resourceId, items);
	        this.context = context;

			this.rowItemlist = items;
			this.arraylist = new ArrayList<GlobalBeans.DrBookingHistoryBean>();
			this.arraylist.addAll(items);
			this.aqList = new AQuery(context);
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


	     
	    /*private view holder class*/
	    private class ViewHolder {
	        ImageView imageperson ;
	        RobottoTextView name;
	        RobottoTextView price;
	        RobottoTextView date;
	        RobottoTextView time;
			ProgressBar pBar;
	        
	    }
	     
	    public View getView(int position, View convertView, ViewGroup parent) {
	        ViewHolder holder = null;
	        GlobalBeans.DrBookingHistoryBean bean = (GlobalBeans.DrBookingHistoryBean) rowItemlist.get(position);
	         
	        LayoutInflater mInflater = (LayoutInflater) context
	                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	        if (convertView == null) {

	            convertView = mInflater.inflate(R.layout.single_book_history_search, null);
	            holder = new ViewHolder();

	            holder.name = (RobottoTextView) convertView.findViewById(R.id.txtname);
	            holder.price = (RobottoTextView) convertView.findViewById(R.id.txtprice);
	            holder.date = (RobottoTextView) convertView.findViewById(R.id.txtdate);
	            holder.time = (RobottoTextView) convertView.findViewById(R.id.txttime);
	            holder.imageperson = (ImageView) convertView.findViewById(R.id.activity_drProfile_iv_drProfile);
	           	holder.pBar = (ProgressBar) convertView.findViewById(R.id.pBar);
	            convertView.setTag(holder);
	        } else
	            holder = (ViewHolder) convertView.getTag();

			if (getItem(position) != null){

				holder.name.setText(bean.fName);
				holder.price.setText("$ " + bean.amount);
				holder.date.setText(bean.booking_date);
				holder.time.setText(bean.booking_time);
			//	holder.imageperson.setImageResource(rowItem.getImageperson());
				AQuery aq = aqList.recycle(convertView);
				aq.id(holder.imageperson).progress(holder.pBar )
						.image(bean.imageURL.replaceAll(" ","%20"), true, true, 0, R.drawable.aala1, null, 0, 1.0f);


			}

	        
	        
	        return convertView;
	    }


	// Filter Class
	public void filter(String charText) {
		charText = charText.toLowerCase(Locale.getDefault());
		rowItemlist.clear();
		if (charText.length() == 0) {
			rowItemlist.addAll(arraylist);
		} else {
			for (GlobalBeans.DrBookingHistoryBean bean : arraylist) {
				if (bean.fName.toLowerCase(Locale.getDefault())
						.contains(charText)) {
					rowItemlist.add(bean);
				}
			}
		}
		notifyDataSetChanged();
	}
	}




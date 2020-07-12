package com.outsource.smsdefault.Database;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.outsource.smsdefault.HandleSms.HandleTime;
import com.outsource.smsdefault.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class SmsStatusAdapter extends RecyclerView.Adapter<SmsStatusAdapter.TasksViewHolder> {

	private Context mCtx;
	private List<SmsStatus> taskList;

	public SmsStatusAdapter(Context mCtx, List<SmsStatus> taskList) {
		this.mCtx = mCtx;
		this.taskList = taskList;
	}

	@Override
	public TasksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_smsstatus, parent, false);
		return new TasksViewHolder(view);
	}

	@Override
	public void onBindViewHolder(TasksViewHolder holder, int position) {
		SmsStatus smsStatus = taskList.get(taskList.size() - position - 1);
		holder.txtFromNumber.setText(smsStatus.getFromNumber());
		holder.txtToNumber.setText(smsStatus.getToNumber());
		holder.txtTime.setText(HandleTime.ConvertMiliseconToDate(smsStatus.getTime()));

		String stt = smsStatus.getStatus();
		if(stt.equals("chưa gửi")){
			holder.txtStatus.setTextColor(mCtx.getResources().getColor(R.color.chuagui));
		}else if (stt.equals("đã gửi")){
			holder.txtStatus.setTextColor(mCtx.getResources().getColor(R.color.dagui));
		}else if (stt.equals("gửi lỗi")){
			holder.txtStatus.setTextColor(mCtx.getResources().getColor(R.color.guiloi));
		}
		holder.txtStatus.setText(smsStatus.getStatus());


	}

	@Override
	public int getItemCount() {
		return taskList.size();
	}

	class TasksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		TextView txtStatus, txtTime, txtFromNumber, txtToNumber;

		public TasksViewHolder(View itemView) {
			super(itemView);

			txtStatus = itemView.findViewById(R.id.txtStatus);
			txtTime = itemView.findViewById(R.id.txtTime);
			txtToNumber = itemView.findViewById(R.id.txtToNumber);
			txtFromNumber = itemView.findViewById(R.id.txtFromNumber);


			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View view) {
			SmsStatus smsStatus = taskList.get(getAdapterPosition());
/*
			Intent intent = new Intent(mCtx, UpdateTaskActivity.class);
			intent.putExtra("task", task);

			mCtx.startActivity(intent);*/
		}
	}
}
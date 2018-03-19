package com.hhchaos.ftp.filetransfer.Client;

import android.content.Context;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hhchaos.ftp.filetransfer.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import it.sauronsoftware.ftp4j.FTPFile;

public class FtpFileAdapter extends BaseAdapter {
	
	
	private static String TAG = "PlayerMusicAdapter" ;
	private List<FTPFile> mFileList = null;
	LayoutInflater mInfater = null;
	private Context mContext = null ;
	
	public FtpFileAdapter(Context context,  List<FTPFile> musicList) 
	{
		mContext = context ;
		mInfater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mFileList = musicList ;
	}

	@Override
	public int getCount() 
	{
		Log.v(TAG, "getCount  ###"  + mFileList.size() );
		return mFileList.size();
	}

	@Override
	public FTPFile getItem(int position) 
	{
		// TODO Auto-generated method stub
		return mFileList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertview, ViewGroup viewGroup) {
		View view = null;
		ViewHolder holder = null;
		if (convertview == null)
		{
			view = mInfater.inflate(R.layout.ftp_file_item, null);
			holder = new ViewHolder(view);
			FTPFile ftpFile =  getItem(position);
			if(ftpFile.getName().length()>15&&ftpFile.getName().contains(".")) {
				String s = ftpFile.getName().substring(0, 13);
				String p = ftpFile.getName().substring(ftpFile.getName().lastIndexOf("."),ftpFile.getName().length());
				s = s + "······"+p;
				holder.tvName.setText(s);
			}else
				holder.tvName.setText(ftpFile.getName());
			holder.tvAuthor.setText(Formatter.formatFileSize(mContext, ftpFile.getSize()));
			holder.tvDuration.setText(makeSimpleDateStringFromLong(ftpFile.getModifiedDate().getTime()));
			view.setTag(holder);
		}
		else
		{
			view = convertview ;
			holder = (ViewHolder) convertview.getTag() ;
		}
		return view;
//		View view = null;
//		ViewHolder holder = null;
//		if (convertview == null || convertview.getTag() == null)
//		{
//			view = mInfater.inflate(R.layout.ftp_file_item, null);
//			holder = new ViewHolder(view);
//			view.setTag(holder);
//		}
//		else
//		{
//			view = convertview ;
//			holder = (ViewHolder) convertview.getTag() ;
//		}
//
//		FTPFile ftpFile =  getItem(position);
//		if(ftpFile.getName().length()>15) {
//			String s = ftpFile.getName().substring(0, 15);
//			String p = ftpFile.getName().substring(ftpFile.getName().lastIndexOf("."),ftpFile.getName().length());
//			s = s + "······"+p;
//			holder.tvName.setText(s);
//		}else holder.tvName.setText(ftpFile.getName());
//		holder.tvAuthor.setText(Formatter.formatFileSize(mContext, ftpFile.getSize()));
//		holder.tvDuration.setText(makeSimpleDateStringFromLong(ftpFile.getModifiedDate().getTime()));
//		return view;
	}

	static class ViewHolder 
	{
		ImageView appIcon;
		TextView tvName;
		TextView tvAuthor;
        TextView tvDuration ;
        
		public ViewHolder(View view)
		{
			this.appIcon = (ImageView) view.findViewById(R.id.imgMuisc);
			this.tvName = (TextView) view.findViewById(R.id.tvName);
			this.tvAuthor = (TextView) view.findViewById(R.id.tvAuthor);
			this.tvDuration = (TextView) view.findViewById(R.id.tvDuration);
		}
	}

	private static final String SIMPLE_FORMAT_SHOW_TIME = "yyyy-MM-dd";

	private static SimpleDateFormat sSimpleDateFormat = new SimpleDateFormat(SIMPLE_FORMAT_SHOW_TIME);
	
	//时间格式转换
	public static CharSequence makeSimpleDateStringFromLong(long inTimeInMillis) {
		return sSimpleDateFormat.format(new Date(inTimeInMillis));
	}
	
	
}
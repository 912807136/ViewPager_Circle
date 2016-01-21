package com.gavin.demo_viewpager_circle;

import java.util.ArrayList;
import java.util.List;

import org.xutils.x;
import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class MainActivity extends Activity {
	private List<Data> dataList = new ArrayList<Data>();
	private List<ImageView> indicatorList = new ArrayList<ImageView>();
	private MyPagerAdapter adapter;
	private TextView titleTxt;
	private LinearLayout indicatorLayout;
	private ViewPager viewPager;
	private int initItem;
	private int delayTime = 1000;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
//			Log.i("mytag", "viewPager.getCurrentItem():"+viewPager.getCurrentItem());
			viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
			handler.sendEmptyMessageDelayed(0, delayTime);
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		x.Ext.init(getApplication());
		x.Ext.setDebug(true); // 是否输出debug日志

		initView();

		intData();
		handler.sendEmptyMessageDelayed(0, delayTime);
	}


	private void initView() {
		titleTxt = (TextView) findViewById(R.id.titleTxt);
		indicatorLayout = (LinearLayout) findViewById(R.id.indicatorLayout);

		int widthPixels = getResources().getDisplayMetrics().widthPixels;
		viewPager = (ViewPager) findViewById(R.id.viewPager1);
		viewPager.getLayoutParams().height = (int) (widthPixels / 1.6);
		viewPager.requestLayout();

		adapter = new MyPagerAdapter(this);
		viewPager.setAdapter(adapter);
		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				position = position % dataList.size();
				for (int i = 0; i < indicatorList.size(); i++) {
					indicatorList.get(i).setSelected(i == position);
				}
				titleTxt.setText(dataList.get(position).getTitle());
			}

			@Override
			public void onPageScrolled(int position, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int status) {
				if (status == ViewPager.SCROLL_STATE_IDLE) {
					handler.sendEmptyMessageDelayed(0, delayTime);
				} else {
					handler.removeCallbacksAndMessages(null);
				}
			}
		});
	}

	private void intData() {
		String[] urls = {
				"http://img20.360buyimg.com/da/jfs/t2479/286/1388089610/101151/38002fae/56988ecaN3c7fec89.jpg",
				"http://img10.360buyimg.com/da/jfs/t1924/87/2176216744/97543/4b39930f/569c3a5cN04335340.jpg",
				"http://img13.360buyimg.com/da/jfs/t1930/250/2040725906/100085/af80c96c/56949caaN1780691e.jpg",
				"http://img13.360buyimg.com/da/jfs/t1984/303/2030231883/291709/3d6b7a35/56976eedN366b84e5.jpg" };
		for (int i = 0; i < urls.length; i++) {
			Data data = new Data();
			data.setId(i + "");
			data.setTitle("title" + i);
			data.setUrl(urls[i]);
			dataList.add(data);
		}
		addIndicator(dataList);
		adapter.setData(dataList);
		adapter.notifyDataSetChanged();
		initItem = dataList.size() * ((Integer.MAX_VALUE >> 6) / 2);
		viewPager.setCurrentItem(initItem);

	}

	private void addIndicator(List<Data> list) {
		LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.leftMargin = DensityUtil.dip2px(3);
		params.rightMargin = DensityUtil.dip2px(3);
		for (int i = 0; i < list.size(); i++) {
			ImageView imageView = new ImageView(this);
			imageView.setImageResource(R.drawable.indicator_selector);
			indicatorLayout.addView(imageView, params);
			indicatorList.add(imageView);
		}

	}

	public class MyPagerAdapter extends PagerAdapter {
		private Context context;
		private List<Data> dataList;
		private List<ImageView> imageList = new ArrayList<ImageView>();
		private ImageOptions imageOptions;

		public MyPagerAdapter(Context context) {
			this.context = context;
			imageOptions = new ImageOptions.Builder().setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))
					// .setRadius(DensityUtil.dip2px(5))
					// 如果ImageView的大小不是定义为wrap_content, 不要crop.
					// .setCrop(true)
					// 加载中或错误图片的ScaleType
					// .setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
					.setImageScaleType(ImageView.ScaleType.CENTER_CROP).setLoadingDrawableId(R.drawable.ic_launcher)
					.setFailureDrawableId(R.drawable.ic_launcher).build();
		}

		public void setData(List<Data> list) {
			this.dataList = list;
			if (list != null) {
				for (int i = 0; i < dataList.size(); i++) {
					ImageView imageView = new ImageView(context);
					imageView.setScaleType(ScaleType.CENTER_CROP);
					imageList.add(imageView);
				}
			}
		}

		@Override
		public int getCount() {
			if (dataList != null) {
				return dataList.size() * (Integer.MAX_VALUE >> 6);
			}
			return 0;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// Log.i("mytag", "instantiateItem position:" + position);
			position = position % dataList.size();
			ImageView imageView = imageList.get(position);
			container.addView(imageView);
			x.image().bind(imageView, dataList.get(position).getUrl(), imageOptions);
			return imageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			ImageView imageView = imageList.get(position % dataList.size());
			container.removeView(imageView);
		}

		@Override
		public int getItemPosition(Object object) {
			return PagerAdapter.POSITION_NONE;
		}

	}

	@Override
	protected void onRestart() {
		super.onRestart();
		handler.sendEmptyMessageDelayed(0, delayTime);
	}

	@Override
	protected void onStop() {
		super.onStop();
		handler.removeCallbacksAndMessages(null);
	}

}

package org.androidannotations.sample;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.xad.sdk.locationsdk.LocationService;
import com.xad.sdk.locationsdk.models.LocationEvent;
import com.xad.sdk.locationsdk.models.UserGender;
import com.xad.sdk.locationsdk.utils.Logger;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.LongClick;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.Touch;
import org.androidannotations.annotations.Transactional;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.BooleanRes;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.StringRes;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@EActivity(R.layout.my_activity)
public class MyActivity extends Activity implements MultiplePermissionsListener {

	@ViewById
	EditText myEditText;

	@ViewById(R.id.myTextView)
	TextView textView;

	@StringRes(R.string.hello)
	String helloFormat;

	@ColorRes
	int androidColor;

	@BooleanRes
	boolean someBoolean;

	@SystemService
	NotificationManager notificationManager;

	@SystemService
	WindowManager windowManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		com.xad.sdk.locationsdk.utils.Logger.setLevel(com.xad.sdk.locationsdk.utils.Logger.Level.DEBUG);

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(this)
                .check();
        EventBus.getDefault().register(this);

		// windowManager should not be null
		windowManager.getDefaultDisplay();
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	}

	@Override
	protected void onDestroy() {
		LocationService.stop(this);
        EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEvent(LocationEvent event) {
	    Logger.logDebug("GTLocationService", "Received a location event");
        Logger.logDebug("GTLocationService", "Pois: " + event.getPoiList().toString() + ", Current location: " + event.getCurrentLocation().toString());
    }

	@Click
	void myButtonClicked() {
		String name = myEditText.getText().toString();
		setProgressBarIndeterminateVisibility(true);
		someBackgroundWork(name, 5);
	}

	@Background
	void someBackgroundWork(String name, long timeToDoSomeLongComputation) {
		try {
			TimeUnit.SECONDS.sleep(timeToDoSomeLongComputation);
		} catch (InterruptedException e) {
		}

		String message = String.format(helloFormat, name);

		updateUi(message, androidColor);

		showNotificationsDelayed();
	}

	@UiThread
	void updateUi(String message, int color) {
		setProgressBarIndeterminateVisibility(false);
		textView.setText(message);
		textView.setTextColor(color);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@UiThread(delay = 2000)
	void showNotificationsDelayed() {
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);

		Notification notification = new Notification.Builder(this)
				.setSmallIcon(R.drawable.icon)
				.setContentTitle("My notification")
				.setContentText("Hello, World!")
				.setContentIntent(contentIntent)
				.getNotification();

		notificationManager.notify(1, notification);
	}

	@LongClick
	void startExtraActivity() {
		Intent intent = ActivityWithExtra_.intent(this).myDate(new Date()).myMessage("hello !").get();
		intent.putExtra(ActivityWithExtra.MY_INT_EXTRA, 42);
		startActivity(intent);
	}

	@Click
	void startListActivity(View v) {
		startActivity(new Intent(this, MyListActivity_.class));
	}

	@Touch
	void myTextView(MotionEvent event) {
		Log.d("MyActivity", "myTextView was touched!");
	}

	@Transactional
	int transactionalMethod(SQLiteDatabase db, int someParam) {
		return 42;
	}

    @Override
    public void onPermissionsChecked(MultiplePermissionsReport report) {
        LocationService.start(this,
                "<access_key>",
                "<aes_password>",
                null,
                UserGender.GenderMale);
    }

    @Override
    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

    }
}

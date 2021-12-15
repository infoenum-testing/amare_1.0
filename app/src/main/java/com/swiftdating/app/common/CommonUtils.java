package com.swiftdating.app.common;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.swiftdating.app.BuildConfig;
import com.swiftdating.app.R;
import com.swiftdating.app.data.network.CallServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CommonUtils {

    public static final String isCompleteRegistration = "isCompleteRegistration";
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /* public static HashMap<String,Object> jsonToMap(String t) throws JSONException {
            HashMap<String, Object> map = new HashMap<String, Object>();
            JSONObject jObject = new JSONObject(t);
            Iterator<?> keys = jObject.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                String value = jObject.getString(key);
                map.put(key, value);
            }
            return map;
        }*/
    /*
     *** Method to Convert String to Date
     */
    public static long stringToDate(String aDate) {
        long startDate = 0L;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdf.parse(aDate);
           /* SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH);
            sdf2.setTimeZone(Calendar.getInstance().getTimeZone());
            startDate = sdf2.parse(sdf2.format(date)).getTime();*/
            startDate = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long min = startDate / 60000;
        return min;
    }

    public static long stringToSheduleDate(String aDate) {
        long startDate = 0L;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdf.parse(aDate);
           /* SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH);
            sdf2.setTimeZone(Calendar.getInstance().getTimeZone());
            startDate = sdf2.parse(sdf2.format(date)).getTime();*/
            startDate = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        long min = startDate / 60000;
        return startDate;
    }

    public static int getCountOfDays(String createdDateString, String expireDateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date createdConvertedDate = null, expireCovertedDate = null, todayWithZeroTime = null;
        try {
            createdConvertedDate = dateFormat.parse(createdDateString);
            expireCovertedDate = dateFormat.parse(expireDateString);
            Date today = new Date();
            todayWithZeroTime = dateFormat.parse(dateFormat.format(today));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int cYear = 0, cMonth = 0, cDay = 0;
        if (createdConvertedDate.after(todayWithZeroTime)) {
            Calendar cCal = Calendar.getInstance();
            cCal.setTime(createdConvertedDate);
            cYear = cCal.get(Calendar.YEAR);
            cMonth = cCal.get(Calendar.MONTH);
            cDay = cCal.get(Calendar.DAY_OF_MONTH);
        } else {
            Calendar cCal = Calendar.getInstance();
            cCal.setTime(todayWithZeroTime);
            cYear = cCal.get(Calendar.YEAR);
            cMonth = cCal.get(Calendar.MONTH);
            cDay = cCal.get(Calendar.DAY_OF_MONTH);
        }
        Calendar eCal = Calendar.getInstance();
        eCal.setTime(expireCovertedDate);
        int eYear = eCal.get(Calendar.YEAR);
        int eMonth = eCal.get(Calendar.MONTH);
        int eDay = eCal.get(Calendar.DAY_OF_MONTH);
        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();
        date1.clear();
        date1.set(cYear, cMonth, cDay);
        date2.clear();
        date2.set(eYear, eMonth, eDay);
        long diff = date2.getTimeInMillis() - date1.getTimeInMillis();
        float dayCount = (float) diff / (24 * 60 * 60 * 1000);
        return (-(int) dayCount);
    }

    /*
     *** Method to show Bottom Sheet Dialog for Report and Unmatch
     */
    public static BottomSheetDialog showMenuSheet(Activity context, View.OnClickListener clickListener) {
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(context);
        View sheetView = context.getLayoutInflater().inflate(R.layout.layout_bottom_sheet_menu, null);
        mBottomSheetDialog.setContentView(sheetView);
        if (mBottomSheetDialog.getWindow() != null) {
            sheetView.findViewById(R.id.tvReport).setOnClickListener(clickListener);
            sheetView.findViewById(R.id.tvUnMatch).setOnClickListener(clickListener);
            sheetView.findViewById(R.id.tvcancel).setOnClickListener(clickListener);
            mBottomSheetDialog.show();
        }
        return mBottomSheetDialog;
    }

    /*
     *** Method to show progress dialog
     */
    public static ProgressDialog showLoadingDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }

    /*
     *** Method to convert dp to Pixel
     */
    public static int dpToPx(Context context, int dp) {
        return Math.round(dp * getPixelScaleFactor(context));
    }

    /*
     *** Method to get Pixel according to devices
     */
    private static float getPixelScaleFactor(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /*
     *** Method to get Message Time for Chat
     */
    public static void setTimeInChat(TextView textView, String stringDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date crdate = format.parse(stringDate);

            String sd = new SimpleDateFormat("h:mm a").format(crdate);
            textView.setText(sd);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /*
     *** Method to get Message Time for Chat
     */
    public static String setDatesInChat(String stringDate) {
        String sd = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date crdate = format.parse(stringDate);
            sd = new SimpleDateFormat("EEE, MMM d yyyy").format(crdate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sd;
    }


    /**
     * set normal image with fresco library
     *
     * @param imageView id of the image where image need to be set
     * @param imagePath url for the image
     * @param sizeType  size of the image
     */
    public static void setImageUsingFresco(SimpleDraweeView imageView, String imagePath, int sizeType) {
        if (imagePath == null || imagePath.equalsIgnoreCase(""))
            imagePath = "no_image";

        ImageRequest imageRequest;

        if (sizeType == 1) {
            imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(imagePath))
                    .setLocalThumbnailPreviewsEnabled(true)
                    .build();
        } else if (sizeType == 2) {
            imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(imagePath))
                    .setResizeOptions(new ResizeOptions(400, 400))
                    .setLocalThumbnailPreviewsEnabled(true)
                    .build();
        } else if (sizeType == 3) {
            imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(imagePath))
                    .setResizeOptions(new ResizeOptions(100, 100))
                    .setLocalThumbnailPreviewsEnabled(true)
                    .build();
        } else if (sizeType == 10) {
            imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(imagePath))
                    .setResizeOptions(new ResizeOptions(50, 50))
                    .setLocalThumbnailPreviewsEnabled(true)
                    .build();
        } else {
            imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(imagePath))
                    .setResizeOptions(new ResizeOptions(350, 350))
                    .setLocalThumbnailPreviewsEnabled(true)
                    .build();
        }
        imageView.setController(Fresco.newDraweeControllerBuilder().setOldController(imageView.getController())
                .setImageRequest(imageRequest).build());

        PipelineDraweeControllerBuilder controllerBuilder = Fresco.newDraweeControllerBuilder()
                .setOldController(imageView.getController())
                .setControllerListener(new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onSubmit(String id, Object callerContext) {
                        super.onSubmit(id, callerContext);
                    }

                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        super.onFinalImageSet(id, imageInfo, animatable);
                    }

                    @Override
                    public void onIntermediateImageSet(String id, ImageInfo imageInfo) {
                        super.onIntermediateImageSet(id, imageInfo);
                    }

                    @Override
                    public void onIntermediateImageFailed(String id, Throwable throwable) {
                        super.onIntermediateImageFailed(id, throwable);
                    }

                    @Override
                    public void onFailure(String id, Throwable throwable) {
                        super.onFailure(id, throwable);
                    }

                    @Override
                    public void onRelease(String id) {
                        super.onRelease(id);
                    }
                })
                .setImageRequest(imageRequest);

        imageView.setController(controllerBuilder.build());

    }

    // convert UTF-8 to internal Java String format
    public static String convertUTF8ToString(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (Exception e) {
            return s;
        }
    }

    // convert internal Java String format to UTF-8
    public static String convertStringToUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (Exception e) {
            return s;
        }
    }

    /*
     *** Method to convert drawable to Bitmap
     */
    static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        final int width = !drawable.getBounds().isEmpty() ? drawable.getBounds().width() : drawable.getIntrinsicWidth();
        final int height = !drawable.getBounds().isEmpty() ? drawable.getBounds().height() : drawable.getIntrinsicHeight();

        final Bitmap bitmap = Bitmap.createBitmap(width <= 0 ? 1 : width, height <= 0 ? 1 : height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /*
     *** Method to get user Age from BirthDate
     */
   /* public static int getAge(String dobString) {

        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = sdf.parse(dobString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date == null) return 0;

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.setTime(date);

        int year = dob.get(Calendar.YEAR);
        int month = dob.get(Calendar.MONTH);
        int day = dob.get(Calendar.DAY_OF_MONTH);

        dob.set(year, month + 1, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age;
    }*/
    public static int getAge(String dobString) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = sdf.parse(dobString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null) return 0;
        Calendar dob = Calendar.getInstance();
        //Calendar today = Calendar.getInstance();
        dob.setTime(date);
        int year = dob.get(Calendar.YEAR);
        int month = dob.get(Calendar.MONTH);
        int day = dob.get(Calendar.DAY_OF_MONTH);
        int date1 = dob.get(Calendar.DATE);
        Calendar dobCalendar = Calendar.getInstance();
        dobCalendar.set(Calendar.YEAR, year);
        dobCalendar.set(Calendar.MONTH, month);
        dobCalendar.set(Calendar.DATE, date1);
        int ageInteger = 0;
        Calendar today = Calendar.getInstance();
        ageInteger = today.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR);
        if (today.get(Calendar.MONTH) == dobCalendar.get(Calendar.MONTH)) {
            if (today.get(Calendar.DAY_OF_MONTH) < dobCalendar.get(Calendar.DAY_OF_MONTH)) {
                ageInteger = ageInteger - 1;
            }
        } else if (today.get(Calendar.MONTH) < dobCalendar.get(Calendar.MONTH)) {
            ageInteger = ageInteger - 1;
        }
        return ageInteger;
    }

    /*
     *** Method to get Location Name from Lat and Long
     */
    public static String getAddress(Context mContext, String lat, String lng) {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lng), 1);
            if (addresses.size() > 0) {
                Address obj = addresses.get(0);
                String customAdd = "";
                if (!TextUtils.isEmpty(obj.getCountryName())) {
                    if (!TextUtils.isEmpty(obj.getAdminArea())) {
                        if (!TextUtils.isEmpty(obj.getSubAdminArea())) {
                            if (!TextUtils.isEmpty(obj.getLocality())) {
                                customAdd = obj.getLocality() + ", " + obj.getAdminArea() + ", " + obj.getCountryName();
                            } else {
                                customAdd = obj.getSubAdminArea() + ", " + obj.getAdminArea() + ", " + obj.getCountryName();
                            }
                        } else {
                            if (!TextUtils.isEmpty(obj.getLocality())) {
                                customAdd = obj.getLocality() + ", " + obj.getAdminArea() + ", " + obj.getCountryName();
                            } else {
                                customAdd = obj.getAdminArea() + ", " + obj.getCountryName();
                            }
                        }
                    } else {
                        customAdd = "";
                    }
                } else {
                    customAdd = "";
                }
                Log.e("WhereYouLiveActivity", "getAddress: " + customAdd);
                return customAdd;
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";//Current Location
        }
    }

    /*
     *** Method to get Location CityName from Lat and Long
     */
    public static String getCityAddress(Context mContext, String lat, String lng) {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lng), 1);
            if (addresses.size() > 0) {
                Address obj = addresses.get(0);
                if (!TextUtils.isEmpty(obj.getLocality())) {
                    return obj.getLocality();
                } else {
                    if (!TextUtils.isEmpty(obj.getSubAdminArea())) {
                        return obj.getSubAdminArea();
                    } else {
                        if (!TextUtils.isEmpty(obj.getAdminArea())) {
                            return obj.getAdminArea();
                        } else
                            return "";
                    }
                }
            } else {
                return "";
            }
        } catch (Exception e) {
            return "Current Location";
        }
    }

    /*
     *** Method to check if internet is connected or not
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm != null){
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    /*
     *** Method to show bottom sheet dialog for Camera and Gallery
     */
    public static BottomSheetDialog showCameraGalleryBottomSheet(Activity context, View.OnClickListener clickListener) {
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(context);
        View sheetView = context.getLayoutInflater().inflate(R.layout.layout_bottom_sheet_image_picker, null);
        mBottomSheetDialog.setContentView(sheetView);
        if (mBottomSheetDialog.getWindow() != null) {
            mBottomSheetDialog.getWindow().findViewById(R.id.bottomSheetGallery).setBackgroundResource(android.R.color.transparent);
            sheetView.findViewById(R.id.ll_camera).setOnClickListener(clickListener);
            sheetView.findViewById(R.id.ll_gallery).setOnClickListener(clickListener);
            sheetView.findViewById(R.id.img_close).setOnClickListener(clickListener);
            mBottomSheetDialog.show();
        }
        return mBottomSheetDialog;
    }
    /*
     *** Method to set Bottom Sheet Behavior
     */
    public static void setBottomSheetBehaviour(View view) {
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(view);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    /**
     * get bitmap from url
     *
     * @param strURL   url of the image
     * @param sizeType to check the bitmap scale is required
     * @return return generated bitmap
     */
    public static Bitmap getBitmapFromUrl(String strURL, int sizeType) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            if (sizeType == 1)
                myBitmap = Bitmap.createScaledBitmap(myBitmap, 100, 100, false);
            Log.e("bitmap Size", myBitmap.getByteCount() +"");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     *** Method to get current time in UTC
     */
    public static String deviceTime(String dateOne) {
        String dd = "";
        try {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date date = sdf1.parse(dateOne);

            sdf1.setTimeZone(TimeZone.getDefault());

            Date crdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sdf1.format(date));
            dd = sdf1.format(crdate);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dd;
    }

    /*
     *** Method to get date of every message to show in chat
     */
    public static void setDateInChat(TextView textView, String stringDate) {
        Calendar cal = Calendar.getInstance();
        String currentdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date cudate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(currentdate);
            Date crdate = format.parse(stringDate);
            Format formatter = new SimpleDateFormat("EEE, MMM d yyyy");
            String s = formatter.format(cudate);
            String s1 = formatter.format(crdate);

            if (s1.equalsIgnoreCase(s)) {
                textView.setText("Today");
            } else
                textView.setText(s1);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /*
     *** Method to get UTC Date Time in string
     */
    public static String getUTCdatetimeAsDate() {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());

        return utcTime;
    }


    /*  public static String getDateForPurchase(TransactionDetails details) {
          Date c = details.purchaseInfo.purchaseData.purchaseTime;
          SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
          return df.format(c);
      }*/
    public static String getDateForPurchase(Long date) {
        Date c = new Date(date);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss", Locale.getDefault());
        df.setTimeZone(Calendar.getInstance().getTimeZone());
        return df.format(c);
    }

    public static boolean checkIsDateExpire(int period,long date) {
        Calendar cal = Calendar.getInstance();
        Date now=cal.getTime(),expire;
        cal.setTime(new Date(date));
        if (BuildConfig.DEBUG) {
            int min=0;
            switch (period) {
                case 1:
                    min = 5;
                    break;
                case 3:
                    min = 10;
                    break;
                case 6:
                    min = 15;
                    break;
                case 12:
                    min = 30;
                    break;
            }
            cal.add(Calendar.MINUTE, min);
        } else {
            cal.add(Calendar.MONTH, period);
        }
        expire=cal.getTime();
        return now.after(expire);
    }

    public static boolean checkLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsProviderEnabled, isNetworkProviderEnabled;
        isGpsProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkProviderEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!isGpsProviderEnabled && !isNetworkProviderEnabled) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Enable Location");
            builder.setMessage("The app needs location. Please enable the location to continue.");
            builder.setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            });
            builder.setNegativeButton(android.R.string.no, null);
            builder.show();
            return false;
        } else {
            return true;
        }
    }

    /*  public static void CheckLocationPermission(Context context) {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { 
              if (context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { 
                  final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                  builder.setTitle("Location Permission");
                  builder.setMessage("The app needs location permissions. Please grant this permission to continue using the features of the app.");
                  builder.setPositiveButton(android.R.string.yes, (dialogInterface, i) -> ActivityCompat.requestPermissions(((Activity)context),new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},2021));
                  builder.setNegativeButton(android.R.string.no, null);
                  builder.show();
              }
          } else {
              LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
              boolean isGpsProviderEnabled, isNetworkProviderEnabled;
              isGpsProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
              isNetworkProviderEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

              if(!isGpsProviderEnabled && !isNetworkProviderEnabled) {
                  final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                  builder.setTitle("Location Permission");
                  builder.setMessage("The app needs location permissions. Please grant this permission to continue using the features of the app.");
                  builder.setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                      Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                      context.startActivity(intent);
                  });
                  builder.setNegativeButton(android.R.string.no, null);
                  builder.show();
              }
          }
      }*/
    public static void loadImageFromUrl(Context context, String url, CustomViewTarget target) {
        Glide.with(context).load(CallServer.BaseImage + url).into(target);
    }

    void CheckLocationPermission(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Location Permission");
                builder.setMessage("The app needs location permissions. Please grant this permission to continue using the features of the app.");
                builder.setPositiveButton(android.R.string.yes, (dialogInterface, i) -> ActivityCompat.requestPermissions(((Activity) context), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2021));
                builder.setNegativeButton(android.R.string.no, null);
                builder.show();
            }
        } else {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            boolean isGpsProviderEnabled, isNetworkProviderEnabled;
            isGpsProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkProviderEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGpsProviderEnabled && !isNetworkProviderEnabled) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Location Permission");
                builder.setMessage("The app needs location permissions. Please grant this permission to continue using the features of the app.");
                builder.setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(intent);
                });
                builder.setNegativeButton(android.R.string.no, null);
                builder.show();
            }
        }
    }
}

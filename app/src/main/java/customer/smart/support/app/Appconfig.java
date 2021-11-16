package customer.smart.support.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class Appconfig {
    //Key values
    public static final String shopIdKey = "shopIdKey";
    public static final String Phone = "phone";
    public static final String mypreference = "mypref";
    public static final String ip = "http://thestockbazaar.com/prisma/tmobile/";
    public static final String ip_img = "http://thestockbazaar.com/prisma/tmobile/";
    /* public static final String ip = "http://192.168.1.108:8111/prisma/tmobile/";
     public static final String ip_img = "http://192.168.1.108:8111/prisma/tmobile/";*/
    public static final String CREATE_SHOP = ip + "create_shop.php";
    public static final String STATUSUPDATE = ip + "status_update";
    public static final String STAFF_LOGIN = ip + "staff_login.php";
    public static final String UPDATE_SHOP = ip + "update_shop.php";
    public static final String ALL_SHOP = ip + "get_all_shop.php";
    public static final String GET_ALL_DEALER = ip + "getallDealer";
    public static final String DELETE_SHOP = ip + "delete_shop.php";
    public static final String ALL_AD = ip + "get_all_feed.php";
    public static final String DELETE_AD = ip + "feed/fileDelete.php";
    public static final String ALL_OFFER = ip + "get_all_offer.php";
    public static final String DELETE_OFFER = ip + "delete_offer.php";
    public static final String UPDATE_OFFER = ip + "update_offer.php";
    public static final String CREATE_OFFER = ip + "create_offer.php";
    public static final String SPARE_CREATE = ip + "spare/create_spare.php";
    public static final String SPARE_UPDATE = ip + "spare/update_spare.php";
    public static final String SPARE_DELETE = ip + "spare/delete_spare.php";
    public static final String SPARE_GET_ALL = ip + "spare/get_all_spare.php";
    public static final String UPDATE_STOCK = ip + "staff/update_stock.php";
    public static final String DATAFETCHSTAFFALL = ip + "staff/dataFetchStaffAll.php";
    public static final String DELETE_STOCK = ip + "seconds/delete_stock.php";
    public static final String CREATE_STOCK = ip + "staff/create_stock.php";
    public static final String ORDER_GET_ALL = ip + "dataFetchAll_order.php";
    public static final String ORDER_CHANGE_STATUS = ip + "order_change_status.php";
    public static final String ORDER_GET_ALL_IDS = ip + "dataFetchIds.php";
    public static final String DATAFETCHALL = ip + "staff/dataFetchAll.php";
    public static final String CREATE_CONTACT = ip + "staff/create_contact.php";
    public static final String CREATE_STOCK_S = ip + "seconds/create_stock.php";
    public static final String UPDATE_STOCK_S = ip + "seconds/update_stock.php";
    public static final String DATAFETCHALL_S = ip + "seconds/dataFetchAll.php";
    public static final String DATA_FETCH_CONTACT = ip + "staff/data_fetch_contact.php";
    public static final String UPDATE_CONTACT = ip + "staff/update_contact.php";
    //client
    public static final String SHOP = ip + "clientshop";
    public static final String STOCK = ip + "stock";
    //category
    public static final String CATEGORIES = ip + "category";
    public static final String FETCH_ITEM_BY_ID = ip + "fetchItemById";


    public static String URL_IMAGE_UPLOAD = ip + "fileUpload.php";
    public static String URL_IMAGE_UPLOAD_LATEST = ip + "fileUploadlatest.php";
    public static String URL_FEED_UPLOAD = ip + "fileFeed.php";
    //getAll_category
    public static String[] CATEGORY = new String[]{
            "New Mobiles", "Old Mobiles", "Accessories", "Spare",
    };

    public static Map<String, String[]> stringMap = new HashMap<String, String[]>() {{
        put("Fashion", new String[]{});
        put("COSMETICS", new String[]{"New Mobiles", "Old Mobiles", "Accessories", "Spare",});
        put("Mobiles and Tablets", new String[]{});
        put("Consumer Electronics", new String[]{});
        put("Books", new String[]{});
        put("Baby Products", new String[]{});
    }};
    public static DecimalFormat decimalFormat = new DecimalFormat("0");

    public static String[] getSubCatFromCat(String category) {
        if (stringMap.containsKey(category)) {
            return stringMap.get(category);
        }
        return new String[]{};
    }

    public static String intToString(int num, int digits) {
        String output = Integer.toString(num);
        while (output.length() < digits) output = "0" + output;
        return output;
    }

    public static String compressImage(String filePath, Context context) {

        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;


        float maxHeight = 1000.0f;
        float maxWidth = 1000.0f;
        if (actualHeight > maxHeight || actualWidth > maxWidth) {

            if (actualWidth > actualHeight) {
                float tempRatio = maxWidth / actualWidth;
                actualHeight = (int) (tempRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else if (actualWidth < actualHeight) {
                float tempRatio = maxHeight / actualHeight;
                actualWidth = (int) (tempRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }

        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

        options.inJustDecodeBounds = false;

        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {

            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename(context);
        try {
            out = new FileOutputStream(filename);

            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }


    public static String getFilename(Context context) {
        File file = new File(context.getCacheDir().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public static String convertTimeToLocal(String time) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = df.parse(time);
            df.setTimeZone(TimeZone.getDefault());
            return df.format(date);
        } catch (Exception e) {
            return time;
        }
    }

    public static String getResizedImage(String path, boolean isResized) {
        if (isResized) {
            return ip_img + "uploads/" + path.substring(path.lastIndexOf("/") + 1);
        }
        return path;
    }

    public static DefaultRetryPolicy getPolicy() {
        return new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }

    public static void multiSelectionModule(final Context context, String title,
                                            final String[] items, final EditText editText) {
        final ArrayList seletedItems = new ArrayList();
        boolean[] checkedItems = new boolean[items.length];

        if (editText.getText().toString() != null && editText.getText().toString().length() > 0) {
            String[] strings = editText.getText().toString().split(",");
            for (int j = 0; j < strings.length; j++) {
                for (int i = 0; i < items.length; i++) {
                    if (items[i].equals(strings[j])) {
                        checkedItems[i] = true;
                        seletedItems.add(items[i]);
                        break;
                    }
                }
            }
        }


        TextView myMsg = new TextView(context);
        myMsg.setText(title);
        myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
        myMsg.setTextSize(13);
        myMsg.setTextColor(Color.BLACK);


        AlertDialog dialog = new AlertDialog.Builder(context)
                .setCustomTitle(myMsg)
                .setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            seletedItems.add(items[indexSelected]);
                        } else if (seletedItems.contains(items[indexSelected])) {
                            // Else, if the item is already in the array, remove it
                            seletedItems.remove(items[Integer.valueOf(indexSelected)]);
                        }
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on OK
                        //  You can write the code  to save the selected item here
                        StringBuffer stringBuffer = new StringBuffer();

                        for (int i = 0; i < seletedItems.size(); i++) {
                            stringBuffer.append(seletedItems.get(i));
                            if (i != seletedItems.size() - 1) {
                                stringBuffer.append(",");
                            }
                            editText.setText(stringBuffer.toString());
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on Cancel
                    }
                }).create();
        dialog.show();
    }
}

package customer.smart.support.address;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import java.io.File;
import java.io.FileOutputStream;

import customer.smart.support.R;
import customer.smart.support.app.BaseActivity;
import customer.smart.support.app.PdfConfig;
import customer.smart.support.order.MyOrderPage;

public class MainActivity extends BaseActivity {

    private final String[] IDS = new String[]{
            "3000042878", "51713"
    };
    TextInputLayout selleraddressText;
    TextInputLayout buyeraddressText,idText,codText;
    BetterSpinner selleraddress,id;
    TextInputEditText buyeraddress,cod;
    NestedScrollView nestScroll;
    TextView seller_name;
    LinearLayout headerbar;
    RelativeLayout scroll;
    MaterialButton selectseller;
    MaterialButton selectbuyer;
    private final String[] ADDRESS = new String[]{
            "NETWORK COMMUNICATION\n" +
                    "134, 4TH STREET ," +
                    "CROSS CUT ROAD," +
                    " GANDHIPURAM," +
                    "COIMBATORE," +
                    " 641012." +
                    "Ph: 9787665726, 7010504536",
            "STOCK BAZAAR\n" +
                    "174/134, 4TH STREET," +
                    "CROSS CUT ROAD," +
                    " GANDHIPURAM, COIMBATORE" +
                    " 641012." +
                    "Ph:9514414404, 9787665726",
    };
    private final String TAG = getClass().getSimpleName();


    @Override
    protected void startDemo() {
        setContentView(R.layout.activity_main_address);


        seller_name = (findViewById(R.id.seller_name));
        nestScroll = (findViewById(R.id.nestScroll));
        headerbar = (findViewById(R.id.headerbar));
        scroll = (findViewById(R.id.scroll));


        selectseller = (findViewById(R.id.selectseller));
        selectbuyer = (findViewById(R.id.selectbuyer));

        selleraddressText = (findViewById(R.id.selleraddressText));
        buyeraddressText = (findViewById(R.id.buyeraddressText));
        selleraddress = (findViewById(R.id.selleraddress));
        buyeraddress = (findViewById(R.id.buyeraddress));

        idText = findViewById(R.id.idText);
        codText = findViewById(R.id.codText);
        id = findViewById(R.id.id);
        cod = findViewById(R.id.cod);

        ArrayAdapter<String> IdsAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_dropdown_item_1line, IDS);
        id.setAdapter(IdsAdapter);

        ArrayAdapter<String> addressAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, ADDRESS);
        selleraddress.setAdapter(addressAdapter);
        selleraddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });


        selleraddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (selleraddress.toString().length() > 0) {
                    selleraddressText.setError(null);
                }
            }
        });

        buyeraddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (buyeraddress.toString().length() > 0) {
                    buyeraddressText.setError(null);
                }
            }
        });


        ExtendedFloatingActionButton print = (findViewById(R.id.print));
        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selleraddress.getText().toString().length() <= 0) {
                    selleraddressText.setError("Select the address");
                    selleraddressText.requestFocus();
                } else if (buyeraddress.getText().toString().length() <= 0) {
                    buyeraddressText.setError("Enter the address");
                    buyeraddressText.requestFocus();
                } else {
                    printDialog();
                }

            }

        });

    }


    private void printFunction() {
        pDialog.setMessage("Printing...");
        showDialog();


        AddressBean tempAddressBean = new AddressBean(
                selleraddress.getText().toString(),
                buyeraddress.getText().toString()
        );

        String jsonVal = new Gson().toJson(tempAddressBean);
        //    getCreateAddress(jsonVal, tempMainbean);


    }

    private void justprintFunction() {
        pDialog.setMessage("Printing...");
        showDialog();
        AddressBean tempAddressBean = new AddressBean(

                selleraddress.getText().toString(),
                buyeraddress.getText().toString()

        );

        tempAddressBean.setCod(cod.getText().toString());
        tempAddressBean.setIdtext(id.getText().toString());

        printFunction(getApplicationContext(), tempAddressBean);


    }

    public void printFunction(Context context, AddressBean addressBean) {

        try {

            String path = getCacheDir().getPath() + "/PDF";
            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();
            Log.d("PDFCreator", "PDF Path: " + path);
            long fileName = System.currentTimeMillis();
            File file = new File(dir, fileName + ".pdf");
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fOut = new FileOutputStream(file);


            Document document = new Document(PageSize.A5, 30, 28, 40, 119);
            PdfWriter pdfWriter = PdfWriter.getInstance(document, fOut);

            document.open();
            PdfConfig.addMetaData(document);
            PdfConfig.addContent(document, addressBean, context);


            document.close();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            } else {
                Intent target = new Intent(Intent.ACTION_VIEW);
                target.setDataAndType(Uri.fromFile(file), "application/pdf");
                Intent intent = Intent.createChooser(target, "Open File");
                startActivity(intent);
            }

        } catch (Error | Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        hideDialog();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                printFunction();
            } else {
                Toast.makeText(MainActivity.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == 1) {
                AddressBean tempMainForSeller = (AddressBean) data.getSerializableExtra("data");
                selleraddress.setText(tempMainForSeller.selleraddress);

            }
        } else if (requestCode == 20) {
            if (resultCode == 2) {
                AddressBean tempMainForBuyer = (AddressBean) data.getSerializableExtra("data");
                buyeraddress.setText(tempMainForBuyer.buyeraddress);
            }
        }


    }

    public void printDialog() {

        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this, R.style.RoundShapeTheme);

        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.print_dialog, null);


        dialogBuilder.setTitle("Alert")

                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        hideDialog();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (ContextCompat.checkSelfPermission(MainActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                        } else {
                            justprintFunction();
                            dialogInterface.cancel();
                        }
                    }
                });
        dialogBuilder.setView(dialogView);
        final AlertDialog b = dialogBuilder.create();
        b.setCancelable(true);


        WindowManager.LayoutParams lp = b.getWindow().getAttributes();
        lp.dimAmount = 0.8f;
        b.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
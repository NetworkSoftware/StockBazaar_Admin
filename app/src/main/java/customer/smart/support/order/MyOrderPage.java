package customer.smart.support.order;

import static customer.smart.support.app.Appconfig.decimalFormat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import customer.smart.support.R;
import customer.smart.support.address.AddressBean;
import customer.smart.support.app.Appconfig;
import customer.smart.support.app.HeaderFooterPageEvent;
import customer.smart.support.app.PdfConfig;
import customer.smart.support.app.PdfConfigInvoice;


public class MyOrderPage extends AppCompatActivity {

    private final String[] ADDRESS = new String[]{
            "NETWORK COMMUNICATION\n" +
                    "134,4th STREET\n," +
                    "GANDHIPURAM," +
                    "COIMBATORE," +
                    "TN- 641012," +
                    "Ph: 7010504536, 9787665726",
            "STOCK BAZAAR\n" +
                    "174 4TH STR," +
                    "CROSS CUT" +
                    "GANDHIPURAM," +
                    "CBE TN 641012," +
                    "PH 9514414404,\n" +
                    "stockbazaar226@gmail.com",
    };

    private final String[] IDS = new String[]{
            "3000042878", "51713"
    };
    RecyclerView myorders_list;
    MyOrderListProAdapter myOrderListAdapter;
    SharedPreferences sharedpreferences;
    TextView address;
    TextView payment, tokenAdvance, wallet;
    TextView grandtotal;
    TextView shippingTotal;
    TextView subtotal, status, paymentId, comments, name, pincode, phone,
            specialDiscount, cashback;
    ProgressDialog pDialog;
    private ArrayList<CartItems> myorderBeans = new ArrayList<>();
    private Order myorderBean;
    private ExtendedFloatingActionButton invoiceBtn, addressBtn;
    private RoundedBottomSheetDialog mBottomSheetDialog;
    private TextView date;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_order);


        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        tokenAdvance = findViewById(R.id.tokenAdvance);

        sharedpreferences = getSharedPreferences(Appconfig.mypreference, Context.MODE_PRIVATE);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_24);
        getSupportActionBar().setTitle("My Orders");

        myorders_list = findViewById(R.id.myorders_list);
        myOrderListAdapter = new MyOrderListProAdapter(getApplicationContext(), myorderBeans);
        final LinearLayoutManager addManager1 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        myorders_list.setLayoutManager(addManager1);
        myorders_list.setAdapter(myOrderListAdapter);

        address = findViewById(R.id.address);
        wallet = findViewById(R.id.wallet);
        name = findViewById(R.id.name);
        pincode = findViewById(R.id.pincode);
        phone = findViewById(R.id.phone);
        payment = findViewById(R.id.payment);
        grandtotal = findViewById(R.id.grandtotal);
        shippingTotal = findViewById(R.id.shippingTotal);
        subtotal = findViewById(R.id.subtotal);
        status = findViewById(R.id.status);
        cashback = findViewById(R.id.cashback);
        paymentId = findViewById(R.id.paymentId);
        comments = findViewById(R.id.comments);
        specialDiscount = findViewById(R.id.specialDiscount);
        invoiceBtn = findViewById(R.id.invoiceBtn);
        addressBtn = findViewById(R.id.addressBtn);
        date = findViewById(R.id.date);
        invoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MyOrderPage.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MyOrderPage.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                } else {
                    printFunction();
                }
            }

        });

        addressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomDialog();
            }
        });
        getValuesFromIntent();
    }

    private void getValuesFromIntent() {
        try {

            myorderBean = (Order) getIntent().getSerializableExtra("data");
            address.setText(myorderBean.address);
            pincode.setText(myorderBean.pincode);
            wallet.setText(myorderBean.wallet);
            name.setText(myorderBean.name);
            phone.setText(myorderBean.phone);
            status.setText(myorderBean.status);
            payment.setText(myorderBean.payment);
            paymentId.setText(myorderBean.paymentId);
            comments.setText(myorderBean.comments);
            cashback.setText(myorderBean.cashback);
            grandtotal.setText(myorderBean.grandCost);
            specialDiscount.setText(myorderBean.specialCost);
            myorderBeans = myorderBean.productBeans;
            shippingTotal.setText(myorderBean.shipCost);
            subtotal.setText(myorderBean.amount);
            myOrderListAdapter.notifyData(myorderBeans);
            float finalTotal = Float.parseFloat(myorderBean.amount.replace("₹", ""));
            if (myorderBean.getPayment().equalsIgnoreCase("cod") &&
                    myorderBean.getPaymentId() != null &&
                    !myorderBean.getPaymentId().equalsIgnoreCase("null")
                    && myorderBean.getPaymentId().length() > 0) {

                if (!"0".equalsIgnoreCase(myorderBean.getTokenValue())) {
                    float token = Float.parseFloat(myorderBean.getTokenValue());
                    tokenAdvance.setText("₹" + decimalFormat.format(token) + ".00");
                } else {
                    float tokenPercent = (finalTotal / 100) * 10;
                    finalTotal = finalTotal - tokenPercent;
                    tokenAdvance.setText("₹" + decimalFormat.format(tokenPercent) + ".00");
                }
            } else {
                tokenAdvance.setText("₹0.00");
            }
            float sub = Float.parseFloat(myorderBean.getTokenValue());
            finalTotal = finalTotal - sub;
            subtotal.setText("₹" + decimalFormat.format(finalTotal) + ".00");


            if (paymentId != null) {
                paymentId.setText(myorderBean.getPaymentId());
            }

            date.setText(myorderBean.getCreatedOn());
            getSupportActionBar().setTitle("Order id:#" + myorderBean.getId());

        } catch (Exception e) {
            Log.e("xxxxxxxxx", e.toString());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getValuesFromIntent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        finish();
    }


    private void showDialog() {
        if (!this.pDialog.isShowing()) this.pDialog.show();

    }

    private void hideDialog() {

        if (this.pDialog.isShowing()) this.pDialog.dismiss();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                printFunction();
            } else {
                Toast.makeText(MyOrderPage.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void showBottomDialog() {

        mBottomSheetDialog = new RoundedBottomSheetDialog(MyOrderPage.this);
        LayoutInflater inflater = MyOrderPage.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_main_address, null);
        TextInputLayout selleraddressText = dialogView.findViewById(R.id.selleraddressText);
        TextInputLayout buyeraddressText = dialogView.findViewById(R.id.buyeraddressText);
        BetterSpinner selleraddress = dialogView.findViewById(R.id.selleraddress);
        TextInputLayout idText = dialogView.findViewById(R.id.idText);
        TextInputLayout codText = dialogView.findViewById(R.id.codText);
        TextInputEditText buyeraddress = dialogView.findViewById(R.id.buyeraddress);
        BetterSpinner id = dialogView.findViewById(R.id.id);
        TextInputEditText cod = dialogView.findViewById(R.id.cod);
        if ("gpay".equalsIgnoreCase(payment.getText().toString())) {
            id.setText("3000042878");
        } else {
            id.setText("51713");
        }

        ArrayAdapter<String> addressAdapter = new ArrayAdapter<String>(MyOrderPage.this,
                android.R.layout.simple_dropdown_item_1line, ADDRESS);
        selleraddress.setAdapter(addressAdapter);

        ArrayAdapter<String> IdsAdapter = new ArrayAdapter<String>(MyOrderPage.this,
                android.R.layout.simple_dropdown_item_1line, IDS);
        id.setAdapter(IdsAdapter);

        buyeraddress.setText(name.getText().toString() + "\n" + address.getText().toString() + "\nPh:" +
                phone.getText().toString());


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

        if (myorderBean.getPayment().equalsIgnoreCase("cod")) {
            codText.setVisibility(View.VISIBLE);
        } else {
            codText.setVisibility(View.GONE);
        }

        ExtendedFloatingActionButton print = dialogView.findViewById(R.id.print);
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
                    AddressBean addressBean = new AddressBean(selleraddress.getText().toString(),
                            buyeraddress.getText().toString());
                    addressBean.setCod(cod.getText().toString());
                    addressBean.setIdtext(id.getText().toString());
                    printFunctionSecond(addressBean);
                }

            }

        });
        mBottomSheetDialog.setContentView(dialogView);
        mBottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mBottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BottomSheetDialog d = (BottomSheetDialog) dialog;
                        FrameLayout bottomSheet = d.findViewById(R.id.design_bottom_sheet);
                        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }, 0);
            }
        });
        mBottomSheetDialog.show();

    }

    public void printFunction() {
        try {
            String path = getCacheDir().getPath() + "/PDF";
            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();
            Log.d("PDFCreator", "PDF Path: " + path);
            File file = new File(dir, myorderBean.getId() + "_" + System.currentTimeMillis() + ".pdf");
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fOut = new FileOutputStream(file);


            Document document = new Document(PageSize.A4, 30, 28, 40, 119);
            PdfWriter pdfWriter = PdfWriter.getInstance(document, fOut);

            document.open();
            PdfConfigInvoice.addMetaData(document);
            HeaderFooterPageEvent event = new HeaderFooterPageEvent();
            pdfWriter.setPageEvent(event);
            PdfConfigInvoice.addContent(document, myorderBean, MyOrderPage.this);
            document.close();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri uri = FileProvider.getUriForFile(this,
                        getPackageName() + ".provider", file);
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


    public void printFunctionSecond(AddressBean addressBean) {

        addressBean.setPayment(myorderBean.getPayment());

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
            PdfConfig.addContent(document, addressBean, MyOrderPage.this);


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

}

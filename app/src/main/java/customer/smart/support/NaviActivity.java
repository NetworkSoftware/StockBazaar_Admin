package customer.smart.support;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import customer.smart.support.ad.MainActivityAd;
import customer.smart.support.app.Appconfig;
import customer.smart.support.client.category.MainActivityCategories;
import customer.smart.support.client.seller_as.MainActivitySeller;
import customer.smart.support.client.service.ServiceTabsActivity;
import customer.smart.support.client.shop.MainActivityShop;
import customer.smart.support.client.stock.MainActivityProduct;
import customer.smart.support.client.wallet.MainActivityWallet;
import customer.smart.support.cmobile.MainActivityMobile;
import customer.smart.support.contact.MainActivityContact;
import customer.smart.support.offer.OfferActivity;
import customer.smart.support.order.MainActivityOrder;
import customer.smart.support.shop.MainActivity;
import customer.smart.support.spares.MainActivitySpares;
import customer.smart.support.staff.MainActivityStaff;
import customer.smart.support.stock.MainActivityStock;

import static customer.smart.support.app.Appconfig.mypreference;

public class NaviActivity extends AppCompatActivity {
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi);

        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

        LinearLayout ad = findViewById(R.id.ad);
        LinearLayout shop = findViewById(R.id.shop);
        LinearLayout stock = findViewById(R.id.stock);
        LinearLayout staff = findViewById(R.id.staff);
        LinearLayout cmobile = findViewById(R.id.cmobile);
        LinearLayout offer = findViewById(R.id.offer);
        LinearLayout contact = findViewById(R.id.contact);
        LinearLayout address = findViewById(R.id.address);
        LinearLayout spare = findViewById(R.id.spare);
        LinearLayout orders = findViewById(R.id.orders);
        LinearLayout seller = findViewById(R.id.seller);
        LinearLayout wallet = findViewById(R.id.wallet);
        LinearLayout service = findViewById(R.id.serviceLi);

        LinearLayout category = findViewById(R.id.category);
        LinearLayout client_shop = findViewById(R.id.client_shop);
        if (sharedpreferences.getString(
                "role", "admin").equalsIgnoreCase("sadmin")) {
            ad.setVisibility(View.VISIBLE);
            shop.setVisibility(View.VISIBLE);
            stock.setVisibility(View.VISIBLE);
            staff.setVisibility(View.VISIBLE);
            cmobile.setVisibility(View.VISIBLE);
            offer.setVisibility(View.VISIBLE);
            contact.setVisibility(View.VISIBLE);
            address.setVisibility(View.VISIBLE);
            spare.setVisibility(View.VISIBLE);
            orders.setVisibility(View.VISIBLE);
            category.setVisibility(View.VISIBLE);
            seller.setVisibility(View.VISIBLE);
            wallet.setVisibility(View.VISIBLE);
            client_shop.setVisibility(View.VISIBLE);
            service.setVisibility(View.VISIBLE);
        } else if (sharedpreferences.getString("role", "admin").equalsIgnoreCase("admin")) {

            String data = sharedpreferences.getString("data", "");

            if (data.contains("adv")) {
                ad.setVisibility(View.VISIBLE);
            } else {
                ad.setVisibility(View.GONE);
            }
            if (data.contains("shop")) {
                shop.setVisibility(View.VISIBLE);
            } else {
                shop.setVisibility(View.GONE);
            }
            if (data.contains("stock")) {
                stock.setVisibility(View.VISIBLE);
            } else {
                stock.setVisibility(View.GONE);
            }
            if (data.contains("staff")) {
                staff.setVisibility(View.VISIBLE);
            } else {
                staff.setVisibility(View.GONE);
            }
            if (data.contains("cmobile")) {
                cmobile.setVisibility(View.VISIBLE);
            } else {
                cmobile.setVisibility(View.GONE);
            }
            if (data.contains("offer")) {
                offer.setVisibility(View.VISIBLE);
            } else {
                offer.setVisibility(View.GONE);
            }
            if (data.contains("contact")) {
                contact.setVisibility(View.VISIBLE);
            } else {
                contact.setVisibility(View.GONE);
            }
            if (data.contains("address")) {
                address.setVisibility(View.VISIBLE);
            } else {
                address.setVisibility(View.GONE);
            }
            if (data.contains("spare")) {
                spare.setVisibility(View.VISIBLE);
            } else {
                spare.setVisibility(View.GONE);
            }
            if (data.contains("orders")) {
                orders.setVisibility(View.VISIBLE);
            } else {
                orders.setVisibility(View.GONE);
            }
            if (data.contains("category")) {
                category.setVisibility(View.VISIBLE);
            } else {
                category.setVisibility(View.GONE);
            }
            if (data.contains("cshop")) {
                client_shop.setVisibility(View.VISIBLE);
            } else {
                client_shop.setVisibility(View.GONE);
            }
            if (data.contains("seller")) {
                seller.setVisibility(View.VISIBLE);
            } else {
                seller.setVisibility(View.GONE);
            }
            if (data.contains("wallet")) {
                wallet.setVisibility(View.VISIBLE);
            } else {
                wallet.setVisibility(View.GONE);
            }
            if (data.contains("service")) {
                service.setVisibility(View.VISIBLE);
            } else {
                service.setVisibility(View.GONE);
            }


        }

        client_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, MainActivityShop.class);
                startActivity(io);
            }
        });
        seller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, MainActivitySeller.class);
                startActivity(io);
            }
        });
        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, MainActivityWallet.class);
                startActivity(io);
            }
        });
        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, MainActivityCategories.class);
                startActivity(io);

            }
        });
        ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, MainActivityAd.class);
                startActivity(io);

            }
        });
        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, MainActivityOrder.class);
                startActivity(io);

            }
        });
        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, MainActivityOrder.class);
                startActivity(io);

            }
        });
        shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, MainActivity.class);
                startActivity(io);

            }
        });

        stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, MainActivityStock.class);
                startActivity(io);

            }
        });
        staff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, MainActivityStaff.class);
                startActivity(io);

            }
        });
        cmobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, MainActivityMobile.class);
                startActivity(io);

            }
        });
        offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, OfferActivity.class);
                startActivity(io);

            }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, MainActivityContact.class);
                startActivity(io);

            }
        });
        spare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, MainActivitySpares.class);
                startActivity(io);

            }
        });
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, customer.smart.support.address.MainActivity.class);
                startActivity(io);

            }
        });
        service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, ServiceTabsActivity.class);
                startActivity(io);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navi_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        if (id == R.id.logOut) {
            logout();
        }


        return super.onOptionsItemSelected(item);
    }

    protected void logout() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(Appconfig.isLogin, false);
        editor.commit();
        startActivity(new Intent(NaviActivity.this, StartActivity.class));
        finishAffinity();
    }
}

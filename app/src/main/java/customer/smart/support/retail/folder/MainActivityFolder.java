package customer.smart.support.retail.folder;


import static customer.smart.support.app.Appconfig.MOVE_FOLDER;
import static customer.smart.support.app.Appconfig.RETAIL_CATEGORY;
import static customer.smart.support.app.Appconfig.RETAIL_FOLDER;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import customer.smart.support.R;
import customer.smart.support.app.AppController;
import customer.smart.support.app.Appconfig;
import customer.smart.support.retail.category.CategoryAdapter;
import customer.smart.support.retail.category.CategoryModel;
import customer.smart.support.retail.category.OnCategoryClick;
import customer.smart.support.retail.order.MainRetailOrder;
import customer.smart.support.retail.stock.MainActivityStock;
import customer.smart.support.retail.stock.StockRegister;

public class MainActivityFolder extends AppCompatActivity implements OnFolderClick, OnCategoryClick {
    private RecyclerView recyclerView;
    private List<FolderModel> folderModelList;
    private FolderAdapter mAdapter;
    private SearchView searchView;
    ProgressDialog progressDialog;
    private RoundedBottomSheetDialog mBottomSheetDialog;
    Button addCate;
    RecyclerView recycler_cate;
    CategoryAdapter categoryAdapter;
    private List<CategoryModel> cateModelList;
    String[] FOLDER_NAME = new String[]{"Loading"};
    private Map<String, String> folderNameIdMap = new HashMap<>();
    MaterialBetterSpinner selectFolder;
    // url to fetch contacts json

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainfolder);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Retail Folders");

        //category
        addCate = findViewById(R.id.addCate);
        addCate.setOnClickListener(v -> showBottomCateDialog(-1, null));
        recycler_cate = findViewById(R.id.recycler_cate);
        cateModelList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(cateModelList, this);
        final LinearLayoutManager addManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recycler_cate.setLayoutManager(addManager1);
        recycler_cate.setAdapter(categoryAdapter);

        recyclerView = findViewById(R.id.recycler_view);
        folderModelList = new ArrayList<>();
        mAdapter = new FolderAdapter(folderModelList, this);
        // white background notification bar
        whiteNotificationBar(recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(mAdapter);

        FloatingActionButton addFolder = findViewById(R.id.addFolder);
        addFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomDialog(-1, null);
            }
        });
    }

    private void fetchContacts() {
        String tag_string_req = "req_register";
        progressDialog.setMessage("Processing ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET, RETAIL_FOLDER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean success = jObj.getBoolean("success");
                    if (success) {
                        JSONArray jsonArray = jObj.getJSONArray("data");
                        folderModelList = new ArrayList<>();
                        folderNameIdMap = new HashMap<>();
                        FOLDER_NAME = new String[jsonArray.length()];
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            folderNameIdMap.put(jsonObject.getString("name"), jsonObject.getString("id"));
                            FOLDER_NAME[i] = jsonArray.getJSONObject(i).getString("name");
                            FolderModel categories = new FolderModel();
                            categories.setId(jsonObject.getString("id"));
                            categories.setName(jsonObject.getString("name"));
                            categories.setCount(jsonObject.getString("count"));
                            categories.setCreatedOn(jsonObject.getString("createdOn"));
                            folderModelList.add(categories);
                        }
                        mAdapter.notifyData(folderModelList);
                        getSupportActionBar().setSubtitle(folderModelList.size() + " Nos");
                    } else {
                        Toast.makeText(MainActivityFolder.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(MainActivityFolder.this, "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(MainActivityFolder.this, "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(Appconfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void fetchCategory() {
        String tag_string_req = "req_register";
        progressDialog.setMessage("Processing ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET, RETAIL_CATEGORY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean success = jObj.getBoolean("success");
                    if (success) {
                        JSONArray jsonArray = jObj.getJSONArray("data");
                        cateModelList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            CategoryModel categories = new CategoryModel();
                            categories.setId(jsonObject.getString("id"));
                            categories.setTitle(jsonObject.getString("title"));
                            categories.setCreatedOn(jsonObject.getString("createdOn"));
                            cateModelList.add(categories);
                        }
                        categoryAdapter.notifyData(cateModelList);
                    } else {
                        Toast.makeText(MainActivityFolder.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(MainActivityFolder.this, "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(MainActivityFolder.this, "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(Appconfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.retail_menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        } else if (id == R.id.action_order) {
            Intent intent = new Intent(MainActivityFolder.this, MainRetailOrder.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        fetchContacts();
        fetchCategory();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onDeleteClick(FolderModel folderModel) {
        AlertDialog diaBox = AskOption(folderModel);
        diaBox.show();
    }

    private AlertDialog AskOption(final FolderModel folderModel) {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Delete").setMessage("Do you want to Delete").setIcon(R.drawable.ic_delete_black_24dp)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteFile(folderModel.id, dialog);
//                        dialog.dismiss();
                    }

                })


                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                }).create();
        return myQuittingDialogBox;

    }

    private void deleteFile(String id, DialogInterface dialog) {
        String tag_string_req = "req_register";
        progressDialog.setMessage("Fetching ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.DELETE, RETAIL_FOLDER + "?id=" + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    if (jObj.getBoolean("success")) {
                        dialog.dismiss();
                        fetchContacts();
                    }
                    Toast.makeText(MainActivityFolder.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(MainActivityFolder.this, "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(MainActivityFolder.this, "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(Appconfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    @Override
    public void onEditClick(FolderModel folderModel) {
        showBottomDialog(1, folderModel);
    }

    @Override
    public void onViewClick(FolderModel folderModel) {
        Intent intent = new Intent(MainActivityFolder.this, MainActivityStock.class);
        intent.putExtra("folderId", folderModel.getId());
        startActivity(intent);
    }

    @Override
    public void onMoveClick(FolderModel folderModel) {
        showBottomMoveDialog(folderModel);
    }

    @Override
    public void onShareClick(FolderModel folderModel) {
        Intent myIntent = new Intent(Intent.ACTION_SEND);
        myIntent.setType("text/plain");
        String shareBody = "STOCK BAZAAR";
        String shareSub = "https://stockbazaar.app/products/"+folderModel.getName();
        myIntent.putExtra(Intent.EXTRA_SUBJECT, shareBody);
        myIntent.putExtra(Intent.EXTRA_TEXT, shareSub);
        startActivity(Intent.createChooser(myIntent, "Share using"));
    }

    private void showBottomMoveDialog(FolderModel folderModel) {
        mBottomSheetDialog = new RoundedBottomSheetDialog(MainActivityFolder.this);
        LayoutInflater inflater = MainActivityFolder.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.move_folder_stock, null);
        selectFolder = dialogView.findViewById(R.id.selectFolder);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(MainActivityFolder.this,
                android.R.layout.simple_dropdown_item_1line, FOLDER_NAME);
        selectFolder.setAdapter(categoryAdapter);
        ExtendedFloatingActionButton submit = dialogView.findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectFolder.getText().toString().length() <= 0) {
                    selectFolder.setError("Select folder");
                    selectFolder.requestFocus();
                } else if (!folderNameIdMap.containsKey(selectFolder.getText().toString())) {
                    selectFolder.setError("Select folder");
                    selectFolder.requestFocus();
                } else {
                    moveFolder(folderModel.getId(), folderNameIdMap.get(selectFolder.getText().toString()));
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

    private void moveFolder(String from, String to) {
        progressDialog.setMessage("Moving ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET, MOVE_FOLDER + "?fromId=" + from + "&toId=" + to, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    String msg = jsonObject.getString("message");
                    if (success) {
                        if (mBottomSheetDialog != null) {
                            mBottomSheetDialog.dismiss();
                        }
                        fetchContacts();
                    }
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(getApplicationContext(), "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(Appconfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq);
    }

    private void showDialog() {
        if (!progressDialog.isShowing()) progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing()) progressDialog.dismiss();
    }

    private void showBottomDialog(Integer position, FolderModel folderModel) {
        mBottomSheetDialog = new RoundedBottomSheetDialog(MainActivityFolder.this);
        LayoutInflater inflater = MainActivityFolder.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.create_folder_screen, null);
        TextInputEditText nameText = dialogView.findViewById(R.id.name);

        ExtendedFloatingActionButton submit = dialogView.findViewById(R.id.submit);
        if (position > 0) {
            nameText.setText(folderModel.getName());
        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nameText.getText().toString().length() <= 0) {
                    nameText.setError("Enter valid name");
                    nameText.requestFocus();
                } else {
                    folderReg(position > 0, position > 0 ? folderModel.id : null, nameText.getText().toString());
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

    private void folderReg(boolean create, String id, String name) {
        progressDialog.setMessage(create ? "Updating ..." : "Creating ...");
        showDialog();
        int method = Request.Method.POST;
        if (create) {
            method = Request.Method.PUT;
        }
        String url = RETAIL_FOLDER;
        StringRequest strReq = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    String msg = jsonObject.getString("message");
                    if (success) {
                        if (mBottomSheetDialog != null) {
                            mBottomSheetDialog.dismiss();
                        }
                        fetchContacts();
                    }
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(getApplicationContext(), "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                if (create) {
                    localHashMap.put("id", id);
                }
                localHashMap.put("name", name);
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(Appconfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq);
    }

    private void showBottomCateDialog(Integer position, CategoryModel categoryModel) {
        mBottomSheetDialog = new RoundedBottomSheetDialog(MainActivityFolder.this);
        LayoutInflater inflater = MainActivityFolder.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.create_cate_screen, null);
        TextInputEditText nameText = dialogView.findViewById(R.id.name);

        ExtendedFloatingActionButton submit = dialogView.findViewById(R.id.submit);
        if (position > 0) {
            nameText.setText(categoryModel.getTitle());
        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nameText.getText().toString().length() <= 0) {
                    nameText.setError("Enter valid name");
                    nameText.requestFocus();
                } else {
                    cateRetail(position > 0, position > 0 ? categoryModel.getId() : null, nameText.getText().toString());
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

    private void cateRetail(boolean create, String id, String name) {
        progressDialog.setMessage(create ? "Updating ..." : "Creating ...");
        showDialog();
        int method = Request.Method.POST;
        if (create) {
            method = Request.Method.PUT;
        }
        String url = RETAIL_CATEGORY;
        StringRequest strReq = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    String msg = jsonObject.getString("message");
                    if (success) {
                        if (mBottomSheetDialog != null) {
                            mBottomSheetDialog.dismiss();
                        }
                        fetchCategory();
                    }
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(getApplicationContext(), "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                if (create) {
                    localHashMap.put("id", id);
                }
                localHashMap.put("title", name);
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(Appconfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq);
    }

    @Override
    public void onDeleteClick(CategoryModel folderModel) {

    }

    @Override
    public void onEditClick(CategoryModel categoryModel) {
        showBottomCateDialog(1, categoryModel);
    }
}

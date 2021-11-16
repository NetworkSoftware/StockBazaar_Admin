package customer.smart.support.cmobile;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

public interface FragmentBecameVisible {
    View startDemo(LayoutInflater inflater, ViewGroup container);

    void fragmentBecameVisible();

    boolean onCreateOptionsMenu(Menu menu);

    void onBackPressed();

    abstract boolean onSupportNavigateUp();
}

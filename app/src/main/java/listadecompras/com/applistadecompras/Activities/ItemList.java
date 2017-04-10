package listadecompras.com.applistadecompras.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import listadecompras.com.applistadecompras.Fragments.ItemsFragment;
import listadecompras.com.applistadecompras.R;

/**
 * Created by Perajim on 20/02/2017.
 */
public class ItemList extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemlist);
        Bundle intent = getIntent().getExtras();
        if(savedInstanceState == null){
            ItemsFragment fragment = new ItemsFragment();
            fragment.setArguments(intent);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.list_item, fragment).commit();
        }
    }
}

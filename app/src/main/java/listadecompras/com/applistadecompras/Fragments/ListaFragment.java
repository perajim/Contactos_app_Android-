package listadecompras.com.applistadecompras.Fragments;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import listadecompras.com.applistadecompras.Activities.MainActivity;
import listadecompras.com.applistadecompras.Helpers.DataBaseHelper;
import listadecompras.com.applistadecompras.R;
import listadecompras.com.applistadecompras.RecyclerItemClickListener;
import listadecompras.com.applistadecompras.adapters.ListaAdapter;

/**
 * Created by Perajim on 09/02/2017.
 */

public class ListaFragment extends Fragment {
    RecyclerView recyclerView;
    ListaAdapter adapter;
    DataBaseHelper myDBHelper;
    FloatingActionButton fb;
    String listarel;
    TextView delete;
    private static final String TAG = "RecyclerViewFragment";

    public interface  CallBacks{
        public void onItemSelected(String nombrelista, String lista);
    }
    public interface Refresh{
        public void refreshList();
    }
    //String[] Data =  new String[]{"ELemento1", "Elemento 2", "Elemento 3", "Elemento 4", "Elemento5"};
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.lista_fragment, container, false);
        rootView.setTag(TAG);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.lista);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        fb = (FloatingActionButton) rootView.findViewById(R.id.fb);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setMessage("Nombre de la lista Nueva").
                        setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try{
                                Cursor cursor = myDBHelper.fetchAlllist();
                                    if(cursor != null){
                                        cursor.moveToLast();
                                        listarel = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0)));
                                        cursor.close();

                                    }
                                }
                                catch (SQLException e){
                                    e.printStackTrace();
                                }
                                DataBaseHelper myDbHelper = new DataBaseHelper(getActivity().getApplicationContext());
                                SQLiteDatabase db = myDbHelper.getWritableDatabase();
                                ContentValues valores = new ContentValues();
                                valores.put("nombre",input.getText().toString());
                                valores.put("listarel","Lista"+listarel);
                                String l = "Lista"+listarel;
                                db.insert("listas",null,valores);
                                db.execSQL("CREATE TABLE '" + l + "' ('_id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE , 'item' TEXT)");
                                ContentValues v = new ContentValues();
                                v.put("item","Item A");
                                db.insert(l, null,v);
                                db.close();
                                try{
                                    Cursor cursor = myDBHelper.fetchAlllist();
                                    if(cursor!= null){
                                        adapter = new ListaAdapter(getActivity().getApplicationContext(), cursor);
                                        recyclerView.setAdapter(adapter);
                                        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity().getApplicationContext(), new OnItemClickListener()));

                                    }
                                }catch(SQLException e){
                                    e.printStackTrace();
                                }

                            }
                        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }

        });
        myDBHelper = new DataBaseHelper(getActivity().getApplicationContext());

        try {
            myDBHelper.createDataBase();
        } catch (IOException e) {
            throw new Error("No se puede crear BD");
        }

        try {
            myDBHelper.openDatabase();
            Cursor cursor = myDBHelper.fetchAlllist();
            if(cursor != null){
                adapter = new ListaAdapter(getActivity().getApplicationContext(), cursor);
                recyclerView.setAdapter(adapter);
                recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity().getApplicationContext(), new  OnItemClickListener()) );
            }
        }catch (SQLException e){

        }
        //adapter = new ListaAdapter(getActivity().getApplicationContext(), cursor);
        //recyclerView.setAdapter(adapter);
        return  rootView;
    }

    private class OnItemClickListener extends RecyclerItemClickListener.SimpleOnItemClickListener{
        @Override
        public void onItemClick(View childview, int position){
            TextView textView = (TextView) childview.findViewById(R.id.title);
            TextView listanombre = (TextView) childview.findViewById(R.id.listanombre);
            ((CallBacks) getActivity()).onItemSelected(textView.getText().toString(), listanombre.getText().toString());
            //Toast.makeText(getActivity(), textView.getText().toString(), Toast.LENGTH_LONG).show();

        }
        @Override
        public void onItemLongPress(View childview, int position){
            delete = (TextView)childview.findViewById(R.id.listanombre);
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle("Eliminar Lista");
            dialog.setMessage("Seguro deseas eliminar esta lista?");
            dialog.setNegativeButton("Cancelar", null);
            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    myDBHelper = new DataBaseHelper(getActivity().getApplication());
                    SQLiteDatabase db = myDBHelper.getWritableDatabase();
                    String where = "listarel"+ "=?";
                    String [] whereArgs = new String[] {delete.getText().toString()};
                    db.delete("listas", where,whereArgs);
                    db.execSQL("DROP TABLE IF EXISTS "+ delete.getText().toString());
                    db.close();
                    Toast.makeText(getActivity(),"Se borro de la lista"+delete.getText().toString(), Toast.LENGTH_SHORT).show();
                     try{
                         myDBHelper.openDatabase();
                         Cursor nCursor = myDBHelper.fetchAlllist();
                         if(nCursor != null){
                             adapter = new ListaAdapter(getActivity().getApplicationContext(),nCursor);
                             recyclerView.setAdapter(adapter);
                             recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new OnItemClickListener()));
                         }
                         if(MainActivity.mTwoPane){
                             ((Refresh)getActivity()).refreshList();
                         }
                     }catch(SQLException e){}

                }
            });
            dialog.show();
        }

    }

}

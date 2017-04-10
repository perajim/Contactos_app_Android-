package listadecompras.com.applistadecompras.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import listadecompras.com.applistadecompras.EnviarMensaje;
import listadecompras.com.applistadecompras.R;




/**
 * Created by Perajim on 08/02/2017.
 */



public class Izquierda extends Fragment {
    View rootView;
    EditText campo;
    Button boton;
    EnviarMensaje EM;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        rootView = inflater.inflate(R.layout.izquierda, container, false);
        campo = (EditText) rootView.findViewById(R.id.campotxt);
        boton = (Button) rootView.findViewById(R.id.boton);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensaje = campo.getText().toString();
                EM.enviarDatos(mensaje);

            }
        });
        return  rootView;
     }

    public void onAttach(Activity activity){
        super.onAttach(activity);
        try {
            EM = (EnviarMensaje) activity;
        }catch (ClassCastException e) {
            throw new ClassCastException("Necesitas Implementar");
        }

    }

}

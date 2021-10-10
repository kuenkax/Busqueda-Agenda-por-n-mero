package org.izv.ad.acl.consultaagendaad;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.UserDictionary;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final int CONTACTS_PERMISSION = 1;
    private final String TAG= "xyzyx";

    private Button btSearch;
    private EditText etPhone;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(TAG, "He entrado on create");
        initialize();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "He entrado on destroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "He entrado on pause");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.v(TAG, "onRequestPermissions");
            switch (requestCode){
                case CONTACTS_PERMISSION:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        search();
                    break;
                default:
                    break;
            }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "He entrado on resume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "He entrado on start");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "He entrado on stop");
    }

    private void explain(){
        showRationaleDialog(getString(R.string.title), getString(R.string.message), Manifest.permission.READ_CONTACTS, CONTACTS_PERMISSION);
    }

    private void initialize(){
        btSearch = findViewById(R.id.btSearch);
        etPhone = findViewById(R.id.etPhone);
        tvResult = findViewById(R.id.tvResult);

        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchIfPermited();
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission(){
        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, CONTACTS_PERMISSION);
    }

    private void search(){

        Uri uri2 = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String proyeccion2[] = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };
        String seleccion2 = null;
        String argumentos2[] = null;
        String orden2 = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
        Cursor cursor2 = getContentResolver().query(uri2, proyeccion2, seleccion2, argumentos2, orden2);
        int columnaNombre = cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int columnaNumero = cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

        String nombre, numero;

        while (cursor2.moveToNext()){
            nombre = cursor2.getString(columnaNombre);
            numero = cursor2.getString(columnaNumero);
            numero = numero.replaceAll("[^0-9]", "");
            if (numero.equals(etPhone.getText().toString())){
                Log.v(TAG, nombre + ": " + numero);
                tvResult.setText(nombre);
            }

        }
    }

    private void searchIfPermited(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {                                       //Es la version despues de la 6?

            if (ContextCompat.checkSelfPermission(                                                  //Tengo el permiso?
                    this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
            {
                //Tengo el permiso
                search();
            }
            else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS))       //Lo tengo que pedir?
            {
                //No lo tengo pero tengo que pedirlo
                explain(); //2ª ejecución

            } else {
                //No lo tengo y no lo quiero pedir de manera educada
                requestPermission(); //1ª Ejecución
            }
        } else {
            //version anterior a la 6, ya tengo el permiso
            search();
        }
    }

    private void showRationaleDialog(String title, String message, String permission, int requestCode){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(message).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //nada
            }
        }).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermission();
            }
        });
        builder.create().show();
    }

}
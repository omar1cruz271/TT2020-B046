package com.example.recibemsm;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class FormActivity extends Activity {
    SQLiteDatabase sqld;
    Spinner s;
    Intent itn;
    Button registrar;
    String nombre, apellidoP, apellidoM, curp, nacimiento, sexo, numero;
    EditText nombreET, apellidoPET, apellidoMET, curpET, nacimientoET, numeroET;
    @Override
    protected void onCreate( Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.registro_form);
        registrar=(Button) findViewById(R.id.registrar);
        numeroET= (EditText) findViewById(R.id.numero);
        nombreET= (EditText) findViewById(R.id.editNombre);
        apellidoPET=(EditText) findViewById(R.id.ApellidoP);
        apellidoMET=(EditText) findViewById(R.id.ApellidoM);
        curpET= (EditText) findViewById(R.id.curp);
        nacimientoET= (EditText) findViewById((R.id.nacimiento));
        DbmsSQLiteHelper2 dsqlh = new DbmsSQLiteHelper2(this, "DBpacientes", null, 1);

        s = (Spinner) findViewById(R.id.spinner);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> av, View v, int i, long l){
                sexo=s.getSelectedItem().toString();
                Toast.makeText(FormActivity.this, s.getSelectedItem().toString(),
                        Toast.LENGTH_LONG).show();
            }
            public void onNothingSelected(AdapterView<?> arg0){ }
        });
        registrar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                nombre = nombreET.getText().toString();
                apellidoP=apellidoPET.getText().toString();
                apellidoM=apellidoMET.getText().toString();
                curp=curpET.getText().toString();
                nacimiento=nacimientoET.getText().toString();
                numero=numeroET.getText().toString();
                sqld = dsqlh.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("nombre",nombre);
                cv.put("apellidoP",apellidoP);
                cv.put("apellidoM",apellidoM);
                cv.put("nacimiento",nacimiento);
                cv.put("numero", numero);
                cv.put("curp", curp);
                cv.put("sexo", sexo);
                sqld.insert("Pacientes_Registrados", null, cv);
                Toast.makeText(FormActivity.this, "Se ha registrado a "+nombre, Toast.LENGTH_LONG).show();
                itn = new Intent(FormActivity.this, MainActivity.class);
                startActivity(itn);
            }
        });

    }
}

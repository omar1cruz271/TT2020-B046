package com.example.recibemsm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase sqld;
    String NumeroOrigen;
    String ID;
    String PresionD;
    String PresionS;
    String Fecha;
    String Hora;
    TextView jtvL;
    Button jbnL, btnRegistro,listaPacientes;
    Intent itn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        jtvL= (TextView) findViewById(R.id.xtvL);
        btnRegistro= (Button) findViewById(R.id.registro);
        listaPacientes=(Button) findViewById(R.id.lisbtn) ;
        jbnL= (Button) findViewById(R.id.xbnL);
        DbmsSQLiteHelper dsqlh = new DbmsSQLiteHelper(this, "DBpacientes", null, 1);
        sqld = dsqlh.getWritableDatabase();
        /*ContentValues cv = new ContentValues();
        String fecha= ObtenerFecha();
        cv.put("presionD","120");
        cv.put("presionS","80");
        cv.put("fecha",fecha);
        cv.put("hora","10:42");
        cv.put("numero", "5584571782");
        sqld.insert("Pacientes", "id", cv);

         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS}, 1000);

        }
        jbnL.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String id, presionD, presionS, fecha, hora, numero;
                try {
                    Cursor c = sqld.rawQuery("SELECT * FROM Pacientes", null);
                    jtvL.setText(" id"+ "      Numero" +"      \tPresionS" +"   PresionD" +"\t  Fecha      Hora" + "\n");
                    if (c.moveToFirst()) {
                        do {
                            id = c.getString(0);
                            presionD = c.getString(1);
                            presionS=c.getString(2);
                            fecha = c.getString(3);
                            hora = c.getString(4);
                            numero = c.getString(5);
                            jtvL.append(" " + id + "\t" + numero +"\t        " + presionS +"\t       " + presionD +"\t       " +fecha+" "+hora+ "\n");
                        } while(c.moveToNext());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
        listaPacientes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String nombre, apellidoP, apellidoM, curp, nacimiento, sexo, numero;
                try {

                    Cursor c = sqld.rawQuery("SELECT * FROM Pacientes_Registrados", null);
                    jtvL.setText(" CURP"+ "\t\t                                  Nombre"+ "\t      Numero" );
                    if (c.moveToFirst()) {
                        do {
                            curp = c.getString(0);
                            nombre = c.getString(1);
                            nacimiento=c.getString(4);
                            sexo = c.getString(5);
                            numero = c.getString(6);
                            jtvL.append("\n" + curp + "\t" + nombre +"\t        " +numero+ "\n");
                        } while(c.moveToNext());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                itn = new Intent(MainActivity.this, FormActivity.class);
                startActivity(itn);
            }
        });
        //tx= (TextView) findViewById(R.id.xtv1);
        //tx.setText("El contenido es: "+ cuerpoMSM);
    }
    public String ObtenerFecha(){

        Date objDate = new Date(); // Sistema actual La fecha y la hora se asignan a objDate
        Log.d( "ObtenerFecha: ",objDate.toString());
        String strDateFormat = "dd-MMM-yyyy"; // El formato de fecha está especificado
        SimpleDateFormat objSDF = new SimpleDateFormat(strDateFormat); // La cadena de formato de fecha se pasa como un argumento al objeto
        Log.d("fecha",objSDF.format(objDate));
        return objSDF.format(objDate);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission not granted!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }


    }

    recibeSMS receiver= new recibeSMS(){
        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
            TextView tv=  (TextView) findViewById(R.id.xtv1);
            tv.setText("El número "+origen+ " ha enviado datos, presiona REGISTRO DE PRESIONES para ver la información");
            datos(cuerpoMSM,origen);
            if(consultaBD(origen)) {
                DbmsSQLiteHelper dsqlh = new DbmsSQLiteHelper(context, "DBpacientes", null, 1);
                sqld = dsqlh.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("presionS", PresionS);
                cv.put("presionD", PresionD);
                cv.put("fecha", Fecha);
                cv.put("hora", Hora);
                cv.put("numero", NumeroOrigen);
                sqld.insert("Pacientes", "id", cv);
                Toast.makeText(context, "Nuevo registro de presión recibido", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(context, "No se ha registrado este número", Toast.LENGTH_SHORT).show();
            }

        }
    };
    public boolean consultaBD(String numero) {
        String numeroConsulta;
        Cursor c = sqld.rawQuery("SELECT * FROM Pacientes_Registrados", null);
        if (c.moveToFirst()) {
            do {
                numeroConsulta = c.getString(6);
                if(numeroConsulta.equals(numero)) return true;
            } while(c.moveToNext());
        }
        return false;
    }
    public void datos(String body, String numero){
        String[] elementos=body.split(";");
        this.PresionD=elementos[0];
        this.PresionS=elementos[1];
        this.Fecha=ObtenerFecha();
        this.Hora=elementos[2];
        this.Hora+=":"+elementos[3];
        this.NumeroOrigen=numero;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the receiver

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(receiver , filter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the receiver to save unnecessary system overhead
        // Paused activities cannot receive broadcasts anyway
        try {
            if(receiver != null){
                unregisterReceiver(receiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
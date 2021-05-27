package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.Result;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Main3Activity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView scaner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4_3);
    }
    public void buttonPausarrr(View view)
    {
        String fini = getIntent().getStringExtra("fini");
        //MainActivity3 mainActivity3 = new MainActivity3();
        //mainActivity3.buttonReanudar(view);
        calculate(shoraini,fini);

        scaner = new ZXingScannerView(this);
        setContentView(scaner);
        scaner.setResultHandler(this);
        scaner.startCamera();
        ejecutarServiciosPausa("http://192.168.0.8/TrabajosPHP/insertar_producto.php");
    }
    Date date = new Date();
    SimpleDateFormat fechaC = new SimpleDateFormat("dd'/'MM'/'yyyy");
    SimpleDateFormat horaC = new SimpleDateFormat("HH:mm:ss");
    String sfecha = fechaC.format(date);
    String shoraini = horaC.format(date);
    public void handleResult(Result result) {
        //clase date



        Log.v("HandleResult",result.getText());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Fecha actual de ingreso:\n" + sfecha + " " + shoraini)
                .setCancelable(false)
                .setPositiveButton("regresar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(),MainActivity2.class);
                        intent.putExtra("fini",shoraini);
                        //intent.putExtra("ffin",u);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.setMessage(result.getText());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        //scaner.resumeCameraPreview(this);
    }
    private final int SECONDS_IN_ONE_DAY = 86_400;
    private final int SECONDS_IN_ONE_HOUR = 3_600;
    private final int SECONDS_IN_ONE_MINUTE = 60;

    int hours = 0;
    int minutes = 0;
    int difference = 0;
    private void calculate(String hora1, String hora2) {
        String shorafin = "11:00:00";
        String fini = getIntent().getStringExtra("fini");
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            Date startDate = simpleDateFormat.parse(shoraini);
            Date endDate = simpleDateFormat.parse(fini);

            long start = startDate.getTime() / 1000;
            long end = endDate.getTime() / 1000;

            if(end < start)
                end += SECONDS_IN_ONE_DAY;

            difference = (int) (end - start);

            int days = difference / SECONDS_IN_ONE_DAY;
            difference %= SECONDS_IN_ONE_DAY;
            hours = difference / SECONDS_IN_ONE_HOUR;
            difference %= SECONDS_IN_ONE_HOUR;
            minutes = difference / SECONDS_IN_ONE_MINUTE;
            difference %= SECONDS_IN_ONE_MINUTE;

            //System.out.println("Days: " + days + ", Hours: " + hours + ", Mins: " + minutes + ", Seconds: " + difference);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private void ejecutarServiciosPausa(String URL)
    {
        String name = getIntent().getStringExtra("nombre");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(), "Operacion Exitosa", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("idasistencia","");
                parametros.put("dia",sfecha+" "+shoraini);
                parametros.put("horas",hours+":"+minutes+":"+difference);
                parametros.put("fkidempleados",name);
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
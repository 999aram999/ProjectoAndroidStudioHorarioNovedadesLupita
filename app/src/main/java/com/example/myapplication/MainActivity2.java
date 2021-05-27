package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.Result;
import com.walkiriaapps.alarmmanagersampl.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.SimpleFormatter;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class MainActivity2 extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scaner;

    private String notificationsTime;
    private int alarmID = 1;
    private SharedPreferences settings;
    //private String fini = getIntent().getStringExtra("fini");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction();
        recuperarPreferenciasEntrada();
        recuperarPreferenciasPausa();
        recuperarPreferenciasReanudar();
        recuperarPreferenciasFin();
        recuperarPreferenciasHorasReanudar();
        //agregarEntrada();}

        settings = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);

        String hour, minute;

        hour = settings.getString("hour","");
        minute = settings.getString("minute","");

        notificationsTime = shorapausar;//(TextView) findViewById(R.id.notifications_time)

        if(hour.length() > 0)
        {
            //notificationsTime; //.setText(hour + ":" + minute)
        }

        findViewById(R.id.change_notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(MainActivity2.this, new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("StringFormatInvalid")
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String finalHour, finalMinute;

                        finalHour = "" + selectedHour;
                        finalMinute = "" + selectedMinute;
                        if (selectedHour < 10) finalHour = "0" + selectedHour;
                        if (selectedMinute < 10) finalMinute = "0" + selectedMinute;
                        notificationsTime.setText(finalHour + ":" + finalMinute);

                        Calendar today = Calendar.getInstance();

                        today.set(Calendar.HOUR_OF_DAY, selectedHour);
                        today.set(Calendar.MINUTE, selectedMinute);
                        today.set(Calendar.SECOND, 0);

                        SharedPreferences.Editor edit = settings.edit();
                        edit.putString("hour", finalHour);
                        edit.putString("minute", finalMinute);

                        //SAVE ALARM TIME TO USE IT IN CASE OF REBOOT
                        edit.putInt("alarmID", alarmID);
                        edit.putLong("alarmTime", today.getTimeInMillis());

                        edit.commit();

                        Toast.makeText(MainActivity2.this, getString(R.string.changed_to, finalHour + ":" + finalMinute), Toast.LENGTH_LONG).show();

                        Utils.setAlarm(alarmID, today.getTimeInMillis(), MainActivity2.this);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle(getString(R.string.select_time));
                mTimePicker.show();

            }
        });

    }
    private static void setAlarm(int i, Long timestamp, Context ctx)
    {
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(ctx, AlarmReceiver.class);
        PendingIntent pendingIntent;
        pendingIntent = PendingIntent.getBroadcast(ctx, i, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
        alarmIntent.setData((Uri.parse("custom://" + System.currentTimeMillis())));
        alarmManager.set(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent);
    }
//    private void guardarPreferencias()
//    {
//        String usuario, password;
//        EditText txtU =  findViewById(R.id.txtUser);
//        EditText txtP =  findViewById(R.id.txtPassword);
//        String u = txtU.getText().toString();
//        String p = txtP.getText().toString();
//        usuario=txtU.getText().toString();
//        password=txtP.getText().toString();
//        SharedPreferences preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString("Horas",shoraini);
//        editor.putBoolean("sesion",true);
//        editor.commit();
//    }
    private void guardarPreferenciasEntrada()
    {
        SharedPreferences preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("entrada",shorainicio);
        editor.putBoolean("sesion",true);
        editor.commit();
    }

    private void recuperarPreferenciasEntrada()
    {
        SharedPreferences preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        shorainicio = preferences.getString("entrada","");
    }
    private void guardarPreferenciasPausa()
    {
        SharedPreferences preferences = getSharedPreferences("preferenciasLoginPausa", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("pausar",shorapausar);
        editor.putBoolean("sesionp",true);
        editor.commit();
    }

    private void recuperarPreferenciasPausa()
    {
        SharedPreferences preferences = getSharedPreferences("preferenciasLoginPausa", Context.MODE_PRIVATE);
        shorapausar = preferences.getString("pausar","");
    }
    private void guardarPreferenciasReanudar()
    {
        SharedPreferences preferences = getSharedPreferences("preferenciasLoginReanudar", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("reanudar",shorareanudar);
        editor.putBoolean("sesionr",true);
        editor.commit();
    }

    private void recuperarPreferenciasReanudar()
    {
        SharedPreferences preferences = getSharedPreferences("preferenciasLoginReanudar", Context.MODE_PRIVATE);
        shorareanudar = preferences.getString("reanudar","");
    }
    private void guardarPreferenciasFin()
    {
        SharedPreferences preferences = getSharedPreferences("preferenciasLoginFin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("finH",shorafin);
        editor.putBoolean("sesionf",true);
        editor.commit();
    }

    private void recuperarPreferenciasFin()
    {
        SharedPreferences preferences = getSharedPreferences("preferenciasLoginFin", Context.MODE_PRIVATE);
        shorafin = preferences.getString("finH","");
    }
    private void guardarPreferenciasHorasReanudar()
    {
        SharedPreferences preferences = getSharedPreferences("preferenciasLoginHorasReanudar", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("horascalculadasreanudar",shoracalculadareanudar);
        editor.putString("minutoscalculadasreanudar",sminutoscalculadareanudar);
        editor.putString("segundoscalculadasreanudar",ssegundoscalculadareanudar);
        editor.putBoolean("sesionhr",true);
        editor.commit();
    }

    private void recuperarPreferenciasHorasReanudar()
    {
        SharedPreferences preferences = getSharedPreferences("preferenciasLoginHorasReanudar", Context.MODE_PRIVATE);
        shoracalculadareanudar = preferences.getString("horascalculadasreanudar","");
        sminutoscalculadareanudar = preferences.getString("minutoscalculadasreanudar","");
        ssegundoscalculadareanudar = preferences.getString("segundoscalculadasreanudar","");
    }
    String shorafin = "";
    String shorainicio = "";
    String shorapausar = "";
    String shorareanudar = "";
    String shoracalculadareanudar = "";
    String sminutoscalculadareanudar = "";
    String ssegundoscalculadareanudar = "";
    String key = "my key";
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void buttonPausar(View view)
    {
//        Intent intent = new Intent(getApplicationContext(),Main3Activity.class);
//        startActivity(intent);
//        String fini = getIntent().getStringExtra("fini");
//        Main3Activity mainActivity3 = new Main3Activity();
//        mainActivity3.buttonPausarrr(view);
//        guardarPreferencias();



        scaner = new ZXingScannerView(this);
        setContentView(scaner);
        scaner.setResultHandler(this);
        scaner.startCamera();
        ejecutarServiciosPausa("http://192.168.0.8/TrabajosPHP/insertar_producto.php");

//        Context context = this;
//        SharedPreferences sharedPreferences = getSharedPreferences("ArchivoSP", context.MODE_PRIVATE);
//        //SharedPreferences sharedPs = getPreferences(context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("horaSalida", horas);
////
////        shorafin = sharedPreferences.getString("horaSalida","");
//        editor.commit();



        //String prefs = PreferenceManager.getDefaultSharedPreferences(this);

        shorapausar = horas;

        guardarPreferenciasPausa();



    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void buttonReanudar(View v)
    {
        scaner = new ZXingScannerView(this);
        setContentView(scaner);
        scaner.setResultHandler(this);
        scaner.startCamera();
        //cambiaaaar
        ejecutarServiciosReanudar("http://192.168.0.8/TrabajosPHP/insertar_producto.php");
        shorareanudar = horas;
        //guardarpreferencias
//          shoracalculadareanudar = Integer.toString(hours);
//          sminutoscalculadareanudar = Integer.toString(mins);
//        ssegundoscalculadareanudar = Integer.toString(secs);
//        guardarPreferenciasHorasReanudar();
//        guardarPreferenciasHorasReanudar();
        guardarPreferenciasReanudar();
        calculateR(shorapausar,shorareanudar);
    }
    public void buttonEntrada(View v)
    {
        //conexionBD();
        scaner = new ZXingScannerView(this);
        setContentView(scaner);
        scaner.setResultHandler(this);
        scaner.startCamera();
        ejecutarServicios("http://192.168.0.8/TrabajosPHP/insertar_producto.php");
//
        //shorainicio = getIntent().getStringExtra("fini");
        shorainicio = horas;
//        Context context = this;
//        SharedPreferences sharedPreferences = getSharedPreferences("ArchivoSP", context.MODE_PRIVATE);
//        //SharedPreferences sharedPs = getPreferences(context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("horaEntrada", horas);
////      shorainicio = sharedPreferences.getString("horaEntrada",horas);
//        editor.commit();

        guardarPreferenciasEntrada();
        //agregarEntrada();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void buttonFinalizar(View v)
    {
        scaner = new ZXingScannerView(this);
        setContentView(scaner);
        scaner.setResultHandler(this);
        scaner.startCamera();
        //cambiaaaar
        ejecutarServiciosFinalizar("http://192.168.0.8/TrabajosPHP/insertar_producto.php");
        shorafin = horas;
        //guardarpreferencias
        guardarPreferenciasFin();
        calculate(shorainicio,shorafin);
    }
    Date date = new Date();
    SimpleDateFormat fechaC = new SimpleDateFormat("dd'/'MM'/'yyyy");
    SimpleDateFormat horaC = new SimpleDateFormat("HH:mm:ss");
    String sfecha = fechaC.format(date);
    String horas = horaC.format(date);

    @Override
    public void handleResult(Result result) {
        //clase date

         /*
         *   shoraini = 2
         *   shorafin = 1
         *   1-2 = -1
         *
         * */

        Log.v("HandleResult",result.getText());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Fecha actual de ingreso:\n" + sfecha + " " + horas)
        .setCancelable(false)
                .setPositiveButton("regresar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(),MainActivity2.class);
                        intent.putExtra("fini",horas);
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
    private void ejecutarServicios(String URL)
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
                //Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("idasistencia","");
                parametros.put("dia",sfecha+" "+shorainicio);
                parametros.put("horas",shorainicio);
                parametros.put("fkidempleados",name);
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
/*    public Connection conexionBD()
    {
        Connection conexion = null;
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Class.forName("com.mysql.jdbc.Driver"); //.newInstance();
            // "net.sourceforge.jtds.jdbc.Driver"
            conexion = DriverManager.getConnection("jdbc:mysql://192.168.0.8:3306/horario" ,"root","Aa1999129");
            // "jdbc:jtds:sqlserver://192.168.0.8;databaseName=horario;user=root;password=Aa1999129;"
        }catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        return conexion;
    }

    public void agregarEntrada()
    {

        try {
            String name = getIntent().getStringExtra("nombre");
        PreparedStatement pst = conexionBD().prepareStatement("insert into empleados values(?,?,?,?)");
        //pst.setString(1,"3");
        pst.setInt(1,'3');
        pst.setString(2,"abc");
        pst.setString(3,"CAJA");
        pst.setInt(4,'9');
        pst.executeUpdate();

            Toast.makeText(getApplicationContext(),"Registro de entrada correcto",Toast.LENGTH_SHORT).show();
        }catch (SQLException e)
        {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }*/
//    private final int SECONDS_IN_ONE_DAY = 86_400;
//    private final int SECONDS_IN_ONE_HOUR = 3_600;
//    private final int SECONDS_IN_ONE_MINUTE = 60;
//
//    int hours = 0;
//    int minutes = 0;
//    int difference = 0;
    int hours = 0 ;
    int mins = 0 ;
    int secs = 0 ;
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void calculate(String shoraini, String shorafin) {
        try {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            Date startDate = simpleDateFormat.parse(shoraini);
            Date endDate = simpleDateFormat.parse(shorafin);

            //method to get time between values
            Duration dur = Duration.between(startDate.toInstant(), endDate.toInstant());

            //variable to get the difference of time in seconds
            int value = (int) dur.getSeconds();

            //begins the magic
             hours = value / 3600;
            int remainder = (int) value - hours * 3600;
             mins = remainder / 60;
            remainder = remainder - mins * 60;
             secs = remainder;

            //array to save the values
            int[] time = {hours , mins , secs};
//        shoracalculadareanudar = Integer.toString(hours);
//        sminutoscalculadareanudar = Integer.toString(mins);
//        ssegundoscalculadareanudar = Integer.toString(secs);
//        guardarPreferenciasHorasReanudar();
            //print the values
            //System.out.println("Hours: " + time[0] + ", Minutes: " + time[1] + ", Seconds: " + time[2]);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        //String shorafin = "11:00:00";
        //String fini = getIntent().getStringExtra("fini");
//        try {
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
//            Date startDate = simpleDateFormat.parse(shoraini);
//            Date endDate = simpleDateFormat.parse(shorafin);//fini
//
//            long start = startDate.getTime() / 1000;
//            long end = endDate.getTime() / 1000;
//
//            if(end < start)
//                end += SECONDS_IN_ONE_DAY;
//
//            difference = (int) (end - start);
//
//            int days = difference / SECONDS_IN_ONE_DAY;
//            difference %= SECONDS_IN_ONE_DAY;
//            hours = difference / SECONDS_IN_ONE_HOUR;
//            difference %= SECONDS_IN_ONE_HOUR;
//            minutes = difference / SECONDS_IN_ONE_MINUTE;
//            difference %= SECONDS_IN_ONE_MINUTE;
//
//            //System.out.println("Days: " + days + ", Hours: " + hours + ", Mins: " + minutes + ", Seconds: " + difference);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
    }
    int hoursR = 0 ;
    int minsR = 0 ;
    int secsR = 0 ;
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void calculateR(String shoraini, String shorafin) {
        try {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            Date startDate = simpleDateFormat.parse(shoraini);
            Date endDate = simpleDateFormat.parse(shorafin);

            //method to get time between values
            Duration dur = Duration.between(startDate.toInstant(), endDate.toInstant());

            //variable to get the difference of time in seconds
            int value = (int) dur.getSeconds();

            //begins the magic
            hoursR = value / 3600;
            int remainder = (int) value - hoursR * 3600;
            minsR = remainder / 60;
            remainder = remainder - minsR * 60;
            secsR = remainder;

            //array to save the values
            int[] time = {hoursR , minsR , secsR};
            shoracalculadareanudar = Integer.toString(hoursR);
            sminutoscalculadareanudar = Integer.toString(minsR);
            ssegundoscalculadareanudar = Integer.toString(secsR);
        guardarPreferenciasHorasReanudar();
            //print the values
            //System.out.println("Hours: " + time[0] + ", Minutes: " + time[1] + ", Seconds: " + time[2]);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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
                //Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("idasistencia","");
                parametros.put("dia",sfecha+" "+shorapausar);
                parametros.put("horas",shorapausar);
                parametros.put("fkidempleados",name);
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void ejecutarServiciosReanudar(String URL)
    {
//        shoracalculadareanudar = Integer.toString(hours);
//        sminutoscalculadareanudar = Integer.toString(mins);
//        ssegundoscalculadareanudar = Integer.toString(secs);
//        guardarPreferenciasHorasReanudar();
        String name = getIntent().getStringExtra("nombre");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(), "Operacion Exitosa", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        })
        {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("idasistencia","");
                parametros.put("dia",sfecha+" "+shorareanudar);
                parametros.put("horas",hoursR+":"+minsR+":"+secsR);
                parametros.put("fkidempleados",name);
                return parametros;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void ejecutarServiciosFinalizar(String URL)
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
                //Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("idasistencia","");
                parametros.put("dia",sfecha+" "+shorafin);
                parametros.put("horas","horas trabajadas:  "+hours+":"+mins+":"+secs+"  horas fuera del trabajo por algún tipo de permiso  "+shoracalculadareanudar+":"+sminutoscalculadareanudar+":"+ssegundoscalculadareanudar);
                parametros.put("fkidempleados",name);
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
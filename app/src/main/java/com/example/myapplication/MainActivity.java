package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction();
        recuperarPreferencias();
    }

    public void Siguiente(View view)
    {
        String usuario, password;
        EditText txtU =  findViewById(R.id.txtUser);
        EditText txtP =  findViewById(R.id.txtPassword);
        String u = txtU.getText().toString();
        String p = txtP.getText().toString();
        //ejecutarServicios("http://192.168.0.8:80/TrabajosPHP/conexion.php");
        /*if (u.equals("aram") && p.equals("123"))//
        {
            Intent siguiente = new Intent(this, MainActivity2.class );
            siguiente.putExtra("nombre",u);
            startActivity(siguiente);
        }*/
        usuario=txtU.getText().toString();
        password=txtP.getText().toString();
        if (!usuario.isEmpty() && !password.isEmpty())
        {
            validarusuario("http://192.168.0.8/TrabajosPHP/validar_usuario.php");
        }
        else
            {
                Toast.makeText(getApplicationContext(), "No se permiten campos vacios", Toast.LENGTH_SHORT).show();
            }
    }
    private void validarusuario(String URL)
    {
        String usuario, password;
        EditText txtU =  findViewById(R.id.txtUser);
        EditText txtP =  findViewById(R.id.txtPassword);
        String u = txtU.getText().toString();
        String p = txtP.getText().toString();
        usuario=txtU.getText().toString();
        password=txtP.getText().toString();
        /*if (!usuario.isEmpty() && !password.isEmpty())
        {

        }*/

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.isEmpty())
                {
                    guardarPreferencias();
                    Intent intent = new Intent(getApplicationContext(),MainActivity2.class);
                    intent.putExtra("nombre",u);
                    startActivity(intent);
                    finish();
                }
                else
                    {
                        Toast.makeText(getApplicationContext(), "Usuario o contraseña incorrecto", Toast.LENGTH_SHORT).show();
                    }

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
                parametros.put("usuario",usuario);
                parametros.put("password",password);
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void guardarPreferencias()
    {
        String usuario, password;
        EditText txtU =  findViewById(R.id.txtUser);
        EditText txtP =  findViewById(R.id.txtPassword);
        String u = txtU.getText().toString();
        String p = txtP.getText().toString();
        usuario=txtU.getText().toString();
        password=txtP.getText().toString();
        SharedPreferences preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("usuario",usuario);
        editor.putString("password", password);
        editor.putBoolean("sesion",true);
        editor.commit();
    }

    private void recuperarPreferencias()
    {
        String usuario, password;
        EditText txtU =  findViewById(R.id.txtUser);
        EditText txtP =  findViewById(R.id.txtPassword);
        String u = txtU.getText().toString();
        String p = txtP.getText().toString();
        usuario=txtU.getText().toString();
        password=txtP.getText().toString();
        SharedPreferences preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        txtU.setText(preferences.getString("usuario",""));
        txtP.setText(preferences.getString("password",""));
    }

    /*private void ejecutarServicios(String URL)
    {
        //String name = getIntent().getStringExtra("nombre");
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
                parametros.put("idempleados","3");
                parametros.put("nombre","aram");
                parametros.put("contraseña","123");
                parametros.put("puesto","CABALLERO");
                parametros.put("sueldobase","1200");
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }*/
}
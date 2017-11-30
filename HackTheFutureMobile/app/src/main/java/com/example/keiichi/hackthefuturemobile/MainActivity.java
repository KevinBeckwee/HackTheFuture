package com.example.keiichi.hackthefuturemobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvCardText;
    Button btStartScan;
    Button btnMines;
    private String QRCODE;
    private String ACCES_Code;
    private final String URL = "http://37.230.98.72/htf/api/";
    RequestQueue queue;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCardText = (TextView)findViewById(R.id.tv_code_text);
        btStartScan = (Button)findViewById(R.id.btn_scan);
        btnMines = findViewById(R.id.mineBtn);

        btnMines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    getMines();
                } catch (AuthFailureError authFailureError) {
                    authFailureError.printStackTrace();
                }

            }
        });
        
         queue = Volley.newRequestQueue(this);
         startQRScanner();

    }


    private void startQRScanner() {
        new IntentIntegrator(this).initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result =   IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this,    "Cancelled",Toast.LENGTH_LONG).show();
            } else {
                updateText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updateText(String scanCode) {
        tvCardText.setText(scanCode);
        QRCODE = scanCode;
    }

    @Override
    public void onClick(View view) {
        try {
            login();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public void login() throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(this);
        Map<String,String> data = new HashMap<>();
        data.put("qrCode", QRCODE);

        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, URL + "auth/login", new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            ACCES_Code = response.getString("accessToken");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println(response.toString());

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(jsonobj);

    }

    public void getMines() throws AuthFailureError{
        /*Map<String, String> params = new HashMap<>();
        params.put("Authorization", "Bearer " + ACCES_Code);
        JSONObject jsonObject = new JSONObject(params);
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, URL + "mines",
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    System.out.println(response.getJSONArray("Beaulieu"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            public Map<String,String> getHeaders(){
             Map<String, String> params = new HashMap<>();
             params.put("Authorization", "Bearer " + ACCES_Code);

             return params;
            }
        };

        queue.add(postRequest);*/
        StringRequest postRequest = new StringRequest(Request.Method.GET, URL + "mines", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }){

            public Map<String,String> getHeaders(){
                Map<String,String> params = new HashMap<>();
                params.put("Authorization", "Bearer "+ ACCES_Code);

                return params;
            }

        };
        queue.add(postRequest);
    }
}
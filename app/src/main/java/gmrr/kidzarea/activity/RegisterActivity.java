package gmrr.kidzarea.activity;

/**
 * Created by Rifqi Zuliansyah on 31/03/2016.
 */
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import gmrr.kidzarea.R;
import gmrr.kidzarea.app.AppConfig;
import gmrr.kidzarea.app.AppController;
import gmrr.kidzarea.helper.SQLiteHandler;
import gmrr.kidzarea.helper.SessionManager;

public class RegisterActivity extends Activity implements OnCheckedChangeListener {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLogin;
    private RadioButton radioParent,radioAnak;
    private RadioGroup radioGroup;
    private EditText inputUniqueID;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputUniqueIDOrtu;
    private String inputStatus;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
/*---------------*/
        inputUniqueID = (EditText) findViewById(R.id.uid);
        inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        radioParent = (RadioButton) findViewById(R.id.OrangTua);
            radioParent.setOnClickListener(null);
        radioAnak = (RadioButton) findViewById(R.id.Anak);
            radioAnak.setOnClickListener(null);
        radioGroup = (RadioGroup) findViewById(R.id.RadioGroupStatus);
            radioGroup.setOnCheckedChangeListener(this);
        inputUniqueIDOrtu = (EditText) findViewById(R.id.uid_ortu);

        OnCheckedChangeListener listener = new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radio, int checkedId) {
                // TODO Auto-generated method stub
                if (radioParent.isChecked()) {
                    inputStatus = "OrangTua";
                    //inputUniqueIDOrtu = inputUniqueID;
                }
                if (radioAnak.isChecked()) {
                    inputStatus = "Anak";
                }
            }
        };
        radioGroup.setOnCheckedChangeListener(listener);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
/*---------------*/

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this,
                    SetLocationActivity.class);
            startActivity(intent);
            finish();
        }

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String uid = inputUniqueID.getText().toString().trim();
                String uid_ortu = inputUniqueIDOrtu.getText().toString().trim();
                String name = inputFullName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String status = inputStatus;


                if (!uid.isEmpty() && !uid_ortu.isEmpty() && !name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !status.isEmpty()) {
                    registerUser(uid,uid_ortu,  name, email, password, status);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Silakan masukkan data yang benar!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void registerUser(final String uid, final String uid_ortu, final String name, final String email, final String password, final String status) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        JSONObject user = jObj.getJSONObject("user");
                        String uid = user.getString("uid");
                        String uid_ortu = user.getString("uid_ortu");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String password = user.getString("password");
                        String status = user.getString("status");
                        String created_at = user.getString("created_at");

                        // Inserting row in users table
                        db.addUser(uid, uid_ortu, name, email, password, status, created_at);

                        Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                RegisterActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid", uid);
                params.put("uid_ortu", uid_ortu);
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("status", status);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Silakan klik dua kali untuk keluar.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }
}
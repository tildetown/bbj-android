package town.tilde.bbj;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public String response = "Something went wrong";
    public String[] threads = new String[0];
    final ArrayList<String> list = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sendGet("http://127.0.0.1:7099/api/get_me");
        getUsername(response);

        final TextView textview1 = (TextView) findViewById(R.id.username);
        final Button button = (Button) findViewById(R.id.button1);
        final ListView thread_list = (ListView) findViewById(R.id.threadview);
        final ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.thread_layout,list);


        thread_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = thread_list.getItemAtPosition(position);
                String str = o.toString();
                Toast.makeText(getBaseContext(),str,Toast.LENGTH_SHORT).show();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                sendGet("http://127.0.0.1:7099/api/thread_index");
                getThreads(response);
                for (int i = 0; i < threads.length; ++i) {
                    list.add(threads[i]);
                }

                thread_list.setAdapter(adapter);

                Log.i("RESPONSE", response);
                for(int i = 0; i < threads.length; i++) {
                    Log.i("RESPONSE", threads[i]);
                }
                //JSONObject jsonParam = new JSONObject();
                //jsonParam.put("timestamp", 1488873360);
                //jsonParam.put("latitude", 0D);
                //jsonParam.put("longitude", 0D);


            }
        });

    }

    public void getThreads(String responseData) {

        try {
            JSONObject jObject1 = new JSONObject(responseData);
            Log.i("RESPONSE",jObject1.toString());
            JSONArray jArray1 = jObject1.getJSONArray("data");
            Log.i("RESPONSE",jArray1.toString());
            String[] localthreads = new String[jArray1.length()];
            for(int i = 0; i < jArray1.length(); i++) {
                JSONObject jObject2 = jArray1.getJSONObject(i);
                String thread_name = jObject2.getString("title");
                localthreads[i] = thread_name;
                Log.i("RESPONSE", thread_name);
            }
            threads = localthreads;
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getUsername(String responseData) {
        try {
        JSONObject jObject1 = new JSONObject(responseData);
        JSONObject jObject2 = new JSONObject(jObject1.getString("data"));
        String username = jObject2.getString("user_name");
        final TextView textView = (TextView) findViewById(R.id.username);
        textView.setText(username);
    } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendPost(final String endpoint, final JSONObject jsonParam) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(endpoint);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    InputStream in = conn.getInputStream();
                    InputStreamReader isw = new InputStreamReader(in);
                    int data = isw.read();
                    StringBuilder sb = new StringBuilder();
                    while (data != -1) {
                        char current = (char) data;
                        data = isw.read();
                        sb.append(current);
                    }
                    String responseData = sb.toString();
                    response = responseData;

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    public void sendGet(final String endpoint) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(endpoint);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    InputStream in = conn.getInputStream();
                    InputStreamReader isw = new InputStreamReader(in);
                    int data = isw.read();
                    StringBuilder sb = new StringBuilder();
                    while (data != -1) {
                        char current = (char) data;
                        data = isw.read();
                        sb.append(current);
                    }
                    String responseData = sb.toString();
                    response = responseData;
                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());
                    Log.i("RESPONSE" , responseData);
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }



    @Override
    public void onBackPressed() {
        Log.i("FUCKME","YOU PRESSED THE BACK BUTTON, WHAT A COOL GUY");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}


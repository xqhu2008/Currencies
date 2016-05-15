package com.bluehawk.alex.currencies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Button mCalcButton;
    private TextView mConvertedTextView;
    private EditText mAmountEditText;
    private Spinner mForeignSpinner, mHomeSpinner;
    private String[] mCurrencies;

    public static final String FOR = "FOR_CURRENCY";
    public static final String HOM = "HOM_CURRENCY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<String> arrayList = ((ArrayList<String>)getIntent().getSerializableExtra(SplashActivity.KEY_ARRAYLIST));
        Collections.sort(arrayList);
        mCurrencies = arrayList.toArray(new String[arrayList.size()]);

        mCalcButton = (Button)findViewById(R.id.btn_calc);
        mConvertedTextView = (TextView)findViewById(R.id.txt_converted);
        mAmountEditText = (EditText) findViewById(R.id.edt_amount);
        mForeignSpinner = (Spinner)findViewById(R.id.spn_foreign);
        mHomeSpinner = (Spinner)findViewById(R.id.spn_home);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_closed, mCurrencies);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mForeignSpinner.setAdapter(arrayAdapter);
        mHomeSpinner.setAdapter(arrayAdapter);

        mForeignSpinner.setOnItemSelectedListener(this);
        mHomeSpinner.setOnItemSelectedListener(this);

        if (savedInstanceState == null
         && (PrefsMgr.getString(this, FOR) == null)
         && (PrefsMgr.getString(this, HOM) == null)) {
            mForeignSpinner.setSelection(findPositonGivenCode("USD", mCurrencies));
            mHomeSpinner.setSelection(findPositonGivenCode("CNY", mCurrencies));

            PrefsMgr.setString(this, FOR, "USD");
            PrefsMgr.setString(this, HOM, "HOM");
        } else {
            mForeignSpinner.setSelection(findPositonGivenCode(PrefsMgr.getString(this, FOR), mCurrencies));
            mHomeSpinner.setSelection(findPositonGivenCode(PrefsMgr.getString(this, HOM), mCurrencies));
        }

        mCalcButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                double amount = Double.parseDouble(mAmountEditText.getText().toString());
                double rslt = amount * 6.45;
                mConvertedTextView.setText(Double.toString(rslt));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private int findPositonGivenCode(String code, String[] currencies) {
        for (int i = 0; i < currencies.length; i++) {
            if (extractCodeFromCurrency(currencies[i]).equalsIgnoreCase(code)) {
                return i;
            }
        }

        return 0;
    }

    private String extractCodeFromCurrency(String currency) {
        return currency.substring(0, 3);
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        }

        return false;
    }

    private void launchBrowser(String strUri) {
        if (isOnline()) {
            Uri uri = Uri.parse(strUri);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    private void invertCurrencies() {
        int nFor = mForeignSpinner.getSelectedItemPosition();
        int nHom = mHomeSpinner.getSelectedItemPosition();

        mForeignSpinner.setSelection(nFor);
        mHomeSpinner.setSelection(nHom);

        mConvertedTextView.setText("");

        PrefsMgr.setString(this, FOR, extractCodeFromCurrency((String)mForeignSpinner.getSelectedItem()));
        PrefsMgr.setString(this, HOM, extractCodeFromCurrency((String)mHomeSpinner.getSelectedItem()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_codes :
                launchBrowser(SplashActivity.URL_CODES);
                break;

            case R.id.menu_invert :
                invertCurrencies();
                break;

            case R.id.menu_exit :
                finish();
                break;
        }

        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spn_foreign :
                PrefsMgr.setString(this, FOR, extractCodeFromCurrency((String)mForeignSpinner.getSelectedItem()));
                break;

            case R.id.spn_home :
                PrefsMgr.setString(this, HOM, extractCodeFromCurrency((String)mHomeSpinner.getSelectedItem()));
                break;

            default :
                break;

        }

        mConvertedTextView.setText("");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

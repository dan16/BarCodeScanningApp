package uk.co.danielnunn.mmapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView itemList;
    private ArrayList<Item> items;
    private MainActivity mainActivity = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        items = new ArrayList<>();
    }

    //Performs validation on the user inputted string and if passed executes async task
    public void buttonManualClick(View view) {
        EditText barEditText = (EditText) findViewById(R.id.barcodeEditText);
        String barcode = barEditText.getText().toString();
        //Length validation based on http://www.keyence.com/ss/products/auto_id/barcode_lecture/basic/jan/
        if (barcode.length() > 14 || barcode.length() < 4) {
            Toast.makeText(this, "Please enter a valid barcode length", Toast.LENGTH_SHORT).show();
        } else if (!barcode.matches("^[0-9]*$")) {//Ensures that only numbers are entered
            Toast.makeText(this, "Please ensure that only numbers are entered", Toast.LENGTH_SHORT).show();
        } else {
            new AsyncQueryAPI(URLEncoder.encode(barcode), this).execute();
        }
    }

    //Checks if the camera permission has been granted, and if so starts the barcode scanner
    public void buttonBarCodeScanner(View view) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, 255
                );
            }
        } else {
            new IntentIntegrator(this).initiateScan();
        }

    }
    //Opens eBay and displays the item searched for
    public void openeBay(final String serial) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String url = "https://www.ebay.co.uk/sch/i.html?_from=R40&_trksid=m570.l1313&_nkw=" + serial;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

    public void itemFound(final Item item) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                items.add(0, item);
                itemList = (ListView) findViewById(R.id.itemListView);
                ItemListAdapter adapterItems = new ItemListAdapter(getApplicationContext(), items, mainActivity);
                itemList.setAdapter(adapterItems);
                adapterItems.notifyDataSetChanged();
            }
        });

    }

    //Called when no item is found, either from incorrect barcode or invalid barcode
    public void itemNotfound() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Context context = getApplicationContext();
                Toast.makeText(context, "No Item could be found, please check that barcode is correctly entered", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Called when barcode activity finishes, either returning a barcode or a cancelled status
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                //Displays message to user when barcode scanner is manually cancelled
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                //Calls async task with the scanned barcode used
                new AsyncQueryAPI(URLEncoder.encode(result.getContents()), this).execute();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

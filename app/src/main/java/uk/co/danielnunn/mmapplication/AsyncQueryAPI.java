package uk.co.danielnunn.mmapplication;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class AsyncQueryAPI extends AsyncTask<Void, Void, Void> {
    MainActivity mainActivity;
    private String searchFor = "";
    Item item;

    public AsyncQueryAPI(String searchText, MainActivity context) {
        //search text is the barcode entered manually or scanned
        searchFor = searchText;
        //Main activity is used for calling methods when completed
        this.mainActivity = context;
        item = new Item();
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            //Url of api
            URL textUrl = new URL("https://api.upcitemdb.com/prod/trial/lookup?upc=" + searchFor);
            BufferedReader bufferReader = new BufferedReader(
                    new InputStreamReader(textUrl.openStream())
            );
            String stringBuffer;
            String stringText = "";
            while ((stringBuffer = bufferReader.readLine()) != null) {
                stringText += stringBuffer;
            }
            bufferReader.close();
            //Json parsing
            JSONObject mainObject = new JSONObject(stringText);
            if (mainObject.getString("code").equalsIgnoreCase("ok")) {
                if (mainObject.getJSONArray("items").getJSONObject(0).has("title")) {
                    item.setTitle(mainObject.getJSONArray("items").getJSONObject(0).getString("title"));
                } else {
                    item.setTitle("No Title Available");
                }
                if (mainObject.getJSONArray("items").getJSONObject(0).has("ean")) {
                    item.setSerial(mainObject.getJSONArray("items").getJSONObject(0).getString("ean"));
                } else {
                    item.setSerial(searchFor);
                }
                if (mainObject.getJSONArray("items").getJSONObject(0).has("images") && mainObject.getJSONArray("items").getJSONObject(0).getJSONArray("images").length() > 0) {
                    item.setImageURL(mainObject.getJSONArray("items").getJSONObject(0).getJSONArray("images").getString(0));
                } else {
                    item.setImageURL("");
                }
                if (mainObject.getJSONArray("items").getJSONObject(0).has("brand")) {
                    item.setBrand(mainObject.getJSONArray("items").getJSONObject(0).getString("brand"));
                } else {
                    item.setBrand("No Brand Found");
                }
                mainActivity.itemFound(item);
            } else {
                mainActivity.itemNotfound();
            }
        } catch (Exception e) {
            e.printStackTrace();
            mainActivity.itemNotfound();
        }

        return null;
    }


}
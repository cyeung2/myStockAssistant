package com.example.mystockassistant;

import android.net.Uri;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class LoadStock implements Runnable{

    private final MainActivity mainActivity;
    private static final String DATA_URL = "https://api.iextrading.com/1.0/ref-data/symbols";
    private static final String DATA_URL2 = "https://cloud.iexapis.com/stable/stock/";
    private static final String DATA_URL3 = "/quote?token=pk_a5eb44ddb59940ebbb40c8e6d6d1bebf";
    private static ArrayList<Stock> stockList1 = new ArrayList<>();

    LoadStock(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        Uri dataUri = Uri.parse(DATA_URL);
        String urlToUse = dataUri.toString();

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL (urlToUse);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                handleResults(null);
                return;
            }

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        handleResults(sb.toString());
    }

    private void handleResults(String s) {

        if (s == null) {
            mainActivity.runOnUiThread(mainActivity::downloadFailed);
            return;
        }
        stockList1.clear();
        final ArrayList<Stock> stockList = parseJSON(s);
        for(int i=0;i<stockList.size();i++) {
            stockList1.add(stockList.get(i));
        }
        mainActivity.runOnUiThread(() -> {
            if (stockList != null) {
                mainActivity.updateData(stockList);
                Toast.makeText(mainActivity, "Loaded " + stockList.size() + " stocks.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private ArrayList<Stock> parseJSON(String s) {
        ArrayList<Stock> stockList = new ArrayList<>();
        try {
            JSONArray jObjMain = new JSONArray(s);

            for(int i=0; i<jObjMain.length();i++) {
                JSONObject jStock = (JSONObject) jObjMain.get(i);
                String symbol = jStock.getString("symbol");
                String name = jStock.getString("name");
                if (symbol.equals("BTCUDT")) {
                    Toast.makeText(mainActivity, "" + i, Toast.LENGTH_LONG).show();
                }
                stockList.add(new Stock(symbol,name));
            }
            return stockList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stockList;
    }
}

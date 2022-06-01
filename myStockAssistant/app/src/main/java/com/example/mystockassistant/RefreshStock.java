package com.example.mystockassistant;

import android.net.Uri;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class RefreshStock implements Runnable{

    private final MainActivity mainActivity;
    private static final String DATA_URL2 = "https://cloud.iexapis.com/stable/stock/";
    private static final String DATA_URL3 = "/quote?token=pk_a5eb44ddb59940ebbb40c8e6d6d1bebf";

    public RefreshStock(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        mainActivity.sortStocks();
        ArrayList<Stock> stockList1 = new ArrayList<>();
        stockList1.addAll(mainActivity.getStonks());
        for(int i=0;i<stockList1.size();i++) {
            String urlNew = DATA_URL2 + stockList1.get(i).getStockSymbol() + DATA_URL3;
            Uri dataUri1 = Uri.parse(urlNew);
            StringBuilder sbb = new StringBuilder();

            try {
                URL url = new URL (urlNew);
                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.connect();

                if (con.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                    handlStonk(null, -1);
                    return;
                }

                InputStream is = con.getInputStream();
                BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

                String line;
                while((line = reader.readLine()) != null) {
                    sbb.append(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            handlStonk(sbb.toString(), i);
        }
        mainActivity.runOnUiThread(() -> {
            mainActivity.donzo();
        });
    }

    private void handlStonk(String s, int pos) {
        if (s == null) {
            mainActivity.runOnUiThread(mainActivity::downloadFailed);
            return;
        }
        final Stock stocker = parseJSON2(s);
        mainActivity.runOnUiThread(() -> {
            if (stocker != null) {
                mainActivity.refreshStock(stocker);
            }
        });
    }

    private Stock parseJSON2(String s) {
        Stock stoc = new Stock();
        String value = "Positive";
        try {
            JSONObject jStock = new JSONObject(s);
            String symbol = jStock.getString("symbol");
            String change = jStock.getString("change");
            String name = jStock.getString("companyName");
            Double cp = 0.0;
            Double roundOff = 0.0;
            Double roundOff1 = 0.0;
            if (change.contains("-")) {
                value = "Negative";
                change = change.substring(1);
                cp = (Double) (Double.parseDouble(change));
                roundOff1 = Math.round(cp * 100.0) / 100.0;
            }
            else if (!change.contains("-")) {
                cp  = (Double) (Double.parseDouble(change));
                roundOff1 = Math.round(cp * 100.0) / 100.0;
            }
            String changeP = jStock.getString("changePercent");
            if (changeP.contains("-")) {
                value = "Negative";
                changeP = changeP.substring(1);
                cp = (Double) (Double.parseDouble(changeP) * 100);
                roundOff = Math.round(cp * 100.0) / 100.0;
            }
            else if (!changeP.contains("-")) {
                cp  = (Double) (Double.parseDouble(changeP) * 100);
                roundOff = Math.round(cp * 100.0) / 100.0;
            }
            String price = jStock.getString("latestPrice");
            Stock sto = new Stock(symbol, name, Double.parseDouble(price), roundOff1, roundOff, value);
            return sto;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stoc;
    }
}

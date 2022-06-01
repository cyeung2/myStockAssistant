package com.example.mystockassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private final ArrayList<Stock> stonks = new ArrayList<>();
    private final ArrayList<Stock> faceStock = new ArrayList<>();
    private RecyclerView recyleView;
    private ListStock change;
    private SwipeRefreshLayout swipi;
    private String addSymbol = "";
    private DataHandlr databaseHandler;
    private final String MARKET_URL = "http://www.marketwatch.com/investing/stock/";
    private String place = "";
    private Stock sCode;
    private ArrayList<String> waiter = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swipi = findViewById(R.id.new_swiper);
        recyleView = findViewById(R.id.recycler);
        change = new ListStock(this, faceStock);
        recyleView.setAdapter(change);
        recyleView.setLayoutManager(new LinearLayoutManager(this));
        setTitle("Stock Watch");
        databaseHandler = new DataHandlr(this);
        LoadStock stockLoaderRunnable = new LoadStock(this);
        new Thread(stockLoaderRunnable).start();
        RefreshStock stockRefreshRunnable = new RefreshStock(this);
        if (checkr()) {
            swipi.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (checkr()) {
                        new Thread(stockRefreshRunnable).start();
                    }
                    else
                        donzo();
                }
            });
        }

        ArrayList<Stock> list = databaseHandler.loadStocks();
        faceStock.clear();
        faceStock.addAll(list);
        sortStocks();
        change.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        setTitle("Stock Watch");
        if (databaseHandler != null) {
            ArrayList<Stock> list = databaseHandler.loadStocks();
            this.faceStock.clear();
            this.faceStock.addAll(list);
            sortStocks();
            change.notifyDataSetChanged();
        }
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menu) {
        if (menu.getItemId() == R.id.menuAdd) {
            if (checkr()) {
                final EditText tmp = new EditText(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                tmp.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(tmp);
                builder.setTitle("Please enter a Stock Symbol:");
                builder.setPositiveButton("OK", (dialog, id) -> {
                    String temp = tmp.getText().toString().toUpperCase();
                    setAddSymbol(temp);
                    String m = getAddSymbol();
                    Boolean tes = false;
                    int x = 0;
                    while (x < faceStock.size()) {
                        if (temp.equals(faceStock.get(x).getStockSymbol())) {
                            dupplicatee();
                            tes = true;
                        }
                        else {
                            if (x >= faceStock.size()) {
                            }
                        }
                        x++;
                    }
                    if(tes != true) {
                        findr(listCreate(temp));
                    }
                });
                builder.setNegativeButton("CANCEL", (dialog, id) -> {
                });
                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            }
            return super.onOptionsItemSelected(menu);
        }
        else
            return super.onOptionsItemSelected(menu);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public String getAddSymbol() {
        return addSymbol;
    }

    public void setAddSymbol(String addSymbol) {
        this.addSymbol = addSymbol;
    }

    @Override
    public void onClick(View v) {
        int place = recyleView.getChildLayoutPosition(v);
        sCode = faceStock.get(place);
        String symb = sCode.getStockSymbol();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(MARKET_URL + symb));

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void setWaiter(ArrayList<String> list) {
        this.waiter.addAll(list);
    }

    public void clearHold() {
        this.waiter.clear();
    }

    public ArrayList<String> getWaiter() {
        return this.waiter;
    }

    public void sortStocks() {
        ArrayList<Stock> tx = new ArrayList<>();
        ArrayList<String> txxtx = new ArrayList<>();
        for(int i = 0; i< faceStock.size(); i++) {
            txxtx.add(faceStock.get(i).getStockSymbol());
        }
        Collections.sort(txxtx);

        for(int i=0;i<txxtx.size();i++) {
            for(int j = 0; j< faceStock.size(); j++) {
                if (txxtx.get(i).equals(faceStock.get(j).getStockSymbol())) {
                    tx.add(faceStock.get(j));
                }
            }
        }
        this.faceStock.clear();
        this.faceStock.addAll(tx);
    }

    @Override
    public boolean onLongClick(View v) {
        final int pos = recyleView.getChildLayoutPosition(v);

        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setPositiveButton("DELETE", (dialog, id) -> {
            databaseHandler.deleteStock(faceStock.get(pos).getStockSymbol());
            faceStock.remove(pos);
            change.notifyDataSetChanged();
        });
        build.setNegativeButton("CANCEL", (dialog, id) -> {
        });
        build.setIcon(R.drawable.trash);
        build.setMessage("Delete Stock Symbol " + faceStock.get(pos).getStockSymbol() + "?");
        build.setTitle("Delete Stock");

        AlertDialog dialog = build.create();
        dialog.show();
        return true;
    }

    public void updateData(ArrayList<Stock> sList) {
        stonks.addAll(sList);
        change.notifyDataSetChanged();
    }

    public void updateStock(Stock x, int place) {
        for (int i = 0; i< faceStock.size(); i++) {
            if (x.getStockSymbol().equals(faceStock.get(i).getStockSymbol()) && place != -1) {
                faceStock.get(i).setChangePercent(x.getChangePercent());
                faceStock.get(i).setpChange(x.getPChange());
                faceStock.get(i).setPrice(x.getPrice());
                faceStock.get(i).setValue(x.getValue());
                databaseHandler.addStock(faceStock.get(i));
            }
        }
        change.notifyDataSetChanged();
    }

    public void downloadFailed() {
        change.notifyDataSetChanged();
    }

    public void donzo() {
        swipi.setRefreshing(false);
    }

    public ArrayList<Stock> getStonks() {
        return this.faceStock;
    }

    @Override
    protected void onDestroy() {
        databaseHandler.shutDown();
        super.onDestroy();
    }

    public void refreshStock(Stock stock) {
        databaseHandler.updateStock(stock);
    }

    public void startAdd() {
        AddStock stockAddRunnable = new AddStock(this);
        new Thread(stockAddRunnable).start();
    }

    public void notFound(String str) {
        AlertDialog.Builder builders = new AlertDialog.Builder(this);
        builders.setTitle("Symbol not Found: " + str);
        builders.setMessage("Data for stock symbol");
        AlertDialog dialogs = builders.create();
        dialogs.show();
    }

    public CharSequence[] listCreate(String str) {
        final ArrayList<String> end = new ArrayList<>();
        int p = 0;
        int count = 0;
        int sze = str.length();
        String sub = "";
        this.place = "None";
        for (int i = 0; i< stonks.size(); i++) {
            if (stonks.get(i).getStockSymbol().length() > sze) {
                sub = stonks.get(i).getStockSymbol().substring(0,sze);
            }
            else
                sub = stonks.get(i).getStockSymbol();
            if (sub.contains(str)) {
                end.add(stonks.get(i).getStockSymbol());
            }
        }
        final CharSequence[] cList = new CharSequence[end.size()];
        while (p < end.size()){
            if (end.get(p).equals(stonks.get(count).getStockSymbol())) {
                cList[p] = end.get(p) + " - " + stonks.get(count).getCName();
                p++;
            }
            count++;
        }
        clearHold();
        setWaiter(end);
        return cList;
    }

    public void dupplicatee() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.error);
        builder.setTitle("Duplicate Stock");
        builder.setMessage("Stock Symbol " + getAddSymbol() + " is already" + '\n' + "Displayed");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialogs = builder.create();
        dialogs.show();
    }

    private boolean checkr() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo aN = cm.getActiveNetworkInfo();
        boolean isConnected = aN != null && aN.isConnectedOrConnecting();
        boolean isMetered = cm.isActiveNetworkMetered();

        if (isConnected) {
            return true;
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Stocks Cannot Be Added Without A" + '\n' + "Network Connection");
            AlertDialog dialogs = builder.create();
            dialogs.show();
            return false;
        }
    }

    public void findr(CharSequence[] seq) {
        AlertDialog.Builder builders = new AlertDialog.Builder(this);
        builders.setTitle("Stock Selection");
        if (seq.length != 0) {
            builders.setItems(seq, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ArrayList<String> ends = getWaiter();
                    int count = 0;
                    Boolean boo = false;
                    Boolean test = true;
                    while (count < stonks.size()) {
                        if (ends.get(which).equals(stonks.get(count).getStockSymbol())) {
                            faceStock.add(stonks.get(count));
                            sortStocks();
                            setAddSymbol(ends.get(which));
                            String m = getAddSymbol();
                            startAdd();
                            count = stonks.size() + 1;
                            boo = true;
                        }
                        count++;
                    }
                    if (boo == false) {
                        notFound(ends.get(which));
                    }
                }
            });

            builders.setNegativeButton("NeverMind", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog dialogs = builders.create();
            dialogs.show();
        }
        else {
            ArrayList<String> ends = getWaiter();
            String str = getAddSymbol();
            notFound(str);
        }
    }
}
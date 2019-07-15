/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quotetracker.resources.classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.json.JSONObject;
import quotetracker.MainView.MainViewFXMLController;

/**
 *
 * @author redayoub
 */
public class GetInfoFromServer extends Thread{
    private static GetInfoFromServer th;
    
    int refreshInterval;
    private static final int MS_IN_MIN=60000; // milisecends in 1 minite
//    private HashSet<String> symbols;
//    private ArrayList<SymbolStockInfo> symbolsObjects;
    private HashMap<String,SymbolStockInfo> mySymbols;
    private boolean running;
    private final String serverBaseUrl="https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&datatype=csv&outputsize=compact&apikey=ZS1JT0GWINFUP1K1";
    private String serverBaseUrl2="https://api.iextrading.com/1.0/stock/market/batch?types=quote";

    private GetInfoFromServer(int refreshInterval) {
        this.refreshInterval = refreshInterval;
        //this.symbols=new HashSet<String>();
        //this.symbolsObjects=new ArrayList<>();
        this.mySymbols=new HashMap<>();
        running=false;
    }
    // add a refreche method whan i refrech interval
    public void refreshThread(){
        this.interrupt();
    }
    public  static GetInfoFromServer getInstence(int refInt){
        if(th==null){
            th=new GetInfoFromServer(refInt);
        }
        return th;
    }
    
    @Override
    public void run() {
        while(running){
            
            if (isConnected()) {
                showProgressIndicator(true);
                SymbolStockInfo[] res=getDataFromServer2(mySymbols.keySet().toArray(new String[0]));
                
                for(SymbolStockInfo ssi:res){
                    String symbol = ssi.getSymbolName();
                    if(mySymbols.get(symbol) == null){
                        mySymbols.put(symbol, ssi);
                    }else{
                        ssi.setPervInfos(mySymbols.get(symbol));
                        mySymbols.put(symbol, ssi);
                    }
                    Platform.runLater(() -> {
                        MainViewFXMLController.changeRow(ssi);
                        //MainViewFXMLController.staticLasrRefLab.setText(ssi.getLastRef().toString());
                    });
                }
                // set last refreched
                Platform.runLater(() -> {    
                        MainViewFXMLController.staticLasrRefLab.setText(res[0].getLastRef().toString());
                    });
                
                Date cuurentTime = new Date();
                if ((cuurentTime.before(res[0].getOpenTime())) && (cuurentTime.after(res[0].getCloseTime()))) {

                    Platform.runLater(() -> {
                        MainViewFXMLController.staticIsMarketClosed.setText("The Market are closed !");
                    });
                }else{
                   Platform.runLater(() -> {
                        MainViewFXMLController.staticIsMarketClosed.setText(null);
                    }); 
                }
                
                showProgressIndicator(false);
            }else{
                showInternetConnectionFailedAlert();
            }            
            try {
                Thread.sleep(MS_IN_MIN*refreshInterval);
            } catch (InterruptedException ex) {
                // to re run the thread
                continue;
                //Logger.getLogger(GetInfoFromServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void showProgressIndicator(boolean visible) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                MainViewFXMLController.staticProgIndicator.setVisible(visible);
            }
        });
    }
    
    public synchronized void stopThread(){
        running=false;
        this.stop();
    }
    
    public synchronized void addSymbol(String symbol){
        //symbols.add(symbol);
        if(mySymbols.containsKey(symbol)){
           return;
        }else{
            mySymbols.put(symbol.toUpperCase(), null);
        }
        
        if(!running){
            running=true;
            this.start();
        }
        if(this.getState().equals(State.TIMED_WAITING)){
            showProgressIndicator(true);
            SymbolStockInfo[] ssi=getDataFromServer2(symbol);
            mySymbols.putIfAbsent(symbol, ssi[0]);
            Platform.runLater(() -> {
                    MainViewFXMLController.changeRow(ssi[0]);
            });
            showProgressIndicator(false);
        }
    }

    public void setRefreshInterval(int refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    
    
    private SymbolStockInfo[] getDataFromServer2(String... symbols) {
        SymbolStockInfo[]res=new SymbolStockInfo[symbols.length];
        StringBuilder serUrlBuilder=new StringBuilder(serverBaseUrl2);
        serUrlBuilder.append("&symbols=");
        for(String symbol:symbols){
            serUrlBuilder.append(symbol).append(",");
        }
        // remove the last comma
        serUrlBuilder.deleteCharAt(serUrlBuilder.length()-1);
        try {
            System.out.println(serUrlBuilder.toString());
            URL serverURL=new URL(serUrlBuilder.toString());
            
            
            StringBuilder jsonText=new StringBuilder();
            BufferedReader br=new BufferedReader(new InputStreamReader(serverURL.openStream()));
            String line=null;
            while((line=br.readLine())!=null){
                jsonText.append(line).append("\n");
            }
            br.close();
            
            JSONObject root=new JSONObject(jsonText.toString());
            for (int i = 0; i < symbols.length; i++) {
                JSONObject jsonSymbol=root.getJSONObject(symbols[i].toUpperCase()).getJSONObject("quote");
                res[i]=getInfoFromJSON(jsonSymbol);
                //res[i].setPervInfos(mySymbols.getOrDefault(symbols[i], null));
            }
            System.out.println("Working S2");
            return res;

        } catch (MalformedURLException ex) {
            Logger.getLogger(GetInfoFromServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GetInfoFromServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private SymbolStockInfo getDataFromServer(String symbol) {
        
        StringBuilder serUrlBuilder=new StringBuilder(serverBaseUrl);
        serUrlBuilder.append("&interval=").append(refreshInterval).append("min");
        serUrlBuilder.append("&symbol=").append(symbol);
        try {
            URL serverURL=new URL(serUrlBuilder.toString());
            
            Scanner scan=new Scanner(serverURL.openStream());
            StringBuilder csvText=new StringBuilder();
            int numReqLines=3; // header+ 2 lines
            int count=0;
            while(scan.hasNext()&&count<numReqLines){
                csvText.append(scan.nextLine()).append("\n");
                count++;
            }
            scan.close();
            
            // get each line by himself
            String[] lines=csvText.toString().split("\n");
            //use just the sec and the third line
            SymbolStockInfo latestData=getDataFromLine(lines[1]);
            SymbolStockInfo oldData=getDataFromLine(lines[2]);
           latestData.setPervInfos(oldData);
           latestData.setSymbolName(symbol);
           return latestData;

        } catch (MalformedURLException ex) {
            Logger.getLogger(GetInfoFromServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GetInfoFromServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
/**
 * 
 * @param InfoSeries
 * @return SymbolStockInfo
 * 
 */
   

    private SymbolStockInfo getDataFromLine(String line) {
        SymbolStockInfo data=new SymbolStockInfo();
        /*
            timestamp,open,high,low,close,volume
            2018-07-13 16:00:00,105.5200,105.5800,105.3900,105.4300,3401761
        */
        String[] dataIn=line.split(",");
        data.setLastRef(dataIn[0]);
        data.setOpen(Double.parseDouble(dataIn[1]));
        data.setHigh(Double.parseDouble(dataIn[2]));
        data.setLow(Double.parseDouble(dataIn[3]));
        data.setClose(Double.parseDouble(dataIn[4]));
        return data;
    }

    public void delSymbol(String symbol) {
//        symbols.remove(symbol);
//        symbolsObjects.remove(new SymbolStockInfo(symbol));
        mySymbols.remove(symbol);
    }
    
    private static boolean isConnected(){
        boolean connected=false;
        try{
            int timeOutInMilliSec=5000;// 5 Seconds
            URL url = new URL("https://api.iextrading.com");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.setConnectTimeout(timeOutInMilliSec);
            conn.setReadTimeout(timeOutInMilliSec);
            int responseCode = conn.getResponseCode();
            if(200 <= responseCode && responseCode <= 399){
                connected=true;
                return true;
            }
            
        }
        catch(Exception ex){
            connected=false;
            return false;
        }
        return connected;
    }

    private void showInternetConnectionFailedAlert() {
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert al=new Alert(Alert.AlertType.ERROR, "There's no connection", ButtonType.CLOSE);
            al.setTitle("No Internet Connection");
                al.show();
            }
        });
    }

    private SymbolStockInfo getInfoFromJSON(JSONObject jsonSymbol) {
        SymbolStockInfo ssi=new SymbolStockInfo();
        ssi.setSymbolName(jsonSymbol.getString("symbol"));
        ssi.setOpen(jsonSymbol.getDouble("open"));
        ssi.setClose(jsonSymbol.getDouble("close"));
        ssi.setCloseTime(new Date(jsonSymbol.getLong("closeTime")));
        ssi.setHigh(jsonSymbol.getDouble("high"));
        ssi.setLow(jsonSymbol.getDouble("low"));
        ssi.setOpen(jsonSymbol.getDouble("open"));
        ssi.setOpenTime(new Date(jsonSymbol.getLong("openTime")));
        ssi.setLastRef(new Date(jsonSymbol.getLong("latestUpdate")));
        
        return ssi;
    }
    
    
    
}

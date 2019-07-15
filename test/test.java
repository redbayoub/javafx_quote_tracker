
import com.sun.deploy.model.LocalApplicationProperties;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import quotetracker.resources.classes.GetInfoFromServer;
import quotetracker.resources.classes.SymbolStockInfo;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author redayoub
 */
public class test {

    public static void main(String[] args) throws ParseException {
//        Locale[] locales=DateFormat.getAvailableLocales().clone();
//        Arrays.sort(locales,new Comparator<Locale>() {
//            @Override
//            public int compare(Locale o1, Locale o2) {
//                return o1.getDisplayName().compareTo(o2.getDisplayName());
//            }
//        });
//        for(Locale l:locales){
//            System.out.println(l.getDisplayName());
//        }

//        String in="2018-07-12 16:00:00";
//        
//        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        sdf.setTimeZone(TimeZone.getTimeZone("EST")); // estern time zone
//        
//        Date d=sdf.parse(in);
//        System.out.println(d.toString());
//        GetInfoFromServer infoFromServer=new GetInfoFromServer(5);
//        infoFromServer.addSymbol("msft");
//        infoFromServer.start();
//        int i=Integer.parseInt("15");
//        
//        System.out.println(i);
//        try {
//            System.out.println("running");
//            int timeOutInMilliSec = 5000;// 5 Seconds
//            URL url = new URL("http://www.google.com/");
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("HEAD");
//            conn.setConnectTimeout(timeOutInMilliSec);
//            conn.setReadTimeout(timeOutInMilliSec);
//            int responseCode = conn.getResponseCode();
//            if (200 <= responseCode && responseCode <= 399) {
//                System.out.println("Internet is Available");
//            }
//        } catch (Exception ex) {
//            System.out.println("No Connectivity");
//        }
        
        Date open=new Date(Long.parseLong("1531834200012"));
        Date close=new Date(Long.parseLong("1531771200471"));
        System.out.println(open);
        System.out.println(close);
//        HashMap<String,SymbolStockInfo> mySymbols=new HashMap<>();
//        mySymbols.put("hello", null);
//        mySymbols.put("hello2", null);
//        String[] res=mySymbols.keySet().toArray(new String[0]);
//        System.out.println(res[0]);
    }
    
}

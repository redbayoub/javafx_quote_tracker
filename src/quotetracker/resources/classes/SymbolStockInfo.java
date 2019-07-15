
package quotetracker.resources.classes;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author redayoub
 */
public class SymbolStockInfo {

    
    private String SymbolName;
    private double open;
    private double high;
    private double low;
    private double close;
    private Date lastRef;
    private Date openTime;
    private Date closeTime;

    public Date getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Date openTime) {
        this.openTime = openTime;
    }

    public Date getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Date closeTime) {
        this.closeTime = closeTime;
    }
    private SymbolStockInfo pervInfos;
    
    
    public SymbolStockInfo() {
    }

    public SymbolStockInfo(String SymbolName) {
        this.SymbolName = SymbolName;
    }
    
    public SymbolStockInfo(String SymbolName, double open, double high, double low, double close, SymbolStockInfo pervInfos) {
        this.SymbolName = SymbolName;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.pervInfos = pervInfos;
    }

    public void setLastRef(Date lastRef) {
        this.lastRef = lastRef;
    }

    public SymbolStockInfo(String SymbolName, SymbolStockInfo pervInfos) {
        this.SymbolName = SymbolName;
        this.pervInfos = pervInfos;
    }

    public String getSymbolName() {
        return SymbolName;
    }

    public void setSymbolName(String SymbolName) {
        this.SymbolName = SymbolName;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public SymbolStockInfo getPervInfos() {
        return pervInfos;
    }

    public void setPervInfos(SymbolStockInfo pervInfos) {
        this.pervInfos = pervInfos;
    }

    public void setLastRef(String lastRefInEst) {
        
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("EST")); // estern time zone
        try {
            Date d=sdf.parse(lastRefInEst);
            this.lastRef=d;
        } catch (ParseException ex) {
            Logger.getLogger(SymbolStockInfo.class.getName()).log(Level.SEVERE, null, ex);
        }   
    }

    public Date getLastRef() {
        return lastRef;
    }

    @Override
    public String toString() {
        return "SymbolStockInfo{" + "SymbolName=" + SymbolName + "\n, open=" + open + "\n, high=" + high + "\n, low=" + low + "\n, close=" + close + "\n, lastRef=" + lastRef.toString() + ", pervInfos= " + pervInfos + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof SymbolStockInfo){
            SymbolStockInfo ssi=(SymbolStockInfo) obj;
            if(ssi.getSymbolName().equals(this.getSymbolName())){
                return true;
            }else{
                return false;
            }
            
        }
        return false;
    }
   
    
    
    
    public double calculateDiff(){
        double diff=getClose()-pervInfos.getClose();
        diff*=pervInfos.getClose();
        diff/=100;
        DecimalFormat df=new DecimalFormat("###.###");
        
        return Double.parseDouble(df.format(diff));
    }
}

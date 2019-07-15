/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quotetracker.MainView;

import com.jfoenix.controls.JFXComboBox;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import quotetracker.resources.classes.GetInfoFromServer;
import quotetracker.resources.classes.SymbolStockInfo;

/**
 *
 * @author redayoub
 */
public class MainViewFXMLController implements Initializable {

    private static final int COL_COUNT=7;
    
    @FXML
    private StackPane spreedsheetPane;
    @FXML
    private JFXComboBox<String> refreshInter;
    @FXML
    private Label lasrRefLab;
    @FXML
    private Label isMarketClosed;
    @FXML
    private ProgressIndicator progressIndicator;
    
    private static SpreadsheetView spreadsheetView;
    public static GridBase staticGB;
//    private JFXListView<HBox> symbolsList;
//    private static JFXListView<HBox> staticSymbolsList;
    GetInfoFromServer getInfoFromServer;
    
    public static ProgressIndicator staticProgIndicator;
    public static Label staticLasrRefLab;
    public static Label staticIsMarketClosed;
    
    
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        initSpreedsheet();
//        symbolsList.setExpanded(true);
//        symbolsList.depthProperty().setValue(1);
//        addListFooter();
//        addListHeader();
        initRefreshInter();
        int refInterval;
        try{
            refInterval=Integer.parseInt(refreshInter.getValue().substring(0, 2));
        }catch(NumberFormatException ex){
            refInterval=Integer.parseInt(""+refreshInter.getValue().charAt(0));
        }
        getInfoFromServer=GetInfoFromServer.getInstence(refInterval);
        lasrRefLab.setText(null);
        isMarketClosed.setText(null);
        staticIsMarketClosed=isMarketClosed;
        staticLasrRefLab=lasrRefLab;
        progressIndicator.setVisible(false);
        staticProgIndicator=progressIndicator;
//        staticSymbolsList=symbolsList;
    }    

    @FXML
    private void changeRefInter(ActionEvent event) {
        int refInterval;
        try{
            refInterval=Integer.parseInt(refreshInter.getValue().substring(0, 2));
        }catch(NumberFormatException ex){
            refInterval=Integer.parseInt(""+refreshInter.getValue().charAt(0));
        }
        getInfoFromServer.setRefreshInterval(refInterval);
        // add a refreche method whan i refrech interval
        getInfoFromServer.refreshThread();
    }
    


    private void initRefreshInter() {
        String[] intervales={"1 Min", "5 Min", "15 Min", "30 Min", "60 Min"};
        refreshInter.getItems().addAll(intervales);
        refreshInter.setValue(intervales[0]);
    }

    private void initSpreedsheet() {
        int rowCount=5;
        GridBase gb=new GridBase(rowCount, COL_COUNT);
        gb.setRowHeightCallback(new GridBase.MapBasedRowHeightFactory(generateRowHight()));
        
        gb.getRows().add(0, generateHeaderRow());
        staticGB=gb;
        spreadsheetView=new SpreadsheetView(gb);
        spreadsheetView.getFixedRows().add(0);
        setPrefWitdhForRow(0);
        spreadsheetView.setShowRowHeader(false);
        spreadsheetView.setShowColumnHeader(false);
        spreadsheetView.setEditable(false);
        
        spreedsheetPane.getChildren().setAll(spreadsheetView);
        spreedsheetPane.setMaxWidth(spreadsheetView.getMinWidth());
    }

    private Map<Integer, Double> generateRowHight() {
        Map<Integer, Double> rowHeight = new HashMap<>();
        rowHeight.put(1, 50.0);
        return rowHeight;
    }

    private static SpreadsheetCell generateDoubleCell(int row, int column, int rowSpan, int colSpan,double info) {
        SpreadsheetCell cell;
        cell = SpreadsheetCellType.DOUBLE.createCell(row, column, rowSpan, colSpan,info);
        cell.setFormat("###.###");
        return cell;
    }
    
    private static SpreadsheetCell generateStringCell(int row, int column, int rowSpan, int colSpan,String info) {
        SpreadsheetCell cell;
        cell = SpreadsheetCellType.STRING.createCell(row, column, rowSpan, colSpan,info);
        return cell;
    }
    private static SpreadsheetCell generateImageCell(int row, int column, int rowSpan, int colSpan,Image info) {
        SpreadsheetCell cell;
        cell = SpreadsheetCellType.STRING.createCell(row, column, rowSpan, colSpan,null);
        cell.setGraphic(new ImageView(info));
        
        return cell;
    }
    
    private ObservableList<SpreadsheetCell> generateHeaderRow() {
        ObservableList<SpreadsheetCell> row=FXCollections.observableArrayList();
        row.add(generateStringCell(0, 0, 1, 1, "Symbol Name"));
        row.add(generateStringCell(0, 1, 1, 1, "Open"));
        row.add(generateStringCell(0, 2, 1, 1, "High"));
        row.add(generateStringCell(0, 3, 1, 1, "Low"));
        row.add(generateStringCell(0, 4, 1, 1, "Close"));
        row.add(generateStringCell(0, 5, 1, 1, "Change %"));
        row.add(generateStringCell(0, 6, 1, 1, "Image"));
        
        return row;
    }
    
    private static ObservableList<SpreadsheetCell> generateRow(SymbolStockInfo ssi,int rowIndex) {
        
        ObservableList<SpreadsheetCell> row=FXCollections.observableArrayList();
        row.add(generateStringCell(rowIndex, 0, 1, 1, ssi.getSymbolName()));
        row.add(generateDoubleCell(rowIndex, 1, 1, 1, ssi.getOpen()));
        row.add(generateDoubleCell(rowIndex, 2, 1, 1, ssi.getHigh()));
        row.add(generateDoubleCell(rowIndex, 3, 1, 1, ssi.getLow()));
        row.add(generateDoubleCell(rowIndex, 4, 1, 1, ssi.getClose()));
        if(ssi.getPervInfos()==null){
            row.add(generateStringCell(rowIndex, 5, 1, 1, "NPI"));
            row.add(generateStringCell(rowIndex, 6, 1, 1, "NPI"));
        } else {
            double diff = ssi.calculateDiff();
            row.add(generateDoubleCell(rowIndex, 5, 1, 1, diff));
            Image arrowImage = null;
            if (diff > 0) {
                arrowImage = new Image("quotetracker/resources/images/up_arrow_image.png", true);
            } else if (diff == 0) {
                arrowImage = new Image("quotetracker/resources/images/equal_image.png", true);
            } else {
                arrowImage = new Image("quotetracker/resources/images/down_arrow_image.png", true);
            }
            row.add(generateImageCell(rowIndex, 6, 1, 1, arrowImage));
        }
        
        return row;
    }

    public static void changeRow(SymbolStockInfo ssi){
        int rowIndex=staticGB.getRows().size();
        int count=0;
        for(ObservableList<SpreadsheetCell> row:staticGB.getRows()){
            if(row.get(0).getText().equals(ssi.getSymbolName())){
                rowIndex=count;
                break;
            }
            count++;
        }
        if(rowIndex!=staticGB.getRows().size()){//found
            staticGB.getRows().set(rowIndex, generateRow(ssi, rowIndex));
        }else{ // not found
            staticGB.getRows().add(generateRow(ssi, rowIndex));
        }
        
        //setPrefWitdhForRow(rowIndex);
    }
    
    @FXML
    private void addSymbol(ActionEvent event) {
         TextInputDialog dialog=new TextInputDialog();
            dialog.setHeaderText("Add new Symbol");
            dialog.setContentText("Plz, write the new symbol :");
            Optional<String> res=dialog.showAndWait();
            if(res.isPresent()&&(!res.get().equals(""))){
                // hander result
                getInfoFromServer.addSymbol(res.get());
            }
    }

    @FXML
    private void delSymbol(ActionEvent event) {
        TextInputDialog dialog=new TextInputDialog();
            dialog.setHeaderText("Delete Symbol");
            dialog.setContentText("Plz, enter the symbol :");
            Optional<String> res=dialog.showAndWait();
            if(res.isPresent()&&(!res.get().equals(""))){
                // hander result
                // find the symbole 
                boolean found=false;
                int rowIndex=0;
                String symbolName=res.get();
                for(ObservableList<SpreadsheetCell> row:staticGB.getRows()){
                    if(row.get(0).getText().equals(symbolName)){
                        found=true;
                         break;
                    }
                    rowIndex++;
                }
                
                if(found){
                    getInfoFromServer.delSymbol(symbolName);
                    spreadsheetView.getGrid().getRows().remove(rowIndex);
                }
            }
    }


    private static void setPrefWitdhForRow(int rowIndex) {
       
        int add=rowIndex*COL_COUNT;
        for(int col=0;col<COL_COUNT-1;col++){
            spreadsheetView.getColumns().get(add+col).setPrefWidth(62);
        }
    }
}

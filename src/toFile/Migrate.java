/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package toFile;
import com.mysql.jdbc.DatabaseMetaData;
import com.mysql.jdbc.ResultSet;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author MtsSk
 */
public class Migrate {
           
    private static Connection conn;
    private static ResultSet rsTable;
    private static ResultSet rsColumn;
    private static ResultSet rsData;
    private static ArrayList<String> columnas = new ArrayList<>();
    
    public static void getConnection(String url, String DB, String username, String password){
        
        try {
            String driver = "com.mysql.jdbc.Driver";
            Class.forName(driver);
            
            conn = DriverManager.getConnection("jdbc:mysql://"+url+"/"+DB, username, password);
            
        } catch (Exception e) {
            System.out.println("Error with the DB connection.");
            System.out.println(e.getMessage());
        }        
        
    }
    
    public static void getTable(String DB){
        
        try {
            
            DatabaseMetaData meta =  (DatabaseMetaData) conn.getMetaData();
            rsTable = (ResultSet) meta.getTables(DB, null, null, new String [] {});
            
            while(rsTable.next()){
                String DBTable = rsTable.getString("TABLE_NAME");
                System.out.println("DB name: "+DBTable);
                writeFile("Table: "+DBTable, true);
                getColumns(DB, DBTable);
            }
            
        } catch (Exception e) {
        }   
        
    }
    
    public static void getColumns(String DB, String tabla){
        
        try {
            
            DatabaseMetaData meta =  (DatabaseMetaData) conn.getMetaData();
            rsColumn = (ResultSet) meta.getColumns(DB, null, tabla, null);
            
            while(rsColumn.next()){
                //columnas.add(rsColumn.getString("COLUMN_NAME"));
                //System.out.println(" - "+rsColumn.getString("COLUMN_NAME")+", "+rsColumn.getString("TYPE_NAME")+", "+rsColumn.getString("COLUMN_SIZE"));
                String columna = rsColumn.getString("COLUMN_NAME");
                System.out.print(columna+", "+rsColumn.getString("TYPE_NAME")+", "+rsColumn.getString("COLUMN_SIZE")+"; ");
                writeFile("Columns: "+columna+", "+rsColumn.getString("TYPE_NAME")+", "+rsColumn.getString("COLUMN_SIZE")+"; ",false);
                columnas.add(columna);
            }
            System.out.println("");
            writeFile("", true);
            getData(tabla, columnas);
            columnas.clear();
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }  
        
    }
    
    public static void getData(String tabla, ArrayList<String> columnas){
        
        try {
            Statement s = conn.createStatement();
            rsData = (ResultSet) s.executeQuery("SELECT * FROM "+tabla+";");
            
            while(rsData.next()){
                for(int i = 0; i < columnas.size(); i++){
                    if(i == (columnas.size()-1)){
                        System.out.println(rsData.getString(columnas.get(i))+"; ");
                        writeFile(rsData.getString(columnas.get(i))+"; ",true);
                    }
                    else{
                        System.out.print(rsData.getString(columnas.get(i))+", ");
                        writeFile(rsData.getString(columnas.get(i))+", ",false);
                    }
                }
            }
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }       
        
    }
    
    public static void writeFile(String linea, boolean ln){
        
        FileWriter fichero = null;
        PrintWriter pw = null;
        try
        {
            fichero = new FileWriter("archivo.txt",true);
            pw = new PrintWriter(fichero);

            if(ln == true)
                pw.println(linea);
            else
                pw.print(linea);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
           try {
           // Nuevamente aprovechamos el finally para 
           // asegurarnos que se cierra el fichero.
           if (null != fichero)
              fichero.close();
           } catch (Exception e2) {
              e2.printStackTrace();
           }
        }
        
    }
    
}

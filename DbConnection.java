/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sales;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 *
 * @author crugl
 */
public class DbConnection {
    
    private static String connectionString = "jdbc:sqlite:salespos.db";
    private static String classLocation = "org.sqlite.JDBC";
    private static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    
    private static Connection con = null;
    
    
    public DbConnection() throws ClassNotFoundException, SQLException{
        if (con == null){
            Class.forName(DbConnection.classLocation);
            this.con = DriverManager.getConnection(DbConnection.connectionString);
        }
    }
    
    public int getQuantity(String Barcode) throws SQLException{
        PreparedStatement pst = con.prepareStatement("select product.id, coalesce (sum(inventory.quantity),0) as quantity from product left join inventory on product.id==inventory.product_id where product.barcode = ? group by product.id");
        pst.setString(1, Barcode);
        ResultSet rs = pst.executeQuery();
        if (rs.next()== false){
            return -1;
        }
        return rs.getInt("quantity");
    }
    
    public int getId(String Barcode) throws SQLException{
        PreparedStatement pst = con.prepareStatement("select id from product where barcode = ? ");
        pst.setString(1, Barcode);
        ResultSet rs = pst.executeQuery();
        if (rs.next()== false){
            return -1;
        }
        return rs.getInt("id");
    }
    
    public int updateQuantity (String Barcode, int quantity) throws SQLException{
        PreparedStatement pst = con.prepareStatement("insert into inventory (product_id,quantity,date) values (?,?,?)");
        int id = this.getId(Barcode);
        pst.setInt(1, id);
        pst.setInt(2,quantity);
        Date date = new Date();
        pst.setString(3, this.formatter.format(date));
        return pst.executeUpdate();
    }
    
    public Object[] getProduct(String Barcode) throws SQLException{
        int quantity = this.getQuantity(Barcode);
        PreparedStatement pst = con.prepareStatement("select id,name,price from product where barcode = ? ");
        pst.setString(1, Barcode);
        ResultSet rs = pst.executeQuery();
        return new Object[]{
                            Barcode,
                            rs.getString("name"),
                            quantity,
                            rs.getFloat("price"),
        };
    }
  
    
    
}

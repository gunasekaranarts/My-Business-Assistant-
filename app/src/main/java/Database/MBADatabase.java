package Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import POJO.Customers;
import POJO.Item;
import POJO.OrderItem;
import POJO.POrders;
import POJO.SecurityProfile;
import TableData.CustomersTableData;
import TableData.EmpSalaryStack;
import TableData.EmployeeTypes;
import TableData.Employees;
import TableData.MasterItems;
import TableData.OrderItems;
import TableData.Orders;
import TableData.SecurityTableData;
import TableData.TableDesign;

/**
 * Created by USER on 21-06-2018.
 */

public class MBADatabase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MyBusinessAssistant.db";
    Context context;
    public String databasePath = "";
    String CreateSecurityQurey = "Create Table " + SecurityTableData.SecurityTableName + " ("+ SecurityTableData.ProfileId + TableDesign.ID_AUTOINCREMENT + SecurityTableData.Name + TableDesign.TEXT+
            SecurityTableData.Password + TableDesign.TEXT + SecurityTableData.Email + TableDesign.TEXT +
            SecurityTableData.Mobile + " TEXT);";
    String CreateEmployeeTypeQurey = "Create Table " + EmployeeTypes.EmployeeTypeTable + " ("+ EmployeeTypes.EmployeeTypeId + TableDesign.ID_AUTOINCREMENT + EmployeeTypes.EmployeeType + TableDesign.TEXT+
            EmployeeTypes.EmployeeTypeDesc + " TEXT);";
    String CreateEmployeeQurey = "Create Table " + Employees.EmployeesTable + " ("+ Employees.EmployeeId + TableDesign.ID_AUTOINCREMENT + Employees.EmployeeName + TableDesign.TEXT+
            Employees.EmployeeMobile + TableDesign.TEXT+ Employees.EmployeeAlternateNo + TableDesign.TEXT
            +Employees.EmpJoinDate + TableDesign.TEXT+Employees.EmpReleaveDate + TableDesign.TEXT
            +Employees.EmployeeTypeId + TableDesign.INTEGER +Employees.EmpStatus + TableDesign.TEXT
            +Employees.EmpRemarks + " TEXT);";
    String CreateEmpSalStack = "Create Table " + EmpSalaryStack.EmpSalaryStackTable + " ("+ EmpSalaryStack.EmpSalStackId + TableDesign.ID_AUTOINCREMENT + EmployeeTypes.EmployeeTypeId + TableDesign.INTEGER
            +EmpSalaryStack.EmpSalary + TableDesign.TEXT+EmpSalaryStack.EmpSalChangeDate + TableDesign.TEXT
            +EmpSalaryStack.EMpSalReason + " TEXT);";
    String CreateCustomerQurey = "Create Table " + CustomersTableData.CustomerTableName + " ("+ CustomersTableData.CustomerID + TableDesign.ID_AUTOINCREMENT + CustomersTableData.CustomerName + TableDesign.TEXT+
            CustomersTableData.CustomerMobile + TableDesign.TEXT + CustomersTableData.CustomerPlace +" TEXT);";
    String CreateMasterItems="Create Table "+ MasterItems.MasterItemsTable+" ("+ MasterItems.MasterItemId+TableDesign.ID_AUTOINCREMENT+MasterItems.MasterItemName+" TEXT );";

    String CreateOrders="Create Table "+ Orders.OrdersTable+" ("+Orders.OrderId +TableDesign.ID_AUTOINCREMENT+Orders.OrderDate+TableDesign.TEXT+Orders.OrderName+TableDesign.TEXT+Orders.CustomerId+" INTEGER );";

    String CreateOrderItems="Create Table "+ OrderItems.OrderItemsTable+" ("+OrderItems.OrderItemId+TableDesign.ID_AUTOINCREMENT+OrderItems.OrderItemName+TableDesign.TEXT
            +OrderItems.OrderItemQty+TableDesign.TEXT+OrderItems.OrderItemSuggest+TableDesign.TEXT+OrderItems.OrderId+" INTEGER );";

    public MBADatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        databasePath = context.getDatabasePath("MyBusinessAssistant").getPath();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CreateSecurityQurey);
        db.execSQL(CreateEmployeeTypeQurey);
        db.execSQL(CreateEmployeeQurey);
        db.execSQL(CreateEmpSalStack);
        db.execSQL(CreateCustomerQurey);
        db.execSQL(CreateMasterItems);
        db.execSQL(CreateOrders);
        db.execSQL(CreateOrderItems);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    // Security Profile Actions start
    public SecurityProfile getProfile(){
        SecurityProfile securityProfile= new SecurityProfile();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor mCursor = sqLiteDatabase.rawQuery("SELECT * FROM " + SecurityTableData.SecurityTableName, null);
        if (mCursor.moveToFirst()){
            do{
                securityProfile.setProfileId(mCursor.getInt(mCursor.getColumnIndex(SecurityTableData.ProfileId)));
                securityProfile.setName(mCursor.getString(mCursor.getColumnIndex(SecurityTableData.Name)));
                securityProfile.setEmail(mCursor.getString(mCursor.getColumnIndex(SecurityTableData.Email)));
                securityProfile.setMobile(mCursor.getString(mCursor.getColumnIndex(SecurityTableData.Mobile)));
                securityProfile.setPassword(mCursor.getString(mCursor.getColumnIndex(SecurityTableData.Password)));

            }while (mCursor.moveToNext());
            mCursor.close();
        }
        sqLiteDatabase.close();
        return securityProfile;
    }
    // ********Security Profile Actions End********

    //*********EmployeeType Actions Start********

    //********EmployeeType Actions End********

    //********Employee Actions Start********

    //********Employee Actions End********


    ///**********Item Master Start *********////
    public void AddOrUpdateItem(Item item){
        SQLiteDatabase dataBase = this.getReadableDatabase();
        ContentValues values = new ContentValues();

        values.put(MasterItems.MasterItemName, item.getMasterItemName());

        dataBase.beginTransaction();
        if(item.getMasterItemId()==0)
            dataBase.insert(MasterItems.MasterItemsTable, null, values);
        else{
            dataBase.update(MasterItems.MasterItemsTable,values,
                    MasterItems.MasterItemId+"=?",new String[] {String.valueOf(item.getMasterItemId())});
        }
        dataBase.setTransactionSuccessful();
        dataBase.endTransaction();
        dataBase.close();
    }
    public void CheckInsertMasterItem(Item item){

        String query="INSERT INTO "+ MasterItems.MasterItemsTable+"( "+MasterItems.MasterItemName
                +" ) SELECT '"+item.getMasterItemName()
                +"' WHERE NOT EXISTS(SELECT 1 FROM "+MasterItems.MasterItemsTable+" WHERE "
                +MasterItems.MasterItemName+" = '"+item.getMasterItemName()+"')";
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(query);
    }
    public ArrayList<Item> GetMasterItems(){
        ArrayList<Item> items=new ArrayList<Item>();

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor mCursor = sqLiteDatabase.rawQuery("SELECT * FROM " + MasterItems.MasterItemsTable, null);
        if (mCursor.moveToFirst()){
            do{
                Item item=new Item();
                item.setMasterItemId(mCursor.getInt(mCursor.getColumnIndex(MasterItems.MasterItemId)));
                item.setMasterItemName(mCursor.getString(mCursor.getColumnIndex(MasterItems.MasterItemName)));
                items.add(item);

            }while (mCursor.moveToNext());
            mCursor.close();
        }
        sqLiteDatabase.close();
        return items;
    }

    ///**********Item Master End *********////

    public ArrayList<Customers> getCustomers(){
        ArrayList<Customers> customers=new ArrayList<Customers>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor mCursor = sqLiteDatabase.rawQuery("SELECT * FROM " + CustomersTableData.CustomerTableName, null);
        if (mCursor.moveToFirst()){
            do{
                Customers customer=new Customers();
                customer.setCustomerID(mCursor.getInt(mCursor.getColumnIndex(CustomersTableData.CustomerID)));
                customer.setCustomerName(mCursor.getString(mCursor.getColumnIndex(CustomersTableData.CustomerName)));
                customer.setCustomerMobile(mCursor.getString(mCursor.getColumnIndex(CustomersTableData.CustomerMobile)));
                customer.setCustomerPlace(mCursor.getString(mCursor.getColumnIndex(CustomersTableData.CustomerPlace)));
                customers.add(customer);

            }while (mCursor.moveToNext());
            mCursor.close();
        }
        sqLiteDatabase.close();
        return customers;
    }
    public void SaveCustomerData(Customers customer) {
        SQLiteDatabase dataBase = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(CustomersTableData.CustomerName, customer.getCustomerName());
        values.put(CustomersTableData.CustomerMobile, customer.getCustomerMobile());
        values.put(CustomersTableData.CustomerPlace, customer.getCustomerPlace());
        dataBase.insert(CustomersTableData.CustomerTableName, null, values);
        dataBase.close();
    }
    public ArrayList<POrders> getOrders(int CustomerID){
        ArrayList<POrders> orders=new ArrayList<POrders>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor mCursor = sqLiteDatabase.rawQuery("SELECT * FROM " + Orders.OrdersTable+" where "
                +Orders.CustomerId+" = "+CustomerID +" Order by "+Orders.OrderDate+" desc", null);
        if (mCursor.moveToFirst()){
            do{
                POrders order=new POrders();
                order.setOrderId(mCursor.getInt(mCursor.getColumnIndex(Orders.OrderId)));
                order.setOrderName(mCursor.getString(mCursor.getColumnIndex(Orders.OrderName)));
                order.setOrderDate(mCursor.getString(mCursor.getColumnIndex(Orders.OrderDate)));
                order.setCustomerId(mCursor.getInt(mCursor.getColumnIndex(Orders.CustomerId)));
                orders.add(order);
            }while (mCursor.moveToNext());
            mCursor.close();
        }
        sqLiteDatabase.close();
        return orders;
    }
    public void InsertOrder(POrders order) {
        SQLiteDatabase dataBase = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(Orders.OrderName, order.getOrderName());
        values.put(Orders.OrderDate, order.getOrderDate());
        values.put(Orders.CustomerId, order.getCustomerId());
        dataBase.insert(Orders.OrdersTable, null, values);
        dataBase.close();
    }
    public void InsertOrUpdateOrderItem(OrderItem orderItem) {
        SQLiteDatabase dataBase = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(OrderItems.OrderItemName, orderItem.getItemName());
        values.put(OrderItems.OrderItemQty, orderItem.getItemQty());
        values.put(OrderItems.OrderItemSuggest, orderItem.getItemSuggest());
        values.put(OrderItems.OrderId, orderItem.getOrderId());
        if(orderItem.getItemId()==0)
            dataBase.insert(OrderItems.OrderItemsTable, null, values);
        else
            dataBase.update(OrderItems.OrderItemsTable,values,
                    OrderItems.OrderItemId+"=?",new String[] {String.valueOf(orderItem.getItemId())});
        dataBase.close();
    }
    public ArrayList<OrderItem> getOrderItems(int OrderId){
        ArrayList<OrderItem> orderitems=new ArrayList<OrderItem>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor mCursor = sqLiteDatabase.rawQuery("SELECT * FROM " + OrderItems.OrderItemsTable+" where "
                +OrderItems.OrderId+" = "+OrderId +" Order by "+OrderItems.OrderItemId+" desc", null);
        if (mCursor.moveToFirst()){
            do{
                OrderItem orderitem=new OrderItem();
                orderitem.setItemId(mCursor.getInt(mCursor.getColumnIndex(OrderItems.OrderItemId)));
                orderitem.setItemName(mCursor.getString(mCursor.getColumnIndex(OrderItems.OrderItemName)));
                orderitem.setItemQty(mCursor.getString(mCursor.getColumnIndex(OrderItems.OrderItemQty)));
                orderitem.setItemSuggest(mCursor.getString(mCursor.getColumnIndex(OrderItems.OrderItemSuggest)));
                orderitem.setOrderId(mCursor.getInt(mCursor.getColumnIndex(OrderItems.OrderId)));
                orderitems.add(orderitem);
            }while (mCursor.moveToNext());
            mCursor.close();
        }
        sqLiteDatabase.close();
        return orderitems;
    }
}

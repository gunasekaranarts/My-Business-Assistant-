package Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import POJO.Customers;
import POJO.EmpSalaryTransactions;
import POJO.Employee;
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
import TableData.SalaryTransaction;
import TableData.SecurityTableData;
import TableData.TableDesign;

/**
 * Created by USER on 21-06-2018.
 */

public class MBADatabase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "MyBusinessAssistant.db";
    Context context;
    public String databasePath = "";
    String CreateSecurityQurey = "Create Table " + SecurityTableData.SecurityTableName + " ("+ SecurityTableData.ProfileId + TableDesign.ID_AUTOINCREMENT + SecurityTableData.Name + TableDesign.TEXT+
            SecurityTableData.Password + TableDesign.TEXT + SecurityTableData.Email + TableDesign.TEXT +
            SecurityTableData.Mobile +TableDesign.TEXT+SecurityTableData.CompanyName+TableDesign.TEXT
            +SecurityTableData.BillHeader+TableDesign.TEXT
            + SecurityTableData.Address+ " TEXT);";
    String CreateEmployeeTypeQurey = "Create Table " + EmployeeTypes.EmployeeTypeTable + " ("+ EmployeeTypes.EmployeeTypeId + TableDesign.ID_AUTOINCREMENT + EmployeeTypes.EmployeeType + TableDesign.TEXT+
            EmployeeTypes.EmployeeTypeDesc + " TEXT);";
    String CreateEmployeeQurey = "Create Table " + Employees.EmployeesTable + " ("+ Employees.EmployeeId + TableDesign.ID_AUTOINCREMENT + Employees.EmployeeName + TableDesign.TEXT+
            Employees.EmployeeMobile + TableDesign.TEXT+ Employees.EmployeeAlternateNo + TableDesign.TEXT
            +Employees.EmpJoinDate + TableDesign.TEXT+Employees.EmpReleaveDate + TableDesign.TEXT
            +Employees.EmployeePhoto + TableDesign.BLOB
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
    String EmpSalTransactions="Create Table "+ SalaryTransaction.EmpSalaryTransactionTable+" ("+
            SalaryTransaction.EmpSalTransId+TableDesign.ID_AUTOINCREMENT+SalaryTransaction.EmployeeId+TableDesign.INTEGER+
            SalaryTransaction.EmpSalTransDate+TableDesign.TEXT+SalaryTransaction.EmpSalAmount+TableDesign.INTEGER+
            SalaryTransaction.EmpSalTransType+TableDesign.TEXT+SalaryTransaction.EmpSalReason+" TEXT );";

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
        db.execSQL(EmpSalTransactions);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion<=1)
        {
            db.execSQL("Alter table "+SecurityTableData.SecurityTableName+ " ADD COLUMN "+ SecurityTableData.BillHeader +" TEXT");

        }
        if(oldVersion<=2)
        {
            db.execSQL("Alter table "+Employees.EmployeesTable+ " ADD COLUMN "+ Employees.EmployeePhoto +" BLOB");
            db.execSQL(EmpSalTransactions);
        }
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
                securityProfile.setCompanyName(mCursor.getString(mCursor.getColumnIndex(SecurityTableData.CompanyName)));
                securityProfile.setAddress(mCursor.getString(mCursor.getColumnIndex(SecurityTableData.Address)));
                securityProfile.setBillHeader(mCursor.getString(mCursor.getColumnIndex(SecurityTableData.BillHeader)));

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
    public void AddOrUpdateEmployee(Employee employee){
        SQLiteDatabase dataBase = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Employees.EmployeeName, employee.getEmployeeName());
        values.put(Employees.EmployeeMobile, employee.getEmpMobile());
        values.put(Employees.EmployeePhoto, employee.getEmployeePhoto());
        values.put(Employees.EmpJoinDate, employee.getEmpJoinDate());
        values.put(Employees.EmployeeAlternateNo, employee.getEmpAlternateNo());
        values.put(Employees.EmployeeTypeId, employee.getEmployeeTypeId());
        values.put(Employees.EmpReleaveDate, employee.getEmpReleaveDate());
        values.put(Employees.EmpRemarks, employee.getEmpRemarks());
        values.put(Employees.EmpStatus, employee.getEmpStatus());


        if(employee.getEmployeeId()==0)
          dataBase.insert(Employees.EmployeesTable, null, values);
        else{
            dataBase.update(Employees.EmployeesTable,values,
                    Employees.EmployeeId+"=?",new String[] {String.valueOf(employee.getEmployeeId())});
        }

        dataBase.setTransactionSuccessful();
        dataBase.endTransaction();
        dataBase.close();
    }

    public ArrayList<Employee> GetEmployees(){
        ArrayList<Employee> employees=new ArrayList<Employee>();

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor mCursor = sqLiteDatabase.rawQuery("SELECT * FROM " + Employees.EmployeesTable, null);
        if (mCursor.moveToFirst()){
            do{
                Employee item=new Employee();
                item.setEmployeeId(mCursor.getInt(mCursor.getColumnIndex(Employees.EmployeeId)));
                item.setEmployeeName(mCursor.getString(mCursor.getColumnIndex(Employees.EmployeeName)));
                item.setEmpMobile(mCursor.getString(mCursor.getColumnIndex(Employees.EmployeeMobile)));
                item.setEmployeePhoto(mCursor.getBlob(mCursor.getColumnIndex(Employees.EmployeePhoto)));
                item.setEmpJoinDate(mCursor.getString(mCursor.getColumnIndex(Employees.EmpJoinDate)));
                item.setEmpAlternateNo(mCursor.getString(mCursor.getColumnIndex(Employees.EmployeeAlternateNo)));
                item.setEmployeeTypeId(mCursor.getInt(mCursor.getColumnIndex(Employees.EmployeeTypeId)));
                item.setEmpReleaveDate(mCursor.getString(mCursor.getColumnIndex(Employees.EmpReleaveDate)));
                item.setEmpRemarks(mCursor.getString(mCursor.getColumnIndex(Employees.EmpRemarks)));
                item.setEmpStatus(mCursor.getString(mCursor.getColumnIndex(Employees.EmpStatus)));
                employees.add(item);

            }while (mCursor.moveToNext());
            mCursor.close();
        }
        sqLiteDatabase.close();
        return employees;
    }
    public Employee GetEmployeeById(int EmpId){
        Employee employee=new Employee();

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor mCursor = sqLiteDatabase.rawQuery("SELECT * FROM " + Employees.EmployeesTable+
                " where "+Employees.EmployeeId+" = "+EmpId, null);
        if (mCursor.moveToFirst()){
            do{

                employee.setEmployeeId(mCursor.getInt(mCursor.getColumnIndex(Employees.EmployeeId)));
                employee.setEmployeeName(mCursor.getString(mCursor.getColumnIndex(Employees.EmployeeName)));
                employee.setEmpMobile(mCursor.getString(mCursor.getColumnIndex(Employees.EmployeeMobile)));
                //employee.setEmployeePhoto(mCursor.getBlob(mCursor.getColumnIndex(Employees.EmployeePhoto)));
                employee.setEmpJoinDate(mCursor.getString(mCursor.getColumnIndex(Employees.EmpJoinDate)));
                employee.setEmpAlternateNo(mCursor.getString(mCursor.getColumnIndex(Employees.EmployeeAlternateNo)));
                employee.setEmployeeTypeId(mCursor.getInt(mCursor.getColumnIndex(Employees.EmployeeTypeId)));
                employee.setEmpReleaveDate(mCursor.getString(mCursor.getColumnIndex(Employees.EmpReleaveDate)));
                employee.setEmpRemarks(mCursor.getString(mCursor.getColumnIndex(Employees.EmpRemarks)));
                employee.setEmpStatus(mCursor.getString(mCursor.getColumnIndex(Employees.EmpStatus)));


            }while (mCursor.moveToNext());
            mCursor.close();
        }
        sqLiteDatabase.close();
        return employee;
    }
    public void AddOrUpdateSalTrans(EmpSalaryTransactions transaction){
        SQLiteDatabase dataBase = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(SalaryTransaction.EmployeeId, transaction.getEmployeeId());
        values.put(SalaryTransaction.EmpSalAmount, transaction.getTransactionAmount());
        values.put(SalaryTransaction.EmpSalTransDate, transaction.getTransactionDate().replace("-",""));
        values.put(SalaryTransaction.EmpSalTransType, transaction.getSalaryTransType());
        values.put(SalaryTransaction.EmpSalReason, transaction.getTransDesc());


        dataBase.beginTransaction();
        if(transaction.getSalaryTransactionId()==0)
            dataBase.insert(SalaryTransaction.EmpSalaryTransactionTable, null, values);
        else{
            dataBase.update(SalaryTransaction.EmpSalaryTransactionTable,values,
                    SalaryTransaction.EmpSalTransId+"=?",new String[] {String.valueOf(transaction.getSalaryTransactionId())});
        }
        dataBase.setTransactionSuccessful();
        dataBase.endTransaction();
        dataBase.close();
    }
    public ArrayList<EmpSalaryTransactions> GetSalTransactions(int EmpId){
        ArrayList<EmpSalaryTransactions> transactions=new ArrayList<EmpSalaryTransactions>();

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor mCursor = sqLiteDatabase.rawQuery("SELECT * FROM " + SalaryTransaction.EmpSalaryTransactionTable
                +" where "+SalaryTransaction.EmployeeId+"= "+EmpId +" Order by "+SalaryTransaction.EmpSalTransDate, null);
        if (mCursor.moveToFirst()){
            do{
                EmpSalaryTransactions transaction=new EmpSalaryTransactions();
                transaction.setSalaryTransactionId(mCursor.getInt(mCursor.getColumnIndex(SalaryTransaction.EmpSalTransId)));
                transaction.setEmployeeId(mCursor.getInt(mCursor.getColumnIndex(SalaryTransaction.EmployeeId)));
                String TransDate=mCursor.getString(mCursor.getColumnIndex(SalaryTransaction.EmpSalTransDate));
                StringBuilder transdates = new StringBuilder(TransDate);
                transdates.insert(4,"-");
                transdates.insert(7,"-");
                transaction.setTransactionDate(transdates.toString());
                transaction.setSalaryTransType(mCursor.getString(mCursor.getColumnIndex(SalaryTransaction.EmpSalTransType)));
                transaction.setTransactionAmount(mCursor.getInt(mCursor.getColumnIndex(SalaryTransaction.EmpSalAmount)));
                transaction.setTransDesc(mCursor.getString(mCursor.getColumnIndex(SalaryTransaction.EmpSalReason)));
                transactions.add(transaction);

            }while (mCursor.moveToNext());
            mCursor.close();
        }
        sqLiteDatabase.close();
        return transactions;
    }
    public int GetSalarySum(int EmpId){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        int sumsalary=0;
        Cursor mCursor = sqLiteDatabase.rawQuery("SELECT (ifnull((select sum("+SalaryTransaction.EmpSalAmount+") from "+SalaryTransaction.EmpSalaryTransactionTable+" where "+SalaryTransaction.EmpSalTransType+"='1' and "+SalaryTransaction.EmployeeId+"= "+EmpId +"),0)-ifnull((select sum("+SalaryTransaction.EmpSalAmount+") from "+SalaryTransaction.EmpSalaryTransactionTable+" where "+SalaryTransaction.EmpSalTransType+"='3' and "+SalaryTransaction.EmployeeId+"= "+EmpId +"),0)) as Pending ", null);
        if (mCursor.moveToFirst()){
            do{
                sumsalary= mCursor.getInt(0);

            }while (mCursor.moveToNext());
            mCursor.close();
        }
        sqLiteDatabase.close();
        return sumsalary;
    }
    public int GetAdvanceSalarySum(int EmpId){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        int sumsalary=0;
        Cursor mCursor = sqLiteDatabase.rawQuery("SELECT (ifnull((select sum("+SalaryTransaction.EmpSalAmount+") from "+SalaryTransaction.EmpSalaryTransactionTable+" where "+SalaryTransaction.EmpSalTransType+"='2' and "+SalaryTransaction.EmployeeId+"= "+EmpId +"),0)-ifnull((select sum("+SalaryTransaction.EmpSalAmount+") from "+SalaryTransaction.EmpSalaryTransactionTable+" where "+SalaryTransaction.EmpSalTransType+"='4' and "+SalaryTransaction.EmployeeId+"= "+EmpId +"),0)) as Pending", null);
        if (mCursor.moveToFirst()){
            do{
                sumsalary= mCursor.getInt(0);

            }while (mCursor.moveToNext());
            mCursor.close();
        }
        sqLiteDatabase.close();
        return sumsalary;
    }


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
    public void CopyOrderItems(int OldOrderId) {
        SQLiteDatabase dataBase = this.getReadableDatabase();
        int newOrderId=0;
        Cursor mCursor = dataBase.rawQuery("select Max("+Orders.OrderId+") from "+Orders.OrdersTable, null);
        if (mCursor.moveToFirst()){
            do{
                newOrderId=(mCursor.getInt(0));
            }while (mCursor.moveToNext());
            mCursor.close();
        }
        String query="insert into "+OrderItems.OrderItemsTable+" ("+ OrderItems.OrderItemName +","
                +OrderItems.OrderItemQty+","+OrderItems.OrderItemSuggest+","+OrderItems.OrderId
                +") select "+OrderItems.OrderItemName
                +","+OrderItems.OrderItemQty+","+OrderItems.OrderItemSuggest+","+String.valueOf(newOrderId)
                +" from "+OrderItems.OrderItemsTable+" where "+OrderItems.OrderId+"="+OldOrderId;
        dataBase.execSQL(query);
                dataBase.close();
    }
    public void DeleteOrder(int OldOrderId) {
        SQLiteDatabase dataBase = this.getReadableDatabase();
        dataBase.execSQL("delete from "+Orders.OrdersTable+" where "+Orders.OrderId+"="+OldOrderId);
        dataBase.execSQL("delete from "+OrderItems.OrderItemsTable +" where "+OrderItems.OrderId+"="+OldOrderId);
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

    public ArrayList<String> getAutoSuggestions(){
        ArrayList<String> items=new ArrayList<String>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor mCursor = sqLiteDatabase.rawQuery("SELECT distinct "+OrderItems.OrderItemSuggest+" FROM " + OrderItems.OrderItemsTable, null);
        if (mCursor.moveToFirst()){
            do{
                String item=new String();

                item=mCursor.getString(mCursor.getColumnIndex(OrderItems.OrderItemSuggest));
                items.add(item);
            }while (mCursor.moveToNext());
            mCursor.close();
        }
        sqLiteDatabase.close();
        return items;
    }
    public ArrayList<String> getAutoQty(){
        ArrayList<String> items=new ArrayList<String>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor mCursor = sqLiteDatabase.rawQuery("SELECT distinct "+OrderItems.OrderItemQty+" FROM " + OrderItems.OrderItemsTable, null);
        if (mCursor.moveToFirst()){
            do{
                String item=new String();

                item=mCursor.getString(mCursor.getColumnIndex(OrderItems.OrderItemQty));
                items.add(item);
            }while (mCursor.moveToNext());
            mCursor.close();
        }
        sqLiteDatabase.close();
        return items;
    }


}

package id.wesudgitgud.burrows.models;

import android.provider.ContactsContract;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import id.wesudgitgud.burrows.Controller.DatabaseManager;

/**
 * Created by rezaramadhan on 25/02/2017.
 */

public class Item {
    private String TAG = "ITEM";
    public String type;
    public int price;
    public String name;

    private DatabaseReference userItemDBRef;

    public Item() {

    }

    public Item(String type, String name, int price) {

        this.type = type;

        this.price = price;
        this.name = name;
    }

    public void buyItem(String buyerUsername) {
        Log.d(TAG, "buy-------------------" + buyerUsername);
        userItemDBRef = FirebaseDatabase.getInstance().getReference().child("useritem").child(buyerUsername).child(type);

        int itemIdx = findUserItem(buyerUsername);
        if (itemIdx != -1) {
            Log.d(TAG, "EXISTING ITEM");
            userItemDBRef = FirebaseDatabase.getInstance().getReference().child("useritem").child(buyerUsername).child(type).child(Integer.toString(itemIdx)).child("quantity");
            DatabaseManager db = new DatabaseManager("useritem/" + buyerUsername + "/" + type + "/" + Integer.toString(itemIdx) + "");
            Log.d(TAG, "data\n" + db.getData());
            String quantity = null;
            try {
                quantity = db.getJSONDObject().getString("quantity");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            int newQuantity = Integer.parseInt(quantity) + 1;
            userItemDBRef.setValue(newQuantity);
        } else {
            Log.d(TAG, "NEWITEM");
            HashMap<String, Object> newItem = new HashMap<>();
            newItem.put("name", this.name);
            newItem.put("quantity", 1);
            JSONArray items = null;

            DatabaseManager db = new DatabaseManager("useritem/" + buyerUsername + "/" + type);

            try {
                if (db.getData() == null || db.getData().equals("null"))
                    items = new JSONArray();
                else
                    items = db.getJSONArray();

                items.put(new JSONObject(newItem));
//                Log.d(TAG, "Arr\n" + items.toString());

                HashMap<String, Object> map = DatabaseManager.convertToFirebaseArray(items);
//                Log.d(TAG, "Map\n"+ map.toString());

                userItemDBRef.setValue(map);
//            userItemDBRef.orderByKey();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private int findUserItem(String buyerUsername) {
        Log.d(TAG, "useritem/" + buyerUsername + "/" + type);
        DatabaseManager db = new DatabaseManager("useritem/" + buyerUsername + "/" + type);

        try {

            Log.d(TAG, "Arr data \n" + db.getData());
            if (db.getData() == null || db.getData().equals("null"))
                return -1;
            JSONArray items = db.getJSONArray();
            Log.d(TAG, "Arr Usrhasitem  \n" + items.toString());

            for (int i = 0; i < items.length(); i++) {
                if (items.getJSONObject(i).getString("name").equals(this.name))
                    return i;
            }
            return -1;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public void useItem(String ownerUsername) {
        int itemIdx = findUserItem(ownerUsername);

        if (itemIdx != -1) {
            userItemDBRef = FirebaseDatabase.getInstance().getReference().child("useritem")
                    .child(ownerUsername).child(type).child(Integer.toString(itemIdx)).child("quantity");

            DatabaseManager db = new DatabaseManager("useritem/" + ownerUsername + "/" + type + "/" + Integer.toString(itemIdx) + "");
            Log.d(TAG, "data\n" + db.getData());
            String quantity = null;
            try {
                quantity = db.getJSONDObject().getString("quantity");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            int newQuantity = Integer.parseInt(quantity) - 1;
            userItemDBRef.setValue(newQuantity);
        }
    }

}

package com.example.shopping;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
//import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

// This is a SQL table
@Entity
class Item
{
    @PrimaryKey
    @NonNull
    public String thename;

    @ColumnInfo
    public int itemcost;

    @ColumnInfo
    public boolean itempurchased;
}

@Entity
class UserInfo
{
    @PrimaryKey
    @NonNull
    public String username;

    @ColumnInfo
    public String password;
}


@Dao
interface UserInfoDao {
    @Query("SELECT * FROM userinfo")
    List<UserInfo> getAll();

    @Query("SELECT * FROM userinfo WHERE username LIKE :n LIMIT 1")
    UserInfo findByName(String n);

    @Insert
    void insertAll(UserInfo... users);

    @Delete
    void delete(UserInfo user);
}



// This is the class used to load from the database
@Dao
interface ItemDao {
    @Query("SELECT * FROM item")
    List<Item> getAll();

    @Query("SELECT * FROM item WHERE thename LIKE :n LIMIT 1")
    Item findByName(String n);

    @Insert
    void insertAll(Item... users);

    @Delete
    void delete(Item user);
}

//This is the database class
@Database(entities = {Item.class, UserInfo.class}, version=1, exportSchema = false)
abstract class AppDatabase extends RoomDatabase
{
    public abstract ItemDao itemDao();
    public abstract UserInfoDao userInfoDao();
}

public class ScrollingActivity extends AppCompatActivity implements View.OnClickListener{

    public class ShopData {
        public ShopData(String name, int imageResource, String description, int cost, Date date) {

            m_name = name;
            m_imageResource = imageResource;
            m_description = description;
            m_cost = cost;
            m_purchased = false;
            m_date = date;
        }
        public ShopData(String n, String o, String d, int c)
        {
            m_name = n;
            try{
                Field idField = R.drawable.class.getDeclaredField(o);
                m_imageResource = ((Field) idField).getInt(idField);
            } catch (Exception e) {

            }
        }

        public String m_name;
        public int m_imageResource;
        public String m_description;
        public int m_cost;
        public boolean m_purchased;
        public Date m_date;
    };


    public static ScrollingActivity instance = null;


    ArrayList<ShopData> m_data = new ArrayList<ShopData>(
        Arrays.asList(
            new ShopData("Dog", "dog", "Cute puppy", 70),
            new ShopData("Carrot", "carrot", "Organic", 5),
            new ShopData("Bone", "bone", "Good chew", 14),
            new ShopData("Flame", "flame", "Very hot", 100),
            new ShopData("Grapes", "grapes", "Sweet sweet", 5),
            new ShopData("House", "house", "Toy house (not Lego)", 15),
            new ShopData("Lamp", "lamp", "It functions", 10),
            new ShopData("Mouse", "mouse", "Good cat snack", 3),
            new ShopData("Nail", "nail", "Just nail", 2),
            new ShopData("Penguin", "penguin", "Not real", 9),
            new ShopData("Rocks", "rocks", "Hard", 25),
            new ShopData("Star", "star", "Lighten up your sky", 90),
            new ShopData("Toad", "toad", "Not ugly at all", 12),
            new ShopData("Van", "van", "You can live in it!", 1000),
            new ShopData("Wheat", "wheat", "Not gluten free", 10),
            new ShopData("Yak", "yak", "Brings good luck", 28)


            ));

    public CustomDialogClass customDialog = null;
    private  SharedPreferences preferences = null;
    private int money = 0;
    final private String MoneyID = "Money";
    private boolean signedIn = false;
    public AppDatabase db = null;

    public void AddMoney(int amount)
    {
        money += amount;

        TextView m = findViewById(R.id.money);
        m.setText("$" + money);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(MoneyID, money);
        editor.commit();
    }

    public int SeeMoney()
    {
        return money;
    }

    private Button signIn, signOut, sort;

    private RadioGroup radioGroup;
    private RadioButton byName, byCost;

    public void populate()
    {
        Thread load = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < m_data.size(); ++i)
                {
                    Item item_ = db.itemDao().findByName(m_data.get(i).m_name);
                    if (item_ != null && item_.itempurchased)
                    {
                        continue;
                    }
                    LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

                    final View myShopItem = inflater.inflate(R.layout.shop_item, null);
                    final int tmp = i;

                    View.OnClickListener click = new View.OnClickListener() {
                        public int id = tmp;

                        @Override

                        public void onClick(View v) {
                            ScrollingActivity.instance.ShopClicked(v, id);

                        }

                    };

                    Button textButton = myShopItem.findViewById(R.id.button0);
                    textButton.setText(m_data.get(i).m_name);
                    ImageButton imageButton = myShopItem.findViewById(R.id.imageButton0);
                    imageButton.setImageResource(m_data.get(i).m_imageResource);
                    imageButton.setOnClickListener(click);

                    final LinearLayout layout = findViewById(R.id.myView);
                    ScrollingActivity.instance.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            layout.addView(myShopItem);
                        }

                    });
                }


            }

        });
        load.start() ;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        instance = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = Room.databaseBuilder(this, AppDatabase.class,
                "ItemDatabase").build();


        signIn = (Button) findViewById(R.id.log_in);
        signOut = (Button) findViewById(R.id.sign_out);
        Intent intent = this.getIntent();
        signedIn = intent.getBooleanExtra("signed", signedIn);
        System.out.println("Get result from : " + signedIn);
        if (signedIn) {
            signIn.setVisibility(View.INVISIBLE);
            signOut.setVisibility(View.VISIBLE);
        } else {
            signIn.setVisibility(View.VISIBLE);
            signOut.setVisibility(View.INVISIBLE);
        }
        signIn.setOnClickListener(this);
        signOut.setOnClickListener(this);

        //sort
//        sort = (Button) findViewById(R.id.sort);
//        sort.setOnClickListener(this);
//        byName = findViewById(R.id.sortName);
//        byCost = findViewById(R.id.sortCost);
//        radioGroup = (RadioGroup) findViewById(R.id.sortGroup);



        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        money = preferences.getInt(MoneyID, 200);
        TextView m = findViewById(R.id.money);
        m.setText("$" + money);


        customDialog = new CustomDialogClass(this);

        RequestQueue queue = Volley.newRequestQueue(ScrollingActivity.instance);
        String url = "http://10.0.2.2:5005/list";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject reader = new JSONObject(response);
                            JSONArray lst = reader.getJSONArray("list");
                            for (int i = 0; i < lst.length(); ++i) {
                                JSONObject obj = lst.getJSONObject(i);
                                String name = obj.getString("name");
                                String image = obj.getString("image");
                                String desc = obj.getString("description");
                                int cost = obj.getInt("cost");
                                m_data.add(new ShopData(name, image, desc, cost));

                            }
                        } catch (JSONException e) {
                        }
                        populate();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        populate();
                    }
                });
        queue.add(stringRequest);


    }

    @Override
    public void onClick(View view) {
//        LinearLayout layout;
//
        switch (view.getId()) {

//            case R.id.sort:
//                final int selectedId = radioGroup.getCheckedRadioButtonId();
//
//                layout = findViewById(R.id.myView);
//                layout.removeAllViews();
//                sort by type
//                Arrays.sort(m_data, new Comparator<ShopData>() {
//                    @Override
//                    public int compare(ShopData shopData, ShopData t1) {
//                        if (selectedId == R.id.sortName) {
//                            return shopData.m_name.compareTo(t1.m_name);
//                        } else if (selectedId == R.id.sortCost) {
//                            return shopData.m_cost - t1.m_cost;
//                        }
//                        else {
//                            return shopData.m_date.compareTo(t1.m_date);
//                        }
//                    }
//                });
//                Thread load = new Thread(new Runnable()
//                {
//                    @Override
//                    public void run()
//                    {
//                        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
//                        for (int i = 0; i < m_data.size(); ++i) {
//
//                            Item item_ = db.itemDao().findByName(m_data.get(i).m_name);
//                            if (item_ != null && item_.itempurchased) {
//                                continue;
//                            }
//
//                            final View myShopItem = inflater.inflate(R.layout.shop_item, null);
//                            final int tmp = i;
//
//                            View.OnClickListener click = new View.OnClickListener() {
//                                public int id = tmp;
//                                @Override
//
//                                public void onClick(View v)
//                                {
//                                    ScrollingActivity.instance.ShopClicked(v,id);
//
//                                }
//
//                            };
//
//                            Button textButton = myShopItem.findViewById(R.id.button0);
//                            textButton.setText(m_data.get(i).m_name);
//                            ImageButton imageButton = myShopItem .findViewById(R.id.imageButton0);
//                            imageButton.setImageResource(m_data.get(i).m_imageResource) ;
//                            imageButton.setOnClickListener(click);
//
//                            final LinearLayout layout =findViewById(R.id.myView);
//                            ScrollingActivity.instance.runOnUiThread(new Runnable(){
//                                @Override
//                                public void run(){
//                                    layout.addView(myShopItem);
//                                }
//
//                            });
//                        }
//
//
//                    }
//
//                });
//                load.start() ;
//                break;
//
            case R.id.log_in:
                Intent intent1 = new Intent(this, LoginActivity.class);
                startActivity(intent1);
                break;
            case R.id.sign_out:
                Toast.makeText(this, "Signed out successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, ScrollingActivity.class);
                intent.putExtra("signed",false);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            AddMoney(100);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class CustomDialogClass extends Dialog
    {
        public Context m_context;
        public Button yes, no;
        public int id = -1;
        public int layoutIndex = 0;

        public CustomDialogClass(Context context)
        {
            super(context);
            m_context = (Activity)context;
        }

        @Override
        protected void onCreate(Bundle savedInstance)
        {
            super.onCreate(savedInstance);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.shop_dialog);

            yes = this.findViewById(R.id.buybutton);
            no = this.findViewById(R.id.cancelbutton);

            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = ScrollingActivity.instance.customDialog.id;
                    if (ScrollingActivity.instance.SeeMoney() >= ScrollingActivity.instance.m_data.get(id).m_cost)
                    {
                        int layoutId = ScrollingActivity.instance.customDialog.layoutIndex;

                        ScrollingActivity.instance.m_data.get(id).m_purchased = true;

                        LinearLayout layout = ((Activity)m_context).findViewById(R.id.myView);
                        layout.removeViewAt(layoutId);

                        ScrollingActivity.instance.AddMoney(-ScrollingActivity.instance.m_data.get(id).m_cost);

                        final Item tmp = new Item();
                        tmp.thename = ScrollingActivity.instance.m_data.get(id).m_name;
                        tmp.itemcost = ScrollingActivity.instance.m_data.get(id).m_cost;
                        tmp.itempurchased = true;

                        Thread save = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ScrollingActivity.instance.db.itemDao().insertAll(tmp);
                            }
                        });
                        save.start();
                    }
                    dismiss();
                }
            });

            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    dismiss();
                }
            }) ;
            SetDetails();



        }
        private void SetDetails()
        {
            TextView title = this.findViewById(R.id.name);
            title.setText(ScrollingActivity.instance.m_data.get(id).m_name);

            TextView  description = this.findViewById(R.id.description);
            description.setText(ScrollingActivity.instance.m_data.get(id).m_description);

            TextView price = this.findViewById(R.id.price);
            price.setText("$" + ScrollingActivity.instance.m_data.get(id).m_cost);

            ImageView image = this.findViewById(R.id.itemImage);
            image.setImageResource(ScrollingActivity.instance.m_data.get(id).m_imageResource);
        }

        public void Show(int index, int lIndex)
        {
            if (id == -1)
            {
                id = index;
                layoutIndex = lIndex;


            }
            else
            {
                id = index;
                layoutIndex = lIndex;
                SetDetails();
            }
            show();
        }

    };

    public void ShopClicked(View v, int dataIndex)
    {
        LinearLayout layout = findViewById(R.id.myView);
        int layoutIndex = layout.indexOfChild((View)v.getParent());
        customDialog.Show(dataIndex, layoutIndex);
    }

}

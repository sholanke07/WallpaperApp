package com.lateef.wallpaper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Header;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kotlin.text.UStringsKt;

public class MainActivity extends AppCompatActivity implements CategoryRVAdapter.CategoryClickInterface{

    private EditText searchEdt;
    private ImageView searchIV;
    private RecyclerView categoryRV, wallpaperRV;
    private ProgressBar loadingPB;
    private ArrayList<String> wallpaperArrayList;
    private ArrayList<CategoryRVModel> categoryRVModelArrayList;
    private CategoryRVAdapter categoryRVAdapter;
    private WallpaperRVAdapter wallpaperRVAdapter;

    //563492ad6f917000010000010e57141f30b2437cb7746c1519d63ee

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchEdt = findViewById(R.id.idEditSearch);
        searchIV = findViewById(R.id.idIvSearch);
        categoryRV = findViewById(R.id.idRVCategory);
        wallpaperRV = findViewById(R.id.idRVWallpapers);
        loadingPB = findViewById(R.id.idPBLoading);
        wallpaperArrayList = new ArrayList<>();
        categoryRVModelArrayList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.HORIZONTAL,false);
        categoryRV.setLayoutManager(linearLayoutManager);
        categoryRVAdapter = new CategoryRVAdapter(categoryRVModelArrayList, this, this::onCategoryClick);
        categoryRV.setAdapter(categoryRVAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        wallpaperRV.setLayoutManager(gridLayoutManager);
        wallpaperRVAdapter = new WallpaperRVAdapter(wallpaperArrayList, this);
        wallpaperRV.setAdapter(wallpaperRVAdapter);

        getCategories();

        getWallpapers();

        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    String searchStr = searchEdt.getText().toString();
                    if (searchStr.isEmpty()){
                        Toast.makeText(MainActivity.this, "Please enter your search query", Toast.LENGTH_SHORT).show();

                    }else {
                        getWallpapersByCategory(searchStr);
                    }
            }
        });
    }

    private void getWallpapersByCategory(String category){
        wallpaperArrayList.clear();
        loadingPB.setVisibility(View.VISIBLE);
        String url = "https://api.pexels.com/v1/search?query="+category+"&per_page=30&page=1";
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                JSONArray photoArray = null;
                try {
                    photoArray = response.getJSONArray("photos");
                    for (int i = 0; i < photoArray.length(); i++){
                        JSONObject photoObj = photoArray.getJSONObject(i);
                        String imgUrl = photoObj.getJSONObject("src").getString("portrait");
                        wallpaperArrayList.add(imgUrl);
                    }
                    wallpaperRVAdapter.notifyDataSetChanged();
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Fail to load wallpapers", Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String>headers = new HashMap<>();
                headers.put("Authorization", "563492ad6f917000010000010e57141f30b2437cb7746c1519d63ee5");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);

    }

    private void getWallpapers(){
        wallpaperArrayList.clear();
        loadingPB.setVisibility(View.VISIBLE);
        String url = "https://api.pexels.com/v1/curated?per_page=30&page=1";
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                try{
                    JSONArray photoArray = response.getJSONArray("photos");
                    for (int i = 0; i < photoArray.length(); i++){
                        JSONObject photoObj = photoArray.getJSONObject(i);
                        String imageUrl = photoObj.getJSONObject("src").getString("portrait");
                        wallpaperArrayList.add(imageUrl);
                    }
                    wallpaperRVAdapter.notifyDataSetChanged();
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Fail to load wallpaper", Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "563492ad6f917000010000010e57141f30b2437cb7746c1519d63ee5");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);

    }

    private void getCategories(){
        categoryRVModelArrayList.add(new CategoryRVModel("Technology", "https://www.bing.com/search?q=technology+images+url&qs=n&form=QBRE&sp=-1&pq=technology+images+url&sc=9-21&sk=&cvid=3A8E7691B265402BAECFC72691F2DFA2&ghsh=0&ghacc=0&ghpl="));
        categoryRVModelArrayList.add(new CategoryRVModel("Programming", "https://www.bing.com/images/search?q=Programming%20Images%20URL&form=IQFRML&first=1&tsc=ImageHoverTitle"));
        categoryRVModelArrayList.add(new CategoryRVModel("Nature", "https://www.bing.com/images/search?q=nature%20Images%20URL&qs=n&form=QBIR&sp=-1&pq=nature%20images%20url&sc=10-17&cvid=9ECD2B4E79864B55A6263CA9FF8C2E2C&ghsh=0&ghacc=0&first=1&tsc=ImageHoverTitle"));
        categoryRVModelArrayList.add(new CategoryRVModel("Travel", "https://www.bing.com/images/search?q=travel%20Images%20Url&qs=n&form=QBIR&sp=-1&pq=travel%20images%20url&sc=4-17&cvid=EFF753DBAD5A43E9B3991B842429C30F&ghsh=0&ghacc=0&first=1&tsc=ImageHoverTitle"));
        categoryRVModelArrayList.add(new CategoryRVModel("Architecture", ""));
        categoryRVModelArrayList.add(new CategoryRVModel("Arts", "https://www.bing.com/images/search?q=arts%20images%20url&qs=n&form=QBIR&sp=-1&pq=a%20images%20url&sc=10-12&cvid=F3CD11EF8528450BA3C48C670A468D91&ghsh=0&ghacc=0&first=1&tsc=ImageHoverTitle"));
        categoryRVModelArrayList.add(new CategoryRVModel("Music", "https://www.bing.com/images/search?q=music%20images%20url&qs=n&form=QBIR&sp=-1&pq=music%20images%20url&sc=5-16&cvid=B20190959F554FB7B75D8A4D5C6107EA&ghsh=0&ghacc=0&first=1&tsc=ImageHoverTitle"));
        categoryRVModelArrayList.add(new CategoryRVModel("Abstract", ""));
        categoryRVModelArrayList.add(new CategoryRVModel("Cars", "https://www.bing.com/images/search?q=car%20Images%20Url&form=IQFRBA&id=E85A6D7D8C4ED8D62AF374A12951DA4117EA10C8&first=1&disoverlay=1"));
        categoryRVModelArrayList.add(new CategoryRVModel("Flowers", ""));

        categoryRVAdapter.notifyDataSetChanged();

    }

    @Override
    public void onCategoryClick(int position) {
        String category = categoryRVModelArrayList.get(position).getCategory();
        getWallpapersByCategory(category);
    }
}
package info.mik.mrua;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

//import info.mik.mrua.utils.ErrorUtils;
//import info.mik.mrua.utils.APIError;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mik on 2018-05-16.
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView = null;
    private CoordinatorLayout coordinatorLayout;
    public AdapterRepo adapter;
    ModelRepoResponse repositoriesList;

    private static final int PAGE_START = 1;
    private int currentPage = PAGE_START;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        coordinatorLayout = findViewById(R.id.container);
        recyclerView = findViewById(R.id.linear_recyclerview);
        progressBar = findViewById(R.id.repository_progress);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration); // Add divider between items
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) {
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                currentPage+=1;
                                fetchNextPage();
                                progressBar.setVisibility(View.VISIBLE);
                            }
                        }, 1000);

                    }
                    progressBar.setVisibility(View.INVISIBLE);

                }
            }
        });


        //checking for network connectivity
        if (!isNetworkAvailable()) {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "No Network connection", Snackbar.LENGTH_LONG)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            fetchFirstPage();
                        }
                    });

            snackbar.show();
        } else {
            fetchFirstPage();
        }

    }


    private void prepareData(ModelRepoResponse repositoriesList) {
        adapter = new AdapterRepo(repositoriesList.getItems());
        recyclerView.setAdapter(adapter);
    }

    private void fetchFirstPage() {
        Map<String, String> data = new HashMap<>();
        data.put("q", "tetris");
        data.put("sort", "");
        data.put("order", "desc");
        APIService apiService = new APIMaker().getService();
        Call<ModelRepoResponse>  repositoryListCall= apiService.getRepositoryList(data);
        repositoryListCall.enqueue(new Callback<ModelRepoResponse>() {
            @Override
            public void onResponse(Call<ModelRepoResponse> call, Response<ModelRepoResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this,
                            " Sucessful",
                            Toast.LENGTH_SHORT).show();
                    repositoriesList = response.body();
                    prepareData(repositoriesList);

                } else {

                    // Error Handling, out of project scope

                }
            }

            @Override
            public void onFailure(Call<ModelRepoResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this,
                        "Request failed. Check your internet connection",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchNextPage() {
        Log.d(TAG, "loadNextPage: " + currentPage);
        Map<String, String> data = new HashMap<>();
        data.put("q", "tetris");
        data.put("sort", "");
        data.put("order", "desc");
        data.put("page", String.valueOf(currentPage));
        APIService apiService = new APIMaker().getService();
        Call<ModelRepoResponse>  repositoryListCall= apiService.getRepositoryList(data);
        repositoryListCall.enqueue(new Callback<ModelRepoResponse>() {
            @Override
            public void onResponse(Call<ModelRepoResponse> call, Response<ModelRepoResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this,
                            " Loading more  ",
                            Toast.LENGTH_SHORT).show();
                    ModelRepoResponse repositoriesList2 = response.body();
                    repositoriesList.getItems().addAll(repositoriesList2.getItems());
                    Log.d("new size ",repositoriesList.getItems().size()+"");
                    adapter.notifyDataSetChanged();


                } else {

                    // Error Handling, out of project scope

                }
            }

            @Override
            public void onFailure(Call<ModelRepoResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this,
                        "Request failed. Check your internet connection",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public void onResume() {
        super.onResume();
        recyclerView.setAdapter(adapter);
    }

}

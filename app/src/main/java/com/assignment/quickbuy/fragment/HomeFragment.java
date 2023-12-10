package com.assignment.quickbuy.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.assignment.quickbuy.Model.Products;
import com.assignment.quickbuy.ProductDetailsActivity;
import com.assignment.quickbuy.R;
import com.assignment.quickbuy.ViewHolder.ProductViewHolder;
import com.assignment.quickbuy.authentication.LoginActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.assignment.quickbuy.MainActivity;

import java.util.Objects;

public class HomeFragment extends Fragment {

    private DatabaseReference ProductsRef;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SearchView searchView = null;
    private String searchQuery = "";
    private Dialog loadingDialog;
    private boolean isSearchOpen = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((MainActivity) requireActivity()).setFabVisibility(View.VISIBLE);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            return networkCapabilities != null &&
                    (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        }
        return false;
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            SearchView searchView = (SearchView) searchItem.getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));

            searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    isSearchOpen = true;
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    isSearchOpen = false;
                    updateRecyclerView();
                    return true;
                }
            });

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchQuery = query;
                    filterRecyclerView(query);
                    return false;
                }
            });
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void updateRecyclerView() {
        if (!isSearchOpen) {
            FirebaseRecyclerOptions<Products> options =
                    new FirebaseRecyclerOptions.Builder<Products>()
                            .setQuery(ProductsRef, Products.class)
                            .build();

            FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                    new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {
                            holder.txtProductName.setText(model.getPname());
                            holder.txtProductPrice.setText("$" + model.getPrice());
                            Picasso.get().load(model.getimage()).into(holder.imageView);

                            holder.itemView.setOnClickListener(v -> {
                                Intent intent = new Intent(requireContext(), ProductDetailsActivity.class);
                                intent.putExtra("pid", model.getPid());
                                startActivity(intent);
                            });
                        }

                        @NonNull
                        @Override
                        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                            return new ProductViewHolder(itemView);
                        }

                        @Override
                        public void onDataChanged() {
                            if (loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
                        }
                    };

            recyclerView.setAdapter(adapter);
            adapter.startListening();
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_profile) {
            Toast.makeText(requireContext(), "Profile", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.menu_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_search) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void filterRecyclerView(String query) {
        if (!query.isEmpty()) {
            FirebaseRecyclerOptions<Products> options =
                    new FirebaseRecyclerOptions.Builder<Products>()
                            .setQuery(ProductsRef.orderByChild("pname")
                                    .startAt(query.toLowerCase()).endAt(query.toLowerCase() + "\uf8ff"), Products.class)
                            .build();

            FirebaseRecyclerAdapter<Products, ProductViewHolder> filteredAdapter =
                    new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {
                            holder.txtProductName.setText(model.getPname());
                            holder.txtProductPrice.setText("$" + model.getPrice());
                            Picasso.get().load(model.getimage()).into(holder.imageView);

                            holder.itemView.setOnClickListener(v -> {
                                Intent intent = new Intent(requireContext(), ProductDetailsActivity.class);
                                intent.putExtra("pid", model.getPid());
                                startActivity(intent);
                            });
                        }

                        @NonNull
                        @Override
                        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                            return new ProductViewHolder(itemView);
                        }

                        @Override
                        public void onDataChanged() {
                            if (loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
                        }
                    };

            recyclerView.setAdapter(filteredAdapter);
            filteredAdapter.startListening();
        } else {
            FirebaseRecyclerOptions<Products> options =
                    new FirebaseRecyclerOptions.Builder<Products>()
                            .setQuery(ProductsRef, Products.class)
                            .build();

            FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                    new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {
                            holder.txtProductName.setText(model.getPname());
                            holder.txtProductPrice.setText("$" + model.getPrice());
                            Picasso.get().load(model.getimage()).into(holder.imageView);

                            holder.itemView.setOnClickListener(v -> {
                                Intent intent = new Intent(requireContext(), ProductDetailsActivity.class);
                                intent.putExtra("pid", model.getPid());
                                startActivity(intent);
                            });
                        }

                        @NonNull
                        @Override
                        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                            return new ProductViewHolder(itemView);
                        }

                        @Override
                        public void onDataChanged() {
                            if (loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
                        }
                    };

            recyclerView.setAdapter(adapter);
            adapter.startListening();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        recyclerView = view.findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(requireContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.grid_layout_spacing);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        ((MainActivity) requireActivity()).setFabVisibility(View.VISIBLE);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadingDialog = new Dialog(requireContext());
        loadingDialog.setContentView(R.layout.loading_dialog);
        loadingDialog.setCancelable(false);
        Objects.requireNonNull(loadingDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadingDialog.show();
        if (!isNetworkAvailable()) {
            if (loadingDialog != null) {
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
            }
        }

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(ProductsRef, Products.class)
                        .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @SuppressLint("SetTextI18n")
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {
                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductPrice.setText("$" + model.getPrice());
                        Picasso.get().load(model.getimage()).into(holder.imageView);

                        holder.itemView.setOnClickListener(v -> {
                            Intent intent = new Intent(requireContext(), ProductDetailsActivity.class);
                            intent.putExtra("pid", model.getPid());
                            startActivity(intent);
                        });
                        if (model.getPname().toLowerCase().contains(searchQuery.toLowerCase())) {
                            holder.itemView.setVisibility(View.VISIBLE);
                            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        } else {
                            holder.itemView.setVisibility(View.GONE);
                            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                        }
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                        return new ProductViewHolder(itemView);
                    }

                    @Override
                    public void onDataChanged() {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    private static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private final int spacing;

        SpacesItemDecoration(int spacing) {
            this.spacing = spacing;
        }

        @Override
        public void getItemOffsets(Rect outRect, @NonNull View view, RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.left = spacing;
            outRect.right = spacing;
            outRect.bottom = spacing;

            if (parent.getChildAdapterPosition(view) < 2) {
                outRect.top = spacing;
            } else {
                outRect.top = 0;
            }
        }
    }
}

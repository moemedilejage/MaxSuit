package com.imegga.suitcase;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private GridAdapter adapter;
    List<Item> itemList = new ArrayList<>();
    String itemId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.items_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new GridAdapter();
        recyclerView.setAdapter(adapter);

        // Read data from Firebase database
        FirebaseDatabase.getInstance().getReference("items").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Item cardItem = snapshot.getValue(Item.class);
                    itemId = snapshot.getKey();
                    itemList.add(cardItem);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    private class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView itemName;
            private final TextView itemPrice;
            private final ImageView itemImage;
            //private final ImageButton deleteItemButton;

            ViewHolder(View itemView) {
                super(itemView);
                itemName = itemView.findViewById(R.id.textViewName);
                itemPrice = itemView.findViewById(R.id.textViewPrice);
                itemImage = itemView.findViewById(R.id.imageViewItemImage);

                //deleteItemButton = itemView.findViewById(R.id.image_button_delete);
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Item cardItem = itemList.get(position);
            holder.itemName.setText(cardItem.getName());
            holder.itemPrice.setText(cardItem.getPrice());
            Picasso.get().load(cardItem.getImageUrl())
                    .placeholder(R.drawable.rounded_corner)
                    .into(holder.itemImage);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), ViewItemActivity.class);

                intent.putExtra("id", itemId);
                intent.putExtra("name", cardItem.getName());
                intent.putExtra("description", cardItem.getDescription());
                intent.putExtra("price", cardItem.getPrice());
                intent.putExtra("purchased", cardItem.isPurchased());
                intent.putExtra("imageUrl", cardItem.getImageUrl());
                intent.putExtra("latTag", cardItem.getLatTag());
                intent.putExtra("lonTag", cardItem.getLonTag());
                startActivity(intent);
            });
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }
    }


}
package com.imegga.suitcase;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.imegga.suitcase.databinding.ActivityMainBinding;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class AddFragment extends Fragment {
    ActivityMainBinding binding;
    Fragment fragment;

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextItemName;
    private EditText editTextItemDescription;
    private EditText editTextItemPrice;
    private Button buttonUploadImage;
    private Button buttonTagItem;
    private Button buttonSaveItem;

    private DatabaseReference databaseReference;
    private StorageReference storageReference, imageReference;

    private Bitmap selectedImageBitmap;

    private String itemName, itemDescription, itemPrice, imageName;
    private double itemLat, itemLon;
    private boolean isPurchased;
    private boolean isTagged;
    private UploadTask uploadTask;

    byte[] data;

    public AddFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        fragment = new AddFragment();

        databaseReference = FirebaseDatabase.getInstance().getReference("items");
        storageReference = FirebaseStorage.getInstance().getReference("item_images");

        editTextItemName = view.findViewById(R.id.editTextItemName);
        editTextItemDescription = view.findViewById(R.id.editTextItemDescription);
        editTextItemPrice = view.findViewById(R.id.editTextItemPrice);
        buttonUploadImage = view.findViewById(R.id.buttonUploadItemImage);
        buttonTagItem = view.findViewById(R.id.buttonTagItem);
        buttonSaveItem = view.findViewById(R.id.button_add_item);

        // Set click listener for the Upload Image button
        buttonUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add logic for uploading the image to Firebase Storage
                galleryLauncher.launch("image/*");
            }
        });

        buttonTagItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapActivity.class);
                mapActivityResultLauncher.launch(intent);
            }
        });

        buttonSaveItem.setOnClickListener(v -> {
            itemName = editTextItemName.getText().toString();
            itemDescription = editTextItemDescription.getText().toString();
            itemPrice = editTextItemPrice.getText().toString();
            // Generate a random name for the image file
            imageName = UUID.randomUUID().toString();

            // Reference to store the image in Firebase Storage
            imageReference = storageReference.child(imageName);

            // Upload the compressed image data to Firebase Storage
            uploadTask = imageReference.putBytes(data);
            // Upload the image to Firebase Storage
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // Get the image download URL
                imageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Create a new Item object with the image URL
                    isPurchased = false;
                    if(itemLon != 0 || itemLat != 0){
                        isTagged = true;
                    }
                    Item item = new Item(itemName, itemDescription, itemPrice, uri.toString(), isPurchased, isTagged, itemLat, itemLon);

                    // Push the item to the Firebase database
                    String key = databaseReference.push().getKey();
                    databaseReference.child(key).setValue(item);
                });
            });
        });
        return view;
    }

    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
        if (uri != null) {
            try {
                if (Build.VERSION.SDK_INT < 28) {
                    selectedImageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
                } else {
                    ImageDecoder.Source source = ImageDecoder.createSource(requireActivity().getContentResolver(), uri);
                    selectedImageBitmap = ImageDecoder.decodeBitmap(source);
                }
                // Compress the image here before storing it to Firebase
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                data = baos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    });

    private final ActivityResultLauncher<Intent> mapActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        itemLat = data.getDoubleExtra("latitude", 0.0);
                        itemLon = data.getDoubleExtra("longitude", 0.0);
                    }
                }
            }
    );
}
package com.imegga.suitcase;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class ViewItemActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_CONTACT_REQUEST = 1;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;

    FloatingActionButton markItemAsPurchasedFAB;
    FloatingActionButton deleteItemFAB;
    FloatingActionButton shareItemFAB;
    FloatingActionButton editItemFAB;
    FloatingActionButton tagItemFAB;
    FloatingActionButton openGalleryFAB;

    TextInputEditText itemName;
    TextInputEditText itemDescription;
    TextInputEditText itemPrice;

    ImageView itemImage;

    Button saveChangesButton;
    Button sendTextButton;

    private boolean editIsGray = true;
    private boolean isDataChanged = false;

    Item item;

    private Uri imageUri;
    byte[] imageData;
    private String id;
    boolean isPurchased;
    boolean isTagged;

    String name;
    String description;
    String imageUrl;
    String price;

    double latitude, longitude;
    private double itemLatitude, itemLongitude;

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        markItemAsPurchasedFAB = findViewById(R.id.mark_item_as_purchased);
        deleteItemFAB = findViewById(R.id.delete_item_button);
        shareItemFAB = findViewById(R.id.share_item_button);
        editItemFAB = findViewById(R.id.edit_item_button);
        tagItemFAB = findViewById(R.id.tag_item_button);
        openGalleryFAB = findViewById(R.id.open_gallery_button);

        itemName = findViewById(R.id.name_field);
        itemDescription = findViewById(R.id.description_field);
        itemPrice = findViewById(R.id.price_field);

        View view = getLayoutInflater().inflate(R.layout.activity_delegation_popup, null);

        itemImage = findViewById(R.id.item_image);

        saveChangesButton = findViewById(R.id.button_save_changes);
        sendTextButton = view.findViewById(R.id.send_button);
        saveChangesButton.setEnabled(false);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        name = intent.getStringExtra("name");
        description = intent.getStringExtra("description");
        price = intent.getStringExtra("price");
        isPurchased = intent.getBooleanExtra("purchased", false);
        imageUrl = intent.getStringExtra("imageUrl");

        databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference itemDataReference = databaseReference.child("items").child(id);
        latitude = intent.getDoubleExtra("latTag", 0);
        longitude = intent.getDoubleExtra("lonTag", 0);

        itemName.setText(name);
        itemDescription.setText(description);
        itemPrice.setText(price);
        Picasso.get().load(imageUrl)
                .placeholder(R.drawable.rounded_corner)
                .into(itemImage);

        itemName.addTextChangedListener(textWatcher());
        itemDescription.addTextChangedListener(textWatcher());
        itemPrice.addTextChangedListener(textWatcher());

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("items").child(id);

        if(isPurchased){
            markItemAsPurchasedFAB.setColorFilter(getResources().getColor(R.color.colorPrimary));
        }
        if(!isPurchased){
            markItemAsPurchasedFAB.setColorFilter(getResources().getColor(R.color.colorGray));
        }

        markItemAsPurchasedFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPurchased) {
                    markItemAsPurchasedFAB.setColorFilter(getResources().getColor(R.color.colorPrimary));
                    databaseReference.child("purchased").setValue(true);
                }
                if (!isPurchased) {
                    markItemAsPurchasedFAB.setColorFilter(getResources().getColor(R.color.colorGray));
                    databaseReference.child("purchased").setValue(false);
                }
                isPurchased = !isPurchased;
            }
        });

        deleteItemFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.removeValue();
                Intent intent = new Intent(ViewItemActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        shareItemFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewItemActivity.this, DelegationActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("description", description);
                intent.putExtra("price", price);
                intent.putExtra("imageUrl", imageUrl);
                startActivity(intent);
            }
        });

        editItemFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editIsGray) {
                    editItemFAB.setColorFilter(getResources().getColor(R.color.colorPrimary));
                    enableEdits(true);
                }else{
                    editItemFAB.setColorFilter(getResources().getColor(R.color.colorGray));
                    enableEdits(false);
                }
                editIsGray = !editIsGray;
            }
        });

        tagItemFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewItemActivity.this, EditMapActivity.class);

                intent.putExtra("id", id);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);

                startActivity(intent);
            }
        });

        openGalleryFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedName = itemName.getText().toString();
                String updatedDescription = itemDescription.getText().toString();
                String updatedPrice = itemPrice.getText().toString();

                databaseReference.child("name").setValue(updatedName);
                databaseReference.child("description").setValue(updatedDescription);
                databaseReference.child("price").setValue(updatedPrice);
            }
        });
    }

    private final ActivityResultLauncher<Intent> mapActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        latitude = data.getDoubleExtra("latitude", 0.0);
                        longitude = data.getDoubleExtra("longitude", 0.0);

                        isTagged = data.getBooleanExtra("isTagged", false);
                    }
                }
            }
    );

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            compressAndUploadImage(imageUri);
            itemImage.setImageURI(imageUri);
        }
    }

    public void enableEdits(boolean isEnabled){
        itemName.setFocusableInTouchMode(isEnabled);
        itemName.setClickable(isEnabled);
        itemName.setCursorVisible(isEnabled);
        itemName.setFocusable(isEnabled);

        itemDescription.setFocusableInTouchMode(isEnabled);
        itemDescription.setClickable(isEnabled);
        itemDescription.setCursorVisible(isEnabled);
        itemDescription.setFocusable(isEnabled);

        itemPrice.setFocusableInTouchMode(isEnabled);
        itemPrice.setClickable(isEnabled);
        itemPrice.setCursorVisible(isEnabled);
        itemPrice.setFocusable(isEnabled);
    }

    private TextWatcher textWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Set the flag indicating that data has changed
                isDataChanged = true;
                // Enable the button
                saveChangesButton.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not used
            }
        };
    }

    private void compressAndUploadImage(Uri imageUri) {
        try {
            // Open an input stream from the image URI
            InputStream imageStream = getContentResolver().openInputStream(imageUri);

            // Decode the input stream into a Bitmap
            Bitmap originalBitmap = BitmapFactory.decodeStream(imageStream);

            // Close the input stream
            imageStream.close();

            // Compress the bitmap to a ByteArrayOutputStream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            originalBitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos); // Adjust quality as needed
            imageData = baos.toByteArray();

            // Upload the compressed image to Firebase Storage
            uploadCompressedImage(imageData);

        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception
            Log.e("Firebase", "Error compressing image: " + e.getMessage());
        }
    }

    private void uploadCompressedImage(byte[] imageData) {
        // Upload the compressed image to Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("images/" + UUID.randomUUID().toString());

        imageRef.putBytes(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Image upload successful, get the download URL
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUrl) {
                        // Now, you can use the downloadUrl to update the database or display the image
                        String imageUrl = downloadUrl.toString();
                        // Update the database with the new image URL
                        updateDatabaseWithNewImage(imageUrl);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                        // Handle the error
                Log.e("Firebase", "Image upload failed: " + e.getMessage());
            }
        });
    }

    private void updateDatabaseWithNewImage(String imageUrl) {
        databaseReference.child("imageUrl").setValue(imageUrl);
        // Update other fields as needed

        // Optionally, add a completion listener to handle success or failure
        databaseReference.child("imageUrl").setValue(imageUrl, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    // Handle the error
                    Log.e("Firebase", "Database update failed: " + databaseError.getMessage());
                } else {
                    // Database update successful
                    Log.d("Firebase", "Database update successful");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear the back stack
        startActivity(intent);
    }

    private void updateButtonState(boolean newState) {
        // Update the button state in the database
        databaseReference.setValue(newState);
    }
}
package com.imegga.suitcase;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

public class CardViewLogic extends CardView {
    private ImageButton purchasedItemButton;
    private boolean isSelected = false;

    public CardViewLogic(@NonNull Context context) {
        super(context);
        init();
    }

    public CardViewLogic(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CardViewLogic(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //Inflate ImageButton inside card
        inflate(getContext(), R.layout.item_card, this);

        //Find ImageButton in card
        /*purchasedItemButton = findViewById(R.id.);

        //Set onClickListener for the ImageButton
        purchasedItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelected = !isSelected;
                if (isSelected) {
                    // Change the image to the selected state
                    purchasedItemButton.setImageResource(R.drawable.icon_purchased_blue);
                } else {
                    // Change the image to the default state
                    purchasedItemButton.setImageResource(R.drawable.icon_purchased);
                }
            }
        });*/

    }
}

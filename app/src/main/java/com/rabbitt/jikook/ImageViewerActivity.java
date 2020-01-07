package com.rabbitt.jikook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;

public class ImageViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        Toolbar toolbar = findViewById(R.id.tool);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow));

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        ImageView imageView = findViewById(R.id.image_);

        String url = intent.getStringExtra("image_url");
        String name = intent.getStringExtra("user_name");
        Glide.with(this)
                .load(url)
                .into(imageView);

        this.setTitle(name);
    }
}

package com.example.owner.sharephototest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.GridView;

import com.example.owner.sharephototest.R;
import com.example.owner.sharephototest.adapter.GalleryAdapter;
import com.example.owner.sharephototest.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private List<String> mGalleryPicturePaths = new ArrayList<>();
    private List<String> mSelectedPicturePaths = new ArrayList<>();
    private ArrayList<String> mSelectedPictures = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_picture_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        GridView gridView = (GridView) findViewById(R.id.gallery_image_grid_view);

        mGalleryPicturePaths = ImageUtil.getMediaImage(this);
        GalleryAdapter myGalleryAdapter = new GalleryAdapter(this, mGalleryPicturePaths, mSelectedPicturePaths);
        gridView.setAdapter(myGalleryAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gallery_action_bar, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

            case R.id.action_next:
                // @임시 뒤로 가기 버튼으로 다시 수행될 때
                if (mSelectedPictures != null) {
                    mSelectedPictures.clear();
                }

                for (int i = 0; i < mSelectedPicturePaths.size(); i++) {
                    mSelectedPictures.add(mSelectedPicturePaths.get(i));
                }

                //** 임시 인텐트 위치
                Intent intent = new Intent(getApplicationContext(), PageEditorActivity.class);
                intent.putExtra("selectedImage", mSelectedPictures);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
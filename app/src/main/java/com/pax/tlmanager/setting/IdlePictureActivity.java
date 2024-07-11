package com.pax.tlmanager.setting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.pax.poslink.peripheries.MiscSettings;
import com.pax.tlmanager.ParameterManager;
import com.pax.tlmanager.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class IdlePictureActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView img1;
    private ImageView img2;
    private ImageView img3;
    private EditText etInterval;
    private Button btnOk;
    private ImageButton imgBack;
    private TextView tvReset;
    private int currentIndex = 1;
    private String pic1 = "";
    private String pic2 = "";
    private String pic3 = "";
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idle_picture);
        activityResultLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            MiscSettings.setHomeKeyEnable(this, true);
                            MiscSettings.setRecentKeyEnable(this, true);
                            MiscSettings.setNavigationBarVisible(this, false);
                            Intent intent = result.getData();
                            if (intent != null) {
                                Uri uri = intent.getData();
                                copyFileToPath(uri);
                            }
                        });
        img1 = findViewById(R.id.img_pic1);
        img2 = findViewById(R.id.img_pic2);
        img3 = findViewById(R.id.img_pic3);
        etInterval = findViewById(R.id.et_interval);
        btnOk = findViewById(R.id.btn_ok);
        imgBack = findViewById(R.id.img_back);
        tvReset = findViewById(R.id.tv_reset);
        img1.setOnClickListener(this);
        img2.setOnClickListener(this);
        img3.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        tvReset.setOnClickListener(this);
        ParameterManager manager = ParameterManager.getInstance(this);
        String imgPath1 = manager.getImgPath1();
        String imgPath2 = manager.getImgPath2();
        String imgPath3 = manager.getImgPath3();
        if (isNonEmpty(imgPath1)) {
            Glide.with(this).load(manager.getInternalPath() + imgPath1)
                    .skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(img1);
        }
        if (isNonEmpty(imgPath2)) {
            Glide.with(this).load(manager.getInternalPath() + imgPath2)
                    .skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(img2);
        }
        if (isNonEmpty(imgPath3)) {
            Glide.with(this).load(manager.getInternalPath() + imgPath3)
                    .skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(img3);
        }
        etInterval.setText(String.valueOf(manager.getBannerInterval() / 1000));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_pic1:
                getPicture(1);
                break;
            case R.id.img_pic2:
                getPicture(2);
                break;
            case R.id.img_pic3:
                getPicture(3);
                break;
            case R.id.btn_ok:
                ParameterManager manager = ParameterManager.getInstance(this);
                if (isNonEmpty(pic1)) {
                    manager.setImgPath1(pic1);
                }
                if (isNonEmpty(pic2)) {
                    manager.setImgPath2(pic2);
                }
                if (isNonEmpty(pic3)) {
                    manager.setImgPath3(pic3);
                }
                int interval = manager.getBannerInterval() / 1000;
                try {
                    interval = Integer.parseInt(etInterval.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (interval == 0) {
                    Toast.makeText(this, "Interval should in range [1,999]!", Toast.LENGTH_SHORT).show();
                } else {
                    manager.setBannerInterval(interval);
                    Toast.makeText(this, "Set idle pictures success!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case R.id.img_back:
                finish();
                break;
            case R.id.tv_reset:
                new AlertDialog.Builder(this).setTitle("Reset")
                        .setMessage("The Idle screen picture will be set to default, continue?")
                        .setPositiveButton("Confirm", (dialog, which) -> {
                            dialog.dismiss();
                            ParameterManager.getInstance(IdlePictureActivity.this).deleteAllPic();
                            Toast.makeText(IdlePictureActivity.this, "Reset idle picture success!", Toast.LENGTH_SHORT).show();
                            finish();
                        }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).show();
                break;
            default:
                break;
        }
    }

    private void getPicture(int picIdx) {
        currentIndex = picIdx;
        XXPermissions.with(this)
                .permission(Permission.WRITE_EXTERNAL_STORAGE)
                .permission(Permission.READ_EXTERNAL_STORAGE)
                .request((permissions, all) -> {
                    MiscSettings.setNavigationBarVisible(this, true);
                    MiscSettings.setHomeKeyEnable(this, false);
                    MiscSettings.setRecentKeyEnable(this, false);
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    activityResultLauncher.launch(intent);
                });
    }

    private void copyFileToPath(Uri uri) {
        boolean needReplace;
        String fileType = "";
        String fp = PickUtils.getPath(this, uri);
        if (fp != null) {
            File file = new File(fp);
            if (file.getName().toLowerCase().endsWith(".png")) {
                needReplace = true;
                fileType = ".png";
            } else if (file.getName().toLowerCase().endsWith(".jpg")) {
                needReplace = true;
                fileType = ".jpg";
            } else {
                needReplace = false;
            }
            File descPath = new File(ParameterManager.getInstance(this).getInternalPath());
            if (!descPath.exists()) {
                descPath.mkdirs();
            }
            if (needReplace) {
                String fileName = "img" + currentIndex + fileType;
                try (FileChannel inputChannel = new FileInputStream(file).getChannel();
                     FileChannel outputChannel = new FileOutputStream(new File(ParameterManager.getInstance(this).getInternalPath() + fileName)).getChannel()) {
                    outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
                    switch (currentIndex) {
                        case 1:
                            Glide.with(this).load(file).into(img1);
                            pic1 = fileName;
                            break;
                        case 2:
                            Glide.with(this).load(file).into(img2);
                            pic2 = fileName;
                            break;
                        case 3:
                            Glide.with(this).load(file).into(img3);
                            pic3 = fileName;
                            break;
                        default:
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private boolean isNonEmpty(String s) {
        return s != null && s.length() > 0;
    }

    private boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }
}

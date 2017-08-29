package cn.longmaster.ihuvc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import cn.longmaster.doctorlibrary.viewinject.FindViewById;
import cn.longmaster.doctorlibrary.viewinject.ViewInjecter;
import cn.longmaster.ihuvc.R;
import cn.longmaster.ihuvc.core.AppApplication;
import cn.longmaster.ihuvc.core.AppConfig;
import cn.longmaster.ihuvc.core.manager.VersionManager;

public class MainActivity extends BaseActivity {
    @FindViewById(R.id.action_main_version)
    private TextView mVersionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewInjecter.inject(this);
        iniView();
        AppApplication.HANDLER.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, CheckInActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }

    private void iniView() {
        mVersionName.setText(getString(R.string.version_number, VersionManager.getInstance().getFullVersionName("", AppConfig.CLIENT_VERSION)));
    }
}

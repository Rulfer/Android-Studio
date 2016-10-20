package makingview.com.apkdownloader;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_layout);
    }

    public void installMovieMenu(View view)
    {
        Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                .setDataAndType(Uri.parse("file:///sdcard/Download/MovieMenu.apk"),
                        "application/vnd.android.package-archive");
        startActivity((promptInstall));
    }

    public void downloadPano(View view)
    {
        String downloadPath;
    }

    public void downloadMenu(View view)
    {
        String downloadPath;
    }

}

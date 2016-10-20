package button.com.buttons;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class FirstActivity extends AppCompatActivity {
    TextView textView;
    Boolean clicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_layout);

        textView = (TextView) findViewById(R.id.greetings_text_view);
        clicked = false;
    }

    public void downloadPano(View view)
    {
        String downloadPath;
        Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                .setDataAndType(Uri.parse("file:///sdcard/Download/MovieMenu.apk"),
                        "application/vnd.android.package-archive");
        startActivity((promptInstall));
    }

    public void downloadMenu(View view)
    {
        String downloadPath;
    }


}

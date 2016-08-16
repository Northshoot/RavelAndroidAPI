package ai.harmony.ravel.canvas;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.widget.TextView;

import ai.harmony.ravel.R;

/**
 * ██╗  ██╗ █████╗ ██████╗ ███╗   ███╗ ██████╗ ███╗   ██╗██╗   ██╗ █████╗ ██╗
 * ██║  ██║██╔══██╗██╔══██╗████╗ ████║██╔═══██╗████╗  ██║╚██╗ ██╔╝██╔══██╗██║
 * ███████║███████║██████╔╝██╔████╔██║██║   ██║██╔██╗ ██║ ╚████╔╝ ███████║██║
 * ██╔══██║██╔══██║██╔══██╗██║╚██╔╝██║██║   ██║██║╚██╗██║  ╚██╔╝  ██╔══██║██║
 * ██║  ██║██║  ██║██║  ██║██║ ╚═╝ ██║╚██████╔╝██║ ╚████║   ██║██╗██║  ██║██║
 * ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝     ╚═╝ ╚═════╝ ╚═╝  ╚═══╝   ╚═╝╚═╝╚═╝  ╚═╝╚═╝
 * <p>
 * Copyright (C) 2016 Laurynas Riliskis
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Created by lauril on 8/11/16.
 */


public class CanvasActivity extends Activity {
    SimpleCanvas drawView;
    TextView rec_count_in;

    private final int interval = 1000; // 1 Second
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable(){
        int i=0;
        public void run() {
            drawView.add();
            i++;
            rec_count_in.setText(String.valueOf(i)+"");
            drawView.invalidate();
            handler.postDelayed(runnable, interval);
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.canvas_layout);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        drawView = (SimpleCanvas)  findViewById(R.id.model_view);
        drawView.setPoints(size);
        drawView.setBackgroundColor(Color.WHITE);
        drawView.invalidate();
        rec_count_in = (TextView) findViewById(R.id.record_count_in);
        handler.postDelayed(runnable, interval);

    }


}


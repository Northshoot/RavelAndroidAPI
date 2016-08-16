package ai.harmony.ravel.canvas;

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


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class SimpleCanvas extends View {
    Paint paint = new Paint();
    int width = 50;
    int height= 50;
    int left_start = 50;
    int top_start = 50;
    int max_packets = 40;
    int current =0;
    int off_set =20;
    boolean on, m_innit = false;
    Rect rectangle;
    Paint p = new Paint();
    Paint red = new Paint();
    Point screen_size;
    ArrayList<Rect> rect_array;


    public SimpleCanvas(Context context ) {
        super(context);
        rect_array = new ArrayList<>();

    }

    public SimpleCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        rect_array = new ArrayList<>();

    }

    public SimpleCanvas(Context context, AttributeSet attrs, int defStyle ) {
        super(context, attrs, defStyle);
        rect_array = new ArrayList<>();

    }

    @Override
    public void onDraw(Canvas canvas) {
//        if(on){
//            paint.setColor(Color.RED);
//            on = false;
//        } else {
//            paint.setColor(Color.GREEN);
//            on = true;
//        }
//        canvas.drawRect(rectangle, paint);
        if(m_innit) {
            if (current <= max_packets) {
                for (int i = 0; i < current; i++) {
                    canvas.drawRect(rect_array.get(i), p);
                }
            } else {
                canvas.drawRect(rect_array.get(0), red);
                for (int i = 1; i < max_packets; i++) {
                    canvas.drawRect(rect_array.get(i), p);
                }
            }
        } else {
            innit();
        }

    }

    void add(){
        current++;
    }
    //
    void setPoints(Point p){
        screen_size =p;
    }

    void innit(){
        int c_x = left_start;
        int c_y = top_start;
        m_innit = true;
        p.setColor(Color.BLUE);
        red.setColor(Color.RED);
        rectangle = new Rect();
        rectangle.set(left_start, top_start, left_start + width, top_start + height);
        //how many we fit per row
        int max_per_row = (screen_size.x-left_start) / (width+off_set);
        System.out.println("Width: " + screen_size.x+" Max per row: " + max_per_row);
        //how many rows we need?
        int rows = max_packets / max_per_row;
        System.out.println("Row: " + rows);
        for(int i = 0; i<rows+1; i++){
            for(int j=0; j < max_per_row; j++) {
                Rect rectangle = new Rect();
                rectangle.set(c_x, c_y, c_x + width, c_y + height);
                c_x += width + off_set;
                rect_array.add(rectangle);
            }
            c_x = left_start;
            c_y+=height+off_set;

        }
    }



}



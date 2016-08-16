package ai.harmony.app;

import android.util.Log;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;

import ai.harmony.ravel.utils.ByteWork;

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
 * Created by lauril on 8/15/16.
 */

public class RandomModel {
    private static final String TAG = "RandomModel";
    private Queue<RandomRecord> queue;
    private int size=50;
    private int counter = 0;

    public RandomModel(){
        queue = new LinkedList<>();
    }

    public void add(String time, String random){
        RandomRecord rr;
        if (is_full()){
            rr = queue.remove();
            Log.d(TAG, "Removed record: " + rr);

        }
        counter++;
        rr = new RandomRecord(time, random, counter);
        queue.add( rr );
        Log.d(TAG, "Added record: " + rr);
    }

    public RandomRecord getRecord(){
        return queue.poll();
    }

    private boolean is_full(){
        return queue.size() == size;
    }

    public void add(byte[] data) {
        long counter = ByteWork.convertFourUnsignedBytesToLong(ByteWork.getBytes(data,0,3));
        int time = ByteWork.convertFourBytesToInt(ByteWork.getBytes(data,4,7));
        float temperature = ByteWork.convertFourBytesToInt(ByteWork.getBytes(data,8,11))/4;
        int random =ByteWork.convertFourBytesToInt(ByteWork.getBytes(data,12,15));
        Log.d(TAG, "EMB counter: " + counter + " time " + time + " temperature " + temperature);
        add(java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()),
                String.valueOf(random));
    }


    public class RandomRecord {
        String time;
        String random;
        int local_num=0;

        public RandomRecord(String time, String random, int num){
            this.time = time;
            this.random = random;
            this.local_num = num;
        }

        @Override
        public String toString() {
            return "Record #: " + local_num + " Arrived: " + time + " value: " + random;
        }
    }
}

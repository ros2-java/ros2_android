/* Copyright 2017 Esteve Fernandez <esteve@apache.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ros2.android.examples;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import org.ros2.android.sensors.ImuNode;
import org.ros2.android.activity.ROSActivity;

public class ROS2SensorsActivity extends ROSActivity {

  private ImuNode imuNode;

  private static String logtag = ROS2SensorsActivity.class.getName();

  private static String IMU_NODE_NAME = "imu_node";

  /** Called when the activity is first created. */
  @Override
  public final void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    Button buttonStart = (Button) findViewById(R.id.buttonStart);
    buttonStart.setOnClickListener(startListener);

    Button buttonStop = (Button) findViewById(R.id.buttonStop);
    buttonStop.setOnClickListener(stopListener);
    buttonStop.setEnabled(false);

    SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    imuNode = new ImuNode(sensorManager, IMU_NODE_NAME);
    getExecutor().addNode(this.imuNode);
  }

  // Create an anonymous implementation of OnClickListener
  private OnClickListener startListener = new OnClickListener() {
    public void onClick(final View view) {
      Log.d(logtag, "onClick() called - start button");
      Toast.makeText(ROS2SensorsActivity.this, "The Start button was clicked.", Toast.LENGTH_LONG).show();
      Log.d(logtag, "onClick() ended - start button");
      Button buttonStart = (Button) findViewById(R.id.buttonStart);
      Button buttonStop = (Button) findViewById(R.id.buttonStop);
      buttonStart.setEnabled(false);
      buttonStop.setEnabled(true);

      imuNode.start();
    }
  };

  // Create an anonymous implementation of OnClickListener
  private OnClickListener stopListener = new OnClickListener() {
    public void onClick(final View view) {
      Log.d(logtag, "onClick() called - stop button");
      Toast.makeText(ROS2SensorsActivity.this, "The Stop button was clicked.", Toast.LENGTH_LONG).show();

      imuNode.stop();

      Button buttonStart = (Button) findViewById(R.id.buttonStart);
      Button buttonStop = (Button) findViewById(R.id.buttonStop);
      buttonStart.setEnabled(true);
      buttonStop.setEnabled(false);
      Log.d(logtag, "onClick() ended - stop button");
    }
  };
}

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

package org.ros2.android.sensors;

import java.util.Arrays;
import java.util.Collection;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.node.AbstractComposableNode;
import org.ros2.rcljava.node.ComposableNode;
import org.ros2.rcljava.node.Node;
import org.ros2.rcljava.publisher.Publisher;

public class ImuNode implements ComposableNode, SensorEventListener {
  private final SensorManager sensorManager;
  private final String name;
  private final String topic;

  private final Sensor accelerometer;
  private Publisher<sensor_msgs.msg.Imu> publisher;
  private Node node;

  public ImuNode(final SensorManager sensorManager, final String name) {
    this(sensorManager, name, "android/imu");
  }

  public ImuNode(final SensorManager sensorManager, final String name,
                 final String topic) {
    this.name = name;
    this.sensorManager = sensorManager;
    this.topic = topic;
    this.accelerometer =
        this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    this.node = RCLJava.createNode(name);
    this.publisher = this.node.<sensor_msgs.msg.Imu>createPublisher(
        sensor_msgs.msg.Imu.class, this.topic);
  }

  public final Node getNode() { return this.node; }

  public void start() {
    this.sensorManager.registerListener(this, this.accelerometer,
                                        SensorManager.SENSOR_DELAY_NORMAL);
  }

  public void stop() { this.sensorManager.unregisterListener(this); }

  public void onAccuracyChanged(Sensor sensor, int accuracy) {}

  public void onSensorChanged(SensorEvent event) {
    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
      sensor_msgs.msg.Imu imu = new sensor_msgs.msg.Imu();
      geometry_msgs.msg.Vector3 linearAcceleration =
          new geometry_msgs.msg.Vector3();
      linearAcceleration.setX(event.values[0]);
      linearAcceleration.setY(event.values[1]);
      linearAcceleration.setZ(event.values[2]);
      imu.setLinearAcceleration(linearAcceleration);

      Collection<Double> tmpCov = Arrays.asList(
          new Double[] {0.01, 0.0, 0.0, 0.0, 0.01, 0.0, 0.0, 0.0, 0.01});
      imu.setLinearAccelerationCovariance(tmpCov);
      std_msgs.msg.Header header = new std_msgs.msg.Header();
      header.setStamp(org.ros2.rcljava.Time.now());
      header.setFrameId("android_imu");
      imu.setHeader(header);
      this.publisher.publish(imu);
    }
  }
}
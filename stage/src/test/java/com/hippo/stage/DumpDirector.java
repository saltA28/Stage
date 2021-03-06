/*
 * Copyright 2017 Hippo Seven
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

package com.hippo.stage;

/*
 * Created by Hippo on 4/29/2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class DumpDirector extends Director {

  private static final String KEY_CURRENT_SCENE_ID = "DumpDirector:current_scene_id";

  private int currentSceneId = Scene.INVALID_ID;

  @Override
  public void requestFocus() {}

  @Override
  public int requireSceneId() {
    int id;
    do {
      id = ++currentSceneId;
    } while (id == Scene.INVALID_ID);
    return id;
  }

  @Override
  boolean willDestroyActivity() {
    return false;
  }

  @Nullable
  @Override
  public Activity getActivity() {
    return null;
  }

  @Override
  void startActivity(@NonNull Intent intent) {}

  @Override
  void startActivity(@NonNull Intent intent, @Nullable Bundle options) {}

  @Override
  void startActivityForResult(Intent intent, int requestCode) {}

  @Override
  void startActivityForResult(Intent intent, int requestCode, Bundle options) {}

  @Override
  void requestPermissions(@NonNull String[] permissions, int requestCode) {}

  @Override
  void saveInstanceState(Bundle outState) {
    outState.putInt(KEY_CURRENT_SCENE_ID, currentSceneId);
    super.saveInstanceState(outState);
  }

  @Override
  void restoreInstanceState(@NonNull Bundle savedInstanceState) {
    currentSceneId = savedInstanceState.getInt(KEY_CURRENT_SCENE_ID, Scene.INVALID_ID);
    super.restoreInstanceState(savedInstanceState);
  }
}

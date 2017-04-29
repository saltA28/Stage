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
 * Created by Hippo on 4/28/2017.
 */

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

class ActivityDirector extends Director {

  private static final String FRAGMENT_TAG = "ActivityDirector";

  private static final String KEY_CURRENT_SCENE_ID = "ActivityDirector:current_scene_id";

  private int currentSceneId = Scene.INVALID_ID;

  private Activity activity;

  static ActivityDirector getInstance(
      @NonNull Activity activity, @Nullable Bundle savedInstanceState) {
    // Get DataFragment
    DataFragment fragment =
        (DataFragment) activity.getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
    if (fragment == null) {
      fragment = new DataFragment();
      activity.getFragmentManager().beginTransaction().add(fragment, FRAGMENT_TAG).commit();
    }

    // Get director
    ActivityDirector director = fragment.getDirector();
    if (director == null) {
      director = new ActivityDirector();
      director.setActivity(activity);
      if (savedInstanceState != null) {
        director.restoreInstanceState(savedInstanceState);
      }
      fragment.setDirector(director);
    } else {
      director.setActivity(activity);
    }

    return director;
  }

  private void setActivity(@Nullable Activity activity) {
    if (this.activity == null) {
      this.activity = activity;
    } else if (this.activity != activity) {
      throw new IllegalStateException("Two different activity for one ActivityDirector. "
          + "Maybe the library developer forgot to release old activity reference.");
    }
  }

  @Override
  public Activity getActivity() {
    return activity;
  }

  @Override
  public int requireSceneId() {
    int id;
    do {
      id = ++currentSceneId;
    } while (id == Scene.INVALID_ID);
    return id;
  }

  @Override
  void detach() {
    super.detach();

    if (!isFinishing()) {
      // The activity will be destroyed soon
      activity = null;
    }
  }

  @Override
  void destroy() {
    super.destroy();

    // The activity will be destroyed soon
    activity = null;
  }

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

  public static class DataFragment extends Fragment {

    private boolean isStarted;
    private boolean isResumed;
    private boolean isDestroy;

    @Nullable
    private ActivityDirector director;

    private final ActivityCallbacks activityCallbacks = new ActivityCallbacks() {
      @Override
      public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        if (activity != null && activity == getActivity()) {
          saveInstanceState(outState);
        }
      }
    };

    public DataFragment() {
      setRetainInstance(true);
      setHasOptionsMenu(true);
    }

    private void setDirector(@NonNull ActivityDirector director) {
      if (this.director == null) {
        this.director = director;

        if (isStarted) {
          director.start();
        }
        if (isResumed) {
          director.resume();
        }
      } else {
        throw new IllegalStateException("Don't hire two Director for one Activity");
      }
    }

    @Nullable
    private ActivityDirector getDirector() {
      return director;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      getActivity().getApplication().registerActivityLifecycleCallbacks(activityCallbacks);
    }

    @Override
    public void onStart() {
      super.onStart();
      isStarted = true;

      if (director != null) {
        director.start();
      }
    }

    @Override
    public void onResume() {
      super.onResume();
      isResumed = true;

      if (director != null) {
        director.resume();
      }
    }

    @Override
    public void onPause() {
      super.onPause();
      isResumed = false;

      if (director != null) {
        director.pause();
      }
    }

    @Override
    public void onStop() {
      super.onStop();
      isStarted = false;

      if (director != null) {
        director.stop();
      }
    }

    @Override
    public void onDestroy() {
      super.onDestroy();
      isDestroy = true;

      if (director != null) {
        director.destroy();
      }
    }

    @Override
    public void onDetach() {
      super.onDetach();

      if (director != null) {
        director.detach();
      }

      if (isDestroy) {
        // onDestroy() is called before onDetach(), clear data here
        getActivity().getApplication().unregisterActivityLifecycleCallbacks(activityCallbacks);
        director = null;
      }
    }

    private void saveInstanceState(Bundle outState) {
      if (director != null) {
        director.saveInstanceState(outState);
      }
    }
  }
}
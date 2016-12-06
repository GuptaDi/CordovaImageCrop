package com.piyush.crop;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import com.soundcloud.android.crop.Crop;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class ImageCropPlugin extends CordovaPlugin {
    private CallbackContext callbackContext;
    private Uri inputUri;
    private Uri outputUri;

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
      if (action.equals("cropImage")) {
          String imagePath = args.getString(0);

          this.inputUri = Uri.parse(imagePath);
          this.outputUri = Uri.fromFile(new File(getTempDirectoryPath() + "/" + System.currentTimeMillis()+ "-cropped.jpg"));

          PluginResult pr = new PluginResult(PluginResult.Status.NO_RESULT);
          pr.setKeepCallback(true);
          callbackContext.sendPluginResult(pr);
          this.callbackContext = callbackContext;

          cordova.setActivityResultCallback(this);

           
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }
          // Crop.of(this.inputUri, this.outputUri)
          //         .asSquare()
          //         .start(cordova.getActivity());

          return true;
      }
      return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                ((ImageView) findViewById(R.id.quick_start_cropped_image)).setImageURI(result.getUri());
                Toast.makeText(this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getTempDirectoryPath() {
        File cache = null;

        // SD Card Mounted
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cache = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/Android/data/" + cordova.getActivity().getPackageName() + "/cache/");
        }
        // Use internal storage
        else {
            cache = cordova.getActivity().getCacheDir();
        }

        // Create the cache directory if it doesn't exist
        cache.mkdirs();
        return cache.getAbsolutePath();
    }

    public Bundle onSaveInstanceState() {
        Bundle state = new Bundle();

        if (this.inputUri != null) {
            state.putString("inputUri", this.inputUri.toString());
        }

        if (this.outputUri != null) {
            state.putString("outputUri", this.outputUri.toString());
        }

        return state;
    }

    public void onRestoreStateForActivityResult(Bundle state, CallbackContext callbackContext) {

        if (state.containsKey("inputUri")) {
            this.inputUri = Uri.parse(state.getString("inputUri"));
        }

        if (state.containsKey("outputUri")) {
            this.inputUri = Uri.parse(state.getString("outputUri"));
        }

        this.callbackContext = callbackContext;
    }
}

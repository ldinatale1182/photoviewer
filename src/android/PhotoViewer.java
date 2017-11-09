package com.sarriaroman.PhotoViewer;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;
/**
 * Class to Open PhotoViewer with the Required Parameters from Cordova
 *
 * - URL
 * - Title
 */
public class PhotoViewer extends CordovaPlugin {

    public static final int PERMISSION_DENIED_ERROR = 20;

    public static final String WRITE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String READ = Manifest.permission.READ_EXTERNAL_STORAGE;

    public static final int REQ_CODE = 0;

    protected JSONArray args;
    protected CallbackContext callbackContext;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
      Toast.makeText(getActivity(), "Toast...", Toast.LENGTH_LONG).show();
        if (action.equals("show")) {
          Toast.makeText(getActivity(), "Opening...", Toast.LENGTH_LONG).show();
            this.args = args;
            this.callbackContext = callbackContext;

            boolean requiresExternalPermission = true;
            try {
                JSONObject options = this.args.optJSONObject(2);
                requiresExternalPermission = options.getBoolean("share");
            } catch(JSONException exception) { }

            if (!requiresExternalPermission || (cordova.hasPermission(READ) && cordova.hasPermission(WRITE))) {
                this.launchActivity();
            } else {
                this.getPermission();
            }
            return true;
        }
        return false;
    }

    protected void getPermission() {
      Toast.makeText(getActivity(), "Need Permission...", Toast.LENGTH_LONG).show();
        cordova.requestPermissions(this, REQ_CODE, new String[]{WRITE, READ});
    }

    protected void launchActivity() throws JSONException {
      Toast.makeText(getActivity(), "Launching...", Toast.LENGTH_LONG).show();
        Intent i = new Intent(this.cordova.getActivity(), com.sarriaroman.PhotoViewer.PhotoActivity.class);

        i.putExtra("url", this.args.getString(0));
        i.putExtra("title", this.args.getString(1));
        i.putExtra("options", this.args.optJSONObject(2).toString());

        this.cordova.getActivity().startActivity(i);
        this.callbackContext.success("");
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions,
                                          int[] grantResults) throws JSONException {
        for(int r:grantResults) {
            if(r == PackageManager.PERMISSION_DENIED) {
                this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, PERMISSION_DENIED_ERROR));
                return;
            }
        }

        switch(requestCode) {
            case REQ_CODE:
                launchActivity();
                break;
        }

    }
}

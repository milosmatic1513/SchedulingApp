package com.example.scheduleme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facetec.sdk.FaceTecCustomization;
import com.facetec.sdk.FaceTecIDScanRetryMode;
import com.facetec.sdk.FaceTecIDScanStatus;
import com.facetec.sdk.FaceTecSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import Processors.Config;
import Processors.NetworkingHelpers;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class FacetecIdScanner extends AppCompatActivity {
    ImageView imageView;
    private boolean isSuccess = false;
    private FacetecAuthentication sampleAppActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facetec_id_scanner);
        imageView = findViewById(R.id.imageView3);

    }

    public void takePicture(View view)
    {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, 0);//zero can be replaced with any action code (called requestCode)
    }

    @Override
    protected void  onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap imageBitmap;
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);

            //Facetec call

            // minMatchLevel allows Developers to specify a Match Level that they would like to target in order for success to be true in the response.
            // minMatchLevel cannot be set to 0.
            // minMatchLevel setting does not affect underlying Algorithm behavior.
            final int minMatchLevel = 3;

            //
            // Part 3: Get essential data off the FaceTecIDScanResult
            //
            JSONObject parameters = new JSONObject();
            try {
                parameters.put("externalDatabaseRefID", sampleAppActivity.getLatestExternalDatabaseRefID());
                //   parameters.put("idScan", idScanResult.getIDScanBase64());
                parameters.put("minMatchLevel", minMatchLevel);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();
                String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                parameters.put("image",imageString);

            }
            catch(JSONException e) {
                e.printStackTrace();
                Log.d("FaceTecSDKSampleApp", "Exception raised while attempting to create JSON payload for upload.");
            }

            //
            // Part 4:  Make the Networking Call to Your Servers.  Below is just example code, you are free to customize based on how your own API works.
            //
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(Config.BaseURL + "/match-3d-2d-3rdparty-idphoto")
                    .header("Content-Type", "application/json")
                    .header("X-Device-Key", Config.DeviceKeyIdentifier)
                    .build();

                    //.header("User-Agent", FaceTecSDK.createFaceTecAPIUserAgentString(idScanResult.getSessionId()))

            //
            // Part 7:  Actually send the request.
            //
            NetworkingHelpers.getApiClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException {
                    //
                    // Part 17:  In our Sample, we evaluate a boolean response and treat true as success, false as "User Needs to Retry",
                    // and handle all other non-nominal responses by cancelling out.  You may have different paradigms in your own API and are free to customize based on these.
                    //
                    String responseString = response.body().string();
                    response.body().close();
                    try {
                        JSONObject responseJSON = new JSONObject(responseString);

                        //
                        // DEVELOPER NOTE:  These properties are for demonstration purposes only so the Sample App can get information about what is happening in the processor.
                        // In the code in your own App, you can pass around signals, flags, intermediates, and results however you would like.
                        //
                        sampleAppActivity.setLatestServerResult(responseJSON);

                        boolean didSucceed = responseJSON.getBoolean("success");
                        // int fullIDStatusEnumInt = responseJSON.getInt("fullIDStatusEnumInt");
                        //int digitalIDSpoofStatusEnumInt = responseJSON.getInt("digitalIDSpoofStatusEnumInt");

                        if (didSucceed == true) {
                            // CASE:  Success!  The ID Match was performed and the User successfully matched.

                            //
                            // DEVELOPER NOTE:  These properties are for demonstration purposes only so the Sample App can get information about what is happening in the processor.
                            // In the code in your own App, you can pass around signals, flags, intermediates, and results however you would like.
                            //
                            isSuccess = true;



                        }
                        else if (didSucceed == false) {


                        }
                        else {
                            // CASE:  UNEXPECTED response from API.  Our Sample Code keys of a success boolean on the root of the JSON object --> You define your own API contracts with yourself and may choose to do something different here based on the error.

                        }
                    }
                    catch(JSONException e) {
                        // CASE:  Parsing the response into JSON failed --> You define your own API contracts with yourself and may choose to do something different here based on the error.  Solid server-side code should ensure you don't get to this case.
                        e.printStackTrace();
                        Log.d("FaceTecSDKSampleApp", "Exception raised while attempting to parse JSON result.");

                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    // CASE:  Network Request itself is erroring --> You define your own API contracts with yourself and may choose to do something different here based on the error.
                    Log.d("FaceTecSDKSampleApp", "Exception raised while attempting HTTPS call.");
                }
            });
        }




    }

}
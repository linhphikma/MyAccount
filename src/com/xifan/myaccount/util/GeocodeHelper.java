
package com.xifan.myaccount.util;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GeocodeHelper {

    private Context mContext;

    private static final String SERVER_URL = "http://api.map.baidu.com/geocoder/v2/";
    private static final String CONVERT_GEO_URL = "http://api.map.baidu.com/geoconv/v1/?";
    private static final String KEY = "7397d486232d0a41a7ac893c157ad2c6";
    private static final String AND = "&";

    public GeocodeHelper(Context context) {
        mContext = context;
    }

    private String getProvider(LocationManager locationManager) {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // 精度要求：ACCURACY_FINE(高)ACCURACY_COARSE(低)
        criteria.setAltitudeRequired(false); // 不要求海拔信息
        criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH); // 方位信息的精度要求：ACCURACY_HIGH(高)ACCURACY_LOW(低)
        criteria.setBearingRequired(true); // 要求方位信息
        criteria.setCostAllowed(true); // 是否允许付费
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM); // 对电量的要求(HIGH、MEDIUM)
        return locationManager.getBestProvider(criteria, true);
    }

    public String getGeoLocation() {
        Log.e("xifan", "Getting GeoLoaction...");
        String matchedGeo = null;
        LocationManager locationManager = (LocationManager) mContext
                .getSystemService(Context.LOCATION_SERVICE);

        String provider = getProvider(locationManager);
        Location loc = locationManager.getLastKnownLocation(provider);

        double lat = loc.getLatitude();
        double lng = loc.getLongitude();
        double[] newGeo = getConvertedGeo(lat, lng);
        lat = newGeo[0];
        lng = newGeo[1];

        // build the request url
        StringBuilder str = new StringBuilder(SERVER_URL);
        str.append("?ak=").append(KEY).append("&location=")
                .append(lat)
                .append(",")
                .append(lng)
                .append("&output=json&pois=1");
        Log.e("xifan", "lat=" + lat + " lng=" + lng);

        String json = getResponseFromURL(str.toString());

        try {
            JSONObject jsonObj = new JSONObject(json.toString());
            String status = jsonObj.getString("status");
            if (status.equals("0")) {
                JSONObject result = jsonObj.getJSONObject("result");
                JSONArray array = result.getJSONArray("pois");
                matchedGeo = array.getJSONObject(0).getString("name");
            } else {
                // failed
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return matchedGeo;
    }

    private String getResponseFromURL(String str) {
        try {
            URL url = new URL(str);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "application/json");

            connection.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "utf-8"));
            StringBuilder json = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            reader.close();
            connection.disconnect();
            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private double[] getConvertedGeo(double lat, double lng) {
        StringBuilder str = new StringBuilder();
        double[] result = new double[2];
        str.append(CONVERT_GEO_URL).append("coords=").append(lng).append(",").append(lat)
                .append("&from=1&to=5").append("&ak=").append(KEY);
        String json = getResponseFromURL(str.toString());
        try {
            JSONArray array = new JSONObject(json).getJSONArray("result");
            result[1] = array.getJSONObject(0).getDouble("x");
            result[0] = array.getJSONObject(0).getDouble("y");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

}

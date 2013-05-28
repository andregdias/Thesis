package com.opt.mobipag.data;

import android.os.Build;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Scanner;

public class WebServiceHandler {
    private static final int CONNECTION_TIMEOUT = 60000;
    private static final int DATARETRIEVAL_TIMEOUT = 60000;

    public static JSONObject RequestPUT(String serviceUrl, String param) {
        HttpURLConnection urlConnection = null;

        try {
            URL urlToRequest = new URL(serviceUrl);

            urlConnection = (HttpURLConnection) urlToRequest.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("PUT");
            urlConnection.setFixedLengthStreamingMode(param.getBytes().length);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(param);
            out.close();

            //int responseCode = connection.getResponseCode();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            return new JSONObject(getResponseText(in));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return null;
    }

    public static JSONObject RequestPOST(String serviceUrl, String param) {
        HttpURLConnection urlConnection = null;

        try {
            URL urlToRequest = new URL(serviceUrl);

            urlConnection = (HttpURLConnection) urlToRequest.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setFixedLengthStreamingMode(param.getBytes().length);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(param);
            out.close();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            return new JSONObject(getResponseText(in));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return null;
    }

    public static JSONObject RequestGET(String serviceUrl) {
        disableConnectionReuseIfNecessary();

        HttpURLConnection urlConnection = null;
        try {
            URL urlToRequest = new URL(serviceUrl);
            urlConnection = (HttpURLConnection) urlToRequest.openConnection();
            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(DATARETRIEVAL_TIMEOUT);

//            // handle issues
//            int statusCode = urlConnection.getResponseCode();
//            if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
//                // handle unauthorized (if service requires user login)
//            } else if (statusCode != HttpURLConnection.HTTP_OK) {
//                // handle any other errors, like 404, 500,..
//            }

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            return new JSONObject(getResponseText(in));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return null;
    }

    public static JSONArray RequestGETArray(String serviceUrl) {
        disableConnectionReuseIfNecessary();

        HttpURLConnection urlConnection = null;
        try {
            URL urlToRequest = new URL(serviceUrl);
            urlConnection = (HttpURLConnection) urlToRequest.openConnection();
            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(DATARETRIEVAL_TIMEOUT);

//            // handle issues
//            int statusCode = urlConnection.getResponseCode();
//            if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
//                // handle unauthorized (if service requires user login)
//            } else if (statusCode != HttpURLConnection.HTTP_OK) {
//                // handle any other errors, like 404, 500,..
//            }

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            return new JSONArray(getResponseText(in));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return null;
    }

    private static void disableConnectionReuseIfNecessary() {
        // see HttpURLConnection API doc
        if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO)
            System.setProperty("http.keepAlive", "false");
    }

    private static String getResponseText(InputStream inStream) {
        // http://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
        return new Scanner(inStream).useDelimiter("\\A").next();
    }
}

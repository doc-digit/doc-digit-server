package com.springbootdev.springcloud.stream.examples.consumer.Listener;

import com.netflix.ribbon.proxy.annotation.ClientProperties;
import com.springbootdev.springcloud.stream.examples.consumer.Funcionality.RotateClassInterface;
import com.springbootdev.springcloud.stream.examples.consumer.Model.ApiUpload;
import com.springbootdev.springcloud.stream.examples.consumer.Model.Task;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.net.ssl.*;
import java.awt.image.BufferedImage;
import java.net.HttpURLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;


@EnableBinding(Sink.class)
@PropertySource("classpath:rotating.properties")
public class rotatemicroservicelistener {
    private static final Logger logger = LoggerFactory.getLogger(rotatemicroservicelistener.class);

    private RotateClassInterface rotate;

    @Value("${urlForUpload}")
    private String urlForUpload;
    @Autowired
    public rotatemicroservicelistener(RotateClassInterface rotate) {
        this.rotate = rotate;
    }

    @StreamListener(target = Sink.INPUT)
    public void listenForTask(Task task) {
        //Logging of receive ne task to rotate
        logger.info(" received new task [" + task.getuuid().toString() + "] ");

        //Initializing image from url given in task json
        BufferedImage image;
        try {
            image = ImageIO.read(task.getUrl());
        } catch (IOException e) {
            logger.error("File under given url not found.");
            return;
        }
        //Rotating image
        BufferedImage rotatedImage = rotate.rotateImage(image);
        logger.info(String.format(" ending rotate image of task [ %s ]", task.getuuid().toString()));

        JSONObject json = new JSONObject();
        json.put("parent_id", task.getuuid());
        json.put("parent_type", "document");

        //Getting url for upload image
        OkHttpClient client = getUnsafeOkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, json.toString());
        Request request = new Request.Builder()
                .url(urlForUpload)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("cache-control", "no-cache")
                .build();
        Response response;
        String responsejson;
        try {
            response = client.newCall(request).execute();
            responsejson = response.body().string();
        } catch (IOException e) {
            logger.error("Conncetion to api error");
            return;
        } catch (NullPointerException e) {
            logger.error("Failed create request to api");
            return;
        }
        logger.info(responsejson);

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(responsejson);
        } catch (JSONException err) {
            logger.error("Json error");
            return;
        } catch (NullPointerException e) {
            logger.error("Failed create request to api");
            return;
        }

        // Create the connection and use it to upload the new object using the pre-signed URL.
        try {

            URL url = new URL(jsonObject.getString("url"));
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("PUT");
            c.setDoOutput(true);
            OutputStream output = c.getOutputStream();
            ImageIO.write(rotatedImage, "PNG", output);
            output.close();
            logger.info("HTTP response code: " + c.getResponseCode());


        } catch (IOException e) {
            logger.error("Problem with sending image");
        }
    }

    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
                                                       String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
                                                       String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    }).build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
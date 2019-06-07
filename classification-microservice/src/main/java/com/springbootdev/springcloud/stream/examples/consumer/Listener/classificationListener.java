package com.springbootdev.springcloud.stream.examples.consumer.Listener;

import com.springbootdev.springcloud.stream.examples.consumer.Funcionality.PdfMakerInterface;
import com.springbootdev.springcloud.stream.examples.consumer.Model.Task;
import com.springbootdev.springcloud.stream.examples.consumer.Model.classifyDocument;


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

import javax.net.ssl.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


@PropertySource("classpath:classification.properties")
@EnableBinding(Sink.class)
public class classificationListener {

    private static final Logger logger = LoggerFactory.getLogger(classificationListener.class);
    private PdfMakerInterface pdfMaker;

    @Value("${urlForUpload}")
    private String uploadPdfUrl;
    @Value("${urlForCategory}")
    private String uploadCategory;
    @Autowired
    public classificationListener(PdfMakerInterface pdfMaker) {
        this.pdfMaker = pdfMaker;
    }


    @StreamListener(target = Sink.INPUT)
    public void listenForTask(Task task) {

        logger.info(" received new task [" + task.getUuid().toString() + "] ");
        boolean makeClassify = false;
        logger.info(task.getCategory());
        if (task.getCategory().equals(""))
            makeClassify = true;

        classifyDocument document = pdfMaker.makePdf(task.getUrls(), task.getStudentid(), task.getUserid(), makeClassify);
        logger.info("Pdf document has been created ");


        StringBuilder json = new StringBuilder();
        json.append("{\n\t\"id\": \"");
        json.append(task.getUuid().toString());
        json.append("\"\n}");
        logger.info(json.toString());


        OkHttpClient client = getUnsafeOkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, json.toString());
        Request request = new Request.Builder()
                .url(uploadPdfUrl)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("cache-control", "no-cache")
                .build();
        Response response;
        String responsejson;
        try {
            response = client.newCall(request).execute();
            responsejson = response.body().string();
            logger.info(responsejson);
        } catch (IOException e) {
            logger.error("Conncetion to api error");
            return;
        } catch (NullPointerException e) {
            logger.error("Failed create request to api");
            return;
        }
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(responsejson);
        } catch (JSONException err) {
            logger.error("Json error");
            return;
        }
        try {
            URL url = new URL(jsonObject.getString("url"));
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("PUT");
            c.setDoOutput(true);
            c.setConnectTimeout(60 * 1000);
            OutputStream output = c.getOutputStream();
            document.getDocument().writeTo(output);
            output.flush();
            output.close();
            logger.info("HTTP response code: " + c.getResponseCode());
        } catch (IOException e) {
            logger.error(e.getMessage());
            logger.error("Conncetion to api error");
        }


        JSONObject categoryjson = new JSONObject();
        categoryjson.put("student", task.getStudentid());
        categoryjson.put("user", task.getStudentid());
        categoryjson.put("category", document.getCategory());

        logger.info(categoryjson.toString());

        RequestBody categorybody = RequestBody.create(mediaType, json.toString());
        Request categoryRequest = new Request.Builder()
                .url(uploadCategory + jsonObject.getString("id"))
                .put(categorybody)
                .addHeader("Content-Type", "application/json")
                .addHeader("cache-control", "no-cache")
                .build();

        try {
            logger.info(categoryRequest.body().toString());
            response = client.newCall(categoryRequest).execute();
            responsejson = response.body().string();
            logger.info(responsejson);
        } catch (IOException e) {
            logger.error(e.getMessage());
            logger.error("Conncetion to api error");

        } catch (NullPointerException e) {
            logger.error("Failed create request to api");
            return;
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

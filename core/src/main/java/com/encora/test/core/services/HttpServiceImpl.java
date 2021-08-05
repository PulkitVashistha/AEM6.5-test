package com.encora.test.core.services;

import com.encora.test.core.http.ServiceAgent;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

@Component(service = HttpService.class)
public class HttpServiceImpl implements  HttpService {

    @Reference
    ServiceAgent serviceAgent;

    /** Default log. */
    private final Logger logger = LoggerFactory.getLogger(HttpServiceImpl.class);

    @Override
    public String callServiceApi(String request, String url) throws IOException {
//        HttpGet postRequest = null;
//        //get the service URL
//        postRequest = new HttpGet(url);
//        //Pass the JSON in the String Entity
//        HttpResponse httpResponse =  serviceAgent.callServiceAPI(postRequest);
//        StringBuilder response = null;
//        // Get the Input Stream and Read the Stream
//        if (null != httpResponse.getEntity()) {
//            try (BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()))) {
//                String inputLine = null;
//                response = new StringBuilder();
//
//                while ((inputLine = reader.readLine()) != null) {
//                    response.append(inputLine);
//                }
//                // Exception handling
//            } catch (Exception exc) {
//                exc.printStackTrace();
//            }
//        } else {
//
//        }
        HttpClient httpClient = HttpClientBuilder.create()
                .build();;
        HttpGet getRequest = new HttpGet("https://fakestoreapi.com/products");
        getRequest.addHeader("accept", "application/json");
        HttpResponse response = httpClient.execute(getRequest);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatusLine().getStatusCode());
        }
        BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
        String score;
        String xmldata="" ;
        while ((score= br.readLine()) != null) {
            xmldata= xmldata + score;
        }
        logger.debug("json  = {}",xmldata);
        return xmldata;
    }
}

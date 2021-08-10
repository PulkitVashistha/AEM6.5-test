/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.encora.test.core.models;

import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.encora.test.core.http.ServiceAgent;
import com.encora.test.core.pojo.Product;
import com.encora.test.core.services.HttpService;
import com.google.gson.Gson;
import jdk.nashorn.internal.parser.JSONParser;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.*;
import org.apache.sling.settings.SlingSettingsService;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

@Model(adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HelloWorldModel {

    private final Logger logger = LoggerFactory.getLogger(HelloWorldModel.class);

    @ValueMapValue(name=PROPERTY_RESOURCE_TYPE, injectionStrategy=InjectionStrategy.OPTIONAL)
    @Default(values="No resourceType")
    protected String resourceType;

    @Inject
    private HttpService httpService;

    @ValueMapValue
    private String category;

    @ValueMapValue
    private String count;

    private Product[] productlist;

    @PostConstruct
    protected void init() {
        try {
            logger.debug("category = {} ............ count = {}",category,count);
            if(category!=null){
                String url = "https://fakestoreapi.com";
                if(count!=null){
                    url = url+ "/" + category + "?limit="+count;
                }
                logger.debug("url = {}",url);
                String response = httpService.callServiceApi("", url);
                productlist = new Gson().fromJson(response, Product[].class);
                logger.debug("product list = {}",productlist.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Product[] getProductlist() {
        return productlist;
    }
}

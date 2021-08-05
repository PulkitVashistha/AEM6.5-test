package com.encora.test.core.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.sling.caconfig.annotation.Property;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.security.KeyStore;
import java.util.Dictionary;

@Component(service = ServiceAgent.class)
public class ServiceAgent {

	/** Default log. */
	private final Logger logger = LoggerFactory.getLogger(ServiceAgent.class);
	private Dictionary configProps;
	private int SERVICE_TIMEOUT;

//	@ObjectClassDefinition(name = "Service Agent", description = "Service Agent for httpClient")
//	public static @interface Config {
//
//		@AttributeDefinition(name = "serviceTimeout")
//		String scheduler_expression() default "*/30 * * * * ?";
//
//		@AttributeDefinition(name = "keystoreCertPath", description = "Whether or not to schedule this task concurrently")
//		boolean scheduler_concurrent() default false;
//
//		@AttributeDefinition(name = "Cleanup Path", description = "Can be configured in /system/console/configMgr")
//		String cleanup_path() default "/var/myPath";
//
//		@AttributeDefinition(name = "A parameter", description = "Can be configured in /system/console/configMgr")
//		String myParameter() default "";
//	}

	private static final String LOG_METHOD_EXIT_SERVICE_AGENT = "ServiceAgent : - callServiceAPI : Exit :-";
	private static final String LOG_METHOD_HTTP_RESPONSE = "ServiceAgent : - exeServiceReq : The HTTPResponse as received from API is: {}";
	
	/**
	 * This method is called to execute the API and invokes HtpClient
	 * @param httpPostRequest
	 * @return
	 */

	public HttpResponse callServiceAPI(HttpGet httpPostRequest) {
		logger.debug("ServiceAgent : - callServiceAPI : Entry :-");
		HttpResponse httpResponse = null;
		logger.debug("ServiceAgent : - callServiceAPI : received httpPostRequest as: {}  ", httpPostRequest);
		if (null != httpPostRequest) {

			logger.debug("ServiceAgent : - callServiceAPI : timeout has now been set to:  {}  ", SERVICE_TIMEOUT);
			httpResponse = exeServiceReq(httpPostRequest, SERVICE_TIMEOUT);
			logger.debug("ServiceAgent : - callServiceAPI : response  {}  ",
					httpResponse.getStatusLine().getStatusCode());
		}
		logger.debug(LOG_METHOD_EXIT_SERVICE_AGENT);
		return httpResponse;

	}

	/**
	 * This method is used to parse the Response retrieved from the
	 * Service API invocation
	 * @param httpResponse
	 * @return String
	 */
	public String parseHttpResponse(final HttpResponse httpResponse)  {
		logger.debug("ServiceAgent :: parseHttpResponse :: Entry");
		// String Builder instance
		StringBuilder response = null;
		// Get the Input Stream and Read the Stream
		if (null != httpResponse.getEntity()) {
			// Try with Resources block for auto handling of Resource Closure
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(httpResponse.getEntity().getContent()))) {
				String inputLine = null;
				response = new StringBuilder();
				while ((inputLine = reader.readLine()) != null) {
					response.append(inputLine);
				}
				// Log the IO Exception and throw it in the form of AEM Exception
			} catch (IOException ioExc) {
				logger.error("ServiceAgent :: parseHttpResponse :: IOException :- {}", ioExc);
			}
		} else {
			// If Input Stream received is null
		}
		logger.debug("ServiceAgent : - parseHttpResponse : final response :- {}", response);
		logger.debug("ServiceAgent :: parseHttpResponse :: Exit");
		return response.toString();
	}
	
	/**
     * This method just calls the overloaded callServiceAPI with support for cookie management. 
     * It is a placeholder to support older method calls before the provision of cookie management.
     */
	public HttpResponse callServiceAPI(HttpGet httpPostRequest, int timeout)  {
		return callServiceAPI(httpPostRequest, timeout, false);
	}
	
	/**
	 * This method is called to execute the API and invokes HtpClient
	 * @param httpPostRequest
	 * @param timeout
	 * @return HttpResponse
	 */
	public HttpResponse callServiceAPI(HttpGet httpPostRequest, int timeout, boolean disableCookieManagement) {

		HttpResponse httpResponse = null;
		if (null != httpPostRequest && timeout != 0) {
			logger.debug("ServiceAgent : - callServiceAPI : timeout has now been set to:  {}  ", timeout);
			httpResponse = disableCookieManagement ? exeServiceReq(httpPostRequest, timeout, true) : exeServiceReq(httpPostRequest, timeout);
			logger.debug("ServiceAgent : - callServiceAPI : response with two Inputs {}  ",
					httpResponse.getStatusLine().getStatusCode());
			logger.debug("ServiceAgent : - callServiceAPI : response  {}  ", httpResponse);
		}

		logger.debug("ServiceAgent : - callServiceAPI with two Inputs : Exit :-");
		return httpResponse;
	}
	
	/**
     * This method just calls the overloaded exeServiceReq with support for cookie management. 
     * It is a placeholder to support older method calls before the provision of cookie management.
     */
	public HttpResponse exeServiceReq(HttpGet httpPostRequest, int timeout){
		return exeServiceReq(httpPostRequest, timeout, false);
	}

	/**
	 * This method is the execution point for API calls 
	 * @param httpPostRequest
	 * @param timeout
	 * @return HttpResponse
	 */
	public HttpResponse exeServiceReq(HttpGet httpPostRequest, int timeout, boolean disableCookieManagement) {
		logger.debug("ServiceAgent : - exeServiceReq : Entry :-");
		HttpResponse httpResponse = null;
		
		//Setting the Connection and Socket Time Out Options 
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(timeout).setConnectionRequestTimeout(timeout).setSocketTimeout(timeout).build();
		
		//Create the HttpClient Object and pass the Configuration object and Automatically close the Resources
		try {
			HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
			if(disableCookieManagement) {
				httpClientBuilder = httpClientBuilder.disableCookieManagement();
			}
			CloseableHttpClient httpClient = httpClientBuilder.setDefaultRequestConfig(requestConfig).build();
			logger.debug("ServiceAgent : - exeServiceReq : httpPostRequest received as: {}",
					httpPostRequest);
			logger.debug("ServiceAgent : - exeServiceReq : timeout received as: {}", timeout);
			logger.debug("ServiceAgent : - Inside the if condition for testing null Request");
			
			// Execution Line for API calls
			long apiStartTime = System.currentTimeMillis();
			httpResponse = httpClient.execute(httpPostRequest);
			long apiEndTime = System.currentTimeMillis();
			logger.debug("======================= MLI API EXECUTION TIME | {} |  {} =========================", httpPostRequest.getURI(), apiEndTime - apiStartTime);

			logger.debug(LOG_METHOD_HTTP_RESPONSE,
					httpResponse.getStatusLine().getStatusCode());
			logger.debug(LOG_METHOD_HTTP_RESPONSE,
					httpResponse);

			logger.debug("After hitting the Rest API :- ");

			logger.debug(LOG_METHOD_EXIT_SERVICE_AGENT);
			return httpResponse;
		} catch (SocketTimeoutException socketExc) {
			logger.error("ServiceAgent :- exeServiceReq :- SocketTimeoutException :- {}", socketExc);
		} catch (SSLHandshakeException sslHndShkExc) {
			logger.error("ServiceAgent :- exeServiceReq :- SSLHandshakeException :- {}", sslHndShkExc);
		} catch (IOException ioExc) {
			logger.error("ServiceAgent :- exeServiceReq :- IOException :- {}", ioExc);
		}
		return httpResponse;
	}

	protected void activate(ComponentContext context) {
		this.configProps = context.getProperties();
		SERVICE_TIMEOUT = Integer.parseInt("60000");
	}

	protected void deactivate() {
		this.configProps = null;
	}
	
	public HttpResponse callServiceAPI(HttpPost httpPostRequest, int timeout, String productType) {
		
		logger.debug("ServiceAgent : - exeServiceReq : Entry :-");
		HttpResponse httpResponse = null;
		
		//Setting the Connection and Socket Time Out Options 
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(timeout).setConnectionRequestTimeout(timeout).setSocketTimeout(timeout).build();
		
		//Create the HttpClient Object and pass the Configuration object and Automatically close the Resources
		try {
			
			 SSLConnectionSocketFactory sslsf =  configureSslSocketConnectionFactory();

	        //Building the CloseableHttpClient
			CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).setSSLSocketFactory(sslsf).build();
			//		httpClient.setDefaultSSLSocketFactory(sslctx2.getSocketFactory());
			logger.debug("ServiceAgent : - exeServiceReq : httpPostRequest received as: {}",
					httpPostRequest);
			logger.debug("ServiceAgent : - exeServiceReq : timeout received as: {}", timeout);
			logger.debug("ServiceAgent : - Inside the if condition for testing null Request");
			
			// Execution Line for API calls
			long apiStartTime = System.currentTimeMillis();			

			httpResponse = httpClient.execute(httpPostRequest);
			long apiEndTime = System.currentTimeMillis();
			logger.debug("======================= MLI API EXECUTION TIME | {} |  {} =========================", httpPostRequest.getURI(), apiEndTime - apiStartTime);

			logger.debug(LOG_METHOD_HTTP_RESPONSE, httpResponse.getStatusLine().getStatusCode());
			logger.debug(LOG_METHOD_HTTP_RESPONSE, httpResponse);
	        
			logger.debug("After hitting the Rest API :- ");
			logger.debug(LOG_METHOD_EXIT_SERVICE_AGENT);
		
				        
		} catch (SocketTimeoutException socketExc) {
			logger.error("ServiceAgent :- exeServiceReq :- SocketTimeoutException :- {}", socketExc);
		} catch (SSLHandshakeException sslHndShkExc) {
			logger.error("ServiceAgent :- exeServiceReq :- SSLHandshakeException :- {}", sslHndShkExc);
		} catch (IOException ioExc) {
			logger.error("ServiceAgent :- exeServiceReq :- IOException :- {}", ioExc);
		} catch (Exception e) {
			logger.error("ServiceAgent :- exeServiceReq :- Exception :- {}", e);
		}
		return httpResponse;
	}

	public SSLConnectionSocketFactory configureSslSocketConnectionFactory() {

		SSLConnectionSocketFactory sslsf = null;

		try {
			//Keystore Certificate Path
			String KEYSTORE = (String) configProps.get("");

			final String KEYSTOREPASS = (String) configProps.get("adminadmin");

			try(FileInputStream keyStoreInpStream = new FileInputStream(KEYSTORE);){

				KeyStore ks = KeyStore.getInstance("pkcs12");
				ks.load(keyStoreInpStream, KEYSTOREPASS.toCharArray());
				KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
				kmf.init(ks, KEYSTOREPASS.toCharArray());

				SSLContext sslctx2 = SSLContext.getInstance("SSLv3");
				sslctx2.init(kmf.getKeyManagers(), null, null);

				sslsf = new SSLConnectionSocketFactory(sslctx2, new String[] { "TLSv1.2" }, null,
						SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			}

		}catch(Exception e) {
			logger.debug("Exception in configureSslSocketConnectionFactory :: {}" , e);

		}
		return sslsf;
	}
}
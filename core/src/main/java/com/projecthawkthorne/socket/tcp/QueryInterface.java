package com.projecthawkthorne.socket.tcp;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.projecthawkthorne.client.HawkthorneGame;
import com.projecthawkthorne.gamestate.Level;

//mockup class for all queries
public class QueryInterface {
	
	private String url = "http://54.221.47.242/hawk/";
	private Json json;
	private HawkthorneGame game;
	
	public QueryInterface(HawkthorneGame game){
		this.game = game;
		this.json = new Json();
		this.json.setOutputType(OutputType.json);
	}

	public void registerServer(String ipAddress, int port, final Results results) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("ip_address", ipAddress);
		parameters.put("port", String.valueOf(port));

		HttpRequest httpRequest = new HttpRequest(Net.HttpMethods.POST);

		httpRequest.setUrl(url+"/registerServer.php");
		httpRequest.setContent(HttpParametersUtils.convertHttpParameters(parameters));
		httpRequest.setHeader("Content-type", "application/x-www-form-urlencoded");

		results.setStatus(Status.LOADING);
		Gdx.net.sendHttpRequest(httpRequest, new HttpResponseListener(){
			@Override
			public void handleHttpResponse(HttpResponse httpResponse) {
				try {
					results.setStatus(Status.SUCCESS);
				}catch(Exception e){
					results.setStatus(Status.EXCEPTION);
					e.printStackTrace();
				}
			}

			@Override
			public void failed(Throwable t) {
				results.setStatus(Status.REQUEST_FAILED);
			}

			@Override
			public void cancelled() {
				results.setStatus(Status.REQUEST_CANCELLED);
			}
			
		});
	}

	public void registerPlayer(String ipAddress, String username, final Results results) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("ip_address", ipAddress);
		parameters.put("username", String.valueOf(username));

		HttpRequest httpRequest = new HttpRequest(Net.HttpMethods.POST);

		httpRequest.setUrl(url+"/registerPlayer.php");
		httpRequest.setContent(HttpParametersUtils.convertHttpParameters(parameters));
		httpRequest.setHeader("Content-type", "application/x-www-form-urlencoded");

		results.setStatus(Status.LOADING);
		Gdx.net.sendHttpRequest(httpRequest, new HttpResponseListener(){
			@Override
			public void handleHttpResponse(HttpResponse httpResponse) {
				try{
					results.setStatus(Status.SUCCESS);
				}catch(Exception e){
					results.setStatus(Status.EXCEPTION);
					e.printStackTrace();
				}
			}

			@Override
			public void failed(Throwable t) {
				results.setStatus(Status.REQUEST_FAILED);
			}

			@Override
			public void cancelled() {
				results.setStatus(Status.REQUEST_CANCELLED);
			}
			
		});
	}

	public void getServerList(final Results results, final List<List<String>> table) {
		Map<String, String> parameters = new HashMap<String, String>();

		HttpRequest httpRequest = new HttpRequest(Net.HttpMethods.GET);

		httpRequest.setUrl(url+"/listServers.php");
		httpRequest.setContent(HttpParametersUtils.convertHttpParameters(parameters));
		httpRequest.setHeader("Content-type", "application/x-www-form-urlencoded");

		results.setStatus(Status.LOADING);
		Gdx.net.sendHttpRequest(httpRequest, new HttpResponseListener(){
			private int ipWidth = 500;
			private int portWidth = 500;

			@Override
			public void handleHttpResponse(HttpResponse httpResponse) {
				String resp = httpResponse.getResultAsString();
				JsonValue val = new JsonReader().parse(resp);
				table.clear();
				
				for(int i=0; i< val.size; i++){
					List<String> currentRow = new ArrayList<String>();
					JsonValue currentVal = val.get(i);
					String ipAddress = currentVal.get("ip_address").asString();
					String port = currentVal.get("port").asString();
					currentRow.add(ipAddress);
					currentRow.add(port);
					table.add(currentRow);
				}
				results.setStatus(Status.SUCCESS);
			}

			@Override
			public void failed(Throwable t) {
				results.setStatus(Status.REQUEST_FAILED);
			}

			@Override
			public void cancelled() {
				results.setStatus(Status.REQUEST_CANCELLED);
			}
			
		});
	}

}

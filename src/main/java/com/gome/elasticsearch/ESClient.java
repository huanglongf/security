package com.gome.elasticsearch;

import java.net.InetSocketAddress;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * ES client与Spring MVC 集成
 * @author chixiaoyong
 *
 */
public class ESClient {

	private Logger logger = LoggerFactory.getLogger(ESClient.class);

	private TransportClient transportClient;

	public ESClient(String clusterName, String esList) {

		logger.info("init es ");

		init(clusterName, esList);
	}

	public TransportClient getTransportClient() {
		if (transportClient == null) {
			throw new RuntimeException(" transportClient null  please check ES configure");
		}

		return transportClient;
	}

	public void init(String clusterName, String esList) {

		Settings settings = Settings.settingsBuilder().put("cluster.name", clusterName).build();

		// client = TransportClient.builder().settings(settings).build();

		transportClient = TransportClient.builder().settings(settings).build();

		String[] ipArray = esList.split(",");

		if (ipArray != null) {
			for (int i = 0; i < ipArray.length; i++) {

				String[] array = ipArray[i].trim().split(":");

				String ip = array[0].trim();

				int port = 9801;

				try {
					port = Integer.parseInt(array[1].trim());
				} catch (NumberFormatException e) {
					if (logger.isErrorEnabled()) {
						logger.error(" Es port {port} error ", port);
					}
					e.printStackTrace();
				}

				transportClient.addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(ip, port)));

			}

		} else {

		}

		// transportClient.addTransportAddress(new
		// InetSocketTransportAddress(new InetSocketAddress("10.58.72.16",
		// 9901)))
		// .addTransportAddress(new InetSocketTransportAddress(new
		// InetSocketAddress("10.58.72.15", 9901)))
		// .addTransportAddress(new InetSocketTransportAddress(new
		// InetSocketAddress("10.58.72.17", 9901)))
		// .addTransportAddress(new InetSocketTransportAddress(new
		// InetSocketAddress("10.58.72.18", 9901)))
		// .addTransportAddress(new InetSocketTransportAddress(new
		// InetSocketAddress("10.58.72.19", 9901)))
		// .addTransportAddress(new InetSocketTransportAddress(new
		// InetSocketAddress("10.58.72.14", 9901)));

	}

	public void close() {
		if (transportClient != null) {
			transportClient.close();
		}
	}
}

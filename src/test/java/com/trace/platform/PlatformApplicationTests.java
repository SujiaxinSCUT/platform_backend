package com.trace.platform;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.starkbank.ellipticcurve.Ecdsa;
import com.starkbank.ellipticcurve.PrivateKey;
import com.starkbank.ellipticcurve.Signature;
import com.trace.platform.entity.Account;
import com.trace.platform.entity.Order;
import com.trace.platform.entity.OrderedProduct;
import com.trace.platform.entity.Product;
import com.trace.platform.repository.AccountRepository;
import com.trace.platform.repository.OrderedProductRepository;
import com.trace.platform.resource.dto.SelectedBatches;
import com.trace.platform.service.IOrderService;
import com.trace.platform.service.dto.OrderCreateRequest;
import com.trace.platform.service.dto.TraceResult;
import com.trace.platform.service.impl.OrderServiceImpl;
import com.trace.platform.utils.DateUtil;
import org.hyperledger.fabric.sdk.FabricClient;
import org.hyperledger.fabric.sdk.util.Responses;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;

@ContextConfiguration
class PlatformApplicationTests {


	String crtFilePath = "D://Desktop//keyAndCertFile//Mp1//client.crt";

	String keyFilePath = "D://Desktop//keyAndCertFile//Mp1//client.key";

	String pemFilePath = "D://Desktop//keyAndCertFile//Mp1//server.crt";

	String client_id = "Mp1";

	FabricClient client = null;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private OrderedProductRepository orderedProductRepository;

	int proId = 2;

	String keyFile = "";
	String crtFile = "";
	String pemFile = "";

	@Test
	void contextLoads() throws Exception {

	}

	@Test
	void fabricInit() throws Exception {

	}

	@Test
	void addProducts() throws Exception {
		keyFile = new String(Files.readAllBytes(Paths.get(keyFilePath)));
		crtFile = new String(Files.readAllBytes(Paths.get(crtFilePath)));
		pemFile = new String(Files.readAllBytes(Paths.get(pemFilePath)));
		client = new FabricClient(client_id, keyFile, crtFile, pemFile);
		client.init();
		if (client != null) {
			String proName = "a";
			String unit = "kg";
			String batchId = "202104282230000002";
			List<String> list = new ArrayList<>();
			String form = JSON.toJSONString(list);
			System.out.println("form: " + form);
			String date = DateUtil.toNormalizeString(new Date());
			Responses responses = client.addProducts("1", proName, unit, batchId, form, date);
			System.out.println("The message of response is " + responses.getMessages());
		}
		Thread.sleep(2000);
		if (client != null) {
			String proName = "b";
			String unit = "kg";
			String batchId = "202104282230000003";
			List<String> list = new ArrayList<>();
			list.add("1202104282230000002");
			String form = JSON.toJSONString(list);
			System.out.println("form: " + form);
			String date = DateUtil.toNormalizeString(new Date());
			Responses responses = client.addProducts("2", proName, unit, batchId, form, date);
			System.out.println("The message of response is " + responses.getMessages());
		}
//		if (client != null) {
//			String proName = "b";
//			String unit = "kg";
//			String batchId = "202104282230000008";
//			List<String> list = new ArrayList<>();
////			list.add("1202104282230000005");
//			String form = JSON.toJSONString(list);
//			System.out.println("form: " + form);
//			String date = new Date().toString();
//			Responses responses = client.addProducts("2", proName, unit, batchId, form, date);
//			System.out.println("The message of response is " + responses.getMessages());
//		}
	}

	@Test
	void addTransaction() throws Exception {
		keyFile = new String(Files.readAllBytes(Paths.get(keyFilePath)));
		crtFile = new String(Files.readAllBytes(Paths.get(crtFilePath)));
		pemFile = new String(Files.readAllBytes(Paths.get(pemFilePath)));
		client = new FabricClient(client_id, keyFile, crtFile, pemFile);
		client.init();
		if (client != null) {
			String keyFileStr = new String(Files.readAllBytes(Paths.get("D://Desktop//keys//Mm1//privateKey.pem")));
			PrivateKey key = PrivateKey.fromPem(keyFileStr);
			JSONObject proTrade= new JSONObject();
			proTrade.put("send", "Mp1");
			proTrade.put("reci", "Mm1");
			proTrade.put("proId", "2");
			proTrade.put("proName", "b");
			proTrade.put("quantity", "100");
			proTrade.put("proUnit", "kg");
			proTrade.put("date", "2021-03-01 22:13".substring(0, 10));
			Signature signaturePro1 = Ecdsa.sign(JSONObject.toJSONString(proTrade), key);
			String proSign = new String(signaturePro1.toBase64().getBytes());

			JSONObject proTradeObject = new JSONObject();
			proTradeObject.put("proId", "2");
			proTradeObject.put("proName", "b");
			proTradeObject.put("proUnit", "kg");
			proTradeObject.put("bathId", "202104282230000006");
			proTradeObject.put("quantity", "100");
			proTradeObject.put("date", "2021-03-01 22:13");
			proTradeObject.put("sign", proSign);
			String proTradeJson = JSONObject.toJSONString(proTradeObject);

			JSONObject fundsTrade= new JSONObject();
			fundsTrade.put("send", "Mp1");
			fundsTrade.put("reci", "Mm1");
			fundsTrade.put("proId", "2");
			fundsTrade.put("proName", "b");
			fundsTrade.put("unitPrice", "1000");
			fundsTrade.put("totalPrice", "100000");
			fundsTrade.put("date", "2021-03-01 22:13".substring(0, 10));

			Signature signaturePro2 = Ecdsa.sign(JSONObject.toJSONString(fundsTrade), key);
			String fundsSign =new String(signaturePro2.toBase64().getBytes());

			JSONObject fundsTradeObject = new JSONObject();
//			fundsTradeObject.put("send", "Mp1");
//			fundsTradeObject.put("reci", "Mm1");
			fundsTradeObject.put("proId", "2");
			fundsTradeObject.put("proName", "b");
			fundsTradeObject.put("unitPrice", "1000");
			fundsTradeObject.put("totalPrice", "100000");
			fundsTradeObject.put("date", "2021-03-01 22:13");
			fundsTradeObject.put("sign", fundsSign);
			String fundsTradeJson = JSONObject.toJSONString(fundsTradeObject);

			Responses responses = client.sendTrade("Mm1", "00000013", proTradeJson, fundsTradeJson, "20210430193011203");
//			Responses responses1 = client.sendTrade("Mm1", "00000011", proTradeJson, fundsTradeJson, "20210430193011202");
			System.out.println("The message of response is " + responses.getMessages());
		}
//		if (client != null) {
//			String keyFileStr = new String(Files.readAllBytes(Paths.get("D://Desktop//keys//Mm1//privateKey.pem")));
//			PrivateKey key = PrivateKey.fromPem(keyFileStr);
//			JSONObject proTrade= new JSONObject();
//			proTrade.put("send", "Mp1");
//			proTrade.put("reci", "Mm1");
//			proTrade.put("proId", "2");
//			proTrade.put("proName", "b");
//			proTrade.put("quantity", "100");
//			proTrade.put("proUnit", "kg");
//			proTrade.put("date", "2021-03-01 22:13".substring(0, 10));
//			Signature signaturePro1 = Ecdsa.sign(JSONObject.toJSONString(proTrade), key);
//			String proSign = new String(signaturePro1.toBase64().getBytes());
//
//			JSONObject proTradeObject = new JSONObject();
//			proTradeObject.put("proId", "2");
//			proTradeObject.put("proName", "b");
//			proTradeObject.put("proUnit", "kg");
//			proTradeObject.put("bathId", "202104282230000008");
//			proTradeObject.put("quantity", "100");
//			proTradeObject.put("date", "2021-03-01 22:13");
//			proTradeObject.put("sign", proSign);
//			String proTradeJson = JSONObject.toJSONString(proTradeObject);
//
//			JSONObject fundsTrade= new JSONObject();
//			fundsTrade.put("send", "Mp1");
//			fundsTrade.put("reci", "Mm1");
//			fundsTrade.put("proId", "2");
//			fundsTrade.put("proName", "b");
//			fundsTrade.put("unitPrice", "1000");
//			fundsTrade.put("totalPrice", "100000");
//			fundsTrade.put("date", "2021-03-01 22:13".substring(0, 10));
//
//			Signature signaturePro2 = Ecdsa.sign(JSONObject.toJSONString(fundsTrade), key);
//			String fundsSign =new String(signaturePro2.toBase64().getBytes());
//
//			JSONObject fundsTradeObject = new JSONObject();
////			fundsTradeObject.put("send", "Mp1");
////			fundsTradeObject.put("reci", "Mm1");
//			fundsTradeObject.put("proId", "2");
//			fundsTradeObject.put("proName", "b");
//			fundsTradeObject.put("unitPrice", "1000");
//			fundsTradeObject.put("totalPrice", "100000");
//			fundsTradeObject.put("date", "2021-03-01 22:13");
//			fundsTradeObject.put("sign", fundsSign);
//			String fundsTradeJson = JSONObject.toJSONString(fundsTradeObject);
//
//			Responses responses = client.sendTrade("Mm1", "00000014", proTradeJson, fundsTradeJson, "20210430193011203");
////			Responses responses1 = client.sendTrade("Mm1", "00000011", proTradeJson, fundsTradeJson, "20210430193011202");
//			System.out.println("The message of response is " + responses.getMessages());
//		}
	}

	@Test
	void queryOrder() throws Exception {
		keyFile = new String(Files.readAllBytes(Paths.get(keyFilePath)));
		crtFile = new String(Files.readAllBytes(Paths.get(crtFilePath)));
		pemFile = new String(Files.readAllBytes(Paths.get(pemFilePath)));
		client = new FabricClient(client_id, keyFile, crtFile, pemFile);
		client.init();
		if (client != null) {
			Responses responses = client.queryOrderByOrgIdSelf(0);
			System.out.println("The message of response is " + responses.getMessages());
		}
	}

	@Test
	void queryAverPrice() {
		if (client != null) {
			//TODO
		}
	}

	@Test
	void service() throws IOException, ParseException {
		IOrderService iOrderService = new OrderServiceImpl();
		OrderCreateRequest request = new OrderCreateRequest();
		keyFile = new String(Files.readAllBytes(Paths.get(keyFilePath)));
		crtFile = new String(Files.readAllBytes(Paths.get(crtFilePath)));
		pemFile = new String(Files.readAllBytes(Paths.get(pemFilePath)));
		request.setRcvId("Mm1");
		request.setSenderId("Mp1");
		request.setClientCrt(crtFile);
		request.setClientKey(keyFile);
		request.setServerCrt(pemFile);
		Order order = new Order();
		order.setId(11);
		Date now = new Date();
		order.setDate(now);

		request.setOrder(order);
		SelectedBatches batches = new SelectedBatches();
		batches.setPrice(1000);
		Product product = new Product();
		product.setId(2);
		product.setUnit("kg");
		product.setName("b");
		batches.setProduct(product);

		String keyFileStr = new String(Files.readAllBytes(Paths.get("D://Desktop//keys//Mm1//privateKey.pem")));
		PrivateKey key = PrivateKey.fromPem(keyFileStr);
		JSONObject proTrade= new JSONObject();
		proTrade.put("send", "Mp1");
		proTrade.put("reci", "Mm1");
		proTrade.put("proId", "2");
		proTrade.put("proName", "b");
		proTrade.put("quantity", String.valueOf((double) 100));
		proTrade.put("proUnit", "kg");
		proTrade.put("date", DateUtil.toNormalizeString(now).substring(0,10));
		Signature signaturePro1 = Ecdsa.sign(JSONObject.toJSONString(proTrade), key);
		String proSign = new String(signaturePro1.toBase64().getBytes());

		JSONObject fundsTrade= new JSONObject();
		fundsTrade.put("send", "Mp1");
		fundsTrade.put("reci", "Mm1");
		fundsTrade.put("proId", "2");
		fundsTrade.put("proName", "b");
		fundsTrade.put("unitPrice", String.valueOf((double) 1000));
		fundsTrade.put("totalPrice", String.valueOf((double) 1000 * (double) 100));
		fundsTrade.put("date", DateUtil.toNormalizeString(now).substring(0,10));

		Signature signaturePro2 = Ecdsa.sign(JSONObject.toJSONString(fundsTrade), key);
		String fundsSign =new String(signaturePro2.toBase64().getBytes());

		batches.setProductSign(proSign);
		batches.setFundSign(fundsSign);

		Map<String, Double> batch = new HashMap<>();
		batch.put("202104282230000006", (double) 100);

		batches.setBatches(batch);

		List<SelectedBatches> batchesList = new ArrayList<>();
		batchesList.add(batches);
		request.setSelectedBatchesList(batchesList);

		iOrderService.createOrder(request);
	}

	@Test
	void getCertificate() {
		OrderedProduct orderedProduct = orderedProductRepository.findByProIdAndOrderId(1, 1);
		System.out.println(orderedProduct.getProductId());
	}


	@Test
	void trace() throws Exception {
		String keyFilePath = "D://Desktop//keyAndCertFile//Spf//client.key";
		String crtFilePath = "D://Desktop//keyAndCertFile//Spf//client.crt";
		String pemFilePath = "D://Desktop//keyAndCertFile//Spf//server.crt";
		String keyFile = new String(Files.readAllBytes(Paths.get(keyFilePath)));
		String crtFile = new String(Files.readAllBytes(Paths.get(crtFilePath)));
		String pemFile = new String(Files.readAllBytes(Paths.get(pemFilePath)));
//		FabricClient client = new FabricClient("Spf", keyFile, crtFile, pemFile);
//		client.init();
//		Responses responses = client.traceProducts("Mp1", "2", "202104282230000006");
//		System.out.println(responses);
		IOrderService iOrderService = new OrderServiceImpl();
		TraceResult result = iOrderService.traceProduct("Mp1", "1", "202104282230000002", "Spf",
				keyFile, crtFile, pemFile);
		System.out.println(result.getNodes().get(0).getBatchId());
	}
}

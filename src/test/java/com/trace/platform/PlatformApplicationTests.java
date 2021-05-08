package com.trace.platform;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.starkbank.ellipticcurve.Ecdsa;
import com.starkbank.ellipticcurve.PrivateKey;
import com.starkbank.ellipticcurve.PublicKey;
import com.starkbank.ellipticcurve.Signature;
import com.trace.platform.entity.Account;
import com.trace.platform.entity.Order;
import com.trace.platform.entity.Product;
import com.trace.platform.repository.AccountRepository;
import com.trace.platform.resource.dto.SelectedBatches;
import com.trace.platform.service.IOrderService;
import com.trace.platform.service.dto.OrderCreateRequest;
import com.trace.platform.service.impl.OrderServiceImpl;
import com.trace.platform.utils.DateUtil;
import org.hyperledger.fabric.sdk.FabricClient;
import org.hyperledger.fabric.sdk.util.Responses;
import org.junit.jupiter.api.Test;
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
			String batchId = "202104282230000005";
			List<String> list = new ArrayList<>();
			String form = JSON.toJSONString(list);
			System.out.println("form: " + form);
			String date = new Date().toString();
			Responses responses = client.addProducts("1", proName, unit, batchId, form, date);
			System.out.println("The message of response is " + responses.getMessages());
		}
		if (client != null) {
			String proName = "b";
			String unit = "kg";
			String batchId = "202104282230000006";
			List<String> list = new ArrayList<>();
			list.add("1202104282230000005");
			String form = JSON.toJSONString(list);
			System.out.println("form: " + form);
			String date = new Date().toString();
			Responses responses = client.addProducts("2", proName, unit, batchId, form, date);
			System.out.println("The message of response is " + responses.getMessages());
		}
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
			proTrade.put("date", "2021-03-02 22:18".substring(0, 10));
			Signature signaturePro1 = Ecdsa.sign(JSONObject.toJSONString(proTrade), key);
			String proSign = new String(signaturePro1.toBase64().getBytes());

			JSONObject proTradeObject = new JSONObject();
			proTradeObject.put("proId", "2");
			proTradeObject.put("proName", "b");
			proTradeObject.put("proUnit", "kg");
			proTradeObject.put("bathId", "202104282230000006");
			proTradeObject.put("quantity", "100");
			proTradeObject.put("date", "2021-03-02 22:18");
			proTradeObject.put("sign", proSign);
			String proTradeJson = JSONObject.toJSONString(proTradeObject);

			JSONObject fundsTrade= new JSONObject();
			fundsTrade.put("send", "Mp1");
			fundsTrade.put("reci", "Mm1");
			fundsTrade.put("proId", "2");
			fundsTrade.put("proName", "b");
			fundsTrade.put("unitPrice", "1000");
			fundsTrade.put("totalPrice", "100000");
			fundsTrade.put("date", "2021-03-02 22:18".substring(0, 10));

			Signature signaturePro2 = Ecdsa.sign(JSONObject.toJSONString(fundsTrade), key);
			String fundsSign =new String(signaturePro2.toBase64().getBytes());

			JSONObject fundsTradeObject = new JSONObject();
//			fundsTradeObject.put("send", "Mp1");
//			fundsTradeObject.put("reci", "Mm1");
			fundsTradeObject.put("proId", "2");
			fundsTradeObject.put("proName", "b");
			fundsTradeObject.put("unitPrice", "1000");
			fundsTradeObject.put("totalPrice", "100000");
			fundsTradeObject.put("date", "2021-03-02 22:18");
			fundsTradeObject.put("sign", fundsSign);
			String fundsTradeJson = JSONObject.toJSONString(fundsTradeObject);

			Responses responses = client.sendTrade("Mm1", "0000016", proTradeJson, fundsTradeJson, "2021043019301121");
			System.out.println("The message of response is " + responses.getMessages());
		}
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
	void hmac() throws IOException {
		JSONObject proTrade= new JSONObject();
		proTrade.put("send", "Mp1");
		proTrade.put("reci", "Mm1");
		proTrade.put("proId", "001");
		proTrade.put("proName", "b");
		proTrade.put("quantity", "100");
		proTrade.put("proUnit", "kg");
		proTrade.put("date", "2021-03-07 20:18".substring(0, 10));
		String keyFileStr = new String(Files.readAllBytes(Paths.get("D://Desktop//keys//Mr1//privateKey.pem")));
		String publicKeyFileStr = new String(Files.readAllBytes(Paths.get("D://Desktop//keys//Mr1//publicKey.pem")));
		PrivateKey key = PrivateKey.fromPem(keyFileStr);
		PublicKey publicKey = PublicKey.fromPem(publicKeyFileStr);
		Signature signaturePro1 = Ecdsa.sign("test", key);
		String proSign = new String(signaturePro1.toBase64().getBytes());


		String originStr = new String(Base64.getDecoder().decode(proSign));
//		System.out.println(Ecdsa.verify(JSONObject.toJSONString(proTrade), signaturePro1, publicKey));
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
		order.setId(1);
		order.setDate(DateUtil.strToDate("2021-03-10 20:18"));

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
		proTrade.put("quantity", String.valueOf(100.0));
		proTrade.put("proUnit", "kg");
		proTrade.put("date", "2021-03-10 20:18".substring(0,10));
		Signature signaturePro1 = Ecdsa.sign(JSONObject.toJSONString(proTrade), key);
		String proSign = new String(signaturePro1.toBase64().getBytes());

		JSONObject fundsTrade= new JSONObject();
		fundsTrade.put("send", "Mp1");
		fundsTrade.put("reci", "Mm1");
		fundsTrade.put("proId", "2");
		fundsTrade.put("proName", "b");
		fundsTrade.put("unitPrice", "1000");
		fundsTrade.put("totalPrice", "100000");
		fundsTrade.put("date", "2021-03-10 20:18".substring(0,10));

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
		Account account = accountRepository.findByName("Mp1");
		System.out.println(account.getCertificate());
	}
}

package com.omipay.demo.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.omipay.demo.ultils.Signature;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class OmipayController {

    @Value("${api.url}")
    private String url;

/*    @Value("${omipay.admin.api.url}")
    private String adminUrl;*/

    @Autowired
    private Signature signature;

    @Autowired
    public RestTemplate restTemplate;

    @RequestMapping("/")
    public String viewHomePage(Model model) {
        return "index";
    }

    @PostMapping("cashoutRequest")
    public String cashoutRequest( CashoutRequest cashoutRequest, Model model) throws IOException, GeneralSecurityException, URISyntaxException {
        Long requestId = System.currentTimeMillis()/1000;
        String msg = requestId + "|" + cashoutRequest.getMerchantId() + "|" + cashoutRequest.getAmount() + "|"
                + cashoutRequest.getBankAccountName() + "|" + cashoutRequest.getPasscode();
        String scRespone = signature.signSHA256withRSA(msg);
        cashoutRequest.setSecureChain(scRespone);
        cashoutRequest.setRequestId(Long.toString(requestId));

        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String timeTranfer = dateFormat.format(date);
        cashoutRequest.setTimeTranfer(timeTranfer);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(gson.toJson(cashoutRequest));
        String prettyJsonString = gson.toJson(je);

        model.addAttribute("json", prettyJsonString);
        model.addAttribute("cashoutRequest", cashoutRequest);
        return "confirm";
    }

    @PostMapping("confirm")
    public String cashoutConfirm( CashoutRequest cashoutRequest, Model model) throws IOException, GeneralSecurityException, URISyntaxException, JSONException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity requestEntity = new HttpEntity<>(cashoutRequest, headers);

        //adding the query params to the URL
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url);
        try {
            ResponseEntity<String> result = this.restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.POST, requestEntity, String.class);
            JSONObject respone = new JSONObject(result.getBody());
            String err = respone.getString("error_code");
            if(err.equals("000")) {
                JSONObject data = respone.getJSONObject("data");
                String scMessage = cashoutRequest.getRequestId() + "|"
                        + cashoutRequest.getBankAccountName() + "|" + data.get("omiCashoutId") + "|" + cashoutRequest.getPasscode();
                boolean valid = signature.verifySHA256withRSA(scMessage,data.getString("secureChain"));
                respone.put("verify sign Omipay", valid);
                model.addAttribute("result", respone.toString());
            }
        } catch (Exception e) {
            model.addAttribute("result", e.getMessage());
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(gson.toJson(cashoutRequest));
        String prettyJsonString = gson.toJson(je);

        model.addAttribute("json", prettyJsonString);

        return "result";
    }

//    @RequestMapping(value="/getPassCode", method=GET)
//    @ResponseBody
//    public String getPassCode(@RequestParam(required = true) long  merchantId) throws Exception {
//        JSONObject req = new JSONObject();
//        req.put("id", merchantId);
//        String obj  = callApi("getPasscode", req);
//        JSONObject ob = new JSONObject(obj);
//        JSONObject data = ob.getJSONObject("data");
//        String passcode = data.getString("passcode");
//        return passcode;
//    }

//    public String callApi(String fnc, JSONObject json) throws Exception {
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
//
//        Map<String, String> vars = new HashMap<>();
//        vars.put("ifnc", fnc);
//        vars.put("idata", json.toString());
//
//        String result = restTemplate.getForObject(adminUrl, String.class, vars);
//
//
//        return result;
//    }
}

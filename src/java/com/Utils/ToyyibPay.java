package com.Utils;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import static java.net.URLEncoder.encode;
import java.nio.charset.StandardCharsets;

public class ToyyibPay {
    private static final String API_KEY = "l3nyadtn-7x4c-win3-644d-w65g2xos4ati";
    private static final String CATEGORY_CODE = "iub589lc";
    private static final String RETURN_URL = "http://localhost:8080/ATCMS3/PaymentServlet?action=callback";
    private static final String CALLBACK_URL = "http://localhost:8080/ATCMS3/PaymentServlet?action=callback";
    
   public static String createBill(double amount, String appointmentId, 
                              String clientName, String clientEmail, 
                              String clientPhone) {
        try {
            String url = "https://toyyibpay.com/index.php/api/createBill";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            
            // Add request header
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            
            String urlParameters = "userSecretKey=" + API_KEY +
                                 "&categoryCode=" + CATEGORY_CODE +
                                 "&billName=" + encode("Therapy Appointment " + appointmentId) +
                                 "&billDescription=" + encode("Payment for therapy session") +
                                 "&billPriceSetting=1" +
                                 "&billPayorInfo=1" +
                                 "&billAmount=" + (amount * 100) + // ToyyibPay uses cents
                                 "&billReturnUrl=" + RETURN_URL +
                                 "&billCallbackUrl=" + CALLBACK_URL +
                                 "&billExternalReferenceNo=APPT" + appointmentId +
                                 "&billTo=" + URLEncoder.encode(clientName, "UTF-8") +
                             "&billEmail=" + URLEncoder.encode(clientEmail, "UTF-8") +
                             "&billPhone=" + URLEncoder.encode(clientPhone, "UTF-8");
            
            // Send post request
            con.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(urlParameters.getBytes(StandardCharsets.UTF_8));
            }
            
            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                
                JSONObject jsonResponse = new JSONObject(response.toString());
                if (jsonResponse.has("BillCode")) {
                    return jsonResponse.getString("BillCode");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static String getPaymentUrl(String billCode) {
        return "https://toyyibpay.com/" + billCode;
    }
    
    public static boolean verifyPayment(String billCode) {
        try {
            String url = "https://toyyibpay.com/index.php/api/getBillTransactions";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            
            // Add request header
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            
            String urlParameters = "userSecretKey=" + API_KEY +
                                 "&billCode=" + billCode;
            
            // Send post request
            con.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(urlParameters.getBytes(StandardCharsets.UTF_8));
            }
            
            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                
                // Response is an array, we check the first element
                String jsonResponse = response.toString();
                if (jsonResponse.startsWith("[") && jsonResponse.length() > 2) {
                    JSONObject transaction = new JSONObject(jsonResponse.substring(1, jsonResponse.length() - 1));
                    return transaction.has("billpaymentStatus") && 
                           transaction.getString("billpaymentStatus").equals("1");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
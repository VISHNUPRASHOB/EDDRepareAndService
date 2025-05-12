package Helper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class OTPServiceHelper {

    private int otp;
    private static final String OTP_SERVICE_URL = "https://exspender.azurewebsites.net/send-otp";

    public String sendOtp(String email) {
        try {
            URL url = new URL(OTP_SERVICE_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String formData = "emailId=" + URLEncoder.encode(email, StandardCharsets.UTF_8.toString());

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = formData.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {

                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine);
                    }

                    String jsonResponse = response.toString();
                    int otpStart = jsonResponse.indexOf("\"otp\":\"") + 7;
                    if (otpStart >= 7) {
                        int otpEnd = jsonResponse.indexOf("\"", otpStart);
                        String otpString = jsonResponse.substring(otpStart, otpEnd);

                        // Set the instance variable
                        this.otp = Integer.parseInt(otpString);
                        return otpString;
                    }
                }
            } else {
                System.err.println("OTP Service Error - HTTP Code: " + responseCode);
               
            }
        } catch (Exception e) {
            System.err.println("OTP Sending Failed: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

 
    public int getOtp() {
        return this.otp;
    }
}

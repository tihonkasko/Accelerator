package com.accelerator.services.implementations;

import com.accelerator.services.HydrogenAIService;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class HydrogenAIServiceImpl implements HydrogenAIService {
    @Override
    public String[] sendPOST(String result) throws IOException {
        URL obj = new URL("http://localhost:9102");
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("body", result);

        con.setDoOutput(true);

        OutputStream os = con.getOutputStream();
        os.flush();
        os.close();

        int responseCode = con.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) { //success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString().split(" ");

        } else {
            System.out.println("POST request did not work.");
        }
        return new String[3];
    }
}

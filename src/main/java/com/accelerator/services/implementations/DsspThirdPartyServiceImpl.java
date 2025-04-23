package com.accelerator.services.implementations;

import com.accelerator.services.DsspThirdPartyService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.lang.String.format;
import static org.apache.http.entity.ContentType.create;

@Service("dsspThirdPartyService")
public class DsspThirdPartyServiceImpl implements DsspThirdPartyService {

    private CloseableHttpClient httpClient;
    private String resultId = "";

    private static final String DSSP_SERVER_URL =  "https://www3.cmbi.umcn.nl/xssp/";
    private static final String DSSP_STATUS_URL =  "https://www3.cmbi.umcn.nl/xssp/api/status/pdb_file/dssp/%s/";
    private static final String DSSP_RESULT_URL =  "https://www3.cmbi.umcn.nl/xssp/api/result/pdb_file/dssp/%s/";
    private static final String DSSP_STATUS_URL_ID =  "https://www3.cmbi.umcn.nl/xssp/api/status/pdb_id/dssp/%s/";
    private static final String DSSP_RESULT_URL_ID =  "https://www3.cmbi.umcn.nl/xssp/api/result/pdb_id/dssp/%s/";
    private static final String CONTENT_TYPE_PDB_FILE =  "chemical/x-pdb";
    private static final String PDB_CONTENT_FILE_PATH = "src/main/resources/pdbContext.txt";
    private static final String DSSP_FAILURE_MESSAGE = "Dssp server is not working properly. It is not possible to get a result.";
    private static final String DSSP_NOT_ANSWER_MESSAGE = "Dssp server is working but there is no response.";
    private static final String LOCATION = "Location";
    private static final String SUCCESS_STATUS = "SUCCESS";
    private static final String FAILURE_STATUS = "FAILURE";
    private static final String PENDING_STATUS = "PENDING";
    private static final String STARTED_STATUS = "STARTED";
    private static final String STATUS_JSON_PARAM = "status";
    private static final String RESULT_JSON_PARAM = "result";
    private static final String RESNUM = "RESNUM";
    private static final String CSRF_TOKEN_FIND_VALUE =  "id=\"csrf_token\"";
    private static final Integer CSRF_TOKEN_START_INDEX = 55;
    private static final Integer REDIRECT_STATUS = 302;
    private static final Integer START_DSSP_CONTEXT = 77;
    private static final Integer RESPONSE_WAITING = 10000; // 10 sec
    private static final Integer RESPONSE_WITH_FILE = 30000; // 10 sec

    private static final String OUTPUT_TYPE_KAY = "output_type";
    private static final String OUTPUT_TYPE_VALUE = "dssp";
    private static final String INPUT_TYPE_KAY = "input_type";
    private static final String INPUT_TYPE_VALUE_FILE = "pdb_file";
    private static final String INPUT_TYPE_VALUE_ID = "pdb_id";
    private static final String CSRF_TOKEN_KAY = "csrf_token";
    private static final String FILE_KAY = "file_";

    @Override
    public List<String> getDsspContext(MultipartFile pdbFile, boolean isFileNeeded) throws IOException, InterruptedException {
        httpClient = HttpClients.createDefault();
        String csrfToken = getRequestGetCsrfToken();
        resultId = postRequestGetDsspContextUrl(csrfToken, pdbFile, isFileNeeded);
        System.out.println("resultId: " + resultId);
        if (resultId == null){
            return null;
        }
        String dsspContext = getRequestGetDsspContext(isFileNeeded);
        httpClient.close();
        return convertToList(dsspContext);
    }

    private String getRequestGetCsrfToken() throws IOException {
        HttpGet request = new HttpGet(DSSP_SERVER_URL);
        HttpResponse response = httpClient.execute(request);
        String responseBody = new BasicResponseHandler().handleResponse(response);
        return getCsrfToken(responseBody);
    }

    private String getCsrfToken(String responseBody) {
        int startCSRFToken = responseBody.indexOf(CSRF_TOKEN_FIND_VALUE) + CSRF_TOKEN_START_INDEX;
        String startCSRFTokenSubstring = responseBody.substring(startCSRFToken);
        int endCSRFToken = startCSRFTokenSubstring.indexOf("\"");
        return startCSRFTokenSubstring.substring(0, endCSRFToken);
    }

    private String postRequestGetDsspContextUrl(String csrfToken, MultipartFile pdbFile, boolean isFileNeeded) throws IOException {
        HttpPost request = new HttpPost(DSSP_SERVER_URL);
        HttpResponse response = httpClient.execute(preparePostRequest(request, csrfToken, pdbFile, isFileNeeded));
        if (REDIRECT_STATUS == response.getStatusLine().getStatusCode()) {
            return getRedirectedResultId(response);
        }
        return null;
    }

    private HttpPost preparePostRequest(HttpPost request, String csrfToken, MultipartFile pdbFile, boolean isFileNeeded)
            throws IOException {
        HttpEntity multipartEntity;
        if(isFileNeeded) {
            multipartEntity = getHttpEntity(csrfToken, pdbFile);
        } else {
            multipartEntity = getHttpEntity(csrfToken, pdbFile.getOriginalFilename().substring(0,4));
        }
        request.setEntity(multipartEntity);
        return request;
    }

    private HttpEntity getHttpEntity(String csrfToken, MultipartFile pdbFile) throws IOException {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody(OUTPUT_TYPE_KAY, OUTPUT_TYPE_VALUE);
        builder.addTextBody(INPUT_TYPE_KAY, INPUT_TYPE_VALUE_FILE);
        builder.addTextBody(CSRF_TOKEN_KAY, csrfToken);
        setPdbFileToBuilder(builder, pdbFile);
        return builder.build();
    }

    private HttpEntity getHttpEntity(String csrfToken, String fileId) throws IOException {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody(OUTPUT_TYPE_KAY, OUTPUT_TYPE_VALUE);
        builder.addTextBody(INPUT_TYPE_KAY, INPUT_TYPE_VALUE_ID);
        builder.addTextBody(INPUT_TYPE_VALUE_ID, fileId);
        builder.addTextBody(CSRF_TOKEN_KAY, csrfToken);
        return builder.build();
    }

    private void setPdbFileToBuilder(MultipartEntityBuilder builder, MultipartFile pdbFile) throws IOException {
        File file = convertMultipartToFile(pdbFile);
        String fileName = pdbFile.getOriginalFilename();
        builder.addBinaryBody(FILE_KAY, file, create(CONTENT_TYPE_PDB_FILE), fileName);
    }

    private File convertMultipartToFile(MultipartFile pdbFile) throws IOException {
        InputStream initialStream = pdbFile.getInputStream();
        byte[] buffer = new byte[initialStream.available()];
        initialStream.read(buffer);

        File file = new File(PDB_CONTENT_FILE_PATH);
        try (OutputStream outStream = new FileOutputStream(file)) {
            outStream.write(buffer);
        }
        return file;
    }

    private String getRedirectedResultId(HttpResponse response) {
        HeaderElement[] headerLocationElements = response.getFirstHeader(LOCATION).getElements();
        String redirectedUrl = Arrays.stream(headerLocationElements).findFirst().get().getName();
        Pattern pattern = Pattern.compile("/[^/]*$");
        Matcher matcher = pattern.matcher(redirectedUrl);
        if (matcher.find())
        {
           return matcher.group(0).substring(1);
        }
        return null;
    }

    private String getRequestGetDsspContext(boolean isFileNeeded) throws IOException, InterruptedException {
        chekDsspResultStatus(isFileNeeded);
        return getDsspResult(isFileNeeded);
    }

    private void chekDsspResultStatus(boolean isFileNeeded) throws IOException, InterruptedException {
        String status = "";
        HttpGet request = new HttpGet(format(isFileNeeded ? DSSP_STATUS_URL : DSSP_STATUS_URL_ID, resultId));
        Date startRequestDate = new Date();
        System.out.printf("Request: " + (isFileNeeded ? DSSP_STATUS_URL : DSSP_STATUS_URL_ID) + "%n", resultId);
        do {
            HttpResponse response = httpClient.execute(request);
            String jsonString = EntityUtils.toString(response.getEntity());
            JSONObject json = new JSONObject(jsonString);
            status = json.getString(STATUS_JSON_PARAM);
            if (FAILURE_STATUS.equals(status)) {
                throw new RuntimeException(DSSP_FAILURE_MESSAGE);
            } else if(PENDING_STATUS.equals(status)) {
                Date currentDate = new Date();
                if (currentDate.getTime() - startRequestDate.getTime() > RESPONSE_WAITING){
                    throw new RuntimeException(DSSP_NOT_ANSWER_MESSAGE);
                }
            } else if(STARTED_STATUS.equals(status)) {
                Thread.sleep(3000);
                Date currentDate = new Date();
                if (currentDate.getTime() - startRequestDate.getTime() > RESPONSE_WITH_FILE){
                    throw new RuntimeException(DSSP_NOT_ANSWER_MESSAGE);
                }
            }
        } while (!SUCCESS_STATUS.equals(status));
        Thread.sleep(500);
    }

    private String getDsspResult(boolean isFileNeeded) throws IOException {
        HttpGet request = new HttpGet(format(isFileNeeded ? DSSP_RESULT_URL : DSSP_RESULT_URL_ID, resultId));
        System.out.println("New request: " + request);
        HttpResponse response = httpClient.execute(request);
        String jsonString = EntityUtils.toString(response.getEntity());
        JSONObject json = new JSONObject(jsonString);
        String fullResult = json.getString(RESULT_JSON_PARAM);
        System.out.println("Result updated");
        return getResultWithoutHeader(fullResult);
    }

    private String getResultWithoutHeader(String fullResult) {
        int resNumIndex = fullResult.indexOf(RESNUM);
        return fullResult.substring(resNumIndex + START_DSSP_CONTEXT);
    }

    private List<String> convertToList(String dsspContext) {
        String[] arrayDsspContent;
        arrayDsspContent = dsspContext.split("\\n");
        return Arrays.asList(arrayDsspContent);
    }
}

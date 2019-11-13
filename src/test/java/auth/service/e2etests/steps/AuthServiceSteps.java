package auth.service.e2etests.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthServiceSteps {
    private String address;
    private String field;
    private String value;
    private String APIVersion;
    private String method;


    @Given("I use REST-service on address {string} with version {string}")
    public void i_use_REST_service_on_address_with_version(String address, String version) {
        this.address = address;
        this.APIVersion = version;
    }

    @Given("I want to {string}")
    public void i_want_to(String method) {
        this.method = method;
    }

    @When("I sending {string} with value {string}")
    public void i_sending_with_value(String field, String value) {
        this.field = field;
        this.value = value;
    }

    private String createJSONBody(String field, String value) {
        return new JSONObject().put(field, value).toString();
    }

    @Then("I receive in JSON body")
    public void i_receive_in_JSON_body() {
        String answer = sendJSON(createJSONBody(field, value).getBytes(StandardCharsets.UTF_8));

        assertEquals("{\"available\":false}", answer);
    }

    private String sendJSON(byte[] body) {

        try {
            URL url = new URL(createURL());
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("POST"); // PUT is another valid option
            http.setDoOutput(true);

            http.setFixedLengthStreamingMode(body.length);
            http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            http.connect();
            OutputStream os = http.getOutputStream();

            System.out.println("Body: "+  new String(body));
            os.write(body);
            os.flush();
            os.close();

            InputStream is = con.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            System.out.println("Responce: " + response.toString());

            return response.toString().trim();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String createURL() {
        String url = "http://" + address + "/v" + APIVersion + "/auth/" + method;
        System.out.println("URL: " + url);
        return url;
    }
}

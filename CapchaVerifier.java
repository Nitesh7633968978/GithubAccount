import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;

public class CaptchaVerifier {

    private static final String SECRET_KEY = "YOUR_SECRET_KEY";
    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    public static boolean verifyCaptcha(String captchaResponse) {
        if (captchaResponse == null || captchaResponse.isEmpty()) {
            return false;
        }
        try {
            URL url = new URL(VERIFY_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            String params = "secret=" + SECRET_KEY + "&response=" + captchaResponse;

            OutputStream os = conn.getOutputStream();
            os.write(params.getBytes());
            os.flush();
            os.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject json = new JSONObject(response.toString());
            return json.getBoolean("success");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
import javax.servlet.http.HttpServletRequest;
public class RegistrationController {
    public String registerUser(HttpServletRequest request) {
        String captchaResponse = request.getParameter("g-recaptcha-response");
        boolean isHuman = CaptchaVerifier.verifyCaptcha(captchaResponse);

        if (isHuman) {
            // Proceed with user registration (save to database, etc.)
            return "Registration successful!";
        } else {
            return "CAPTCHA verification failed. Please try again.";
        }
    }
}

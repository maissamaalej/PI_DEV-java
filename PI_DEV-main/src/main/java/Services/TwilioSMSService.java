package Services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

public class TwilioSMSService {
    // Renseigne tes identifiants Twilio ici
    private static final String ACCOUNT_SID = "AC278a493c2e58f96e50578d16eecb86c1";
    private static final String AUTH_TOKEN = "d663308b837ff6dd11114ec5225a7938";
    private static final String TWILIO_PHONE_NUMBER = "+13169999631"; // Numéro Twilio

    public static void sendSms(String to, String messageBody) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        Message message = Message.creator(
                new com.twilio.type.PhoneNumber(to),  // Numéro du destinataire
                new com.twilio.type.PhoneNumber(TWILIO_PHONE_NUMBER),  // Numéro Twilio
                messageBody  // Contenu du SMS
        ).create();

        System.out.println("SMS envoyé avec l'ID : " + message.getSid());
    }
}

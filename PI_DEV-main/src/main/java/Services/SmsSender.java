package Services;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SmsSender {

    // Remplace ces valeurs par les informations de ton compte Twilio
    public static final String ACCOUNT_SID = "AC0416a1a2959f49cd69bf5e103d9d22b2";
    public static final String AUTH_TOKEN = "bfd1ccc3d4ab4765a8c979672a17c32e";
    public static final String TWILIO_PHONE_NUMBER = "+17753839312";

    public static void envoyerSms(String numeroDestinataire, String message) {
        try {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

            Message messageEnvoye = Message.creator(
                    new PhoneNumber(numeroDestinataire), // Numéro du destinataire
                    new PhoneNumber(TWILIO_PHONE_NUMBER), // Ton numéro Twilio
                    message // Contenu du message
            ).create();

            System.out.println("Message envoyé à : " + numeroDestinataire);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


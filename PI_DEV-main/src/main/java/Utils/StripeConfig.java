package Utils;

import com.stripe.Stripe;

public class StripeConfig {
    private static final String SECRET_KEY = "sk_test_51QwWQy5NfsiWXvvbzS7EsLjI4Z2CY93sXua9vFXB9WjSAhwimEEQEtXI6Ks3jY6EiOwRAdb7ZrYgPXhpZinTDYz800VyNMFBt4";

    public static void init() {
        Stripe.apiKey = SECRET_KEY;
    }
}
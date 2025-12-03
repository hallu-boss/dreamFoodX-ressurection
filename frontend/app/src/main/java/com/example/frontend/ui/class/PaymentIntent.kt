data class CreatePaymentIntentRequest(
    // Kwota w najmniejszej jednostce (np. groszach dla PLN).
    // Używamy Long, ponieważ kwota może przekroczyć zakres Int.
    val amount: Long,
    // Waluta (opcjonalnie, ale dobrze jest ją wysłać, jeśli backend obsługuje wiele walut)
    val currency: String = "pln"
)

data class PaymentIntentResponse(
    // Klucz klienta, używany przez Stripe Android SDK do finalizacji płatności.
    val clientSecret: String,
    // Klucz publikowalny Stripe. Używany przez SDK do komunikacji z serwerami Stripe.
    val publishableKey: String
)
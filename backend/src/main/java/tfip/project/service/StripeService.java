package tfip.project.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.LineItem;
import com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData;
import com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData.ProductData;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

        @Value("${stripe.secret.key}")
        private String stripeSecretKey;

        @PostConstruct
        public void init() {
                Stripe.apiKey = stripeSecretKey;
        }

        public Session createCheckoutSession(String name, Long price) throws StripeException {

                ProductData product = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                .setName(name)
                                .build();
                PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("sgd")
                                .setUnitAmount(price)
                                .setProductData(product)
                                .build();
                LineItem lineItem = SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(priceData)
                                .build();
                SessionCreateParams params = SessionCreateParams.builder()
                                .setMode(SessionCreateParams.Mode.PAYMENT)
                                .setSuccessUrl("http://localhost:4200/payment/success")
                                .setCancelUrl("http://localhost:4200/payment/cancel")
                                .addLineItem(lineItem)
                                .build();
                return Session.create(params);
        }

        public PaymentIntent createPaymentIntent(long amount) throws Exception {
                PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
                                .setAmount(amount)
                                .setCurrency("sgd")
                                .build();
                return PaymentIntent.create(createParams);
        }
        // post("/create-checkout-session", (request, response) -> {
        // String YOUR_DOMAIN = "http://localhost:4242";
        // SessionCreateParams params =
        // SessionCreateParams.builder()
        // .setMode(SessionCreateParams.Mode.PAYMENT)
        // .setSuccessUrl(YOUR_DOMAIN + "/payment/success")
        // .setCancelUrl(YOUR_DOMAIN + "/payment/cancel")
        // .addLineItem(
        // SessionCreateParams.LineItem.builder()
        // .setQuantity(1L)
        // // Provide the exact Price ID (for example, pr_1234) of the product you want
        // to sell
        // .setPrice("{{PRICE_ID}}")
        // .build())
        // .build();
        // Session session = Session.create(params);

        // response.redirect(session.getUrl(), 303);
        // return "";
        // });

        // public String createCheckoutSession(SubscriptionRequestDTO dto) throws
        // StripeException {
        // SessionCreateParams params = SessionCreateParams.builder()
        // .addLineItem(
        // SessionCreateParams.LineItem.builder()
        // //live id: price_1OyGWhRovknRUrZ7YpWqQpuK
        // .setPrice("price_1OypdkRovknRUrZ7r04LzNL6")
        // .setQuantity(1L)
        // .build())
        // .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
        // .setSuccessUrl(dto.getSuccessUrl())
        // .setCancelUrl(dto.getCancelUrl())
        // .setCustomerEmail(dto.getEmail())
        // .build();

        // Session session = Session.create(params);
        // return session.getId();
        // }

}

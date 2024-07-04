package tfip.project.controller;

import org.springframework.web.bind.annotation.RestController;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.Subscription;
import com.stripe.net.Webhook;

import tfip.project.model.StripePriceId;
import tfip.project.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
public class StripeController {

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @Autowired
    UserService userSvc;

    @SuppressWarnings("deprecation")
    @PostMapping("/stripe/events")
    public String handleStripeEvent(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) throws StripeException {
        if (sigHeader == null)
            return "";

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            System.out.println("Webhook error while validating signature.");
            return "";
        }
        
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            System.out.println(">>> Deserialization failed");
            return "";
        }

        // Handle the event
        switch (event.getType()) {
            case "customer.subscription.updated":
                Subscription sub = (Subscription) event.getData().getObject();
                String custId = sub.getCustomer();
                Customer cust = Customer.retrieve(custId);
                String email = cust.getEmail();
                if (sub.getCancelAt() == null) {
                    String priceId = sub.getItems().getData().get(0).getPlan().getId();
                    userSvc.updateUserMembership(email, StripePriceId.fromPriceId(priceId).getTier());
                    System.out.println(">>> update membership for " + email + "; new membership: " + StripePriceId.fromPriceId(priceId).toString());
                } else {
                    userSvc.updateUserMembership(email, 0);
                    System.out.println(">>> cancelling membership for " + email);
                }
                break;
            default:
                break;
        }
        return "";
    }
}

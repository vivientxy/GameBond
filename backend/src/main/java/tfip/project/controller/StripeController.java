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

import jakarta.json.Json;
import jakarta.json.JsonObject;
import tfip.project.model.StripeCheckoutRequest;
import tfip.project.model.StripePriceId;
import tfip.project.model.UserMembership;
import tfip.project.service.MembershipService;
import tfip.project.service.StripeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/api")
public class StripeController {

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @Autowired
    private MembershipService memSvc;

    @Autowired
    private StripeService stripeSvc;

    @PostMapping("/stripe/create-session")
    public ResponseEntity<String> createSession(@RequestBody StripeCheckoutRequest req) throws Exception {
        String url = stripeSvc.createSession(req.getTier(), req.getEmail());
        JsonObject json = Json.createObjectBuilder()
            .add("checkoutUrl", url)
            .build();
        return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    }

    @PostMapping("/stripe/get-current-membership")
    public ResponseEntity<String> getMembership(@RequestBody String email) throws Exception {
        UserMembership membership = memSvc.getUserMembership(email);
        if (membership == null)
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<String>(membership.toJson().toString(), HttpStatus.OK);
    }

    @PostMapping("/stripe/check-new-member")
    public ResponseEntity<String> checkNewMember(@RequestBody String email) throws Exception {
        JsonObject json = Json.createObjectBuilder()
            .add("isNewMember", stripeSvc.isNewCustomer(email))
            .build();
        return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    }

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
                Subscription sub = (Subscription) stripeObject;
                Long currCycleStart = sub.getCurrentPeriodStart();
                String email = Customer.retrieve(sub.getCustomer()).getEmail();
                if (sub.getCancelAt() == null) {
                    String priceId = sub.getItems().getData().get(0).getPlan().getId();
                    memSvc.updateUserMembership(email, StripePriceId.fromPriceId(priceId).getTier(), currCycleStart);
                    System.out.println(">>> update membership for " + email + "; new membership: " + StripePriceId.fromPriceId(priceId).toString());
                } else {
                    memSvc.updateUserMembership(email, 0, currCycleStart);
                    System.out.println(">>> cancelling membership for " + email);
                }
                break;
            default:
                break;
        }
        return "";
    }
}

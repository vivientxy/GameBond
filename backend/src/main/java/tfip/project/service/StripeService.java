package tfip.project.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerSearchResult;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerSearchParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.LineItem;

import jakarta.annotation.PostConstruct;
import tfip.project.model.StripePriceId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

        @Value("${stripe.secret.key}")
        private String stripeSecretKey;
        @Value("${project.url}")
        private String projectUrl;

        @PostConstruct
        public void init() {
                Stripe.apiKey = stripeSecretKey;
        }

        public String createSession(Integer tier, String email) throws StripeException {
                CustomerSearchParams custParams = CustomerSearchParams.builder()
                        .setQuery("email:'"+ email +"'")
                        .build();
                CustomerSearchResult customers = Customer.search(custParams);
                
                if (customers.getData().size() > 0) {
                        // existing customer -- go customer portal
                        Customer cust = customers.getData().get(0);
                        com.stripe.param.billingportal.SessionCreateParams portalParams = new com.stripe.param.billingportal.SessionCreateParams.Builder()
                                .setReturnUrl(projectUrl)
                                .setCustomer(cust.getId())
                                .build();
                        com.stripe.model.billingportal.Session portalSession = com.stripe.model.billingportal.Session.create(portalParams);
                        return portalSession.getUrl();
                } else {
                        // new customer -- go checkout
                        String priceId = StripePriceId.fromTier(tier).getPriceId();
                        LineItem lineItem = SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPrice(priceId)
                                        .build();
                        SessionCreateParams params = SessionCreateParams.builder()
                                        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                                        .setSuccessUrl(projectUrl + "/#/payment/success/" + tier)
                                        .setCancelUrl(projectUrl + "/#/membership")
                                        .setCustomerEmail(email)
                                        .addLineItem(lineItem)
                                        .build();
                        Session session = Session.create(params);
                        return session.getUrl();
                }
        }

        public boolean isNewCustomer(String email) throws StripeException {
                CustomerSearchParams custParams = CustomerSearchParams.builder()
                        .setQuery("email:'"+ email +"'")
                        .build();
                CustomerSearchResult customers = Customer.search(custParams);
                return customers.getData().size() > 0 ? false : true;
        }

}

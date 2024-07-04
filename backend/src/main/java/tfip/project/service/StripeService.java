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
import tfip.project.repo.RedisRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

        @Value("${stripe.secret.key}")
        private String stripeSecretKey;
        @Value("${project.url}")
        private String projectUrl;

        @Autowired
        RedisRepository redisRepo;

        @PostConstruct
        public void init() {
                Stripe.apiKey = stripeSecretKey;
        }

        public String createCheckoutSession(Integer tier, String email) throws StripeException {
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
                        // switch (tier) {
                        //         case (0):
                        //                 // priceId = "price_1PWzgh00X8yfGho68KJc7CAw"; // live
                        //                 priceId = "price_1PX2vX00X8yfGho6SkWxqXb0"; // test
                        //                 break;
                        //         case (1):
                        //                 // priceId = "price_1PWzhL00X8yfGho630tSf85G"; // live
                        //                 priceId = "price_1PX2wj00X8yfGho62vufqZlR"; // test
                        //                 break;
                        //         case (2):
                        //                 // priceId = "price_1PWzhy00X8yfGho6dqu70GjB"; // live
                        //                 priceId = "price_1PX2xD00X8yfGho6F9dG5kdf"; // test
                        //                 break;
                        //         case (3):
                        //                 // priceId = "price_1PWziU00X8yfGho6qyOyYPS5"; // live
                        //                 priceId = "price_1PX2xk00X8yfGho6TmlaOpLO"; // test
                        //                 break;
                        //         default:
                        //                 priceId = null;
                        //                 break;
                        // }
                        LineItem lineItem = SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPrice(priceId)
                                        .build();
                        SessionCreateParams params = SessionCreateParams.builder()
                                        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                                        .setSuccessUrl(projectUrl + "/#/payment/success")
                                        .setCancelUrl(projectUrl + "/#/membership")
                                        .setCustomerEmail(email)
                                        .addLineItem(lineItem)
                                        .build();
                        Session session = Session.create(params);
                        return session.getUrl();
                }
        }

        public String saveTier(Integer tier, String email) {
                return redisRepo.saveMembershipTier(tier, email);
        }

        public Integer validateTier(String uuid, String email) {
                return redisRepo.validateMembershipTier(uuid, email);
        }

}

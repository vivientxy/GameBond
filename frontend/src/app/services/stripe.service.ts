import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { loadStripe, Stripe } from '@stripe/stripe-js';
import { Observable } from 'rxjs';

@Injectable()
export class StripeService {

  private readonly http = inject(HttpClient)
  private stripe: Stripe | null = null;

  constructor() {
    this.initializeStripe();
  }

  async initializeStripe() {
    this.stripe = await loadStripe('pk_test_51PWcHA00X8yfGho62wlHKrlumXSqVw4DvhCwII9JQvcG6nbswzOeBpaXmuwrkVRLdOGaTbf7ZU2FV8V56oF9aCxR004H5sEzrz');
  }

  createPaymentSession(tier: number, email: string): Observable<any> {
    const body = { tier: tier, email: email }; 
    return this.http.post<any>('api/payment/create-session', body)
      .pipe(result => {return result})
  }

  // redirectToCheckout(sessionId: string): void {
  //   this.stripe?.redirectToCheckout({sessionId: sessionId})
  //     .then(resp => {alert(resp.error.message)})
  // }

  upgradeMembership(uuid: string, email: string): Observable<any> {
    const body = { uuid: uuid, email: email }; 
    return this.http.post<any>('api/payment/upgrade-membership', body)
      .pipe(result => {return result})
  }

}
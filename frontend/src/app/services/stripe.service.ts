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
    return this.http.post<any>('api/stripe/create-session', body)
      .pipe(result => {return result})
  }

  getMembership(email: string): Observable<any> {
    return this.http.post<any>('api/stripe/get-current-membership', email)
      .pipe(result => {return result})
  }

  checkNewMember(email: string): Observable<any> {
    return this.http.post<any>('api/stripe/check-new-member', email)
      .pipe(result => {return result})
  }

}
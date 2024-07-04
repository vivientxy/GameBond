import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { loadStripe, Stripe } from '@stripe/stripe-js';
import { firstValueFrom } from 'rxjs';

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

  createPaymentSession(tier: number, email: string): Promise<any> {
    const body = { tier: tier, email: email }; 
    return firstValueFrom(this.http.post<any>('api/stripe/create-session', body))
  }

  getMembership(email: string): Promise<any> {
    return firstValueFrom(this.http.post<any>('api/stripe/get-current-membership', email))
  }

  checkNewMember(email: string): Promise<any> {
    return firstValueFrom(this.http.post<any>('api/stripe/check-new-member', email))
  }

}
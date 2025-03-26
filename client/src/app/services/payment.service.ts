import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { AuthenticationService } from './authentication.service';
import { catchError, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PaymentService 
{
  constructor() { }

  private http = inject(HttpClient);

  private getAuthHeaders(): HttpHeaders
  {
    const jwtToken = localStorage.getItem("jwtToken")

    // Set headers to authorise
    const headers = new HttpHeaders({
      "Authorization": `Bearer ${jwtToken}`, // Attach token
    });
    return headers;
  }

  // Create checkout session for user to pay and upgrade to PRO
  public createCheckoutSession(username: string, useVisaDiscount: boolean): Observable<any>
  {
    const successUrl = `${window.location.origin}/payment-success`;
    const cancelUrl = `${window.location.origin}/payment-cancel`;

    return this.http.post<any>(`/api/payment/create-checkout-session/${username}?successUrl=${successUrl}&cancelUrl=${cancelUrl}&visaDiscount=${useVisaDiscount}`, {}, { headers: this.getAuthHeaders() } ).pipe(
      catchError((error) => 
        {
          if(error.status === 400) // bad request
            {
              console.log("Error: ", error.error.error)
              throw new Error(error.error.error);
            }
            throw new Error("Unexpected error occurred. Please try again.");
        })
    )
  }



  public upgradeUserToPro(username:string): Observable<any>
  {
    return this.http.post<any>(`/api/payment/upgrade/${username}`, {}, { headers: this.getAuthHeaders() } ).pipe(
      catchError((error) => 
        {
          if(error.status === 400) // bad request
            {
              console.log("Error: ", error.error.error)
              throw new Error(error.error.error);
            }
            throw new Error("Unexpected error occurred. Please try again.");
        })
    )
  }


}

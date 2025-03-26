import { HttpClient } from '@angular/common/http';
import { Component, inject, OnInit } from '@angular/core';
import { loadStripe } from '@stripe/stripe-js';
import { AuthenticationService } from '../../../../services/authentication.service';
import { PaymentService } from '../../../../services/payment.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-checkout',
  standalone: false,
  templateUrl: './checkout.component.html',
  styleUrl: './checkout.component.css'
})
export class CheckoutComponent implements OnInit
{
  private router = inject(Router)
  private authSvc = inject(AuthenticationService);
  private paymentSvc = inject(PaymentService);

  useVisaDiscount: boolean = false;
  originalPrice: string = 'S$9.99';
  discountedPrice: string = 'S$7.49';
  
  errorMessage: string = '';
  username: string = '';
  publishableKey: string = "pk_test_51R3pS5Ka5LfOitr64PqA24e9Xa8lB9RN4QKskTCdtY8osg5pn5W5ybJUTq8Rw2WqQjSIulRCaPhgvvsRiOA5BFAk00zzwgexS9";

  panelOpenState = false; // For the expansion panel
  
  // Benefits list
  benefits: {icon: string, text: string}[] = [
    { icon: 'smart_toy', text: 'Access to AI-powered travel recommendations' },
    { icon: 'flight_takeoff', text: 'Unlimited trips planning' },
    { icon: 'support_agent', text: 'Priority customer support' },
    { icon: 'map', text: 'Advanced itinerary features' },
    { icon: 'discount', text: 'Exclusive travel deals and discounts' }
  ];
  
  ngOnInit(): void 
  {
    const userDetails = this.authSvc.getUserDetails();
    this.username = userDetails.username;

    if(userDetails.isPro)
    {
      alert("You are already a PRO member");
      this.router.navigate(['/Home'])
    }
  }

  toggleVisaDiscount(): void {
    this.useVisaDiscount = !this.useVisaDiscount;
    console.log('Visa discount toggled:', this.useVisaDiscount);
  }

  protected onCheckout(): void
  {
    this.paymentSvc.createCheckoutSession(this.username, this.useVisaDiscount).subscribe({
      next: async (sessionResp) => {
        const stripe = await loadStripe(this.publishableKey);
        console.log("checkoutsession resp: ", sessionResp);

        if(!stripe)
        {
          this.errorMessage = 'Failed to load stripe..'
          console.error('Failed to load stripe..');
        }

        window.location.href = sessionResp.url // point current url to session resp url (Stripe paymenet page)
      }
    })
  }
 
}

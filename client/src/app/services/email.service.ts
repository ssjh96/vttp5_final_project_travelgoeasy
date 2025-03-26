import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { EmailRequest } from '../model/email-request';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class EmailService {

  constructor() { }

  private http = inject(HttpClient)

  private sendEmail(emailRequest: EmailRequest): Observable<any>
  {
    return this.http.post('/api/email/send', emailRequest)
  }

  public sendWelcomeEmail(email: string, username: string): Observable<any> {
    const emailRequest: EmailRequest = {
      to: email,
      subject: "Welcome to TravelGoEasy!",
      message: "Thank you for joining TravelGoEasy! Start planning your next adventure today!!",
      name: username
    };

    return this.sendEmail(emailRequest);
  }

  public sendTripCreationEmail(email: string, username: string, tripName: string, destination: string, startDate: string, endDate: string, tripId: number): Observable<any> {
    const url = window.location.origin;
    const emailRequest: EmailRequest = {
      to: email,
      subject: `Trip to ${destination} succesfully created!`,
      message: `Your trip titled "${tripName}" to ${destination} from ${startDate} to ${endDate} has been succesfully created. Start adding places to your itinerary! 
      
      View the itinerary using link: ${url}/shared-itinerary/${tripId}!`,
      name: username
    };
    return this.sendEmail(emailRequest);
  }

  // Trip invitation for trip mates
  sendTripInvitationEmail(email: string, inviterName: string, tripName: string, destination: string, startDate: string, endDate: string, tripId: number): Observable<any> {
    const url = window.location.origin;
    const emailRequest: EmailRequest = {
      to: email,
      subject: `${inviterName} has invited you to join a trip to ${destination}`,
      message: `${inviterName} has invited you to join their trip "${tripName}" to ${destination} from [${startDate}] to [${endDate}]. 
      
      You can view the itinerary using the link: ${url}/shared-itinerary/${tripId}`,
      name: 'Traveler'
    };
    return this.sendEmail(emailRequest);
  }

  sendPaymentSuccessEmail(email: string, username: string): Observable<any>
  {
    const url = window.location.origin;
    const emailRequest: EmailRequest = {
      to: email,
      subject: `PRO Membership Succesfully Purchased!`,
      message: `Congratulations! You have succesfully upgraded to PRO membership! Explore our PRO features now! Features includes: (1) Access to AI travel recommendations, (2) Unlimited trip planning, (3) Priority customer support, (4) Advanced itinerary features, (5) Exclusive travel deals and discounts!`,
      name: username
    };
    return this.sendEmail(emailRequest);
  }
}

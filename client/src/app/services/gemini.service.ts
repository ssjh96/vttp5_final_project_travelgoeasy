import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { AuthenticationService } from './authentication.service';

@Injectable({
  providedIn: 'root'
})
export class GeminiService {
  private http = inject(HttpClient);
  private authService = inject(AuthenticationService);


  private sendPrompt(prompt: string, destination: string, daysCount: number, tripId: number): Observable<string> 
  {
    const userId = this.authService.getUserDetails().userId;
    const params = new HttpParams()
                  .set('destination', destination)
                  .set('daysCount', daysCount.toString())
                  .set('tripId', tripId.toString())
                  .set('userId', userId.toString());  
    
    
    return this.http.post('/api/gemini/prompt', prompt, 
      { responseType: 'text', params: params })
      .pipe(
        map(response => {
          const parsedResponse = JSON.parse(response);
          console.log("parsedResp: ", parsedResponse)
          return parsedResponse.geminiResp;
        }),
        catchError((error) => {
          if(error.status === 400 || error.status === 500) // bad request or internal server error
          {
            throw new Error("Error sending prompt. Please try again." + error.error.error);
          }
          throw new Error("Unexpected error occurred. Please try again." + error.error.error);
        })
      );
  }



  public generateItinerary(destination: string, days: number, startDate: string, tripId: number): Observable<string> {
    const prompt = `
      Generate a detailed ${days}-day itinerary for ${destination}.
      
      Format your response as a structured JSON with the following format:
      [
        {
          "day": 1,
          "places": [
            {
              "name": "Place Name",
              "description": "Brief description",
              "address": "Full address",
              "lat": latitude,
              "lng": longitude,
              "orderIndex": 0
            },
            ...more places for day 1
          ]
        },
        ...more days
      ]
      
      Start date is ${startDate}.
      Include 3-5 popular attractions or activities per day.
      Make sure all places are real with proper names and addresses.
      Ensure the itinerary is logistically feasible (group places by proximity).
      Include coordinates (latitude and longitude) for each place.
      Return ONLY the JSON with no additional text.
    `;

    return this.sendPrompt(prompt, destination, days, tripId);
  }
}
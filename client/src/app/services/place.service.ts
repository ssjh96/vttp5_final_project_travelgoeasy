import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Place } from '../model/place';
import { catchError, Observable } from 'rxjs';
// import { JwtStore } from '../store/jwt.store';
import { AuthenticationService } from './authentication.service';


@Injectable({
  providedIn: 'root'
})
export class PlaceService 
{
  private http = inject(HttpClient);
  // private jwtStore = inject(JwtStore);
  private authSvc = inject(AuthenticationService)

  constructor() { }

  private getAuthHeaders(): HttpHeaders
  {
    const jwtToken = localStorage.getItem("jwtToken")

    // Set headers to authorise
    const headers = new HttpHeaders({
      "Authorization": `Bearer ${jwtToken}`, // Attach token
    });

    return headers;
  }

  // Create/Update itinerary
  public saveItinerary(tripId: number, places: Place[]): Observable<any>
  { 
    return this.http.put<any>(`/api/places/create/${tripId}`, places, { headers: this.getAuthHeaders() } ).pipe(
      catchError((error) => {
        if(error.status === 400 || error.status === 500) // bad request or internal server error
        {
          throw new Error("Error creating/updating itinerary. Please try again." + error.error.error);
        }
        throw new Error("Unexpected error occurred. Please try again." + error.error.error + error.status + error);
      })
    );
  }

  // Get all trips for given userId
  public getItineraryByTripId(tripId: number): Observable<Place[]>
  {
    return this.http.get<Place[]>(`/api/places/get-all/${tripId}`).pipe(
        catchError((error) => {
          if(error.status === 404) // not found
          {
            throw new Error("user has no itinerary planned yet.");
          }
          throw new Error("Unexpected error occurred. Please try again.");
        })
    );
  }

  
}

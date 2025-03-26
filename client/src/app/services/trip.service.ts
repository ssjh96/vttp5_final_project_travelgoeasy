import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Trip } from '../model/trip';
import { catchError, lastValueFrom, Observable } from 'rxjs';
// import { JwtStore } from '../store/jwt.store';
import { AuthenticationService } from './authentication.service';


@Injectable({
  providedIn: 'root'
})
export class TripService 
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

  // Create trip
  public createTrip(trip: Trip): Observable<number>
  {
    return this.http.post<number>('/api/trips/create', trip, { headers: this.getAuthHeaders() } ).pipe(
        catchError((error) => {
          if(error.status === 400) // bad request
          {
            throw new Error("Error creating trip. Please try again.");
          }
          throw new Error("Unexpected error occurred. Please try again.");
        })
    );
  }

  // Get all trips for given userId
  public getAllTrips(userId: number): Observable<Trip[]>
  {
    return this.http.get<Trip[]>(`/api/trips/all-trips/${userId}`, { headers: this.getAuthHeaders() } ).pipe(
        catchError((error) => {
          if(error.status === 404) // not found
          {
            throw new Error("user has no trips planned yet.");
          }
          throw new Error("Unexpected error occurred. Please try again.");
        })
    );
  }

  // Get single trip by tripId
  public getTripById(tripId: number): Observable<Trip>
  {
    return this.http.get<Trip>(`/api/trips/trip-id/${tripId}`).pipe(
        catchError((error) => {
          if(error.status === 404) // not found
          {
            throw new Error(`user has no trip for ${tripId }planned yet.`);
          }
          throw new Error("Unexpected error occurred. Please try again.");
        })
    );
  }

  // Update trip by tripId
  public updateTrip(tripId: number, updatedTrip: Trip) : Observable<Object>
  {
    return this.http.put<string>(`/api/trips/update/${tripId}`, updatedTrip, { headers: this.getAuthHeaders() } ).pipe(
        catchError((error) => {
          if(error.status === 400) // bad request
          {
            throw new Error("Failed to update. No trip found for given tripId: " + tripId);
          }
          throw new Error("Unexpected error occurred. Please try again.");
        })
    );
  }

  // Delete trip by tripId
  public deleteTrip(tripId: number) : Observable<Object>
  {
    return this.http.delete<string>(`/api/trips/delete/${tripId}`, { headers: this.getAuthHeaders() } ).pipe(
        catchError((error) => {
          if(error.status === 400) // bad request
          {
            throw new Error("Failed to delete trip for given tripId: " + tripId);
          }
          console.error(error.message)
          throw new Error("Unexpected error occurred. Please try again");
        })
    );
  }
}

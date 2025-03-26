import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { catchError, Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProfileService 
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

  // Get profile
  public getProfile (userId: number): Observable<any>
  {
    console.log("getting profile")
    return this.http.get<any>(`/api/profile/get/${userId}`, { headers: this.getAuthHeaders() } ).pipe(
      tap((resp) => console.log(resp)),
      catchError((error) => 
      {
        if(error.status === 400 || error.status === 500) // bad request
          {
            throw new Error("Error getting profile. Please try again.");
          }
          throw new Error("Unexpected error occurred. Please try again.");
      })
    )
  }

   // Update profile with no picture
   public updateProfileNoPic(userId: number, firstName: string, lastName: string): Observable<any>
   {
     const formData = new FormData();
     formData.set('firstName', firstName);
     formData.set('lastName', lastName);
         
     return this.http.put<any>(`/api/profile/update/${userId}`, formData, { headers: this.getAuthHeaders() } ).pipe(
       catchError((error) => 
       {
         if(error.status === 400 || error.status === 500) // bad request
           {
             throw new Error("Error updating profile. Please try again.");
           }
           throw new Error("Unexpected error occurred. Please try again.");
       })
     )
   }

  // Update profile with new picture
  public updateProfile(userId: number, firstName: string, lastName: string, profilePic: File): Observable<any>
  {
    const formData = new FormData();
    formData.set('firstName', firstName);
    formData.set('lastName', lastName);
    formData.set('profilePicFile', profilePic); 
        
    return this.http.put<any>(`/api/profile/update/${userId}`, formData, { headers: this.getAuthHeaders() } ).pipe(
      catchError((error) => 
      {
        if(error.status === 400 || error.status === 500) // bad request
          {
            throw new Error("Error updating profile. Please try again.");
          }
          throw new Error("Unexpected error occurred. Please try again.");
      })
    )
  }

  
  // Delete profile pic
  public removeProfilePic(userId: number): Observable<any>
  {
    return this.http.put<any>(`/api/profile/removePic/${userId}`, {}, { headers: this.getAuthHeaders() } ).pipe(
      catchError((error) => 
      {
        if(error.status === 400 || error.status === 500) // bad request
          {
            throw new Error("Error deleting profile pic. Please try again.");
          }
          throw new Error("Unexpected error occurred. Please try again.");
      })
    )
  }


  // Get user rank
  public getUserRank(userId: number): Observable<any>
  {
    return this.http.get<any>(`/api/rank/${userId}`, { headers: this.getAuthHeaders() } ).pipe(
      catchError((error) => 
      {
        if(error.status === 500)
          {
            throw new Error("Error getting rank. Please try again.");
          }
          throw new Error("Unexpected error occurred. Please try again.");
      })
    ) 
  }


  // delete user by id
  // upgrade pro status
  
}

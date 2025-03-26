import { inject, Injectable } from '@angular/core';
import { JwtResponse } from '../model/jwt-response';
import { JwtRequest } from '../model/jwt-request';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, catchError, firstValueFrom, lastValueFrom, Observable, tap } from 'rxjs';
import { NewUser, UserDetails } from '../model/user';
import { jwtDecode } from 'jwt-decode';
// import { JwtStore } from '../store/jwt.store';
// import { AppDB } from '../shared/app.db';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService
{
  private http = inject(HttpClient);

  constructor() { }
  
  // expose auth status as observable, behaviour subject initial value = false (no token)
  private authSubject = new BehaviorSubject<boolean>(this.isAuthenticated());
  public authObs$: Observable<boolean> = this.authSubject.asObservable(); 
 
  public login(jwtRequest: JwtRequest): Promise<JwtResponse>
  {
    return lastValueFrom(
      this.http.post<JwtResponse>('/api/auth/login', jwtRequest).pipe(
        tap((jwtResponse) => {
          console.log("Auth Svc - jwtResponse: ", jwtResponse);
          console.log("Auth Svc - jwtResponse.jwtToken: ", jwtResponse.jwtToken);
          
          // Save JWT token to localStorage
          localStorage.setItem('jwtToken', jwtResponse.jwtToken);

          // update auth state
          this.authSubject.next(true);
        }),
        catchError((error) => {
          if(error.status === 401) // Unauthorised
          {
            throw new Error("Invalid username or password.");
          }
          throw new Error("Unexpected error occurred. Please try again.");
        }
      )));
  }



  public register(newUser: NewUser): Promise<any>
  {
    return lastValueFrom(
      this.http.post('/api/auth/register', newUser).pipe(
        catchError((error) => {
          if(error.status === 400) // Bad request
          {
            throw new Error("Username or email already exists. Please use a different one.");
          }
          throw new Error("Unexpected error occurred. Please try again.");
        })
      ));
  }



  public logout()
  {
    localStorage.removeItem("jwtToken");
    console.log("jwttoken cleared")
  }



  public isAuthenticated() : boolean
  {
    const token = localStorage.getItem("jwtToken");
    return !!token; // token exist means user authenticated 
  }



  public getUserDetails(): UserDetails
  {
    const token = localStorage.getItem("jwtToken");

    // no token in indexedDB
    if(!token) 
    {
      return {
        userId: 0,
        username: "",
        email: "",
        role: "",
        isPro: false
      } as UserDetails
    }
    
    const decodedJwt: any = jwtDecode(token);
    console.log("AuthSvc - decoded Jwt: ", decodedJwt);

    const userDetails: UserDetails = { 
                                        userId: decodedJwt.userId,
                                        username: decodedJwt.username,
                                        email: decodedJwt.email,
                                        role: decodedJwt.role,
                                        isPro: decodedJwt.isPro
                                      } as UserDetails

    console.log("AuthSvc - userDetails: ", userDetails)

    return userDetails;
  }

}

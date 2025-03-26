import { Component, inject, Input, OnInit } from '@angular/core';
import { AuthenticationService } from '../../../services/authentication.service';
import { Router } from '@angular/router';
import { UserDetails } from '../../../model/user';
import { Observable } from 'rxjs';


@Component({
  selector: 'app-header',
  standalone: false,
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent implements OnInit
{
  private authSvc = inject(AuthenticationService)
  private router = inject(Router)

  authStatus$!: Observable<boolean>

  

  isLoggedIn: boolean = false;
  isPro:boolean = false;
  username: string = "";

  ngOnInit(): void {
    // this.checkAuthStatus();
    this.authStatus$ = this.authSvc.authObs$

    this.authStatus$.subscribe({
      next: (status: boolean) => { 
        this.isLoggedIn = status;
        console.log("Header - isLoggedIn: ", this.isLoggedIn)

        if (this.isLoggedIn) 
        {
          const userDetails = this.authSvc.getUserDetails() as UserDetails;
          console.log("Header - UserDetails: ", userDetails)
          console.log("Header - UserDetails: if default values means localStorage no token")

          this.username = userDetails.username;  
          this.isPro = userDetails.isPro;
        } 
      },
      error: (err) => { console.error(err.message); }
    });
  }

  // protected checkAuthStatus()
  // {
  //   this.isLoggedIn = this.authSvc.isAuthenticated();

  //   if(this.isLoggedIn)
  //   {
  //     const userDetails = this.authSvc.getUserDetails() as UserDetails;
  //     this.username = userDetails.username;  
  //     this.isPro = userDetails.isPro;
  //   }
  // }

  protected onLogout()
  {
    const confirmLogout = confirm('Are you sure you want to logout?');
  
    if (!confirmLogout) {
      return; 
    }
    this.authSvc.logout(); // remove jwt from localstorage
    this.isLoggedIn = false;
    this.router.navigate(['/']);
  }

    
}

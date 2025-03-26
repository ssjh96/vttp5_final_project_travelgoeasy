import { Component, inject, OnInit } from '@angular/core';
import { AuthenticationService } from '../../../services/authentication.service';
import { UserDetails } from '../../../model/user';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: false,
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit
{
  private authSvc = inject(AuthenticationService);
  private router = inject(Router);

  username!: string;
  isPro!: boolean

  ngOnInit(): void 
  {
    const userDetails = this.getUserDetails();
    this.username = userDetails.username;
    this.isPro = userDetails.isPro;
  }

  private getUserDetails()
  {
    return this.authSvc.getUserDetails() as UserDetails;
  }

  onCreateTravelPlan() {
    this.router.navigate(['/create-plan']);
  }

  onViewTravelPlans() {
    this.router.navigate(['/all-plans']);
  }
 
}
